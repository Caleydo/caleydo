package org.caleydo.view.visbricks.brick.ui;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;

/**
 * Simple renderer for a background rectangle.
 * 
 * @author Christian Partl
 *
 */
public class BackGroundRenderer extends LayoutRenderer {
	
	private AGLView view;
	
	public BackGroundRenderer(AGLView view) {
		this.view = view;
	}

	@Override
	public void render(GL2 gl) {
		
//		gl.glPushName(view.getPickingManager().getPickingID(view.getID(),
//				EPickingType.BRICK, view.getID()));
		
		gl.glColor3f(1, 1, 1);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(x, 0, 0);
		gl.glVertex3f(x, y, 0);
		gl.glVertex3f(0, y, 0);
		gl.glEnd();
//		
//		gl.glPopName();

	}
}
