package org.caleydo.core.util.save;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.export.SetExporter;
import org.caleydo.core.data.collection.export.SetExporter.EWhichViewToExport;
import org.caleydo.core.manager.general.GeneralManager;

/**
 * @author Alexander Lex
 */
public class ProjectSaver {

	public void save(String fileName) {
		String tempDirectory = GeneralManager.CALEYDO_HOME_PATH + "export";
		String exportedData = tempDirectory + "/data.csv";
		String geneTreePath = tempDirectory + "/bgene_tree.xml";

		File tempDirFile = new File(tempDirectory);
		tempDirFile.mkdir();
		
		
		ISet set = GeneralManager.get().getUseCase().getSet();

		SetExporter exporter = new SetExporter();
		exporter.export(set, exportedData, EWhichViewToExport.WHOLE_DATA);

		exporter.exportTrees(set, tempDirectory);

		try 
		{ 
		    //create a ZipOutputStream to zip the data to 
		    ZipOutputStream zos = new 
		           ZipOutputStream(new FileOutputStream(fileName)); 
		    //assuming that there is a directory named inFolder (If there 
		    //isn't create one) in the same directory as the one the code 		    runs from, 
		    //call the zipDir method 
		    zipDir(tempDirectory, zos); 
		    //close the stream 
		    zos.close(); 
		} 
		catch(Exception e) 
		{ 
		    //handle exception 
		} 
		
//		deleteDir(tempDirFile);

	}

	public void zipDir(String dir2zip, ZipOutputStream zos) {
		try {
			// create a new File object based on the directory we have to zip
			File zipDir = new File(dir2zip);
			// get a listing of the directory content
			String[] dirList = zipDir.list();
			byte[] readBuffer = new byte[2156];
			int bytesIn = 0;
			// loop through dirList, and zip the files
			for (int i = 0; i < dirList.length; i++) {
				File f = new File(zipDir, dirList[i]);
				if (f.isDirectory()) {
					// if the File object is a directory, call this
					// function again to add its content recursively
					String filePath = f.getPath();
					zipDir(filePath, zos);
					// loop again
					continue;
				}
				// if we reached here, the File object f was not a directory
				// create a FileInputStream on top of f
				FileInputStream fis = new FileInputStream(f);
				// create a new zip entry
				ZipEntry anEntry = new ZipEntry(f.getPath());
				// place the zip entry in the ZipOutputStream object
				zos.putNextEntry(anEntry);
				// now write the content of the file to the ZipOutputStream
				while ((bytesIn = fis.read(readBuffer)) != -1) {
					zos.write(readBuffer, 0, bytesIn);
				}
				// close the Stream
				fis.close();
			}
		}
		catch (Exception e) {
			// handle exception
		}
	}
	
	 public boolean deleteDir(File dir) {
	        if (dir.isDirectory()) {
	            String[] children = dir.list();
	            for (int i=0; i<children.length; i++) {
	                boolean success = deleteDir(new File(dir, children[i]));
	                if (!success) {
	                    return false;
	                }
	            }
	        }
	    
	        // The directory is now empty so delete it
	        return dir.delete();
	    }

}
