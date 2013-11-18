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
 * @author Christian
 *
 */
public class ContinuousDataRenderer extends AColumnBasedDataRenderer {

	/**
	 * @param contentRenderer
	 */
	public ContinuousDataRenderer(ContentRenderer contentRenderer) {
		super(contentRenderer);
		registerPickingListeners();
	}

	@Override
	protected void renderColumnBar(GL2 gl, int columnID, float x, float y, List<SelectionType> selectionTypes,
			boolean useShading) {
		Float value = contentRenderer.dataDomain.getNormalizedValue(contentRenderer.resolvedRowIDType,
				contentRenderer.resolvedRowID, contentRenderer.resolvedColumnIDType, columnID);

		renderSingleBar(gl, 0, 0, y * value, x, selectionTypes, MappedDataRenderer.BAR_COLOR.getRGBA(), columnID,
				useShading);

	}
}