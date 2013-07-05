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
 * Iterator implementation for group lists
 *
 * @author Bernhard Schlegl
 */
public class GroupIterator<GroupType extends GroupList<?, ?, ?>>
	implements ListIterator<Group> {

	private final GroupType group;
	private int index = -1;
	private boolean lastMoveOperationWasPrevious = false;

	/**
	 * Constructor
	 *
	 * @param groupList
	 *            the group list on which the iterator is executed
	 */
	public GroupIterator(GroupType groupList) {
		this.group = groupList;
	}

	@Override
	public void add(Group iNewElement) {
		group.add(++index, iNewElement);
	}

	@Override
	public boolean hasNext() {
		if (index < group.size() - 1)
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
		return group.get(++index);

	}

	@Override
	public int nextIndex() {
		return index + 1;
	}

	@Override
	public Group previous() {
		lastMoveOperationWasPrevious = true;
		return group.get(index--);
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
			group.remove(index);
		}
		else {
			group.remove(--index);
		}
	}

	@Override
	public void set(Group iNewElement) {
		if (lastMoveOperationWasPrevious) {
			group.set(index, iNewElement);
		}
		else {
			group.set(index - 1, iNewElement);
		}
	}
}
