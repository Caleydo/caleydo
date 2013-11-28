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
import org.caleydo.core.io.DataDescription;
import org.caleydo.core.io.NumericalProperties;

/**
 * @author Christian
 *
 */
public class CenteredDataRenderer extends AColumnBasedDataRenderer {

	protected boolean showCenterLineAtRowCenter;
	protected float minValue;
	protected float maxValue;
	protected float dataCenter;
	protected float spacing;
	protected float range;
	protected float normalizedCenter;

	/**
	 * @param contentRenderer
	 */
	public CenteredDataRenderer(ContentRenderer contentRenderer, boolean showCenterLineAtRowCenter, float minValue,
			float maxValue, float dataCenter) {
		super(contentRenderer);
		this.showCenterLineAtRowCenter = showCenterLineAtRowCenter;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.dataCenter = dataCenter;
		normalizedCenter = getNormalizedValue(dataCenter);
		registerPickingListeners();
	}

	protected float getNormalizedValue(float rawValue) {
		float value = (rawValue - minValue) / (maxValue - minValue);
		if (value > 1)
			return 1;
		if (value < 0)
			return 0;
		return value;
	}

	@Override
	public void render(GL2 gl, float x, float y, List<SelectionType> selectionTypes) {

		spacing = 0;
		range = y;
		// dataCenter = -1;
		// showCenterLineAtRowCenter = true;
		if (showCenterLineAtRowCenter) {
			float diffMax = maxValue - dataCenter;
			float diffMin = dataCenter - minValue;
			float totalValueRange = 0;
			if (diffMax >= diffMin) {
				totalValueRange = diffMax * 2.0f;
			} else {
				totalValueRange = diffMin * 2.0f;
			}
			float numberSpacing = diffMax - diffMin;
			if (totalValueRange != 0) {
				spacing = numberSpacing / totalValueRange * y;
			}
			range = y - Math.abs(spacing);
			if (spacing < 0)
				spacing = 0;
		}

		// float center = 0.5f;

		if (!contentRenderer.isHighlightMode) {
			gl.glColor3f(0, 0, 0);
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(0, (normalizedCenter * range) + spacing, z);
			gl.glVertex3f(x, (normalizedCenter * range) + spacing, z);
			gl.glEnd();
		}

		super.render(gl, x, y, selectionTypes);
	}

	@Override
	protected void renderColumnBar(GL2 gl, int columnID, float x, float y, List<SelectionType> selectionTypes,
			boolean useShading) {

		Float value = Float.NaN;
		Object rawValue = contentRenderer.dataDomain.getRaw(contentRenderer.resolvedRowIDType,
				contentRenderer.resolvedRowID, contentRenderer.resolvedColumnIDType, columnID);

		if (rawValue instanceof Integer) {
			value = getNormalizedValue(((Integer) rawValue).floatValue());
		} else if (rawValue instanceof Float) {
			value = getNormalizedValue(((Float) rawValue).floatValue());
		} else if (rawValue instanceof Double) {
			value = getNormalizedValue(((Double) rawValue).floatValue());
		} else {
			throw new IllegalStateException("The value " + rawValue + " is not supported.");
		}
		if (value < normalizedCenter + 0.0001 && value > normalizedCenter - 0.0001) {
			return;
		}
		DataDescription dataDescription = contentRenderer.dataDomain.getDataSetDescription().getDataDescription();
		// CategoricalClassDescription<?> categoryDescription = null;
		boolean isCategorical = false;
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
				isCategorical = false;
			} else {
				isCategorical = true;
			}
		} else if (dataDescription.getNumericalProperties() != null) {
			isCategorical = false;
		} else {
			isCategorical = true;
		}

		float[] color = isCategorical ? getMappingColorForItem(columnID) : (contentRenderer.parentView
				.isUseColorMapping() ? getMappingColorForItem(columnID) : contentRenderer.dataDomain.getColor()
				.darker().darker().getRGBA());
		renderSingleBar(gl, 0, (normalizedCenter * range) + spacing, (value - normalizedCenter) * range, x,
				selectionTypes, color, columnID, useShading);

	}
}
