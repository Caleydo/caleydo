package org.caleydo.core.manager.specialized.genetic.pathway;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.caleydo.core.application.helper.PathwayListGenerator;
import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.genetic.EOrganism;
import org.caleydo.core.manager.specialized.genetic.GeneticUseCase;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Loads all pathways of a certain type by providing the XML path.
 * 
 * @author Marc Streit
 */
public class PathwayLoaderThread
	extends Thread {

	private IGeneralManager generalManager;

	private Collection<PathwayDatabase> pathwayDatabases;

	/**
	 * Constructor.
	 */
	public PathwayLoaderThread(final Collection<PathwayDatabase> pathwayDatabases) {
		super("Pathway Loader Thread");

		this.generalManager = GeneralManager.get();
		this.pathwayDatabases = pathwayDatabases;

		generalManager.getLogger().log(
			new Status(IStatus.INFO, IGeneralManager.PLUGIN_ID, "Start pathway databases loader thread"));

		start();
	}

	@Override
	public void run() {
		super.run();

		IViewManager viewManager = generalManager.getViewGLCanvasManager();
		viewManager.requestBusyMode(this);

		Iterator<PathwayDatabase> iterPathwayDatabase = pathwayDatabases.iterator();
		while (iterPathwayDatabase.hasNext()) {
			loadAllPathwaysByType(generalManager, iterPathwayDatabase.next());
		}

		viewManager.releaseBusyMode(this);
		generalManager.getPathwayManager().notifyPathwayLoadingFinished(true);
		// notifyViews();
	}

	public static void loadAllPathwaysByType(final IGeneralManager generalManager,
		final PathwayDatabase pathwayDatabase) {
		// // Try reading list of files directly from local hard dist
		// File folder = new File(sXMLPath);
		// File[] arFiles = folder.listFiles();

		generalManager.getLogger().log(
			new Status(IStatus.INFO, IGeneralManager.PLUGIN_ID, "Start parsing " + pathwayDatabase.getName()
				+ " pathways."));

		BufferedReader file = null;
		String sLine = null;
		String sFileName = "";
		String sPathwayPath = pathwayDatabase.getXMLPath();
		IPathwayResourceLoader pathwayResourceLoader = null;
		EOrganism eOrganism =
			((GeneticUseCase) GeneralManager.get().getUseCase(EDataDomain.GENETIC_DATA)).getOrganism();

		if (pathwayDatabase.getName().equals("KEGG")) {

			if (eOrganism == EOrganism.HOMO_SAPIENS) {
				sFileName = "data/pathway_list_KEGG_homo_sapiens.txt";
			}
			else if (eOrganism == EOrganism.MUS_MUSCULUS) {
				sFileName = "data/pathway_list_KEGG_mus_musculus.txt";
			}
			else {
				throw new IllegalStateException("Cannot load pathways from organism " + eOrganism);
			}

			generalManager.getSWTGUIManager()
				.setProgressBarTextFromExternalThread("Loading KEGG Pathways...");

			generalManager.getPathwayManager().createPathwayResourceLoader();
			pathwayResourceLoader = generalManager.getPathwayManager().getPathwayResourceLoader();
		}
		else if (pathwayDatabase.getName().equals("BioCarta")) {

			if (eOrganism == EOrganism.HOMO_SAPIENS) {
				sFileName =
					IGeneralManager.CALEYDO_HOME_PATH
						+ PathwayListGenerator.OUTPUT_FILE_NAME_BIOCARTA_HOMO_SAPIENS;
			}
			else if (eOrganism == EOrganism.MUS_MUSCULUS) {
				sFileName =
					IGeneralManager.CALEYDO_HOME_PATH
						+ PathwayListGenerator.OUTPUT_FILE_NAME_BIOCARTA_MUS_MUSCULUS;
			}
			else {
				throw new IllegalStateException("Cannot load pathways from organism " + eOrganism);
			}

			generalManager.getSWTGUIManager().setProgressBarTextFromExternalThread(
				"Loading BioCarta Pathways...");
		}

		int iPathwayIndex = 0;

		try {

			if (pathwayDatabase.getName().equals("KEGG"))
				file = pathwayResourceLoader.getResource(sFileName);
			else
				file = GeneralManager.get().getResourceLoader().getResource(sFileName);

			StringTokenizer tokenizer;
			String sPathwayName;
			PathwayGraph tmpPathwayGraph;
			while ((sLine = file.readLine()) != null) {
				tokenizer = new StringTokenizer(sLine, " ");

				sPathwayName = tokenizer.nextToken();

				// Skip non pathway files
				if (!sPathwayName.endsWith(".xml") && !sLine.contains("h_") && !sLine.contains("m_")) {
					continue;
				}

				generalManager.getXmlParserManager().parseXmlFileByName(sPathwayPath + sPathwayName);

				tmpPathwayGraph =
					((PathwayManager) generalManager.getPathwayManager()).getCurrenPathwayGraph();
				tmpPathwayGraph.setWidth(Integer.valueOf(tokenizer.nextToken()).intValue());
				tmpPathwayGraph.setHeight(Integer.valueOf(tokenizer.nextToken()).intValue());

				int iImageWidth = tmpPathwayGraph.getWidth();
				int iImageHeight = tmpPathwayGraph.getHeight();

				if (iImageWidth == -1 || iImageHeight == -1) {
					generalManager.getLogger().log(
						new Status(IStatus.INFO, IGeneralManager.PLUGIN_ID, "Pathway texture width="
							+ iImageWidth + " / height=" + iImageHeight));
				}

				iPathwayIndex++;
			}

		}
		catch (FileNotFoundException e) {
			throw new IllegalStateException("Pathway list file " + sFileName + " not found.");
		}
		catch (IOException e) {
			throw new IllegalStateException("Error reading data from pathway list file: " + sFileName);
		}

		// if (tmpGLRemoteRendering3D != null)
		// {
		// tmpGLRemoteRendering3D.enableBusyMode(false);
		// }

		generalManager.getLogger().log(
			new Status(IStatus.INFO, IGeneralManager.PLUGIN_ID, "Finished parsing "
				+ pathwayDatabase.getName() + " pathways."));
	}
}
