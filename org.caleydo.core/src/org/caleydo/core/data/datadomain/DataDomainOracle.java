/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
import org.caleydo.core.util.base.ILabelProvider;

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

	public synchronized static void createRowCategoricalPerspective(ATableBasedDataDomain dataDomain, Integer id,
			boolean isPrivate) {
		if (!isCategoricalDataDomain(dataDomain))
			return;
		IDType rowIDType;
		if (dataDomain.isColumnDimension()) {
			rowIDType = dataDomain.getRecordIDType();
		} else {
			rowIDType = dataDomain.getDimensionIDType();
		}
		perspectiveCreator.createTablePerspeciveByRowID(dataDomain, id, rowIDType, isPrivate);
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

	public static class ClinicalVariable implements ILabelProvider {
		private final String label;
		private final int dimId;
		private final EDataClass dataClass;

		public ClinicalVariable(String label, int dimId, EDataClass dataClass) {
			this.label = label;
			this.dimId = dimId;
			this.dataClass = dataClass;
		}

		/**
		 * @return the label, see {@link #label}
		 */
		@Override
		public String getLabel() {
			return label;
		}

		@Override
		public String getProviderName() {
			return null;
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
