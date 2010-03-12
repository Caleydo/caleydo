package org.caleydo.view.compare.rendercommand;

import gleem.linalg.Vec3f;

import java.awt.geom.Rectangle2D;

import javax.media.opengl.GL;

import org.caleydo.view.compare.AHeatMapLayout;
import org.caleydo.view.compare.HeatMapWrapper;

import com.sun.opengl.util.j2d.TextRenderer;

public class CaptionLabelRenderCommand implements IHeatMapRenderCommand {

	private static final float VERTICAL_TEXT_SPACING = 0.1f;
	private static final float HORIZONTAL_TEXT_SPACING = 0.001f;

	private TextRenderer textRenderer;

	public CaptionLabelRenderCommand(TextRenderer textRenderer) {
		this.textRenderer = textRenderer;
	}

	@Override
	public ERenderCommandType getRenderCommandType() {
		return ERenderCommandType.CAPTION_LABEL;
	}

	@Override
	public void render(GL gl, HeatMapWrapper heatMapWrapper) {

		String textToRender = heatMapWrapper.getCaption();
		AHeatMapLayout layout = heatMapWrapper.getLayout();

		Rectangle2D bounds = textRenderer.getBounds(textToRender);
		float captionLabelHeight = layout.getCaptionLabelHeight();
		float captionLabelWidth = layout.getCaptionLabelWidth();
		float fFontScaling = determineFontScaling(gl, textToRender, layout);
		Vec3f captionLabelPosition = layout
				.getCaptionLabelPosition((float) bounds.getWidth()
						* fFontScaling);

		// float fTextPositionX = fWidth / 2.0f
		// - ((float) bounds.getWidth() / 2.0f) * fFontScaling;
		// float fTextPositionY = fSlidingElementDrawingPosition
		// + fSlidingElementHeight / 2.0f
		// - ((float) bounds.getHeight() / 2.0f) * fFontScaling;
		textRenderer.setColor(0, 0, 0, 1);
		textRenderer.begin3DRendering();

		textRenderer.draw3D(textToRender, captionLabelPosition.x(),
				captionLabelPosition.y(), captionLabelPosition.z(),
				fFontScaling);

		textRenderer.end3DRendering();
		textRenderer.flush();

	}

	/**
	 * Determines the scaling of a specified text that is needed for this text
	 * to fit into the label.
	 * 
	 * @param sText
	 *            Text the scaling shall be calculated for.
	 * @return Scaling factor for the specified text.
	 */
	private float determineFontScaling(GL gl, String text, AHeatMapLayout layout) {

		float captionLabelHeight = layout.getCaptionLabelHeight();
		float captionLabelWidth = layout.getCaptionLabelWidth();

		Rectangle2D bounds = textRenderer.getBounds(text);
		float fScalingWidth = captionLabelWidth
				/ (float) bounds.getWidth();
		float fScalingHeight = captionLabelHeight
				/ (float) bounds.getHeight();

		return Math.min(fScalingHeight, fScalingWidth);
	}

}
