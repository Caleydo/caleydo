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
package org.caleydo.core.util.function;

import com.google.common.base.Predicate;


/**
 * simple float specific function with primitive and wrapper handling
 *
 * @author Samuel Gratzl
 *
 */
public class FloatPredicates {
	public static final IFloatPredicate isNaN = new CompareFloatPredicate(6, 0);

	public static IFloatPredicate lt(float v) {
		return new CompareFloatPredicate(0, v);
	}

	public static IFloatPredicate le(float v) {
		return new CompareFloatPredicate(1, v);
	}

	public static IFloatPredicate gt(float v) {
		return new CompareFloatPredicate(2, v);
	}
	public static IFloatPredicate ge(float v) {
		return new CompareFloatPredicate(3, v);
	}

	public static IFloatPredicate eq(float v) {
		return new CompareFloatPredicate(4, v);
	}

	public static IFloatPredicate ne(float v) {
		return new CompareFloatPredicate(5, v);
	}

	public static IFloatPredicate not(final IFloatPredicate o) {
		return new IFloatPredicate() {
			@Override
			public boolean apply(Float arg0) {
				return !o.apply(arg0);
			}

			@Override
			public boolean apply(float in) {
				return !o.apply(in);
			}
		};
	}

	public static IFloatPredicate wrap(final Predicate<Float> p) {
		return new IFloatPredicate() {
			@Override
			public boolean apply(Float arg0) {
				return p.apply(arg0);
			}

			@Override
			public boolean apply(float in) {
				return apply(Float.valueOf(in));
			}
		};
	}

	private static class CompareFloatPredicate implements IFloatPredicate {
		private final int mode;
		private final float v;

		public CompareFloatPredicate(int mode, float v) {
			this.mode = mode;
			this.v = v;
		}

		@Override
		public boolean apply(Float arg0) {
			return apply(arg0.floatValue());
		}

		@Override
		public boolean apply(float in) {
			switch (mode) {
			case 0:
				return in < v;
			case 1:
				return in <= v;
			case 2:
				return in > v;
			case 3:
				return in >= v;
			case 4:
				return in != v;
			case 5:
				return in == v;
			case 6:
				return Float.isNaN(in);
			}
			return false;
		}

	}
}

