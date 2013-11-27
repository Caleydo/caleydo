/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.mappeddataview;

import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.column.container.CategoryProperty;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.io.DataDescription;
import org.caleydo.core.io.NumericalProperties;

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

		DataDescription dataDescription = contentRenderer.dataDomain.getDataSetDescription().getDataDescription();
		// CategoricalClassDescription<?> categoryDescription = null;
		float[] color = null;

		// inhomogeneous
		if (dataDescription == null) {
			Object dataClassDesc = null;
			if (contentRenderer.columnIDType.getIDCategory() == contentRenderer.dataDomain.getColumnIDCategory()) {
				dataClassDesc = contentRenderer.dataDomain.getTable().getDataClassSpecificDescription(columnID,
						contentRenderer.rowID);
			} else {
				dataClassDesc = contentRenderer.dataDomain.getTable().getDataClassSpecificDescription(
						contentRenderer.rowID, columnID);
			}

			if (dataClassDesc instanceof NumericalProperties) {
				color = getBarColorFromNumericValue(columnID);
			} else {
				color = getBarColorFromCategory((CategoricalClassDescription<?>) dataClassDesc, columnID);
			}
		} else if (dataDescription.getNumericalProperties() != null) {
			color = getBarColorFromNumericValue(columnID);
		} else {
			color = getBarColorFromCategory(dataDescription.getCategoricalClassDescription(), columnID);
		}

		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		renderSingleBar(gl, 0, 0, y, x, selectionTypes, color, columnID, useShading);

	}

	private float[] getBarColorFromCategory(CategoricalClassDescription<?> categoryDescription, int columnID) {
		CategoryProperty<?> property = categoryDescription.getCategoryProperty(contentRenderer.dataDomain.getRaw(
				contentRenderer.resolvedColumnIDType, columnID, contentRenderer.resolvedRowIDType,
				contentRenderer.resolvedRowID));
		if (property == null)
			return new float[] { 1, 1, 1, 0f };
		return property.getColor().getRGBA();
	}

	private float[] getBarColorFromNumericValue(int columnID) {
		Float value = contentRenderer.dataDomain.getNormalizedValue(contentRenderer.resolvedRowIDType,
				contentRenderer.resolvedRowID, contentRenderer.resolvedColumnIDType, columnID);

		float[] mappedColor = contentRenderer.dataDomain.getTable().getColorMapper().getColor(value);
		return new float[] { mappedColor[0], mappedColor[1], mappedColor[2], 1f };
	}
}
