/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.ui;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.Column.VAlign;

/**
 * @author Samuel Gratzl
 *
 */
public interface IColumnRenderInfo {
	boolean isCollapsed();

	VAlign getAlignment();

	boolean hasFreeSpace();

	/**
	 * returns the color to use for bar outlines or null if no outline
	 * 
	 * @return
	 */
	Color getBarOutlineColor();
}
