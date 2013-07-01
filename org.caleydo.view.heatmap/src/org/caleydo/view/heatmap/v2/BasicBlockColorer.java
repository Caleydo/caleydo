/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap.v2;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.util.color.Color;

/**
 * @author Samuel Gratzl
 *
 */
public class BasicBlockColorer implements IBlockColorer {
	public static final BasicBlockColorer INSTANCE = new BasicBlockColorer();

	private BasicBlockColorer() {

	}

	@Override
	public Color apply(int recordID, int dimensionID, ATableBasedDataDomain dataDomain, boolean deSelected) {
		float value = dataDomain.getTable().getNormalizedValue(dimensionID, recordID);
		float[] color = dataDomain.getColorMapper().getColor(value);
		float opacity = deSelected ? 0.3f : 1.0f;

		return new Color(color[0], color[1], color[2], opacity);
	}
}
