/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.function;


/**
 * a {@link IDoubleFunction} which can be inverted
 * 
 * @author Samuel Gratzl
 * 
 */
public interface IInvertableDoubleFunction extends IDoubleFunction {
	double unapply(double in);
}

