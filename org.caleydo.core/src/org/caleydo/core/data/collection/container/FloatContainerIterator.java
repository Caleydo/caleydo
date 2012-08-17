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
 * Iterator for FloatContainer Initialized by passing the container. Then provides the common iterator
 * accessors.
 * 
 * @author Alexander Lex
 */
public class FloatContainerIterator
	extends AContainerIterator {

	private FloatContainer floatContainer = null;

	/**
	 * Constructor Pass the container you want to have an iterator on
	 * 
	 * @param primitiveFloatDimension
	 */
	public FloatContainerIterator(FloatContainer floatContainer) {
		this.floatContainer = floatContainer;
		this.iSize = floatContainer.size();
	}

	public FloatContainerIterator(FloatContainer floatCContainer, VirtualArray<?, ?, ?> virtualArray) {
		this(floatCContainer);
		this.virtualArray = virtualArray;
		this.vaIterator = virtualArray.iterator();
	}

	/**
	 * Returns the next element in the container Throws a NoSuchElementException if no more elements eist
	 * 
	 * @return the next element
	 */
	public float next() {
		if (virtualArray != null)
			return floatContainer.get(vaIterator.next());
		else {
			try {
				return floatContainer.get(++iIndex);
			}
			catch (IndexOutOfBoundsException e) {
				throw new NoSuchElementException();
			}
		}
	}

}
