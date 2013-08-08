/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.data.pathway.kegg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.caleydo.core.util.system.UrlDownload;

/**
 * Class for downloading all KEGG pathways for a specific organism
 * 
 * @author Marc Streit
 * 
 */
public class PathwayFetcher {

	private static String PATHWAY_LIST_URL = "http://rest.kegg.jp/list/pathway/";

	private static String PATHWAY_OUTPUT_PATH = "/home/streit/tmp/kegg/";

	public static void main(String[] args) {
		PathwayFetcher pathwayFetcher = new PathwayFetcher();
		pathwayFetcher.run();
	}

	public void run() {

		downloadPathwayFiles(PATHWAY_LIST_URL + "hsa", "http://rest.kegg.jp/get/", "/image",
				".png");
		downloadPathwayFiles(PATHWAY_LIST_URL + "hsa",
				"http://www.genome.jp/kegg-bin/download?entry=", "&format=kgml", ".xml");

		downloadPathwayFiles(PATHWAY_LIST_URL + "mmu", "http://rest.kegg.jp/get/", "/image",
				".png");
		downloadPathwayFiles(PATHWAY_LIST_URL + "mmu",
				"http://www.genome.jp/kegg-bin/download?entry=", "&format=kgml", ".xml");
	}

	private void downloadPathwayFiles(String pathwayUrl, String downloadPrefix,
			String downloadPostfix, String outputfileExtension) {
		try {
			URL url = new URL(pathwayUrl);
			URLConnection uc = url.openConnection();

			InputStreamReader input = new InputStreamReader(uc.getInputStream());
			BufferedReader in = new BufferedReader(input);
			String inputLine;

			while ((inputLine = in.readLine()) != null) {

				String pathwayID = inputLine.substring(5, 13);
				UrlDownload.fileUrl(downloadPrefix + pathwayID + downloadPostfix,
						PATHWAY_OUTPUT_PATH + pathwayID + outputfileExtension);
			}

			in.close();
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
