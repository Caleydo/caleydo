/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.util.function;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.caleydo.core.util.function.FloatReductions.EReduceOperations;

/**
 * @author Samuel Gratzl
 *
 */
public class ExpressionFunctions {

	/**
	 * identity mapping
	 */
	public static final AFloatFunction IDENTITY = new AFloatFunction() {
		@Override
		public float apply(float v) {
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
	public static IFloatFunction compose(final IFloatFunction g, final IFloatFunction f) {
		return new IFloatFunction() {

			@Override
			public Float apply(Float input) {
				return g.apply(f.apply(input));
			}

			@Override
			public float apply(float in) {
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
	 * {@code public static float m(float in)};
	 *
	 * @param m
	 *            the method to invoce
	 * @return
	 */
	public static IFloatFunction call(final Method m) {
		assert m.getReturnType() == float.class;
		assert m.getParameterTypes().length == 1 && m.getParameterTypes()[0] == float.class;
		assert Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers());

		return new IFloatFunction() {
			@Override
			public Float apply(Float in) {
				try {
					return (Float) m.invoke(null, in);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new IllegalStateException("invoke error", e);
				}
			}

			@Override
			public float apply(float in) {
				return apply(Float.valueOf(in)).floatValue();
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
	public static IFloatFunction combine(final EReduceOperations op, final IFloatFunction a, final IFloatFunction b) {
		return new AFloatFunction() {
			@Override
			public float apply(float in) {
				float av = a.apply(in);
				float bv = b.apply(in);
				return op.reduce(av, bv);
			}

			@Override
			public String toString() {
				return String.format("%s(%s,%s)", op, a, b);
			}
		};
	}

	public enum EMonoOperator implements IFloatFunction {
		NEGATE, LN, LOG10, SQUARED;

		@Override
		public float apply(float v) {
			switch (this) {
			case NEGATE:
				return -v;
			case LN:
				return (float) Math.log(v);
			case LOG10:
				return (float) Math.log10(v);
			case SQUARED:
				return v * v;
			}
			throw new IllegalStateException();
		}

		@Override
		public Float apply(Float input) {
			return apply(input.floatValue());
		}

		@Override
		public String toString() {
			return String.format("%s(v)", name());
		}
	}
}
