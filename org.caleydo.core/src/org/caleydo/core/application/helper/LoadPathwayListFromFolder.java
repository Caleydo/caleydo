package org.caleydo.core.application.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.ImageIcon;

/**
 * Helper tool can load a pathway list from a local folder.
 * This is needed because the folder.listFiles method does not work in deployed RCP applications.
 *  
 * @author Marc Streit
 *
 */
public class LoadPathwayListFromFolder {

	private final static String INPUT_FOLDER_PATH_KEGG = "data/genome/pathway/xml/hsa/";
	private final static String INPUT_IMAGE_PATH_KEGG = "data/genome/pathway/images/hsa/";
	private final static String OUTPUT_FILE_NAME_KEGG = "data/genome/pathway/pathway_list_KEGG.txt";

	private final static String INPUT_FOLDER_PATH_BIOCARTA = "data/genome/pathway/biocarta/imagemap/";
	private final static String INPUT_IMAGE_PATH_BIOCARTA = "data/genome/pathway/biocarta/image/";
	private final static String OUTPUT_FILE_NAME_BIOCARTA = "data/genome/pathway/pathway_list_BIOCARTA.txt";
	
	private PrintWriter outputWriter;
	
	public void run(final String sInputFolderPath,
			final String sInputImagePath, final String sOutputFileName) 
		throws FileNotFoundException {
		
		outputWriter = new PrintWriter(sOutputFileName);		
		
	    File folder = new File(sInputFolderPath);
	    File[] arFiles = folder.listFiles();
	    
	    for (File tmpFile : arFiles)
	    {
	    	if (tmpFile.toString().endsWith(".svn"))
	    		continue;
	    	
	    	outputWriter.append(tmpFile.toString() + " ");

	    	String sImagePath = "";
	    	if (tmpFile.toString().contains(".xml"))
	    	{
	    		sImagePath = sInputImagePath
					+ tmpFile.toString().substring(tmpFile.toString().lastIndexOf('/') + 1, 
							tmpFile.toString().length() - 4) + ".gif";
	    	}
	    	// find out image path of biocarta pathway - necessary because xml path != image path
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
							sImagePath = sLine.substring(sLine.indexOf("http://cgap.nci.nih.gov/BIOCARTA/Pathways/") + 42,
									sLine.indexOf(".gif", sLine.indexOf("http://cgap.nci.nih.gov/BIOCARTA/Pathways/")) + 4);

							sImagePath = sInputImagePath + sImagePath;
							
							break;
						}
					}
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
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
	
    public static void main(String[] args) {
    	
    	LoadPathwayListFromFolder pathwayListLoader = new LoadPathwayListFromFolder();
    	
    	try
		{
			pathwayListLoader.run(INPUT_FOLDER_PATH_KEGG, INPUT_IMAGE_PATH_KEGG, OUTPUT_FILE_NAME_KEGG);
			pathwayListLoader.run(INPUT_FOLDER_PATH_BIOCARTA, INPUT_IMAGE_PATH_BIOCARTA, OUTPUT_FILE_NAME_BIOCARTA);
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
    }

}
