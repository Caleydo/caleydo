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
