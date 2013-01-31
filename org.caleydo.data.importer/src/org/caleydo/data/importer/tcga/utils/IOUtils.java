package org.caleydo.data.importer.tcga.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

public class IOUtils {
	public static String readAll(InputStream in) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringWriter b = new StringWriter();
		PrintWriter out = new PrintWriter(b);
		String line = null;
		while ((line = reader.readLine()) != null)
			out.println(line);
		out.close();
		return b.toString();
	}
}
