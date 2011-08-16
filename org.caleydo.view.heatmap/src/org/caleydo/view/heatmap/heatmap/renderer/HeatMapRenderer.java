package org.caleydo.view.heatmap.heatmap.renderer;

import static org.caleydo.view.heatmap.HeatMapRenderStyle.FIELD_Z;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.dimension.ADimension;
import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.selection.RecordSelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.util.mapping.color.ColorMapper;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.template.AHeatMapTemplate;

public class HeatMapRenderer extends AContentRenderer {

	private ColorMapper colorMapper;

	// private Set<Integer> setSelectedElements;

	public HeatMapRenderer(GLHeatMap heatMap) {
		super(heatMap);
		colorMapper = ColorMappingManager.get().getColorMapping(
				EColorMappingType.GENE_EXPRESSION);
	}

	@Override
	public void updateSpacing(ElementLayout parameters) {

		AHeatMapTemplate heatMapTemplate = heatMap.getTemplate();

		int contentElements = heatMap.getRecordVA().size();

		RecordSelectionManager selectionManager = heatMap.getContentSelectionManager();
		if (heatMap.isHideElements()) {

			contentElements -= selectionManager
					.getNumberOfElements(GLHeatMap.SELECTION_HIDDEN);
		}

		contentSpacing.calculateContentSpacing(contentElements, heatMap.getDimensionVA()
				.size(), parameters.getSizeScaledX(), parameters.getSizeScaledY(),
				heatMapTemplate.getMinSelectedFieldHeight());
		heatMapTemplate.setContentSpacing(contentSpacing);

		// ((AContentRenderer) renderer).setContentSpacing(contentSpacing);
	}

	@Override
	public void render(final GL2 gl) {

		contentSpacing.getYDistances().clear();
		float yPosition = y;
		float xPosition = 0;
		float fieldHeight = 0;
		float fieldWidth = contentSpacing.getFieldWidth();

		int iCount = 0;

		for (Integer recordID : heatMap.getRecordVA()) {
			iCount++;
			fieldHeight = contentSpacing.getFieldHeight(recordID);

			// we treat normal and deselected the same atm

			if (heatMap.isHideElements()
					&& heatMap.getContentSelectionManager().checkStatus(
							GLHeatMap.SELECTION_HIDDEN, recordID)) {
				contentSpacing.getYDistances().add(yPosition);
				continue;
			}

			yPosition -= fieldHeight;
			xPosition = 0;

			for (Integer iDimensionIndex : heatMap.getDimensionVA()) {

				renderElement(gl, iDimensionIndex, recordID, yPosition, xPosition,
						fieldHeight, fieldWidth);

				xPosition += fieldWidth;

			}

			contentSpacing.getYDistances().add(yPosition);

		}
	}

	private void renderElement(final GL2 gl, final int iDimensionIndex,
			final int recordIndex, final float fYPosition, final float fXPosition,
			final float fFieldHeight, final float fFieldWidth) {

		// GLHelperFunctions.drawPointAt(gl, 0, fYPosition, 0);
		ADimension dimension = heatMap.getTable().get(iDimensionIndex);
		if (dimension == null)
			return;
		float value = dimension.getFloat(heatMap.getRenderingRepresentation(), recordIndex);

		float fOpacity = 1.0f;

		if (dimension
				.containsDataRepresentation(DataRepresentation.UNCERTAINTY_NORMALIZED)) {
			// setSelectedElements = heatMap.getContentSelectionManager()
			// .getElements(SelectionType.MOUSE_OVER);
			// for (Integer selectedElement : setSelectedElements) {
			// if (recordIndex == selectedElement.intValue()) {
			// fOpacity = dimension.getFloat(
			// EDataRepresentation.UNCERTAINTY_NORMALIZED,
			// recordIndex);
			// }
			// }
		} else if (heatMap.getContentSelectionManager().checkStatus(
				SelectionType.DESELECTED, recordIndex)) {
			fOpacity = 0.3f;
		}

		float[] fArMappingColor = colorMapper.getColor(value);

		gl.glColor4f(fArMappingColor[0], fArMappingColor[1], fArMappingColor[2], fOpacity);

		gl.glPushName(heatMap.getPickingManager().getPickingID(heatMap.getID(),
				PickingType.HEAT_MAP_DIMENSION_SELECTION, iDimensionIndex));
		gl.glPushName(heatMap.getPickingManager().getPickingID(heatMap.getID(),
				PickingType.HEAT_MAP_LINE_SELECTION, recordIndex));
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
		if (contentSpacing != null)
			return y
					- contentSpacing.getYDistances().get(recordIndex)
					- contentSpacing.getFieldHeight(heatMap.getRecordVA().get(
							recordIndex)) / 2;
		return 0;
	}

	public float getXCoordinateByDimensionIndex(int dimensionIndex) {
		return contentSpacing.getFieldWidth() * dimensionIndex;
	}

	@Override
	public String toString() {
		return "HeatMapRenderer";
	}

}
