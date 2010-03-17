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

	ArrayList<Float> yDistances;

	public HeatMapRenderer(GLHeatMap heatMap) {
		super(heatMap);
		colorMapper = ColorMappingManager.get().getColorMapping(
				EColorMappingType.GENE_EXPRESSION);

		yDistances = new ArrayList<Float>();

	}

	public void render(final GL gl) {

		yDistances.clear();
		// renderStyle.updateFieldSizes();
		float yPosition = y;
		float xPosition = 0;
		float fieldHeight = 0;

		// renderStyle.clearFieldWidths();
		int iCount = 0;

		for (Integer iContentIndex : heatMap.getContentVA()) {
			iCount++;
			// we treat normal and deselected the same atm

			if (heatMap.getContentSelectionManager().checkStatus(
					SelectionType.SELECTION, iContentIndex)
					|| heatMap.getContentSelectionManager().checkStatus(
							SelectionType.MOUSE_OVER, iContentIndex)) {
				fieldHeight = selectedFieldHeight;
				// currentType = SelectionType.SELECTION;
			} else if (heatMap.isHideElements()
					&& heatMap.getContentSelectionManager().checkStatus(
							GLHeatMap.SELECTION_HIDDEN, iContentIndex)) {
				yDistances.add(yPosition);
				continue;
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
			yDistances.add(yPosition);

		}
		renderSelection(gl, SelectionType.SELECTION);
		renderSelection(gl, SelectionType.MOUSE_OVER);
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

	public void renderSelection(final GL gl, SelectionType selectionType) {

		// content selection
		Set<Integer> selectedSet = heatMap.getContentSelectionManager()
				.getElements(selectionType);
		float width = 0;
		float yPosition = y;
		float xPosition = 0;

		if (selectionType == SelectionType.SELECTION) {
			gl.glColor4fv(SELECTED_COLOR, 0);
			gl.glLineWidth(SELECTED_LINE_WIDTH);
		} else if (selectionType == SelectionType.MOUSE_OVER) {
			gl.glColor4fv(MOUSE_OVER_COLOR, 0);
			gl.glLineWidth(MOUSE_OVER_LINE_WIDTH);
		}

		int lineIndex = 0;
		for (int tempLine : heatMap.getContentVA()) {
			for (Integer currentLine : selectedSet) {
				if (currentLine == tempLine) {
					width = heatMap.getStorageVA().size() * fieldWidth;
					yPosition = yDistances.get(lineIndex);
					xPosition = 0;
					gl.glPushName(heatMap.getPickingManager().getPickingID(
							heatMap.getID(),
							EPickingType.HEAT_MAP_LINE_SELECTION, currentLine));

					gl.glBegin(GL.GL_LINE_LOOP);
					gl.glVertex3f(xPosition, yPosition, SELECTION_Z);
					gl.glVertex3f(xPosition, yPosition + selectedFieldHeight,
							SELECTION_Z);
					gl.glVertex3f(xPosition + width, yPosition
							+ selectedFieldHeight, SELECTION_Z);
					gl.glVertex3f(xPosition + width, yPosition, SELECTION_Z);
					gl.glEnd();
					gl.glPopName();
				}
			}
			lineIndex++;
		}

		// storage selection
		gl.glEnable(GL.GL_LINE_STIPPLE);
		gl.glLineStipple(2, (short) 0xAAAA);

		selectedSet = heatMap.getStorageSelectionManager().getElements(
				selectionType);
		int columnIndex = 0;
		for (int tempColumn : heatMap.getStorageVA()) {
			for (Integer selectedColumn : selectedSet) {
				if (tempColumn == selectedColumn) {
					// TODO we need indices of all elements

					xPosition = columnIndex * fieldWidth;

					gl.glPushName(heatMap.getPickingManager().getPickingID(
							heatMap.getID(),
							EPickingType.HEAT_MAP_STORAGE_SELECTION,
							selectedColumn));

					gl.glBegin(GL.GL_LINE_LOOP);
					gl.glVertex3f(xPosition, y, SELECTION_Z);
					gl.glVertex3f(xPosition, 0, SELECTION_Z);
					gl.glVertex3f(xPosition + fieldWidth, 0, SELECTION_Z);
					gl.glVertex3f(xPosition + fieldWidth, y, SELECTION_Z);
					gl.glEnd();
					gl.glPopName();
				}
			}
			columnIndex++;
		}

		gl.glDisable(GL.GL_LINE_STIPPLE);
	}

	public float getYCoordinateByContentIndex(int contentIndex) {
		if (!yDistances.isEmpty())
			return y - yDistances.get(contentIndex) - normalFieldHeight / 2;
		else {
			return contentIndex * normalFieldHeight - normalFieldHeight / 2;
		}
	}

}
