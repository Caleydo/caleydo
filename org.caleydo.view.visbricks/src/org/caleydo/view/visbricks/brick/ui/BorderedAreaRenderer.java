package org.caleydo.view.visbricks.brick.ui;

import javax.media.opengl.GL2;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;

/**
 * Renders a bordered area.
 * 
 * @author Christian Partl
 * 
 */
public class BorderedAreaRenderer extends LayoutRenderer {

	@Override
	public void render(GL2 gl) {

		gl.glColor3f(0.35f, 0.35f, 0.35f);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(0, 0, 0);
		gl.glColor3f(0.5f, 0.5f, 0.5f);
		gl.glVertex3f(x, 0, 0);
		gl.glColor3f(0.65f, 0.65f, 0.65f);
		gl.glVertex3f(x, y, 0);
		gl.glColor3f(0.5f, 0.5f, 0.5f);
		gl.glVertex3f(0, y, 0);
		gl.glEnd();

		gl.glPushAttrib(GL2.GL_LINE_BIT);
		gl.glLineWidth(2);

		gl.glColor3f(0.3f, 0.3f, 0.3f);
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(x, 0, 0);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(0, y, 0);

		gl.glColor3f(0.7f, 0.7f, 0.7f);
		gl.glVertex3f(0, y, 0);
		gl.glVertex3f(x, y, 0);
		gl.glVertex3f(x, 0, 0);
		gl.glVertex3f(x, y, 0);

		gl.glEnd();

		gl.glPopAttrib();
	}

}
