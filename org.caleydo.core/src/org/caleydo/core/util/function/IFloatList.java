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
package org.caleydo.core.util.function;

import java.util.List;

/**
 * a special version of a {@link List} for float handling
 *
 * @author Samuel Gratzl
 *
 */
public interface IFloatList extends List<Float> {
	/**
	 * returns the primitive version of {@link #get(int)}
	 *
	 * @param index
	 * @return
	 */
	float getPrimitive(int index);

	/**
	 * returns a view of this list where, the given function will be applied
	 * 
	 * @param f
	 * @return
	 */
	IFloatListView map(IFloatFunction f);

	/**
	 * simple statistics 0...min 1...max, more may follow
	 *
	 * @return
	 */
	float[] computeStats();
}
