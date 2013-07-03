/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.system;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Code from
 * http://stackoverflow.com/questions/2302233/download-file-by-passing-
 * url-using-java-code
 * 
 */
public class UrlDownload {
	final static int size = 1024;

	public static void fileUrl(String fAddress, String destinationFilePath) {
		OutputStream outStream = null;
		URLConnection uCon = null;

		InputStream is = null;
		try {
			URL Url;
			byte[] buf;
			int ByteRead, ByteWritten = 0;
			Url = new URL(fAddress);
			outStream = new BufferedOutputStream(new FileOutputStream(destinationFilePath));

			uCon = Url.openConnection();
			is = uCon.getInputStream();
			buf = new byte[size];
			while ((ByteRead = is.read(buf)) != -1) {
				outStream.write(buf, 0, ByteRead);
				ByteWritten += ByteRead;
			}
			System.out.println("File downloaded to:\"" + destinationFilePath);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				is.close();
				outStream.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
