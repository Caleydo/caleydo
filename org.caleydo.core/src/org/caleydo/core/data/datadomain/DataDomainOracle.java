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
package org.caleydo.core.data.datadomain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.caleydo.core.data.perspective.table.CategoricalTablePerspectiveCreator;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IIDTypeMapper;
import org.caleydo.core.util.collection.Pair;

/**
 * helper class for encapsulating magic meta data information about a data domain
 *
 * @author Samuel Gratzl
 *
 */
public final class DataDomainOracle {
	private final static CategoricalTablePerspectiveCreator perspectiveCreator = new CategoricalTablePerspectiveCreator();

	public static boolean isCategoricalDataDomain(IDataDomain dataDomain) {
		if (dataDomain == null)
			return false;
		String label = dataDomain.getLabel();
		if (dataDomain.getLabel() == null)
			return false;
		return label.toLowerCase().contains("mutation") || label.toLowerCase().contains("copy");
	}

	public synchronized static void initDataDomain(ATableBasedDataDomain dataDomain) {
		if (isCategoricalDataDomain(dataDomain))
			perspectiveCreator.createAllTablePerspectives(dataDomain);
	}

	public static boolean isClinical(IDataDomain dataDomain) {
		return dataDomain.getLabel().toLowerCase().equals("clinical");
	}

	public static ATableBasedDataDomain getClinicalDataDomain() {
		for(ATableBasedDataDomain dd : DataDomainManager.get().getDataDomainsByType(ATableBasedDataDomain.class))
			if (isClinical(dd))
				return dd;
		return null;
	}

	public static Collection<Pair<Integer,String>> getClinicalVariables() {
		ATableBasedDataDomain clinical = getClinicalDataDomain();
		if (clinical == null)
			return Collections.emptyList();
		DimensionVirtualArray va = clinical.getTable().getDefaultDimensionPerspective().getVirtualArray();
		IDMappingManager manager = IDMappingManagerRegistry.get().getIDMappingManager(va.getIdType());
		IIDTypeMapper<Integer,String> mapper = manager.getIDTypeMapper(va.getIdType(), va.getIdType().getIDCategory().getHumanReadableIDType());

		Collection<Pair<Integer,String>> result = new ArrayList<>();
		for(Integer id : va) {
			result.add(Pair.make(id, mapper.apply(id).iterator().next()));
		}
		return result;
	}
}
