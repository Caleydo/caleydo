/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

/**
 * Utility class for file operations.
 *
 * @author Christian
 *
 */
public final class FileUtil {

	public static void transposeCSV(String fileName, String fileNameOut, String delimiter) {
		// log.info("tranposing: " + fileName);
		File in = new File(fileName);
		File out = new File(fileNameOut);

		// if (out.exists() && !settings.isCleanCache())
		// return;

		List<String> data;
		try {
			data = Files.readAllLines(in.toPath(), Charset.forName("UTF-8"));
		} catch (IOException e2) {
			e2.printStackTrace();
			return;
		}
		// split into parts
		String[][] parts = new String[data.size()][];
		int maxCol = -1;
		for (int i = 0; i < data.size(); ++i) {
			parts[i] = data.get(i).split(delimiter, -1);
			if (parts[i].length > maxCol)
				maxCol = parts[i].length;
		}
		data = null;

		try (BufferedWriter writer = Files.newBufferedWriter(out.toPath(), Charset.forName("UTF-8"))) {
			for (int c = 0; c < maxCol; ++c) {
				for (int i = 0; i < parts.length; ++i) {
					if (i > 0)
						writer.append(delimiter);
					String[] p = parts[i];
					if (p.length > c)
						writer.append(p[c]);
				}
				writer.newLine();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	/**
	 * Gets the name of the file from a complete file path.
	 *
	 * @param path
	 * @return
	 */
	public static String exctractFileName(String path) {
		// new File(path).getName()
		if (path == null)
			return null;
		int lastSeparatorIndex = path.lastIndexOf("/");
		if (lastSeparatorIndex == -1) {
			lastSeparatorIndex = path.lastIndexOf("\\");
		}
		if (lastSeparatorIndex == -1 || lastSeparatorIndex == path.length() - 1)
			return "";
		return path.substring(lastSeparatorIndex + 1);
	}

}
