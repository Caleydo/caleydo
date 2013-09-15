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
import org.caleydo.core.util.base.ILabeled;

import com.google.common.collect.Collections2;

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
		return DataSupportDefinitions.inhomogenousTables.apply(dataDomain);
	}

	public static Collection<ATableBasedDataDomain> getClinicalDataDomain() {
		return Collections2.filter(DataDomainManager.get().getDataDomainsByType(ATableBasedDataDomain.class),
				DataSupportDefinitions.inhomogenousTables);
	}

	/**
	 * returns a list of clinical variables that are natural numbers
	 *
	 * @return pair with id,label
	 */
	public static Collection<ClinicalVariable> getClinicalVariables() {
		Collection<ATableBasedDataDomain> clinicals = getClinicalDataDomain();
		if (clinicals == null || clinicals.isEmpty())
			return Collections.emptyList();
		Collection<ClinicalVariable> result = new ArrayList<>();
		for (ATableBasedDataDomain clinical : clinicals) {
			List<Integer> va = clinical.getTable().getColumnIDList();
			IDType dimId = clinical.getDimensionIDType();
			Integer row = clinical.getTable().getRowIDList().get(0);
			IDMappingManager manager = clinical.getDimensionIDMappingManager();
			IIDTypeMapper<Integer, String> mapper = manager.getIDTypeMapper(dimId, dimId.getIDCategory()
					.getHumanReadableIDType());

			for (Integer id : va) {
				EDataClass dataClass = clinical.getTable().getDataClass(id, row);
				result.add(new ClinicalVariable(clinical, mapper.apply(id).iterator().next(), id, dataClass));
			}
		}
		return result;
	}

	public static class ClinicalVariable implements ILabeled {
		private final ATableBasedDataDomain dataDomain;
		private final String label;
		private final int dimId;
		private final EDataClass dataClass;

		public ClinicalVariable(ATableBasedDataDomain dataDomain, String label, int dimId, EDataClass dataClass) {
			this.dataDomain = dataDomain;
			this.label = label;
			this.dimId = dimId;
			this.dataClass = dataClass;
		}

		/**
		 * @return the dataDomain, see {@link #dataDomain}
		 */
		public ATableBasedDataDomain getDataDomain() {
			return dataDomain;
		}

		/**
		 * @return the dimId, see {@link #dimId}
		 */
		public int getDimId() {
			return dimId;
		}

		/**
		 * @return the label, see {@link #label}
		 */
		@Override
		public String getLabel() {
			return label;
		}

		/**
		 * @return the dataClass, see {@link #dataClass}
		 */
		public EDataClass getDataClass() {
			return dataClass;
		}

	}
}
