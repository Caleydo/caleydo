/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.util.function;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.caleydo.core.util.function.DoubleReductions.EReduceOperations;

/**
 * @author Samuel Gratzl
 *
 */
public class ExpressionFunctions {

	/**
	 * identity mapping
	 */
	public static final ADoubleFunction IDENTITY = new ADoubleFunction() {
		@Override
		public double apply(double v) {
			return v;
		}

		@Override
		public String toString() {
			return "v";
		}
	};
	/**
	 * composes the given function. For {@code f: A->B} and {@code g: B->C}, composition is defined as the function h
	 * such that {@code h(a) == g(f(a))} for each {@code a}.
	 *
	 * @param g
	 * @param f
	 * @return g(f(in))
	 * @see <a href="//en.wikipedia.org/wiki/Function_composition">function composition</a>
	 */
	public static IDoubleFunction compose(final IDoubleFunction g, final IDoubleFunction f) {
		return new IDoubleFunction() {

			@Override
			public Double apply(Double input) {
				return g.apply(f.apply(input));
			}

			@Override
			public double apply(double in) {
				return g.apply(f.apply(in));
			}

			@Override
			public String toString() {
				return String.format("%s(%s)", g, f);
			}
		};
	}

	/**
	 * calls the given function via reflection per item, requires a method signature of
	 * {@code public static double m(double in)};
	 *
	 * @param m
	 *            the method to invoce
	 * @return
	 */
	public static IDoubleFunction call(final Method m) {
		assert m.getReturnType() == Double.class;
		assert m.getParameterTypes().length == 1 && m.getParameterTypes()[0] == Double.class;
		assert Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers());

		return new IDoubleFunction() {
			@Override
			public Double apply(Double in) {
				try {
					return (double) m.invoke(null, in);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new IllegalStateException("invoke error", e);
				}
			}

			@Override
			public double apply(double in) {
				return apply(Double.valueOf(in)).doubleValue();
			}

			@Override
			public String toString() {
				return String.format("call(%s)(v)", m.getName());
			}
		};
	}

	/**
	 * negates the given function
	 *
	 * @param f
	 * @return
	 */
	public static IDoubleFunction combine(final EReduceOperations op, final IDoubleFunction a, final IDoubleFunction b) {
		return new ADoubleFunction() {
			@Override
			public double apply(double in) {
				double av = a.apply(in);
				double bv = b.apply(in);
				return op.apply(av, bv);
			}

			@Override
			public String toString() {
				return String.format("%s(%s,%s)", op, a, b);
			}
		};
	}

	public enum EMonoOperator implements IDoubleFunction {
		NEGATE, LN, LOG10, SQUARED;

		@Override
		public double apply(double v) {
			switch (this) {
			case NEGATE:
				return -v;
			case LN:
				return Math.log(v);
			case LOG10:
				return Math.log10(v);
			case SQUARED:
				return v * v;
			}
			throw new IllegalStateException();
		}

		@Override
		public Double apply(Double input) {
			return apply(input.doubleValue());
		}

		@Override
		public String toString() {
			return String.format("%s(v)", name());
		}
	}
}
