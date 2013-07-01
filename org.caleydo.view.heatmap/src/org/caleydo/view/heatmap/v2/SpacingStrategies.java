/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap.v2;

import org.caleydo.view.heatmap.v2.spacing.FishEyeSpacingCalculator;
import org.caleydo.view.heatmap.v2.spacing.UniformSpacingCalculator;

/**
 * factory for different ISpacingStrategys
 * 
 * @author Samuel Gratzl
 * 
 */
public class SpacingStrategies {
	public static final ISpacingStrategy UNIFORM = new UniformSpacingCalculator();

	public static final ISpacingStrategy fishEye(float minSelectionSize) {
		return new FishEyeSpacingCalculator(minSelectionSize);
	}
}
