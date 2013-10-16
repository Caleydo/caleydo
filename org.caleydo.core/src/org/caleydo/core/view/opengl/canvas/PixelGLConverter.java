/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.canvas;

import gleem.linalg.Vec2f;

import java.nio.FloatBuffer;

import javax.media.opengl.GL2;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Converter between pixel space and gl coordinates space for a given {@link GLCanvas} and {@link ViewFrustum}.
 *
 * @author Alexander Lex
 * @author Christian Partl
 */
public class PixelGLConverter {

	private final ViewFrustum viewFrustum;
	private final IGLCanvas canvas;

	/**
	 * <p>
	 * The constructor requires a ViewFrustum and a GLCanvas. Notice, that both have to be from the same top level,
	 * locally rendered view - i.e., viewFrustums of embedded views don't work.
	 * </p>
	 * <p>
	 * This is package-private to avoid it being created anywhere but in {@link AGLView}.
	 * </p>
	 *
	 * @param viewFrustum
	 * @param canvas
	 */
	PixelGLConverter(ViewFrustum viewFrustum, IGLCanvas canvas) {
		this.viewFrustum = viewFrustum;
		this.canvas = canvas;
	}

	public Vec2f convertMouseCoord2GL(Vec2f mousePos) {
		float x = getGLWidthForPixelWidth(mousePos.x());
		float y = getGLHeightForPixelHeight(canvas.getDIPHeight() - mousePos.y());
		return new Vec2f(x, y);
	}

	/**
	 * Converts a width specified in pixels to a width that can be used for openGL drawing commands based on the current
	 * {@link ViewFrustum} and size of the {@link GLCanvas}.
	 *
	 * @param pixelWidth
	 * @return
	 */
	public float getGLWidthForPixelWidth(float pixelWidth) {
		float totalWidthGL = viewFrustum.getWidth();
		float totalWidthPixel = canvas.getDIPWidth();
		// System.out.println("Frustum width: " +totalWidthGL);
		// System.out.println("Pixel width: " +totalWidthPixel);

		if (totalWidthPixel <= 0)
			throw new IllegalStateException("Width of Canvas in pixel is " + totalWidthPixel
					+ ". It's likely that the canvas is not initialized.");

		float width = totalWidthGL / totalWidthPixel * pixelWidth;
		// System.out.println("GL width: " +width);

		return width;
	}

	/**
	 * Same as {@link #getGLWidthForPixelWidth(int)} but for height.
	 *
	 * @param pixelHeight
	 * @return
	 */
	public float getGLHeightForPixelHeight(float pixelHeight) {
		float totalHeightGL = viewFrustum.getHeight();
		float totalHeightPixel = canvas.getDIPHeight();

		if (totalHeightPixel <= 0)
			throw new IllegalStateException("Height of Canvas in pixel is " + totalHeightPixel
					+ ". It's likely that the canvas is not initialized.");
		float height = totalHeightGL / totalHeightPixel * pixelHeight;
		return height;
	}

	/**
	 * Converts the given glWidht to a width in pixels in the context of the set {@link ViewFrustum} and
	 * {@link GLCanvas}
	 *
	 * @param glWidth
	 * @return
	 */
	public int getPixelWidthForGLWidth(double glWidth) {
		double totalWidthGL = viewFrustum.getWidth();
		float totalWidthPixel = canvas.getDIPWidth();
		if (totalWidthPixel <= 0)
			throw new IllegalStateException("Width of Canvas in pixel is " + totalWidthPixel
					+ ". It's likely that the canvas is not initialized.");

		double width = totalWidthPixel / totalWidthGL * glWidth;
		return (int) width;
	}

	/**
	 * Same as {@link #getPixelWidthForGLWidth(double)} for height
	 *
	 * @param glHeight
	 * @return
	 */
	public int getPixelHeightForGLHeight(double glHeight) {
		double totalHeightGL = viewFrustum.getHeight();
		float totalHeightPixel = canvas.getDIPHeight();

		if (totalHeightPixel <= 0)
			throw new IllegalStateException("Height of Canvas in pixel is " + totalHeightPixel
					+ ". It's likely that the canvas is not initialized.");
		double height = totalHeightPixel / totalHeightGL * glHeight;
		return (int) height;
	}

	// FIXME rename, document, change to proportions based on frustum
	public float getGLHeightForGLWidth(float glWidth) {
		int pixelWidth = getPixelWidthForGLWidth(glWidth);

		return getGLHeightForPixelHeight(pixelWidth);
	}

	// FIXME rename, document, change to proportions based on frustum
	public float getGLWidthForGLHeight(float glHeight) {
		int pixelHeight = getPixelHeightForGLHeight(glHeight);

		return getGLWidthForPixelWidth(pixelHeight);
	}

	/**
	 * returns the current accumulated translation
	 *
	 * @param gl
	 * @return
	 */
	public Vec2f getCurrentPixelPos(GL2 gl) {
		FloatBuffer buffer = FloatBuffer.wrap(new float[16]);

		gl.glGetFloatv(GLMatrixFunc.GL_MODELVIEW_MATRIX, buffer);

		Vec2f r = new Vec2f(getPixelWidthForGLWidth(buffer.get(12)), getPixelHeightForGLHeight(buffer.get(13)));
		// FIXME scrolling on mac creates an offset
		return r;
	}
}
