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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.ImageIcon;

/**
 * Helper tool can load a pathway list from a local folder. This is needed
 * because the folder.listFiles method does not work in deployed RCP
 * applications.
 * 
 * @author Marc Streit
 */
public class PathwayListGenerator {

	public final static String INPUT_FOLDER_PATH_KEGG_HOMO_SAPIENS = "data/xml/hsa";
	public final static String INPUT_IMAGE_PATH_KEGG_HOMO_SAPIENS = "data/images/hsa/";
	public final static String OUTPUT_FILE_NAME_KEGG_HOMO_SAPIENS = "data/pathway_list_KEGG_homo_sapiens.txt";

	public final static String INPUT_FOLDER_PATH_KEGG_MUS_MUSCULUS = "data/xml/mmu/";
	public final static String INPUT_IMAGE_PATH_KEGG_MUS_MUSCULUS = "data/images/mmu/";
	public final static String OUTPUT_FILE_NAME_KEGG_MUS_MUSCULUS = "data/pathway_list_KEGG_mus_musculus.txt";

	// public final static String INPUT_FOLDER_PATH_BIOCARTA =
	// "cgap.nci.nih.gov/Pathways/BioCarta/";
	// public final static String INPUT_IMAGE_PATH_BIOCARTA =
	// "cgap.nci.nih.gov/BIOCARTA/Pathways/";
	// public final static String OUTPUT_FILE_NAME_BIOCARTA_HOMO_SAPIENS =
	// "pathway_list_BIOCARTA_homo_sapiens.txt";
	// public final static String OUTPUT_FILE_NAME_BIOCARTA_MUS_MUSCULUS =
	// "pathway_list_BIOCARTA_mus_musculus.txt";

	private PrintWriter outputWriter;

	public void run(String sInputFolderPath, String sInputImagePath,
			String sOutputFileName) throws FileNotFoundException {

		outputWriter = new PrintWriter(sOutputFileName);

		File folder = new File(sInputFolderPath);
		File[] arFiles = folder.listFiles();
		String sOutput = "";

		for (File tmpFile : arFiles) {
			if (tmpFile.toString().endsWith(".svn")) {
				continue;
			}

			// Ignore mus musculus pathways when generating homo sapiens list
			if (sOutputFileName.contains("homo_sapiens")
					&& tmpFile.toString().contains("m_")) {
				continue;
			}

			// Ignore homo sapiens pathways when generating mus musculus list
			if (sOutputFileName.contains("mus_musculus")
					&& tmpFile.toString().contains("h_")) {
				continue;
			}

			// Cut off path
			sOutput = tmpFile.toString();
			String sPathDelimiter = "";
			if (sOutput.contains("\\")) {
				sPathDelimiter = "\\";
			} else if (sOutput.contains("/")) {
				sPathDelimiter = "/";
			} else
				throw new IllegalStateException("Problem with detecting path separator.");

			sOutput = sOutput.substring(sOutput.lastIndexOf(sPathDelimiter) + 1,
					sOutput.length());

			String sImagePath = "";
			if (tmpFile.toString().contains(".xml")) {
				sImagePath = sInputImagePath
						+ tmpFile.toString().substring(
								tmpFile.toString().lastIndexOf(sPathDelimiter) + 1,
								tmpFile.toString().length() - 4) + ".png";
			}
			// find out image path of biocarta pathway - necessary because xml
			// path != image path
			else {
				BufferedReader brFile = new BufferedReader(new FileReader(
						tmpFile.toString()));

				String sLine = "";
				try {
					while ((sLine = brFile.readLine()) != null) {
						if (sLine.contains("http://cgap.nci.nih.gov/BIOCARTA/Pathways/")) {
							sImagePath = sLine
									.substring(
											sLine.indexOf("http://cgap.nci.nih.gov/BIOCARTA/Pathways/") + 42,
											sLine.indexOf(
													".gif",
													sLine.indexOf("http://cgap.nci.nih.gov/BIOCARTA/Pathways/")) + 4);

							sImagePath = sInputImagePath + sImagePath;

							break;
						}
					}
				} catch (IOException e) {
					throw new IllegalStateException("Cannot open pathway list file at "
							+ tmpFile.toString());
				}

			}
			ImageIcon img = new ImageIcon(sImagePath);
			int iWidth = img.getIconWidth();
			int iHeight = img.getIconHeight();

			if (iWidth != -1 && iHeight != -1) {
				outputWriter.append(sOutput + " ");
				outputWriter.append(img.getIconWidth() + " " + img.getIconHeight());
				outputWriter.append("\n");
			}

			img = null;
		}

		outputWriter.flush();
		outputWriter.close();
	}

	public static void main(String[] args) {
		PathwayListGenerator pathwayListLoader = new PathwayListGenerator();

		try {
			pathwayListLoader.run(INPUT_FOLDER_PATH_KEGG_HOMO_SAPIENS,
					INPUT_IMAGE_PATH_KEGG_HOMO_SAPIENS,
					OUTPUT_FILE_NAME_KEGG_HOMO_SAPIENS);
			pathwayListLoader.run(INPUT_FOLDER_PATH_KEGG_MUS_MUSCULUS,
					INPUT_IMAGE_PATH_KEGG_MUS_MUSCULUS,
					OUTPUT_FILE_NAME_KEGG_MUS_MUSCULUS);
			// pathwayListLoader.run(INPUT_FOLDER_PATH_BIOCARTA,
			// INPUT_IMAGE_PATH_BIOCARTA,
			// OUTPUT_FILE_NAME_BIOCARTA_HOMO_SAPIENS);
			// pathwayListLoader.run(INPUT_FOLDER_PATH_BIOCARTA,
			// INPUT_IMAGE_PATH_BIOCARTA,
			// OUTPUT_FILE_NAME_BIOCARTA_MUS_MUSCULUS);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
