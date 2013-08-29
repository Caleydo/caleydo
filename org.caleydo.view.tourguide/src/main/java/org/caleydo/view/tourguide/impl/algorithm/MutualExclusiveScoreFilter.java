/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
import org.caleydo.view.tourguide.api.compute.ComputeScoreFilters;
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
		boolean r = Objects.equals(property.getCategoryName(), itemGroup.getLabel());
		if (!r)
			return false;
		return ComputeScoreFilters.TOO_SMALL.doCompute(item, itemGroup, reference, bg);
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
