/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.system;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class FileOperations {

	public static void writeInputStreamToFile(String fileName, BufferedReader bufferedReader) {
		try {
			FileWriter fstream = new FileWriter(fileName);
			BufferedWriter out = new BufferedWriter(fstream);

			char buf[] = new char[1024];
			int len;
			while ((len = bufferedReader.read(buf)) > 0)
				out.write(buf, 0, len);

			out.close();
			bufferedReader.close();
		}
		catch (IOException e) {
		}
	}

	/**
	 * Renames a given folder.
	 * 
	 * @param fromDir source directory
	 * @param toDir target directory
	 */
	public static void renameDirectory(String fromDir, String toDir) {

		File from = new File(fromDir);

		if (!from.exists() || !from.isDirectory()) {
			throw new RuntimeException("Directory does not exist: " + fromDir);
		}

		File to = new File(toDir);
		if (to.exists())
			FileOperations.deleteDirectory(to);

		// Rename
		if (!from.renameTo(to))
			throw new RuntimeException("Error moving folder: " + fromDir);
	}

	/**
	 * Deletes the directory with the given name
	 * 
	 * @param dirName directory name to delete
	 * @return <code>true</code> if the directory was deleted,
	 *         <code>false</code> otherwise
	 */
	public static boolean deleteDirectory(String dirName) {
		File directory = new File(dirName);
		return deleteDirectory(directory);
	}

	/**
	 * Deletes the given directory
	 * 
	 * @param directory directory to delete
	 * @return <code>true</code> if the directory was deleted,
	 *         <code>false</code> otherwise
	 */
	public static boolean deleteDirectory(File directory) {
		if (directory.isDirectory()) {
			String[] children = directory.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDirectory(new File(directory, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return directory.delete();
	}

	public static void createDirectory(String dirName) {
		if (dirName.charAt(dirName.length() - 1) != File.separatorChar) {
			dirName += File.separator;
		}

		if (!(new File(dirName)).exists()) {
			if (!(new File(dirName)).mkdirs()) {
				// Directory creation failed
				throw new RuntimeException("Unable to create directory " + dirName);
			}
		}
	}

	public static void copyFolder(File src, File dest) throws IOException {

		if (src.isDirectory()) {

			// if directory not exists, create it
			if (!dest.exists()) {
				dest.mkdir();
				System.out.println("Directory copied from " + src + "  to " + dest);
			}

			// list all the directory contents
			String files[] = src.list();

			for (String file : files) {
				// construct the src and dest file structure
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				// recursive copy
				copyFolder(srcFile, destFile);
			}

		}
		else {
			// if file, then copy it
			// Use bytes stream to support all file types
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);

			byte[] buffer = new byte[1024];

			int length;
			// copy the file content in bytes
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}

			in.close();
			out.close();

			Logger.log(new Status(IStatus.INFO, GeneralManager.PLUGIN_ID, "File copied from "
					+ src + " to " + dest));
		}
	}
}
