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

	public MinSizeTextRenderer(int fontSize) {
		caleydoTextRenderer = new CaleydoTextRenderer(new Font("SansSerif", Font.PLAIN, fontSize));
	}

	public void setWindowSize(double windowWidth, double windowHeight) {
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;
	}

	public void renderText(GL2 gl, String text, float x, float y, float z) {
		caleydoTextRenderer.renderText(gl, text, x, y, z, fontScaling, minSize);
	}

	/**
	 * Render the text at the position specified (lower left corner) within the bounding box The height is
	 * scaled to fit, the string is truncated to fit the width
	 * 
	 * @param gl
	 * @param text
	 * @param xPosition
	 *            x of lower left corner
	 * @param yPosition
	 *            y of lower left corner
	 * @param zPositon
	 * @param widht
	 *            width fo the bounding box
	 * @param height
	 *            height of the bounding box
	 */
	public void renderTextInBounds(GL2 gl, String text, float xPosition, float yPosition, float zPositon,
		float widht, float height) {

		Rectangle2D bounds = caleydoTextRenderer.getBounds(text);

		float scaling = height / (float) bounds.getHeight();
		// if(scaling > fontScaling * 2)
		// scaling = fontScaling * 2;
		float requiredWidth = ((float) bounds.getWidth() * scaling);
		if (requiredWidth > widht) {
			float truncateFactor = widht / requiredWidth;
			int length = text.length();
			length *= truncateFactor;
			text = text.substring(0, length);
			// System.out.println("toBigBy " + (widht / requiredWidth) + " for " + text);

		}
		caleydoTextRenderer.renderText(gl, text, xPosition, yPosition, zPositon, scaling, minSize);
	}

	public Rectangle2D getBounds(String text) {

		Rectangle2D bounds = caleydoTextRenderer.getBounds(text);
		float scaling = calculateScaling();

		bounds.setRect(0, 0, bounds.getWidth() * scaling * fontScaling, bounds.getHeight() * scaling
			* fontScaling);
		return bounds;
	}

	public void setColor(float red, float green, float blue, float alpha) {
		caleydoTextRenderer.setColor(red, green, blue, alpha);
	}

	public void setColor(float[] color) {
		setColor(color[0], color[1], color[2], 1);
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
