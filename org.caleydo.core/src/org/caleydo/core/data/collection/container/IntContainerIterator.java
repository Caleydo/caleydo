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
package org.caleydo.core.data.collection.container;

import java.util.NoSuchElementException;
import org.caleydo.core.data.virtualarray.VirtualArray;

/**
 * Iterator for IntCContainer. Initialized by passing the container. Provides the common iterator accessors.
 * 
 * @author Alexander Lex
 */
public class IntContainerIterator
	extends AContainerIterator {

	private int iIndex = 0;

	private IntContainer intCContainer = null;

	/**
	 * Constructor
	 * 
	 * @param intCContainer
	 *            the container over which to iterate
	 */
	public IntContainerIterator(IntContainer intCContainer) {
		this.intCContainer = intCContainer;
	}

	/**
	 * Constructor
	 * 
	 * @param intCContainer
	 * @param uniqueID
	 */
	public IntContainerIterator(IntContainer intCContainer, VirtualArray<?, ?, ?> virtualArray) {
		this(intCContainer);
		this.virtualArray = virtualArray;
		this.vaIterator = virtualArray.iterator();
	}

	/**
	 * Returns the next element in the container Throws a NoSuchElementException if no more elements exist
	 * 
	 * @return the next element
	 */
	public int next() {
		if (virtualArray != null)
			return intCContainer.get(vaIterator.next());
		else {
			try {
				return intCContainer.get(++iIndex);
			}
			catch (IndexOutOfBoundsException e) {
				throw new NoSuchElementException();
			}
		}
	}
}