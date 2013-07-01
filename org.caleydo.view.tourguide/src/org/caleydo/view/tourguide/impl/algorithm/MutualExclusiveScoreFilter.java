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

import java.util.List;
import java.util.Objects;

import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.column.container.CategoryProperty;
import org.caleydo.core.data.collection.table.CategoricalTable;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainOracle;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.view.tourguide.spi.algorithm.IComputeElement;
import org.caleydo.view.tourguide.spi.compute.IComputeScoreFilter;

/**
 * @author Samuel Gratzl
 *
 */
public class MutualExclusiveScoreFilter implements IComputeScoreFilter {
	private final CategoryProperty<?> property;

	public MutualExclusiveScoreFilter(CategoryProperty<?> property) {
		this.property = property;
	}

	@Override
	public boolean doCompute(IComputeElement item, Group itemGroup, IComputeElement reference, Group bg) {
		if (itemGroup == null || bg == null)
			return false;
		IDataDomain dataDomainA = item.getDataDomain();
		IDataDomain dataDomainB = reference.getDataDomain();
		if (!dataDomainA.equals(dataDomainB))
			return false;
		if (!(dataDomainA instanceof ATableBasedDataDomain)
				|| !canHaveMutualExclusiveScore((ATableBasedDataDomain) dataDomainA))
			return false;
		return Objects.equals(property.getCategoryName(), itemGroup.getLabel());
	}

	public static List<?> getProperties(ATableBasedDataDomain dataDomain) {
		CategoricalClassDescription<?> categoryDescriptions = ((CategoricalTable<?>) dataDomain.getTable())
				.getCategoryDescriptions();
		return categoryDescriptions.getCategoryProperties();
	}

	public static boolean canHaveMutualExclusiveScore(ATableBasedDataDomain dataDomain) {
		return DataDomainOracle.isCategoricalDataDomain(dataDomain) && getProperties(dataDomain).size() == 2;
	}
}
