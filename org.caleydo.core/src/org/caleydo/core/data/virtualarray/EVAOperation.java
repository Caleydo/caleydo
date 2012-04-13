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
package org.caleydo.core.data.virtualarray;

import org.caleydo.core.data.virtualarray.delta.VADeltaItem;

/**
 * List of operations allowed in virtual array deltas ({@link VADeltaItem}
 * 
 * @author Alexander Lex
 */
public enum EVAOperation {
	/**
	 * Append an element at the end of a virtual array
	 */
	APPEND,
	/**
	 * Append an element at the end of a virtual array, if the element is not yet contained in the list
	 */
	APPEND_UNIQUE,
	/**
	 * Add an element at a specified index
	 */
	ADD,
	/**
	 * Remove an element at a specified index
	 */
	REMOVE,
	/**
	 * Remove all occurrences of a specific element
	 */
	REMOVE_ELEMENT,
	/**
	 * Move an element from a specific index to another index
	 */
	MOVE,
	/**
	 * Copy an element at a specific index. The result will be added at index + 1, the rest will be moved one
	 * to the right
	 */
	COPY
}
