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
