/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.function;


/**
 * @author Samuel Gratzl
 *
 */
public class FloatReductions {
	public static final IFloatReduction SUM = new IFloatReduction() {
		@Override
		public float reduce(float a, float b) {
			return a + b;
		}
	};

	public static final IFloatReduction MAX = new IFloatReduction() {
		@Override
		public float reduce(float a, float b) {
			return Math.max(a, b);
		}
	};

	public static final IFloatReduction MIN = new IFloatReduction() {
		@Override
		public float reduce(float a, float b) {
			return Math.min(a, b);
		}
	};

	public static final IFloatReduction PRODUCT = new IFloatReduction() {
		@Override
		public float reduce(float a, float b) {
			return a * b;
		}
	};
}
