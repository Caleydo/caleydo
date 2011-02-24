package org.caleydo.core.view.opengl.canvas;

import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Converts pixel space to gl space for a given canvas and frustum. Most useful for creating static sized gl
 * elements.
 * 
 * @author Alexander Lex
 */
public class PixelGLConverter {
	ViewFrustum viewFrustum;
	GLCaleydoCanvas canvas;

	/**
	 * The constructor requires a ViewFrustum and a GLCaleydoCanvas. Notice, that both have to be from the
	 * same top level, locally rendered view - i.e., viewFrustums of embedded views don't work.
	 * 
	 * @param viewFrustum
	 * @param canvas
	 */
	public PixelGLConverter(ViewFrustum viewFrustum, GLCaleydoCanvas canvas) {
		this.viewFrustum = viewFrustum;
		this.canvas = canvas;
	}

	public float getGLWidthForPixelWidth(int pixelWidth) {
		float totalWidthGL = viewFrustum.getWidth();
		Double totalWidthPixel = canvas.getBounds().getWidth();
		if (totalWidthPixel == null || totalWidthPixel <= 0)
			throw new IllegalStateException("Width of Canvas in pixel is " + totalWidthPixel
				+ ". It's likely that the canvas is not initialized.");

		float width = totalWidthGL / totalWidthPixel.floatValue() * pixelWidth;
		return width;
	}

	public float getGLHeightForPixelHeight(int pixelHeight) {
		float totalHeightGL = viewFrustum.getHeight();
		Double totalHeightPixel = canvas.getBounds().getHeight();

		if (totalHeightPixel == null || totalHeightPixel <= 0)
			throw new IllegalStateException("Height of Canvas in pixel is " + totalHeightPixel
				+ ". It's likely that the canvas is not initialized.");
		float height = totalHeightGL / totalHeightPixel.floatValue() * pixelHeight;
		return height;
	}
}
