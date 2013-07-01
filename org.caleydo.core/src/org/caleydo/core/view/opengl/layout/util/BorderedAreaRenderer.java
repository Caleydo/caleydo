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


import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.canvas.AGLView;

/**
 * Renders a bordered area.
 * 
 * @author Christian Partl
 * @author Alexander Lex
 */
public class BorderedAreaRenderer extends APickableLayoutRenderer {

	public final static Color DEFAULT_COLOR = Color.GRAY;

	private Color color = DEFAULT_COLOR;

	public BorderedAreaRenderer() {
		super();
	}

	public BorderedAreaRenderer(AGLView view, String pickingType, int id) {
		super(view, pickingType, id);
	}

	public BorderedAreaRenderer(AGLView view, List<Pair<String, Integer>> pickingIDs) {
		super(view, pickingIDs);
	}

	/**
	 * @param color
	 *            Color with rgba values.
	 */
	public void setColor(Color color) {
		this.color = color;
		setDisplayListDirty(true);
	}

	public Color getColor() {
		return color;
	}

	@Override
	protected void renderContent(GL2 gl) {
		gl.glColor3fv(color.getRGB(), 0);
		pushNames(gl);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(x, 0, 0);
		gl.glVertex3f(x, y, 0);
		gl.glVertex3f(0, y, 0);
		gl.glEnd();



		gl.glPushAttrib(GL2.GL_LINE_BIT);
		gl.glLineWidth(2);

		// gl.glColor3f(0.3f, 0.3f, 0.3f);
		// gl.glColor3f(color[0] - 0.2f, color[1] - 0.2f, color[2] - 0.2f);
		gl.glColor3fv(color.darker().getRGBA(), 0);

		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(x, 0, 0);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(0, y, 0);
		gl.glVertex3f(0, y, 0);
		gl.glVertex3f(x, y, 0);
		gl.glVertex3f(x, 0, 0);
		gl.glVertex3f(x, y, 0);

		gl.glEnd();

		gl.glPopAttrib();
		popNames(gl);
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}

}
