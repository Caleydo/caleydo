/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.data;

import com.google.common.base.Function;

/**
 * double special version of a {@link Function} to avoid boxing primitives
 *
 * @author Samuel Gratzl
 *
 */
public interface IDoubleSetterFunction<F> extends IDoubleFunction<F> {
	void set(F in, double value);
}

