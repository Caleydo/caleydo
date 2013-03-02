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
