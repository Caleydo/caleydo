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
package org.caleydo.view.dvi;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.util.texture.TextureManager;

public class ViewNodeBackGroundRenderer extends LayoutRenderer {

	private float[] color;
	private String imagePath;
	private TextureManager textureManager;

	/**
	 * Constructor.
	 * 
	 * @param color
	 *            Color of the rendered rectangle. The array must have a length
	 *            of 4 specifying the RGBA values of the color.
	 * @param imagePath
	 *            Path to the image that shall be used as texture.
	 * @param textureManager
	 */
	public ViewNodeBackGroundRenderer(float[] color, String imagePath,
			TextureManager textureManager) {
		this.color = color;
		this.imagePath = imagePath;
		this.textureManager = textureManager;
	}

	@Override
	public void renderContent(GL2 gl) {

		gl.glColor4fv(color, 0);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(x, 0, 0);
		gl.glVertex3f(x, y, 0);
		gl.glVertex3f(0, y, 0);
		gl.glEnd();

		if (imagePath != null) {
			float textureSize = Math.min(x, y);
			float posX = x / 2.0f - textureSize / 2.0f;
			float posY = y / 2.0f - textureSize / 2.0f;

			Vec3f lowerLeftCorner = new Vec3f(posX, posY, 0);
			Vec3f lowerRightCorner = new Vec3f(posX + textureSize, posY, 0);
			Vec3f upperRightCorner = new Vec3f(posX + textureSize, posY + textureSize, 0);
			Vec3f upperLeftCorner = new Vec3f(posX, posY + textureSize, 0);

			textureManager.renderTexture(gl, imagePath, lowerLeftCorner,
					lowerRightCorner, upperRightCorner, upperLeftCorner, color[0],
					color[1], color[2], 0.5f);
		}
	}

	@Override
	protected boolean permitsDisplayLists() {
		return false;
	}

}
