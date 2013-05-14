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
package org.caleydo.core.view.opengl.util.text;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Samuel Gratzl
 *
 */
public class TextUtils {
	public static List<String> wrap(ITextRenderer renderer, String text, float width, float lineHeight) {
		if (renderer instanceof IWrappingTextRenderer) {
			return ((IWrappingTextRenderer) renderer).wrap(text, width, lineHeight);
		}

		String[] lines = text.split("\n");

		List<String> r = new ArrayList<>();
		for (String line : lines) {
			float lineWidth = renderer.getTextWidth(line, lineHeight);
			if (lineWidth < width) {
				r.add(line);
				continue;
			}
			//need to split the line
			int from = 0;
			int prev = 0;
			int to = line.indexOf(' ', from + 1);
			while (to > 0) {
				if (to > line.length())
					System.err.println();
				lineWidth = renderer.getTextWidth(line.substring(from, to), lineHeight);
				if (lineWidth > width && prev > 0) {
					// can't add the word to the line, so newline before the word
					String subline = line.substring(from, prev);
					r.add(subline);
					from = prev + 1;
				}
				prev = to;
				to = line.indexOf(' ', prev + 1);
				if (to < 0 && prev < line.length())
					to = line.length();
			}
			r.add(line.substring(from)); // add rest and stop
		}
		return r;
	}
}
