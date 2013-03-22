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
package org.caleydo.vis.rank.model;

import static org.caleydo.vis.rank.ui.RenderStyle.binsForWidth;

import org.caleydo.vis.rank.data.IFloatFunction;

import com.google.common.base.Stopwatch;

/**
 * wrapper for a {@link SimpleHistogram} with caching strategies
 *
 * @author Samuel Gratzl
 *
 */
public class HistCache {
	private SimpleHistogram hist = null;
	private float computedWidth;

	/**
	 * returns a histogram for the given data, maybe cached
	 *
	 * @param width
	 *            the target pixel width to render
	 * @param it
	 *            the data
	 * @param map
	 *            the mapping function to convert the data into floats
	 * @return its histogram
	 */
	public SimpleHistogram get(float width, Iterable<IRow> it, IFloatFunction<IRow> map) {
		Stopwatch w = new Stopwatch();
		int bins;
		if (hist != null) {
			if (Math.abs(computedWidth - width) < 3) // should get the same histogram
				return hist;
			bins = binsForWidth(width);
			if (bins >= 0 && hist.size() == bins) {// fast bin computation and the same bins
				computedWidth = width; // this width works too
				return hist;
			}
			w.start();
			bins = binsForWidth(width, countValidEntries(it, map));
			w.stop();
			if (bins == hist.size()) {// correct number of bins
				computedWidth = width; // this width works too
				return hist;
			}
			hist = null; // don't hit the cache
		} else {
			bins = binsForWidth(width);
			if (bins <= 0) // fast and
				bins = binsForWidth(width, countValidEntries(it, map));
		}
		System.out.println("compute hist " + w);
		w.start();
		assert bins > 0;
		hist = new SimpleHistogram(bins);
		computedWidth = width;
		for (IRow row : it) {
			hist.add(map.applyPrimitive(row));
		}
		System.out.println(w);
		return hist;
	}

	private int countValidEntries(Iterable<IRow> it, IFloatFunction<IRow> map) {
		Stopwatch w = new Stopwatch().start();
		int valid = 0;
		int invalid = 0;
		for (IRow row : it) {
			float v = map.applyPrimitive(row);
			if (!Float.isNaN(v))
				valid++;
			else
				invalid++;
		}
		System.out.println("valid: " + valid + " and " + invalid + " " + w);
		return valid;
	}

	public void invalidate() {
		this.hist = null;
	}
}
