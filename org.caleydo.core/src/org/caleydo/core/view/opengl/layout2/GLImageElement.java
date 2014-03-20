/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2;

import gleem.linalg.Vec2f;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.util.color.Color;

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

	protected Color color = Color.WHITE;

	protected float alphaTestThreshold = .05f;

	public GLImageElement(String path) {
		texturePath = path;

		// Default size to prevent culling (will be updated
		// during first render pass)
		setSize(32, 32);
	}

	public void setColor(Color color) {
		this.color = color.clone();
	}

	public void setAlphaThreshold(float minAlpha) {
		alphaTestThreshold = minAlpha;
	}

	@Override
	public Vec2f getMinSize() {
		if( texture != null )
			return new Vec2f(texture.getWidth(), texture.getHeight());
		else
			return new Vec2f(32, 32);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		if (texture == null) {
			texture = g.getTexture(texturePath);
			setSize(texture.getWidth(), texture.getHeight());
			relayout();

			// Do not render before relayout with correct size
			return;
		}

		// Alpha test (required to use depth base picking)
		g.gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);
		g.gl.glAlphaFunc(GL.GL_GREATER, alphaTestThreshold);
		g.gl.glEnable(GL2.GL_ALPHA_TEST);

		g.fillImage(texture, 0, 0, w, h, color);

		g.gl.glPopAttrib();
	}

}
