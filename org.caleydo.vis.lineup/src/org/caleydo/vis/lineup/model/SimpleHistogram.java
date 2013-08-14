/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.model;

import java.util.Iterator;

import com.google.common.primitives.Ints;

/**
 * simple version of a normalized histogram
 *
 * @author Samuel Gratzl
 *
 */
public class SimpleHistogram implements Iterable<Integer> {
	/**
	 * the hist
	 */
	private final int[] bins;
	/**
	 * number of nans
	 */
	private int nans = 0;
	/**
	 * the largest bin value
	 */
	private int largestValue;
	/**
	 * the total number of items
	 */
	private int count;

	public SimpleHistogram(int bins) {
		this.bins = new int[bins];
		largestValue = 0;
	}

	/**
	 * @return the number of bins
	 */
	public int size() {
		return bins.length;
	}

	/**
	 * returns the bin of the given value or -1 if it is a NaN
	 *
	 * @param value
	 * @return
	 */
	public int getBinOf(float value) {
		if (Float.isNaN(value))
			return -1;
		return Math.round(value * (bins.length - 1));
	}

	/**
	 * add the given normalized value to this histogram
	 *
	 * @param value
	 */
	public void add(float value) {
		if (Float.isNaN(value)) {
			nans++;
			return;
		}
		int bin = Math.round(value * (bins.length - 1));
		if (bin < 0)
			bin = 0;
		if (bin >= bins.length)
			bin = bins.length - 1;
		bins[bin]++;
		count++;
		if (bins[bin] > largestValue)
			largestValue = bins[bin];
	}

	/**
	 * @return the count, see {@link #count}
	 */
	public int getCount(boolean includeNaN) {
		return count + (includeNaN ? nans : 0);
	}

	/**
	 * returns the number of NaN entries
	 *
	 * @return
	 */
	public int getNaN() {
		return nans;
	}

	/**
	 * returns the largest value of this histogram
	 *
	 * @param includeNaN
	 *            should also be NaN values be considered
	 * @return
	 */
	public int getLargestValue(boolean includeNaN) {
		if (includeNaN && nans > largestValue)
			return nans;
		return largestValue;
	}

	public int get(int bin) {
		return bins[bin];
	}

	@Override
	public Iterator<Integer> iterator() {
		return Ints.asList(bins).iterator();
	}

}

