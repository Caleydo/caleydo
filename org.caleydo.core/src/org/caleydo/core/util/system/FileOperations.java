package org.caleydo.core.util.system;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileOperations {

	/**
	 * Deletes all files and subdirectories under dir. Returns true if all deletions were successful. If a
	 * deletion fails, the method stops attempting to delete and returns false.
	 */
	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (String element : children) {
				boolean success = deleteDir(new File(dir, element));
				if (!success)
					return false;
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}

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
}
