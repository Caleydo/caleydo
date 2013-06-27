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
package org.caleydo.view.tourguide.impl.algorithm;

import java.util.Set;

import org.caleydo.core.data.collection.table.CategoricalTable;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainOracle;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDType;
import org.caleydo.view.tourguide.spi.algorithm.IComputeElement;
import org.caleydo.view.tourguide.spi.algorithm.IGroupAlgorithm;
import org.caleydo.view.tourguide.spi.compute.IComputeScoreFilter;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Samuel Gratzl
 *
 */
public class MutualExclusive implements IGroupAlgorithm, IComputeScoreFilter {
	private static final MutualExclusive instance = new MutualExclusive();

	public static MutualExclusive get() {
		return instance;
	}

	@Override
	public boolean doCompute(IComputeElement a, Group ag, IComputeElement b, Group bg) {
		if (ag == null || bg == null)
			return false;
		IDataDomain dataDomainA = a.getDataDomain();
		IDataDomain dataDomainB = b.getDataDomain();
		if (!dataDomainA.equals(dataDomainB))
			return false;
		if (!(dataDomainA instanceof ATableBasedDataDomain)
				|| !canHaveMutualExclusiveScore((ATableBasedDataDomain) dataDomainA))
			return false;
		return ag.getLabel().equals(bg.getLabel());
	}

	public static boolean canHaveMutualExclusiveScore(ATableBasedDataDomain dataDomain) {
		return DataDomainOracle.isCategoricalDataDomain(dataDomain)
				&& ((CategoricalTable<?>) dataDomain.getTable()).getCategoryDescriptions().getCategoryProperties()
						.size() == 2;
	}

	private MutualExclusive() {

	}

	@Override
	public void init(IProgressMonitor monitor) {
		// nothing todo
	}

	@Override
	public String getAbbreviation() {
		return "ME";
	}

	@Override
	public String getDescription() {
		return "Mutual Exclusive against ";
	}

	@Override
	public IDType getTargetType(IComputeElement a, IComputeElement b) {
		return a.getIdType();
	}

	@Override
	public float compute(Set<Integer> q, Set<Integer> d, IProgressMonitor monitior) {
		// 1 - |intersect( q_mut, d_mut )|/|q_mut|
		int intersection = 0;
		for (Integer ai : q) {
			if (d.contains(ai))
				intersection++;
		}
		float score = 1 - ((float) intersection) / q.size();
		return score;
	}
}
