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
package org.caleydo.core.serialize;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.caleydo.core.manager.GeneralManager;

/**
 * Utility class for zipping and deleting directories.
 * 
 * @author Werner Puff
 * @author Marc Streit
 */
public class ZipUtils {

	/**
	 * Zips a directory into a zip-archive
	 * 
	 * @param dirName
	 *            directory to zip
	 * @param zipFileName
	 *            name of the resulting zip-file
	 */
	public void zipDirectory(String dirName, String zipFileName) {
		try {
			ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFileName));
			zipDirectory(dirName, zos);
			zos.close();
		}
		catch (Exception ex) {
			throw new RuntimeException("Error saving project files (zip)", ex);
		}
	}

	/**
	 * Browses through a given directory and writes the files to a given {@link ZipOutputStream}
	 * 
	 * @param dir2zip
	 *            directory to zip
	 * @param zos
	 *            stream to write to
	 */
	public void zipDirectory(String dir2zip, ZipOutputStream zos) {
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
					// FIXME files in sub-directories loose their relative-path in the zip file
					String filePath = f.getPath();
					zipDirectory(filePath, zos);
				}
				else {
					// if we reached here, the File object f was not a directory
					// create a FileInputStream on top of f
					FileInputStream fis = new FileInputStream(f);
					// create a new zip entry
					ZipEntry anEntry = new ZipEntry(f.getName());
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
		}
		catch (Exception ex) {
			throw new RuntimeException("Error saving project files (zip)", ex);
		}
	}

	/**
	 * Extracts a zip file to a directory
	 * 
	 * @param fileName
	 *            file to unzip
	 * @param dirName
	 *            directory to store the unzipped files
	 */
	public void unzipToDirectory(String fileName, String dirName) {
		if (dirName.charAt(dirName.length() - 1) != File.separatorChar) {
			dirName += File.separator;
		}

		File tempDirFile = new File(dirName);
		tempDirFile.mkdir();

		try {
			ZipInputStream zis =
				new ZipInputStream(GeneralManager.get().getResourceLoader().getInputSource(fileName)
					.getByteStream());
			unzipToDirectory(zis, dirName);
		}
		catch (Exception ex) {
			throw new RuntimeException("Could not unzip file '" + fileName + "' to '" + dirName + "'", ex);
		}
	}

	/**
	 * Extracts a zip file to a directory
	 * 
	 * @param zis
	 *            Stream to read the zip entries from
	 * @param dirName
	 *            directory to store the unzipped files
	 */
	public void unzipToDirectory(ZipInputStream zis, String dirName) {
		try {
			byte[] readBuffer = new byte[2156];
			int bytesIn = 0;

			ZipEntry entry = zis.getNextEntry();

			while (entry != null) {
				FileOutputStream fos = new FileOutputStream(dirName + entry.getName());

				while ((bytesIn = zis.read(readBuffer)) != -1) {
					fos.write(readBuffer, 0, bytesIn);
				}
				fos.close();

				entry = zis.getNextEntry();
			}
		}
		catch (Exception ex) {
			throw new RuntimeException("Error saving project files (zip)", ex);
		}
	}
}
