/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.view.enroute.mappeddataview;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;

import org.caleydo.core.data.perspective.table.Average;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.table.TablePerspectiveStatistics;
import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.util.collection.Algorithms;

/**
 * Preview renderer for continuous content.
 *
 * @author Christian
 *
 */
public class ContinuousContentPreviewRenderer extends AContentPreviewRenderer {

	private Average average;

	/**
	 * @param initializer
	 */
	public ContinuousContentPreviewRenderer(int rowID, TablePerspective tablePerspective,
			EventBasedSelectionManager geneSelectionManager, EventBasedSelectionManager sampleSelectionManager) {
		super(rowID, tablePerspective, geneSelectionManager, sampleSelectionManager);
		colorCalculator.setBaseColor(MappedDataRenderer.SUMMARY_BAR_COLOR);
		if (resolvedRowID == null)
			return;
		average = TablePerspectiveStatistics.calculateAverage(experimentPerspective.getVirtualArray(), dataDomain,
				resolvedRowIDType, resolvedRowID);

	}

	@Override
	public void renderContent(GL2 gl) {
		if (resolvedRowID == null || average == null)
			return;
		List<SelectionType> geneSelectionTypes = geneSelectionManager.getSelectionTypes(rowID);
		// ArrayList<SelectionType> selectionTypes = parent.sampleGroupSelectionManager
		// .getSelectionTypes(group.getID());
		// if (selectionTypes.size() > 0
		// && selectionTypes.contains(MappedDataRenderer.abstractGroupType)) {
		// topBarColor = MappedDataRenderer.SUMMARY_BAR_COLOR;
		// bottomBarColor = topBarColor;
		// }

		List<List<SelectionType>> selectionLists = new ArrayList<List<SelectionType>>();
		selectionLists.add(geneSelectionTypes);

		for (Integer sampleID : experimentPerspective.getVirtualArray()) {
			// Integer resolvedSampleID = columnIDMappingManager.getID(
			// dataDomain.getSampleIDType(), parent.sampleIDType,
			// experimentID);

			selectionLists.add(sampleSelectionManager.getSelectionTypes(experimentPerspective.getIdType(), sampleID));
		}

		colorCalculator.calculateColors(Algorithms.mergeListsToUniqueList(selectionLists));
		float[] topBarColor = colorCalculator.getPrimaryColor().getRGBA();
		float[] bottomBarColor = colorCalculator.getSecondaryColor().getRGBA();
		// gl.glPushName(parentView.getPickingManager().getPickingID(parentView.getID(),
		// PickingType.SAMPLE_GROUP_RENDERER.name(), rendererID));

		float[] color = MappedDataRenderer.EVEN_BACKGROUND_COLOR;

		gl.glBegin(GL2GL3.GL_QUADS);
		gl.glColor4fv(color, 0);
		gl.glVertex3f(0, 0, z);
		// gl.glColor3f(color[0] * 1.1f, color[1] * 1.1f, color[2] * 1.1f);
		gl.glVertex3f(x, 0, z);
		// gl.glColor4fv(color, 0);
		gl.glVertex3d(x, 0.8f * y, z);
		// gl.glColor3f(color[0] * 1.1f, color[1] * 1.1f, color[2] * 1.1f);
		gl.glVertex3d(0, 0.8f * y, z);
		gl.glEnd();

		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(0, 0, z);
		// gl.glColor3f(color[0] * 1.1f, color[1] * 1.1f, color[2] * 1.1f);
		gl.glVertex3f(x, 0, z);
		// gl.glColor4fv(color, 0);
		gl.glVertex3d(x, 0.8f * y, z);
		// gl.glColor3f(color[0] * 1.1f, color[1] * 1.1f, color[2] * 1.1f);
		gl.glVertex3d(0, 0.8f * y, z);
		gl.glEnd();

		gl.glBegin(GL2GL3.GL_QUADS);
		gl.glColor4fv(topBarColor, 0);
		gl.glVertex3f(0, 0, z);
		gl.glColor4fv(bottomBarColor, 0);
		gl.glVertex3f(x, 0, z);
		gl.glColor3f(bottomBarColor[0] * 0.8f, bottomBarColor[1] * 0.8f, bottomBarColor[2] * 0.8f);
		gl.glVertex3d(x, average.getArithmeticMean() * 0.8f * y, z);
		gl.glColor3f(topBarColor[0] * 0.8f, topBarColor[1] * 0.8f, topBarColor[2] * 0.8f);
		gl.glVertex3d(0, average.getArithmeticMean() * 0.8f * y, z);
		gl.glEnd();

		gl.glColor3f(0.2f, 0.2f, 0.2f);
		gl.glLineWidth(0.1f);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(0, 0, z);
		gl.glVertex3f(x, 0, z);
		gl.glVertex3d(x, average.getArithmeticMean() * 0.8f * y, z);
		gl.glVertex3d(0, average.getArithmeticMean() * 0.8f * y, z);
		gl.glEnd();

		// float lineZ = z + 0.01f;

		// gl.glColor3f(0, 0, 0);
		// // gl.glColor3f(1 , 1, 1);
		//
		// gl.glLineWidth(0.8f);
		//
		// float xMinusDeviation = (float) (average.getArithmeticMean() -
		// average
		// .getStandardDeviation()) * x;
		// float xPlusDeviation = (float) (average.getArithmeticMean() + average
		// .getStandardDeviation()) * x;
		//
		// float lineTailHeight = parentView.getPixelGLConverter()
		// .getGLHeightForPixelHeight(3);
		//
		// gl.glBegin(GL.GL_LINES);
		// gl.glVertex3f(xMinusDeviation, y / 2, lineZ);
		// gl.glVertex3f(xPlusDeviation, y / 2, lineZ);
		//
		// gl.glLineWidth(0.6f);
		//
		// gl.glVertex3f(xPlusDeviation, y / 2 - lineTailHeight, lineZ);
		// gl.glVertex3f(xPlusDeviation, y / 2 + lineTailHeight, lineZ);
		//
		// gl.glVertex3f(xMinusDeviation, y / 2 - lineTailHeight, lineZ);
		// gl.glVertex3f(xMinusDeviation, y / 2 + lineTailHeight, lineZ);
		//
		// gl.glEnd();
		// gl.glPopName();
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}

}
