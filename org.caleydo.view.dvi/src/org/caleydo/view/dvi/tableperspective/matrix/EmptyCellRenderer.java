/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.tableperspective.matrix;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.layout.util.ColorRenderer;

class EmptyCellRenderer extends ColorRenderer {

	public static final float[] DEFAULT_COLOR = { 0.9f, 0.9f, 0.9f, 1f };
	public static final float[] DEFAULT_BORDER_COLOR = { 0.7f, 0.7f, 0.7f, 1f };

	private int id;

	public EmptyCellRenderer(int id) {
		super(DEFAULT_COLOR, DEFAULT_BORDER_COLOR, 2);
		this.setID(id);
	}

	@Override
	public void renderContent(GL2 gl) {
		gl.glPushMatrix();
		gl.glTranslatef(0, 0, 0.1f);
		super.renderContent(gl);
		gl.glPopMatrix();

		// float[] color = new float[] { 0.8f, 0.8f, 0.8f };
		// gl.glColor3fv(color, 0);
		// gl.glBegin(GL2.GL_QUADS);
		// gl.glVertex3f(0, 0, 0.1f);
		// gl.glVertex3f(x, 0, 0.1f);
		// gl.glVertex3f(x, y, 0.1f);
		// gl.glVertex3f(0, y, 0.1f);
		// gl.glEnd();
		//
		// gl.glPushAttrib(GL2.GL_LINE_BIT);
		// gl.glLineWidth(2);
		//
		// // gl.glColor3f(0.3f, 0.3f, 0.3f);
		// gl.glColor3f(color[0] - 0.2f, color[1] - 0.2f, color[2] - 0.2f);
		// gl.glBegin(GL.GL_LINES);
		// gl.glVertex3f(0, 0, 0);
		// gl.glVertex3f(x, 0, 0);
		// gl.glVertex3f(0, 0, 0);
		// gl.glVertex3f(0, y, 0);
		//
		// // gl.glColor3f(0.7f, 0.7f, 0.7f);
		// gl.glColor3f(color[0] - 0.2f, color[1] - 0.2f, color[2] - 0.2f);
		// gl.glVertex3f(0, y, 0);
		// gl.glVertex3f(x, y, 0);
		// gl.glVertex3f(x, 0, 0);
		// gl.glVertex3f(x, y, 0);
		//
		// gl.glEnd();
		//
		// gl.glPopAttrib();
	}

	public void setID(int id) {
		this.id = id;
	}

	public int getID() {
		return id;
	}
	
	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}

}
