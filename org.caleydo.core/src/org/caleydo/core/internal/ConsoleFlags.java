/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.internal;

/**
 * set of system properties than can be set to influence the behavior of the caleydo framework
 *
 * @author Samuel Gratzl
 *
 */
public class ConsoleFlags {
	/**
	 * enables experimental features, which are not yet released
	 */
	public static final boolean EXPERIMENTAL_MODE = Boolean.getBoolean("org.caleydo.experimental");

	/**
	 * chooses the implementation for the jogl canvas, possible values are: awt, swt (default) and newt
	 */
	public static final String CANVAS_IMPLEMENTATION = System.getProperty("org.caleydo.opengl", "swt");
}
