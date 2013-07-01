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

import java.util.Collection;
import java.util.Collections;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDType;

/**
 * @author Samuel Gratzl
 *
 */
public final class CategoricalPerspectiveRow extends AVirtualArrayScoreRow implements ITablePerspectiveScoreRow {
	private final CategoricalDataDomainQuery query;
	private final String label;
	private final Integer id;

	private volatile VirtualArray va = null; // lazy
	private volatile TablePerspective perspective; // lazy

	public CategoricalPerspectiveRow(String label, Integer id, CategoricalDataDomainQuery query) {
		this.label = label;
		this.id = id;
		this.query = query;
	}

	@Override
	public CategoricalPerspectiveRow clone() {
		return (CategoricalPerspectiveRow) super.clone();
	}

	@Override
	public String getLabel() {
		return label;
	}

	public Integer getDimensionID() {
		return id;
	}

	@Override
	public String getPersistentID() {
		return query.getDataDomain().getDataDomainID() + "_" + id;
	}

	@Override
	public IDataDomain getDataDomain() {
		return query.getDataDomain();
	}

	@Override
	public VirtualArray getVirtualArray() {
		if (va != null)
			return va;
		synchronized(this) {
			va = query.createVirtualArray(label, id);
		}
		return va;
	}

	public IDType getCategoryIDType() {
		return query.getCategoryIDType();
	}

	@Override
	public IDType getDimensionIdType() {
		return getCategoryIDType();
	}

	@Override
	public Iterable<Integer> getDimensionIDs() {
		return Collections.singleton(id);
	}

	@Override
	public IDType getIdType() {
		return query.getDataDomain().getOppositeIDType(query.getCategoryIDType());
	}

	@Override
	public int getGroupSize() {
		if (this.perspective != null)
			super.getGroupSize();
		return query.getGroupSize(this.id);
	}

	@Override
	public Collection<GroupInfo> getGroupInfos() {
		return query.getGroupInfos(this.id);
	}

	@Override
	public int size() {
		ATableBasedDataDomain dataDomain = query.getDataDomain();
		return dataDomain.getTable().depth();
	}

	@Override
	protected boolean isFiltered() {
		return query.hasFilter();
	}

	@Override
	protected boolean filter(Group g) {
		return query.apply(g);
	}

	@Override
	public TablePerspective asTablePerspective() {
		if (perspective != null)
			return perspective;
		synchronized (this) {
			perspective = query.createTablePerspective(label, id, getVirtualArray());
		}
		return perspective;
	}

	@Override
	public boolean is(TablePerspective tablePerspective) {
		if (perspective != null)
			return perspective.equals(tablePerspective);
		// estimator using the same label
		return tablePerspective.getDataDomain().equals(query.getDataDomain())
				&& tablePerspective.getLabel().equals(label);
	}
}
