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
package org.caleydo.view.stratomex.tourguide;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.layout.util.ColorRenderer;

public class BrickHighlightRenderer extends ColorRenderer {

	public BrickHighlightRenderer(float[] color) {
		super(color);
	}

	@Override
	public void renderContent(GL2 gl) {
		float xoffset = x * 0.04f;
		float yoffset = y * 0.04f;
		gl.glColor4f(color[0], color[1], color[2], 0.75f);
		gl.glPushAttrib(GL2.GL_LINE_BIT);
		gl.glLineWidth(3);
		gl.glEnable(GL.GL_LINE_SMOOTH);
		gl.glBegin(GL.GL_LINE_LOOP);
		{
			gl.glVertex3f(-xoffset, -yoffset, 0);
			gl.glVertex3f(x + xoffset, -yoffset, 0);
			gl.glVertex3f(x + xoffset, y + yoffset, 0);
			gl.glVertex3f(-xoffset, y + yoffset, 0);
		}
		gl.glEnd();
		gl.glPopAttrib();
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}

}
