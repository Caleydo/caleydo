/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.model.mixin;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;

/**
 * contract that the column can search for an item
 * 
 * @author Samuel Gratzl
 * 
 */
public interface ISearchableColumnMixin extends IFilterColumnMixin {
	void openSearchDialog(GLElement summary, IGLElementContext context);
}
