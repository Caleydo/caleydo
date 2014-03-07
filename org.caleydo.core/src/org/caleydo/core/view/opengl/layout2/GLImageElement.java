/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2;

import com.jogamp.opengl.util.texture.Texture;

/**
 * Render image and set setSize to size of image on loading.
 * 
 * @author Thomas Geymayer
 * 
 */
public class GLImageElement extends GLElement {

	/**
	 * Path to the texture to display
	 */
	protected String texturePath;

	protected Texture texture;

	public GLImageElement(String path) {
		texturePath = path;

		// Default size to prevent culling (will be updated
		// during first render pass)
		setSize(32, 32);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		if (texture == null) {
			texture = g.getTexture(texturePath);
			setSize(texture.getWidth(), texture.getHeight());
			relayout();
		}

		g.fillImage(texture, 0, 0, w, h);
	}

}
