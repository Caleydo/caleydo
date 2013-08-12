/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.heatmap.v2.internal;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.util.color.Color;
import org.caleydo.view.heatmap.v2.IBlockColorer;

/**
 * @author Samuel Gratzl
 *
 */
public final class ConstantBlockColorer implements IBlockColorer {
	public static final ConstantBlockColorer NEUTRAL_GREY = new ConstantBlockColorer(Color.NEUTRAL_GREY);

	private final Color color;

	public ConstantBlockColorer(Color color) {
		this.color = color;
	}

	@Override
	public Color apply(int recordID, int dimensionID, ATableBasedDataDomain dataDomain, boolean deSelected) {
		return color;
	}

}
