package org.caleydo.view.visbricks.brick.ui;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.view.visbricks.PickingType;
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
    public static final int MOVE_HORIZONTALLY_HANDLE = 0x20;
    public static final int EXPAND_LEFT_HANDLE = 0x40;
    public static final int EXPAND_RIGHT_HANDLE = 0x80;

    public static final int ALL_RESIZE_HANDLES = 0xF;
    public static final int ALL_MOVE_HANDLES = 0x30;
    public static final int ALL_EXPAND_HANDLES = 0xC0;
    public static final int ALL_HANDLES = 0xFF;

    private GLBrick brick;
    private int handleSize;
    private TextureManager textureManager;
    private int handles;

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
    }

    @Override
    public void render(GL2 gl) {

	float glHandleHeight = getPixelGLConverter().getGLHeightForPixelHeight(
		handleSize);
	float glHandleWidth = getPixelGLConverter().getGLWidthForPixelWidth(
		handleSize);

	gl.glLineWidth(3);
	gl.glColor3f(0.6f, 0.6f, 0.6f);

	if ((handles & RESIZE_HANDLE_LOWER_LEFT) > 0) {
	    gl.glPushName(brick.getPickingManager().getPickingID(brick.getID(),
		    PickingType.RESIZE_HANDLE_LOWER_LEFT.name(), 1));
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
		    PickingType.RESIZE_HANDLE_LOWER_RIGHT.name(), 1));
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
		    PickingType.RESIZE_HANDLE_UPPER_RIGHT.name(), 1));
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
		    PickingType.RESIZE_HANDLE_UPPER_LEFT.name(), 1));
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
		    PickingType.MOVE_VERTICALLY_HANDLE.name(), 0));
	    // gl.glColor4f(1f, 1f, 1f, 1);
	    Vec3f lowerLeftCorner = new Vec3f(-glHandleWidth, y / 2.0f
		    - glHandleHeight, 1);
	    Vec3f lowerRightCorner = new Vec3f(0, y / 2.0f - glHandleHeight, 1);
	    Vec3f upperRightCorner = new Vec3f(0, y / 2.0f, 1);
	    Vec3f upperLeftCorner = new Vec3f(-glHandleWidth, y / 2.0f, 1);

	    textureManager.renderTexture(gl,
		    EIconTextures.NAVIGATION_BACKGROUND, upperRightCorner,
		    upperLeftCorner, lowerLeftCorner, lowerRightCorner, 1, 1,
		    1, 1);

	    lowerLeftCorner = new Vec3f(-glHandleWidth, y / 2.0f, 1);
	    lowerRightCorner = new Vec3f(0, y / 2.0f, 1);
	    upperRightCorner = new Vec3f(0, y / 2.0f + glHandleHeight, 1);
	    upperLeftCorner = new Vec3f(-glHandleWidth, y / 2.0f
		    + glHandleHeight, 1);

	    textureManager.renderTexture(gl,
		    EIconTextures.NAVIGATION_BACKGROUND, lowerLeftCorner,
		    lowerRightCorner, upperRightCorner, upperLeftCorner, 1, 1,
		    1, 1);

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
	    // gl.glBegin(GL2.GL_QUADS);
	    // gl.glVertex3f(x / 2f - glHandleWidth, y, 1);
	    // gl.glVertex3f(x / 2f + glHandleWidth, y, 1);
	    // gl.glVertex3f(x / 2f + glHandleWidth, y + 2 * glHandleHeight, 1);
	    // gl.glVertex3f(x / 2f - glHandleWidth, y + 2 * glHandleHeight, 1);
	    // gl.glEnd();

	    Vec3f lowerLeftCorner = new Vec3f(x / 2f - glHandleWidth, y, 1);
	    Vec3f lowerRightCorner = new Vec3f(x / 2f, y, 1);
	    Vec3f upperLeftCorner = new Vec3f(x / 2f, y + glHandleHeight, 1);
	    Vec3f upperRightCorner = new Vec3f(x / 2f - glHandleWidth, y
		    + glHandleHeight, 1);

	    textureManager.renderTexture(gl,
		    EIconTextures.NAVIGATION_BACKGROUND, lowerRightCorner,
		    upperLeftCorner, upperRightCorner, lowerLeftCorner, 1, 1,
		    1, 1);

	    lowerLeftCorner = new Vec3f(x / 2f + glHandleWidth, y, 1);
	    lowerRightCorner = new Vec3f(x / 2f, y, 1);
	    upperLeftCorner = new Vec3f(x / 2f, y + glHandleHeight, 1);
	    upperRightCorner = new Vec3f(x / 2f + glHandleWidth, y
		    + glHandleHeight, 1);
	    textureManager.renderTexture(gl,
		    EIconTextures.NAVIGATION_BACKGROUND, upperLeftCorner,
		    lowerRightCorner, lowerLeftCorner, upperRightCorner, 1, 1,
		    1, 1);

	    gl.glPopName();
	}

	gl.glColor3f(0, 0, 0);

	if ((handles & EXPAND_LEFT_HANDLE) > 0) {
	    gl.glPushName(brick.getPickingManager().getPickingID(brick.getID(),
		    PickingType.EXPAND_LEFT_HANDLE.name(), brick.getID()));

	    // gl.glBegin(GL2.GL_QUADS);
	    // gl.glVertex3f(-glHandleWidth, y, 1);
	    // gl.glVertex3f(0, y, 1);
	    // gl.glVertex3f(0, y - 2 * glHandleHeight, 1);
	    // gl.glVertex3f(-glHandleWidth, y - 2 * glHandleHeight, 1);
	    // gl.glEnd();

	    Vec3f lowerLeftCorner = new Vec3f(-glHandleWidth, y, 1);
	    Vec3f lowerRightCorner = new Vec3f(0, y, 1);
	    Vec3f upperLeftCorner = new Vec3f(0, y - 2 * glHandleHeight, 1);
	    Vec3f upperRightCorner = new Vec3f(-glHandleWidth, y - 2
		    * glHandleHeight, 1);
	    textureManager.renderTexture(gl,
		    EIconTextures.NAVIGATION_NEXT_BIG_MIDDLE, upperLeftCorner,
		    lowerRightCorner, lowerLeftCorner, upperRightCorner, 1, 1,
		    1, 1);

	    gl.glPopName();
	}

	if ((handles & EXPAND_RIGHT_HANDLE) > 0) {
	    gl.glPushName(brick.getPickingManager().getPickingID(brick.getID(),
		    PickingType.EXPAND_RIGHT_HANDLE.name(), brick.getID()));

	    Vec3f lowerLeftCorner = new Vec3f(x, y, 1);
	    Vec3f lowerRightCorner = new Vec3f(x + glHandleWidth, y, 1);
	    Vec3f upperLeftCorner = new Vec3f(x + glHandleWidth, y - 2
		    * glHandleHeight, 1);
	    Vec3f upperRightCorner = new Vec3f(x, y - 2 * glHandleHeight, 1);
	    textureManager.renderTexture(gl,
		    EIconTextures.NAVIGATION_NEXT_BIG_MIDDLE, lowerLeftCorner,
		    upperRightCorner, upperLeftCorner, lowerRightCorner, 1, 1,
		    1, 1);

	    gl.glPopName();
	}

    }
}
