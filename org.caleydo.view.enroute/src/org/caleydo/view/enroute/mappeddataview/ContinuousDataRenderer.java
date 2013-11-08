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
import org.caleydo.core.view.opengl.picking.IPickingLabelProvider;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.enroute.EPickingType;

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

		List<SelectionType> experimentSelectionTypes = contentRenderer.parent.sampleSelectionManager.getSelectionTypes(
				contentRenderer.columnIDType, columnID);

		float[] topBarColor = MappedDataRenderer.BAR_COLOR.getRGBA();
		float[] bottomBarColor = MappedDataRenderer.BAR_COLOR.getRGBA();
		// FIXME - bad hack
		if (!contentRenderer.rowIDType.getIDCategory().getCategoryName().equals("GENE")) {

			topBarColor = MappedDataRenderer.CONTEXT_BAR_COLOR.getRGBA();
			bottomBarColor = MappedDataRenderer.CONTEXT_BAR_COLOR.getRGBA();
		}

		@SuppressWarnings("unchecked")
		List<SelectionType> sTypes = Algorithms.mergeListsToUniqueList(experimentSelectionTypes, selectionTypes);

		if (contentRenderer.isHighlightMode
				&& !(sTypes.contains(SelectionType.MOUSE_OVER) || sTypes.contains(SelectionType.SELECTION))) {
			return;
		}

		if (contentRenderer.isHighlightMode) {
			colorCalculator.setBaseColor(MappedDataRenderer.BAR_COLOR);

			colorCalculator.calculateColors(sTypes);

			topBarColor = colorCalculator.getPrimaryColor().getRGBA();
			bottomBarColor = colorCalculator.getSecondaryColor().getRGBA();
		}

		float upperEdge = value * y;

		// gl.glPushName(parentView.getPickingManager().getPickingID(
		// parentView.getID(), PickingType.ROW_PRIMARY.name(), rowID));

		Integer resolvedSampleID = contentRenderer.columnIDMappingManager.getID(
				contentRenderer.dataDomain.getPrimaryIDType(contentRenderer.columnIDType),
				contentRenderer.parent.sampleIDType, columnID);
		if (resolvedSampleID != null) {
			gl.glPushName(contentRenderer.parentView.getPickingManager().getPickingID(
					contentRenderer.parentView.getID(), EPickingType.SAMPLE.name(), resolvedSampleID));
		}
		gl.glPushName(contentRenderer.parentView.getPickingManager().getPickingID(contentRenderer.parentView.getID(),
				EPickingType.SAMPLE.name() + hashCode(), columnID));

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
		gl.glPopName();

	}

	protected void registerPickingListeners() {

		contentRenderer.parentView.addTypePickingTooltipListener(new IPickingLabelProvider() {

			@Override
			public String getLabel(Pick pick) {
				return ""
						+ contentRenderer.dataDomain.getRawAsString(contentRenderer.resolvedRowIDType,
								contentRenderer.resolvedRowID, contentRenderer.resolvedColumnIDType, pick.getObjectID());
			}
		}, EPickingType.SAMPLE.name() + hashCode());

	}

}
