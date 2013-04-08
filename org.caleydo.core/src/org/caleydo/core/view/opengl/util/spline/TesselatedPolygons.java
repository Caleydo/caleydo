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
package org.caleydo.core.view.opengl.util.spline;

import gleem.linalg.Vec3f;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.util.vislink.NURBSCurve;

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

	public static ITesselatedPolygon coloredPolygon3(Collection<ColoredVec3f> points) {
		return new ColoredTesselatedPolygon(0, points);
	}

	public static Band band(List<Pair<Vec3f, Vec3f>> anchorPoints) {
		return band(anchorPoints, NUMBER_OF_SPLINE_POINTS);
	}

	public static Band band(List<Pair<Vec3f, Vec3f>> anchorPoints, int numberOfSplinePoints) {
		List<Vec3f> top = Lists.transform(anchorPoints, Pair.<Vec3f, Vec3f> mapFirst());
		top = NURBSCurve.spline3(top, numberOfSplinePoints);

		List<Vec3f> bottom = Lists.transform(anchorPoints, Pair.<Vec3f, Vec3f> mapSecond());
		bottom = NURBSCurve.spline3(bottom, numberOfSplinePoints);

		return new Band(top, bottom);
	}
}
