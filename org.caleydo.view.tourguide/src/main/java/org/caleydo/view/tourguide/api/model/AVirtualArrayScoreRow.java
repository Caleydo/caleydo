/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.id.IDType;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

/**
 * a {@link AScoreRow} based on a {@link VirtualArray}
 * 
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
		// filter the groups
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
	public Collection<GroupInfo> getGroupInfos() {
		return Collections2.transform(getGroups(), new Function<Group, GroupInfo>() {
			@Override
			public GroupInfo apply(Group in) {
				return new GroupInfo(in.getLabel(), in.getSize(), null);
			}
		});
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
