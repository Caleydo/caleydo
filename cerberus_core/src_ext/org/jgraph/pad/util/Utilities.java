/*
 * Copyright (C) 2001-2004 Gaudenz Alder
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

package org.jgraph.pad.util;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.util.StringTokenizer;

/**
 * Utility methods. A utility method is characterized as a method which is of
 * general utility and is not specific to GPGraphpad or JGraph.
 * 
 * IMPORTANT NOTE: MORE UTILITIES CAN BE FOUND IN PREVIOUS VERSIONS OF JGRAPHPAD
 * (3 SERIES), BUT THEY HAVE BEEN REMOVED BECAUSE NOT USED.
 * 
 * For example, this would include things like generic sorting algorithms,
 * parsing routines, standard error handling methods, etc.
 * 
 * It is important that this code be optimized, and secondly you should be
 * concerned about not reinventing the wheel...before adding content here you
 * should try and find another open source project that already implements said
 * functionality in a robust manner. A good place to look is: Apache/Jakarta
 * Commons.
 * 
 * There are many methods commented out in this class as many of these methods
 * were imported from different projects but not yet currently used. Please take
 * a look here first to see if anything that you need has already been
 * implemented.
 */
public final class Utilities {

	/**
	 * The Utilities class should never be instantiated and should not have any
	 * state data associated with it, and this constructor enforces that.
	 */
	private Utilities() {
	}

	/**
	 * Take the given string and chop it up into a series of strings on
	 * whitespace boundries. This is useful for trying to get an array of
	 * strings out of the resource file.
	 */
	public static String[] tokenize(final String input) {
		return tokenize(input, " \t\n\r\f");
	}

	public static String[] tokenize(final String input, final String delim) {
		if (input == null)
			return new String[] {};
		StringTokenizer t = new StringTokenizer(input, delim);
		String cmd[];

		cmd = new String[t.countTokens()];
		int i = 0;
		while (t.hasMoreTokens()) {
			cmd[i] = t.nextToken();
			i++;
		}

		return cmd;
	}

	/**
	 * Returns a random number between 0 and max.
	 */
	public static int rnd(int max) {
		return (int) (Math.random() * max);
	}

	/**
	 * parses the pattern and tries to parse each token as a float.
	 * 
	 * @return array with the float value for each token
	 */
	public static float[] parsePattern(final String pattern) {
		StringTokenizer st = new StringTokenizer(pattern, ",");
		float[] f = new float[st.countTokens()];
		if (f.length > 0) {
			int i = 0;
			while (st.hasMoreTokens())
				f[i++] = Float.parseFloat(st.nextToken());
		}
		return f;
	}

	/**
	 * Returns the classname without the package. Example: If the input class is
	 * Stringt than the return value is String.
	 * 
	 * @param cl
	 *            The class to inspect
	 * @return The classname
	 * 
	 */
	public static String getClassNameWithoutPackage(Class cl) {
		// build the name for this action
		// without the package prefix
		String className = cl.getName();
		int pos = className.lastIndexOf('.') + 1;
		if (pos == -1)
			pos = 0;
		String name = className.substring(pos);
		return name;
	}

	public static void center(Window frame) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = frame.getSize();
		frame.setLocation(screenSize.width / 2 - (frameSize.width / 2),
				screenSize.height / 2 - (frameSize.height / 2));
	}
}
