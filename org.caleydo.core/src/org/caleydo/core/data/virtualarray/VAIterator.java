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

import java.util.ListIterator;

/**
 * Iterator implementation for virtual arrays.
 * 
 * @author Alexander Lex
 */
public class VAIterator
	implements ListIterator<Integer> {

	int iCount = -1;
	VirtualArray virtualArray;
	boolean bLastMoveOperationWasPrevious = false;
	ListIterator<Integer> backingListIterator;

	/**
	 * Constructor
	 * 
	 * @param virtualArray
	 *            the virtual array on which the iterator is executed
	 */
	public VAIterator(VirtualArray virtualArray) {
		this.virtualArray = virtualArray;
		backingListIterator = virtualArray.virtualArrayList.listIterator();
	}

	@Override
	public void add(Integer newElement) {
		backingListIterator.add(newElement);
		virtualArray.setHashDirty();
	}

	@Override
	public boolean hasNext() {
		return backingListIterator.hasNext();
	}

	@Override
	public boolean hasPrevious() {
		return backingListIterator.hasPrevious();
	}

	@Override
	public Integer next() {
		return backingListIterator.next();
	}

	@Override
	public int nextIndex() {
		return backingListIterator.nextIndex();
	}

	@Override
	public Integer previous() {
		return backingListIterator.previous();
	}

	@Override
	public int previousIndex() {
		return backingListIterator.previousIndex();
	}

	@Override
	public void remove() {
		backingListIterator.remove();
		virtualArray.setHashDirty();
	}

	@Override
	public void set(Integer newElementID) {
		backingListIterator.set(newElementID);
		virtualArray.setHashDirty();
	}

}
