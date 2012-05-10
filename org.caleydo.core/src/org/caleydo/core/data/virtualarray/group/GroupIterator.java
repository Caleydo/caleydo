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

	int iCount = -1;
	GroupType groupList;
	boolean bLastMoveOperationWasPrevious = false;

	/**
	 * Constructor
	 * 
	 * @param groupList
	 *            the group list on which the iterator is executed
	 */
	public GroupIterator(GroupType groupList) {
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
