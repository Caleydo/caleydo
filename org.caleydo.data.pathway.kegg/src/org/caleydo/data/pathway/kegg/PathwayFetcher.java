/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 * 
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/

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
