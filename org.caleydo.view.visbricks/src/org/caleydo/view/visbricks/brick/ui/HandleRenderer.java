package org.caleydo.view.visbricks.brick.ui;

import javax.media.opengl.GL2;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.view.visbricks.brick.GLBrick;

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
	public static final int MOVE_HORIZONTALLY_HANDLE = 0xF;
	public static final int ALL_RESIZE_HANDLES = 0x1F;
	public static final int ALL_HANDLES = 0x3F;

	private GLBrick brick;
	private PixelGLConverter pixelGLConverter;
	private int handleSize;
	private TextureManager textureManager;
	private int handles;

	/**
	 * Constructor.
	 * 
	 * @param view
	 * @param pixelGLConverter
	 * @param handleSize
	 *            Size in pixels of the handles.
	 * @param textureManager
	 * @param handles
	 *            Specifies which handles shall be used, e.g.
	 *            MOVE_VERTICALLY_HANDLE | RESIZE_HANDLE_UPPER_LEFT
	 */
	public HandleRenderer(GLBrick brick, PixelGLConverter pixelGLConverter,
			int handleSize, TextureManager textureManager, int handles) {
		this.brick = brick;
		this.pixelGLConverter = pixelGLConverter;
		this.handleSize = handleSize;
		this.textureManager = textureManager;
		this.handles = handles;
	}

	@Override
	public void render(GL2 gl) {

		float glHandleHeight = pixelGLConverter.getGLHeightForPixelHeight(handleSize);
		float glHandleWidth = pixelGLConverter.getGLWidthForPixelWidth(handleSize);

		gl.glLineWidth(3);
		gl.glColor3f(0.2f, 0.2f, 0.2f);

		if ((handles & RESIZE_HANDLE_LOWER_LEFT) > 0) {
			gl.glPushName(brick.getPickingManager().getPickingID(brick.getID(),
					EPickingType.RESIZE_HANDLE_LOWER_LEFT, 1));
			gl.glBegin(GL2.GL_LINES);
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(glHandleWidth, 0, 0);
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(0, glHandleHeight, 0);
			gl.glEnd();
			gl.glPopName();
		}

		if ((handles & RESIZE_HANDLE_LOWER_RIGHT) > 0) {
			gl.glPushName(brick.getPickingManager().getPickingID(brick.getID(),
					EPickingType.RESIZE_HANDLE_LOWER_RIGHT, 1));
			gl.glBegin(GL2.GL_LINES);
			gl.glVertex3f(x, 0, 0);
			gl.glVertex3f(x - glHandleWidth, 0, 0);
			gl.glVertex3f(x, 0, 0);
			gl.glVertex3f(x, glHandleHeight, 0);
			gl.glEnd();
			gl.glPopName();
		}

		if ((handles & RESIZE_HANDLE_UPPER_RIGHT) > 0) {
			gl.glPushName(brick.getPickingManager().getPickingID(brick.getID(),
					EPickingType.RESIZE_HANDLE_UPPER_RIGHT, 1));
			gl.glBegin(GL2.GL_LINES);
			gl.glVertex3f(x, y, 0);
			gl.glVertex3f(x - glHandleWidth, y, 0);
			gl.glVertex3f(x, y, 0);
			gl.glVertex3f(x, y - glHandleHeight, 0);
			gl.glEnd();
			gl.glPopName();
		}

		if ((handles & RESIZE_HANDLE_UPPER_LEFT) > 0) {
			gl.glPushName(brick.getPickingManager().getPickingID(brick.getID(),
					EPickingType.RESIZE_HANDLE_UPPER_LEFT, 1));
			gl.glBegin(GL2.GL_LINES);
			gl.glVertex3f(0, y, 0);
			gl.glVertex3f(glHandleWidth, y, 0);
			gl.glVertex3f(0, y, 0);
			gl.glVertex3f(0, y - glHandleHeight, 0);
			gl.glEnd();
			gl.glPopName();
		}

		if ((handles & MOVE_VERTICALLY_HANDLE) > 0) {
			gl.glPushName(brick.getPickingManager().getPickingID(
					brick.getDimensionGroup().getID(),
					EPickingType.MOVE_VERTICALLY_HANDLE, 0));
			//
			// Vec3f lowerLeftCorner = new Vec3f(-glHandleWidth * 2.0f, y / 2.0f
			// - glHandleHeight, 0);
			// Vec3f lowerRightCorner = new Vec3f(-glHandleWidth * 2.0f,
			// 2.0f + glHandleHeight, 0);
			// Vec3f upperRightCorner = new Vec3f(0, y / 2.0f + glHandleHeight,
			// 0);
			// Vec3f upperLeftCorner = new Vec3f(0, y / 2.0f - glHandleHeight,
			// 0);
			//
			// textureManager
			// .renderTexture(gl, EIconTextures.MOVE_ICON, lowerLeftCorner,
			// lowerRightCorner, upperRightCorner, upperLeftCorner, 1,
			// 1, 1, 1);
			gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3f(-glHandleWidth * 2.0f, y / 2.0f - glHandleHeight, 1);
			gl.glVertex3f(-glHandleWidth * 2.0f, y / 2.0f + glHandleHeight, 1);
			gl.glVertex3f(0, y / 2.0f + glHandleHeight, 1);
			gl.glVertex3f(0, y / 2.0f - glHandleHeight, 1);
			gl.glEnd();
			gl.glPopName();
		}

		if ((handles & MOVE_HORIZONTALLY_HANDLE) > 0) {
			gl.glPushName(brick.getPickingManager().getPickingID(
					brick.getDimensionGroup().getVisBricksView().getID(),
					EPickingType.MOVE_HORIZONTALLY_HANDLE,
					brick.getDimensionGroup().getID()));
			//
			// Vec3f lowerLeftCorner = new Vec3f(-glHandleWidth * 2.0f, y / 2.0f
			// - glHandleHeight, 0);
			// Vec3f lowerRightCorner = new Vec3f(-glHandleWidth * 2.0f,
			// 2.0f + glHandleHeight, 0);
			// Vec3f upperRightCorner = new Vec3f(0, y / 2.0f + glHandleHeight,
			// 0);
			// Vec3f upperLeftCorner = new Vec3f(0, y / 2.0f - glHandleHeight,
			// 0);
			//
			// textureManager
			// .renderTexture(gl, EIconTextures.MOVE_ICON, lowerLeftCorner,
			// lowerRightCorner, upperRightCorner, upperLeftCorner, 1,
			// 1, 1, 1);
			gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3f(x / 2f - glHandleWidth, y, 1);
			gl.glVertex3f(x / 2f + glHandleWidth, y, 1);
			gl.glVertex3f(x / 2f + glHandleWidth, y + 2 * glHandleHeight, 1);
			gl.glVertex3f(x / 2f - glHandleWidth, y + 2 * glHandleHeight, 1);
			gl.glEnd();
			gl.glPopName();
		}

	}

}
