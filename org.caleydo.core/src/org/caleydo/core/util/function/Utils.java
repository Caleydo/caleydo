/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.util.function;

/**
 * @author Samuel Gratzl
 *
 */
public class Utils {
	public static final IDoubleIterator EMPTY = new IDoubleIterator() {
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
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
}
