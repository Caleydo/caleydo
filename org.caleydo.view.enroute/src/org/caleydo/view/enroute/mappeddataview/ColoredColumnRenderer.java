/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.mappeddataview;

import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.data.selection.SelectionType;

/**
 * @author Christian
 *
 */
public class ColoredColumnRenderer extends AColumnBasedDataRenderer {

	/**
	 * @param contentRenderer
	 */
	public ColoredColumnRenderer(ContentRenderer contentRenderer) {
		super(contentRenderer);
		registerPickingListeners();
	}

	@Override
	protected void renderColumnBar(GL2 gl, int columnID, float x, float y, List<SelectionType> selectionTypes,
			boolean useShading) {

		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		renderSingleBar(gl, 0, 0, y, x, selectionTypes, getMappingColorForItem(columnID), columnID, useShading);

	}


}
