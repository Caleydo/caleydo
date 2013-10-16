/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.util.gleem;

import gleem.linalg.Vec2f;

import org.caleydo.core.util.color.Color;

/**
 * a vertex with a color
 *
 * @author Samuel Gratzl
 *
 */
public class ColoredVec2f extends Vec2f {
	private Color color = Color.NEUTRAL_GREY;

	public ColoredVec2f() {
	}
	/**
	 * @param color
	 */
	public ColoredVec2f(Vec2f v, Color color) {
		super(v);
		this.color = color;
	}

	public ColoredVec2f(float x, float y, Color color) {
		super(x, y);
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

