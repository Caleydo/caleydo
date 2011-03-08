package org.caleydo.core.view.opengl.util.text;

import java.awt.Font;
import java.awt.geom.Rectangle2D;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

public class MinSizeTextRenderer {
	CaleydoTextRenderer caleydoTextRenderer;

	double windowWidth = 0;
	double windowHeight = 0;

	int minSize = GeneralRenderStyle.TEXT_MIN_SIZE;
	float fontScaling = GeneralRenderStyle.SMALL_FONT_SCALING_FACTOR;

	public MinSizeTextRenderer() {
		caleydoTextRenderer = new CaleydoTextRenderer(new Font("SansSerif", Font.PLAIN, 25));
	}

	public void setWindowSize(double windowWidth, double windowHeight) {
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;
	}

	public void renderText(GL2 gl, String text, float x, float y, float z) {
		caleydoTextRenderer.renderText(gl, text, x, y, z, fontScaling, minSize);
	}

	public Rectangle2D getBounds(String text) {

		Rectangle2D bounds = caleydoTextRenderer.getBounds(text);
		float scaling = calculateScaling();
		bounds.setRect(0, 0, bounds.getWidth() * scaling * fontScaling, bounds.getHeight()
			* scaling * fontScaling);
		return bounds;
	}

	public void setColor(float red, float green, float blue, float alpha) {
		caleydoTextRenderer.setColor(red, green, blue, alpha);
	}

	private float calculateScaling() {

		float referenceWidth =
			minSize / (float) caleydoTextRenderer.getReferenceBounds().getHeight() * 500.0f;
		float scaling = 1;

		if (referenceWidth > windowWidth)
			scaling = scaling * referenceWidth / (float) windowWidth;

		return scaling;
	}

}
