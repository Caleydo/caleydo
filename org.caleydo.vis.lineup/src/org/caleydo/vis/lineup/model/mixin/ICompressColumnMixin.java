/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.model.mixin;

/**
 * contract that the column can be collapsed
 *
 * @author Samuel Gratzl
 *
 */
public interface ICompressColumnMixin extends ICollapseableColumnMixin {
	String PROP_COMPRESSED = "compressed";

	boolean isCompressed();

	ICompressColumnMixin setCompressed(boolean compressed);

}
