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
package org.caleydo.view.tourguide.v2.r.model;


/**
 * @author Samuel Gratzl
 *
 */
public class Values {


	public static final IValue NaN = of(Float.NaN);

	public static IValue of(final float v) {
		return new IValue() {

			@Override
			public boolean hasMultiple() {
				return false;
			}

			@Override
			public int getRepr() {
				return 0;
			}

			@Override
			public int size() {
				return 1;
			}

			@Override
			public float[] asFloats() {
				return new float[] { v };
			}

			@Override
			public float asFloat() {
				return v;
			}
		};
	}

	public static IValue max(final float... vs) {
		if (vs.length == 0)
			return NaN;
		else if (vs.length == 1)
			return of(vs[0]);
		else {
			int max = 0;
			for (int i = 1; i < vs.length; ++i)
				if (vs[i] > vs[max]) {
					max = i;
				}
			return of(max, vs);
		}
	}

	public static IValue of(final int repr, final float... vs) {
		assert repr >= 0 && vs.length > repr;
		return new CompositeValue(repr, vs);
	}

	public static IValue clamp01(IValue v) {
		if (!v.hasMultiple()) {
			float f = v.asFloat();
			return of(clamp01(f));
		} else {
			CompositeValue c = (CompositeValue)v;
			float[] t = new float[c.vs.length];
			for(int i = 0; i < t.length; ++i)
				t[i] = clamp01(c.vs[i]);
			return new CompositeValue(c.repr, t);
		}
	}

	/**
	 * (v+a)*b + c
	 *
	 * @param v
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	public static IValue transform(IValue v, float a, float b, float c) {
		if (!v.hasMultiple()) {
			float f = v.asFloat();
			return of(transform(f, a, b, c));
		} else {
			CompositeValue cv = (CompositeValue) v;
			float[] t = new float[cv.vs.length];
			for (int i = 0; i < t.length; ++i)
				t[i] = transform(cv.vs[i], a, b, c);
			return new CompositeValue(cv.repr, t);
		}
	}

	public static float clamp01(float f) {
		return f < 0 ? 0 : (f > 1 ? 1 : f);
	}

	public static float transform(float f, float a, float b, float c) {
		return (f + a) * b + c;
	}

	/**
	 * @author Samuel Gratzl
	 *
	 */
	private static final class CompositeValue implements IValue {
		private final int repr;
		private final float[] vs;

		private CompositeValue(int repr, float[] vs) {
			this.repr = repr;
			this.vs = vs;
		}

		@Override
		public int getRepr() {
			return repr;
		}

		@Override
		public int size() {
			return vs.length;
		}

		@Override
		public boolean hasMultiple() {
			return vs.length > 1;
		}

		@Override
		public float[] asFloats() {
			return vs;
		}

		@Override
		public float asFloat() {
			return vs[repr];
		}
	}
}
