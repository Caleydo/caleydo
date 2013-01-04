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

import org.caleydo.core.util.color.Colors;
import org.caleydo.core.util.color.IColor;
import org.caleydo.core.view.opengl.canvas.AGLView;

/**
 * Simple renderer for a colored rectangle exactly of the size of the layout.
 *
 * @author Christian Partl
 */
public class PickingRenderer extends APickableLayoutRenderer {
	private IColor color;
	private float z = -0.01f;

	public PickingRenderer(String pickingType, int pickingID, AGLView view) {
		super(view, pickingType, pickingID);
		this.color = Colors.TRANSPARENT;
	}

	public PickingRenderer moveBack() {
		this.z -= 0.01f;
		return this;
	}

	/**
	 * @param color
	 *            setter, see {@link color}
	 */
	public void setColor(IColor color) {
		if (this.color.equals(color))
			return;
		this.color = color;
		setDisplayListDirty();
	}

	@Override
	protected void renderContent(GL2 gl) {
		pushNames(gl);

		gl.glBegin(GL2.GL_QUADS);
		gl.glColor4fv(this.color.getRGBA(), 0);
		gl.glVertex3f(0, 0, z);
		gl.glVertex3f(x, 0, z);
		gl.glVertex3f(x, y, z);
		gl.glVertex3f(0, y, z);
		gl.glEnd();
		popNames(gl);
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}
}
