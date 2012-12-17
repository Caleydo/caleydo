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
import org.caleydo.core.id.IDType;
import org.caleydo.view.tourguide.algorithm.LogRank;

import com.google.common.collect.Sets;

/**
 * @author Samuel Gratzl
 *
 */
public class LogRankGroupMetric extends AComputedGroupScore implements IComputedGroupScore {
	private final LogRank algorithm;
	private final ATableBasedDataDomain clinical;

	public LogRankGroupMetric(String label, Integer clinicalVariable) {
		super(label);
		this.clinical = DataDomainOracle.getClinicalDataDomain();
		this.algorithm = LogRank.get(clinicalVariable, clinical);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((algorithm == null) ? 0 : algorithm.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LogRankGroupMetric other = (LogRankGroupMetric) obj;
		if (algorithm == null) {
			if (other.algorithm != null)
				return false;
		} else if (!algorithm.equals(other.algorithm))
			return false;
		return true;
	}


	@Override
	public IDType getTargetType(TablePerspective as) {
		return clinical.getRecordIDType();
	}

	@Override
	public float compute(Set<Integer> group, Set<Integer> stratification) {
		// me versus the rest
		return algorithm.compute(group, Sets.difference(stratification, group));
	}


}

