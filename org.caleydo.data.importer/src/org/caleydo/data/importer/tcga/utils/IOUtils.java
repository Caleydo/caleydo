package org.caleydo.data.importer.tcga.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class IOUtils {
	public final static boolean dumpToFile(CharSequence data, File file) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			writer.append(data);
			writer.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String readAll(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringWriter b = new StringWriter();
		PrintWriter out = new PrintWriter(b);
		String line = null;
		while ((line = reader.readLine()) != null)
			out.println(line);
		out.close();
		return b.toString();
	}
}
