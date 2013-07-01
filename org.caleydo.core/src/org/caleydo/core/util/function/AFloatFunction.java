/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.function;

/**
 * basic implementation of a {@link IFloatFunction}
 *
 * @author Samuel Gratzl
 *
 */
public abstract class AFloatFunction implements IFloatFunction {
	@Override
	public final Float apply(Float in) {
		return Float.valueOf(apply(in.floatValue()));
	}
}
