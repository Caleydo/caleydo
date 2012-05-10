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
package org.caleydo.core.view.opengl.renderstyle.border;

import javax.media.opengl.GL2;

public class BorderRenderStyleLineSolid
	extends BorderRenderStyle {
	private float fHeight = 1.0f;
	private float fWidth = 1.0f;

	@Override
	public void init(GL2 gl) {
		if (glList >= 0) {
			gl.glDeleteLists(glList, 1);
		}

		glList = gl.glGenLists(1);
		gl.glNewList(glList, GL2.GL_COMPILE);
		draw(gl);
		gl.glEndList();
	}

	@Override
	public void display(GL2 gl) {
		if (glList < 0) {
			draw(gl);
		}
		else {
			gl.glCallList(glList);
		}
	}

	private void draw(GL2 gl) {
		gl.glPushMatrix();
		gl.glLineWidth(iBorderWidth);

		if (bBorderLeft) {
			gl.glBegin(GL2.GL_LINES);
			gl.glColor4f(vBorderColor.get(0), vBorderColor.get(1), vBorderColor.get(2), vBorderColor.get(3));
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(0, fHeight, 0);
			gl.glEnd();
		}

		gl.glTranslatef(0f, fHeight, 0f);

		if (bBorderTop) {
			gl.glBegin(GL2.GL_LINES);
			gl.glColor4f(vBorderColor.get(0), vBorderColor.get(1), vBorderColor.get(2), vBorderColor.get(3));
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(fWidth, 0, 0);
			gl.glEnd();
		}

		gl.glTranslatef(fWidth, 0f, 0f);

		if (bBorderRight) {
			gl.glBegin(GL2.GL_LINES);
			gl.glColor4f(vBorderColor.get(0), vBorderColor.get(1), vBorderColor.get(2), vBorderColor.get(3));
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(0, -fHeight, 0);
			gl.glEnd();
		}

		gl.glTranslatef(0f, -fHeight, 0f);

		if (bBorderBottom) {
			gl.glBegin(GL2.GL_LINES);
			gl.glColor4f(vBorderColor.get(0), vBorderColor.get(1), vBorderColor.get(2), vBorderColor.get(3));
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(-fWidth, 0, 0);
			gl.glEnd();
		}

		gl.glLineWidth(1);
		gl.glPopMatrix();
	}

}
