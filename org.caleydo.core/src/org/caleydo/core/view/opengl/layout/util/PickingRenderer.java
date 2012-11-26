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

import org.caleydo.core.view.opengl.canvas.AGLView;

/**
 * Simple renderer for a colored rectangle exactly of the size of the layout.
 *
 * @author Christian Partl
 */
public class PickingRenderer extends APickableLayoutRenderer {
	public PickingRenderer(AGLView view, String pickingType, int pickingID) {
		super(view, pickingType, pickingID);
	}

	@Override
	protected void renderContent(GL2 gl) {
		pushNames(gl);

		gl.glBegin(GL2.GL_QUADS);
		gl.glColor4f(1, 1, 1, 0);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(x, 0, 0);
		gl.glVertex3f(x, y, 0);
		gl.glVertex3f(0, y, 0);
		gl.glEnd();
		popNames(gl);
	}

	@Override
	protected boolean permitsDisplayLists() {
		return false;
	}
}
