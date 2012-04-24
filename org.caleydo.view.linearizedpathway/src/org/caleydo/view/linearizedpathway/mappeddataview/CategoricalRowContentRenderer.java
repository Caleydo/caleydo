/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 * 
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.linearizedpathway.mappeddataview;

import java.util.ArrayList;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.container.ContainerStatistics;
import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.perspective.ADataPerspective;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.collection.Algorithms;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.util.GLHelperFunctions;
import org.caleydo.datadomain.genetic.GeneticDataDomain;

/**
 * @author alexsb
 * 
 */
public class CategoricalRowContentRenderer extends ContentRenderer {

	Histogram histogram;

	/**
	 * 
	 */
	public CategoricalRowContentRenderer(Integer geneID, Integer davidID,
			GeneticDataDomain dataDomain, DataContainer dataContainer,
			ADataPerspective<?, ?, ?, ?> experimentPerspective, AGLView parentView,
			MappedDataRenderer parent, Group group) {

		super(geneID, davidID, dataDomain, dataContainer, experimentPerspective,
				parentView, parent, group);
	}

	@Override
	public void init() {
		if (geneID == null)
			return;
		if (experimentPerspective instanceof RecordPerspective) {
			DimensionVirtualArray dimensionVirtualArray = new DimensionVirtualArray();
			dimensionVirtualArray.append(geneID);
			histogram = ContainerStatistics.calculateHistogram(dataDomain.getTable(),
					(RecordVirtualArray) experimentPerspective.getVirtualArray(),
					dimensionVirtualArray, 5);
		} else {
			RecordVirtualArray recordVirtualArray = new RecordVirtualArray();
			recordVirtualArray.append(geneID);
			histogram = ContainerStatistics.calculateHistogram(dataDomain.getTable(),
					recordVirtualArray,
					(DimensionVirtualArray) experimentPerspective.getVirtualArray(), 5);
		}

	}

	@Override
	public void render(GL2 gl) {
		if (geneID == null)
			return;
		ArrayList<SelectionType> geneSelectionTypes = parent.geneSelectionManager
				.getSelectionTypes(davidID);

		ArrayList<SelectionType> selectionTypes = parent.sampleGroupSelectionManager
				.getSelectionTypes(group.getID());
		if (selectionTypes.size() > 0
				&& selectionTypes.contains(MappedDataRenderer.abstractGroupType)) {
			topBarColor = MappedDataRenderer.SUMMARY_BAR_COLOR;
			bottomBarColor = topBarColor;

			ArrayList<ArrayList<SelectionType>> selectionLists = new ArrayList<ArrayList<SelectionType>>();
			selectionLists.add(geneSelectionTypes);

			for (Integer experimentID : experimentPerspective.getVirtualArray()) {
				selectionLists.add(parent.sampleSelectionManager
						.getSelectionTypes(experimentID));
			}

			renderAverageBar(gl, Algorithms.mergeListsToUniqueList(selectionLists));
		} else {
			renderAllBars(gl, geneSelectionTypes);
		}

	}

	@SuppressWarnings("unchecked")
	private void renderAllBars(GL2 gl, ArrayList<SelectionType> geneSelectionTypes) {
		float xIncrement = x / experimentPerspective.getVirtualArray().size();
		int experimentCount = 0;

//		float[] tempTopBarColor = topBarColor;
//		float[] tempBottomBarColor = bottomBarColor;

		gl.glColor3f(0, 0, 0);
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3f(0, 0.5f * y, z);
		gl.glVertex3f(x, 0.5f * y, z);
		gl.glEnd();

		for (Integer experimentID : experimentPerspective.getVirtualArray()) {

			float value;
			if (geneID != null) {
				value = dataDomain.getGeneValue(DataRepresentation.NORMALIZED, geneID,
						experimentID);

				if (value < 0.5001 && value > 0.499)
				{
					experimentCount++;
					continue;
				}
				
				ArrayList<SelectionType> experimentSelectionTypes = parent.sampleSelectionManager
						.getSelectionTypes(experimentID);

				topBarColor = dataDomain.getColorMapper().getColor(value);
				bottomBarColor = topBarColor;
				calculateColors(Algorithms.mergeListsToUniqueList(
						experimentSelectionTypes, geneSelectionTypes));

				float leftEdge = xIncrement * experimentCount;

				float upperEdge = value * y;

				gl.glPushName(parentView.getPickingManager().getPickingID(
						parentView.getID(), PickingType.GENE.name(), davidID));
				gl.glPushName(parentView.getPickingManager().getPickingID(
						parentView.getID(), PickingType.SAMPLE.name(), experimentID));

				gl.glColor3fv(topBarColor, 0);
				gl.glBegin(GL2.GL_QUADS);

				gl.glVertex3f(leftEdge, 0.5f * y, z);
				gl.glVertex3f(leftEdge + xIncrement, 0.5f * y, z);

				gl.glVertex3f(leftEdge + xIncrement, upperEdge, z);
				gl.glVertex3f(leftEdge, upperEdge, z);
				// gl.glColor4fv(topBarColor, 0);
				// gl.glBegin(GL2.GL_QUADS);
				// gl.glVertex3f(leftEdge, 0, z);
				// gl.glColor3f(topBarColor[0] * 0.9f, topBarColor[1] *
				// 0.9f,
				// topBarColor[2] * 0.9f);
				// gl.glVertex3f(leftEdge + xIncrement, 0, z);
				// gl.glColor3f(bottomBarColor[0] * 0.9f, bottomBarColor[1]
				// * 0.9f,
				// bottomBarColor[2] * 0.9f);
				//
				// gl.glVertex3f(leftEdge + xIncrement, upperEdge, z);
				// gl.glColor4fv(bottomBarColor, 0);
				//
				// gl.glVertex3f(leftEdge, upperEdge, z);

				gl.glEnd();

				gl.glPopName();
				gl.glPopName();
				experimentCount++;
//				topBarColor = tempTopBarColor;
//				bottomBarColor = tempBottomBarColor;
			}

		}
	}

	public void renderAverageBar(GL2 gl, ArrayList<SelectionType> selectionTypes) {
		gl.glPushName(parentView.getPickingManager().getPickingID(parentView.getID(),
				PickingType.GENE.name(), davidID));

		int bucketCount = 0;
		float barWidth = y / histogram.size();
		float renderWith = x
				- parentView.getPixelGLConverter().getGLWidthForPixelWidth(20);
		for (Integer nrValues : histogram) {
			// calculateColors(Algorithms.mergeListsToUniqueList(
			// experimentSelectionTypes, geneSelectionTypes));
			float lowerEdge = barWidth * bucketCount;
			float value = 0;
			if (nrValues != 0)
				value = ((float) nrValues) / histogram.getLargestValue();

			topBarColor = dataDomain.getColorMapper().getColor(
					(float) bucketCount / (histogram.size() - 1));
			bottomBarColor = topBarColor;
			calculateColors(selectionTypes);

			float barHeight = value * renderWith;

			// barHeight = x;
			// barHeight *= x/20;
			// gl.glPushName(parentView.getPickingManager().getPickingID(
			// parentView.getID(), PickingType.SAMPLE.name(), experimentID));
			gl.glBegin(GL2.GL_QUADS);

			gl.glColor3fv(topBarColor, 0);
			gl.glVertex3f(0, lowerEdge, z);
			gl.glColor3f(topBarColor[0] * 0.9f, topBarColor[1] * 0.9f,
					topBarColor[2] * 0.9f);
			// gl.glColor4fv(topBarColor, 0);
			gl.glVertex3d(barHeight, lowerEdge, z);

			// gl.glColor4fv(bottomBarColor, 0);
			gl.glColor3f(bottomBarColor[0] * 0.9f, bottomBarColor[1] * 0.9f,
					bottomBarColor[2] * 0.9f);
			gl.glVertex3d(barHeight, lowerEdge + barWidth, z);

			gl.glColor3fv(bottomBarColor, 0);
			// gl.glColor3f(bottomBarColor[0] * 0.9f, bottomBarColor[1] * 0.9f,
			// bottomBarColor[2] * 0.9f);
			gl.glVertex3f(0, lowerEdge + barWidth, z);

			gl.glEnd();

			gl.glColor3f(0, 0, 0);
			gl.glLineWidth(0.5f);
			gl.glBegin(GL2.GL_LINE_STRIP);
			gl.glVertex3f(0, lowerEdge, z);
			gl.glVertex3d(barHeight, lowerEdge, z);
			gl.glVertex3d(barHeight, lowerEdge + barWidth, z);
			gl.glVertex3f(0, lowerEdge + barWidth, z);
			gl.glEnd();

			bucketCount++;

		}
		gl.glPopName();
	}

}
