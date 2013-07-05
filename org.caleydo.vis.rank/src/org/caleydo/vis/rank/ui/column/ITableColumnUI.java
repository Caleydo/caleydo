/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.ui.column;

import gleem.linalg.Vec4f;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.vis.rank.model.ARankColumnModel;

/**
 * @author Samuel Gratzl
 *
 */
public interface ITableColumnUI {
	ARankColumnModel getModel();

	void relayout();

	GLElement asGLElement();

	/**
	 * @param rowIndex
	 * @return
	 */
	Vec4f getBounds(int rowIndex);
}
