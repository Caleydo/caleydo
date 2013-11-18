/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.compute;

import java.util.Collection;
import java.util.Iterator;

import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDType;
import org.caleydo.view.tourguide.spi.algorithm.IComputeElement;

/**
 *
 * implementation of {@link IComputeElement} based on a {@link Perspective}
 *
 * @author Samuel Gratzl
 *
 */
public final class ComputeElement implements IComputeElement {
	private final Perspective stratifation;

	public ComputeElement(Perspective stratifation) {
		this.stratifation = stratifation;
	}

	@Override
	public String getLabel() {
		return stratifation.getLabel();
	}

	@Override
	public Iterator<Integer> iterator() {
		return getVirtualArray().iterator();
	}

	@Override
	public String getPersistentID() {
		return stratifation.getPerspectiveID();
	}

	@Override
	public IDType getIdType() {
		return stratifation.getIdType();
	}

	@Override
	public Collection<Group> getGroups() {
		return getVirtualArray().getGroupList().getGroups();
	}

	@Override
	public int getGroupSize() {
		return getGroups().size();
	}

	@Override
	public Collection<Integer> of(Group group) {
		if (group == null)
			return getVirtualArray().getIDs();
		return getVirtualArray().getIDsOfGroup(group.getGroupIndex());
	}

	@Override
	public IDType getDimensionIdType() {
		return ((ATableBasedDataDomain) stratifation.getDataDomain()).getOppositeIDType(getIdType());
	}

	@Override
	public Iterable<Integer> getDimensionIDs() {
		ATableBasedDataDomain d = ((ATableBasedDataDomain) stratifation.getDataDomain());
		Table table = d.getTable();
		if (d.getRecordIDType() == getIdType())
			return table.getDefaultDimensionPerspective(false).getVirtualArray();
		else
			return table.getDefaultRecordPerspective(false).getVirtualArray();
	}

	public VirtualArray getVirtualArray() {
		return stratifation.getVirtualArray();
	}

	@Override
	public IDataDomain getDataDomain() {
		return stratifation.getDataDomain();
	}

	@Override
	public int size() {
		return getVirtualArray().size();
	}
}

