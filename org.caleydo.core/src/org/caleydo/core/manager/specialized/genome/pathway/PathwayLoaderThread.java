package org.caleydo.core.manager.specialized.genome.pathway;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.logging.Level;
import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;

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

		// Turn on busy mode
		for (AGLEventListener tmpGLEventListener : generalManager.getViewGLCanvasManager()
				.getAllGLEventListeners())
		{
			if (!tmpGLEventListener.isRenderedRemote())
				tmpGLEventListener.enableBusyMode(true);
		}

		Iterator<PathwayDatabase> iterPathwayDatabase = pathwayDatabases.iterator();
		while (iterPathwayDatabase.hasNext())
		{
			loadAllPathwaysByType(generalManager, iterPathwayDatabase.next());
		}

		// Turn off busy mode
		for (AGLEventListener tmpGLEventListener : generalManager.getViewGLCanvasManager()
				.getAllGLEventListeners())
		{
			if (!tmpGLEventListener.isRenderedRemote())
				tmpGLEventListener.enableBusyMode(false);
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
			file = GeneralManager.get().getResourceLoader().getResource(sFileName);

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
				tmpPathwayGraph.setWidth(Integer.valueOf(tokenizer.nextToken()).intValue());
				tmpPathwayGraph.setHeight(Integer.valueOf(tokenizer.nextToken()).intValue());

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
			throw new IllegalStateException("Pathway list file: " + sFileName + " not found.");
		}
		catch (IOException e)
		{
			throw new IllegalStateException("Error reading data from pathway list file: "
					+ sFileName);
		}

		// if (tmpGLRemoteRendering3D != null)
		// {
		// tmpGLRemoteRendering3D.enableBusyMode(false);
		// }

		generalManager.getLogger().log(Level.INFO,
				"Finished parsing " + pathwayDatabase.getName() + " pathways.");
	}
}
