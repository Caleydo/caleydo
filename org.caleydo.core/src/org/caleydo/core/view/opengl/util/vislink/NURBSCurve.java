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
package org.caleydo.core.view.opengl.util.vislink;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.caleydo.core.view.opengl.renderstyle.ConnectionLineRenderStyle;

/**
 * @author Oliver Pimas
 * @version 0.1 (2009-07-20) NURBSCurve implements a simple NURBS (Non-uniform rational B-Spline) curve
 *          without weights. This class can be used to calculate a curve by a given set of control points.
 */

public class NURBSCurve {

	protected List<Vec3f> controlPoints;

	protected ArrayList<Vec3f> curvePoints;

	/** knot vector, there are (n + d + 1) knot values */
	protected float[] knots;

	/** parameter n, there are (n+1) control points [n = # CPs - 1] */
	protected int n;

	/** degree parameter [2 <= d <= (n+1)] */
	protected int d;

	protected float u_min;
	protected float u_max;
	protected float u;
	protected float step_length;

	protected static final float SEGMENT_LENGTH = ConnectionLineRenderStyle.CONNECTION_LINE_SEGMENT_LENGTH; // FIXME:

	// This
	// is
	// not
	// nice,
	// NURBSCurve
	// should
	// work
	// without
	// this
	// context

	/**
	 * Constructor.
	 *
	 * @param sourcePoint
	 *            defines the first vertex of the spline (control point 1).
	 * @param bundlingPoint
	 *            defines the middle of the spine (control point 2).
	 * @param destPoint
	 *            defines the last vertex of the spline (control point 3).
	 * @param numberOfSegments
	 *            defines the subintervals of the spline. The resulting curve will have n segments, where n >=
	 *            numberOfSegments. There is no guarantee, so you must use getNumberOfSegments() to know how
	 *            many segments.
	 */
	public NURBSCurve(Vec3f sourcePoint, Vec3f bundlingPoint, Vec3f destPoint, int numberOfSegments) {
		this(Arrays.asList(sourcePoint, bundlingPoint, destPoint), numberOfSegments);
	}

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
		this.controlPoints = controlPoints;

		this.n = controlPoints.size() - 1;
		this.d = 3;

		// float[] knots = { 0.0f, 0.0f, 1.0f, 2.0f, 3.0f, 3.0f };
		float[] knots = generateKnotsVector(n + 1, d);
		this.knots = knots;

		this.u_min = (this.knots[this.d - 1]);
		this.u_max = (this.knots[this.n + 1]);
		this.u = 0.0f;
		this.step_length = (this.knots[this.n + 1] - this.knots[this.d - 1]) / (numberOfSegments);

		buildCurve(numberOfSegments);
		// ArrayList<Vec3f> curvePoints = new ArrayList<Vec3f>(numberOfSegments+1);
		// ArrayList<Vec3f> curvePoints = new ArrayList<Vec3f>();
		// curvePoints.add(controlPoints.get(0));
		// this.curvePoints = curvePoints;
		// this.evaluateCurve();
		// this.curvePoints.add(controlPoints.get(controlPoints.size() - 1));
	}

	public static List<Vec3f> spline3(List<Vec3f> controlPoints, int numberOfSegments) {
		return new NURBSCurve(controlPoints, numberOfSegments).getCurvePoints();
	}

	public static List<Vec3f> spline3(Vec3f source, Vec3f bundling, Vec3f dest, int numberOfSegments) {
		return new NURBSCurve(source, bundling, dest, numberOfSegments).getCurvePoints();
	}

	/**
	 * The curve is composed of the evaluated NURBS spline and 2 straight lines at the end. In general, NURBS
	 * curve doesn't go through the starting and end point, that's why we need the straight lines.
	 */
	protected void buildCurve(int numberOfSplineSegments) {
		ArrayList<Vec3f> spline = new ArrayList<>(numberOfSplineSegments + 2);
		spline.add(controlPoints.get(0));
		evaluateCurve(spline, numberOfSplineSegments);
		spline.add(controlPoints.get(controlPoints.size() - 1));
		this.curvePoints = spline;
	}

	/**
	 * Evaluates the spline and calculates the curve points. After this function has been called, curvePoints
	 * stores the points of the curve. The points can be obtained by the method getCurvePoints().
	 */
	private void evaluateCurve(List<Vec3f> points, int numberOfSegments) {

		int numberOfPoints = (numberOfSegments - 1);

		for (int step = 0; step <= numberOfPoints; step++) {
			Vec3f point = new Vec3f();
			for (int k = 0; k <= n; k++) {
				this.u = this.u_min + this.step_length * step;
				if (step == numberOfPoints) // because of rounding errors we call the last blending function
					// with exact u_max to avoid u outside the definition
					this.u = this.u_max;
				point = point.addScaled(this.coxDeBoor(k, this.d), this.controlPoints.get(k));
			}
			points.add(point);
			// this.curvePoints.add(point);
			// System.out.println("added point " + this.curvePoints[step].x() + ", " +
			// this.curvePoints[step].y() + ", " + this.curvePoints[step].z());
		}
		// System.out.println("----------------");
	}

	/**
	 * Returns an array with the curvePoints.
	 */
	public ArrayList<Vec3f> getCurvePoints() {
		return curvePoints;
	}

	/**
	 * The Cox-deBoor recursive formula is used as blending function.
	 *
	 * @param k
	 *            defines the control variable representing the current control point.
	 * @param u
	 *            the B-Spline curve equation parameter.
	 * @return the result of the Cox-de Boor recursive formula.
	 */
	private float coxDeBoor(int k, int d) {
		int k_max = this.n;
		float result = 0.0f;

		if (d == 1)
			if (this.knots[k] <= this.u && this.u < this.knots[k + 1] && k <= k_max)
				return 1.0f;
			else if (k == k_max && this.u == this.u_max)
				return 1.0f;
			else
				return 0.0f;

		float factor1 = blendingDivision((this.u - this.knots[k]), (this.knots[k + d - 1] - this.knots[k]));
		result = factor1 * this.coxDeBoor(k, (d - 1));
		float factor2 = blendingDivision((this.knots[k + d] - this.u), (this.knots[k + d] - this.knots[k + 1]));
		result += factor2 * this.coxDeBoor((k + 1), (d - 1));

		return result;
	}

	/**
	 * Division for the blending function (coxDeBoor). Needed because it is possible to choose the elements of
	 * the knot vector so that some denominators in the Cox-deBoor calculations evaluate to 0. These terms are
	 * by definition evaluated to 0.
	 *
	 * @param dividend
	 *            the dividend
	 * @param divisor
	 *            the divisor
	 * @return the result of the calculation (0 if the divisor == 0)
	 */
	private static float blendingDivision(float dividend, float divisor) {
		if (divisor == 0.0f)
			return 0.0f;
		return (dividend / divisor);
	}

	/**
	 * Generates the knots vector needed for the spline calculation
	 *
	 * @param numberOfControlPoints
	 *            The number of given control points
	 * @param degree
	 *            The degree of the spline
	 * @return The knots vector with (numberOfControlPoints + degree + 1) elements
	 */
	private static float[] generateKnotsVector(int numberOfControlPoints, int degree) {

		int n = numberOfControlPoints + degree + 1;

		float[] knots = new float[n];

		knots[0] = 0;
		knots[1] = 0;

		for (int i = 2; i < (n - 1); i++) {
			knots[i] = (i - 1);
		}

		knots[n - 1] = n - 3;

		// System.out.println("numberOfControlPoints=" + numberOfControlPoints + " + degree=" + degree +
		// " + 1 = " + n);
		// for(int i = 0; i < n; i++)
		// System.out.print(knots[i] + " ");

		return knots;
	}

}
