/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.util.gleem;

import gleem.linalg.Vec2f;

/**
 * a vertex with a color
 *
 * @author Samuel Gratzl
 *
 */
public class TexturedVec2f extends Vec2f {
	private Vec2f texCoords = null;

	public TexturedVec2f() {
	}
	/**
	 * @param color
	 */
	public TexturedVec2f(Vec2f v, Vec2f texCoords) {
		super(v);
		this.texCoords = texCoords;
	}

	public TexturedVec2f(float x, float y, float s, float t) {
		super(x, y);
		this.texCoords = new Vec2f(s, t);
	}

	public void setTexCoords(float s, float t) {
		this.texCoords = new Vec2f(s, t);
	}

	public void setTexCoords(Vec2f texCoords) {
		this.texCoords = texCoords;
	}

	/**
	 * @return the texCoords, see {@link #texCoords}
	 */
	public Vec2f getTexCoords() {
		return texCoords;
	}
}

