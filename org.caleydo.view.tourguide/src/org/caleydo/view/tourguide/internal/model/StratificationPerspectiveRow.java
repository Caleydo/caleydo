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

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;

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
	protected boolean isFiltered() {
		return false;
	}

	@Override
	protected boolean filter(Group g) {
		return true;
	}

	@Override
	public TablePerspective asTablePerspective() {
		return query.asTablePerspective(stratification);
	}

	@Override
	public boolean is(TablePerspective tablePerspective) {
		return stratification.equals(tablePerspective.getRecordPerspective());
	}
}
