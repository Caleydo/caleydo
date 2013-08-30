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
	public enum EReduceOperations implements IFloatReduction {
		PRODUCT, SUM, MIN, MAX, SUB, DIVIDE;

		@Override
		public float reduce(float a, float b) {
			switch (this) {
			case PRODUCT:
				return a * b;
			case SUM:
				return a + b;
			case MIN:
				return Math.min(a, b);
			case MAX:
				return Math.max(a, b);
			case SUB:
				return a - b;
			case DIVIDE:
				return a / b;
			}
			throw new IllegalStateException();
		}
	}
}
