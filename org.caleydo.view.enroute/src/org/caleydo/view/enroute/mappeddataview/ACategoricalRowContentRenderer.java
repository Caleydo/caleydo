/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.enroute.mappeddataview;

import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.collection.Algorithms;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.view.enroute.EPickingType;

/**
 * @author Alexander Lex
 *
 */
public abstract class ACategoricalRowContentRenderer
	extends ContentRenderer {

	protected static final int MAX_HISTOGRAM_BAR_WIDTH_PIXELS = 20;

	Histogram histogram;
	boolean useShading = true;

	/**
	 *
	 */
	public ACategoricalRowContentRenderer(Integer geneID, Integer davidID, GeneticDataDomain dataDomain,
			TablePerspective tablePerspective, Perspective experimentPerspective,
			AGLView parentView, MappedDataRenderer parent, Group group, boolean isHighlightMode) {

		super(geneID, davidID, dataDomain, tablePerspective, experimentPerspective, parentView, parent, group,
				isHighlightMode);
	}

	public ACategoricalRowContentRenderer(IContentRendererInitializor contentRendererInitializor) {
		super(contentRendererInitializor);
	}

	@Override
	public void renderContent(GL2 gl) {
		if (geneID == null)
			return;
		ArrayList<SelectionType> geneSelectionTypes = parent.geneSelectionManager.getSelectionTypes(davidID);

		ArrayList<SelectionType> selectionTypes = parent.sampleGroupSelectionManager.getSelectionTypes(group.getID());
		if (selectionTypes.size() > 0 && selectionTypes.contains(MappedDataRenderer.abstractGroupType)) {
			// topBarColor = MappedDataRenderer.SUMMARY_BAR_COLOR;
			// bottomBarColor = topBarColor;

			renderAverageBar(gl, selectionTypes);
		}
		else {
			renderAllBars(gl, geneSelectionTypes);
		}

	}

	protected abstract void renderAllBars(GL2 gl, ArrayList<SelectionType> geneSelectionTypes);

	@SuppressWarnings("unchecked")
	public void renderAverageBar(GL2 gl, ArrayList<SelectionType> selectionTypes) {
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

			if (parent.selectedBucketID == histogram.getBucketID(bucketNumber)) {
				selectionTypes.add(SelectionType.SELECTION);
			}
			else {
				selectionTypes.remove(SelectionType.SELECTION);
			}

			float[] baseColor = dataDomain.getColorMapper().getColor((float) bucketCount / (histogram.size() - 1));

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
					EPickingType.HISTOGRAM_BAR.name(), histogram.getBucketID(bucketNumber)));
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

	protected void registerPickingListener() {
		pickingListener = new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				// System.out.println("Bucket: " + pick.getObjectID());

				parent.sampleSelectionManager.clearSelection(SelectionType.SELECTION);
				parent.sampleSelectionManager.addToType(SelectionType.SELECTION, sampleIDType,
						histogram.getIDsForBucketFromBucketID(pick.getObjectID()));
				parent.sampleSelectionManager.triggerSelectionUpdateEvent();
				parentView.setDisplayListDirty();

				parent.selectedBucketID = pick.getObjectID();
			}

		};

		for (int bucketCount = 0; bucketCount < histogram.size(); bucketCount++) {
			parentView.addIDPickingListener(pickingListener, EPickingType.HISTOGRAM_BAR.name(),
					histogram.getBucketID(bucketCount));
		}
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}

}
