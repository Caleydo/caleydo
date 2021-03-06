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

import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.table.TablePerspectiveStatistics;
import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.util.collection.Algorithms;
import org.caleydo.core.util.color.Color;

/**
 * @author Christian
 *
 */
public class CategoricalContentPreviewRenderer extends AContentPreviewRenderer {

	private Histogram histogram;

	/**
	 * @param initializor
	 */
	public CategoricalContentPreviewRenderer(int davidID, TablePerspective tablePerspective,
			EventBasedSelectionManager geneSelectionManager, EventBasedSelectionManager sampleSelectionManager) {
		super(davidID, tablePerspective, geneSelectionManager, sampleSelectionManager);
		if (resolvedRowID == null)
			return;

		VirtualArray geneVA = new VirtualArray(dataDomain.getGeneIDType());
		geneVA.append(resolvedRowID);
		histogram = TablePerspectiveStatistics.calculateHistogram(dataDomain.getTable(),
				experimentPerspective.getVirtualArray(), geneVA);
	}

	@Override
	public void renderContent(GL2 gl) {
		if (resolvedRowID == null)
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

		// ArrayList<SelectionType> selectionTypes =
		// parent.sampleGroupSelectionManager.getSelectionTypes(group.getID());
		// if (selectionTypes.size() > 0
		// && selectionTypes.contains(MappedDataRenderer.abstractGroupType)) {
		// topBarColor = MappedDataRenderer.SUMMARY_BAR_COLOR;
		// bottomBarColor = topBarColor;
		// }

		int bucketCount = 0;
		// float barWidth = y / histogram.size();
		// float renderWith = x
		// - parentView.getPixelGLConverter().getGLWidthForPixelWidth(20);

		int sumValues = 0;

		for (int i = 0; i < histogram.size(); i++) {
			sumValues += histogram.get(i);
		}

		float step = 0;
		if (sumValues != 0)
			step = 0.85f * y / sumValues;

		float currentPositionY = 0;

		for (int bucketNumber = 0; bucketNumber < histogram.size(); bucketNumber++) {
			// Set<SelectionType> sampleSelectionTypes = new HashSet<>();
			// for (Integer id : columnPerspective.getVirtualArray()) {
			// List<SelectionType> selectionTypes = parent.sampleSelectionManager.getSelectionTypes(
			// columnPerspective.getIdType(), id);
			// if (selectionTypes != null) {
			// sampleSelectionTypes.addAll(selectionTypes);
			// }
			// }
			//
			// List<SelectionType> selectionTypes = new ArrayList<>(sampleSelectionTypes);
			// Collections.sort(selectionTypes);
			// for (Integer sampleID : histogram.getIDsForBucket(bucketNumber)) {
			// sampleSelectionTypes.addAll(parent.sampleSelectionManager.getSelectionTypes(columnIDType, sampleID));
			// }
			float[] baseColor = dataDomain.getTable().getColorMapper()
					.getColor((float) bucketCount / (histogram.size() - 1));
			colorCalculator.setBaseColor(new Color(baseColor[0], baseColor[1], baseColor[2]));

			// colorCalculator.calculateColors(Algorithms.mergeListsToUniqueList(selectionTypes,
			// sampleSelectionTypes));

			float currentBarHeight = histogram.get(bucketNumber) * step;

			colorCalculator.calculateColors(Algorithms.mergeListsToUniqueList(selectionLists));

			float[] topBarColor = colorCalculator.getPrimaryColor().getRGBA();
			float[] bottomBarColor = colorCalculator.getSecondaryColor().getRGBA();

			gl.glBegin(GL2GL3.GL_QUADS);
			gl.glColor3fv(topBarColor, 0);
			gl.glVertex3f(0, currentPositionY, z);
			gl.glColor3fv(bottomBarColor, 0);
			gl.glVertex3d(x, currentPositionY, z);
			gl.glColor3f(bottomBarColor[0] * 0.9f, bottomBarColor[1] * 0.9f, bottomBarColor[2] * 0.9f);
			gl.glVertex3d(x, currentPositionY + currentBarHeight, z);
			gl.glColor3f(topBarColor[0] * 0.9f, topBarColor[1] * 0.9f, topBarColor[2] * 0.9f);
			gl.glVertex3d(0, currentPositionY + currentBarHeight, z);
			gl.glEnd();

			gl.glColor3f(0.2f, 0.2f, 0.2f);
			gl.glLineWidth(0.1f);
			gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex3f(0, currentPositionY, z);
			gl.glVertex3d(x, currentPositionY, z);
			gl.glVertex3d(x, currentPositionY + currentBarHeight, z);
			gl.glVertex3d(0, currentPositionY + currentBarHeight, z);
			gl.glEnd();

			currentPositionY += currentBarHeight;
			bucketCount++;
		}
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}
}
