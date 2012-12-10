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
package org.caleydo.view.tourguide.data.compute;


/**
 * @author Samuel Gratzl
 *
 */
public class IDSets {

	public static int intersect(IDSet a, IDSet b) {
		IDSet tmp;
		// iterate over those, which is faster
		if (a.isFastIteration() && b.isFastIteration()) {
			if (a.size() < b.size()) { // iterate over smaller
				tmp = a;
				a = b;
				b = tmp;
			}
		} else if (a.isFastIteration()) { // use the faster one
			tmp = a;
			a = b;
			b = tmp;
		}

		int intersection = 0;
		for (Integer bi : b) {
			if (a.contains(bi))
				intersection++;
		}
		return intersection;
	}

	public static int difference(IDSet a, IDSet b) {
		return a.size() - intersect(a, b);
	}

	public static int union(IDSet a, IDSet b) {
		return a.size() + b.size() - intersect(a, b);
	}
}
