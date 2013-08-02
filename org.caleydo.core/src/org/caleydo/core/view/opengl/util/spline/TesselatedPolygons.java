/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.util.spline;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.util.vislink.Splines;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * @author Samuel Gratzl
 *
 */
public final class TesselatedPolygons {
	public final static int NUMBER_OF_SPLINE_POINTS = 30;


	public static ITesselatedPolygon polygon(Vec3f... points) {
		return polygon(Arrays.asList(points));
	}

	public static ITesselatedPolygon polygon(Collection<Vec3f> points) {
		return new TesselatedPolygon(0, points);
	}

	public static ITesselatedPolygon polygon2(Collection<Vec2f> points) {
		return new TesselatedPolygon2(0, points);
	}

	public static ITesselatedPolygon coloredPolygon3(Collection<ColoredVec3f> points) {
		return new ColoredTesselatedPolygon(0, points);
	}

	public static Band band(List<Pair<Vec3f, Vec3f>> anchorPoints) {
		return band(anchorPoints, NUMBER_OF_SPLINE_POINTS);
	}

	public static Band band(List<Pair<Vec3f, Vec3f>> anchorPoints, int numberOfSplinePoints) {
		List<Vec3f> top = Lists.transform(anchorPoints, Pair.<Vec3f, Vec3f> mapFirst());
		top = Splines.spline3(top, numberOfSplinePoints);

		List<Vec3f> bottom = Lists.transform(anchorPoints, Pair.<Vec3f, Vec3f> mapSecond());
		bottom = Splines.spline3(bottom, numberOfSplinePoints);

		return new Band(top, bottom);
	}

	/**
	 * create a band based on a set of 2d curve points
	 *
	 * @param anchorPoints
	 * @param z
	 * @param radius
	 *            radius of the band
	 * @param numberOfSplinePoints
	 * @return
	 */
	public static Band band(List<Vec2f> anchorPoints, final float z, float radius, int numberOfSplinePoints) {
		Preconditions.checkArgument(anchorPoints.size() >= 2, "at least two points");
		List<Vec3f> curve = Splines.spline3(Lists.transform(anchorPoints, new Function<Vec2f, Vec3f>() {
			@Override
			public Vec3f apply(Vec2f in) {
				return new Vec3f(in.x(), in.y(), z);
			}
		}), numberOfSplinePoints);

		return toBand(curve, radius);
	}

	public static List<Vec2f> spline(List<Vec2f> anchorPoints, int numberOfSplinePoints) {
		Preconditions.checkArgument(anchorPoints.size() >= 2, "at least two points");
		return Splines.spline2(anchorPoints, numberOfSplinePoints);
	}


	private static Band toBand(List<Vec3f> curve, float radius) {
		final int last = curve.size() - 1;
		Vec3f[] c = curve.toArray(new Vec3f[0]);
		Vec3f[] normals = new Vec3f[curve.size()];

		normals[0] = normal(c[0], c[1]);
		for(int i = 1; i < last; ++i) {
			normals[i] = normal(c[i - 1], c[i]).plus(normal(c[i], c[i + 1]));
			normals[i].scale(0.5f);
			normals[i].normalize();
		}
		normals[last] = normal(c[last - 1], c[last]);

		Vec3f[] top = new Vec3f[curve.size()];
		Vec3f[] bottom = new Vec3f[curve.size()];
		for (int i = 0; i <= last; ++i) {
			top[i] = c[i].addScaled(radius, normals[i]);
			bottom[i] = c[i].addScaled(-radius, normals[i]);
		}

		return new Band(Arrays.asList(top), Arrays.asList(bottom));
	}

	private static Vec3f normal(Vec3f a, Vec3f b) {
		Vec3f d = b.minus(a);
		d.normalize();
		d.set(-d.y(), d.x(), d.z());
		return d;
	}

	public static void main(String[] args) {

		GLSandBox.main(args, new IGLRenderer() {
			@Override
			public void render(GLGraphics g, float w, float h, GLElement parent) {
				renderBand(g, new Vec2f(0, 0), new Vec2f(2, -2), new Vec2f(4, -4), new Vec2f(5, -4),
						new Vec2f(5.5f, -2));

			}

			private void renderBand(GLGraphics g, Vec2f... vecs) {
				final Band band = band(Arrays.asList(vecs), 0, 1, 10);
				g.move(100, 100);
				g.save().gl.glScalef(10, 10, 10);
				g.color(1, 0, 0, 0.5f);
				g.fillPolygon(band);
				g.color(0, 1, 0, 0.5f);
				g.drawPath(band);
				g.restore();
			}
		});

	}
}
