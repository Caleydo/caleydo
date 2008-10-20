package org.caleydo.core.application.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.ImageIcon;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;

/**
 * Helper tool can load a pathway list from a local folder. This is needed
 * because the folder.listFiles method does not work in deployed RCP
 * applications.
 * 
 * @author Marc Streit
 */
public class PathwayListGenerator
{
	public final static String INPUT_FOLDER_PATH_KEGG = "www.genome.jp/kegg/KGML/KGML_v0.6.1/hsa/";
	public final static String INPUT_IMAGE_PATH_KEGG = "www.genome.ad.jp/kegg/pathway/hsa/";
	public final static String OUTPUT_FILE_NAME_KEGG = "pathway_list_KEGG.txt";
	public final static String INPUT_FOLDER_PATH_BIOCARTA = "cgap.nci.nih.gov/Pathways/BioCarta/";
	public final static String INPUT_IMAGE_PATH_BIOCARTA = "cgap.nci.nih.gov/BIOCARTA/Pathways/";
	public final static String OUTPUT_FILE_NAME_BIOCARTA = "pathway_list_BIOCARTA.txt";

	private PrintWriter outputWriter;

	public void run(String sInputFolderPath, String sInputImagePath, String sOutputFileName)
			throws FileNotFoundException
	{
		sInputFolderPath = IGeneralManager.CALEYDO_HOME_PATH + sInputFolderPath;
		sInputImagePath = IGeneralManager.CALEYDO_HOME_PATH + sInputImagePath;
		sOutputFileName = IGeneralManager.CALEYDO_HOME_PATH + sOutputFileName;

		outputWriter = new PrintWriter(sOutputFileName);

		File folder = new File(sInputFolderPath);
		File[] arFiles = folder.listFiles();
		String sOutput = "";

		for (File tmpFile : arFiles)
		{
			if (tmpFile.toString().endsWith(".svn"))
				continue;

			// Ignore mice pathways
			if (tmpFile.toString().contains("m_"))
				continue;

			// Cut off path
			sOutput = tmpFile.toString();
			String sPathDelimiter = "";
			if (sOutput.contains("\\"))
			{
				sPathDelimiter = "\\";
			}
			else if (sOutput.contains("/"))
			{
				sPathDelimiter = "/";
			}
			else
			{
				throw new CaleydoRuntimeException("Problem with detecting path separator.",
						CaleydoRuntimeExceptionType.DATAHANDLING);
			}

			sOutput = sOutput.substring(sOutput.lastIndexOf(sPathDelimiter) + 1, sOutput
					.length());

			outputWriter.append(sOutput + " ");

			String sImagePath = "";
			if (tmpFile.toString().contains(".xml"))
			{
				sImagePath = sInputImagePath
						+ tmpFile.toString().substring(
								tmpFile.toString().lastIndexOf(sPathDelimiter) + 1,
								tmpFile.toString().length() - 4) + ".gif";
			}
			// find out image path of biocarta pathway - necessary because xml
			// path != image path
			else
			{
				BufferedReader brFile = new BufferedReader(new FileReader(tmpFile.toString()));

				String sLine = "";
				try
				{
					while ((sLine = brFile.readLine()) != null)
					{
						if (sLine.contains("http://cgap.nci.nih.gov/BIOCARTA/Pathways/"))
						{
							sImagePath = sLine.substring(sLine
											.indexOf("http://cgap.nci.nih.gov/BIOCARTA/Pathways/") + 42,
										sLine.indexOf(".gif", sLine
											.indexOf("http://cgap.nci.nih.gov/BIOCARTA/Pathways/")) + 4);

							sImagePath = sInputImagePath + sImagePath;

							break;
						}
					}
				}
				catch (IOException e)
				{
					throw new CaleydoRuntimeException("Cannot open pathway list file at "
							+ tmpFile.toString(), CaleydoRuntimeExceptionType.DATAHANDLING);
				}

			}
			ImageIcon img = new ImageIcon(sImagePath);
			outputWriter.append(img.getIconWidth() + " " + img.getIconHeight());

			img = null;

			outputWriter.append("\n");
		}

		outputWriter.flush();
		outputWriter.close();
	}

	public static void main(String[] args)
	{
		PathwayListGenerator pathwayListLoader = new PathwayListGenerator();

		try
		{
			pathwayListLoader.run(INPUT_FOLDER_PATH_KEGG, INPUT_IMAGE_PATH_KEGG,
					OUTPUT_FILE_NAME_KEGG);
			pathwayListLoader.run(INPUT_FOLDER_PATH_BIOCARTA, INPUT_IMAGE_PATH_BIOCARTA,
					OUTPUT_FILE_NAME_BIOCARTA);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
}
