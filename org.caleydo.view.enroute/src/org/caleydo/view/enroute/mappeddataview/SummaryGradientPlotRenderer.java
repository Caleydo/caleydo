/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.mappeddataview;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.util.collection.Algorithms;
import org.caleydo.view.enroute.EPickingType;

/**
 * @author Christian
 *
 */
public class SummaryGradientPlotRenderer extends AAverageBasedSummaryRenderer {

	protected float normalizedMin = 0;
	protected float normalizedMax = 0;

	/**
	 * @param contentRenderer
	 */
	public SummaryGradientPlotRenderer(ContentRenderer contentRenderer) {
		super(contentRenderer);
		VirtualArray va = contentRenderer.columnPerspective.getVirtualArray();

		normalizedMin = Float.MAX_VALUE;
		normalizedMax = Float.MIN_VALUE;
		for (Integer id : va) {
			Float value = contentRenderer.dataDomain.getNormalizedValue(contentRenderer.resolvedRowIDType,
					contentRenderer.resolvedRowID, contentRenderer.resolvedColumnIDType, id);
			if (value < normalizedMin) {
				normalizedMin = value;
			}
			if (value > normalizedMax) {
				normalizedMax = value;
			}
		}
	}

	@Override
	public void render(GL2 gl, float x, float y, List<SelectionType> selectionTypes) {
		// if (contentRenderer.isHighlightMode)
		// return;

		colorCalculator.setBaseColor(MappedDataRenderer.SUMMARY_BAR_COLOR);

		List<List<SelectionType>> selectionLists = new ArrayList<List<SelectionType>>();
		selectionLists.add(selectionTypes);

		colorCalculator.calculateColors(Algorithms.mergeListsToUniqueList(selectionLists));
		float[] color = colorCalculator.getSecondaryColor().getRGBA();
		// float[] color = MappedDataRenderer.SUMMARY_BAR_COLOR.getRGBA();

		gl.glPushName(contentRenderer.parentView.getPickingManager().getPickingID(contentRenderer.parentView.getID(),
				EPickingType.SAMPLE_GROUP_RENDERER.name(), rendererID));

		float xMinusDeviation = (float) (average.getArithmeticMean() - average.getStandardDeviation()) * x;
		float xPlusDeviation = (float) (average.getArithmeticMean() + average.getStandardDeviation()) * x;

		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		gl.glBegin(GL2GL3.GL_QUADS);
		gl.glColor4fv(color, 0);
		gl.glVertex3f(xMinusDeviation, y / 3, z);
		gl.glVertex3d(xPlusDeviation, y / 3, z);
		gl.glVertex3d(xPlusDeviation, y / 3 * 2, z);
		gl.glVertex3f(xMinusDeviation, y / 3 * 2, z);
		gl.glEnd();

		// Gradients
		float min = normalizedMin * x;
		float max = normalizedMax * x;
		// float min = (float) (average.getArithmeticMean() - (3 * average.getStandardDeviation())) * x;
		// float max = (float) (average.getArithmeticMean() + (3 * average.getStandardDeviation())) * x;

		gl.glBegin(GL2GL3.GL_QUADS);
		gl.glColor4fv(color, 0);
		gl.glVertex3d(xMinusDeviation, y / 3, z);
		gl.glVertex3d(xMinusDeviation, y / 3 * 2, z);

		gl.glColor4f(color[0], color[1], color[2], 0f);
		gl.glVertex3f(min, y / 3 * 2, z);
		gl.glVertex3f(min, y / 3, z);
		gl.glEnd();

		gl.glBegin(GL2GL3.GL_QUADS);
		gl.glColor4fv(color, 0);
		gl.glVertex3d(xPlusDeviation, y / 3, z);
		gl.glVertex3d(xPlusDeviation, y / 3 * 2, z);

		gl.glColor4f(color[0], color[1], color[2], 0f);
		gl.glVertex3f(max, y / 3 * 2, z);
		gl.glVertex3f(max, y / 3, z);
		gl.glEnd();
		gl.glPopName();
	}

}
