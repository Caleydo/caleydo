/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.data.perspective.table;

import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.collection.table.NumericalTable;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.util.function.IDoubleList;

/**
 * @author Samuel Gratzl
 *
 */
public class TableDoubleLists {
	public static IDoubleList asNormalizedList(TablePerspective t) {
		return new ATablePerspectiveDoubleList(t) {
			@Override
			protected double getValue(Integer dimensionID, Integer recordID) {
				return table.getNormalizedValue(dimensionID, recordID);
			}
		};
	}

	public static IDoubleList asRawList(TablePerspective t) {
		assert DataSupportDefinitions.dataClass(EDataClass.REAL_NUMBER).apply(t);
		return new ATablePerspectiveDoubleList(t) {
			@Override
			protected double getValue(Integer dimensionID, Integer recordID) {
				Number r = table.getRaw(dimensionID, recordID);
				return r == null ? Double.NaN : r.doubleValue();
			}
		};
	}

	public static IDoubleList asNormalizedList(Table t) {
		return asNormalizedList(t.getDataDomain().getDefaultTablePerspective());
	}

	public static IDoubleList asRawList(NumericalTable t) {
		return asRawList(t.getDataDomain().getDefaultTablePerspective());
	}
}
