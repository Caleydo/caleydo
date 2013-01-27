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
package org.caleydo.view.tourguide.internal.renderer;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout.IDim;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.layout.Row.HAlign;
import org.caleydo.core.view.opengl.util.texture.TextureManager;

/**
 * Renders a texture within the layout element.
 *
 * @author Partl
 */
public class DecorationTextureRenderer extends ALayoutRenderer {
	private final TextureManager textureManager;
	private String imagePath;
	private float z;

	private final HAlign hAlign;
	private final VAlign vAlign;
	private final IDim width;
	private final IDim height;

	public DecorationTextureRenderer(String imagePath, TextureManager textureManager, IDim width, IDim height,
			HAlign hAlign, VAlign vAlign) {
		this.imagePath = imagePath;
		this.textureManager = textureManager;
		this.hAlign = hAlign;
		this.vAlign = vAlign;
		this.width = width;
		this.height = height;
		this.z = 0;
	}

	/**
	 * @param z
	 *            the z to set
	 */
	public DecorationTextureRenderer setZ(float z) {
		if (this.z == z)
			return this;
		this.z = z;
		setDisplayListDirty();
		return this;
	}

	/**
	 * @return the z, see {@link #z}
	 */
	public float getZ() {
		return z;
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
		PixelGLConverter pixelGLConverter = elementLayout.getLayoutManager().getPixelGLConverter();
		float w = width.resolve(pixelGLConverter, this.x, this.y);
		float h = height.resolve(pixelGLConverter, this.x, this.y);
		float x = 0, y = 0;
		switch (vAlign) {
		case LEFT:
			x = 0;
			break;
		case RIGHT:
			x = this.x - w;
			break;
		case CENTER:
			x = this.x / 2 - w / 2;
			break;
		}
		switch (hAlign) {
		case BOTTOM:
			y = 0;
			break;
		case TOP:
			y = this.y - h;
			break;
		case CENTER:
			y = this.y / 2 - h / 2;
			break;
		}
		Vec3f lowerLeftCorner = new Vec3f(x, y, z);
		Vec3f lowerRightCorner = new Vec3f(x + w, y, z);
		Vec3f upperRightCorner = new Vec3f(x + w, y + h, z);
		Vec3f upperLeftCorner = new Vec3f(x, y + h, z);

		textureManager.renderTexture(gl, imagePath, lowerLeftCorner, lowerRightCorner,
				upperRightCorner, upperLeftCorner, 1, 1, 1, 1);
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return true;
	}

}
