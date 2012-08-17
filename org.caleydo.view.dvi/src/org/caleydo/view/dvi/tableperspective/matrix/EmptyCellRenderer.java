/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.dvi.tableperspective.matrix;

import javax.media.opengl.GL2;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;

public class EmptyCellRenderer extends ColorRenderer {

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
		// gl.glBegin(GL2.GL_LINES);
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
	protected boolean permitsDisplayLists() {
		return false;
	}

}
