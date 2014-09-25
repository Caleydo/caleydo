/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.model.mixin;

import java.util.Comparator;

import org.caleydo.core.util.color.Color;
import org.caleydo.vis.lineup.model.ColumnRanker;
import org.caleydo.vis.lineup.model.IRow;


/**
 * contract that this column can be used to rank a table
 *
 * @author Samuel Gratzl
 *
 */
public interface IRankableColumnMixin extends IRankColumnModel, Comparator<IRow> {
	Color getBgColor();

	Color getColor();

	void orderByMe();

	ColumnRanker getMyRanker();
}
