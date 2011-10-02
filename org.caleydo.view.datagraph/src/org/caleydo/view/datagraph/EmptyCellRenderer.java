package org.caleydo.view.datagraph;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.layout.util.ColorRenderer;

public class EmptyCellRenderer extends ColorRenderer {

	private int id;

	public EmptyCellRenderer(int id) {
		super(new float[] { 0.8f, 0.8f, 0.8f, 1f }, new float[] { 0.6f, 0.6f,
				0.6f, 1f }, 2);
		this.setID(id);
	}

	@Override
	public void render(GL2 gl) {
		
		super.render(gl);

//		float[] color = new float[] { 0.8f, 0.8f, 0.8f };
//		gl.glColor3fv(color, 0);
//		gl.glBegin(GL2.GL_QUADS);
//		gl.glVertex3f(0, 0, 0.1f);
//		gl.glVertex3f(x, 0, 0.1f);
//		gl.glVertex3f(x, y, 0.1f);
//		gl.glVertex3f(0, y, 0.1f);
//		gl.glEnd();
//
//		gl.glPushAttrib(GL2.GL_LINE_BIT);
//		gl.glLineWidth(2);
//
//		// gl.glColor3f(0.3f, 0.3f, 0.3f);
//		gl.glColor3f(color[0] - 0.2f, color[1] - 0.2f, color[2] - 0.2f);
//		gl.glBegin(GL2.GL_LINES);
//		gl.glVertex3f(0, 0, 0);
//		gl.glVertex3f(x, 0, 0);
//		gl.glVertex3f(0, 0, 0);
//		gl.glVertex3f(0, y, 0);
//
//		// gl.glColor3f(0.7f, 0.7f, 0.7f);
//		gl.glColor3f(color[0] - 0.2f, color[1] - 0.2f, color[2] - 0.2f);
//		gl.glVertex3f(0, y, 0);
//		gl.glVertex3f(x, y, 0);
//		gl.glVertex3f(x, 0, 0);
//		gl.glVertex3f(x, y, 0);
//
//		gl.glEnd();
//
//		gl.glPopAttrib();
	}

	public void setID(int id) {
		this.id = id;
	}

	public int getID() {
		return id;
	}

}
