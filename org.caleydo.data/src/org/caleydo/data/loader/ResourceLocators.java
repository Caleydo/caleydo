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
package org.caleydo.data.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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

	public static final IResourceLocator URL = new IResourceLocator() {

		@Override
		public InputStream get(String res) {
			URL url;
			try {
				url = new URL(res);
				return url.openStream();
			} catch (IOException e) {
				return null;
			}
		}

		@Override
		public String toString() {
			return "URL";
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