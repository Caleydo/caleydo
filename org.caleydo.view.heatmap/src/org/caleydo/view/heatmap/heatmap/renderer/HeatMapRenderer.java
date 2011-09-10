package org.caleydo.view.heatmap.heatmap.renderer;

import static org.caleydo.view.heatmap.HeatMapRenderStyle.FIELD_Z;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.selection.RecordSelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.util.mapping.color.ColorMapper;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.ColorMappingType;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.template.AHeatMapTemplate;

public class HeatMapRenderer extends AHeatMapRenderer {

	private ColorMapper colorMapper;

	public HeatMapRenderer(GLHeatMap heatMap) {
		super(heatMap);
		colorMapper = ColorMappingManager.get().getColorMapping(
				ColorMappingType.GENE_EXPRESSION);
	}

	@Override
	public void updateSpacing(ElementLayout parameters) {

		AHeatMapTemplate heatMapTemplate = heatMap.getTemplate();

		int contentElements = heatMap.getRecordVA().size();

		RecordSelectionManager selectionManager = heatMap.getRecordSelectionManager();
		if (heatMap.isHideElements()) {

			contentElements -= selectionManager
					.getNumberOfElements(GLHeatMap.SELECTION_HIDDEN);
		}

		recordSpacing.calculateRecordSpacing(contentElements, heatMap.getDimensionVA()
				.size(), parameters.getSizeScaledX(), parameters.getSizeScaledY(),
				heatMapTemplate.getMinSelectedFieldHeight());
		heatMapTemplate.setContentSpacing(recordSpacing);
	}

	@Override
	public void render(final GL2 gl) {

		recordSpacing.getYDistances().clear();
		float yPosition = y;
		float xPosition = 0;
		float fieldHeight = 0;
		float fieldWidth = recordSpacing.getFieldWidth();

		DimensionVirtualArray recordVA = heatMap.getDimensionVA();

		for (Integer recordID : heatMap.getRecordVA()) {
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

			for (Integer dimensionID : recordVA) {

				renderElement(gl, dimensionID, recordID, yPosition, xPosition,
						fieldHeight, fieldWidth);

				xPosition += fieldWidth;

			}

			recordSpacing.getYDistances().add(yPosition);
		}
	}

	private void renderElement(final GL2 gl, final int dimensionID, final int recordID,
			final float fYPosition, final float fXPosition, final float fFieldHeight,
			final float fFieldWidth) {

		// GLHelperFunctions.drawPointAt(gl, 0, fYPosition, 0);

		float value = heatMap.getTable().getFloat(heatMap.getRenderingRepresentation(),
				dimensionID, recordID);

		float fOpacity = 1.0f;

		if (heatMap.getTable().containsDataRepresentation(
				DataRepresentation.UNCERTAINTY_NORMALIZED, dimensionID, recordID)) {
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
				PickingType.HEAT_MAP_LINE_SELECTION, recordID));
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
					- recordSpacing
							.getFieldHeight(heatMap.getRecordVA().get(recordIndex)) / 2;
		return 0;
	}

	public float getXCoordinateByDimensionIndex(int dimensionIndex) {
		return recordSpacing.getFieldWidth() * dimensionIndex;
	}

	@Override
	public String toString() {
		return "HeatMapRenderer";
	}

}
