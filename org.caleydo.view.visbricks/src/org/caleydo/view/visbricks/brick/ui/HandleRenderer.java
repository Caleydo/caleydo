package org.caleydo.view.visbricks.brick.ui;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL2;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.TextureManager;

/**
 * Renderer for handles used for resizing and moving a brick.
 * 
 * @author Christian Partl
 * 
 */
public class HandleRenderer extends LayoutRenderer {

	private AGLView view;
	private PixelGLConverter pixelGLConverter;
	private int handleSize;
	private TextureManager textureManager;

	/**
	 * Constructor.
	 * 
	 * @param view
	 * @param pixelGLConverter
	 * @param handleSize
	 *            Size in pixels of the handles.
	 * @param textureManager
	 */
	public HandleRenderer(AGLView view, PixelGLConverter pixelGLConverter,
			int handleSize, TextureManager textureManager) {
		this.view = view;
		this.pixelGLConverter = pixelGLConverter;
		this.handleSize = handleSize;
		this.textureManager = textureManager;
	}

	@Override
	public void render(GL2 gl) {

		float glHandleHeight = pixelGLConverter
				.getGLHeightForPixelHeight(handleSize);
		float glHandleWidth = pixelGLConverter
				.getGLWidthForPixelWidth(handleSize);

		gl.glLineWidth(3);
		gl.glColor3f(0, 0, 0);

		gl.glPushName(view.getPickingManager().getPickingID(view.getID(),
				EPickingType.RESIZE_HANDLE_LOWER_LEFT, view.getID()));
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(glHandleWidth, 0, 0);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(0, glHandleHeight, 0);
		gl.glEnd();
		gl.glPopName();

		gl.glPushName(view.getPickingManager().getPickingID(view.getID(),
				EPickingType.RESIZE_HANDLE_LOWER_RIGHT, view.getID()));
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3f(x, 0, 0);
		gl.glVertex3f(x - glHandleWidth, 0, 0);
		gl.glVertex3f(x, 0, 0);
		gl.glVertex3f(x, glHandleHeight, 0);
		gl.glEnd();
		gl.glPopName();

		gl.glPushName(view.getPickingManager().getPickingID(view.getID(),
				EPickingType.RESIZE_HANDLE_UPPER_RIGHT, view.getID()));
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3f(x, y, 0);
		gl.glVertex3f(x - glHandleWidth, y, 0);
		gl.glVertex3f(x, y, 0);
		gl.glVertex3f(x, y - glHandleHeight, 0);
		gl.glEnd();
		gl.glPopName();

		gl.glPushName(view.getPickingManager().getPickingID(view.getID(),
				EPickingType.RESIZE_HANDLE_UPPER_LEFT, view.getID()));
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3f(0, y, 0);
		gl.glVertex3f(glHandleWidth, y, 0);
		gl.glVertex3f(0, y, 0);
		gl.glVertex3f(0, y - glHandleHeight, 0);
		gl.glEnd();
		gl.glPopName();

		gl.glPushName(view.getPickingManager().getPickingID(view.getID(),
				EPickingType.DRAGGING_HANDLE, view.getID()));
		//
		// Vec3f lowerLeftCorner = new Vec3f(-glHandleWidth * 2.0f, y / 2.0f
		// - glHandleHeight, 0);
		// Vec3f lowerRightCorner = new Vec3f(-glHandleWidth * 2.0f,
		// 2.0f + glHandleHeight, 0);
		// Vec3f upperRightCorner = new Vec3f(0, y / 2.0f + glHandleHeight, 0);
		// Vec3f upperLeftCorner = new Vec3f(0, y / 2.0f - glHandleHeight, 0);
		//
		// textureManager
		// .renderTexture(gl, EIconTextures.MOVE_ICON, lowerLeftCorner,
		// lowerRightCorner, upperRightCorner, upperLeftCorner, 1,
		// 1, 1, 1);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(-glHandleWidth * 2.0f, y / 2.0f - glHandleHeight, 0);
		gl.glVertex3f(-glHandleWidth * 2.0f, y / 2.0f + glHandleHeight, 0);
		gl.glVertex3f(0, y / 2.0f + glHandleHeight, 0);
		gl.glVertex3f(0, y / 2.0f - glHandleHeight, 0);
		gl.glEnd();
		gl.glPopName();

	}

}
