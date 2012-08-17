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
package org.caleydo.view.tagclouds;

import javax.media.opengl.GL2;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;

public class DimensionCaptionRenderer extends LayoutRenderer {
	private CaleydoTextRenderer textRenderer;
	private String text = "";

	private float color[] = { 44f / 256, 162f / 256, 95f / 256 };

	public DimensionCaptionRenderer(CaleydoTextRenderer textRenderer, String text) {
		this.textRenderer = textRenderer;
		this.text = text;
	}

	@Override
	public void renderContent(GL2 gl) {

		float sideSpacing = 0.1f;
		float topSpacing = 0.03f;

		gl.glColor3f(0.9f, 0.9f, 0.9f);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3f(0, 0, -.001f);
		gl.glVertex3f(0, y, -.001f);
		gl.glColor3f(0.7f, 0.7f, 0.7f);
		gl.glVertex3f(x, y, -.001f);
		gl.glVertex3f(x, 0, -.001f);
		gl.glEnd();

		textRenderer.setColor(color);

		textRenderer.renderTextInBounds(gl, text, sideSpacing, topSpacing / 2, 0, x
				- sideSpacing, y - topSpacing);

	};
	
	@Override
	protected boolean permitsDisplayLists() {
		return false;
	}

}
