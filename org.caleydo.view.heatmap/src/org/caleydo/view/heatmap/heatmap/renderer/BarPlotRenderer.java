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
package org.caleydo.view.heatmap.heatmap.renderer;

import static org.caleydo.view.heatmap.HeatMapRenderStyle.FIELD_Z;
import java.util.ArrayList;
import javax.media.opengl.GL2;
import org.caleydo.core.data.selection.RecordSelectionManager;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.template.AHeatMapLayoutConfiguration;
import org.caleydo.view.heatmap.uncertainty.GLUncertaintyHeatMap;

public class BarPlotRenderer extends AHeatMapRenderer {

	ArrayList<Float> uncertainties = new ArrayList<Float>();
	private boolean isDirty = false;
	private GLUncertaintyHeatMap uncertaintyHeatmap;

	public BarPlotRenderer(GLHeatMap heatMap, GLUncertaintyHeatMap uncertaintyHeatmap) {
		super(heatMap);
		this.uncertaintyHeatmap = uncertaintyHeatmap;
	}

	@Override
	public void updateSpacing() {

		AHeatMapLayoutConfiguration heatMapTemplate = heatMap.getTemplate();

		int nrRecordElements = heatMap.getTablePerspective().getRecordPerspective()
				.getVirtualArray().size();

		RecordSelectionManager selectionManager = heatMap.getRecordSelectionManager();
		if (heatMap.isHideElements()) {

			nrRecordElements -= selectionManager
					.getNumberOfElements(GLHeatMap.SELECTION_HIDDEN);
		}

		recordSpacing.calculateRecordSpacing(nrRecordElements, heatMap.getTablePerspective()
				.getDimensionPerspective().getVirtualArray().size(),
				elementLayout.getSizeScaledX(), elementLayout.getSizeScaledY(),
				heatMapTemplate.getMinSelectedFieldHeight());
		heatMapTemplate.setContentSpacing(recordSpacing);

		// ((AContentRenderer) renderer).setContentSpacing(contentSpacing);
	}

	@Override
	public void renderContent(final GL2 gl) {

		recordSpacing.getYDistances().clear();
		float yPosition = y;
		float xPosition = 0;
		float fieldHeight = 0;
		float fieldWidth = x;

		if (this.isDirty) {
			isDirty = false;
		}

		ArrayList<double[]> uncertainties = uncertaintyHeatmap.getMultiLevelUncertainty();

		PixelGLConverter conv = heatMap.getPixelGLConverter();

		for (Integer recordID : heatMap.getTablePerspective().getRecordPerspective()
				.getVirtualArray()) {
			fieldHeight = recordSpacing.getFieldHeight(recordID);

			// we treat normal and deselected the same atm

			if (heatMap.isHideElements()
					&& heatMap.getRecordSelectionManager().checkStatus(
							GLHeatMap.SELECTION_HIDDEN, recordID)) {
				recordSpacing.getYDistances().add(yPosition);
				continue;
			}

			yPosition -= fieldHeight;
			xPosition = 0;

			int screenHeight = conv.getPixelHeightForGLHeight(fieldHeight);

			float uncertaintyMax = uncertaintyHeatmap.getMaxUncertainty(recordID);
			if (screenHeight < 15) {
				renderBlock(gl, yPosition, xPosition, fieldHeight, fieldWidth,
						uncertaintyMax, GLUncertaintyHeatMap.getUncertaintyColor(0));
			} else {

				for (int i = 0; i < uncertainties.size(); i++) {

					float uncertainty = (float) uncertainties.get(i)[recordID];

					float height = fieldHeight / (float) uncertainties.size();
					float yPos = yPosition + height * (uncertainties.size() - i - 1);
					// certainty[i] = certainty[i] > 1 ? 1 : certainty[i];
					// certainty[i] = certainty[i] < 0 ? 0 : certainty[i];

					// in case of not normalized values, show valid data as well
					// if (uncertainty >= 1) {
					// renderLine(gl, yPos, xPosition, height, fieldWidth,
					// uncertainty,
					// GLUncertaintyHeatMap.DATA_VALID[i]);
					// } else {
					renderBlock(gl, yPos, xPosition, height, fieldWidth, uncertainty,
							GLUncertaintyHeatMap.getUncertaintyColor(i + 1));
					// }
				}
			}
			if (screenHeight >= 1) {
				renderVLine(gl, yPosition, xPosition + fieldWidth * (uncertaintyMax),
						fieldHeight, GLUncertaintyHeatMap.getUncertaintyColor(0));
			}

			recordSpacing.getYDistances().add(yPosition);

		}
	}

	private void renderBlock(final GL2 gl, final float fYPosition,
			final float fXPosition, final float fFieldHeight, final float fFieldWidth,
			final float uncertainty, float[] rgba) {

		float certainWidth = fFieldWidth * (1 - uncertainty);
		float unCertainWidth = fFieldWidth * (uncertainty);

		// gl.glPushName(heatMap.getPickingManager().getPickingID(heatMap.getID(),
		// EPickingType.HEAT_MAP_STORAGE_SELECTION, iDimensionIndex));
		// gl.glPushName(heatMap.getPickingManager().getPickingID(heatMap.getID(),
		// EPickingType.HEAT_MAP_LINE_SELECTION, recordIndex));

		// uncertain

		gl.glBegin(GL2.GL_POLYGON);
		gl.glColor4fv(GLUncertaintyHeatMap.BACKGROUND, 0);
		gl.glVertex3f(fXPosition, fYPosition, FIELD_Z);
		gl.glVertex3f(fXPosition + unCertainWidth, fYPosition, FIELD_Z);
		gl.glVertex3f(fXPosition + unCertainWidth, fYPosition + fFieldHeight, FIELD_Z);
		gl.glVertex3f(fXPosition, fYPosition + fFieldHeight, FIELD_Z);
		gl.glEnd();

		// certain
		gl.glBegin(GL2.GL_POLYGON);
		gl.glColor4fv(rgba, 0);
		gl.glVertex3f(fXPosition + unCertainWidth, fYPosition, FIELD_Z);
		gl.glVertex3f(fXPosition + certainWidth + unCertainWidth, fYPosition, FIELD_Z);
		gl.glVertex3f(fXPosition + certainWidth + unCertainWidth, fYPosition
				+ fFieldHeight, FIELD_Z);
		gl.glVertex3f(fXPosition + unCertainWidth, fYPosition + fFieldHeight, FIELD_Z);
		gl.glEnd();

		// gl.glPopName();
		// gl.glPopName();
	}

	private void renderVLine(final GL2 gl, final float fYPosition,
			final float fXPosition, final float fFieldHeight, float[] rgba) {

		gl.glBegin(GL2.GL_LINES);
		gl.glColor4fv(rgba, 0);
		gl.glLineWidth(1f);
		gl.glVertex3f(fXPosition, fYPosition, FIELD_Z);
		gl.glVertex3f(fXPosition, fYPosition + fFieldHeight, FIELD_Z);
		gl.glEnd();

		// gl.glPopName();
		// gl.glPopName();
	}

	public float getYCoordinateByContentIndex(int recordIndex) {
		if (recordSpacing != null)
			return y
					- recordSpacing.getYDistances().get(recordIndex)
					- recordSpacing.getFieldHeight(heatMap.getTablePerspective()
							.getRecordPerspective().getVirtualArray().get(recordIndex))
					/ 2;
		return 0;
	}

	public float getXCoordinateByDimensionIndex(int dimensionIndex) {
		return recordSpacing.getFieldWidth() * dimensionIndex;
	}

	@Override
	public String toString() {
		return "BarPlotRenderer";
	}

	@Override
	protected boolean permitsDisplayLists() {
		return false;
	}

}
