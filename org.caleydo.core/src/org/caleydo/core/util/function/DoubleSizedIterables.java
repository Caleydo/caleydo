/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.function;

/**
 * basic implementation of a {@link IDoubleList}
 *
 * @author Samuel Gratzl
 *
 */
public final class DoubleSizedIterables {

	public static IDoubleSizedIterable map(final IDoubleSizedIterable it, final IDoubleFunction f) {
		return new IDoubleSizedIterable() {
			@Override
			public int size() {
				return it.size();
			}

			@Override
			public IDoubleSizedIterable map(IDoubleFunction f2) {
				return DoubleSizedIterables.map(it, ExpressionFunctions.compose(f2, f));
			}

			@Override
			public IDoubleSizedIterator iterator() {
				return new MapItr(it.iterator(), f);
			}
		};
	}

	private static class MapItr implements IDoubleSizedIterator {
		private final IDoubleSizedIterator iterator;
		private final IDoubleFunction f;

		public MapItr(IDoubleSizedIterator iterator, IDoubleFunction f) {
			this.iterator = iterator;
			this.f = f;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Double next() {
			return nextPrimitive();
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public double nextPrimitive() {
			return f.apply(iterator.nextPrimitive());
		}

		@Override
		public int size() {
			return iterator.size();
		}
	}
}
