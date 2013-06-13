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
package org.caleydo.core.view.opengl.util.spline;

import gleem.linalg.Vec3f;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.Colors;
import org.caleydo.core.util.color.IColor;

/**
 * a vertex with a color
 *
 * @author Samuel Gratzl
 *
 */
public class ColoredVec3f extends Vec3f {
	private IColor color = Color.NEUTRAL_GREY;

	/**
	 * @param color
	 *            setter, see {@link color}
	 */
	public void setColor(IColor color) {
		this.color = color;
	}

	public void setColor(java.awt.Color color) {
		this.color = Colors.of(color);
	}

	public void setColor(float r, float g, float b, float a) {
		this.color = new Color(r, g, b, a);
	}

	/**
	 * @return the color, see {@link #color}
	 */
	public IColor getColor() {
		return color;
	}
}

