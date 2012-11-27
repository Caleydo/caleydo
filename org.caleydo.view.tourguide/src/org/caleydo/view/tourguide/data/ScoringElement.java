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
package org.caleydo.view.tourguide.data;

import java.util.Map;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.base.ILabelProvider;
import org.caleydo.view.tourguide.data.score.IScore;
import org.caleydo.view.tourguide.data.score.ProductScore;

/**
 * @author Samuel Gratzl
 *
 */
public final class ScoringElement implements ILabelProvider {
	private final TablePerspective stratification;
	private final Group group;
	/**
	 * product scores have different scores depending on the current scoring element, this map stores their selections
	 */
	private final Map<IScore, IScore> productSelections;

	public ScoringElement(TablePerspective stratification) {
		this(stratification, null, null);
	}

	public ScoringElement(TablePerspective stratification, Map<IScore, IScore> productSelections) {
		this(stratification, null, productSelections);
	}

	public ScoringElement(TablePerspective stratification, Group group) {
		this(stratification, group, null);
	}

	public ScoringElement(TablePerspective stratification, Group group, Map<IScore, IScore> productSelections) {
		this.stratification = stratification;
		this.group = group;
		this.productSelections = productSelections;
	}

	public IScore getSelected(ProductScore productScore) {
		return productSelections == null ? null : productSelections.get(productScore);
	}

	@Override
	public String getLabel() {
		String label = stratification.getRecordPerspective().getLabel();
		if (group != null)
			label += ": " + group.getLabel();
		return label;
	}

	@Override
	public String getProviderName() {
		return stratification.getProviderName();
	}

	public ATableBasedDataDomain getDataDomain() {
		return stratification.getDataDomain();
	}

	public TablePerspective getStratification() {
		return stratification;
	}

	public Group getGroup() {
		return group;
	}
}
