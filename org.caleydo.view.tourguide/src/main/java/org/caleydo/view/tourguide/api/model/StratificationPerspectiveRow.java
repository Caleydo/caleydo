/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.model;

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
public final class StratificationPerspectiveRow extends AVirtualArrayScoreRow implements ITablePerspectiveScoreRow {
	private final StratificationDataDomainQuery query;
	private final Perspective stratification;

	public StratificationPerspectiveRow(Perspective stratification, StratificationDataDomainQuery query) {
		this.stratification = stratification;
		this.query = query;
	}

	@Override
	public StratificationPerspectiveRow clone() {
		return (StratificationPerspectiveRow) super.clone();
	}

	/**
	 * @return the stratification, see {@link #stratification}
	 */
	public Perspective getStratification() {
		return stratification;
	}

	@Override
	public String getLabel() {
		return stratification.getLabel();
	}

	@Override
	public String getPersistentID() {
		return stratification.getPerspectiveID();
	}

	@Override
	public VirtualArray getVirtualArray() {
		return stratification.getVirtualArray();
	}

	@Override
	public IDataDomain getDataDomain() {
		return stratification.getDataDomain();
	}

	@Override
	public IDType getDimensionIdType() {
		return ((ATableBasedDataDomain) getDataDomain()).getOppositeIDType(getIdType());
	}

	@Override
	public Iterable<Integer> getDimensionIDs() {
		return query.getDimensionSelection().getVirtualArray();
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
		return query.asTablePerspective(stratification);
	}

	@Override
	public boolean is(TablePerspective tablePerspective) {
		return stratification.equals(tablePerspective.getRecordPerspective());
	}

	@Override
	public boolean is(Perspective p) {
		return stratification.equals(p);
	}
}
