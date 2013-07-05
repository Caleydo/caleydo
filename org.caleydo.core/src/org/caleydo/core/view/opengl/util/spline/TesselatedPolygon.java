/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.util.spline;

import gleem.linalg.Vec3f;

import java.util.Collection;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;

import org.caleydo.core.view.opengl.layout2.GLGraphics;

/**
 * a simple tesselated polygon
 * 
 * @author Samuel Gratzl
 * 
 */
final class TesselatedPolygon implements ITesselatedPolygon {
	private final int windingRule;

	private final Collection<Vec3f> points;

	TesselatedPolygon(int windingRule, Collection<Vec3f> points) {
		this.windingRule = windingRule;
		this.points = points;
	}

	@Override
	public int size() {
		return points.size();
	}

	/**
	 * @param glGraphics
	 */
	@Override
	public void draw(GLGraphics g) {
		if (points.isEmpty())
			return;
		GL2 gl = g.gl;
		gl.glBegin(GL.GL_LINE_LOOP);
		for (Vec3f v : points) {
			gl.glVertex3f(v.x(), v.y(), v.z());
		}
		gl.glEnd();
	}

	@Override
	public void fill(GLGraphics g, TesselationRenderer renderer) {
		GLUtessellator tesselator = renderer.begin(g.gl);

		if (windingRule != 0) {
			GLU.gluTessProperty(tesselator, GLU.GLU_TESS_WINDING_RULE, windingRule);
		}
		GLU.gluTessBeginPolygon(tesselator, null);
		{
			GLU.gluTessBeginContour(tesselator);

			for (Vec3f v : points) {
				GLU.gluTessVertex(tesselator, asDoubleArray(v), 0, v);
			}

			GLU.gluTessEndContour(tesselator);
		}
		GLU.gluTessEndPolygon(tesselator);
		renderer.end();
	}

	static double[] asDoubleArray(Vec3f v) {
		return new double[] { v.x(), v.y(), v.z() };
	}
}
