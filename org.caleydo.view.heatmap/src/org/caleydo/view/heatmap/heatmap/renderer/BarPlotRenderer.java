package org.caleydo.view.heatmap.heatmap.renderer;

import static org.caleydo.view.heatmap.HeatMapRenderStyle.FIELD_Z;

import java.util.ArrayList;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.selection.RecordSelectionManager;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.template.AHeatMapTemplate;
import org.caleydo.view.heatmap.uncertainty.GLUncertaintyHeatMap;

public class BarPlotRenderer extends AContentRenderer {

	ArrayList<Float> uncertainties = new ArrayList<Float>();
	private boolean isDirty = false;
	private GLUncertaintyHeatMap uncertaintyHeatmap;

	public BarPlotRenderer(GLHeatMap heatMap,
			GLUncertaintyHeatMap uncertaintyHeatmap) {
		super(heatMap);
		this.uncertaintyHeatmap = uncertaintyHeatmap;
	}

	@Override
	public void updateSpacing(ElementLayout parameters) {

		AHeatMapTemplate heatMapTemplate = heatMap.getTemplate();

		int contentElements = heatMap.getRecordVA().size();

		RecordSelectionManager selectionManager = heatMap
				.getContentSelectionManager();
		if (heatMap.isHideElements()) {

			contentElements -= selectionManager
					.getNumberOfElements(GLHeatMap.SELECTION_HIDDEN);
		}

		contentSpacing.calculateContentSpacing(contentElements, heatMap
				.getDimensionVA().size(), parameters.getSizeScaledX(), parameters
				.getSizeScaledY(), heatMapTemplate.getMinSelectedFieldHeight());
		heatMapTemplate.setContentSpacing(contentSpacing);

		// ((AContentRenderer) renderer).setContentSpacing(contentSpacing);
	}

	@Override
	public void render(final GL2 gl) {

		contentSpacing.getYDistances().clear();
		float yPosition = y;
		float xPosition = 0;
		float fieldHeight = 0;
		float fieldWidth = x;

		int iCount = 0;

		if (this.isDirty) {
			isDirty = false;
		}

		ArrayList<double[]> uncertainties = uncertaintyHeatmap
				.getMultiLevelUncertainty();

		// GLHelperFunctions.drawPointAt(gl, 0, fYPosition, 0);
		DataTable set = heatMap.getTable();
		if (set == null)
			return;

		PixelGLConverter conv = heatMap.getPixelGLConverter();

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

			int screenHeight = conv.getPixelHeightForGLHeight(fieldHeight);

			float uncertaintyMax = uncertaintyHeatmap
					.getMaxUncertainty(recordID);
			if (screenHeight < 15) {
				renderBlock(gl, yPosition, xPosition, fieldHeight, fieldWidth,
						uncertaintyMax,
						GLUncertaintyHeatMap.getUncertaintyColor(0));
			} else {

				for (int i = 0; i < uncertainties.size(); i++) {

					float uncertainty = (float) uncertainties.get(i)[recordID];

					float height = fieldHeight / (float) uncertainties.size();
					float yPos = yPosition + height
							* (uncertainties.size() - i - 1);
					// certainty[i] = certainty[i] > 1 ? 1 : certainty[i];
					// certainty[i] = certainty[i] < 0 ? 0 : certainty[i];

					// in case of not normalized values, show valid data as well
					// if (uncertainty >= 1) {
					// renderLine(gl, yPos, xPosition, height, fieldWidth,
					// uncertainty,
					// GLUncertaintyHeatMap.DATA_VALID[i]);
					// } else {
					renderBlock(gl, yPos, xPosition, height, fieldWidth,
							uncertainty,
							GLUncertaintyHeatMap.getUncertaintyColor(i + 1));
					// }
				}
			}
			if (screenHeight >= 1) {
				renderVLine(gl, yPosition, xPosition + fieldWidth
						* (uncertaintyMax), fieldHeight, 
						GLUncertaintyHeatMap.getUncertaintyColor(0));
			}

			contentSpacing.getYDistances().add(yPosition);

		}
	}

	private void renderBlock(final GL2 gl, final float fYPosition,
			final float fXPosition, final float fFieldHeight,
			final float fFieldWidth, final float uncertainty, float[] rgba) {

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
		gl.glVertex3f(fXPosition + unCertainWidth, fYPosition + fFieldHeight,
				FIELD_Z);
		gl.glVertex3f(fXPosition, fYPosition + fFieldHeight, FIELD_Z);
		gl.glEnd();

		// certain
		gl.glBegin(GL2.GL_POLYGON);
		gl.glColor4fv(rgba, 0);
		gl.glVertex3f(fXPosition + unCertainWidth, fYPosition, FIELD_Z);
		gl.glVertex3f(fXPosition + certainWidth + unCertainWidth, fYPosition,
				FIELD_Z);
		gl.glVertex3f(fXPosition + certainWidth + unCertainWidth, fYPosition
				+ fFieldHeight, FIELD_Z);
		gl.glVertex3f(fXPosition + unCertainWidth, fYPosition + fFieldHeight,
				FIELD_Z);
		gl.glEnd();

		// gl.glPopName();
		// gl.glPopName();
	}

	private void renderVLine(final GL2 gl, final float fYPosition,
			final float fXPosition, 
			final float fFieldHeight,
			float[] rgba) {

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
		return "BarPlotRenderer";
	}

}
