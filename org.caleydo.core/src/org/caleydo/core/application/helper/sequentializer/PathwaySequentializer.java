package org.caleydo.core.application.helper.sequentializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.caleydo.core.manager.IGeneralManager;

/**
 * Helper tool can append all pathway files to one huge file.
 * 
 * @author Marc Streit
 */
public class PathwaySequentializer
{
	public final static String INPUT_FOLDER_PATH_KEGG =
		"www.genome.jp/kegg/KGML/KGML_v0.6.1/hsa/";
	public final static String INPUT_IMAGE_PATH_KEGG = "www.genome.ad.jp/kegg/pathway/hsa/";
	public final static String OUTPUT_FILE_NAME_KEGG = "pathway_sources_KEGG.txt";
	public final static String INPUT_FOLDER_PATH_BIOCARTA =
		"cgap.nci.nih.gov/Pathways/BioCarta/";
	public final static String INPUT_IMAGE_PATH_BIOCARTA =
		"cgap.nci.nih.gov/BIOCARTA/Pathways/";
	public final static String OUTPUT_FILE_NAME_BIOCARTA = "pathway_sources_BIOCARTA.txt";

	private OutputStream outputStream;

	public void run(String sInputFolderPath, String sInputImagePath, String sOutputFileName)
		throws FileNotFoundException
	{
		sInputFolderPath = IGeneralManager.CALEYDO_HOME_PATH + sInputFolderPath;
		sInputImagePath = IGeneralManager.CALEYDO_HOME_PATH + sInputImagePath;
		sOutputFileName = IGeneralManager.CALEYDO_HOME_PATH + sOutputFileName;

		outputStream = new FileOutputStream(new File(sOutputFileName), false);

		File folder = new File(sInputFolderPath);
		File[] arFiles = folder.listFiles();

		for (File tmpFile : arFiles)
		{
			if (tmpFile.toString().endsWith(".svn"))
				continue;

			// Ignore mice pathways
			if (tmpFile.toString().contains("m_"))
				continue;

			copyfile(tmpFile);
		}

		try
		{
			outputStream.close();
		}
		catch (IOException e)
		{
			System.out.println("Cannot close output stream.");
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		PathwaySequentializer pathwaySequentializer = new PathwaySequentializer();

		try
		{
			pathwaySequentializer.run(INPUT_FOLDER_PATH_KEGG, INPUT_IMAGE_PATH_KEGG,
				OUTPUT_FILE_NAME_KEGG);
			pathwaySequentializer.run(INPUT_FOLDER_PATH_BIOCARTA, INPUT_IMAGE_PATH_BIOCARTA,
				OUTPUT_FILE_NAME_BIOCARTA);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	private void copyfile(File file)
	{
		try
		{
			InputStream in = new FileInputStream(file);

			byte[] buf = new byte[1024];

			int len;

			while ((len = in.read(buf)) > 0)
			{
				outputStream.write(buf, 0, len);
			}

			in.close();

			System.out.println("File copied: " +file.getName());

		}

		catch (FileNotFoundException ex)
		{

			System.out.println(ex.getMessage() + " in the specified directory.");

			System.exit(0);
		}
		catch (IOException e)
		{

			System.out.println(e.getMessage());

		}

	}
}
