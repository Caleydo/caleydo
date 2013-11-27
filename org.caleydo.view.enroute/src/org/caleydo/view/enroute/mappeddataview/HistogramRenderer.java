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

import org.caleydo.core.data.collection.CategoricalHistogram;
import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.perspective.table.TablePerspectiveStatistics;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.util.collection.Algorithms;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.enroute.EPickingType;

/**
 * @author Christian
 *
 */
public class HistogramRenderer extends ADataRenderer {

	// protected static final int MAX_HISTOGRAM_BAR_WIDTH_PIXELS = 20;
	protected static final int SPACING_PIXELS = 2;

	protected Histogram histogram;

	public HistogramRenderer(ContentRenderer contentRenderer) {
		super(contentRenderer);
		if (contentRenderer.resolvedRowID == null)
			return;

		VirtualArray dimensionVirtualArray = new VirtualArray(contentRenderer.resolvedRowIDType);
		dimensionVirtualArray.append(contentRenderer.resolvedRowID);
		histogram = TablePerspectiveStatistics.calculateHistogram(contentRenderer.dataDomain.getTable(),
				contentRenderer.columnPerspective.getVirtualArray(), dimensionVirtualArray);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void render(GL2 gl, float x, float y, List<SelectionType> selectionTypes) {
		if (histogram == null)
			return;
		int bucketCount = 0;
		float spacing = contentRenderer.parentView.getPixelGLConverter().getGLHeightForPixelHeight(SPACING_PIXELS);
		float barWidth = (y - 2f * spacing) / histogram.size();
		// float maxBarWidth = contentRenderer.parentView.getPixelGLConverter().getGLHeightForPixelHeight(
		// MAX_HISTOGRAM_BAR_WIDTH_PIXELS);
		float histogramStartY = spacing;
		// if (barWidth > maxBarWidth) {
		// barWidth = maxBarWidth;
		// histogramStartY = (y - histogram.size() * barWidth) / 2.0f + spacing;
		// }
		float renderWith = x - contentRenderer.parentView.getPixelGLConverter().getGLWidthForPixelWidth(5);

		for (int bucketNumber = 0; bucketNumber < histogram.size(); bucketNumber++) {

			// for (Integer id : histogram.getIDsForBucketFromBucketID(histogram.getBucketID(bucketNumber))) {
			// if (parent.sampleSelectionManager.checkStatus(SelectionType.SELECTION, id)) {
			// selectionTypes.add(SelectionType.SELECTION);
			// break;
			// }
			// }

			if (contentRenderer.parent.selectedBucketNumber == bucketNumber
					&& contentRenderer.parent.selectedBucketGeneID == contentRenderer.rowID
					&& contentRenderer.parent.selectedBucketExperimentPerspective == contentRenderer.columnPerspective) {
				selectionTypes.add(SelectionType.SELECTION);
			} else {
				selectionTypes.remove(SelectionType.SELECTION);
			}

			float[] baseColor = null;
			if (histogram instanceof CategoricalHistogram) {
				baseColor = ((CategoricalHistogram) histogram).getColor(bucketNumber).getRGBA();
			} else {
				baseColor = contentRenderer.dataDomain.getTable().getColorMapper()
						.getColor((float) bucketCount / (histogram.size() - 1));
			}

			colorCalculator.setBaseColor(new Color(baseColor[0], baseColor[1], baseColor[2]));

			// calculateColors(Algorithms.mergeListsToUniqueList(selectionTypes));
			float lowerEdge = histogramStartY + barWidth * bucketCount;
			float value = 0;
			int nrValues = histogram.get(bucketNumber);
			if (nrValues != 0)
				value = ((float) nrValues) / histogram.getLargestValue();

			colorCalculator.calculateColors(Algorithms.mergeListsToUniqueList(selectionTypes));

			float[] topBarColor = colorCalculator.getPrimaryColor().getRGBA();
			float[] bottomBarColor = colorCalculator.getSecondaryColor().getRGBA();

			float barHeight = value * renderWith;
			gl.glPushName(contentRenderer.parentView.getPickingManager().getPickingID(
					contentRenderer.parentView.getID(), EPickingType.HISTOGRAM_BAR.name() + hashCode(), bucketNumber));
			gl.glBegin(GL2GL3.GL_QUADS);
			gl.glColor3fv(bottomBarColor, 0);
			gl.glVertex3f(0, lowerEdge, z);
			gl.glColor3f(bottomBarColor[0] * 0.9f, bottomBarColor[1] * 0.9f, bottomBarColor[2] * 0.9f);

			gl.glVertex3d(barHeight, lowerEdge, z);

			gl.glColor3f(topBarColor[0] * 0.9f, topBarColor[1] * 0.9f, topBarColor[2] * 0.9f);
			gl.glVertex3d(barHeight, lowerEdge + barWidth, z);

			gl.glColor3fv(topBarColor, 0);

			gl.glVertex3f(0, lowerEdge + barWidth, z);

			gl.glEnd();

			gl.glColor3f(0, 0, 0);
			gl.glLineWidth(0.5f);
			gl.glBegin(GL.GL_LINE_STRIP);
			gl.glVertex3f(0, lowerEdge, z);
			gl.glVertex3d(barHeight, lowerEdge, z);
			gl.glVertex3d(barHeight, lowerEdge + barWidth, z);
			gl.glVertex3f(0, lowerEdge + barWidth, z);
			gl.glEnd();
			gl.glPopName();
			bucketCount++;

			// parent.selectedBucketID = 0;
		}
	}

	protected void registerPickingListeners() {

		// for (int bucketCount = 0; bucketCount < histogram.size(); bucketCount++) {
		// //FIXME: HACKY hashcode
		contentRenderer.parent.pickingListenerManager.addTypePickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {

				contentRenderer.parent.sampleSelectionManager.clearSelection(SelectionType.SELECTION);
				contentRenderer.parent.sampleSelectionManager.addToType(SelectionType.SELECTION,
						contentRenderer.columnIDType,
						histogram.getIDsForBucketFromBucketID(histogram.getBucketID(pick.getObjectID())));
				contentRenderer.parent.sampleSelectionManager.triggerSelectionUpdateEvent();
				contentRenderer.parent.selectedBucketNumber = pick.getObjectID();
				contentRenderer.parent.selectedBucketGeneID = contentRenderer.rowID;
				contentRenderer.parent.selectedBucketExperimentPerspective = contentRenderer.columnPerspective;
				contentRenderer.parentView.setDisplayListDirty();
			}

		}, EPickingType.HISTOGRAM_BAR.name() + hashCode());
		// }
	}

}
