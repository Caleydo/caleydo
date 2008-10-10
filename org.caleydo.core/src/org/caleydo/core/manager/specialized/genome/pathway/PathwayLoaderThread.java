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
import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;
import org.caleydo.core.util.system.StringConversionTool;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;

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
	public PathwayLoaderThread(final Collection<PathwayDatabase> pathwayDatabases)
	{
		super("Pathway Loader Thread");

		this.generalManager = GeneralManager.get();
		this.pathwayDatabases = pathwayDatabases;

		generalManager.getLogger().log(Level.INFO, "Start pathway databases loader thread");

		start();
	}

	@Override
	public void run()
	{
		super.run();

		Iterator<PathwayDatabase> iterPathwayDatabase = pathwayDatabases.iterator();
		while (iterPathwayDatabase.hasNext())
		{
			loadAllPathwaysByType(generalManager, iterPathwayDatabase.next());
		}

		generalManager.getPathwayManager().notifyPathwayLoadingFinished(true);
		// notifyViews();
	}

	public static void loadAllPathwaysByType(final IGeneralManager generalManager,
			final PathwayDatabase pathwayDatabase)
	{
		// // Try reading list of files directly from local hard dist
		// File folder = new File(sXMLPath);
		// File[] arFiles = folder.listFiles();

		generalManager.getLogger().log(Level.INFO,
				"Start parsing " + pathwayDatabase.getName() + " pathways.");

		GLRemoteRendering tmpGLRemoteRendering3D = null;
		for (AGLEventListener tmpGLEventListener : generalManager.getViewGLCanvasManager()
				.getAllGLEventListeners())
		{
			if (!tmpGLEventListener.isRenderedRemote())
				tmpGLEventListener.enableBusyMode(true);
			if (tmpGLEventListener instanceof GLRemoteRendering)
			{
				tmpGLRemoteRendering3D = ((GLRemoteRendering) tmpGLEventListener);
				// tmpGLRemoteRendering3D.enableBusyMode(true);
				// break;
			}
		}

		BufferedReader file = null;
		String sLine = null;
		String sFileName = "";
		String sPathwayPath = pathwayDatabase.getXMLPath();
		float fProgressFactor = 0;

		if (pathwayDatabase.getName().equals("KEGG"))
		{
			sFileName = IGeneralManager.CALEYDO_HOME_PATH + PATHWAY_LIST_KEGG;
			fProgressFactor = 100f / APPROX_PATHWAY_COUNT_KEGG;

			generalManager.getSWTGUIManager().setProgressBarTextFromExternalThread(
					"Loading KEGG Pathways...");
		}
		else if (pathwayDatabase.getName().equals("BioCarta"))
		{
			sFileName = IGeneralManager.CALEYDO_HOME_PATH + PATHWAY_LIST_BIOCARTA;
			fProgressFactor = 100f / APPROX_PATHWAY_COUNT_BIOCARTA;

			generalManager.getSWTGUIManager().setProgressBarTextFromExternalThread(
					"Loading BioCarta Pathways...");
		}

		int iPathwayIndex = 0;
		try
		{
			if (generalManager.getClass().getClassLoader().getResourceAsStream(sFileName) != null)
			{
				file = new BufferedReader(new InputStreamReader(generalManager.getClass()
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

				tmpPathwayGraph = ((PathwayManager) generalManager.getPathwayManager())
						.getCurrenPathwayGraph();
				tmpPathwayGraph.setWidth(StringConversionTool.convertStringToInt(tokenizer
						.nextToken(), -1));
				tmpPathwayGraph.setHeight(StringConversionTool.convertStringToInt(tokenizer
						.nextToken(), -1));

				int iImageWidth = tmpPathwayGraph.getWidth();
				int iImageHeight = tmpPathwayGraph.getHeight();

				if (iImageWidth == -1 || iImageHeight == -1)
				{
					generalManager.getLogger().log(
							Level.INFO,
							"Pathway texture width=" + iImageWidth + " / height="
									+ iImageHeight);
				}

				iPathwayIndex++;

				// Update progress bar only on each 10th pathway
				if (iPathwayIndex % 10 == 0)
				{
					generalManager.getSWTGUIManager()
							.setProgressBarPercentageFromExternalThread(
									(int) (fProgressFactor * iPathwayIndex));
				}
			}

		}
		catch (FileNotFoundException e)
		{
			throw new CaleydoRuntimeException("Pathway list file: " + sFileName
					+ " not found.", CaleydoRuntimeExceptionType.DATAHANDLING);
		}
		catch (IOException e)
		{
			throw new CaleydoRuntimeException("Error reading data from pathway list file: "
					+ sFileName, CaleydoRuntimeExceptionType.DATAHANDLING);
		}

		for (AGLEventListener tmpGLEventListener : generalManager.getViewGLCanvasManager()
				.getAllGLEventListeners())
		{
			if (!tmpGLEventListener.isRenderedRemote())
				tmpGLEventListener.enableBusyMode(false);
		}

		// if (tmpGLRemoteRendering3D != null)
		// tmpGLRemoteRendering3D.enableBusyMode(false);

		generalManager.getLogger().log(Level.INFO,
				"Finished parsing " + pathwayDatabase.getName() + " pathways.");
	}
}
