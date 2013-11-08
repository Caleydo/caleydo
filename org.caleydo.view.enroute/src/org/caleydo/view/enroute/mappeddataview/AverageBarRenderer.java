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

import org.caleydo.core.data.perspective.table.Average;
import org.caleydo.core.data.perspective.table.TablePerspectiveStatistics;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.util.collection.Algorithms;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.enroute.EPickingType;

/**
 * @author Christian
 *
 */
public class AverageBarRenderer extends ADataRenderer {

	private static Integer rendererIDCounter = 0;

	protected Average average;
	private int rendererID;

	/**
	 * @param contentRenderer
	 */
	public AverageBarRenderer(ContentRenderer contentRenderer) {
		super(contentRenderer);
		if (contentRenderer.resolvedRowID == null)
			return;
		synchronized (rendererIDCounter) {
			rendererID = rendererIDCounter++;
		}
		average = TablePerspectiveStatistics.calculateAverage(contentRenderer.columnPerspective.getVirtualArray(),
				contentRenderer.dataDomain, contentRenderer.resolvedRowIDType, contentRenderer.resolvedRowID);

		registerPickingListeners();
	}

	@Override
	public void render(GL2 gl, float x, float y, List<SelectionType> selectionTypes) {
		if (average == null)
			return;

		colorCalculator.setBaseColor(MappedDataRenderer.SUMMARY_BAR_COLOR);

		List<List<SelectionType>> selectionLists = new ArrayList<List<SelectionType>>();
		selectionLists.add(selectionTypes);

		colorCalculator.calculateColors(Algorithms.mergeListsToUniqueList(selectionLists));
		float[] topBarColor = colorCalculator.getPrimaryColor().getRGBA();
		float[] bottomBarColor = colorCalculator.getSecondaryColor().getRGBA();

		gl.glPushName(contentRenderer.parentView.getPickingManager().getPickingID(contentRenderer.parentView.getID(),
				EPickingType.SAMPLE_GROUP_RENDERER.name(), rendererID));
		gl.glBegin(GL2GL3.GL_QUADS);
		gl.glColor4fv(bottomBarColor, 0);
		gl.glVertex3f(0, y / 3, z);
		gl.glColor3f(bottomBarColor[0] * 0.9f, bottomBarColor[1] * 0.9f, bottomBarColor[2] * 0.9f);
		gl.glVertex3d(average.getArithmeticMean() * x, y / 3, z);
		gl.glColor3f(topBarColor[0] * 0.9f, topBarColor[1] * 0.9f, topBarColor[2] * 0.9f);
		gl.glVertex3d(average.getArithmeticMean() * x, y / 3 * 2, z);
		gl.glColor4fv(topBarColor, 0);
		gl.glVertex3f(0, y / 3 * 2, z);
		gl.glEnd();

		gl.glColor3f(0, 0, 0);
		gl.glLineWidth(0.5f);
		gl.glBegin(GL.GL_LINE_STRIP);
		gl.glVertex3f(0, y / 3, z);
		gl.glVertex3d(average.getArithmeticMean() * x, y / 3, z);
		gl.glVertex3d(average.getArithmeticMean() * x, y / 3 * 2, z);
		gl.glVertex3f(0, y / 3 * 2, z);
		gl.glEnd();

		float lineZ = z + 0.01f;

		gl.glColor3f(0, 0, 0);
		// gl.glColor3f(1 , 1, 1);

		gl.glLineWidth(0.8f);

		float xMinusDeviation = (float) (average.getArithmeticMean() - average.getStandardDeviation()) * x;
		float xPlusDeviation = (float) (average.getArithmeticMean() + average.getStandardDeviation()) * x;

		float lineTailHeight = contentRenderer.parentView.getPixelGLConverter().getGLHeightForPixelHeight(3);

		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(xMinusDeviation, y / 2, lineZ);
		gl.glVertex3f(xPlusDeviation, y / 2, lineZ);

		gl.glLineWidth(0.6f);

		gl.glVertex3f(xPlusDeviation, y / 2 - lineTailHeight, lineZ);
		gl.glVertex3f(xPlusDeviation, y / 2 + lineTailHeight, lineZ);

		gl.glVertex3f(xMinusDeviation, y / 2 - lineTailHeight, lineZ);
		gl.glVertex3f(xMinusDeviation, y / 2 + lineTailHeight, lineZ);

		gl.glEnd();
		gl.glPopName();

	}

	protected void registerPickingListeners() {

		contentRenderer.parentView.addIDPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {

				contentRenderer.parent.sampleSelectionManager.clearSelection(SelectionType.SELECTION);

				contentRenderer.parent.sampleSelectionManager.addToType(SelectionType.SELECTION,
						contentRenderer.columnIDType, contentRenderer.columnPerspective.getVirtualArray().getIDs());
				contentRenderer.parent.sampleSelectionManager.triggerSelectionUpdateEvent();

				contentRenderer.parent.sampleGroupSelectionManager.clearSelection(SelectionType.SELECTION);

				contentRenderer.parent.sampleGroupSelectionManager.addToType(SelectionType.SELECTION,
						contentRenderer.group.getID());
				contentRenderer.parent.sampleGroupSelectionManager.triggerSelectionUpdateEvent();
				contentRenderer.parentView.setDisplayListDirty();
			}
		}, EPickingType.SAMPLE_GROUP_RENDERER.name(), rendererID);
	}

}
