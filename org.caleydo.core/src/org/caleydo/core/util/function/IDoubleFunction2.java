/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.function;


/**
 * simple double specific function with primitive and wrapper handling
 *
 * @author Samuel Gratzl
 *
 */
public interface IDoubleFunction2 extends Function2<Double, Double, Double> {
	public double apply(double v1, double v2);
}

