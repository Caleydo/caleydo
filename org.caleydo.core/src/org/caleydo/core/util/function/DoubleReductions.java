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
public class DoubleReductions {
	/**
	 * set of simple reduce operators, for some of them the data item order matters
	 * 
	 * @author Samuel Gratzl
	 * 
	 */
	public enum EReduceOperations implements IDoubleFunction2 {
		PRODUCT, SUM, MIN, MAX, SUB, DIVIDE;

		@Override
		public Double apply(Double input1, Double input2) {
			return Double.valueOf(apply(input1.doubleValue(), input2.doubleValue()));
		}

		@Override
		public double apply(double a, double b) {
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
