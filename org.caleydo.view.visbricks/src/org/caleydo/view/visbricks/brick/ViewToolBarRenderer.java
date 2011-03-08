package org.caleydo.view.visbricks.brick;

import javax.media.opengl.GL2;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;

public class ViewToolBarRenderer extends LayoutRenderer {

	GLBrick brick;

	public ViewToolBarRenderer(GLBrick brick) {
		this.brick = brick;

	}

	@Override
	public void render(GL2 gl) {
		gl.glPushName(brick.getPickingManager().getPickingID(brick.getID(),
				EPickingType.BRICK_CLUSTER, 1));
		gl.glColor3f(1f, 0, 0);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(y, 0, 0);
		gl.glVertex3f(y, y, 0);
		gl.glVertex3f(0, y, 0);
		gl.glEnd();
		gl.glPopName();
	}
}
