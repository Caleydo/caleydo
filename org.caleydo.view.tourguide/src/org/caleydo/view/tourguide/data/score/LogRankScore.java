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
package org.caleydo.view.tourguide.data.score;

import java.util.Set;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainOracle;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDType;
import org.caleydo.view.tourguide.algorithm.LogRank;

/**
 * @author Samuel Gratzl
 *
 */
public class LogRankScore extends AGroupScore implements IComputedGroupScore {
	private final ATableBasedDataDomain clinical = DataDomainOracle.getClinicalDataDomain();
	private final Integer clinicalVariable;
	private final String clinicialVariableLabel;

	public LogRankScore(Integer clinicalVariable, TablePerspective stratification, Group group) {
		this(null, clinicalVariable, stratification, group);
	}

	public LogRankScore(String label, Integer clinicalVariable, TablePerspective stratification, Group group) {
		super(label, stratification, group);
		this.clinicalVariable = clinicalVariable;
		this.clinicialVariableLabel = clinical.getDimensionLabel(clinicalVariable);
	}

	public Integer getClinicalVariable() {
		return clinicalVariable;
	}

	/**
	 * @return the clinicialVariableLabel, see {@link #clinicialVariableLabel}
	 */
	public String getClinicialVariableLabel() {
		return clinicialVariableLabel;
	}

	@Override
	public IDType getTargetType(TablePerspective as) {
		return clinical.getRecordIDType();
	}

	@Override
	public float compute(Set<Integer> a, Set<Integer> b) {
		return LogRank.get(clinicalVariable, clinical).compute(a, b);
	}
}
