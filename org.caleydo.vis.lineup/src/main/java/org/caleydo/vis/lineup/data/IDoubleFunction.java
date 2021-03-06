/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.data;

import com.google.common.base.Function;

/**
import 
 * double special version of a {@link Function} to avoid boxing primitives
 *
 * @author Samuel Gratzl
 *
 */
public interface IDoubleFunction<F> extends Function<F, Double> {
	double applyPrimitive(F in);
}

