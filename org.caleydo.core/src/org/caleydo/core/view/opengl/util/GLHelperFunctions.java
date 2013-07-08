/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
