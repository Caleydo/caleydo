package org.caleydo.core.view.opengl.layout2.internal;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.apache.commons.lang.StringUtils;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

public final class SandBoxLibraryLoader {
	/**
	 * extract the given library of the classpath and put it to a temporary file
	 */
	public static File toTemporaryFile(String libName) throws IOException {
		// convert to native library name
		libName = System.mapLibraryName(libName);

		// create
		String extension = Files.getFileExtension(libName);
		File file = File.createTempFile(StringUtils.removeEnd(libName, extension), "." + extension);
		file.deleteOnExit();

		URL res = SandBoxLibraryLoader.class.getResource("/" + libName);
		if (res == null)
			throw new FileNotFoundException("can't extract: " + libName);
		try (InputStream in = res.openStream();
				OutputStream to = new BufferedOutputStream(new FileOutputStream(file))) {
			ByteStreams.copy(in, to);
		} catch (IOException e) {
			System.err.println("can't extract: " + libName);
			e.printStackTrace();
			throw new FileNotFoundException("can't extract: " + libName);
		}
		return file;
	}

	/**
	 * convention for custom library loader
	 */
	public static void loadLibrary(String libName) throws IOException {
		File file = toTemporaryFile(libName);
		// use System.load as it supports absolute file paths
		System.load(file.getAbsolutePath());
	}
}