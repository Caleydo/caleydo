/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.layout;

/**
 * padding similar to html padding
 *
 * @author Samuel Gratzl
 *
 */
public final class GLPadding {
	public static final GLPadding ZERO = new GLPadding(0, 0, 0, 0);

	public final float left, top, right, bottom;

	public GLPadding(float value) {
		this(value, value);
	}

	public GLPadding(float hor, float vert) {
		this(hor, vert, hor, vert);
	}

	public GLPadding(float left, float top, float right, float bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}

	public float hor() {
		return left + right;
	}

	public float vert() {
		return top + bottom;
	}
}
