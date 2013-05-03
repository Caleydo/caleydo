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
package org.caleydo.view.tourguide.internal.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.id.IDType;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class AVirtualArrayScoreRow extends AScoreRow {

	public abstract VirtualArray getVirtualArray();

	@Override
	public final Collection<Group> getGroups() {
		GroupList base = getVirtualArray().getGroupList();
		if (!isFiltered())
			return base.getGroups();
		Collection<Group> r = new ArrayList<>(base.size());
		for (Group g : base) {
			if (filter(g))
				r.add(g);
		}
		return r;
	}

	protected abstract boolean isFiltered();
	protected abstract boolean filter(Group g);

	@Override
	public final Collection<Integer> of(Group group) {
		if (group == null)
			return getVirtualArray().getIDs();
		return getVirtualArray().getIDsOfGroup(group.getGroupIndex());
	}

	@Override
	public final Iterator<Integer> iterator() {
		return getVirtualArray().iterator();
	}

	@Override
	public IDType getIdType() {
		return getVirtualArray().getIdType();
	}

	@Override
	public int getGroupSize() {
		return getVirtualArray().getGroupList().size();
	}

	@Override
	public int size() {
		return getVirtualArray().size();
	}
}
