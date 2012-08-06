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

import java.util.ArrayList;

import org.caleydo.core.data.virtualarray.VirtualArray;

/**
 * Base class for CContainer which can use Generics
 * 
 * @author Alexander Lex
 * @param <T>
 *            the Type of the container
 */

public abstract class ATypedContainer<T>
	implements IContainer {
	ArrayList<T> alContainer;

	/**
	 * Returns the element of type T at the index iIndex
	 * 
	 * @param iIndex
	 *            the index
	 * @return the value at iIndex of type T
	 */
	public T get(int iIndex) {
		return alContainer.get(iIndex);
	}

	@Override
	public int size() {
		return alContainer.size();
	}

	/**
	 * Returns an iterator on the data Do not use the iterators remove, add or set function, since it will
	 * cause an UnsupportedOperationException.
	 * 
	 * @return the iterator
	 */
	public ContainerIterator<T> iterator() {
		return new ContainerIterator<T>(this, alContainer.listIterator());
	}

	/**
	 * Returns an iterator on the container which iterates based on a virtual array
	 * 
	 * @param virtualArray
	 *            the virtual array which the iteration is based on
	 * @return the iterator
	 */
	public ContainerIterator<T> iterator(VirtualArray<?, ?, ?> virtualArray) {
		return new ContainerIterator<T>(this, virtualArray);
	}

}
