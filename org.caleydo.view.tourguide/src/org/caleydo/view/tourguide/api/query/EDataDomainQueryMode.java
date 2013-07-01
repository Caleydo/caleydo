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
}
