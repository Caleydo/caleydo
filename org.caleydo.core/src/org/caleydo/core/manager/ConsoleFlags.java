/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.manager;

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
