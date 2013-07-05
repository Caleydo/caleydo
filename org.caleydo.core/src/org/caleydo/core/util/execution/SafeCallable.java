/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.execution;

import java.util.concurrent.Callable;

/**
 * @author Samuel Gratzl
 *
 */
public interface SafeCallable<V> extends Callable<V> {
	@Override
	public V call();
}
