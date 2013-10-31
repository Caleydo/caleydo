/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.enroute.mappeddataview;

import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.collection.Algorithms;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.enroute.EPickingType;

/**
 * @author Alexander Lex
 *
 */
public abstract class ACategoricalRowContentRenderer extends ContentRenderer {

	protected static final int MAX_HISTOGRAM_BAR_WIDTH_PIXELS = 20;

	Histogram histogram;
	boolean useShading = true;

	/**
	 *
	 */
	public ACategoricalRowContentRenderer(IDType rowIDType, Integer rowID, IDType resolvedRowIDType,
			Integer resolvedRowID, ATableBasedDataDomain dataDomain, Perspective columnPerspective, AGLView parentView,
			MappedDataRenderer parent, Group group, boolean isHighlightMode) {
		super(rowIDType, rowID, resolvedRowIDType, resolvedRowID, dataDomain, columnPerspective, parentView, parent,
				group, isHighlightMode);

	}

	@Override
	public void renderContent(GL2 gl) {
		if (rowID == null)
			return;
		List<SelectionType> geneSelectionTypes = parent.rowSelectionManager.getSelectionTypes(rowIDType, rowID);

		List<SelectionType> selectionTypes = parent.sampleGroupSelectionManager.getSelectionTypes(group.getID());
		if (selectionTypes.size() > 0 && selectionTypes.contains(MappedDataRenderer.abstractGroupType)) {
			// topBarColor = MappedDataRenderer.SUMMARY_BAR_COLOR;
			// bottomBarColor = topBarColor;

			renderAverageBar(gl, selectionTypes);
		} else {
			renderAllBars(gl, geneSelectionTypes);
		}

	}

	protected abstract void renderAllBars(GL2 gl, List<SelectionType> geneSelectionTypes);

	@SuppressWarnings("unchecked")
	public void renderAverageBar(GL2 gl, List<SelectionType> selectionTypes) {
		if (histogram == null)
			return;
		int bucketCount = 0;
		float barWidth = y / histogram.size();
		float maxBarWidth = parentView.getPixelGLConverter().getGLHeightForPixelHeight(MAX_HISTOGRAM_BAR_WIDTH_PIXELS);
		float histogramStartY = 0;
		if (barWidth > maxBarWidth) {
			barWidth = maxBarWidth;
			histogramStartY = (y - histogram.size() * barWidth) / 2.0f;
		}
		float renderWith = x - parentView.getPixelGLConverter().getGLWidthForPixelWidth(20);

		for (int bucketNumber = 0; bucketNumber < histogram.size(); bucketNumber++) {

			// for (Integer id : histogram.getIDsForBucketFromBucketID(histogram.getBucketID(bucketNumber))) {
			// if (parent.sampleSelectionManager.checkStatus(SelectionType.SELECTION, id)) {
			// selectionTypes.add(SelectionType.SELECTION);
			// break;
			// }
			// }

			if (parent.selectedBucketNumber == bucketNumber && parent.selectedBucketGeneID == rowID
					&& parent.selectedBucketExperimentPerspective == columnPerspective) {
				selectionTypes.add(SelectionType.SELECTION);
			} else {
				selectionTypes.remove(SelectionType.SELECTION);
			}

			float[] baseColor = dataDomain.getTable().getColorMapper()
					.getColor((float) bucketCount / (histogram.size() - 1));

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
			gl.glPushName(parentView.getPickingManager().getPickingID(parentView.getID(),
					EPickingType.HISTOGRAM_BAR.name() + hashCode(), bucketNumber));
			gl.glBegin(GL2.GL_QUADS);
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
		pickingListener = new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				// System.out.println("Bucket: " + pick.getObjectID());

				parent.sampleSelectionManager.clearSelection(SelectionType.SELECTION);
				parent.sampleSelectionManager.addToType(SelectionType.SELECTION, columnIDType,
						histogram.getIDsForBucketFromBucketID(histogram.getBucketID(pick.getObjectID())));
				parent.sampleSelectionManager.triggerSelectionUpdateEvent();
				parent.selectedBucketNumber = pick.getObjectID();
				parent.selectedBucketGeneID = rowID;
				parent.selectedBucketExperimentPerspective = columnPerspective;
				parentView.setDisplayListDirty();
			}

		};

		// for (int bucketCount = 0; bucketCount < histogram.size(); bucketCount++) {
		// //FIXME: HACKY hashcode
		parentView.addTypePickingListener(pickingListener, EPickingType.HISTOGRAM_BAR.name() + hashCode());
		// }
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}

}
