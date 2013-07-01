/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.model.mixin;

/**
 * contract that the column can be collapsed
 *
 * @author Samuel Gratzl
 *
 */
public interface ICollapseableColumnMixin extends IRankColumnModel {
	int COLLAPSED_WIDTH = 16;
	String PROP_COLLAPSED = "collapsed";

	boolean isCollapsed();

	/**
	 * is the column currently collapse able
	 *
	 * @return
	 */
	boolean isCollapseAble();

	IRankColumnModel setCollapsed(boolean collapsed);

}
