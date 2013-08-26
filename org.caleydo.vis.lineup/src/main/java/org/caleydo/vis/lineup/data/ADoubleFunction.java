/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.data;

/**
 * basic implementation of a {@link IDoubleFunction}
 *
 * @author Samuel Gratzl
 *
 */
public abstract class ADoubleFunction<F> implements IDoubleFunction<F> {
	@Override
	public final Double apply(F in) {
		return applyPrimitive(in);
	}
}
