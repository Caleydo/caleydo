/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.util.gleem;

import gleem.linalg.Vec3f;

import org.caleydo.core.util.color.Color;

/**
 * a vertex with a color
 *
 * @author Samuel Gratzl
 *
 */
public class ColoredVec3f extends Vec3f {
	private Color color = Color.NEUTRAL_GREY;

	public ColoredVec3f() {
	}
	/**
	 * @param color
	 */
	public ColoredVec3f(Vec3f v, Color color) {
		super(v);
		this.color = color;
	}

	/**
	 * @param color
	 *            setter, see {@link color}
	 */
	public void setColor(Color color) {
		this.color = color;
	}


	public void setColor(float r, float g, float b, float a) {
		this.color = new Color(r, g, b, a);
	}

	/**
	 * @return the color, see {@link #color}
	 */
	public Color getColor() {
		return color;
	}
}

