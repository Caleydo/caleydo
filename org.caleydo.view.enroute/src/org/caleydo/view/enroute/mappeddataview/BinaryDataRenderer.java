/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.mappeddataview;

import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.util.collection.Algorithms;
import org.caleydo.core.util.color.Color;
import org.caleydo.view.enroute.EPickingType;

/**
 * @author Christian
 *
 */
public class BinaryDataRenderer extends AColumnBasedDataRenderer {

	/**
	 * @param contentRenderer
	 */
	public BinaryDataRenderer(ContentRenderer contentRenderer) {
		super(contentRenderer);
	}

	@Override
	protected void renderColumnBar(GL2 gl, int columnID, float x, float y, List<SelectionType> selectionTypes,
			boolean useShading) {

		Float value = contentRenderer.dataDomain.getNormalizedValue(contentRenderer.resolvedRowIDType,
				contentRenderer.resolvedRowID, contentRenderer.resolvedColumnIDType, columnID);

		List<SelectionType> experimentSelectionTypes = contentRenderer.parent.sampleSelectionManager.getSelectionTypes(
				contentRenderer.columnIDType, columnID);

		float[] mappedColor = contentRenderer.dataDomain.getTable().getColorMapper().getColor(value);
		float[] baseColor = new float[] { mappedColor[0], mappedColor[1], mappedColor[2], 1f };

		float[] topBarColor = baseColor;
		float[] bottomBarColor = baseColor;

		@SuppressWarnings("unchecked")
		List<SelectionType> sTypes = Algorithms.mergeListsToUniqueList(experimentSelectionTypes, selectionTypes);

		if (contentRenderer.isHighlightMode
				&& !(sTypes.contains(SelectionType.MOUSE_OVER) || sTypes.contains(SelectionType.SELECTION))) {
			return;
		}

		if (contentRenderer.isHighlightMode) {
			colorCalculator.setBaseColor(new Color(baseColor));

			colorCalculator.calculateColors(sTypes);

			topBarColor = colorCalculator.getPrimaryColor().getRGBA();
			bottomBarColor = colorCalculator.getSecondaryColor().getRGBA();
		}

		float upperEdge = value * y;

		// gl.glPushName(parentView.getPickingManager().getPickingID(
		// parentView.getID(), PickingType.ROW_PRIMARY.name(), rowID));

		Integer resolvedSampleID = contentRenderer.columnIDMappingManager.getID(contentRenderer.resolvedColumnIDType,
				contentRenderer.parent.sampleIDType, columnID);
		if (resolvedSampleID != null) {
			gl.glPushName(contentRenderer.parentView.getPickingManager().getPickingID(
					contentRenderer.parentView.getID(), EPickingType.SAMPLE.name(), resolvedSampleID));
		}

		gl.glBegin(GL2GL3.GL_QUADS);

		gl.glColor4fv(bottomBarColor, 0);
		gl.glVertex3f(0, 0, z);
		if (useShading) {
			gl.glColor3f(bottomBarColor[0] * 0.9f, bottomBarColor[1] * 0.9f, bottomBarColor[2] * 0.9f);

		}
		gl.glVertex3f(x, 0, z);
		if (useShading) {
			gl.glColor3f(topBarColor[0] * 0.9f, topBarColor[1] * 0.9f, topBarColor[2] * 0.9f);
		} else {
			gl.glColor4fv(topBarColor, 0);
		}

		gl.glVertex3f(x, upperEdge, z);
		gl.glColor4fv(topBarColor, 0);

		gl.glVertex3f(0, upperEdge, z);

		gl.glEnd();
		if (resolvedSampleID != null)
			gl.glPopName();

	}

}
