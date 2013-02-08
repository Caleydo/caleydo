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
package org.caleydo.core.util;

import java.util.BitSet;

/**
 * integer id pool
 *
 * @author Samuel Gratzl
 *
 */
public final class IntegerPool {
	private int last = 0; // last used
	// free list for elements not used anymore
	private BitSet free = new BitSet();

	/**
	 * returns a new free id
	 *
	 * @return
	 */
	public int checkOut() {
		if (!free.isEmpty()) // reuse
			return free.nextSetBit(0);
		return ++last; // reserve new
	}

	/**
	 * frees a given id, such as it can be used again
	 *
	 * @param id
	 */
	public void checkIn(int id) {
		assert id >= 0 && id <= last; // valid range
		if (last == id) { // same as last used, undo
			last--;
			// compact the free list
			while (free.get(last)) {
				free.clear(last);
				last--;
			}
		} else
			free.set(id);
	}

}
