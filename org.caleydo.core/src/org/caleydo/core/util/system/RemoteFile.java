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
package org.caleydo.core.util.system;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * helper class for handling remote files and local caches of them
 *
 * @author Samuel Gratzl
 *
 */
public final class RemoteFile implements IRunnableWithProgress {
	private static final Logger log = Logger.create(RemoteFile.class);

	private static final int BUFFER_SIZE = 4096;
	private static final int WORK_TRIGGER_FREQUENCY = 64;

	private static final int CONNECT_TIMEOUT = 10 * 1000; // [ms]

	private final URL url;
	private final File file;

	private boolean successful = true;
	private Exception caught = null;

	private RemoteFile(URL url) {
		this.url = url;
		File f = RemoteFileCache.inCache(url);
		if (f == null)
			f = RemoteFileCache.reserve(url);
		this.file = f;
	}

	/**
	 * factory for creating a {@link RemoteFile}
	 *
	 * @param url
	 * @return
	 */
	public static RemoteFile of(URL url) {
		return new RemoteFile(url);
	}

	/**
	 * checks whether the file is already in the cache
	 *
	 * @param checkModificationDate
	 *            check also whether the remote and local modification date matches
	 * @return
	 */
	public boolean inCache(boolean checkModificationDate) {
		if (!file.exists())
			return false;
		if (!checkModificationDate)
			return true;
		long have = file.lastModified();
		URLConnection connection;
		try {
			connection = url.openConnection();
			connection.setConnectTimeout(CONNECT_TIMEOUT);
			connection.connect();

			long expected = connection.getLastModified();
			return expected == have;
		} catch (IOException e) {
			log.warn("can't check modification date of: " + url, e);
			return true; // assume correct as we can't verify it
		}
	}

	public boolean delete() {
		if (file.exists())
			return file.delete();
		return true;
	}

	/**
	 * @return the file, see {@link #file}
	 */
	public File getFile() {
		return file;
	}

	/**
	 * the exception caught during {@link #run(IProgressMonitor)} or null if none occurred
	 *
	 * @return the caught, see {@link #caught}
	 */
	public Exception getCaught() {
		return caught;
	}

	public File getOrLoad(boolean checkModificationDate, IProgressMonitor monitor) {
		if (!inCache(checkModificationDate)) {
			delete();
			run(monitor);
			if (!file.exists())
				return null;
			return file;
		}
		return file;
	}

	@Override
	public void run(IProgressMonitor monitor) {
		if (inCache(false)) {
			monitor.done();
			return;
		}
		successful = false;
		caught = null;
		File tmp = new File(file.getAbsolutePath() + "-tmp");
		tmp.getParentFile().mkdirs();
		long lastModified = 0;
		try (OutputStream out = new BufferedOutputStream(new FileOutputStream(tmp))) {
			URLConnection connection = url.openConnection();
			connection.connect();

			int length = connection.getContentLength();
			lastModified = connection.getLastModified();
			if (length < 0)
				length = IProgressMonitor.UNKNOWN;
			monitor.beginTask(String.format("Downloading: %s (%d MB)", url, length / 1024 / 1024), length);


			try (InputStream in = new BufferedInputStream(connection.getInputStream())) {
				byte[] data = new byte[BUFFER_SIZE];
				int count = 0;
				int acc = 0;

				int i = 0;
				while ((count = in.read(data)) != -1) {
					acc += count;
					out.write(data, 0, count);
					if (i++ >= WORK_TRIGGER_FREQUENCY) {
						i -= WORK_TRIGGER_FREQUENCY;
						if (monitor.isCanceled()) {
							break;
						}
						monitor.worked(acc);
						acc = 0;
					}
				}
			}
			if (!monitor.isCanceled()) {
				monitor.done();
				successful = true;
			}
		} catch (IOException e) {
			log.error("can't download file: " + url, e);
			caught = e;
		}
		if (successful) {
			try {
				Files.move(tmp.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
				if (lastModified != 0)
					file.setLastModified(lastModified);
				RemoteFileCache.putToCache(url, file);
			} catch (IOException e) {
				log.error("can't move file: " + url, e);
				caught = e;
			}
		} else {
			tmp.delete();
		}
	}

	public static void main(String[] args) throws MalformedURLException, URISyntaxException {
		File f = new File(RemoteFileCache.cacheDir, "asdfas/tests.txt");
		System.out.println(f.getAbsolutePath());
		System.out.println(f.getName());
		dump("http://data.icg.tugraz.at/caleydo/ download/2.2/caleydo_2.2_linux_x86-64.deb");
		dump("http://data.icg.tugraz.at/caleydo/download/2.2/tmp.pnp?adsfa&bsbad");
	}

	/**
	 * @param string
	 * @throws MalformedURLException
	 * @throws URISyntaxException
	 */
	private static void dump(String string) throws MalformedURLException, URISyntaxException {
		URL url = new URL(string);
		System.out.println("ext:" + url.toExternalForm());
		System.out.println("host: " + url.getHost());
		System.out.println("path: " + url.getPath());

		System.out.println("file: " + url.getFile());
	}

	private static final class RemoteFileCache {
		private static final File cacheDir = new File(GeneralManager.CALEYDO_HOME_PATH, "cache");
		private static final File cacheContentsFile = new File(cacheDir, "contents.properties");
		private static final BiMap<String, String> cacheContents = HashBiMap.create();

		static {
			if (cacheContentsFile.exists()) {
				try {
					for (String line : Files.readAllLines(cacheContentsFile.toPath(), Charset.defaultCharset())) {
						if (line.isEmpty())
							continue;
						String[] url_path = line.split("\t");
						cacheContents.put(url_path[0], url_path[1]);
					}
				} catch (IOException e) {
					log.error("can't read " + cacheContentsFile, e);
				}
			}
		}

		static synchronized void putToCache(URL url, File file) {
			String relative = toRelative(file);
			cacheContents.put(url.toExternalForm(), relative);
			try (FileWriter out = new FileWriter(cacheContentsFile, true)) {
				out.write(url.toExternalForm());
				out.write('\t');
				out.write(relative);
				out.write('\n');
			} catch (IOException e) {
				log.error("can't write " + cacheContentsFile, e);
			}
		}

		private static String toRelative(File file) {
			Path relative = cacheDir.toPath().relativize(file.toPath());
			return relative.toString();
		}

		static synchronized File reserve(URL url) {
			String path = url.getPath();
			int i = path.lastIndexOf('.');
			String suffix = "";
			if (i > 0) {
				suffix = path.substring(i);
				path = path.substring(0, i);
			}
			try {// try whether we can use the path as a file name path
				new File(path).getCanonicalPath();
			} catch (IOException e) {
				path = "unparseable";
			}

			String key = toUnique(path, suffix);
			cacheContents.put(url.toExternalForm(), key);
			return new File(cacheDir, key);
		}

		/**
		 * converts the given path with the given suffix to a unique derivat that doesn't yet exist
		 *
		 * @param local
		 * @return
		 */
		private static String toUnique(String path, String suffix) {
			Set<String> values = cacheContents.values();
			String test = (path + suffix).toLowerCase();
			if (!values.contains(test) && !cacheContentsFile.getName().equalsIgnoreCase(suffix))
				return path + suffix;
			int next = 0;
			do {
				test = (path + (next++) + suffix).toLowerCase();
			} while (values.contains(test) || cacheContentsFile.getName().equalsIgnoreCase(suffix));
			return test;
		}

		static synchronized File inCache(URL url) {
			String key = url.toExternalForm();
			if (cacheContents.containsKey(key)) {
				String s = cacheContents.get(key);
				return new File(cacheDir, s);
			}
			return null;
		}
	}
}
