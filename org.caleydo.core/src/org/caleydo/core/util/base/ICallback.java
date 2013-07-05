/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.base;

/**
 * generic version of a callback definition
 * 
 * @author Samuel Gratzl
 * 
 */
public interface ICallback<T> {
	public void on(T data);
}
