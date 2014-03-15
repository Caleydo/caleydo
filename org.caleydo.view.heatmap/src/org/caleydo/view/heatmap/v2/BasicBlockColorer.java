/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap.v2;

import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.function.Function2;

/**
 * basic {@link IBlockColorer} implementation
 *
 * @author Samuel Gratzl
 *
 */
public class BasicBlockColorer implements Function2<Integer, Integer, Color> {
	private final Table table;

	public BasicBlockColorer(ATableBasedDataDomain dataDomain) {
		this(dataDomain.getTable());
	}

	public BasicBlockColorer(Table table) {
		this.table = table;
	}

	@Override
	public Color apply(Integer recordID, Integer dimensionID) {
		if (isInvalid(recordID) || isInvalid(dimensionID))
			return Color.NOT_A_NUMBER_COLOR;
		// get value
		float[] color = table.getColor(dimensionID, recordID);
		// to a color
		return new Color(color[0], color[1], color[2], 1.0f);
	}

	/**
	 * @param recordID
	 * @return
	 */
	private static boolean isInvalid(Integer id) {
		return id == null || id.intValue() < 0;
	}

	/**
	 * @param dataDomain
	 * @return
	 */
	public static Function2<Integer, Integer, Float> toRaw(final ATableBasedDataDomain dataDomain) {
		final Table table = dataDomain.getTable();
		return new Function2<Integer, Integer, Float>() {
			@Override
			public Float apply(Integer recordID, Integer dimensionID) {
				if (isInvalid(recordID) || isInvalid(dimensionID))
					return Float.NaN;
				// get value
				Object v = table.getRaw(dimensionID, recordID);
				// to a color
				return v instanceof Number ? ((Number)v).floatValue() : Float.NaN;
			}
		};
	}

}
