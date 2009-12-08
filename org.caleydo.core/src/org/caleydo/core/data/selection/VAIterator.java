package org.caleydo.core.data.selection;

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

	/**
	 * Constructor
	 * 
	 * @param virtualArray
	 *            the virtual array on which the iterator is executed
	 */
	public VAIterator(VirtualArray virtualArray) {
		this.virtualArray = virtualArray;
	}

	@Override
	public void add(Integer iNewElement) {
		virtualArray.add(++iCount, iNewElement);
	}

	@Override
	public boolean hasNext() {
		if (iCount < virtualArray.size() - 1)
			return true;

		return false;
	}

	@Override
	public boolean hasPrevious() {
		if (iCount > 0)
			return true;

		return false;
	}

	@Override
	public Integer next() {
		bLastMoveOperationWasPrevious = false;
		return virtualArray.get(++iCount);

	}

	@Override
	public int nextIndex() {
		return iCount + 1;
	}

	@Override
	public Integer previous() {
		bLastMoveOperationWasPrevious = true;
		return virtualArray.get(iCount--);
	}

	@Override
	public int previousIndex() {
		if (iCount < 0)
			return -1;

		return iCount;
	}

	@Override
	public void remove() {
		if (bLastMoveOperationWasPrevious) {
			virtualArray.remove(iCount);
		}
		else {
			virtualArray.remove(--iCount);
		}
	}

	@Override
	public void set(Integer iNewElement) {
		if (bLastMoveOperationWasPrevious) {
			virtualArray.set(iCount, iNewElement);
		}
		else {
			virtualArray.set(iCount - 1, iNewElement);
		}

	}

}
