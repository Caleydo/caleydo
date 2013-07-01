/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.datadomain;

import org.caleydo.core.data.collection.table.CategoricalTable;
import org.caleydo.core.data.collection.table.NumericalTable;

/**
 * factory class for {@link IDataSupportDefinition}s
 *
 * @author Samuel Gratzl
 *
 */
public final class DataSupportDefinitions {
	private DataSupportDefinitions() {

	}
	/**
	 * Support definition for views that can handle (general) tabular data
	 */
	public static final IDataSupportDefinition tableBased = new ADataSupportDefinition() {
		@Override
		public boolean apply(IDataDomain dataDomain) {
			return dataDomain instanceof ATableBasedDataDomain;
		}
	};

	public static final IDataSupportDefinition homogenousTables = new ADataSupportDefinition() {
		@Override
		public boolean apply(IDataDomain dataDomain) {
			return (dataDomain instanceof ATableBasedDataDomain)
					&& ((ATableBasedDataDomain) dataDomain).getTable().isDataHomogeneous();
		}
	};

	public static final IDataSupportDefinition numericalTables = new ADataSupportDefinition() {
		@Override
		public boolean apply(IDataDomain dataDomain) {
			return (dataDomain instanceof ATableBasedDataDomain)
					&& (((ATableBasedDataDomain) dataDomain).getTable() instanceof NumericalTable);
		}
	};

	public static final IDataSupportDefinition categoricalTables = new ADataSupportDefinition() {
		@Override
		public boolean apply(IDataDomain dataDomain) {
			return (dataDomain instanceof ATableBasedDataDomain)
					&& (((ATableBasedDataDomain) dataDomain).getTable() instanceof CategoricalTable);
		}
	};

	/**
	 * Default definition that can be used if all {@link DataDomain}s are supported.
	 */
	public static final IDataSupportDefinition all = new ADataSupportDefinition() {
		@Override
		public boolean apply(IDataDomain dataDomain) {
			return true;
		}
	};

	public static IDataSupportDefinition not(final IDataSupportDefinition toNegate) {
		return new ADataSupportDefinition() {
			@Override
			public boolean apply(IDataDomain dataDomain) {
				return !toNegate.apply(dataDomain);
			}
		};
	}


}
