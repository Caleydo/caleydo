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
package org.caleydo.view.tourguide.renderer;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.util.texture.TextureManager;

/**
 * Renders a texture within the layout element.
 *
 * @author Partl
 */
public class AnimatedTextureRenderer extends LayoutRenderer {
	private final String imagePath;
	private final TextureManager textureManager;
	private final int delay;

	private long last = 0;

	public AnimatedTextureRenderer(String imagePath, int delay, TextureManager textureManager) {
		this.imagePath = imagePath;
		this.delay = delay;
		this.textureManager = textureManager;
	}

	@Override
	protected void renderContent(GL2 gl) {
		// TODO perform animation by rendering
		// if (this.last == 0)
		// this.last = System.currentTimeMillis();
		// long now = System.currentTimeMillis();
		// if (now-last > delay) {
		// last
		// }

		Vec3f lowerLeftCorner = new Vec3f(x * .3f, y * 0.3f, 0);
		Vec3f lowerRightCorner = new Vec3f(x * .6f, y * 0.3f, 0);
		Vec3f upperRightCorner = new Vec3f(x * .6f, y * .6f, 0);
		Vec3f upperLeftCorner = new Vec3f(x * .3f, y * .6f, 0);

		textureManager.renderTexture(gl, imagePath, lowerLeftCorner, lowerRightCorner,
				upperRightCorner, upperLeftCorner, 1, 1, 1, 1);
	}

	@Override
	protected boolean permitsDisplayLists() {
		return true;
	}

}
