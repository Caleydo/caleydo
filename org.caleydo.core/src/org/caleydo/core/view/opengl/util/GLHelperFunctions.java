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
package org.caleydo.core.view.opengl.util;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

/**
 * Class contains GL2 commands for rendering GL2 objects of common interest (like axis, etc.)
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class GLHelperFunctions {

	public static void drawSmallPointAt(final GL2 gl, float x, float y, float z) {

		gl.glColor4f(1, 0, 0, 1);
		gl.glLineWidth(0.5f);
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(x - 0.5f, y, z);
		gl.glVertex3f(x + 0.5f, y, z);
		gl.glVertex3f(x, y - 0.5f, z);
		gl.glVertex3f(x, y + 0.5f, z);
		gl.glEnd();

	}
}
