package org.caleydo.datadomain.pathway.manager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.StringTokenizer;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.gui.preferences.PreferenceConstants;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.specialized.Organism;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Loads all pathways of a certain type by providing the XML path.
 * 
 * @author Marc Streit
 */
public class PathwayLoaderThread extends Thread {

	/**
	 * Constructor.
	 */
	public PathwayLoaderThread() {
		super("Pathway Loader Thread");

		Logger.log(new Status(IStatus.INFO, this.toString(),
				"Start pathway databases loader thread"));
	}

	@Override
	public void run() {
		super.run();

		String pathwayDataSources = GeneralManager.get().getPreferenceStore()
				.getString(PreferenceConstants.LAST_CHOSEN_PATHWAY_DATA_SOURCES);

		if (pathwayDataSources.contains(PathwayDatabaseType.BIOCARTA.getName())) {

			PathwayDataDomain pathwayDataDomain = (PathwayDataDomain) DataDomainManager
					.get().createDataDomain("org.caleydo.datadomain.pathway");
			
			pathwayDataDomain.init();
			Thread thread = new Thread(pathwayDataDomain, pathwayDataDomain.getDataDomainType());
			thread.start();
			
			pathwayDataDomain.setPathwayDatabaseType(PathwayDatabaseType.BIOCARTA);

			PathwayDatabase pathwayDatabase = PathwayManager.get().createPathwayDatabase(
					PathwayDatabaseType.BIOCARTA, "data/html/", "data/images/",
					"data/html");

			loadPathwaysByType(pathwayDatabase);
		}

		if (pathwayDataSources.contains(PathwayDatabaseType.KEGG.getName())) {

			PathwayDataDomain pathwayDataDomain = (PathwayDataDomain) DataDomainManager
					.get().createDataDomain("org.caleydo.datadomain.pathway");

			pathwayDataDomain.init();
			Thread thread = new Thread(pathwayDataDomain, pathwayDataDomain.getDataDomainType());
			thread.start();
			
			pathwayDataDomain.setPathwayDatabaseType(PathwayDatabaseType.KEGG);

			PathwayDatabase pathwayDatabase = PathwayManager.get().createPathwayDatabase(
					PathwayDatabaseType.KEGG, "data/xml/", "data/images/", "");

			loadPathwaysByType(pathwayDatabase);
		}

		PathwayManager.get().notifyPathwayLoadingFinished(true);
	}

	public void loadPathwaysByType(PathwayDatabase pathwayDatabase) {

		// // Try reading list of files directly from local hard dist
		// File folder = new File(sXMLPath);
		// File[] arFiles = folder.listFiles();

		GeneralManager generalManager = GeneralManager.get();

		Logger.log(new Status(IStatus.INFO, "PathwayLoaderThread", "Start parsing "
				+ pathwayDatabase.getName() + " pathways."));

		BufferedReader file = null;
		String line = null;
		String fileName = "";
		String pathwayPath = pathwayDatabase.getXMLPath();
		IPathwayResourceLoader pathwayResourceLoader = null;
		Organism organism = GeneralManager.get().getBasicInfo().getOrganism();

		if (pathwayDatabase.getType() == PathwayDatabaseType.KEGG) {

			if (organism == Organism.HOMO_SAPIENS) {
				fileName = "data/pathway_list_KEGG_homo_sapiens.txt";
			} else if (organism == Organism.MUS_MUSCULUS) {
				fileName = "data/pathway_list_KEGG_mus_musculus.txt";
			} else {
				throw new IllegalStateException("Cannot load pathways from organism "
						+ organism);
			}

			generalManager.getSWTGUIManager().setProgressBarTextFromExternalThread(
					"Loading KEGG Pathways...");
		} else if (pathwayDatabase.getType() == PathwayDatabaseType.BIOCARTA) {

			if (organism == Organism.HOMO_SAPIENS) {
				fileName = "data/pathway_list_BIOCARTA_homo_sapiens.txt";
			} else if (organism == Organism.MUS_MUSCULUS) {
				fileName = "data/pathway_list_BIOCARTA_mus_musculus.txt";
			} else {
				throw new IllegalStateException("Cannot load pathways from organism "
						+ organism);
			}

			generalManager.getSWTGUIManager().setProgressBarTextFromExternalThread(
					"Loading BioCarta Pathways...");
		}

		PathwayManager.get().createPathwayResourceLoader(pathwayDatabase.getType());
		pathwayResourceLoader = PathwayManager.get().getPathwayResourceLoader(
				pathwayDatabase.getType());

		try {

			if (pathwayDatabase.getType() == PathwayDatabaseType.KEGG
					|| pathwayDatabase.getType() == PathwayDatabaseType.BIOCARTA)
				file = pathwayResourceLoader.getResource(fileName);
			else
				file = GeneralManager.get().getResourceLoader().getResource(fileName);

			StringTokenizer tokenizer;
			String pathwayName;
			PathwayGraph tmpPathwayGraph;
			while ((line = file.readLine()) != null) {
				tokenizer = new StringTokenizer(line, " ");

				pathwayName = tokenizer.nextToken();

				// Skip non pathway files
				if (!pathwayName.endsWith(".xml") && !line.contains("h_")
						&& !line.contains("m_")) {
					continue;
				}

				PathwayManager.get().getXmlParserManager()
						.parseXmlFileByName(pathwayPath + pathwayName);

				tmpPathwayGraph = ((PathwayManager) PathwayManager.get())
						.getCurrenPathwayGraph();
				tmpPathwayGraph.setWidth(Integer.valueOf(tokenizer.nextToken())
						.intValue());
				tmpPathwayGraph.setHeight(Integer.valueOf(tokenizer.nextToken())
						.intValue());

				int iImageWidth = tmpPathwayGraph.getWidth();
				int iImageHeight = tmpPathwayGraph.getHeight();

				if (iImageWidth == -1 || iImageHeight == -1) {
					Logger.log(new Status(IStatus.INFO, "PathwayLoaderThread",
							"Pathway texture width=" + iImageWidth + " / height="
									+ iImageHeight));
				}
			}

		} catch (FileNotFoundException e) {
			throw new IllegalStateException("Pathway list file " + fileName
					+ " not found.");
		} catch (IOException e) {
			throw new IllegalStateException("Error reading data from pathway list file: "
					+ fileName);
		}

		Logger.log(new Status(IStatus.INFO, "PathwayLoaderThread", "Finished parsing "
				+ pathwayDatabase.getName() + " pathways."));
	}
}
