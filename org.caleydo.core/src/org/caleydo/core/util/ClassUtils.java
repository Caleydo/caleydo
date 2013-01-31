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
package org.caleydo.core.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;

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
	 * see {@link #findAllDeclaredMethods(Class)} but with a optional stop criteria, when to stop going upwards the
	 * class hierarchy
	 *
	 * @param clazz
	 * @param stopAt
	 * @return
	 */
	public static Iterable<Method> findAllDeclaredMethods(final Class<?> clazz, final Class<?> stopAt) {
		return new Iterable<Method>() {
			@Override
			public Iterator<Method> iterator() {
				return new MethodIterator(clazz, stopAt);
			}
		};
	}

	private static class MethodIterator implements Iterator<Method> {
		private final Class<?> stopAt;
		private Class<?> clazz;
		private Iterator<Method> methods = null;

		public MethodIterator(Class<?> clazz, Class<?> stopAt) {
			this.clazz = clazz;
			this.stopAt = stopAt;
		}

		@Override
		public boolean hasNext() {
			if (methods == null && clazz == null)
				return false;
			if (methods == null || !methods.hasNext()) {
				if (clazz.equals(stopAt))
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
}
