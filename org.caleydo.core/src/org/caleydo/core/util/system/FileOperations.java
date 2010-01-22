package org.caleydo.core.util.system;

import java.io.File;

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
}
