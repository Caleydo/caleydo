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
			return table.getDefaultDimensionPerspective().getVirtualArray();
		else
			return table.getDefaultRecordPerspective().getVirtualArray();
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

