/*******************************************************************************
 * Copyright (c) 2008, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Ferenc Hechler - initial API and implementation
 *     Ferenc Hechler, ferenc_hechler@users.sourceforge.net - 219530 [jar application] add Jar-in-Jar ClassLoader option
 *     Ferenc Hechler, ferenc_hechler@users.sourceforge.net - 262746 [jar exporter] Create a builder for jar-in-jar-loader.zip
 *     Ferenc Hechler, ferenc_hechler@users.sourceforge.net - 262748 [jar exporter] extract constants for string literals in JarRsrcLoader et al.
 *******************************************************************************/


import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * adapted and enhanced version of the eclipse jdt jar in jar launcher for simple platform checks for custom classpath
 * entries
 *
 * notation:
 *
 * <pre>
 * Rsrc - Class - Path[-(win | mac | linux)][-(x86 | amd64)]
 * </pre>
 *
 * and
 *
 * <pre>
 * RsrcMainClass
 * </pre>
 *
 * as system property to override the main class
 *
 * @since 3.5
 */
public class JarInJarLauncher {
	static final String REDIRECTED_CLASS_PATH_MANIFEST_NAME = "Rsrc-Class-Path"; //$NON-NLS-1$
	static final String REDIRECTED_MAIN_CLASS_MANIFEST_NAME = "Rsrc-Main-Class"; //$NON-NLS-1$

	private static final String OS;
	private static final String OS_ARCH = System.getProperty("os.arch").toLowerCase();

	static {
		String os_name = System.getProperty("os.name").toLowerCase();
		String path_sep = System.getProperty("path.separator");

		if (os_name.indexOf("windows") > -1)
			OS = "win";
		else if (os_name.indexOf("mac") > -1 || os_name.indexOf("darwin") > -1)
			OS = "mac";
		else if (path_sep.equals(":") && os_name.indexOf("openvms") <= -1
				&& (os_name.endsWith("x") || os_name.indexOf("darwin") > -1))
			OS = "linux";
		else
			OS = "unknown";
	}

	private static class ManifestInfo {
		String rsrcMainClass;
		List<String> rsrcClassPath = new ArrayList<>();
	}

	public static void main(String[] args) throws Exception {
		ManifestInfo mi = getManifestInfo();
		String rsrcMainClass = mi.rsrcMainClass;
		rsrcMainClass = System.getProperty("RsrcMainClass", rsrcMainClass);
		List<String> rsrcClassPath = mi.rsrcClassPath;

		changeClassPaths(args, rsrcMainClass, rsrcClassPath);
	}

	private static ManifestInfo getManifestInfo() throws IOException {
		Enumeration<URL> resEnum = Thread.currentThread().getContextClassLoader().getResources(JarFile.MANIFEST_NAME);
		while (resEnum.hasMoreElements()) {
			try (InputStream is = resEnum.nextElement().openStream()) {
				if (is != null) {
					ManifestInfo result = new ManifestInfo();
					Manifest manifest = new Manifest(is);
					Attributes mainAttribs = manifest.getMainAttributes();
					result.rsrcMainClass = mainAttribs.getValue(REDIRECTED_MAIN_CLASS_MANIFEST_NAME);

					for (Object key : mainAttribs.keySet()) {
						if (key.toString().startsWith(REDIRECTED_CLASS_PATH_MANIFEST_NAME))
							addPlatformEntries(result.rsrcClassPath, key.toString(), (String) mainAttribs.get(key));
					}
					if ((result.rsrcMainClass != null) && !result.rsrcMainClass.trim().equals(""))    //$NON-NLS-1$
						return result;
				}
			} catch (Exception e) {
				// Silently ignore wrong manifests on classpath?
			}
		}
		System.err
				.println("Missing attributes for JarRsrcLoader in Manifest (" + REDIRECTED_MAIN_CLASS_MANIFEST_NAME + ", " + REDIRECTED_CLASS_PATH_MANIFEST_NAME + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return null;
	}

	private static void addPlatformEntries(List<String> r, String key, String value) {
		key = key.substring(REDIRECTED_CLASS_PATH_MANIFEST_NAME.length()).trim().toLowerCase();
		if (matches(key)) {
			r.addAll(Arrays.asList(value.split(" ")));
		}
	}

	private static boolean matches(String key) {
		if (key.isEmpty())
			return true;
		return key.equals("-" + OS + "-" + OS_ARCH) || key.equals("-" + OS) || key.equals("-" + OS_ARCH);
	}

	private static void changeClassPaths(String[] args, String rsrcMainClass, List<String> rsrcClassPath) throws Exception {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		Class<? extends URLStreamHandlerFactory> factory = Class.forName(
				"org.eclipse.jdt.internal.jarinjarloader.RsrcURLStreamHandlerFactory").asSubclass(
				URLStreamHandlerFactory.class);
		URL.setURLStreamHandlerFactory(factory.getConstructor(ClassLoader.class).newInstance(cl));

		Class<?> jijConstants = Class.forName("org.eclipse.jdt.internal.jarinjarloader.JIJConstants");
		String pathSeparator = getValue(jijConstants.getDeclaredField("PATH_SEPARATOR"));
		String internalUrl = getValue(jijConstants.getDeclaredField("INTERNAL_URL_PROTOCOL_WITH_COLON"));
		String jarInternalUrl = getValue(jijConstants.getDeclaredField("JAR_INTERNAL_URL_PROTOCOL_WITH_COLON"));
		String sep = getValue(jijConstants.getDeclaredField("JAR_INTERNAL_SEPARATOR"));

		URL[] rsrcUrls = new URL[rsrcClassPath.size()];
		for (int i = 0; i < rsrcClassPath.size(); i++) {
			String rsrcPath = rsrcClassPath.get(i);
			if (rsrcPath.endsWith(pathSeparator))
				rsrcUrls[i] = new URL(internalUrl + rsrcPath);
			else
				rsrcUrls[i] = new URL(jarInternalUrl + rsrcPath + sep);
		}

		try (URLClassLoader jceClassLoader = new URLClassLoader(rsrcUrls, null)) {
			Thread.currentThread().setContextClassLoader(jceClassLoader);
			Class<?> c = Class.forName(rsrcMainClass, true, jceClassLoader);
			Method main = c.getMethod("main", args.getClass());
			main.invoke(null, new Object[] { args });
		}
	}

	/**
	 * @param declaredField
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	private static String getValue(Field declaredField) throws IllegalArgumentException, IllegalAccessException {
		declaredField.setAccessible(true);
		return declaredField.get(null).toString();
	}
}
