/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.util.vislink;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Oliver Pimas
 * @version 0.1 (2009-07-20) NURBSCurve implements a simple NURBS (Non-uniform rational B-Spline) curve
 *          without weights. This class can be used to calculate a curve by a given set of control points.
 */

abstract class ANURBSCurve<T> {
	protected final List<T> controlPoints;

	protected final ArrayList<T> curvePoints;

	/** knot vector, there are (n + d + 1) knot values */
	protected final float[] knots;

	/** parameter n, there are (n+1) control points [n = # CPs - 1] */
	protected final int n;

	/** degree parameter [2 <= d <= (n+1)] */
	protected final int d;

	protected final float u_min;
	protected final float u_max;
	protected final float step_length;

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
	public ANURBSCurve(List<T> controlPoints, int numberOfSegments) {
		this.controlPoints = controlPoints;

		this.n = controlPoints.size() - 1;
		this.d = 3;

		// float[] knots = { 0.0f, 0.0f, 1.0f, 2.0f, 3.0f, 3.0f };
		float[] knots = generateKnotsVector(n + 1, d);
		this.knots = knots;

		this.u_min = (this.knots[this.d - 1]);
		this.u_max = (this.knots[this.n + 1]);
		this.step_length = (this.knots[this.n + 1] - this.knots[this.d - 1]) / (numberOfSegments);

		ArrayList<T> spline = new ArrayList<>(numberOfSegments + 2);
		spline.add(controlPoints.get(0));
		evaluateCurve(spline, numberOfSegments);
		spline.add(controlPoints.get(controlPoints.size() - 1));
		this.curvePoints = spline;
	}

	/**
	 * Evaluates the spline and calculates the curve points. After this function has been called, curvePoints
	 * stores the points of the curve. The points can be obtained by the method getCurvePoints().
	 */
	private void evaluateCurve(List<T> points, int numberOfSegments) {

		int numberOfPoints = (numberOfSegments - 1);

		for (int step = 0; step <= numberOfPoints; step++) {
			T acc = createEmpty();
			float u = this.u_min + this.step_length * step;
			if (step == numberOfPoints) // because of rounding errors we call the last blending function
				// with exact u_max to avoid u outside the definition
				u = this.u_max;

			for (int k = 0; k <= n; k++) {
				float s = this.coxDeBoor(k, this.d, u);
				T p = this.controlPoints.get(k);

				add(acc, s, p);
			}
			points.add(acc);
			// this.curvePoints.add(point);
			// System.out.println("added point " + this.curvePoints[step].x() + ", " +
			// this.curvePoints[step].y() + ", " + this.curvePoints[step].z());
		}
		// System.out.println("----------------");
	}

	protected abstract void add(T acc, float s, T p);

	protected abstract T createEmpty();

	/**
	 * Returns an array with the curvePoints.
	 */
	public ArrayList<T> getCurvePoints() {
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
	private float coxDeBoor(int k, int d, float u) {
		int k_max = this.n;
		float result = 0.0f;

		if (d == 1)
			if (this.knots[k] <= u && u < this.knots[k + 1] && k <= k_max)
				return 1.0f;
			else if (k == k_max && u == this.u_max)
				return 1.0f;
			else
				return 0.0f;

		float factor1 = blendingDivision((u - this.knots[k]), (this.knots[k + d - 1] - this.knots[k]));
		result = factor1 * this.coxDeBoor(k, (d - 1), u);
		float factor2 = blendingDivision((this.knots[k + d] - u), (this.knots[k + d] - this.knots[k + 1]));
		result += factor2 * this.coxDeBoor((k + 1), (d - 1), u);

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
