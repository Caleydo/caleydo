/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.data.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * factory class for different ways to locate resources
 *
 * @author Samuel Gratzl
 *
 */
public class ResourceLocators {
	public interface IResourceLocator {
		InputStream get(String res);
	}

	public static final IResourceLocator FILE =  new IResourceLocator() {
		@Override
		public InputStream get(String res) {
			File f = new File(res);
			if (f.exists())
				try {
					return new FileInputStream(f);
				} catch (FileNotFoundException e) {
					// can't happen
				}
			return null;
		}

		@Override
		public String toString() {
			return "FILE";
		}
	};

	/**
	 * class loader resource locator based on the data class loader
	 */
	public static final IResourceLocator DATA_CLASSLOADER = classLoader(ResourceLocators.class.getClassLoader());

	/**
	 * class loader for loading resources using a class loader instance
	 *
	 * @param loader
	 * @return
	 */
	public static IResourceLocator classLoader(final ClassLoader loader) {
		return new IResourceLocator() {
			@Override
			public InputStream get(String res) {
				return loader.getResourceAsStream(res);
			}

			@Override
			public String toString() {
				return "CLASSLOADER: " + loader;
			}
		};
	}

	/**
	 * combines the given {@link IResourceLocator}s in a first found first returned style
	 *
	 * @param childs
	 * @return
	 */
	public static IResourceLocator chain(final IResourceLocator... childs) {
		if (childs.length == 1)
			return childs[0];
		return new IResourceLocator() {
			@Override
			public InputStream get(String res) {
				for (IResourceLocator c : childs) {
					InputStream in = c.get(res);
					if (in != null)
						return in;
				}
				return null;
			}

			@Override
			public String toString() {
				return "CHAIN: " + Arrays.toString(childs);
			}
		};
	}
}
