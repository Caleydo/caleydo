/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.layout;

/**
 * interface describing that the instance provides layout data, or e.g. is a layout data but wrappes another one
 * 
 * @author Samuel Gratzl
 * 
 */
public interface IHasGLLayoutData {
	/**
	 * returns the layout data of the element if it is of the specific class otherwise return the default value
	 *
	 * @param clazz
	 *            the instance of expected layout data
	 * @param default_
	 *            default value
	 * @return
	 */
	<T> T getLayoutDataAs(Class<T> clazz, T default_);
}
