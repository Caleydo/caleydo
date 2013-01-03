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

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.column.DataRepresentation;
import org.caleydo.core.data.selection.RecordSelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.util.mapping.color.ColorMapper;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.template.AHeatMapLayoutConfiguration;

public class HeatMapRenderer extends AHeatMapRenderer {

	public HeatMapRenderer(GLHeatMap heatMap) {
		super(heatMap);
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
	}

	@Override
	public void renderContent(final GL2 gl) {

		ColorMapper colorMapper = heatMap.getDataDomain().getColorMapper();
		recordSpacing.getYDistances().clear();
		float yPosition = y;
		float xPosition = 0;
		float fieldHeight = 0;
		float fieldWidth = recordSpacing.getFieldWidth();

		// DimensionVirtualArray dimensionVA =

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

			for (Integer dimensionID : heatMap.getTablePerspective()
					.getDimensionPerspective().getVirtualArray()) {

				renderElement(gl, dimensionID, recordID, yPosition, xPosition,
						fieldHeight, fieldWidth, colorMapper);

				xPosition += fieldWidth;

			}

			recordSpacing.getYDistances().add(yPosition);
		}
	}

	private void renderElement(final GL2 gl, final int dimensionID, final int recordID,
			final float fYPosition, final float fXPosition, final float fFieldHeight,
			final float fFieldWidth, ColorMapper colorMapper) {

		// GLHelperFunctions.drawPointAt(gl, 0, fYPosition, 0);

		float value = heatMap.getDataDomain().getTable()
				.getFloat(heatMap.getRenderingRepresentation(), recordID, dimensionID);

		float fOpacity = 1.0f;

		if (heatMap
				.getDataDomain()
				.getTable()
				.containsDataRepresentation(DataRepresentation.UNCERTAINTY_NORMALIZED,
						dimensionID, recordID)) {
			// setSelectedElements = heatMap.getContentSelectionManager()
			// .getElements(SelectionType.MOUSE_OVER);
			// for (Integer selectedElement : setSelectedElements) {
			// if (recordIndex == selectedElement.intValue()) {
			// fOpacity = dimension.getFloat(
			// EDataRepresentation.UNCERTAINTY_NORMALIZED,
			// recordIndex);
			// }
			// }
		} else if (heatMap.getRecordSelectionManager().checkStatus(
				SelectionType.DESELECTED, recordID)) {
			fOpacity = 0.3f;
		}

		float[] fArMappingColor = colorMapper.getColor(value);

		gl.glColor4f(fArMappingColor[0], fArMappingColor[1], fArMappingColor[2], fOpacity);

		gl.glPushName(heatMap.getPickingManager().getPickingID(heatMap.getID(),
				PickingType.HEAT_MAP_DIMENSION_SELECTION, dimensionID));
		gl.glPushName(heatMap.getPickingManager().getPickingID(heatMap.getID(),
				PickingType.HEAT_MAP_RECORD_SELECTION, recordID));
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3f(fXPosition, fYPosition, FIELD_Z);
		gl.glVertex3f(fXPosition + fFieldWidth, fYPosition, FIELD_Z);
		gl.glVertex3f(fXPosition + fFieldWidth, fYPosition + fFieldHeight, FIELD_Z);
		gl.glVertex3f(fXPosition, fYPosition + fFieldHeight, FIELD_Z);
		gl.glEnd();

		gl.glPopName();
		gl.glPopName();
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
		return "HeatMapRenderer";
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}

}
