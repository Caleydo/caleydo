package org.caleydo.view.visbricks.brick;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.layout.ARenderer;

public class BrickRenderer extends ARenderer {

	private GLBrick brick;

	public BrickRenderer(GLBrick brick) {
		this.brick = brick;
	}

	public GLBrick getBrick() {
		return brick;
	}

	@Override
	public void render(GL2 gl) {
		brick.displayRemote(gl);
	}

	@Override
	public void setLimits(float x, float y) {
		brick.getViewFrustum().setRight(x);
		brick.getViewFrustum().setTop(y);
	}
}
