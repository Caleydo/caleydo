/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.model;

import org.caleydo.core.data.collection.EDimension;
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
public final class StratificationPerspectiveRow extends AVirtualArrayScoreRow implements ITablePerspectiveScoreRow,
		IPerspectiveScoreRow {
	private final StratificationDataDomainQuery query;
	private final Perspective stratification;
	private final EDimension dim;

	public StratificationPerspectiveRow(Perspective stratification, EDimension dim, StratificationDataDomainQuery query) {
		this.stratification = stratification;
		this.dim = dim;
		this.query = query;
	}

	@Override
	public StratificationPerspectiveRow clone() {
		return (StratificationPerspectiveRow) super.clone();
	}

	/**
	 * @return the dim, see {@link #dim}
	 */
	@Override
	public EDimension getDimension() {
		return dim;
	}
	/**
	 * @return the stratification, see {@link #stratification}
	 */
	@Override
	public Perspective asPerspective() {
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
		return query.getOppositeSelection().getVirtualArray();
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
		return stratification.equals(dim.select(tablePerspective.getDimensionPerspective(),
				tablePerspective.getRecordPerspective()));
	}

	@Override
	public boolean is(Perspective p) {
		return stratification.equals(p);
	}
}
