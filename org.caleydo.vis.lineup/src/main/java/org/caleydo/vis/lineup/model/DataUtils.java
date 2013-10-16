/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.model;

import java.util.Iterator;

import org.caleydo.core.util.function.IDoubleIterator;
import org.caleydo.vis.lineup.data.IDoubleFunction;

/**
 * @author Samuel Gratzl
 *
 */
public class DataUtils {
	public static SimpleHistogram getHist(int bins, IDoubleIterator it) {
		SimpleHistogram hist = new SimpleHistogram(bins);
		while (it.hasNext()) {
			double value = it.nextPrimitive();
			hist.add(value);
		}
		return hist;
	}

	public static <T> SimpleHistogram getHist(int bins, Iterator<T> it, IDoubleFunction<T> map) {
		SimpleHistogram hist = new SimpleHistogram(bins);
		while (it.hasNext()) {
			hist.add(map.applyPrimitive(it.next()));
		}
		return hist;
	}
}
