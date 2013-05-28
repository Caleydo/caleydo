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
package org.caleydo.data.pathway.wikipathways;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.swing.ImageIcon;

import org.pathvisio.core.model.ConverterException;
import org.pathvisio.core.model.GpmlFormat;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.core.model.PathwayExporter;
import org.pathvisio.core.model.PathwayImporter;
import org.pathvisio.core.model.RasterImageExporter;
import org.pathvisio.core.preferences.PreferenceManager;

/**
 * Helper tool can load a pathway list from a local folder. This is needed because the folder.listFiles method does not
 * work in deployed RCP applications.
 *
 * @author Marc Streit
 */
public class PathwayListGenerator {

	public final static String XML_FOLDER_WIKIPATHWAYS_HOMO_SAPIENS = "data/xml/hsa/";
	public final static String IMAGE_WIKIPATHWAYS_HOMO_SAPIENS = "data/images/hsa/";
	public final static String LIST_FILE_WIKIPATHWAYS_HOMO_SAPIENS = "data/pathway_list_Wikipathways_homo_sapiens.txt";

	public final static String XML_FOLDER_WIKIPATHWAYS_MUS_MUSCULUS = "data/xml/mmu/";
	public final static String IMAGE_FOLDER_WIKIPATHWAYS_MUS_MUSCULUS = "data/images/mmu/";
	public final static String LIST_FILE_WIKIPATHWAYS_MUS_MUSCULUS = "data/pathway_list_Wikipathways_mus_musculus.txt";

	private PrintWriter outputWriter;

	public void run(String inputFolderPath, String inputImagePath, String outputFileName, boolean generatePathwayImages)
			throws FileNotFoundException {

		outputWriter = new PrintWriter(outputFileName);

		PreferenceManager.init();

		File folder = new File(inputFolderPath);
		File[] files = folder.listFiles();
		String output = "";
		try {
			for (File tmpFile : files) {
				if (!tmpFile.toString().endsWith(".gpml")) {
					continue;
				}

				// Cut off path
				output = tmpFile.toString();
				String sPathDelimiter = "";
				if (output.contains("\\")) {
					sPathDelimiter = "\\";
				} else if (output.contains("/")) {
					sPathDelimiter = "/";
				} else
					throw new IllegalStateException("Problem with detecting path separator.");

				if (generatePathwayImages) {
					PathwayImporter importer = new GpmlFormat();

					Pathway pathway = importer.doImport(tmpFile);
					PathwayExporter exporter = new RasterImageExporter("png");
					exporter.doExport(
							new File(inputImagePath
									+ tmpFile.toString().substring(tmpFile.toString().lastIndexOf(sPathDelimiter) + 1,
											tmpFile.toString().length() - 5) + ".png"), pathway);
				}
				output = output.substring(output.lastIndexOf(sPathDelimiter) + 1, output.length());

				String imagePath = "";

				imagePath = inputImagePath
						+ tmpFile.toString().substring(tmpFile.toString().lastIndexOf(sPathDelimiter) + 1,
								tmpFile.toString().length() - 5) + ".png";

				ImageIcon img = new ImageIcon(imagePath);
				int width = img.getIconWidth();
				int height = img.getIconHeight();

				if (width != -1 && height != -1) {
					outputWriter.append(output + " ");
					outputWriter.append(img.getIconWidth() + " " + img.getIconHeight());
					outputWriter.append("\n");
				}

				img = null;
			}
		} catch (ConverterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		outputWriter.flush();
		outputWriter.close();
	}

	public static void main(String[] args) {
		PathwayListGenerator pathwayListLoader = new PathwayListGenerator();

		try {
			pathwayListLoader.run(XML_FOLDER_WIKIPATHWAYS_HOMO_SAPIENS, IMAGE_WIKIPATHWAYS_HOMO_SAPIENS,
					LIST_FILE_WIKIPATHWAYS_HOMO_SAPIENS, false);
			pathwayListLoader.run(XML_FOLDER_WIKIPATHWAYS_MUS_MUSCULUS, IMAGE_FOLDER_WIKIPATHWAYS_MUS_MUSCULUS,
					LIST_FILE_WIKIPATHWAYS_MUS_MUSCULUS, false);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
