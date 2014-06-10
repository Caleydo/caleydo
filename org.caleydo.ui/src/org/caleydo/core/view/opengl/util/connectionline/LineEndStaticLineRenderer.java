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

import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;

/**
 * Renders a horizontal or vertical line at the end of a connection line
 * 
 * @author Christian
 * 
 */
public class LineEndStaticLineRenderer extends ALineEndRenderer {
	
	public static final int DEFAULT_LINE_LENGTH = 15;
	public static final int DEFAULT_LINE_WIDTH = 1;
	public static final float[] DEFAULT_LINE_COLOR = { 0, 0, 0, 1 };

	private PixelGLConverter pixelGLConverter;

	/**
	 * Length of the crossing line in pixels.
	 */
	private int lineLengthPixels = DEFAULT_LINE_LENGTH;

	/**
	 * Width of the crossing line.
	 */
	private float lineWidth = DEFAULT_LINE_WIDTH;

	/**
	 * RGBA color for the line of the arrow.
	 */
	private float[] lineColor = DEFAULT_LINE_COLOR;

	/**
	 * Determines whether the rendered line is horizontal or vertical.
	 */
	private boolean isHorizontalLine = true;

	public LineEndStaticLineRenderer(boolean isLineEnd1, PixelGLConverter pixelGLConverter) {
		super(isLineEnd1);
		this.pixelGLConverter = pixelGLConverter;
	}

	@Override
	public void render(GL2 gl, List<Vec3f> linePoints) {

		Vec3f lineEnd;

		if (isLineEnd1) {
			lineEnd = linePoints.get(0);
		} else {
			lineEnd = linePoints.get(linePoints.size() - 1);
		}

		float halfLineLength = pixelGLConverter.getGLWidthForPixelWidth(lineLengthPixels) / 2.0f;

		gl.glLineWidth(lineWidth);
		gl.glColor4fv(lineColor, 0);
		gl.glBegin(GL.GL_LINE_STRIP);
		if (isHorizontalLine) {
			gl.glVertex3f(lineEnd.x() - halfLineLength, lineEnd.y(), lineEnd.z());
			gl.glVertex3f(lineEnd.x() + halfLineLength, lineEnd.y(), lineEnd.z());
		} else {
			gl.glVertex3f(lineEnd.x(), lineEnd.y() - halfLineLength, lineEnd.z());
			gl.glVertex3f(lineEnd.x(), lineEnd.y() + halfLineLength, lineEnd.z());
		}
		gl.glEnd();

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

	/**
	 * @param isHorizontalLine
	 *            setter, see {@link #isHorizontalLine}
	 */
	public void setHorizontalLine(boolean isHorizontalLine) {
		this.isHorizontalLine = isHorizontalLine;
	}

	/**
	 * @return the isHorizontalLine, see {@link #isHorizontalLine}
	 */
	public boolean isHorizontalLine() {
		return isHorizontalLine;
	}

}
