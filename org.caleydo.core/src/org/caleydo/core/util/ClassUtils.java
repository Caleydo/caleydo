/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;

import com.google.common.base.Predicate;

/**
 * set of utility function for reflection
 *
 * @author Samuel Gratzl
 *
 */
public final class ClassUtils {
	private ClassUtils() {

	}

	/**
	 * iterable object for iterating over all declared methods of a class including super classes
	 *
	 * @param clazz
	 * @return
	 */
	public static Iterable<Method> findAllDeclaredMethods(final Class<?> clazz) {
		return findAllDeclaredMethods(clazz, null);
	}

	/**
	 * see {@link #findAllDeclaredMethods(Class)} but with a predicate when to stop going upwards the hiearchy
	 *
	 * @param clazz
	 * @param stopAt
	 * @return
	 */
	public static Iterable<Method> findAllDeclaredMethods(final Class<?> clazz,
			final Predicate<? super Class<?>> scanWhile) {
		return new Iterable<Method>() {
			@Override
			public Iterator<Method> iterator() {
				return new MethodIterator(clazz, scanWhile);
			}
		};
	}

	/**
	 * similar to {@link #findAllDeclaredFields(Class, Predicate)} but for fields
	 * 
	 * @param clazz
	 * @param stopAt
	 * @return
	 */
	public static Iterable<Field> findAllDeclaredFields(final Class<?> clazz,
			final Predicate<? super Class<?>> scanWhile) {
		return new Iterable<Field>() {
			@Override
			public Iterator<Field> iterator() {
				return new FieldIterator(clazz, scanWhile);
			}
		};
	}

	private static class MethodIterator implements Iterator<Method> {
		private final Predicate<? super Class<?>> scanWhile;
		private Class<?> clazz;
		private Iterator<Method> methods = null;

		public MethodIterator(Class<?> clazz, Predicate<? super Class<?>> scanWhile) {
			this.clazz = clazz;
			this.scanWhile = scanWhile;
		}

		@Override
		public boolean hasNext() {
			if (methods == null && clazz == null)
				return false;
			while (methods == null || !methods.hasNext()) {
				if (clazz == null || (scanWhile != null && !scanWhile.apply(clazz)))
					return false;
				methods = Arrays.asList(clazz.getDeclaredMethods()).iterator();
				clazz = clazz.getSuperclass();
			}
			return methods.hasNext();
		}

		@Override
		public Method next() {
			return methods.next();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	private static class FieldIterator implements Iterator<Field> {
		private final Predicate<? super Class<?>> scanWhile;
		private Class<?> clazz;
		private Iterator<Field> fields = null;

		public FieldIterator(Class<?> clazz, Predicate<? super Class<?>> scanWhile) {
			this.clazz = clazz;
			this.scanWhile = scanWhile;
		}

		@Override
		public boolean hasNext() {
			if (fields == null && clazz == null)
				return false;
			while (fields == null || !fields.hasNext()) {
				if (clazz == null || (scanWhile != null && !scanWhile.apply(clazz)))
					return false;
				fields = Arrays.asList(clazz.getDeclaredFields()).iterator();
				clazz = clazz.getSuperclass();
			}
			return fields.hasNext();
		}

		@Override
		public Field next() {
			return fields.next();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
