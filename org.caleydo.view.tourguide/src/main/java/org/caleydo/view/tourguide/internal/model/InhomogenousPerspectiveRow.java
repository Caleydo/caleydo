/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.model;

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
public final class InhomogenousPerspectiveRow extends AVirtualArrayScoreRow implements ITablePerspectiveScoreRow {
	private final TablePerspective clinical;
	private final InhomogenousDataDomainQuery query;

	public InhomogenousPerspectiveRow(TablePerspective clinical, InhomogenousDataDomainQuery query) {
		this.clinical = clinical;
		this.query = query;
	}

	@Override
	public InhomogenousPerspectiveRow clone() {
		return (InhomogenousPerspectiveRow) super.clone();
	}

	/**
	 * @return the stratification, see {@link #stratification}
	 */
	public Perspective getStratification() {
		return clinical.getRecordPerspective();
	}

	@Override
	public IDType getDimensionIdType() {
		return clinical.getDimensionPerspective().getIdType();
	}

	@Override
	public Iterable<Integer> getDimensionIDs() {
		return clinical.getDimensionPerspective().getVirtualArray();
	}

	@Override
	public String getLabel() {
		return clinical.getLabel();
	}

	@Override
	public String getPersistentID() {
		return clinical.getTablePerspectiveKey();
	}

	@Override
	public VirtualArray getVirtualArray() {
		return clinical.getRecordPerspective().getVirtualArray();
	}

	@Override
	public IDataDomain getDataDomain() {
		return clinical.getDataDomain();
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
		return clinical;
	}

	@Override
	public boolean is(TablePerspective tablePerspective) {
		return clinical.equals(tablePerspective);
	}
}
