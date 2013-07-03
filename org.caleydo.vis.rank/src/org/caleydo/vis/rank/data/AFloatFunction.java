/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.data;

/**
 * basic implementation of a {@link IFloatFunction}
 * 
 * @author Samuel Gratzl
 * 
 */
public abstract class AFloatFunction<F> implements IFloatFunction<F> {
	@Override
	public final Float apply(F in) {
		return applyPrimitive(in);
	}
}
