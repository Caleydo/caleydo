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

import gleem.linalg.Vec3f;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Class contains GL2 commands for rendering GL2 objects of common interest (like axis, etc.)
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class GLHelperFunctions {

	public static void drawAxis(final GL2 gl) {

		gl.glLineWidth(3);
		gl.glBegin(GL2.GL_LINES);
		gl.glColor4f(1, 0, 0, 1);
		gl.glVertex3f(-1, 0, 0);
		gl.glVertex3f(1, 0, 0);
		gl.glColor4f(0, 1, 0, 1);
		gl.glVertex3f(0, -1, 0);
		gl.glVertex3f(0, 1, 0);
		gl.glColor4f(0, 0, 1, 1);
		gl.glVertex3f(0, 0, -1);
		gl.glVertex3f(0, 0, 1);
		gl.glEnd();
	}

	public static void drawViewFrustum(final GL2 gl, final ViewFrustum viewFrustum) {

		gl.glColor3f(1, 0, 0);
		gl.glBegin(GL2.GL_LINE_LOOP);
		gl.glVertex3f(viewFrustum.getLeft(), viewFrustum.getBottom(), 1);
		gl.glVertex3f(viewFrustum.getRight() - viewFrustum.getLeft(), viewFrustum.getBottom(), 1);
		gl.glVertex3f(viewFrustum.getRight() - viewFrustum.getLeft(),
			viewFrustum.getTop() - viewFrustum.getBottom(), 1);
		gl.glVertex3f(viewFrustum.getLeft(), viewFrustum.getTop() - viewFrustum.getBottom(), 1);

		gl.glEnd();
	}

	public static void drawPointAt(final GL2 gl, final Vec3f vecPoint) {

		gl.glColor4f(1, 0, 0, 1);
		gl.glLineWidth(3);
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3f(vecPoint.x() - 10, vecPoint.y(), vecPoint.z());
		gl.glVertex3f(vecPoint.x() + 10, vecPoint.y(), vecPoint.z());
		gl.glVertex3f(vecPoint.x(), vecPoint.y() - 10, vecPoint.z());
		gl.glVertex3f(vecPoint.x(), vecPoint.y() + 10, vecPoint.z());
		gl.glVertex3f(vecPoint.x(), vecPoint.y(), vecPoint.z() - 10);
		gl.glVertex3f(vecPoint.x(), vecPoint.y(), vecPoint.z() + 10);
		gl.glVertex3f(vecPoint.x() - 10, vecPoint.y() - 10, vecPoint.z());
		gl.glVertex3f(vecPoint.x() + 10, vecPoint.y() + 10, vecPoint.z());
		gl.glVertex3f(vecPoint.x() + 10, vecPoint.y() - 10, vecPoint.z());
		gl.glVertex3f(vecPoint.x() - 10, vecPoint.y() + 10, vecPoint.z());
		gl.glEnd();
	}

	public static void drawPointAt(final GL2 gl, float x, float y, float z) {
		drawPointAt(gl, new Vec3f(x, y, z));
	}

	public static void drawSmallPointAt(final GL2 gl, float x, float y, float z) {

		gl.glColor4f(1, 0, 0, 1);
		gl.glLineWidth(0.5f);
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3f(x - 0.5f, y, z);
		gl.glVertex3f(x + 0.5f, y, z);
		gl.glVertex3f(x, y - 0.5f, z);
		gl.glVertex3f(x, y + 0.5f, z);
		gl.glEnd();

	}
}
