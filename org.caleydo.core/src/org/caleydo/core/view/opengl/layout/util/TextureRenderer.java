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
package org.caleydo.core.view.opengl.layout.util;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL2;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.util.texture.TextureManager;

/**
 * Renders a texture within the layout element.
 *
 * @author Partl
 */
public class TextureRenderer extends ALayoutRenderer {

	private String imagePath;
	private TextureManager textureManager;

	/**
	 * Constructor.
	 *
	 * @param imagePath
	 *            Path to the image that shall be used as texture.
	 * @param textureManager
	 */
	public TextureRenderer(String imagePath, TextureManager textureManager) {
		this.imagePath = imagePath;
		this.textureManager = textureManager;
	}

	@Override
	protected void renderContent(GL2 gl) {
		Vec3f lowerLeftCorner = new Vec3f(0, 0, 0);
		Vec3f lowerRightCorner = new Vec3f(x, 0, 0);
		Vec3f upperRightCorner = new Vec3f(x, y, 0);
		Vec3f upperLeftCorner = new Vec3f(0, y, 0);

		textureManager.renderTexture(gl, imagePath, lowerLeftCorner, lowerRightCorner,
 upperRightCorner,
				upperLeftCorner, Color.WHITE);
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return true;
	}

}
