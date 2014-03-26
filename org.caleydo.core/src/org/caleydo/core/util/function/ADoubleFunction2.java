/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.function;

/**
 * basic implementation of a {@link IDoubleFunction}
 *
 * @author Samuel Gratzl
 *
 */
public abstract class ADoubleFunction2 implements IDoubleFunction2 {
	@Override
	public final Double apply(Double v1, Double v2) {
		return Double.valueOf(apply(v1.doubleValue(), v2.doubleValue()));
	}
}
