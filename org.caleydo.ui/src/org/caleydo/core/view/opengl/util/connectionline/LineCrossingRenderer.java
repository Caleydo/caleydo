/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 * 
 */
package org.caleydo.core.view.opengl.util.connectionline;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;

/**
 * Renders a line that crosses the connection line in a specified angle.
 * 
 * @author Christian
 * 
 */
public class LineCrossingRenderer extends ARelativeLinePositionRenderer {

	public static final int DEFAULT_LINE_LENGTH = 15;
	public static final int DEFAULT_LINE_WIDTH = 1;
	public static final int DEFAULT_CROSSING_ANGLE = 90;
	public static final float[] DEFAULT_LINE_COLOR = { 0, 0, 0, 1 };

	/**
	 * Length of the crossing line in pixels.
	 */
	private int lineLengthPixels = DEFAULT_LINE_LENGTH;

	/**
	 * Width of the crossing line.
	 */
	private float lineWidth = DEFAULT_LINE_WIDTH;

	/**
	 * The angle the line crosses the connection line in degrees.
	 */
	private float crossingAngle = DEFAULT_CROSSING_ANGLE;

	/**
	 * RGBA color for the line of the arrow.
	 */
	private float[] lineColor = DEFAULT_LINE_COLOR;

	/**
	 * @param linePositionProportion
	 */
	public LineCrossingRenderer(float linePositionProportion,
			PixelGLConverter pixelGLConverter) {
		super(linePositionProportion, pixelGLConverter);
	}

	@Override
	protected void render(GL2 gl, List<Vec3f> linePoints, Vec3f relativePositionOnLine,
			Vec3f enclosingPoint1, Vec3f enclosingPoint2) {

		float halfLineLength = pixelGLConverter.getGLWidthForPixelWidth(lineLengthPixels) / 2.0f;
		Vec2f direction = new Vec2f(enclosingPoint2.x() - enclosingPoint1.x(),
				enclosingPoint2.y() - enclosingPoint1.y());

		float scalingFactor = halfLineLength / direction.length();
		Vec3f linePoint1 = new Vec3f(relativePositionOnLine.x() + direction.x()
				* scalingFactor, relativePositionOnLine.y() + direction.y()
				* scalingFactor, relativePositionOnLine.z());
		Vec3f linePoint2 = new Vec3f(relativePositionOnLine.x() - direction.x()
				* scalingFactor, relativePositionOnLine.y() - direction.y()
				* scalingFactor, relativePositionOnLine.z());

		gl.glPushMatrix();
		gl.glTranslatef(relativePositionOnLine.x(), relativePositionOnLine.y(), 0);
		gl.glRotatef(crossingAngle, 0, 0, 1);
		gl.glTranslatef(-relativePositionOnLine.x(), -relativePositionOnLine.y(), 0);
		gl.glLineWidth(lineWidth);
		gl.glColor4fv(lineColor, 0);
		gl.glBegin(GL.GL_LINE_STRIP);
		gl.glVertex3f(linePoint1.x(), linePoint1.y(), linePoint1.z());
		gl.glVertex3f(linePoint2.x(), linePoint2.y(), linePoint1.z());
		gl.glEnd();
		gl.glPopMatrix();

	}

	/**
	 * @param lineLengthPixels
	 *            setter, see {@link #lineLengthPixels}
	 */
	public void setLineLengthPixels(int lineLengthPixels) {
		this.lineLengthPixels = lineLengthPixels;
	}

	/**
	 * @return the lineLengthPixels, see {@link #lineLengthPixels}
	 */
	public int getLineLengthPixels() {
		return lineLengthPixels;
	}

	/**
	 * @param lineWidth
	 *            setter, see {@link #lineWidth}
	 */
	public void setLineWidth(float lineWidth) {
		this.lineWidth = lineWidth;
	}

	/**
	 * @return the lineWidth, see {@link #lineWidth}
	 */
	public float getLineWidth() {
		return lineWidth;
	}

	/**
	 * @param crossingAngle
	 *            setter, see {@link #crossingAngle}
	 */
	public void setCrossingAngle(float crossingAngle) {
		this.crossingAngle = crossingAngle;
	}

	/**
	 * @return the crossingAngle, see {@link #crossingAngle}
	 */
	public float getCrossingAngle() {
		return crossingAngle;
	}

	/**
	 * @param lineColor
	 *            setter, see {@link #lineColor}
	 */
	public void setLineColor(float[] lineColor) {
		this.lineColor = lineColor;
	}

	/**
	 * @return the lineColor, see {@link #lineColor}
	 */
	public float[] getLineColor() {
		return lineColor;
	}

}
