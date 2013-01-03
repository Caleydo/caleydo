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
package org.caleydo.core.data.collection.column.container;

import java.util.ListIterator;

import org.caleydo.core.data.virtualarray.VirtualArray;

/**
 * Iterator for containers of the type ATypedCContainer. The iterator can handle virtual arrays (optionally),
 * thereby masking the real structure in the dimension
 * 
 * @author Alexander Lex
 * @param <T>
 *            the type
 */
public class ContainerIterator<T>
	extends AContainerIterator
	implements ListIterator<T> {
	private ATypedContainer<T> container;
	private ListIterator<T> containerIterator;

	/**
	 * Constructor for iterator. Provide a ListIterator from the ArrayList of the Container
	 * 
	 * @param container
	 *            the container itself
	 * @param containerIterator
	 *            a ListIterator from the ArrayList
	 */
	public ContainerIterator(ATypedContainer<T> container, ListIterator<T> containerIterator) {
		this.container = container;
		this.containerIterator = containerIterator;
	}

	/**
	 * Constructor for iterator when operating with virtual arrays.
	 * 
	 * @param container
	 *            the container on which is iterated
	 * @param virtualArray
	 *            the virtual array
	 */
	public ContainerIterator(ATypedContainer<T> container, VirtualArray<?, ?, ?> virtualArray) {
		this.container = container;
		this.virtualArray = virtualArray;
		this.vaIterator = virtualArray.iterator();
	}

	/**
	 * Operation not supported
	 */
	@Override
	public void add(T element) {
		throw new UnsupportedOperationException("Adding is not supported on dimensions");
	}

	@Override
	public boolean hasPrevious() {
		if (vaIterator != null)
			return vaIterator.hasPrevious();
		else
			return containerIterator.hasPrevious();
	}

	@Override
	public T next() {
		if (vaIterator != null)
			return container.get(vaIterator.next());
		else
			return containerIterator.next();
	}

	@Override
	public int nextIndex() {
		if (vaIterator != null)
			return vaIterator.nextIndex();
		else
			return containerIterator.nextIndex();
	}

	@Override
	public T previous() {
		if (vaIterator != null)
			return container.get(vaIterator.previous());
		else
			return containerIterator.previous();
	}

	@Override
	public int previousIndex() {
		if (vaIterator != null)
			return vaIterator.previousIndex();
		else
			return containerIterator.previousIndex();
	}

	@Override
	public void set(T e) {
		// we could allow that here (technically) but we probably don't want to
		throw new UnsupportedOperationException("Modification is not supported on dimensions");
	}

}
