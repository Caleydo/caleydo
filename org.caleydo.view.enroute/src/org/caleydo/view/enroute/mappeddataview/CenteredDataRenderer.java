/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.mappeddataview;

import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.util.collection.Algorithms;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.picking.IPickingLabelProvider;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.enroute.EPickingType;

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

	@SuppressWarnings("unchecked")
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

		List<SelectionType> experimentSelectionTypes = contentRenderer.parent.sampleSelectionManager.getSelectionTypes(
				contentRenderer.columnIDType, columnID);

		float[] baseColor = contentRenderer.dataDomain.getTable().getColorMapper().getColor(value);
		float[] topBarColor = baseColor;
		float[] bottomBarColor = baseColor;

		colorCalculator.calculateColors(Algorithms.mergeListsToUniqueList(experimentSelectionTypes, selectionTypes));

		List<SelectionType> sTypes = Algorithms.mergeListsToUniqueList(experimentSelectionTypes, selectionTypes);

		if (contentRenderer.isHighlightMode
				&& !(sTypes.contains(SelectionType.MOUSE_OVER) || sTypes.contains(SelectionType.SELECTION))) {
			return;
		}

		if (contentRenderer.isHighlightMode) {
			colorCalculator.setBaseColor(new Color(baseColor[0], baseColor[1], baseColor[2]));

			colorCalculator.calculateColors(sTypes);

			topBarColor = colorCalculator.getPrimaryColor().getRGB();
			bottomBarColor = colorCalculator.getSecondaryColor().getRGB();
		}

		float upperEdge = (value * range) + spacing;

		// gl.glPushName(parentView.getPickingManager().getPickingID(
		// parentView.getID(), PickingType.ROW_PRIMARY.name(), rowID));

		Integer resolvedColumnID = contentRenderer.columnIDMappingManager.getID(
				contentRenderer.dataDomain.getPrimaryIDType(contentRenderer.columnIDType),
				contentRenderer.parent.sampleIDType, columnID);
		gl.glPushName(contentRenderer.parentView.getPickingManager().getPickingID(contentRenderer.parentView.getID(),
				EPickingType.SAMPLE.name(), resolvedColumnID));
		gl.glPushName(contentRenderer.parentView.getPickingManager().getPickingID(contentRenderer.parentView.getID(),
				EPickingType.SAMPLE.name() + hashCode(), columnID));

		gl.glColor3fv(bottomBarColor, 0);
		gl.glBegin(GL2GL3.GL_QUADS);

		gl.glVertex3f(0, (normalizedCenter * range) + spacing, z);
		if (useShading) {
			gl.glColor3f(bottomBarColor[0] * 0.9f, bottomBarColor[1] * 0.9f, bottomBarColor[2] * 0.9f);
		}
		gl.glVertex3f(x, (normalizedCenter * range) + spacing, z);

		if (useShading) {
			gl.glColor3f(topBarColor[0] * 0.9f, topBarColor[1] * 0.9f, topBarColor[2] * 0.9f);
		} else {
			gl.glColor3fv(topBarColor, 0);
		}
		gl.glVertex3f(x, upperEdge, z);
		gl.glColor3fv(topBarColor, 0);

		gl.glVertex3f(0, upperEdge, z);

		gl.glEnd();

		gl.glPopName();
		gl.glPopName();

	}

	protected void registerPickingListeners() {
		contentRenderer.parentView.addTypePickingTooltipListener(new IPickingLabelProvider() {

			@Override
			public String getLabel(Pick pick) {
				if (contentRenderer.dataDomain.getDataSetDescription().getDataDescription()
						.getCategoricalClassDescription() != null) {
					return contentRenderer.dataDomain
							.getDataSetDescription()
							.getDataDescription()
							.getCategoricalClassDescription()
							.getCategoryProperty(
									contentRenderer.dataDomain.getRaw(contentRenderer.resolvedRowIDType,
											contentRenderer.resolvedRowID, contentRenderer.resolvedColumnIDType,
											pick.getObjectID())).getCategoryName();
				}

				return ""
						+ contentRenderer.dataDomain.getRawAsString(contentRenderer.resolvedRowIDType,
								contentRenderer.resolvedRowID, contentRenderer.resolvedColumnIDType, pick.getObjectID());
			}
		}, EPickingType.SAMPLE.name() + hashCode());
	}

}
