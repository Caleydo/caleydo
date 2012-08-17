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
package org.caleydo.core.view.opengl.layout.util;

import javax.media.opengl.GL2;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;

public class LineSeparatorRenderer extends LayoutRenderer {

	private boolean isVertical;

	public LineSeparatorRenderer(boolean isVertical) {
		this.isVertical = isVertical;
	}

	@Override
	protected void renderContent(GL2 gl) {
		gl.glColor3f(0.3f, 0.3f, 0.3f);
		gl.glLineWidth(1);
		gl.glBegin(GL2.GL_LINES);

		if (isVertical) {
			gl.glVertex3f(x / 2.0f, 0, 0);
			gl.glVertex3f(x / 2.0f, y, 0);

		} else {
			gl.glVertex3f(0, y / 2.0f, 0);
			gl.glVertex3f(x, y / 2.0f, 0);
		}

		gl.glEnd();

	}

	@Override
	protected boolean permitsDisplayLists() {
		return true;
	}
}
