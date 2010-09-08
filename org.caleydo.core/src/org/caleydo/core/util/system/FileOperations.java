package org.caleydo.core.util.system;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
	 * Deletes the directory with the given name
	 * 
	 * @param dirName
	 *            directory name to delete
	 * @return <code>true</code> if the directory was deleted, <code>false</code> otherwise
	 */
	public static boolean deleteDirectory(String dirName) {
		File directory = new File(dirName);
		return deleteDirectory(directory);
	}

	/**
	 * Renames a given folder.
	 * 
	 * @param fromDir
	 * 			source directory
	 * @param toDir
	 * 			target directory
	 */
	public static void renameDirectory(String fromDir, String toDir) {

		File from = new File(fromDir);

		if (!from.exists() || !from.isDirectory()) {
			throw new RuntimeException("Directory does not exist: " +fromDir);
		}

		File to = new File(toDir);
		if (to.exists())
			FileOperations.deleteDirectory(to);
		
		// Rename
		if (!from.renameTo(to))
			throw new RuntimeException("Error moving folder: " +fromDir);
	}

	/**
	 * Deletes the given directory
	 * 
	 * @param directory
	 *            directory to delete
	 * @return <code>true</code> if the directory was deleted, <code>false</code> otherwise
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

		File tempDirFile = new File(dirName);
		tempDirFile.mkdir();
	}
}
