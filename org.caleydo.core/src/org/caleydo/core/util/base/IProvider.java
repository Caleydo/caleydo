/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.base;

/**
 * Generic version of a provider for any data type.
 *
 * @author Christian Partl
 *
 */
public interface IProvider<T> {
	public T get();
}
