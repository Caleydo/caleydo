/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.function;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Predicate;


/**
 * simple double specific function with primitive and wrapper handling
 *
 * @author Samuel Gratzl
 *
 */
public class DoublePredicates {
	public static final IDoublePredicate isNaN = new CompareDoublePredicate(6, 0);
	public static final IDoublePredicate alwaysTrue = new IDoublePredicate() {
		@Override
		public boolean apply(Double arg0) {
			return true;
		}

		@Override
		public boolean apply(double in) {
			return true;
		}

		@Override
		public String toString() {
			return "true";
		}
	};
	public static final IDoublePredicate alwaysFalse = not(alwaysTrue);

	public static IDoublePredicate and(final IDoublePredicate... operands) {
		if (operands.length == 1)
			return operands[0];
		if (operands.length == 0)
			return alwaysTrue;
		return new IDoublePredicate() {
			@Override
			public boolean apply(Double input) {
				for (IDoublePredicate p : operands)
					if (!p.apply(input))
						return false;
				return true;
			}

			@Override
			public boolean apply(double in) {
				for (IDoublePredicate p : operands)
					if (!p.apply(in))
						return false;
				return true;
			}

			@Override
			public String toString() {
				return "and(" + StringUtils.join(operands, ", ") + ")";
			}
		};
	}

	public static IDoublePredicate or(final IDoublePredicate... operands) {
		if (operands.length == 1)
			return operands[0];
		if (operands.length == 0)
			return alwaysFalse;
		return new IDoublePredicate() {
			@Override
			public boolean apply(Double input) {
				for(IDoublePredicate p : operands)
					if (p.apply(input))
						return true;
				return false;
			}

			@Override
			public boolean apply(double in) {
				for(IDoublePredicate p : operands)
					if (!p.apply(in))
						return true;
				return false;
			}

			@Override
			public String toString() {
				return "or(" + StringUtils.join(operands, ", ") + ")";
			}
		};
	}

	public static IDoublePredicate lt(double v) {
		return new CompareDoublePredicate(0, v);
	}

	public static IDoublePredicate le(double v) {
		return new CompareDoublePredicate(1, v);
	}

	public static IDoublePredicate gt(double v) {
		return new CompareDoublePredicate(2, v);
	}
	public static IDoublePredicate ge(double v) {
		return new CompareDoublePredicate(3, v);
	}

	public static IDoublePredicate eq(double v) {
		return new CompareDoublePredicate(4, v);
	}

	public static IDoublePredicate ne(double v) {
		return new CompareDoublePredicate(5, v);
	}

	public static IDoublePredicate not(final IDoublePredicate o) {
		return new IDoublePredicate() {
			@Override
			public boolean apply(Double arg0) {
				return !o.apply(arg0);
			}

			@Override
			public boolean apply(double in) {
				return !o.apply(in);
			}

			@Override
			public String toString() {
				return "not(" + o + ")";
			}
		};
	}

	public static IDoublePredicate wrap(final Predicate<Double> p) {
		return new IDoublePredicate() {
			@Override
			public boolean apply(Double arg0) {
				return p.apply(arg0);
			}

			@Override
			public boolean apply(double in) {
				return apply(Double.valueOf(in));
			}
		};
	}

	private static class CompareDoublePredicate implements IDoublePredicate {
		private final int mode;
		private final double v;

		public CompareDoublePredicate(int mode, double v) {
			this.mode = mode;
			this.v = v;
		}

		@Override
		public boolean apply(Double arg0) {
			return apply(arg0.doubleValue());
		}

		@Override
		public boolean apply(double in) {
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
				return Double.isNaN(in);
			}
			return false;
		}

		@Override
		public String toString() {
			switch (mode) {
			case 0:
				return "lt(" + v + ")";
			case 1:
				return "le(" + v + ")";
			case 2:
				return "gt(" + v + ")";
			case 3:
				return "ge(" + v + ")";
			case 4:
				return "ne(" + v + ")";
			case 5:
				return "eq(" + v + ")";
			case 6:
				return "isNaN";
			}
			return "";
		}

	}
}

