package org.caleydo.view.datagraph;

import javax.media.opengl.GL2;

public class EmptyCellRenderer extends ARenderer {

	private int id;

	public EmptyCellRenderer(int id) {
		this.setID(id);
	}

	@Override
	public void render(GL2 gl) {
		gl.glColor3f(0.7f, 0.7f, 0.7f);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(0, 0, 0.1f);
		gl.glVertex3f(x, 0, 0.1f);
		gl.glVertex3f(x, y, 0.1f);
		gl.glVertex3f(0, y, 0.1f);
		gl.glEnd();
	}

	public void setID(int id) {
		this.id = id;
	}

	public int getID() {
		return id;
	}

}
