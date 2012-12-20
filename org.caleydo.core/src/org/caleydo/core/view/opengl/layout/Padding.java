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
package org.caleydo.core.view.opengl.layout;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;

/**
 * a abstraction of a padding used by label renderer
 *
 * @author Samuel Gratzl
 *
 */
public class Padding {
	public static final Padding NONE = new Padding(Dims.zero, Dims.zero);

	private final IDim west, north, east, south;

	public Padding(IDim hor, IDim vert) {
		this(hor, vert, hor, vert);
	}

	public Padding(IDim west, IDim north, IDim east, IDim south) {
		this.west = west;
		this.north = north;
		this.east = east;
		this.south = south;
	}

	@Override
	public String toString() {
		return String.format("Padding(%s,%s,%s,%s)", west, north, east, south);
	}

	public float[] resolve(PixelGLConverter converter, float w, float h) {
		return new float[] { west.resolve(converter, w, h), north.resolve(converter, w, h),
				east.resolve(converter, w, h), south.resolve(converter, w, h) };
	}
}
