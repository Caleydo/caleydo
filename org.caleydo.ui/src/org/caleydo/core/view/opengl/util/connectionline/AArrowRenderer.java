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

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;

/**
 * Base class for renderers of different kinds of arrows.
 * 
 * @author Christian
 * 
 */
public abstract class AArrowRenderer {
	
	public static final int DEFAULT_HEAD_TO_BASE = 15;
	public static final int DEFAULT_BASE_WIDTH = 10;
	public static final int DEFAULT_LINE_WIDTH = 1;
	public static final int DEFAULT_CROSSING_ANGLE = 90;
	public static final float[] DEFAULT_LINE_COLOR = { 0, 0, 0, 1 };

	protected PixelGLConverter pixelGLConverter;
	/**
	 * The distance from the arrow head to its base in pixels.
	 */
	protected int headToBasePixels = DEFAULT_HEAD_TO_BASE;
	/**
	 * The width of the arrow base in pixels.
	 */
	protected int baseWidthPixels = DEFAULT_BASE_WIDTH;

	/**
	 * RGBA color for the line of the arrow.
	 */
	protected float[] lineColor = DEFAULT_LINE_COLOR;

	/**
	 * Line Width of the arrow.
	 */
	protected float lineWidth = DEFAULT_LINE_WIDTH;

	/**
	 * Constructor.
	 * 
	 * @param pixelGLConverter
	 */
	public AArrowRenderer(PixelGLConverter pixelGLConverter) {
		this.pixelGLConverter = pixelGLConverter;
	}

	/**
	 * Rendering method of the arrow.
	 * 
	 * @param gl
	 * @param arrowHead
	 *            Position of the arrow head.
	 * @param direction
	 *            The direction of the arrow as vector.
	 */
	public void render(GL2 gl, Vec3f arrowHead, Vec2f direction) {

		float headToBase = pixelGLConverter.getGLWidthForPixelWidth(headToBasePixels);
		float baseLineWidth = pixelGLConverter.getGLWidthForPixelWidth(baseWidthPixels);

		float scalingFactor = headToBase / direction.length();
		Vec2f basePoint = new Vec2f(arrowHead.x() - direction.x() * scalingFactor,
				arrowHead.y() - scalingFactor * direction.y());

		Vec2f baselineDirection = new Vec2f(-direction.y(), direction.x());

		scalingFactor = (baseLineWidth / 2.0f) / direction.length();

		Vec3f corner1 = new Vec3f(basePoint.x() + baselineDirection.x() * scalingFactor,
				basePoint.y() + baselineDirection.y() * scalingFactor, arrowHead.z());

		Vec3f corner2 = new Vec3f(basePoint.x() - baselineDirection.x() * scalingFactor,
				basePoint.y() - baselineDirection.y() * scalingFactor, arrowHead.z());

		render(gl, arrowHead, corner1, corner2);

		// gl.glPointSize(3);
		// gl.glColor3f(1, 0, 0);
		// gl.glBegin(GL2.GL_POINTS);
		// gl.glVertex3f(arrowHead.x(), arrowHead.y(), arrowHead.z());
		// gl.glColor3f(0, 0, 1);
		// gl.glVertex3f(basePoint.x(), basePoint.y(), arrowHead.z());
		// gl.glEnd();

	}

	protected abstract void render(GL2 gl, Vec3f arrowHead, Vec3f corner1, Vec3f corner2);

	/**
	 * @param headToBasePixels
	 *            setter, see {@link #headToBasePixels}
	 */
	public void setHeadToBasePixels(int headToBasePixels) {
		this.headToBasePixels = headToBasePixels;
	}

	/**
	 * @return the headToBasePixels, see {@link #headToBasePixels}
	 */
	public int getHeadToBasePixels() {
		return headToBasePixels;
	}

	/**
	 * @param baseWidthPixels
	 *            setter, see {@link #baseWidthPixels}
	 */
	public void setBaseWidthPixels(int baseWidthPixels) {
		this.baseWidthPixels = baseWidthPixels;
	}

	/**
	 * @return the baseWidthPixels, see {@link #baseWidthPixels}
	 */
	public int getBaseWidthPixels() {
		return baseWidthPixels;
	}

	/**
	 * @return the lineColor, see {@link #lineColor}
	 */
	public float[] getLineColor() {
		return lineColor;
	}

	/**
	 * @param lineColor
	 *            setter, see {@link #lineColor}
	 */
	public void setLineColor(float[] lineColor) {
		this.lineColor = lineColor;
	}

	/**
	 * @return the lineWidth, see {@link #lineWidth}
	 */
	public float getLineWidth() {
		return lineWidth;
	}

	/**
	 * @param lineWidth
	 *            setter, see {@link #lineWidth}
	 */
	public void setLineWidth(float lineWidth) {
		this.lineWidth = lineWidth;
	}

}
