/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.datadomain;

import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.collection.table.CategoricalTable;
import org.caleydo.core.data.collection.table.NumericalTable;
import org.caleydo.core.data.perspective.table.TablePerspective;

import com.google.common.base.Predicate;

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
	 * returns true if the datadomains is homogeneous or it is just a single column of an inhomogenous data domain
	 */
	public static final Predicate<TablePerspective> homogenousColumns = new Predicate<TablePerspective>() {
		@Override
		public boolean apply(TablePerspective in) {
			return in != null
 && (homogenousTables.apply(in) || getSingleColumnDataClass(in) != null);
		}
	};

	public static final Predicate<TablePerspective> numericalColumns = new Predicate<TablePerspective>() {
		@Override
		public boolean apply(TablePerspective in) {
			return in != null && (numericalTables.apply(in) || getSingleColumnDataClass(in) == EDataClass.REAL_NUMBER);
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

	/**
	 * @param in
	 * @return
	 */
	protected static EDataClass getSingleColumnDataClass(TablePerspective in) {
		if (in.getDimensionPerspective().getVirtualArray().size() > 1)
			return null; // multi column
		int dimensionId = in.getDimensionPerspective().getVirtualArray().get(0);
		int recordId = in.getRecordPerspective().getVirtualArray().get(0);
		return in.getDataDomain().getTable().getDataClass(dimensionId, recordId);
	}

}
