/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.util.spline;

import static org.caleydo.core.view.opengl.util.spline.TesselatedPolygon.asDoubleArray;
import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLGraphics;

/**
 * a simple band
 *
 * @author Samuel Gratzl
 *
 */
public class Band implements ITesselatedPolygon {
	private final Collection<Vec3f> curveTop;
	private final Collection<Vec3f> curveBottom;
	/**
	 * flag indicating, whether only the band borders or the whole contour should be drawn upon
	 * {@link #draw(GLGraphics)}
	 */
	private boolean drawBandBordersOnly = true;
	/**
	 * flag indicating, whether the band borders should be drawn upon {@link #fill(GLGraphics, TesselationRenderer)} or
	 * not
	 */
	private boolean drawBandBordersOnFill = true;

	/**
	 * flag, whether the current {@link GLGraphics} z value should be considered or not
	 */
	private boolean moveByCurrentZ = true;

	public Band(Collection<Vec3f> curveTop, Collection<Vec3f> curveBottom) {
		this.curveTop = curveTop;
		// create a reversed ordered version for simpler handling
		List<Vec3f> bottom = new ArrayList<>(curveBottom);
		Collections.reverse(bottom);
		this.curveBottom = bottom;
	}

	/**
	 * @return the moveByCurrentZ, see {@link #moveByCurrentZ}
	 */
	public boolean isMoveByCurrentZ() {
		return moveByCurrentZ;
	}

	/**
	 * @param moveByCurrentZ
	 *            setter, see {@link moveByCurrentZ}
	 */
	public Band setMoveByCurrentZ(boolean moveByCurrentZ) {
		this.moveByCurrentZ = moveByCurrentZ;
		return this;
	}

	/**
	 * @return the drawBandBordersOnly, see {@link #drawBandBordersOnly}
	 */
	public boolean isDrawBandBordersOnly() {
		return drawBandBordersOnly;
	}

	/**
	 * @param drawBandBordersOnly
	 *            setter, see {@link drawBandBordersOnly}
	 */
	public Band setDrawBandBordersOnly(boolean drawBandBordersOnly) {
		this.drawBandBordersOnly = drawBandBordersOnly;
		return this;
	}

	/**
	 * @return the drawBandBordersOnFill, see {@link #drawBandBordersOnFill}
	 */
	public boolean isDrawBandBordersOnFill() {
		return drawBandBordersOnFill;
	}

	/**
	 * @param drawBandBordersOnFill
	 *            setter, see {@link drawBandBordersOnFill}
	 */
	public Band setDrawBandBordersOnFill(boolean drawBandBordersOnFill) {
		this.drawBandBordersOnFill = drawBandBordersOnFill;
		return this;
	}

	@Override
	public void draw(GLGraphics g) {
		GL2 gl = g.gl;
		if (moveByCurrentZ)
			gl.glTranslatef(0, 0, g.z());
		if (drawBandBordersOnly) {
			drawBorders(gl);
		} else {
			drawContour(gl);
		}
		if (moveByCurrentZ)
			gl.glTranslatef(0, 0, -g.z());
	}

	protected void drawContour(GL2 gl) {
		gl.glBegin(GL.GL_LINE_LOOP);
		for (Vec3f v : curveTop) {
			setOptionalColor(gl, v);
			gl.glVertex3f(v.x(), v.y(), v.z());
		}
		for (Vec3f v : curveBottom) {
			setOptionalColor(gl, v);
			gl.glVertex3f(v.x(), v.y(), v.z());
		}
		gl.glEnd();
	}

	/**
	 * @param v
	 */
	private void setOptionalColor(GL2 gl, Vec3f v) {
		if (v instanceof ColoredVec3f) {
			Color c = ((ColoredVec3f) v).getColor();
			gl.glColor4f(c.r, c.g, c.b, c.a);
		}
	}

	protected void drawBorders(GL2 gl) {
		gl.glBegin(GL.GL_LINE_STRIP);
		for (Vec3f v : curveTop) {
			setOptionalColor(gl, v);
			gl.glVertex3f(v.x(), v.y(), v.z());
		}
		gl.glEnd();
		gl.glBegin(GL.GL_LINE_STRIP);
		for (Vec3f v : curveBottom) {
			setOptionalColor(gl, v);
			gl.glVertex3f(v.x(), v.y(), v.z());
		}
		gl.glEnd();
	}

	@Override
	public void fill(GLGraphics g, TesselationRenderer renderer) {
		if (drawBandBordersOnFill) {
			draw(g);
		}

		if (moveByCurrentZ)
			g.gl.glTranslatef(0, 0, g.z());

		GLUtessellator tesselator = renderer.begin(g.gl);

		GLU.gluTessBeginPolygon(tesselator, null);
		{
			GLU.gluTessBeginContour(tesselator);

			for (Vec3f v : curveTop) {
				GLU.gluTessVertex(tesselator, asDoubleArray(v), 0, v);
			}
			for (Vec3f v : curveBottom) {
				GLU.gluTessVertex(tesselator, asDoubleArray(v), 0, v);
			}

			GLU.gluTessEndContour(tesselator);
		}
		GLU.gluTessEndPolygon(tesselator);
		renderer.end();

		if (moveByCurrentZ)
			g.gl.glTranslatef(0, 0, -g.z());
	}

	@Override
	public int size() {
		return curveTop.size() + curveBottom.size();
	}

}
