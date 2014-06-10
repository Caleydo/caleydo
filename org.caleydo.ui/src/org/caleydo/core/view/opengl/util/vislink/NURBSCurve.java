/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.util.vislink;

import gleem.linalg.Vec3f;

import java.util.List;

/**
 * Vec3f version
 *
 * @author Samuel Gratzl
 *
 */
public class NURBSCurve extends ANURBSCurve<Vec3f> {
	/**
	 * Constructor.
	 *
	 * @param controlPoints
	 *            a set of control points of which the spline is generated.
	 * @param numberOfSegments
	 *            defines the subintervals of the spline. The resulting curve will have n segments, where n >=
	 *            numberOfSegments. There is no guarantee, so you must use getNumberOfSegments() to know how
	 *            many segments.
	 */
	public NURBSCurve(List<Vec3f> controlPoints, int numberOfSegments) {
		super(controlPoints, numberOfSegments);
	}

	@Override
	protected void add(Vec3f acc, float s, Vec3f p) {
		acc.set(acc.x() + s * p.x(), acc.y() + s * p.y(), acc.z() + s * p.z());
	}

	@Override
	protected Vec3f createEmpty() {
		return new Vec3f(0, 0, 0);
	}
}
