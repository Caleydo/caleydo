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
package org.caleydo.data.importer.tcga.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;
import org.caleydo.core.util.system.FileOperations;

public class ArchiveExtractionUtils {

	public static String extractFileFromTarGzArchive(String archiveName, String fileToExtract,
			String outputDirectoryName, String remoteArchiveDirectory) {
		
		String outputFileName = null;
		
		// Do not download the archive if the target file from the TAR already exists locally
		if (new File(outputDirectoryName + fileToExtract).exists())
			return outputDirectoryName + fileToExtract;
		
		try {
			byte[] buf = new byte[1024];
			TarInputStream tarInputStream = null;
			TarEntry tarEntry;

			tarInputStream = new TarInputStream(new GZIPInputStream(new URL(
					remoteArchiveDirectory + archiveName).openStream()));

			tarEntry = tarInputStream.getNextEntry();
			while (tarEntry != null) {
				// for each entry to be extracted
				String entryName = tarEntry.getName();

				// only continue if the this entry is the one we need to extract
				if (!entryName.endsWith(fileToExtract)) {
					tarEntry = tarInputStream.getNextEntry();
					continue;
				}

				int n;
				FileOutputStream fileoutputstream;
				File newFile = new File(entryName);
				String directory = newFile.getParent();

				if (directory == null) {
					if (newFile.isDirectory())
						break;
				}

				FileOperations.createDirectory(outputDirectoryName);
				outputFileName = outputDirectoryName + fileToExtract;
				fileoutputstream = new FileOutputStream(outputFileName);

				while ((n = tarInputStream.read(buf, 0, 1024)) > -1)
					fileoutputstream.write(buf, 0, n);

				fileoutputstream.close();
				tarInputStream.close();

				break;
			}// while

		}
		catch (Exception e) {
			throw new RuntimeException("Unable to extract " + fileToExtract + " from "
					+ archiveName + ".");
		}

		if (outputFileName == null) {
			throw new RuntimeException("File " + fileToExtract + " not found in "
					+ archiveName + ".");
		}

		return outputFileName;
	}
}
