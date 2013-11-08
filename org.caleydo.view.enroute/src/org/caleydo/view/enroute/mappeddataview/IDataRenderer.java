/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.mappeddataview;

import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.selection.SelectionType;

/**
 * Basic interface for rendering data in enroute.
 *
 * @author Christian Partl
 *
 */
public interface IDataRenderer {

	/**
	 * Renders the data of the content renderer.
	 *
	 * @param gl
	 * @param contentRenderer
	 * @param x
	 * @param y
	 */
	public void render(GL2 gl, float x, float y, List<SelectionType> selectionTypes);

}
