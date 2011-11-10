package org.caleydo.core.view.opengl.canvas;

import java.nio.FloatBuffer;

import javax.media.opengl.GL2;
import javax.media.opengl.awt.GLCanvas;

import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Converts pixel space to gl space for a given canvas and frustum. Most useful for creating static sized gl
 * elements.
 * 
 * @author Alexander Lex
 */
public class PixelGLConverter {
	ViewFrustum viewFrustum;
	GLCanvas canvas;

	/**
	 * The constructor requires a ViewFrustum and a GLCanvas. Notice, that both have to be from the same top
	 * level, locally rendered view - i.e., viewFrustums of embedded views don't work.
	 * 
	 * @param viewFrustum
	 * @param canvas
	 */
	PixelGLConverter(ViewFrustum viewFrustum, GLCanvas canvas) {
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

	public float getGLHeightForGLWidth(float glWidth) {
		int pixelWidth = getPixelWidthForGLWidth(glWidth);

		return getGLHeightForPixelHeight(pixelWidth);
	}

	public float getGLWidthForGLHeight(float glHeight) {
		int pixelHeight = getPixelHeightForGLHeight(glHeight);

		return getGLWidthForPixelWidth(pixelHeight);
	}

	public int getPixelWidthForGLWidth(float glWidth) {
		float totalWidthGL = viewFrustum.getWidth();
		Double totalWidthPixel = canvas.getBounds().getWidth();
		if (totalWidthPixel == null || totalWidthPixel <= 0)
			throw new IllegalStateException("Width of Canvas in pixel is " + totalWidthPixel
				+ ". It's likely that the canvas is not initialized.");

		float width = totalWidthPixel.floatValue() / totalWidthGL * glWidth;
		return (int) width;
	}

	public int getPixelHeightForGLHeight(float glHeight) {
		float totalHeightGL = viewFrustum.getHeight();
		Double totalHeightPixel = canvas.getBounds().getHeight();

		if (totalHeightPixel == null || totalHeightPixel <= 0)
			throw new IllegalStateException("Height of Canvas in pixel is " + totalHeightPixel
				+ ". It's likely that the canvas is not initialized.");
		float height = totalHeightPixel.floatValue() / totalHeightGL * glHeight;
		return (int) height;
	}

	public float getGLHeightForCurrentGLTransform(GL2 gl) {
		FloatBuffer buffer = FloatBuffer.wrap(new float[16]);

		gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, buffer);

		return buffer.get(13);
	}

	public float getGLWidthForCurrentGLTransform(GL2 gl) {
		FloatBuffer buffer = FloatBuffer.wrap(new float[16]);

		gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, buffer);

		return buffer.get(12);
	}

	public int getPixelHeightForCurrentGLTransform(GL2 gl) {

		return getPixelHeightForGLHeight(getGLHeightForCurrentGLTransform(gl));
	}

	public int getPixelWidthForCurrentGLTransform(GL2 gl) {

		return getPixelWidthForGLWidth(getGLWidthForCurrentGLTransform(gl));
	}
}
