package org.caleydo.core.manager.specialized.genome.pathway;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.logging.Level;
import javax.media.opengl.GLEventListener;
import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;
import org.caleydo.core.util.system.StringConversionTool;
import org.caleydo.core.view.opengl.canvas.pathway.GLCanvasPathway3D;
import org.caleydo.core.view.opengl.canvas.remote.GLCanvasRemoteRendering3D;

/**
 * Loads all pathways of a certain type by providing the XML path.
 * 
 * @author Marc Streit
 */
public class PathwayLoaderThread
	extends Thread
{
	private static final String PATHWAY_LIST_KEGG = "pathway_list_KEGG.txt";
	private static final String PATHWAY_LIST_BIOCARTA = "pathway_list_BIOCARTA.txt";
	
	/**
	 * Needed for updating progress bar
	 */
	private static final int APPROX_PATHWAY_COUNT_KEGG = 214;
	private static final int APPROX_PATHWAY_COUNT_BIOCARTA = 314;
	
	private IGeneralManager generalManager;

	private Collection<PathwayDatabase> pathwayDatabases;

	/**
	 * Constructor.
	 * 
	 * @param sXMLPath
	 */
	public PathwayLoaderThread(
			final Collection<PathwayDatabase> pathwayDatabases)
	{
		super("Pathway Loader Thread");

		this.generalManager = GeneralManager.get();
		this.pathwayDatabases = pathwayDatabases;

		generalManager.getLogger().log(Level.INFO, "Start pathway databases loader thread");

		start();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run()
	{
		super.run();

		Iterator<PathwayDatabase> iterPathwayDatabase = pathwayDatabases.iterator();

		while (iterPathwayDatabase.hasNext())
		{
			loadAllPathwaysByType(iterPathwayDatabase.next());
		}

		notifyViews();
	}

	private void loadAllPathwaysByType(final PathwayDatabase pathwayDatabase)
	{
		// // Try reading list of files directly from local hard dist
		// File folder = new File(sXMLPath);
		// File[] arFiles = folder.listFiles();

		GLCanvasRemoteRendering3D tmpGLRemoteRendering3D = null;
		for (GLEventListener tmpGLEventListener : generalManager.getViewGLCanvasManager()
				.getAllGLEventListeners())
		{
			if (tmpGLEventListener instanceof GLCanvasRemoteRendering3D)
			{
				tmpGLRemoteRendering3D = ((GLCanvasRemoteRendering3D) tmpGLEventListener);
				tmpGLRemoteRendering3D.enableBusyMode(true);
				break;
			}
		}

		BufferedReader file = null;
		String sLine = null;
		String sFileName = "";
		String sPathwayPath = pathwayDatabase.getXMLPath();
		float fProgressFactor = 0;
		
		if (pathwayDatabase.getName().equals("KEGG"))
		{
			sFileName = generalManager.getCaleydoHomePath() + PATHWAY_LIST_KEGG;
			fProgressFactor = 100f / APPROX_PATHWAY_COUNT_KEGG ;
			
			generalManager.getSWTGUIManager().setLoadingProgressBarTextFromExternalThread(
					"Loading KEGG Pathways...");
		}
		else if (pathwayDatabase.getName().equals("BioCarta"))
		{
			sFileName = generalManager.getCaleydoHomePath() + PATHWAY_LIST_BIOCARTA;
			fProgressFactor = 100f / APPROX_PATHWAY_COUNT_BIOCARTA;
			
			generalManager.getSWTGUIManager().setLoadingProgressBarTextFromExternalThread(
				"Loading BioCarta Pathways...");
		}
		
		int iPathwayIndex = 0;
		try
		{
			if (this.getClass().getClassLoader().getResourceAsStream(sFileName) != null)
			{
				file = new BufferedReader(new InputStreamReader(this.getClass()
						.getClassLoader().getResourceAsStream(sFileName)));
			}
			else
			{
				file = new BufferedReader(new FileReader(sFileName));
			}

			
			StringTokenizer tokenizer;
			String sPathwayName;
			PathwayGraph tmpPathwayGraph;
			while ((sLine = file.readLine()) != null)
			{
				tokenizer = new StringTokenizer(sLine, " ");

				sPathwayName = tokenizer.nextToken();
				
				// Skip non pathway files
				if (!sPathwayName.endsWith(".xml") && !sLine.contains("h_"))
					continue;
				
				generalManager.getXmlParserManager().parseXmlFileByName(
						sPathwayPath + sPathwayName);

				tmpPathwayGraph = generalManager.getPathwayManager().getCurrenPathwayGraph();
				tmpPathwayGraph.setWidth(StringConversionTool.convertStringToInt(tokenizer
						.nextToken(), -1));
				tmpPathwayGraph.setHeight(StringConversionTool.convertStringToInt(tokenizer
						.nextToken(), -1));

				int iImageWidth = tmpPathwayGraph.getWidth();
				int iImageHeight = tmpPathwayGraph.getHeight();

				if (iImageWidth == -1 || iImageHeight == -1)
					generalManager.getLogger().log(
							Level.INFO,
							"Pathway texture width=" + iImageWidth + " / height="
									+ iImageHeight);
				
				iPathwayIndex++;
				
				// Update progress bar only on each 10th pathway
				if (iPathwayIndex % 10 == 0)
				{
					generalManager.getSWTGUIManager().setLoadingProgressBarPercentageFromExternalThread(
							(int)(fProgressFactor * iPathwayIndex));
				}
			}

		}
		catch (FileNotFoundException e)
		{
			throw new CaleydoRuntimeException(
					"Pathway list file: " + sFileName + " not found.",
					CaleydoRuntimeExceptionType.DATAHANDLING);
		}
		catch (IOException e)
		{
			throw new CaleydoRuntimeException(
					"Error reading data from pathway list file: " + sFileName,
					CaleydoRuntimeExceptionType.DATAHANDLING);			
		}

		if (tmpGLRemoteRendering3D != null)
			tmpGLRemoteRendering3D.enableBusyMode(false);
	}

	/**
	 * Method notifies all dependent views that the loading is ready.
	 */
	private void notifyViews()
	{
		int iTmpPathwayId;

		for (GLEventListener tmpGLEventListener : generalManager.getViewGLCanvasManager()
				.getAllGLEventListeners())
		{
			if (tmpGLEventListener instanceof GLCanvasRemoteRendering3D)
			{
				for (GLEventListener tmpGLEventListenerInner : generalManager
						.getViewGLCanvasManager().getAllGLEventListeners())
				{
					if (tmpGLEventListenerInner instanceof GLCanvasPathway3D)
					{
						iTmpPathwayId = ((GLCanvasPathway3D) tmpGLEventListenerInner)
								.getPathwayID();

						((GLCanvasRemoteRendering3D) tmpGLEventListener)
								.addPathwayView(iTmpPathwayId);
					}
				}
			}

			// if
			// (tmpGLEventListener.getClass().equals(GLCanvasPathway3D.class))
			// {
			// GLCaleydoCanvas tmpGLCanvas =
			// ((GLCanvasPathway3D)tmpGLEventListener).getParentGLCanvas();
			//				
			// // Force GLCanvas to call init(gl) of the GLEventListener again
			// // by removing and adding it from the GL canvas
			// tmpGLCanvas.removeGLEventListener(tmpGLEventListener);
			// tmpGLCanvas.addGLEventListener(tmpGLEventListener);
			// }
		}
	}
}
