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
		assert DataSupportDefinitions.dataClass(EDataClass.REAL_NUMBER, EDataClass.NATURAL_NUMBER).apply(t);
		final boolean isInteger = DataSupportDefinitions.dataClass(EDataClass.NATURAL_NUMBER).apply(t);
		return new ATablePerspectiveDoubleList(t) {
			@Override
			protected double getValue(Integer dimensionID, Integer recordID) {
				Number r = table.getRaw(dimensionID, recordID);
				return isInvalid(r) ? Double.NaN : r.doubleValue();
			}

			private boolean isInvalid(Number r) {
				if (r == null)
					return true;
				// handle corner case in which integer invalid values are treated as Integer.MIN_VALUE
				if (isInteger && r instanceof Integer && r.intValue() == Integer.MIN_VALUE)
					return true;
				if ((r instanceof Double && Double.isNaN(r.doubleValue()))
						|| (r instanceof Float && Float.isNaN(r.floatValue())))
					return true;
				return false;
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
