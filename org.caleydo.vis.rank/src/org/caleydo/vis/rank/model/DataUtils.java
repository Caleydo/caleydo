/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.model;

import java.util.Iterator;

import org.caleydo.core.util.function.IFloatList;
import org.caleydo.vis.rank.data.IFloatFunction;

/**
 * @author Samuel Gratzl
 *
 */
public class DataUtils {
	public static SimpleHistogram getHist(int bins, IFloatList l) {
		SimpleHistogram hist = new SimpleHistogram(bins);
		int s = l.size();
		for (int i = 0; i < s; ++i) {
			float value = l.getPrimitive(i);
			hist.add(value);
		}
		return hist;
	}

	public static <T> SimpleHistogram getHist(int bins, Iterator<T> it, IFloatFunction<T> map) {
		SimpleHistogram hist = new SimpleHistogram(bins);
		while (it.hasNext()) {
			hist.add(map.applyPrimitive(it.next()));
		}
		return hist;
	}
}
