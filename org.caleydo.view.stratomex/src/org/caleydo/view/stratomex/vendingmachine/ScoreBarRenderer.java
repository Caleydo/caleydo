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
package org.caleydo.view.stratomex.vendingmachine;

import javax.media.opengl.GL2;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;

public class ScoreBarRenderer
	extends LayoutRenderer {
	
	private float score;
	
	private Color color;
	
	public ScoreBarRenderer(float score, Color color) {
		this.score = score;
		this.color = color;
	}

	@Override
	public void renderContent(GL2 gl) {

		float padding = 0.015f;
		float barWidth = x * score;

		gl.glColor4fv(color.getRGBA(), 0);
		
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3f(0+padding, 0+padding, 0);
		gl.glVertex3f(barWidth-padding, 0+padding, 0);
		gl.glVertex3f(barWidth-padding, y-padding, 0);
		gl.glVertex3f(0+padding, y-padding, 0);
		gl.glEnd();
		
		gl.glLineWidth(1);
		gl.glColor4f(0.3f, 0.3f, 0.3f, 1);
		
		gl.glBegin(GL2.GL_LINE_LOOP);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(x, 0, 0);
		gl.glVertex3f(x, y, 0);
		gl.glVertex3f(0, y, 0);
		gl.glEnd();
	}

	@Override
	protected boolean permitsDisplayLists() {
		return true;
	}
}
