package org.geneview.core.application.mapping;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;


public class MappingBuilder_BioCartaGeneID2Accession {
	
	private static String BIOCARTA_INPUT_FOLDER_PATH =
		"data/genome/pathway/biocarta/gene";
	
	private static String OUTPUT_FILE_PATH = 
		"data/genome/mapping/accession_code_2_biocarta_geneid.map";
	
	private PrintWriter outputWriter;	
	
	public MappingBuilder_BioCartaGeneID2Accession() 
	 	throws IOException {
		
		 outputWriter = new PrintWriter(
				 new BufferedWriter(new FileWriter(OUTPUT_FILE_PATH), 100000));
			//new PrintWriter(OUTPUT_FILE_PATH);
	}
	
	public void loadAllFilesInFolder(final String sFolderPath) {
	
		File folder = new File(sFolderPath);
	    File[] arFiles = folder.listFiles();

	    for (int iFileIndex = 0; iFileIndex < arFiles.length; iFileIndex++) 
	    {		
	    	searchForAccessionInFile(arFiles[iFileIndex]);
	    }
	}
	
	public void searchForAccessionInFile(final File file) {

		try
		{	
			FileInputStream fis = new FileInputStream(file);

			FileChannel fc = fis.getChannel();

			MappedByteBuffer mbf = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc
					.size());

			byte[] bArTmp = new byte[(int) (fc.size())];
			mbf.get(bArTmp);
			
			String sFileText = new String(bArTmp); // one big string
			
			// TODO: Search for more that the first occurrence
			int iStartIndex = sFileText.indexOf("NM_");
			
			if (iStartIndex == -1)
				return;
			
			String sAccessionNumber = sFileText.substring(iStartIndex, 
					sFileText.indexOf('"', iStartIndex));
			
			String sBioCartaGeneId = file.getName().substring(
					file.getName().lastIndexOf("BCID=") + 5, 
					file.getName().length());
			
			appendMappingToFile(sBioCartaGeneId, sAccessionNumber);
			
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void appendMappingToFile(final String sBioCartaGeneID, 
			final String sAccessionNumber) {
	
		outputWriter.println(sAccessionNumber + ";" + sBioCartaGeneID);
//		boolean err = outputWriter.checkError();
//		System.out.println(err);
		outputWriter.flush();
	}
	
    public static void main(String[] args) {
    	
    	try {
    		
        	MappingBuilder_BioCartaGeneID2Accession mappingBuilder = 
        		new MappingBuilder_BioCartaGeneID2Accession();
        	
        	mappingBuilder.loadAllFilesInFolder(BIOCARTA_INPUT_FOLDER_PATH);
    		
		} catch (Exception e)
		{
			e.printStackTrace();
		}    	
    }
}
