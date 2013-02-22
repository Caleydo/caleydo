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
import java.util.List;

import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.perspective.table.CategoricalTablePerspectiveCreator;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IIDTypeMapper;

/**
 * helper class for encapsulating magic meta data information about a data domain
 *
 * @author Samuel Gratzl
 *
 */
public final class DataDomainOracle {
	private final static CategoricalTablePerspectiveCreator perspectiveCreator = new CategoricalTablePerspectiveCreator();

	public static boolean isCategoricalDataDomain(IDataDomain dataDomain) {
		return DataSupportDefinitions.categoricalTables.apply(dataDomain);
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

	/**
	 * returns a list of clinical variables that are natural numbers
	 *
	 * @return pair with id,label
	 */
	public static Collection<ClinicalVariable> getClinicalVariables() {
		ATableBasedDataDomain clinical = getClinicalDataDomain();
		if (clinical == null)
			return Collections.emptyList();
		List<Integer> va = clinical.getTable().getColumnIDList();
		IDType dimId = clinical.getDimensionIDType();
		Integer row = clinical.getTable().getRowIDList().get(0);
		IDMappingManager manager = clinical.getDimensionIDMappingManager();
		IIDTypeMapper<Integer, String> mapper = manager.getIDTypeMapper(dimId, dimId.getIDCategory()
				.getHumanReadableIDType());

		Collection<ClinicalVariable> result = new ArrayList<>();
		for(Integer id : va) {
			EDataClass dataClass = clinical.getTable().getDataClass(id, row);
			result.add(new ClinicalVariable(mapper.apply(id).iterator().next(), id, dataClass));
		}
		return result;
	}

	public static class ClinicalVariable {
		private final String label;
		private final int dimId;
		private final EDataClass dataClass;

		public ClinicalVariable(String label, int dimId, EDataClass dataClass) {
			super();
			this.label = label;
			this.dimId = dimId;
			this.dataClass = dataClass;
		}

		/**
		 * @return the label, see {@link #label}
		 */
		public String getLabel() {
			return label;
		}

		/**
		 * @return the dimId, see {@link #dimId}
		 */
		public int getDimId() {
			return dimId;
		}

		/**
		 * @return the dataClass, see {@link #dataClass}
		 */
		public EDataClass getDataClass() {
			return dataClass;
		}

	}
}
