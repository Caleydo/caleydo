/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.parcoords.v2.internal;

import gleem.linalg.Vec2f;

import org.caleydo.core.view.opengl.layout2.GLGraphics;

/**
 * @author Samuel Gratzl
 *
 */
public class Brush {

	private final Vec2f start;

	/**
	 * @param pickedPoint
	 */
	public Brush(Vec2f start) {
		this.start = start;
	}

	/**
	 * @param g
	 */
	public void render(GLGraphics g) {
		// TODO Auto-generated method stub

	}

}
