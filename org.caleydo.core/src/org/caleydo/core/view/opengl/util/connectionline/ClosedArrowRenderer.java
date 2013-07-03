/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.core.view.opengl.util.connectionline;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;

/**
 * Renderer for a closed arrow with a baseline.
 *
 * @author Christian
 *
 */
public class ClosedArrowRenderer extends AArrowRenderer {

	public static final float[] DEFAULT_FILL_COLOR = { 0, 0, 0, 1 };

	/**
	 * Color the arrow is filled with.
	 */
	private float[] fillColor = DEFAULT_FILL_COLOR;

	/**
	 * @param pixelGLConverter
	 */
	public ClosedArrowRenderer(PixelGLConverter pixelGLConverter) {
		super(pixelGLConverter);
	}

	@Override
	protected void render(GL2 gl, Vec3f arrowHead, Vec3f corner1, Vec3f corner2) {

		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT | GL2.GL_LINE_BIT);

		gl.glColor4fv(fillColor, 0);
		gl.glBegin(GL.GL_TRIANGLES);
		gl.glVertex3f(arrowHead.x(), arrowHead.y(), arrowHead.z());
		gl.glVertex3f(corner1.x(), corner1.y(), corner1.z());
		gl.glVertex3f(corner2.x(), corner2.y(), corner2.z());
		gl.glEnd();

		gl.glColor4fv(lineColor, 0);
		gl.glLineWidth(lineWidth);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(arrowHead.x(), arrowHead.y(), arrowHead.z());
		gl.glVertex3f(corner1.x(), corner1.y(), corner1.z());
		gl.glVertex3f(corner2.x(), corner2.y(), corner2.z());
		gl.glEnd();
		gl.glPopAttrib();
	}

	/**
	 * @return the fillColor, see {@link #fillColor}
	 */
	public float[] getFillColor() {
		return fillColor;
	}

	/**
	 * @param fillColor
	 *            setter, see {@link #fillColor}
	 */
	public void setFillColor(float[] fillColor) {
		this.fillColor = fillColor;
	}

}
