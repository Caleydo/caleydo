/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.util.vislink;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

import java.util.List;

/**
 * factory class for 2d and 3d splines
 * 
 * @author Samuel Gratzl
 * 
 */
public class Splines {
	public static List<Vec3f> spline3(List<Vec3f> controlPoints, int numberOfSplinePoints) {
		return new NURBSCurve(controlPoints, numberOfSplinePoints).getCurvePoints();
	}

	public static List<Vec2f> spline2(List<Vec2f> controlPoints, int numberOfSplinePoints) {
		return new Splines2(controlPoints, numberOfSplinePoints).getCurvePoints();
	}

	private static class Splines2 extends ANURBSCurve<Vec2f> {
		public Splines2(List<Vec2f> controlPoints, int numberOfSegments) {
			super(controlPoints, numberOfSegments);
		}

		@Override
		protected void add(Vec2f acc, float s, Vec2f p) {
			acc.set(acc.x() + s * p.x(), acc.y() + s * p.y());
		}

		@Override
		protected Vec2f createEmpty() {
			return new Vec2f(0, 0);
		}
	}
}
