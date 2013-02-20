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
package org.caleydo.view.tourguide.v3.model;

import java.util.Iterator;

import com.google.common.primitives.Ints;

/**
 * @author Samuel Gratzl
 *
 */
public class SimpleHistogram implements Iterable<Integer> {
	private final int[] bins;
	private int nans = 0;
	private int largestValue;

	public SimpleHistogram(int bins) {
		this.bins = new int[bins];
		largestValue = 0;
	}

	public int size() {
		return bins.length;
	}

	public int getBinOf(float value) {
		if (Float.isNaN(value))
			return -1;
		return Math.round(value * (bins.length - 1));
	}

	public void add(float value) {
		if (Float.isNaN(value)) {
			nans++;
			if (nans > largestValue)
				largestValue = nans;
			return;
		}
		int bin = Math.round(value * (bins.length - 1));
		if (bin < 0)
			bin = 0;
		if (bin >= bins.length)
			bin = bins.length - 1;
		bins[bin]++;
		if (bins[bin] > largestValue)
			largestValue = bins[bin];
	}

	public int getNaN() {
		return nans;
	}

	/**
	 * @return the largestValue, see {@link #largestValue}
	 */
	public int getLargestValue() {
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

