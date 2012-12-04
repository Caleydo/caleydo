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
import org.caleydo.core.view.opengl.layout.Padding;
import org.caleydo.core.view.opengl.util.texture.TextureManager;

/**
 * Renders a texture within the layout element.
 *
 * @author Partl
 */
public class AdvancedTextureRenderer extends LayoutRenderer {
	private final TextureManager textureManager;
	private final Padding padding;
	private String imagePath;

	/**
	 * Constructor.
	 *
	 * @param imagePath
	 *            Path to the image that shall be used as texture.
	 * @param textureManager
	 */
	public AdvancedTextureRenderer(String imagePath, TextureManager textureManager) {
		this(imagePath, textureManager, null);
	}

	public AdvancedTextureRenderer(String imagePath, TextureManager textureManager, Padding padding) {
		this.imagePath = imagePath;
		this.textureManager = textureManager;
		this.padding = padding == null ? Padding.NONE : padding;
	}

	/**
	 * @return the imagePath, see {@link #imagePath}
	 */
	public String getImagePath() {
		return imagePath;
	}

	/**
	 * @param imagePath
	 *            the imagePath to set
	 */
	public void setImagePath(String imagePath) {
		if (this.imagePath != null && this.imagePath.equals(imagePath))
			return;
		this.imagePath = imagePath;
		if (this.imagePath != null)
			setDisplayListDirty();
	}

	@Override
	protected void renderContent(GL2 gl) {
		if (this.imagePath == null)
			return;
		float[] p = padding.resolve(layoutManager.getPixelGLConverter());
		Vec3f lowerLeftCorner = new Vec3f(p[0], p[3], 0);
		Vec3f lowerRightCorner = new Vec3f(x - p[2], p[3], 0);
		Vec3f upperRightCorner = new Vec3f(x - p[2], y - p[1], 0);
		Vec3f upperLeftCorner = new Vec3f(p[0], y - p[1], 0);

		textureManager.renderTexture(gl, imagePath, lowerLeftCorner, lowerRightCorner,
				upperRightCorner, upperLeftCorner, 1, 1, 1, 1);
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return true;
	}

}
