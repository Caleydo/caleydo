package org.caleydo.view.visbricks.brick;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.TextureManager;

public class ButtonRenderer extends LayoutRenderer {

	public static final int TEXTURE_ROTATION_0 = 0;
	public static final int TEXTURE_ROTATION_90 = 1;
	public static final int TEXTURE_ROTATION_180 = 2;
	public static final int TEXTURE_ROTATION_270 = 3;

	private EIconTextures iconTexture;
	private TextureManager textureManager;
	private AGLView view;
	private Button button;
	private int textureRotation;

	public ButtonRenderer(Button button, AGLView view,
			EIconTextures iconTexture, TextureManager textureManager) {
		this.view = view;
		this.button = button;
		this.iconTexture = iconTexture;
		this.textureManager = textureManager;
		textureRotation = TEXTURE_ROTATION_0;
	}

	public ButtonRenderer(Button button, AGLView view,
			EIconTextures iconTexture, TextureManager textureManager,
			int textureRotation) {
		this.view = view;
		this.button = button;
		this.iconTexture = iconTexture;
		this.textureManager = textureManager;
		this.textureRotation = textureRotation;

	}

	@Override
	public void render(GL2 gl) {

		gl.glPushName(view.getPickingManager().getPickingID(view.getID(),
				button.getPickingType(), button.getButtonID()));

		Vec3f lowerLeftCorner = new Vec3f(0, 0, 0);
		Vec3f lowerRightCorner = new Vec3f(x, 0, 0);
		Vec3f upperRightCorner = new Vec3f(x, y, 0);
		Vec3f upperLeftCorner = new Vec3f(0, y, 0);

		switch (textureRotation) {
		case TEXTURE_ROTATION_0:
			textureManager.renderTexture(gl, iconTexture, lowerLeftCorner,
					lowerRightCorner, upperRightCorner, upperLeftCorner, 1, 1,
					1, 1);
			break;
		case TEXTURE_ROTATION_90:
			textureManager.renderTexture(gl, iconTexture, lowerRightCorner,
					upperRightCorner, upperLeftCorner, lowerLeftCorner, 1, 1,
					1, 1);
			break;
		case TEXTURE_ROTATION_180:
			textureManager.renderTexture(gl, iconTexture, upperRightCorner,
					upperLeftCorner, lowerLeftCorner, lowerRightCorner, 1, 1,
					1, 1);
			break;
		case TEXTURE_ROTATION_270:
			textureManager.renderTexture(gl, iconTexture, upperLeftCorner,
					lowerLeftCorner, lowerRightCorner, upperRightCorner, 1, 1,
					1, 1);
			break;
		}

		if (button.isSelected()) {

			gl.glColor4f(0.7f, 0.7f, 0.7f, 0.5f);
			gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3f(0, 0, 0);
			gl.glColor4f(0.55f, 0.55f, 0.55f, 0.5f);
			gl.glVertex3f(x, 0, 0);
			gl.glColor4f(0.3f, 0.3f, 0.3f, 0.5f);
			gl.glVertex3f(x, y, 0);
			gl.glColor4f(0.55f, 0.55f, 0.55f, 0.5f);
			gl.glVertex3f(0, y, 0);
			gl.glEnd();

			gl.glLineWidth(1);
			gl.glColor3f(0.3f, 0.3f, 0.3f);
			gl.glBegin(GL2.GL_LINE_LOOP);
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(x, 0, 0);
			gl.glVertex3f(x, y, 0);
			gl.glVertex3f(0, y, 0);
			gl.glEnd();

		}

		gl.glPopName();

	}

}
