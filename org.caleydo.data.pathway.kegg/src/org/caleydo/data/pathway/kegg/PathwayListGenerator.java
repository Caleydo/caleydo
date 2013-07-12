/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.data.pathway.kegg;

import java.io.File;
import java.io.FileNotFoundException;
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

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
