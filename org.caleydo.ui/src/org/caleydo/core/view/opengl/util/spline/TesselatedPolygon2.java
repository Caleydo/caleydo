/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.util.spline;

import gleem.linalg.Vec2f;

import java.util.Collection;

import org.caleydo.core.view.opengl.layout2.GLGraphics;

/**
 * a simple tesselated polygon
 *
 * @author Samuel Gratzl
 *
 */
final class TesselatedPolygon2 implements ITesselatedPolygon {
	private final int windingRule;

	private final Collection<Vec2f> points;

	TesselatedPolygon2(int windingRule, Collection<Vec2f> points) {
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
		g.drawPath(points, true);
	}

	@Override
	public void fill(GLGraphics g, TesselationRenderer renderer) {
		if (windingRule != 0) {
			renderer.setWindingRule(windingRule);
		}
		renderer.render2(g, points);
	}
}
