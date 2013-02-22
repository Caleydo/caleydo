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
package org.caleydo.view.tourguide.internal.view;

import java.util.Collection;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.base.ILabelProvider;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.caleydo.view.tourguide.v3.model.ARow;

/**
 * @author Samuel Gratzl
 *
 */
public final class PerspectiveRow extends ARow implements ILabelProvider {
	private final TablePerspective perspective;
	private final Perspective stratification;
	private final Group group;

	public PerspectiveRow(Perspective stratification, TablePerspective perspective) {
		this(stratification, null, perspective);
	}

	public PerspectiveRow(Perspective stratification, Group group, TablePerspective perspective) {
		this.stratification = stratification;
		this.perspective = perspective;
		this.group = group;
	}

	@Override
	public String getLabel() {
		String label = stratification.getLabel();
		if (group != null)
			label += ": " + group.getLabel();
		return label;
	}

	@Override
	public String getProviderName() {
		return stratification.getProviderName();
	}

	public IDataDomain getDataDomain() {
		return stratification.getDataDomain();
	}

	public Perspective getStratification() {
		return stratification;
	}

	public TablePerspective getPerspective() {
		return perspective;
	}

	public Group getGroup() {
		return group;
	}

	/**
	 * @param visibleColumns
	 * @return
	 */
	public Pair<Collection<Integer>, IDType> getIntersection(Collection<IScore> visibleColumns) {
		// TODO Auto-generated method stub
		return null;
	}
}
