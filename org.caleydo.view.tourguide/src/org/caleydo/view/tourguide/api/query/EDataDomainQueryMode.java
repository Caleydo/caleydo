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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.caleydo.core.data.datadomain.ADataDomain;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.view.tourguide.spi.score.IScore;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

/**
 * the mode in which the data domain query is see {@link DataDomainQuery}
 *
 * @author Samuel Gratzl
 *
 */
public enum EDataDomainQueryMode {
	TABLE_BASED, GENE_SET;

	public boolean isCompatible(IDataDomain dataDomain) {
		switch(this) {
		case GENE_SET:
			return dataDomain instanceof PathwayDataDomain;
		case TABLE_BASED:
			return dataDomain instanceof ATableBasedDataDomain;
		}
		throw new IllegalArgumentException("unknown me");
	}

	public List<? extends IDataDomain> getAllDataDomains() {
		switch(this) {
		case TABLE_BASED:
			List<ATableBasedDataDomain> dataDomains = new ArrayList<>(DataDomainManager.get().getDataDomainsByType(ATableBasedDataDomain.class));

			for (Iterator<ATableBasedDataDomain> it = dataDomains.iterator(); it.hasNext();)
				if (!it.next().getTable().isDataHomogeneous()) // remove inhomogenous
					it.remove();

			// Sort data domains alphabetically
			Collections.sort(dataDomains, new Comparator<ADataDomain>() {
				@Override
				public int compare(ADataDomain dd1, ADataDomain dd2) {
					return dd1.getLabel().compareTo(dd2.getLabel());
				}
			});
			return dataDomains;
		case GENE_SET:
			return Lists.newArrayList(DataDomainManager.get().getDataDomainsByType(PathwayDataDomain.class));
		}
		throw new IllegalArgumentException("unknown me");
	}

	public Predicate<IScore> isSupportedBy() {
		return new Predicate<IScore>() {
			@Override
			public boolean apply(IScore s) {
				return s.supports(EDataDomainQueryMode.this);
			}
		};
	}
}
