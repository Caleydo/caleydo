/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.model.mixin;

import org.caleydo.vis.rank.model.IRow;


public interface ISetableColumnMixin extends IFloatRankableColumnMixin {
	boolean isOverriden(IRow row);

	String getOriginalValue(IRow row);
	void set(IRow row, String value);
}
