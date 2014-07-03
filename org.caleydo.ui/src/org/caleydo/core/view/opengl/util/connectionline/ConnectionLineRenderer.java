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

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

/**
 * The ConnectionLineRenderer is responsible for drawing lines along specified
 * control points with different attributes (e.g. arrows).
 *
 * @author Christian
 *
 */
public class ConnectionLineRenderer {

	public static final int DEFAULT_LINE_WIDTH = 1;
	public static final float[] DEFAULT_LINE_COLOR = { 0, 0, 0, 1 };

	/**
	 * Width of the crossing line.
	 */
	private float lineWidth = DEFAULT_LINE_WIDTH;

	/**
	 * RGBA color for the line of the arrow.
	 */
	private float[] lineColor = DEFAULT_LINE_COLOR;

	/**
	 * The list of {@link IConnectionLineAttributeRenderer} objects responsible
	 * for rendering the line attributes.
	 */
	private List<IConnectionLineAttributeRenderer> attributeRenderers = new ArrayList<IConnectionLineAttributeRenderer>();

	/**
	 * Determines whether the connection line is stippled or not.
	 */
	private boolean isLineStippled = false;

	public void renderLine(GL2 gl, List<Vec3f> linePoints) {

		gl.glColor4fv(lineColor, 0);
		gl.glLineWidth(lineWidth);
		if (isLineStippled) {
			gl.glEnable(GL2.GL_LINE_STIPPLE);
			gl.glLineStipple(1, (short) 2047);
		}
		gl.glBegin(GL.GL_LINE_STRIP);
		for (Vec3f point : linePoints) {
			gl.glVertex3f(point.x(), point.y(), point.z());
		}
		gl.glEnd();

		if (isLineStippled) {
			gl.glDisable(GL2.GL_LINE_STIPPLE);
		}

		for (IConnectionLineAttributeRenderer attributeRenderer : attributeRenderers) {
			attributeRenderer.render(gl, linePoints);
		}
	}

	/**
	 * Adds the specified attribute renderer to this connection line.
	 *
	 * @param attributeRenderer
	 */
	public void addAttributeRenderer(IConnectionLineAttributeRenderer attributeRenderer) {
		if (attributeRenderers == null)
			attributeRenderers = new ArrayList<IConnectionLineAttributeRenderer>();

		attributeRenderers.add(attributeRenderer);
	}

	/**
	 * @return the attributeRenderers, see {@link #attributeRenderers}
	 */
	public List<IConnectionLineAttributeRenderer> getAttributeRenderers() {
		return attributeRenderers;
	}

	/**
	 * @param attributeRenderers
	 *            setter, see {@link #attributeRenderers}
	 */
	public void setAttributeRenderers(
			List<IConnectionLineAttributeRenderer> attributeRenderers) {
		this.attributeRenderers = attributeRenderers;
	}

	/**
	 * @param isLineStippled
	 *            setter, see {@link #isLineStippled}
	 */
	public void setLineStippled(boolean isLineStippled) {
		this.isLineStippled = isLineStippled;
	}

	/**
	 * @return the isLineStippled, see {@link #isLineStippled}
	 */
	public boolean isLineStippled() {
		return isLineStippled;
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
}
