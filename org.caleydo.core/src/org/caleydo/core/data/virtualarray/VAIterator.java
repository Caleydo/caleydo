/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
