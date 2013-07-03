/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.function;

import com.google.common.base.Predicate;

/**
 * simple float specific function with primitive and wrapper handling
 *
 * @author Samuel Gratzl
 *
 */
public interface IFloatPredicate extends Predicate<Float> {
	public boolean apply(float in);
}

