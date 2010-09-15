package org.caleydo.view.heatmap.heatmap.renderer;

import static org.caleydo.view.heatmap.HeatMapRenderStyle.FIELD_Z;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

public class HeatMapRenderer extends AContentRenderer {

	private ColorMapping colorMapper;

	public HeatMapRenderer(GLHeatMap heatMap) {
		super(heatMap);
		colorMapper = ColorMappingManager.get().getColorMapping(
				EColorMappingType.GENE_EXPRESSION);
	}

	@Override
	public void render(final GL gl) {

		contentSpacing.getYDistances().clear();
		// renderStyle.updateFieldSizes();
		float yPosition = y;
		float xPosition = 0;
		float fieldHeight = 0;
		float fieldWidth = contentSpacing.getFieldWidth();

		// renderStyle.clearFieldWidths();
		int iCount = 0;

		for (Integer contentID : heatMap.getContentVA()) {
			iCount++;
			fieldHeight = contentSpacing.getFieldHeight(contentID);

			// we treat normal and deselected the same atm

			if (heatMap.isHideElements()
					&& heatMap.getContentSelectionManager().checkStatus(
							GLHeatMap.SELECTION_HIDDEN, contentID)) {
				contentSpacing.getYDistances().add(yPosition);
				continue;
			}

			yPosition -= fieldHeight;
			xPosition = 0;

			for (Integer iStorageIndex : heatMap.getStorageVA()) {

				renderElement(gl, iStorageIndex, contentID, yPosition, xPosition,
						fieldHeight, fieldWidth);

				xPosition += fieldWidth;

			}

			contentSpacing.getYDistances().add(yPosition);

		}
	}

	private void renderElement(final GL gl, final int iStorageIndex,
			final int iContentIndex, final float fYPosition, final float fXPosition,
			final float fFieldHeight, final float fFieldWidth) {

		IStorage storage = heatMap.getSet().get(iStorageIndex);
		float value = storage.getFloat(EDataRepresentation.NORMALIZED, iContentIndex);

		float fOpacity = 0;
		if (heatMap.getContentSelectionManager().checkStatus(SelectionType.DESELECTED,
				iContentIndex)) {
			fOpacity = 0.3f;
		} else {
			fOpacity = 1.0f;
		}

		float[] fArMappingColor = colorMapper.getColor(value);

		gl.glColor4f(fArMappingColor[0], fArMappingColor[1], fArMappingColor[2], fOpacity);

		gl.glPushName(heatMap.getPickingManager().getPickingID(heatMap.getID(),
				EPickingType.HEAT_MAP_STORAGE_SELECTION, iStorageIndex));
		gl.glPushName(heatMap.getPickingManager().getPickingID(heatMap.getID(),
				EPickingType.HEAT_MAP_LINE_SELECTION, iContentIndex));
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(fXPosition, fYPosition, FIELD_Z);
		gl.glVertex3f(fXPosition + fFieldWidth, fYPosition, FIELD_Z);
		gl.glVertex3f(fXPosition + fFieldWidth, fYPosition + fFieldHeight, FIELD_Z);
		gl.glVertex3f(fXPosition, fYPosition + fFieldHeight, FIELD_Z);
		gl.glEnd();

		gl.glPopName();
		gl.glPopName();
	}

	public float getYCoordinateByContentIndex(int contentIndex) {
		if (contentSpacing != null)
			return y
					- contentSpacing.getYDistances().get(contentIndex)
					- contentSpacing.getFieldHeight(heatMap.getContentVA().get(
							contentIndex)) / 2;
		return 0;
	}

	public float getXCoordinateByStorageIndex(int storageIndex) {
		return contentSpacing.getFieldWidth() * storageIndex;
	}

}
