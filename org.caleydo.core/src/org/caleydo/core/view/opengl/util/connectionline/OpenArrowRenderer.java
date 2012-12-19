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
/**
 *
 */
package org.caleydo.core.view.opengl.util.connectionline;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;

/**
 * Renderer of an open arrow with no baseline.
 *
 * @author Christian
 *
 */
public class OpenArrowRenderer extends AArrowRenderer {

	/**
	 * @param pixelGLConverter
	 */
	public OpenArrowRenderer(PixelGLConverter pixelGLConverter) {
		super(pixelGLConverter);
	}

	@Override
	protected void render(GL2 gl, Vec3f arrowHead, Vec3f corner1, Vec3f corner2) {

		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT | GL2.GL_LINE_BIT);
		gl.glColor4fv(lineColor, 0);
		gl.glLineWidth(lineWidth);
		gl.glBegin(GL.GL_LINE_STRIP);
//		gl.glVertex3f(arrowHead.x(), arrowHead.y(), arrowHead.z());
		gl.glVertex3f(corner1.x(), corner1.y(), corner1.z());
		gl.glVertex3f(arrowHead.x(), arrowHead.y(), arrowHead.z());
		gl.glVertex3f(corner2.x(), corner2.y(), corner2.z());
		gl.glEnd();
		gl.glPopAttrib();

	}

}
