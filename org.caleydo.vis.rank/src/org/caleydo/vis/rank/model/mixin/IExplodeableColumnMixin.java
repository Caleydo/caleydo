/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.model.mixin;

/**
 * contract that the column can be exploded, i.e. splitted in individual columns
 * 
 * @author Samuel Gratzl
 * 
 */
public interface IExplodeableColumnMixin extends IRankColumnModel {
	void explode();
}

