package org.caleydo.core.view.opengl.util.spline;

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
import gleem.linalg.Vec3f;

import java.util.HashMap;
import java.util.Map;

public class Spline3D {
	/**
	 * Constructor.
	 * 
	 * @param points
	 *            Input points
	 * @param accuracy
	 *            Accuracy works very smooth when using 0.001-0.010. The higher the rougher, cheaper in first
	 *            calc, same speed (!) when cached.
	 * @param margin
	 *            Margin defines the margin-of-error the actual answer. measured in units.
	 */
	public Spline3D(Vec3f[] points, float accuracy, float margin) {
		n0 = points.length;
		n1 = n0 - 1;

		this.accuracy = accuracy;
		this.margin = margin;

		cacheUpToRange = 1.0F / 128 / 8 * n1;
		cacheToFloat = new HashMap<Cache, Float>();

		float[] x = new float[n0];
		float[] y = new float[n0];
		float[] z = new float[n0];

		for (int i = 0; i < n0; i++) {
			x[i] = points[i].x();
			y[i] = points[i].y();
			z[i] = points[i].z();
		}

		this.x = Curve.calcCurve(n1, x);
		this.y = Curve.calcCurve(n1, y);
		this.z = Curve.calcCurve(n1, z);
	}

	private final int n0, n1;
	private final float accuracy, margin;
	private final Cubic[] x, y, z;

	/**
	 * POINT COUNT
	 */

	public final int pointCount() {
		return n0;
	}

	/**
	 * POSITION
	 */

	public final Vec3f getPositionAt(float param) {
		Vec3f v = new Vec3f();
		this.getPositionAt(param, v);
		return v;
	}

	public final void getPositionAt(float param, Vec3f result) {
		// clamp
		if (param < 0.0F) {
			param = 0.0F;
		}
		if (param >= n1) {
			param = n1 - 0.00001F;
		}

		// split
		int ti = (int) param;
		float tf = param - ti;

		// eval
		result.setX(x[ti].eval(tf));
		result.setY(y[ti].eval(tf));
		result.setZ(z[ti].eval(tf));
	}

	/**
	 * POSITION AT DISTANCE
	 */

	public final Vec3f getPositionAtDistance(float dist) {
		Vec3f v = new Vec3f();
		this.getPositionAtDistance(dist, v);
		return v;
	}

	public final void getPositionAtDistance(float dist, Vec3f result) {
		// System.out.println(cacheToFloat.size());
		this.getPositionAt(this.getParameterAtDistance(dist), result);
	}

	/**
	 * DISTANCE
	 */

	public final float getDistanceAt(float param) {
		return this.getDistanceForInterval(0.0F, param, false);
	}

	/**
	 * PARAMETER AT DISTANCE
	 */

	private final float getParameterAtDistance(float dist) {
		return this.getParameterAtDistance(0.0F, n1, 0.0F, dist);
	}

	private final float getParameterAtDistance(float t0, float t1, float accDistance, float findDistance) {
		final float half = (t1 - t0) * 0.5F;
		final float th = t0 + half;

		// margins too small
		if (EasyMath.equals(t0, t1, accuracy) || EasyMath.equals(accDistance, findDistance, margin))
			return t0;

		//
		// accept tiny errors... if it's just over 0..1 just go ahead.
		//

		// 0..h
		float d0 = this.getDistanceForInterval(t0, th, true);
		if (accDistance + d0 > findDistance)
			return this.getParameterAtDistance(t0, th, accDistance, findDistance);

		// h..1
		return this.getParameterAtDistance(th, t1, accDistance + d0, findDistance);
	}

	/**
	 * LENGTH
	 */

	private float length = -1;

	public final float getLength() {
		if (length != -1)
			return length;

		length = this.getDistanceForInterval(0.0F, n1, true);

		return length;
	}

	/**
	 * DISTANCE FOR INTERVAL
	 */

	private final Map<Cache, Float> cacheToFloat;
	private final float cacheUpToRange;
	private Cache cacheKey;

	private final float getDistanceForInterval(float t0, float t1, boolean cache) {
		// only cache upto a certain range
		boolean caching = cache && t1 - t0 > cacheUpToRange;

		if (caching) {
			cacheKey = new Cache(t0, t1);
			Float f = cacheToFloat.get(cacheKey);

			if (f != null)
				return f.floatValue();
		}

		// calculate distance
		final int steps = (int) ((t1 - t0) / accuracy);
		final float step = (t1 - t0) / steps;
		float distance = 0.0F;

		Vec3f currV = new Vec3f();
		Vec3f lastV = new Vec3f();

		this.getPositionAt(t0, lastV);

		float t = t0;
		for (int i = 1; i < steps; i++) {
			t += step;

			this.getPositionAt(t, currV);
			distance += VecMath.distance3D(lastV, currV);
			lastV.set(currV);
		}

		// cache the result
		if (caching) {
			cacheToFloat.put(cacheKey, new Float(distance));

			// clear cache if it grows way out of control
			if (cacheToFloat.size() > 1024) {
				cacheToFloat.clear();
			}
		}

		return distance;
	}

	/**
	 * CACHE CLASS
	 */

	private class Cache {
		final float t0, t1;
		final int id;

		private Cache(float t0, float t1) {
			this.t0 = t0;
			this.t1 = t1;

			id = Float.floatToRawIntBits(t0) ^ Float.floatToRawIntBits(t1);
		}

		@Override
		public int hashCode() {
			return id;
		}

		@Override
		public boolean equals(Object other) {
			if (other == null || !(other instanceof Cache))
				return false;

			Cache that = (Cache) other;

			return this.t0 == that.t0 && this.t1 == that.t1;
		}
	}

	/**
	 * CURVE CLASS
	 */

	private static class Curve {
		static final Cubic[] calcCurve(int n, float[] axis) {
			float[] gamma = new float[n + 1];
			float[] delta = new float[n + 1];
			float[] d = new float[n + 1];
			Cubic[] c = new Cubic[n];

			// gamma
			gamma[0] = 0.5F;
			for (int i = 1; i < n; i++) {
				gamma[i] = 1.0F / (4.0F - gamma[i - 1]);
			}
			gamma[n] = 1.0F / (2.0F - gamma[n - 1]);

			// delta
			delta[0] = 3.0F * (axis[1] - axis[0]) * gamma[0];
			for (int i = 1; i < n; i++) {
				delta[i] = (3.0F * (axis[i + 1] - axis[i - 1]) - delta[i - 1]) * gamma[i];
			}
			delta[n] = (3.0F * (axis[n] - axis[n - 1]) - delta[n - 1]) * gamma[n];

			// d
			d[n] = delta[n];
			for (int i = n - 1; i >= 0; i--) {
				d[i] = delta[i] - gamma[i] * d[i + 1];
			}

			// c
			for (int i = 0; i < n; i++) {
				float x0 = axis[i];
				float x1 = axis[i + 1];
				float d0 = d[i];
				float d1 = d[i + 1];
				c[i] = new Cubic(x0, d0, 3.0F * (x1 - x0) - 2.0F * d0 - d1, 2.0F * (x0 - x1) + d0 + d1);
			}
			return c;
		}
	}

	/**
	 * CUBIC CLASS
	 */

	private static class Cubic {
		private final float a, b, c, d;

		private Cubic(float a, float b, float c, float d) {
			this.a = a;
			this.b = b;
			this.c = c;
			this.d = d;
		}

		final float eval(float u) {
			return ((d * u + c) * u + b) * u + a;
		}
	}

	/**
	 * COPIED METHODS FROM UTILITY CLASSES
	 */

	static class VecMath {
		public static final float distance3D(Vec3f a, Vec3f b) {
			float x = a.x() - b.x();
			float y = a.y() - b.y();
			float z = a.z() - b.z();

			return (float) Math.sqrt(x * x + y * y + z * z);
		}
	}

	static class EasyMath {
		public static final boolean equals(float a, float b, float error) {
			if (a == b)
				return true;

			float diff = a - b;
			return diff * diff < error * error;
		}
	}
}