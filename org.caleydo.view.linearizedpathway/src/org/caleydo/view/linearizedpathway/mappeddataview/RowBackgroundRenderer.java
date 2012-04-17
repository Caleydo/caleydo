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
package org.caleydo.view.linearizedpathway.mappeddataview;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.layout.LayoutRenderer;

/**
 * @author alexsb
 * 
 */
public class RowBackgroundRenderer extends LayoutRenderer {

	/**
	 * Flag telling whether the row of this <code>RowBackgroundRenderer</code>
	 * is at an even (true, default) or at an odd position (false)
	 */
	private boolean isEven;
	float[] backgroundColor;
	// float[] oddColor = { 236f/255f, 231f/255, 242f/255, 1f };
	float[] oddColor = { 43f / 255f, 140f / 255, 190f / 255, 1f };
	float[] evenColor = { 166f / 255f, 189f / 255, 219f / 255, 1f };
	float[] frameColor = { 0, 0, 0, 1 };

	@Override
	public void render(GL2 gl) {
		
		float backgroundZ = 0;
		float frameZ = 0.3f;
		
		gl.glColor4fv(backgroundColor, 0);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(0, 0, backgroundZ);
		gl.glVertex3f(0, y, backgroundZ);
		gl.glVertex3f(x, y, backgroundZ);
		gl.glVertex3f(x, 0, backgroundZ);
		gl.glEnd();

		gl.glColor4fv(frameColor, 0);
		gl.glBegin(GL2.GL_LINE_LOOP);
		gl.glVertex3f(0, 0, frameZ);
		gl.glVertex3f(0, y, frameZ);
		gl.glVertex3f(x, y, frameZ);
		gl.glVertex3f(x, 0, frameZ);
		gl.glEnd();

	}

	/**
	 * @param isEven
	 *            setter, see {@link #isEven}
	 */
	public void setEven(boolean isEven) {
		this.isEven = isEven;
		if (isEven)
			backgroundColor = evenColor;
		else
			backgroundColor = oddColor;
	}
}
