/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.model.mixin;

import org.caleydo.vis.lineup.data.IFloatFunction;
import org.caleydo.vis.lineup.model.IRow;
import org.caleydo.vis.lineup.model.SimpleHistogram;


/**
 * contract that this column can be used to rank a table
 *
 * @author Samuel Gratzl
 *
 */
public interface IFloatRankableColumnMixin extends IRankableColumnMixin, IFloatFunction<IRow> {

	boolean isValueInferred(IRow row);

	/**
	 * returns a summary of the current filtered data as a simple histogram
	 * 
	 * @param width
	 *            the target with in pixels
	 * @return
	 */
	SimpleHistogram getHist(float width);


}
