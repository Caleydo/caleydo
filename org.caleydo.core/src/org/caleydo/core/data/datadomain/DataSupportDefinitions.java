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
	public static final IDataSupportDefinition tableBased = new IDataSupportDefinition() {
		@Override
		public boolean isDataDomainSupported(IDataDomain dataDomain) {
			return dataDomain instanceof ATableBasedDataDomain;
		}
	};

	public static final IDataSupportDefinition homogenousTables = new IDataSupportDefinition() {
		@Override
		public boolean isDataDomainSupported(IDataDomain dataDomain) {
			return (dataDomain instanceof ATableBasedDataDomain)
					&& ((ATableBasedDataDomain) dataDomain).getTable().isDataHomogeneous();
		}
	};

	public static final IDataSupportDefinition numericalTables = new IDataSupportDefinition() {
		@Override
		public boolean isDataDomainSupported(IDataDomain dataDomain) {
			return (dataDomain instanceof ATableBasedDataDomain)
					&& (((ATableBasedDataDomain) dataDomain).getTable() instanceof NumericalTable);
		}
	};

	public static final IDataSupportDefinition categoricalTables = new IDataSupportDefinition() {
		@Override
		public boolean isDataDomainSupported(IDataDomain dataDomain) {
			return (dataDomain instanceof ATableBasedDataDomain)
					&& (((ATableBasedDataDomain) dataDomain).getTable() instanceof CategoricalTable);
		}
	};

	/**
	 * Default definition that can be used if all {@link DataDomain}s are supported.
	 */
	public static final IDataSupportDefinition all = new IDataSupportDefinition() {
		@Override
		public boolean isDataDomainSupported(IDataDomain dataDomain) {
			return true;
		}
	};

	public static IDataSupportDefinition not(final IDataSupportDefinition toNegate) {
		return new IDataSupportDefinition() {
			@Override
			public boolean isDataDomainSupported(IDataDomain dataDomain) {
				return !toNegate.isDataDomainSupported(dataDomain);
			}
		};
	}
}
