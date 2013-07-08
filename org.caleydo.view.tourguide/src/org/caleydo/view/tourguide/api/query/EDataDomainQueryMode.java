/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.query;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.util.base.DefaultLabelProvider;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.view.tourguide.internal.model.InhomogenousDataDomainQuery;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * the mode in which the data domain query is see {@link DataDomainQuery}
 *
 * @author Samuel Gratzl
 *
 */
public enum EDataDomainQueryMode implements Predicate<IDataDomain> {
	STRATIFICATIONS, PATHWAYS, OTHER;

	/**
	 * @return
	 */
	public String getLabel() {
		switch (this) {
		case STRATIFICATIONS:
			return "Stratification";
		case PATHWAYS:
			return "Pathway";
		case OTHER:
			return "Other";
		}
		throw new IllegalArgumentException("unknown me");
	}

	public boolean isDependent() {
		return this != STRATIFICATIONS;
	}

	@Override
	public boolean apply(IDataDomain dataDomain) {
		switch(this) {
		case PATHWAYS:
			return dataDomain instanceof PathwayDataDomain;
		case STRATIFICATIONS:
			if (!(dataDomain instanceof ATableBasedDataDomain))
				return false;
			if (!((ATableBasedDataDomain) dataDomain).getTable().isDataHomogeneous())
				return InhomogenousDataDomainQuery.hasOne(dataDomain, EDataClass.CATEGORICAL);
			return true;
		case OTHER:
			return (dataDomain instanceof ATableBasedDataDomain && !((ATableBasedDataDomain) dataDomain).getTable()
					.isDataHomogeneous()) && InhomogenousDataDomainQuery.hasOne(dataDomain, EDataClass.NATURAL_NUMBER);
		}
		throw new IllegalArgumentException("unknown me");
	}

	public Collection<? extends IDataDomain> getAllDataDomains() {
		List<? extends IDataDomain> dataDomains;
		switch(this) {
		case PATHWAYS:
			dataDomains = DataDomainManager.get().getDataDomainsByType(PathwayDataDomain.class);
			break;
		default:
			dataDomains = DataDomainManager.get().getDataDomainsByType(ATableBasedDataDomain.class);
			break;
		}
		dataDomains = Lists.newArrayList(Iterables.filter(dataDomains, this));
		Collections.sort(dataDomains, DefaultLabelProvider.BY_LABEL);
		return dataDomains;
	}

	/**
	 * @param secondaryId
	 * @return
	 */
	public static EDataDomainQueryMode valueOfSafe(String value) {
		value = value.toLowerCase();
		for (EDataDomainQueryMode mode : EDataDomainQueryMode.values()) {
			if (value.contains(mode.name().toLowerCase()))
				return mode;
		}
		return EDataDomainQueryMode.STRATIFICATIONS;
	}
}
