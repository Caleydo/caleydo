/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap.v2;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.util.color.Color;

/**
 * hook in interface for specifying the color of an item in the heatmap
 *
 * @author Samuel Gratzl
 *
 */
public interface IBlockColorer {
	/**
	 * computes the heatmap color to used for the given element
	 * 
	 * @param recordID
	 * @param dimensionID
	 * @param dataDomain
	 * @param deSelected
	 * @return
	 */
	Color apply(int recordID, int dimensionID, ATableBasedDataDomain dataDomain, boolean deSelected);
}
