package org.caleydo.core.data.group;

import java.util.ListIterator;

/**
 * Iterator implementation for group lists
 * 
 * @author Bernhard Schlegl
 */
public class GroupIterator
	implements ListIterator<Group> {

	int iCount = -1;
	GroupList groupList;
	boolean bLastMoveOperationWasPrevious = false;

	/**
	 * Constructor
	 * 
	 * @param groupList
	 *            the group list on which the iterator is executed
	 */
	public GroupIterator(GroupList groupList) {
		this.groupList = groupList;
	}

	@Override
	public void add(Group iNewElement) {
		groupList.add(++iCount, iNewElement);
	}

	@Override
	public boolean hasNext() {
		if (iCount < groupList.size() - 1)
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
	public Group next() {
		bLastMoveOperationWasPrevious = false;
		return groupList.get(++iCount);

	}

	@Override
	public int nextIndex() {
		return iCount + 1;
	}

	@Override
	public Group previous() {
		bLastMoveOperationWasPrevious = true;
		return groupList.get(iCount--);
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
			groupList.remove(iCount);
		}
		else {
			groupList.remove(--iCount);
		}
	}

	@Override
	public void set(Group iNewElement) {
		if (bLastMoveOperationWasPrevious) {
			groupList.set(iCount, iNewElement);
		}
		else {
			groupList.set(iCount - 1, iNewElement);
		}
	}
}
