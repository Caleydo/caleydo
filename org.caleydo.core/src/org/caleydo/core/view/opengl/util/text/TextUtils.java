/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
