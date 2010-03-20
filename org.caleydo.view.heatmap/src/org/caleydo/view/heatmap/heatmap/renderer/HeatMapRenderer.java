package org.caleydo.view.heatmap.heatmap.renderer;

import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.MOUSE_OVER_COLOR;
import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.MOUSE_OVER_LINE_WIDTH;
import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.SELECTED_COLOR;
import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.SELECTED_LINE_WIDTH;
import static org.caleydo.view.heatmap.HeatMapRenderStyle.FIELD_Z;
import static org.caleydo.view.heatmap.HeatMapRenderStyle.SELECTION_Z;

import java.util.ArrayList;
import java.util.Set;

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

	public void render(final GL gl) {

		contentSpacing.yDistances.clear();
		// renderStyle.updateFieldSizes();
		float yPosition = y;
		float xPosition = 0;
		float fieldHeight = 0;

		// renderStyle.clearFieldWidths();
		int iCount = 0;

		for (Integer iContentIndex : heatMap.getContentVA()) {
			iCount++;
			// we treat normal and deselected the same atm

			if (heatMap.isHideElements()
					&& heatMap.getContentSelectionManager().checkStatus(
							GLHeatMap.SELECTION_HIDDEN, iContentIndex)) {
				contentSpacing.yDistances.add(yPosition);
				continue;
			} else if (heatMap.getContentSelectionManager().checkStatus(
					SelectionType.SELECTION, iContentIndex)
					|| heatMap.getContentSelectionManager().checkStatus(
							SelectionType.MOUSE_OVER, iContentIndex)) {
				fieldHeight = selectedFieldHeight;
				// currentType = SelectionType.SELECTION;

			} else {

				fieldHeight = normalFieldHeight;
				// currentType = SelectionType.NORMAL;
			}
			yPosition -= fieldHeight;
			xPosition = 0;

			for (Integer iStorageIndex : heatMap.getStorageVA()) {

				renderElement(gl, iStorageIndex, iContentIndex, yPosition,
						xPosition, fieldHeight, fieldWidth);

				xPosition += fieldWidth;

			}

			// renderStyle.setXDistanceAt(contentVA.indexOf(iContentIndex),
			// fXPosition);
			contentSpacing.yDistances.add(yPosition);

		}
	}

	private void renderElement(final GL gl, final int iStorageIndex,
			final int iContentIndex, final float fYPosition,
			final float fXPosition, final float fFieldHeight,
			final float fFieldWidth) {

		IStorage storage = heatMap.getSet().get(iStorageIndex);
		float fLookupValue = storage.getFloat(EDataRepresentation.NORMALIZED,
				iContentIndex);

		float fOpacity = 0;
		if (heatMap.getContentSelectionManager().checkStatus(
				SelectionType.DESELECTED, iContentIndex)) {
			fOpacity = 0.3f;
		} else {
			fOpacity = 1.0f;
		}

		float[] fArMappingColor = colorMapper.getColor(fLookupValue);

		gl.glColor4f(fArMappingColor[0], fArMappingColor[1],
				fArMappingColor[2], fOpacity);

		gl.glPushName(heatMap.getPickingManager().getPickingID(heatMap.getID(),
				EPickingType.HEAT_MAP_STORAGE_SELECTION, iStorageIndex));
		gl.glPushName(heatMap.getPickingManager().getPickingID(heatMap.getID(),
				EPickingType.HEAT_MAP_LINE_SELECTION, iContentIndex));
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(fXPosition, fYPosition, FIELD_Z);
		gl.glVertex3f(fXPosition + fFieldWidth, fYPosition, FIELD_Z);
		gl.glVertex3f(fXPosition + fFieldWidth, fYPosition + fFieldHeight,
				FIELD_Z);
		gl.glVertex3f(fXPosition, fYPosition + fFieldHeight, FIELD_Z);
		gl.glEnd();

		gl.glPopName();
		gl.glPopName();
	}

	public float getYCoordinateByContentIndex(int contentIndex) {
		if (!contentSpacing.yDistances.isEmpty())
			return y - contentSpacing.yDistances.get(contentIndex)
					- normalFieldHeight / 2;
		else {
			return contentIndex * normalFieldHeight - normalFieldHeight / 2;
		}
	}

	public float getXCoordinateByStorageIndex(int storageIndex) {
		return fieldWidth * storageIndex;
	}

}
