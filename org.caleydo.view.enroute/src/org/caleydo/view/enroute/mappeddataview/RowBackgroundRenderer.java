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
package org.caleydo.view.enroute.mappeddataview;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.layout.ALayoutRenderer;

/**
 * Renders the background of a row
 * 
 * @author Alexander Lex
 * 
 */
public class RowBackgroundRenderer extends ALayoutRenderer {

	private float[] backgroundColor;

	// private float[] frameColor = { 0, 0, 0, 1 };

	/**
	 * 
	 */
	public RowBackgroundRenderer(float[] backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	@Override
	public void renderContent(GL2 gl) {

		float backgroundZ = 0;
		float frameZ = 0.3f;

		gl.glColor4fv(backgroundColor, 0);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(0, 0, backgroundZ);
		gl.glVertex3f(0, y, backgroundZ);
		gl.glColor3f(backgroundColor[0] + 0.1f, backgroundColor[0] + 0.1f,
				backgroundColor[0] + 0.1f);
		gl.glVertex3f(x, y, backgroundZ);
		gl.glVertex3f(x, 0, backgroundZ);
		gl.glEnd();

		gl.glLineWidth(1);
		gl.glColor4fv(MappedDataRenderer.FRAME_COLOR, 0);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(0, 0, frameZ);
		gl.glVertex3f(0, y, frameZ);
		gl.glVertex3f(x, y, frameZ);
		gl.glVertex3f(x, 0, frameZ);
		gl.glEnd();

	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return true;
	}

}
