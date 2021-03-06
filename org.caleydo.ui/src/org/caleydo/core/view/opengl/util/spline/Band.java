/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.util.spline;

import gleem.linalg.Vec3f;

import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.util.gleem.ColoredVec3f;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * a simple band
 *
 * @author Samuel Gratzl
 *
 */
public class Band implements ITesselatedPolygon {
	private final List<Vec3f> curveTop;
	private final List<Vec3f> curveBottom;
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

	public Band(List<Vec3f> curveTop, List<Vec3f> curveBottom) {
		this.curveTop = curveTop;
		// create a reversed ordered version for simpler handling
		this.curveBottom = Lists.reverse(curveBottom);
	}

	/**
	 * @return the curveTop, see {@link #curveTop}
	 */
	public List<Vec3f> getCurveTop() {
		return curveTop;
	}

	/**
	 * @return the curveBottom, see {@link #curveBottom}
	 */
	public List<Vec3f> getCurveBottom() {
		return Lists.reverse(curveBottom); // undo reverse again
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

		renderer.render3(g, Iterables.concat(curveTop, curveBottom));

		if (moveByCurrentZ)
			g.gl.glTranslatef(0, 0, -g.z());
	}

	@Override
	public int size() {
		return curveTop.size() + curveBottom.size();
	}

}
