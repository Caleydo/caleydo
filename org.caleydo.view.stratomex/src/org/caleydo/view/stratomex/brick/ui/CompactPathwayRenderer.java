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
package org.caleydo.view.stratomex.brick.ui;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.TextureManager;

/**
 * Renderer for a pathway icon and text.
 * 
 * @author Partl
 * 
 */
public class CompactPathwayRenderer extends ALayoutRenderer {

	private static final int ICON_SIZE_PIXELS = 16;
	private static final int SPACING_PIXELS = 4;

	private AGLView view;
	private String caption;
	private String pickingType;
	private TextureManager textureManager;
	private EIconTextures texture;
	private int id;

	public CompactPathwayRenderer(AGLView view, String caption, String pickingType,
			int id, TextureManager textureManager, EIconTextures texture) {
		this.view = view;
		this.caption = caption;
		this.pickingType = pickingType;
		this.id = id;
		this.textureManager = textureManager;
		this.texture = texture;
	}

	@Override
	public void renderContent(GL2 gl) {

		int pickingID = view.getPickingManager().getPickingID(view.getID(), pickingType,
				id);

		PixelGLConverter pixelGLConverter = view.getPixelGLConverter();
		float iconHeight = pixelGLConverter.getGLHeightForPixelHeight(ICON_SIZE_PIXELS);
		float iconWidth = pixelGLConverter.getGLWidthForPixelWidth(ICON_SIZE_PIXELS);
		float spacingWidth = pixelGLConverter.getGLWidthForPixelWidth(SPACING_PIXELS);

		gl.glPushName(pickingID);
		gl.glColor4f(1, 1, 1, 0);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex2f(0, 0);
		gl.glVertex2f(x, 0);
		gl.glVertex2f(x, y);
		gl.glVertex2f(0, y);
		gl.glEnd();

		Vec3f lowerLeftCorner = new Vec3f(0, 0, 0);
		Vec3f lowerRightCorner = new Vec3f(iconWidth, 0, 0);
		Vec3f upperRightCorner = new Vec3f(iconWidth, iconHeight, 0);
		Vec3f upperLeftCorner = new Vec3f(0, iconHeight, 0);

		textureManager.renderTexture(gl, texture, lowerLeftCorner, lowerRightCorner,
				upperRightCorner, upperLeftCorner, 1, 1, 1, 1);

		gl.glPopName();

		CaleydoTextRenderer textRenderer = view.getTextRenderer();

		float ySpacing = view.getPixelGLConverter().getGLHeightForPixelHeight(1);

		textRenderer.setColor(0, 0, 0, 1);
		textRenderer.renderTextInBounds(gl, caption, iconWidth + spacingWidth, ySpacing,
				0, x - (iconWidth + spacingWidth), y - 2 * ySpacing);

	}
	
	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}

	@Override
	public int getMinHeightPixels() {
		return ICON_SIZE_PIXELS;
	}

	@Override
	public int getMinWidthPixels() {
		return ICON_SIZE_PIXELS + SPACING_PIXELS + 60;
	}
}
