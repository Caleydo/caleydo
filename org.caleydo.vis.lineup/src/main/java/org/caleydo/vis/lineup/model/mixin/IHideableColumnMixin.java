/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.model.mixin;


/**
 * contract that the column can be hidden
 *
 * @author Samuel Gratzl
 *
 */
public interface IHideableColumnMixin extends IRankColumnModel {
	/**
	 * currently hide able in its state
	 *
	 * @return
	 */
	boolean isHideAble();

	/**
	 * triggers to hide this column
	 *
	 * @return successful?
	 */
	boolean hide();

	boolean isHidden();

	/**
	 * currently destroy able in its state
	 *
	 * @return
	 */
	boolean isDestroyAble();

	/**
	 * triggers to destroy this column
	 *
	 * @return successful?
	 */
	boolean destroy();
}
