/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.model;

import static org.caleydo.vis.lineup.ui.RenderStyle.binsForWidth;

import org.caleydo.vis.lineup.data.IFloatFunction;

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
		// Stopwatch w = new Stopwatch();
		int bins;
		if (hist != null) {
			if (Math.abs(computedWidth - width) < 3) // should get the same histogram
				return hist;
			bins = binsForWidth(width);
			if (bins >= 0 && hist.size() == bins) {// fast bin computation and the same bins
				computedWidth = width; // this width works too
				return hist;
			}
			// w.start();
			bins = binsForWidth(width, countValidEntries(it, map));
			// w.stop();
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
		// System.out.println("compute hist " + w);
		// w.start();
		assert bins > 0;
		hist = new SimpleHistogram(bins);
		computedWidth = width;
		for (IRow row : it) {
			hist.add(map.applyPrimitive(row));
		}
		// System.out.println(w);
		return hist;
	}

	private int countValidEntries(Iterable<IRow> it, IFloatFunction<IRow> map) {
		// Stopwatch w = new Stopwatch().start();
		int valid = 0;
		for (IRow row : it) {
			float v = map.applyPrimitive(row);
			if (!Float.isNaN(v))
				valid++;
		}
		// System.out.println("valid: " + valid + " and " + invalid + " " + w);
		return valid;
	}

	public void invalidate() {
		this.hist = null;
	}
}
