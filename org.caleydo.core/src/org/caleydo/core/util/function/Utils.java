/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.util.function;

import com.google.common.base.Function;

/**
 * @author Samuel Gratzl
 *
 */
public class Utils {
	public static final IDoubleSizedIterator EMPTY = new IDoubleSizedIterator() {
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public Double next() {
			return null;
		}

		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public double nextPrimitive() {
			return 0;
		}
	};

	public static Function<Integer, Double> list2function(final IDoubleList l) {
		return new Function<Integer, Double>() {
			@Override
			public Double apply(Integer input) {
				return l.get(input.intValue());
			}
			@Override
			public String toString() {
				return "lookup(" + l + ")";
			}
		};
	}
}
