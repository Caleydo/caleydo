package org.caleydo.view.heatmap.heatmap.renderer;

import javax.media.opengl.GL2;

import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.view.heatmap.HeatMapRenderStyle;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

/**
 * Rendering the dimension captions for the GLHeatmap
 * 
 * @author Alexander Lex
 * 
 */
public class DimensionCaptionRenderer extends AContentRenderer {

	private float fFontScaling = GeneralRenderStyle.SMALL_FONT_SCALING_FACTOR / 1.2f;

	public DimensionCaptionRenderer(GLHeatMap heatMap) {
		super(heatMap);
	}

	@Override
	public void render(GL2 gl) {

		DimensionVirtualArray dimensionVA = heatMap.getDimensionVA();
		heatMap.getTextRenderer().setColor(0, 0, 0, 1);

		float xPosition = 0;
		float fieldWidth = contentSpacing.getFieldWidth();
		// offset to center the caption
		float xOffset = 0;

		float height = (float) heatMap.getTextRenderer().getScaledBounds(gl, "TEST", fFontScaling,
				HeatMapRenderStyle.LABEL_TEXT_MIN_SIZE).getHeight();

		if (fieldWidth > height)
			xOffset = (fieldWidth - height) / 2;
		for (Integer dimensionID : dimensionVA) {
			String label = heatMap.getDataDomain().getDimensionLabel(dimensionID);

			float fRotation = -90;
			float yOffset = elementLayout.getSizeScaledY();

			gl.glTranslatef(xPosition + xOffset, yOffset, 0);
			gl.glRotatef(fRotation, 0, 0, 1);
			heatMap.getTextRenderer().renderText(gl, label, 0, 0f, 0, fFontScaling,
					HeatMapRenderStyle.LABEL_TEXT_MIN_SIZE);
			gl.glRotatef(-fRotation, 0, 0, 1);
			gl.glTranslatef(-xPosition - xOffset, -yOffset, 0);
			xPosition += fieldWidth;

		}
	}
}
