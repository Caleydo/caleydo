/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.vis.lineup.model.mixin;

import org.caleydo.vis.lineup.model.IRow;

import com.google.common.base.Function;

/**
 * @author Samuel Gratzl
 *
 */
public interface IDataBasedColumnMixin {
	Function<IRow, ?> getData();
}
