/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.view.opengl.canvas;

import java.nio.FloatBuffer;

import javax.media.opengl.GL2;
import javax.media.opengl.awt.GLCanvas;

import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Converter between pixel space and gl coordinates space for a given
 * {@link GLCanvas} and {@link ViewFrustum}.
 * 
 * @author Alexander Lex
 * @author Christian Partl
 */
public class PixelGLConverter {
	private ViewFrustum viewFrustum;
	private GLCanvas canvas;

	/**
	 * <p>
	 * The constructor requires a ViewFrustum and a GLCanvas. Notice, that both
	 * have to be from the same top level, locally rendered view - i.e.,
	 * viewFrustums of embedded views don't work.
	 * </p>
	 * <p>
	 * This is package-private to avoid it being created anywhere but in
	 * {@link AGLView}.
	 * </p>
	 * 
	 * @param viewFrustum
	 * @param canvas
	 */
	PixelGLConverter(ViewFrustum viewFrustum, GLCanvas canvas) {
		this.viewFrustum = viewFrustum;
		this.canvas = canvas;
	}

	/**
	 * Converts a width specified in pixels to a width that can be used for
	 * openGL drawing commands based on the current {@link ViewFrustum} and size
	 * of the {@link GLCanvas}.
	 * 
	 * @param pixelWidth
	 * @return
	 */
	public float getGLWidthForPixelWidth(int pixelWidth) {
		float totalWidthGL = viewFrustum.getWidth();
		Double totalWidthPixel = canvas.getBounds().getWidth();
		if (totalWidthPixel == null || totalWidthPixel <= 0)
			throw new IllegalStateException("Width of Canvas in pixel is "
					+ totalWidthPixel
					+ ". It's likely that the canvas is not initialized.");

		float width = totalWidthGL / totalWidthPixel.floatValue() * pixelWidth;
		return width;
	}

	/**
	 * Same as {@link #getGLWidthForPixelWidth(int)} but for height.
	 * 
	 * @param pixelHeight
	 * @return
	 */
	public float getGLHeightForPixelHeight(int pixelHeight) {
		float totalHeightGL = viewFrustum.getHeight();
		Double totalHeightPixel = canvas.getBounds().getHeight();

		if (totalHeightPixel == null || totalHeightPixel <= 0)
			throw new IllegalStateException("Height of Canvas in pixel is "
					+ totalHeightPixel
					+ ". It's likely that the canvas is not initialized.");
		float height = totalHeightGL / totalHeightPixel.floatValue() * pixelHeight;
		return height;
	}

	/**
	 * Converts the given glWidht to a width in pixels in the context of the set
	 * {@link ViewFrustum} and {@link GLCanvas}
	 * 
	 * @param glWidth
	 * @return
	 */
	public int getPixelWidthForGLWidth(double glWidth) {
		double totalWidthGL = viewFrustum.getWidth();
		Double totalWidthPixel = canvas.getBounds().getWidth();
		if (totalWidthPixel == null || totalWidthPixel <= 0)
			throw new IllegalStateException("Width of Canvas in pixel is "
					+ totalWidthPixel
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
		Double totalHeightPixel = canvas.getBounds().getHeight();

		if (totalHeightPixel == null || totalHeightPixel <= 0)
			throw new IllegalStateException("Height of Canvas in pixel is "
					+ totalHeightPixel
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

	// FIXME document
	public float getGLHeightForCurrentGLTransform(GL2 gl) {
		FloatBuffer buffer = FloatBuffer.wrap(new float[16]);

		gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, buffer);

		return buffer.get(13);
	}

	// FIXME document
	public float getGLWidthForCurrentGLTransform(GL2 gl) {
		FloatBuffer buffer = FloatBuffer.wrap(new float[16]);

		gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, buffer);

		return buffer.get(12);
	}
	
	// FIXME document
	public int getPixelHeightForCurrentGLTransform(GL2 gl) {

		return getPixelHeightForGLHeight(getGLHeightForCurrentGLTransform(gl));
	}

	// FIXME document
	public int getPixelWidthForCurrentGLTransform(GL2 gl) {

		return getPixelWidthForGLWidth(getGLWidthForCurrentGLTransform(gl));
	}
}
