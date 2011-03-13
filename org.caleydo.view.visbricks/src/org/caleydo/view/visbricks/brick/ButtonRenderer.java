package org.caleydo.view.visbricks.brick;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.TextureManager;

public class ButtonRenderer extends LayoutRenderer {

	private EIconTextures iconTexture;
	private TextureManager textureManager;
	private AGLView view;
	private Button button;

	public ButtonRenderer(Button button, AGLView view,
			EIconTextures iconTexture, TextureManager textureManager) {
		this.view = view;
		this.button = button;
		this.iconTexture = iconTexture;
		this.textureManager = textureManager;

	}

	@Override
	public void render(GL2 gl) {

		gl.glPushName(view.getPickingManager().getPickingID(view.getID(),
				button.getPickingType(), button.getButtonID()));

		Vec3f lowerLeftCorner = new Vec3f(0, 0, 0);
		Vec3f lowerRightCorner = new Vec3f(x, 0, 0);
		Vec3f upperRightCorner = new Vec3f(x, y, 0);
		Vec3f upperLeftCorner = new Vec3f(0, y, 0);
		
		textureManager
		.renderTexture(gl, iconTexture, lowerLeftCorner,
				lowerRightCorner, upperRightCorner, upperLeftCorner, 1,
				1, 1, 1);

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

		
		gl.glEnd();
		gl.glPopName();

	}

}
