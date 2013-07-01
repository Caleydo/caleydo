/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.virtualarray.group;

import java.util.ListIterator;

/**
 * Iterator implementation for groupList lists
 * 
 * @author Bernhard Schlegl
 * @author Alexander Lex
 */
public class GroupIterator
	implements ListIterator<Group> {

	private final GroupList groupList;
	private int index = -1;
	private boolean lastMoveOperationWasPrevious = false;

	/**
	 * Constructor
	 * 
	 * @param groupList
	 *            the groupList list on which the iterator is executed
	 */
	public GroupIterator(GroupList groupList) {
		this.groupList = groupList;
	}

	@Override
	public void add(Group iNewElement) {
		groupList.add(++index, iNewElement);
	}

	@Override
	public boolean hasNext() {
		if (index < groupList.size() - 1)
			return true;

		return false;
	}

	@Override
	public boolean hasPrevious() {
		if (index > 0)
			return true;

		return false;
	}

	@Override
	public Group next() {
		lastMoveOperationWasPrevious = false;
		return groupList.get(++index);

	}

	@Override
	public int nextIndex() {
		return index + 1;
	}

	@Override
	public Group previous() {
		lastMoveOperationWasPrevious = true;
		return groupList.get(index--);
	}

	@Override
	public int previousIndex() {
		if (index < 0)
			return -1;

		return index;
	}

	@Override
	public void remove() {
		if (lastMoveOperationWasPrevious) {
			groupList.remove(index);
		}
		else {
			groupList.remove(--index);
		}
	}

	@Override
	public void set(Group iNewElement) {
		if (lastMoveOperationWasPrevious) {
			groupList.set(index, iNewElement);
		}
		else {
			groupList.set(index - 1, iNewElement);
		}
	}
}
