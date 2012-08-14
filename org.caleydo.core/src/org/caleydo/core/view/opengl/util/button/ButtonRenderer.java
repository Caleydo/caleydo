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
package org.caleydo.core.view.opengl.util.button;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.util.APickableLayoutRenderer;
import org.caleydo.core.view.opengl.util.texture.TextureManager;

/**
 * Renderer for a {@link Button}.
 * 
 * @author Christian Partl
 */
public class ButtonRenderer extends APickableLayoutRenderer {

	public static final int TEXTURE_ROTATION_0 = 0;
	public static final int TEXTURE_ROTATION_90 = 1;
	public static final int TEXTURE_ROTATION_180 = 2;
	public static final int TEXTURE_ROTATION_270 = 3;

	private TextureManager textureManager;
	// private AGLView view;
	private Button button;
	private int textureRotation;
	/** The z-coordinate of the button, defaults to 0.02 */
	private float zCoordinate = 0.02f;

	/**
	 * Constructor.
	 * 
	 * @param button
	 *            Button this renderer should be used for.
	 * @param view
	 *            View that calls this renderer.
	 * @param iconTexture
	 *            Texture for the button.
	 */
	public ButtonRenderer(Button button, AGLView view) {
		// this.view = view;
		this.view = view;
		this.button = button;
		this.textureManager = view.getTextureManager();
		textureRotation = TEXTURE_ROTATION_0;
	}

	/**
	 * Same as {@link #ButtonRenderer(Button, AGLView)} but with additional
	 * zCoordinate for button
	 * 
	 * @param button
	 * @param view
	 * @param textureManager
	 * @param zCoordinate
	 */
	public ButtonRenderer(Button button, AGLView view, TextureManager textureManager,
			float zCoordinate) {
		// this.view = view;
		this.view = view;
		this.button = button;
		this.textureManager = textureManager;
		this.zCoordinate = zCoordinate;
		textureRotation = TEXTURE_ROTATION_0;

	}

	/**
	 * Constructor.
	 * 
	 * @param button
	 *            Button this renderer should be used for.
	 * @param view
	 *            View that calls this renderer.
	 * @param iconTexture
	 *            Texture for the button.
	 * @param textureManager
	 *            TextureManager.
	 * @param textureRotation
	 *            Specifies the angle the texture of the button should be
	 *            rotated. Possible values: TEXTURE_ROTATION_0,
	 *            TEXTURE_ROTATION_90, TEXTURE_ROTATION_180,
	 *            TEXTURE_ROTATION_270
	 */
	public ButtonRenderer(Button button, AGLView view, TextureManager textureManager,
			int textureRotation) {
		this.view = view;
		this.button = button;
		this.textureManager = textureManager;
		this.textureRotation = textureRotation;
	}

	/**
	 * Same as {@link #ButtonRenderer(Button, AGLView, TextureManager, int)} but
	 * with additional zCoordinate for button
	 * 
	 * @param button
	 * @param view
	 * @param textureManager
	 * @param textureRotation
	 * @param zCoordinate
	 */
	public ButtonRenderer(Button button, AGLView view, TextureManager textureManager,
			int textureRotation, float zCoordinate) {
		this.view = view;
		this.button = button;
		this.textureManager = textureManager;
		this.textureRotation = textureRotation;
		this.zCoordinate = zCoordinate;
	}

	public int getTextureRotation() {
		return textureRotation;
	}

	public void setTextureRotation(int textureRotation) {
		this.textureRotation = textureRotation;
		setDisplayListDirty();
	}

	/**
	 * @param zCoordinate
	 *            setter, see {@link #zCoordinate}
	 */
	public void setZCoordinate(float zCoordinate) {
		this.zCoordinate = zCoordinate;
		setDisplayListDirty();
	}

	/**
	 * @return the zCoordinate, see {@link #zCoordinate}
	 */
	public float getZCoordinate() {
		return zCoordinate;
	}


	@Override
	protected void renderContent(GL2 gl) {
		if (!button.isVisible())
			return;

		pushNames(gl);
		gl.glPushName(view.getPickingManager().getPickingID(view.getID(),
				button.getPickingType(), button.getButtonID()));

		Vec3f lowerLeftCorner = new Vec3f(0, 0, zCoordinate);
		Vec3f lowerRightCorner = new Vec3f(x, 0, zCoordinate);
		Vec3f upperRightCorner = new Vec3f(x, y, zCoordinate);
		Vec3f upperLeftCorner = new Vec3f(0, y, zCoordinate);

		switch (textureRotation) {
		case TEXTURE_ROTATION_0:
			textureManager.renderTexture(gl, button.getIconTexture(), lowerLeftCorner,
					lowerRightCorner, upperRightCorner, upperLeftCorner, 1, 1, 1, 1);
			break;
		case TEXTURE_ROTATION_90:
			textureManager.renderTexture(gl, button.getIconTexture(), lowerRightCorner,
					upperRightCorner, upperLeftCorner, lowerLeftCorner, 1, 1, 1, 1);
			break;
		case TEXTURE_ROTATION_180:
			textureManager.renderTexture(gl, button.getIconTexture(), upperRightCorner,
					upperLeftCorner, lowerLeftCorner, lowerRightCorner, 1, 1, 1, 1);
			break;
		case TEXTURE_ROTATION_270:
			textureManager.renderTexture(gl, button.getIconTexture(), upperLeftCorner,
					lowerLeftCorner, lowerRightCorner, upperRightCorner, 1, 1, 1, 1);
			break;
		}

		if (button.isSelected()) {

			gl.glColor4f(0.7f, 0.7f, 0.7f, zCoordinate * 1.1f);
			gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3f(0, 0, 0);
			gl.glColor4f(0.55f, 0.55f, 0.55f, zCoordinate * 1.1f);
			gl.glVertex3f(x, 0, 0);
			gl.glColor4f(0.3f, 0.3f, 0.3f, zCoordinate * 1.1f);
			gl.glVertex3f(x, y, 0);
			gl.glColor4f(0.55f, 0.55f, 0.55f, zCoordinate * 1.1f);
			gl.glVertex3f(0, y, 0);
			gl.glEnd();

			gl.glLineWidth(1);
			gl.glColor3f(0.3f, 0.3f, 0.3f);
			gl.glBegin(GL2.GL_LINE_LOOP);
			gl.glVertex3f(0, 0, zCoordinate);
			gl.glVertex3f(x, 0, zCoordinate);
			gl.glVertex3f(x, y, zCoordinate);
			gl.glVertex3f(0, y, zCoordinate);
			gl.glEnd();

		}

		popNames(gl);
		gl.glPopName();
		
	}

	@Override
	protected boolean permitsDisplayLists() {
		// TODO Auto-generated method stub
		return false;
	}

}
