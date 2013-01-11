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
	};

	/**
	 * class loader resource locator based on the data class loader
	 */
	public static final IResourceLocator DATA_CLASSLOADER = classLoader(ResourceLocators.class.getClassLoader());

	public static IResourceLocator classLoader(final ClassLoader loader) {
		return new IResourceLocator() {
			@Override
			public InputStream get(String res) {
				return loader.getResourceAsStream(res);
			}
		};
	}

	public static IResourceLocator chain(final IResourceLocator... childs) {
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
		};
	}
}