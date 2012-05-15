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
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.view.stratomex.PickingType;
import org.caleydo.view.stratomex.brick.GLBrick;

/**
 * Renderer for handles used for resizing and moving a brick.
 * 
 * @author Christian Partl
 * @author Alexander Lex
 */
public class HandleRenderer extends LayoutRenderer {

	public static final int RESIZE_HANDLE_UPPER_LEFT = 0x1;
	public static final int RESIZE_HANDLE_UPPER_RIGHT = 0x2;
	public static final int RESIZE_HANDLE_LOWER_LEFT = 0x4;
	public static final int RESIZE_HANDLE_LOWER_RIGHT = 0x8;
	public static final int MOVE_VERTICALLY_HANDLE = 0x10;
	public static final int MOVE_HORIZONTALLY_HANDLE = 0x20;
	public static final int EXPAND_HANDLE = 0x40;

	public static final int ALL_RESIZE_HANDLES = 0xF;
	public static final int ALL_MOVE_HANDLES = 0x30;
	public static final int ALL_EXPAND_HANDLES = 0xC0;
	public static final int ALL_HANDLES = 0xFF;

	private static final float BUTTON_Z = 5f;

	private GLBrick brick;
	private int handleSize;
	private TextureManager textureManager;
	private int handles;

	private APickingListener brickPickingListener;

	private boolean hide = true;

	/**
	 * Constructor.
	 * 
	 * @param view
	 * @param handleSize
	 *            Size in pixels of the handles.
	 * @param textureManager
	 * @param handles
	 *            Specifies which handles shall be used, e.g.
	 *            MOVE_VERTICALLY_HANDLE | RESIZE_HANDLE_UPPER_LEFT
	 */
	public HandleRenderer(final GLBrick brick, int handleSize,
			TextureManager textureManager, int handles) {
		this.brick = brick;
		this.handleSize = handleSize;
		this.textureManager = textureManager;
		this.handles = handles;

		brick.addIDPickingListener(new APickingListener() {
			@Override
			public void clicked(Pick pick) {
				brick.getDimensionGroup().setVerticalMoveDraggingActive(true);
			}

		}, PickingType.MOVE_VERTICALLY_HANDLE.name(), brick.getID());

		brickPickingListener = new APickingListener() {
			@Override
			public void mouseOver(Pick pick) {
				if (pick.getObjectID() == brick.getID())
					hide = false;
				else
					hide = true;
			}
		};

		brick.getDimensionGroup().getVisBricksView()
				.addTypePickingListener(brickPickingListener, PickingType.BRICK.name());

	}

	@Override
	public void render(GL2 gl) {

		if (hide)
			return;

		float glHandleHeight = getPixelGLConverter()
				.getGLHeightForPixelHeight(handleSize);
		float glHandleWidth = getPixelGLConverter().getGLWidthForPixelWidth(handleSize);

		gl.glLineWidth(3);
		gl.glColor3f(0.6f, 0.6f, 0.6f);

		if ((handles & RESIZE_HANDLE_LOWER_LEFT) > 0) {
			gl.glPushName(brick.getPickingManager().getPickingID(brick.getID(),
					PickingType.RESIZE_HANDLE_LOWER_LEFT.name(), 1));
			gl.glBegin(GL2.GL_LINES);
			gl.glVertex3f(0, 0, BUTTON_Z);
			gl.glVertex3f(glHandleWidth, 0, BUTTON_Z);
			gl.glVertex3f(0, 0, BUTTON_Z);
			gl.glVertex3f(0, glHandleHeight, BUTTON_Z);
			gl.glEnd();
			gl.glPopName();
		}

		if ((handles & RESIZE_HANDLE_LOWER_RIGHT) > 0) {
			gl.glPushName(brick.getPickingManager().getPickingID(brick.getID(),
					PickingType.RESIZE_HANDLE_LOWER_RIGHT.name(), 1));
			gl.glBegin(GL2.GL_LINES);
			gl.glVertex3f(x, 0, BUTTON_Z);
			gl.glVertex3f(x - glHandleWidth, 0, BUTTON_Z);
			gl.glVertex3f(x, 0, 0);
			gl.glVertex3f(x, glHandleHeight, BUTTON_Z);
			gl.glEnd();
			gl.glPopName();
		}

		if ((handles & RESIZE_HANDLE_UPPER_RIGHT) > 0) {
			gl.glPushName(brick.getPickingManager().getPickingID(brick.getID(),
					PickingType.RESIZE_HANDLE_UPPER_RIGHT.name(), 1));
			gl.glBegin(GL2.GL_LINES);
			gl.glVertex3f(x, y, BUTTON_Z);
			gl.glVertex3f(x - glHandleWidth, y, BUTTON_Z);
			gl.glVertex3f(x, y, BUTTON_Z);
			gl.glVertex3f(x, y - glHandleHeight, BUTTON_Z);
			gl.glEnd();
			gl.glPopName();
		}

		if ((handles & RESIZE_HANDLE_UPPER_LEFT) > 0) {
			gl.glPushName(brick.getPickingManager().getPickingID(brick.getID(),
					PickingType.RESIZE_HANDLE_UPPER_LEFT.name(), 1));
			gl.glBegin(GL2.GL_LINES);
			gl.glVertex3f(0, y, BUTTON_Z);
			gl.glVertex3f(glHandleWidth, y, BUTTON_Z);
			gl.glVertex3f(0, y, 0);
			gl.glVertex3f(0, y - glHandleHeight, BUTTON_Z);
			gl.glEnd();
			gl.glPopName();
		}

		if ((handles & MOVE_VERTICALLY_HANDLE) > 0) {
			gl.glPushName(brick.getPickingManager().getPickingID(brick.getID(),
					PickingType.MOVE_VERTICALLY_HANDLE.name(), brick.getID()));
			// gl.glColor4f(1f, 1f, 1f, 1);
			Vec3f lowerLeftCorner = new Vec3f(-glHandleWidth, y / 2.0f - glHandleHeight,
					BUTTON_Z);
			Vec3f lowerRightCorner = new Vec3f(0, y / 2.0f - glHandleHeight, BUTTON_Z);
			Vec3f upperRightCorner = new Vec3f(0, y / 2.0f, BUTTON_Z);
			Vec3f upperLeftCorner = new Vec3f(-glHandleWidth, y / 2.0f, BUTTON_Z);

			textureManager.renderTexture(gl, EIconTextures.NAVIGATION_BACKGROUND,
					upperRightCorner, upperLeftCorner, lowerLeftCorner, lowerRightCorner,
					1, 1, 1, 1);

			lowerLeftCorner = new Vec3f(-glHandleWidth, y / 2.0f, BUTTON_Z);
			lowerRightCorner = new Vec3f(0, y / 2.0f, BUTTON_Z);
			upperRightCorner = new Vec3f(0, y / 2.0f + glHandleHeight, BUTTON_Z);
			upperLeftCorner = new Vec3f(-glHandleWidth, y / 2.0f + glHandleHeight,
					BUTTON_Z);

			textureManager.renderTexture(gl, EIconTextures.NAVIGATION_BACKGROUND,
					lowerLeftCorner, lowerRightCorner, upperRightCorner, upperLeftCorner,
					1, 1, 1, 1);

			// gl.glBegin(GL2.GL_QUADS);
			// gl.glVertex3f(-glHandleWidth * 2.0f, y / 2.0f - glHandleHeight,
			// 1);
			// gl.glVertex3f(-glHandleWidth * 2.0f, y / 2.0f + glHandleHeight,
			// 1);
			// gl.glVertex3f(0, y / 2.0f + glHandleHeight, 1);
			// gl.glVertex3f(0, y / 2.0f - glHandleHeight, 1);
			// gl.glEnd();
			gl.glPopName();
		}

		if ((handles & MOVE_HORIZONTALLY_HANDLE) > 0) {
			gl.glPushName(brick.getPickingManager().getPickingID(
					brick.getDimensionGroup().getVisBricksView().getID(),
					PickingType.MOVE_HORIZONTALLY_HANDLE.name(),
					brick.getDimensionGroup().getID()));
			
			Vec3f lowerLeftCorner = new Vec3f(x / 2f - glHandleWidth, y ,
					BUTTON_Z);
			Vec3f lowerRightCorner = new Vec3f(x / 2f, y, BUTTON_Z);
			Vec3f upperLeftCorner = new Vec3f(x / 2f, y + glHandleHeight, BUTTON_Z);
			Vec3f upperRightCorner = new Vec3f(x / 2f - glHandleWidth, y + glHandleHeight, BUTTON_Z);

			textureManager.renderTexture(gl, EIconTextures.NAVIGATION_BACKGROUND,
					lowerRightCorner, upperLeftCorner, upperRightCorner, lowerLeftCorner,
					1, 1, 1, 1);

			lowerLeftCorner = new Vec3f(x / 2f + glHandleWidth, y, BUTTON_Z);
			lowerRightCorner = new Vec3f(x / 2f,y, 1);
			upperLeftCorner = new Vec3f(x / 2f, y + glHandleHeight, BUTTON_Z);
			upperRightCorner = new Vec3f(x / 2f + glHandleWidth, y + glHandleHeight, BUTTON_Z);
			textureManager.renderTexture(gl, EIconTextures.NAVIGATION_BACKGROUND,
					upperLeftCorner, lowerRightCorner, lowerLeftCorner, upperRightCorner,
					1, 1, 1, 1);

			gl.glPopName();
		}

		gl.glColor3f(0, 0, 0);

		if ((handles & EXPAND_HANDLE) > 0) {
			gl.glPushName(brick.getPickingManager().getPickingID(brick.getID(),
					PickingType.EXPAND_LEFT_HANDLE.name(), brick.getID()));

			// gl.glBegin(GL2.GL_QUADS);
			// gl.glVertex3f(-glHandleWidth, y, 1);
			// gl.glVertex3f(0, y, 1);
			// gl.glVertex3f(0, y - 2 * glHandleHeight, 1);
			// gl.glVertex3f(-glHandleWidth, y - 2 * glHandleHeight, 1);
			// gl.glEnd();

			Vec3f lowerLeftCorner = new Vec3f(-glHandleWidth, y, BUTTON_Z);
			Vec3f lowerRightCorner = new Vec3f(0, y, BUTTON_Z);
			Vec3f upperLeftCorner = new Vec3f(0, y - 2 * glHandleHeight, BUTTON_Z);
			Vec3f upperRightCorner = new Vec3f(-glHandleWidth, y - 2 * glHandleHeight,
					BUTTON_Z);
			textureManager.renderTexture(gl, EIconTextures.NAVIGATION_NEXT_BIG_MIDDLE,
					upperLeftCorner, lowerRightCorner, lowerLeftCorner, upperRightCorner,
					1, 1, 1, 1);

			gl.glPopName();
		}

		if ((handles & EXPAND_HANDLE) > 0) {
			gl.glPushName(brick.getPickingManager().getPickingID(brick.getID(),
					PickingType.EXPAND_RIGHT_HANDLE.name(), brick.getID()));

			Vec3f lowerLeftCorner = new Vec3f(x, y, BUTTON_Z);
			Vec3f lowerRightCorner = new Vec3f(x + glHandleWidth, y, BUTTON_Z);
			Vec3f upperLeftCorner = new Vec3f(x + glHandleWidth, y - 2 * glHandleHeight,
					BUTTON_Z);
			Vec3f upperRightCorner = new Vec3f(x, y - 2 * glHandleHeight, BUTTON_Z);
			textureManager.renderTexture(gl, EIconTextures.NAVIGATION_NEXT_BIG_MIDDLE,
					lowerLeftCorner, upperRightCorner, upperLeftCorner, lowerRightCorner,
					1, 1, 1, 1);

			gl.glPopName();
		}

	}
}
