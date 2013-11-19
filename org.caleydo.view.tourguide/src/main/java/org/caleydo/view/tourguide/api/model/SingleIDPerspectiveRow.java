/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.model;

import java.util.Collection;
import java.util.Collections;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDType;

/**
 * @author Samuel Gratzl
 *
 */
public final class SingleIDPerspectiveRow extends AVirtualArrayScoreRow implements ITablePerspectiveScoreRow {
	private final ASingleIDDataDomainQuery query;
	private final String label;
	private final Integer id;

	private volatile VirtualArray va = null; // lazy
	private volatile TablePerspective perspective; // lazy

	public SingleIDPerspectiveRow(String label, Integer id, ASingleIDDataDomainQuery query) {
		this.label = label;
		this.id = id;
		this.query = query;
	}

	@Override
	public SingleIDPerspectiveRow clone() {
		return (SingleIDPerspectiveRow) super.clone();
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

	public IDType getSingleIDType() {
		return query.getSingleIDType();
	}

	@Override
	public IDType getDimensionIdType() {
		return getSingleIDType();
	}

	@Override
	public Iterable<Integer> getDimensionIDs() {
		return Collections.singleton(id);
	}

	@Override
	public IDType getIdType() {
		return query.getDataDomain().getOppositeIDType(query.getSingleIDType());
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
		return query.getDim().select(dataDomain.getTable().depth(), dataDomain.getTable().size());
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

	@Override
	public boolean is(Perspective p) {
		if (perspective != null)
			return perspective.getRecordPerspective().equals(p) || perspective.getDimensionPerspective().equals(p);
		// estimator using the same label
		return p.getDataDomain().equals(query.getDataDomain()) && p.getLabel().equals(label);
	}
}
