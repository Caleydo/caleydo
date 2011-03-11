package org.caleydo.view.visbricks.brick;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL2;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.TextureManager;

public class ButtonRenderer extends LayoutRenderer {

	private int buttonID;
	private EPickingType pickingType;
	private EIconTextures iconTexture;
	private TextureManager textureManager;
	private AGLView view;

	public ButtonRenderer(AGLView view, EPickingType pickingType,
			int buttonID, EIconTextures iconTexture, TextureManager textureManager) {
		this.view = view;
		this.pickingType = pickingType;
		this.buttonID = buttonID;
		this.iconTexture = iconTexture;
		this.textureManager = textureManager;

	}

	@Override
	public void render(GL2 gl) {
		
		PickingManager pickingManager = view.getPickingManager();

		gl.glPushName(view.getPickingManager().getPickingID(view.getID(),
				pickingType, buttonID));

		Vec3f lowerLeftCorner = new Vec3f(0, 0, 0);
		Vec3f lowerRightCorner = new Vec3f(x, 0, 0);
		Vec3f upperRightCorner = new Vec3f(x, y, 0);
		Vec3f upperLeftCorner = new Vec3f(0, y, 0);

		textureManager
				.renderTexture(gl, iconTexture, lowerLeftCorner,
						lowerRightCorner, upperRightCorner, upperLeftCorner, 1,
						1, 1, 1);
		gl.glEnd();
		gl.glPopName();

	}

}
