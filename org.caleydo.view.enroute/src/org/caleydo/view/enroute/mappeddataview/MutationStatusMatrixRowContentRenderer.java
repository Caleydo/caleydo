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
package org.caleydo.view.enroute.mappeddataview;

import java.util.ArrayList;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.column.DataRepresentation;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.table.TablePerspectiveStatistics;
import org.caleydo.core.data.perspective.variable.AVariablePerspective;
import org.caleydo.core.data.perspective.variable.RecordPerspective;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.collection.Algorithms;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.view.enroute.EPickingType;

/**
 * Renderer for matrix of mutation status data.
 *
 * @author Christian Partl
 *
 */
public class MutationStatusMatrixRowContentRenderer extends ACategoricalRowContentRenderer {

	public static final int NUM_ROWS = 6;

	public MutationStatusMatrixRowContentRenderer(IContentRendererInitializor contentRendererInitializor) {
		super(contentRendererInitializor);
	}

	public MutationStatusMatrixRowContentRenderer(Integer geneID, Integer davidID, GeneticDataDomain dataDomain,
			TablePerspective tablePerspective, AVariablePerspective<?, ?, ?, ?> experimentPerspective,
			AGLView parentView, MappedDataRenderer parent, Group group, boolean isHighlightMode) {

		super(geneID, davidID, dataDomain, tablePerspective, experimentPerspective, parentView, parent, group,
				isHighlightMode);
	}

	@Override
	public void init() {
		if (geneID == null)
			return;
		if (experimentPerspective instanceof RecordPerspective) {

			DimensionVirtualArray dimensionVirtualArray = new DimensionVirtualArray();
			dimensionVirtualArray.append(geneID);
			histogram = TablePerspectiveStatistics.calculateHistogram(dataDomain.getTable(),
					(RecordVirtualArray) experimentPerspective.getVirtualArray(), dimensionVirtualArray, 2);
		} else {
			RecordVirtualArray recordVirtualArray = new RecordVirtualArray();
			recordVirtualArray.append(geneID);
			histogram = TablePerspectiveStatistics.calculateHistogram(dataDomain.getTable(), recordVirtualArray,
					(DimensionVirtualArray) experimentPerspective.getVirtualArray(), 2);
		}
		registerPickingListener();
	}

	@Override
	protected void renderAllBars(GL2 gl, ArrayList<SelectionType> geneSelectionTypes) {

		if (geneID == null)
			return;

		float matrixColumnWidth = x
				/ (float) Math.ceil((float) experimentPerspective.getVirtualArray().size() / NUM_ROWS);
		float matrixRowHeight = y / NUM_ROWS;
		int vaIndex = 0;
		int columnIndex = 0;

		if (matrixColumnWidth < parentView.getPixelGLConverter().getGLWidthForPixelWidth(3)) {
			useShading = false;
		}

		while (vaIndex < experimentPerspective.getVirtualArray().size()) {
			for (int rowIndex = 0; (rowIndex < NUM_ROWS) && (vaIndex < experimentPerspective.getVirtualArray().size()); rowIndex++) {
				Integer sampleID = experimentPerspective.getVirtualArray().get(vaIndex);
				float value = dataDomain.getGeneValue(DataRepresentation.NORMALIZED, geneID, sampleID);

				if (useShading || value > 0.0001f) {
					renderMatrixCell(gl, rowIndex, columnIndex, matrixRowHeight, matrixColumnWidth, sampleID, value,
							geneSelectionTypes);
				}

				vaIndex++;
			}
			columnIndex++;
		}

	}

	@SuppressWarnings("unchecked")
	private void renderMatrixCell(GL2 gl, int rowIndex, int columnIndex, float rowHeight, float columnWidth,
			int sampleID, float value, ArrayList<SelectionType> geneSelectionTypes) {
		ArrayList<SelectionType> experimentSelectionTypes = parent.sampleSelectionManager.getSelectionTypes(
				sampleIDType, sampleID);

		float[] mappedColor = dataDomain.getColorMapper().getColor(value);
		float[] baseColor = new float[] { mappedColor[0], mappedColor[1], mappedColor[2], 1f };

		float[] topBarColor = baseColor;
		float[] bottomBarColor = baseColor;

		ArrayList<SelectionType> selectionTypes = Algorithms.mergeListsToUniqueList(experimentSelectionTypes,
				geneSelectionTypes);

		if (isHighlightMode
				&& !(selectionTypes.contains(SelectionType.MOUSE_OVER) || selectionTypes
						.contains(SelectionType.SELECTION))) {
			return;
		}

		if (isHighlightMode) {
			colorCalculator.setBaseColor(new Color(baseColor));

			colorCalculator.calculateColors(selectionTypes);

			topBarColor = colorCalculator.getPrimaryColor().getRGBA();
			bottomBarColor = colorCalculator.getSecondaryColor().getRGBA();
		}

		float leftEdge = columnWidth * columnIndex;
		float lowerEdge = rowHeight * rowIndex;

		// gl.glPushName(parentView.getPickingManager().getPickingID(
		// parentView.getID(), PickingType.GENE.name(), davidID));

		Integer resolvedSampleID = sampleIDMappingManager.getID(dataDomain.getSampleIDType(), parent.sampleIDType,
				sampleID);
		if (resolvedSampleID != null) {
			gl.glPushName(parentView.getPickingManager().getPickingID(parentView.getID(), EPickingType.SAMPLE.name(),
					resolvedSampleID));
		}

		if (value < 0.5f) {
			gl.glBegin(GL2.GL_QUADS);

			if (useShading) {
				gl.glColor4f(bottomBarColor[0] * 1.4f, bottomBarColor[1] * 1.4f, bottomBarColor[2] * 1.4f, 0.5f);

			} else {
				gl.glColor4f(bottomBarColor[0], bottomBarColor[1], bottomBarColor[2], 0.5f);
			}
			gl.glVertex3f(leftEdge, lowerEdge, z);

			gl.glColor4f(bottomBarColor[0], bottomBarColor[1], bottomBarColor[2], 0.5f);

			// if (useShading) {
			// gl.glColor3f(bottomBarColor[0] * 0.9f, bottomBarColor[1] * 0.9f, bottomBarColor[2] * 0.9f);
			//
			// }

			gl.glVertex3f(leftEdge + columnWidth, lowerEdge, z);

			if (useShading) {
				gl.glColor4f(topBarColor[0] * 0.6f, topBarColor[1] * 0.6f, topBarColor[2] * 0.6f, 0.5f);
			} else {
				gl.glColor4f(topBarColor[0], topBarColor[1], topBarColor[2], 0.5f);
			}

			gl.glVertex3f(leftEdge + columnWidth, lowerEdge + rowHeight, z);
			// if (useShading) {
			// gl.glColor3f(topBarColor[0] * 1.1f, topBarColor[1] * 1.1f, topBarColor[2] * 1.1f);
			// } else {
			gl.glColor4f(topBarColor[0], topBarColor[1], topBarColor[2], 0.5f);

			gl.glVertex3f(leftEdge, lowerEdge + rowHeight, z);

			gl.glEnd();
		} else {

			gl.glBegin(GL2.GL_QUADS);

			if (useShading) {
				gl.glColor4f(bottomBarColor[0] * 1.2f, bottomBarColor[1] * 1.2f, bottomBarColor[2] * 1.2f, 1f);

			} else {
				gl.glColor4f(bottomBarColor[0], bottomBarColor[1], bottomBarColor[2], 1f);
			}
			gl.glVertex3f(leftEdge, lowerEdge, z);

			gl.glColor4f(bottomBarColor[0], bottomBarColor[1], bottomBarColor[2], 1f);

			// if (useShading) {
			// gl.glColor3f(bottomBarColor[0] * 0.9f, bottomBarColor[1] * 0.9f, bottomBarColor[2] * 0.9f);
			//
			// }

			gl.glVertex3f(leftEdge + columnWidth, lowerEdge, z);

			if (useShading) {
				gl.glColor4f(topBarColor[0] * 0.8f, topBarColor[1] * 0.8f, topBarColor[2] * 0.8f, 1f);
			} else {
				gl.glColor4f(topBarColor[0], topBarColor[1], topBarColor[2], 1f);
			}

			gl.glVertex3f(leftEdge + columnWidth, lowerEdge + rowHeight, z);
			// if (useShading) {
			// gl.glColor3f(topBarColor[0] * 1.1f, topBarColor[1] * 1.1f, topBarColor[2] * 1.1f);
			// } else {
			gl.glColor4f(topBarColor[0], topBarColor[1], topBarColor[2], 1f);

			gl.glVertex3f(leftEdge, lowerEdge + rowHeight, z);

			gl.glEnd();

		}
		if (resolvedSampleID != null)
			gl.glPopName();
	}
}
