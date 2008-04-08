/*
 * @(#)ImageLoader.java	1.0 23/01/02
 *
 * 6/01/2006: I, Raphpael Valyi, changed back the header of this file to LGPL
 * because nobody changed the file significantly since the last
 * 3.0 version of GPGraphpad that was LGPL. By significantly, I mean: 
 *  - less than 3 instructions changes could honnestly have been done from an old fork,
 *  - license or copyright changes in the header don't count
 *  - automaticaly updating imports don't count,
 *  - updating systematically 2 instructions to a library specification update don't count.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package org.jgraph.pad.resources;

import java.awt.Image;
import java.net.URL;
import java.util.Stack;

import javax.swing.ImageIcon;

/**
 * Loader for the resource images. The class uses the getResource Method to get
 * the Resource from the relative path.
 */
public final class ImageLoader {

	/**
	 * contains string objects which respresents the search paths
	 */
	private static Stack searchPath = new Stack();

	/**
	 * Returns an Image from the same path.
	 * 
	 * @param imageName
	 *            An image name with the file extension buttonEdge.g. Can.gif
	 */
	public static Image getImage(final String imageName) {
		return getImageIcon(imageName).getImage();
	}

	/**
	 * Returns an ImageIcon from the same path.
	 * 
	 * @param imageName
	 *            An image name with the file extension buttonEdge.g. Can.gif
	 */
	public static ImageIcon getImageIcon(final String imageName) {
		return getImageIcon(searchPath.size() - 1, imageName);
	}

	/**
	 * Returns an ImageIcon from the same path.
	 * 
	 * @param imageName
	 *            An image name with the file extension buttonEdge.g. Can.gif
	 */
	public static ImageIcon getImageIcon(final int searchPathIndex, final String imageName) {
		// precondition test
		if (imageName == null)
			return null;

		// image loading
		if (searchPathIndex < searchPath.size() && searchPathIndex >= 0) {
			final URL url = ImageLoader.class.getResource(((String) searchPath
					.get(searchPathIndex))
					+ imageName);
			if (url != null) {
				return new ImageIcon(url);
			}
			return getImageIcon(searchPathIndex - 1, imageName);
		}
		return null;

	}

	/**
	 * pushes the specified path to the search path
	 * 
	 * An example for a search path file name is 'com/jgraph/pad/resources'.
	 * 
	 */
	public static void pushSearchPath(String path) {
		if (path == null)
			return;

		if (!path.endsWith("/")) {
			path = path + "/";
		}

		searchPath.push(path);
	}

	/**
	 * removes the searchpath at the specified index
	 */
	public static void removeSearchPath(final int index) {
		searchPath.remove(index);
	}

	/**
	 * pops the highest search path
	 */
	public static void popSearchPath() {
		searchPath.pop();
	}
}
