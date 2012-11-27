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
package org.caleydo.core.util.color;

/**
 * @author Samuel Gratzl
 *
 */
public final class Colors {
	private Colors() {

	}

	public static final IColor TRANSPARENT = new Color(1, 1, 1, 0);
	public static final IColor RED = new Color(1, 0, 0, 1);
	public static final IColor GREEN = new Color(0, 1, 0, 1);
	public static final IColor BLUE = new Color(0, 0, 1, 1);
	public static final IColor YELLOW = new Color(1, 1, 0, 1);

}