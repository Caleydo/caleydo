package org.caleydo.view.heatmap.heatmap.renderer;

import java.awt.Font;

import javax.media.opengl.GL2;

import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.heatmap.HeatMapRenderStyle;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

public class ContentCaptionRenderer extends AContentRenderer {

	private CaleydoTextRenderer textRenderer;

	float fontScaling = HeatMapRenderStyle.SMALL_FONT_SCALING_FACTOR;
	int fontSize = 24;
	float spacing = 0;

	public ContentCaptionRenderer(GLHeatMap heatMap) {
		super(heatMap);

		textRenderer = new CaleydoTextRenderer(new Font("Arial", Font.PLAIN, fontSize),
				false);
	}

	@Override
	public void render(GL2 gl) {

		float yPosition = y;
		float fieldHeight = 0;

		ContentVirtualArray contentVA = heatMap.getContentVA();

		for (Integer contentID : contentVA) {

			if (heatMap.isHideElements()
					&& heatMap.getContentSelectionManager().checkStatus(
							GLHeatMap.SELECTION_HIDDEN, contentID)) {
				continue;
			}

			fieldHeight = contentSpacing.getFieldHeight(contentID);
			if (fieldHeight < HeatMapRenderStyle.MIN_FIELD_HEIGHT_FOR_CAPTION)
				continue;

			yPosition = contentSpacing.getYDistances().get(contentVA.indexOf(contentID));

			textRenderer.setColor(0, 0, 0, 1);

			renderCaption(gl, contentID, 0, yPosition, 0, fontScaling);

		}
	}

	public void setFontScaling(float fontScaling) {
		this.fontScaling = fontScaling;
	}

	private String getID(Integer contentID, boolean beVerbose) {
		return heatMap.getDataDomain().getContentLabel(contentID);
	}

	private void renderCaption(GL2 gl, int contentIndex, float xOrigin, float yOrigin,
			float zOrigin, float fontScaling) {

		String sLabel = getID(contentIndex, false);
		if (sLabel == null)
			sLabel = "Unknown";

		if (sLabel.length() > GeneralRenderStyle.NUM_CHAR_LIMIT + 1) {
			sLabel = sLabel.substring(0, GeneralRenderStyle.NUM_CHAR_LIMIT - 2);
			sLabel = sLabel + "..";
		}

		float requiredSize = (float) textRenderer.getScaledBounds(gl, sLabel,
				fontScaling, fontSize).getHeight();

		spacing = (contentSpacing.getFieldHeight(contentIndex) - requiredSize) / 2;
		if (spacing < 0)
			spacing = 0;

		// textRenderer.setColor(0, 0, 0, 1);
		gl.glPushAttrib(GL2.GL_CURRENT_BIT | GL2.GL_LINE_BIT);
		gl.glTranslatef(xOrigin, yOrigin + spacing, zOrigin);

		textRenderer.renderText(gl, sLabel, 0, 0, 0, fontScaling,
				HeatMapRenderStyle.LABEL_TEXT_MIN_SIZE);
		gl.glTranslatef(-xOrigin, -yOrigin - spacing, -zOrigin);
		// textRenderer.begin3DRendering();
		gl.glPopAttrib();
	}

}
