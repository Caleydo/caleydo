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

import javax.media.opengl.GL2;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.canvas.AGLView;

/**
 * Renders a bordered area.
 * 
 * @author Christian Partl
 */
public class BorderedAreaRenderer extends APickableLayoutRenderer {

	public final static float[] DEFAULT_COLOR = { 0.5f, 0.5f, 0.5f, 1.0f };

	private float[] color;

	public BorderedAreaRenderer() {
		super();
		color = DEFAULT_COLOR;
	}

	public BorderedAreaRenderer(AGLView view, String pickingType, int id) {
		super(view, pickingType, id);
		color = DEFAULT_COLOR;
	}

	public BorderedAreaRenderer(AGLView view, List<Pair<String, Integer>> pickingIDs) {
		super(view, pickingIDs);
		color = DEFAULT_COLOR;

	}

	/**
	 * @param color
	 *            Color with rgba values.
	 */
	public void setColor(float[] color) {
		this.color = color;
		setDisplayListDirty();
	}

	public float[] getColor() {
		return color;
	}

	@Override
	protected void renderContent(GL2 gl) {
		pushNames(gl);
		gl.glColor4f(color[0] - 0.15f, color[1] - 0.15f, color[2] - 0.15f, color[3]);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(0, 0, 0);
		gl.glColor4f(color[0], color[1], color[2], color[3]);
		gl.glVertex3f(x, 0, 0);
		gl.glColor4f(color[0] + 0.15f, color[1] + 0.15f, color[2] + 0.15f, color[3]);
		gl.glVertex3f(x, y, 0);
		gl.glColor4f(color[0], color[1], color[2], color[3]);
		gl.glVertex3f(0, y, 0);
		gl.glEnd();

		// SelectionManager selectionManager = brick
		// .getContentGroupSelectionManager();
		//
		// gl.glColor3f(0.35f, 0.35f, 0.35f);
		// gl.glBegin(GL2.GL_QUADS);
		// gl.glVertex3f(0, 0, 0);
		// gl.glColor3f(0.5f, 0.5f, 0.5f);
		// gl.glVertex3f(x, 0, 0);
		// gl.glColor3f(0.65f, 0.65f, 0.65f);
		// gl.glVertex3f(x, y, 0);
		// gl.glColor3f(0.5f, 0.5f, 0.5f);
		// gl.glVertex3f(0, y, 0);
		// gl.glEnd();
		//
		// if (selectionManager.checkStatus(SelectionType.SELECTION, brick
		// .getGroup().getID())) {
		// float[] baseColor = SelectionType.SELECTION.getColor();
		//
		// gl.glColor3f(baseColor[0] + 0.3f, baseColor[1] + 0.3f,
		// baseColor[2] + 0.3f);
		//
		// gl.glColor4f(baseColor[0] - 0.15f, baseColor[1]- 0.15f, baseColor[2]-
		// 0.15f, 0.4f);
		// gl.glBegin(GL2.GL_QUADS);
		// gl.glVertex3f(0, 0, 0);
		// gl.glColor4f(baseColor[0], baseColor[1], baseColor[2], 0.4f);
		// gl.glVertex3f(x, 0, 0);
		// gl.glColor4f(baseColor[0] + 0.15f, baseColor[1]+ 0.15f, baseColor[2]+
		// 0.15f, 0.4f);
		// gl.glVertex3f(x, y, 0);
		// gl.glColor4f(baseColor[0], baseColor[1], baseColor[2], 0.4f);
		// gl.glVertex3f(0, y, 0);
		// gl.glEnd();
		// }

		gl.glPushAttrib(GL2.GL_LINE_BIT);
		gl.glLineWidth(2);

		// gl.glColor3f(0.3f, 0.3f, 0.3f);
		gl.glColor3f(color[0] - 0.2f, color[1] - 0.2f, color[2] - 0.2f);
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(x, 0, 0);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(0, y, 0);

		// gl.glColor3f(0.7f, 0.7f, 0.7f);
		gl.glColor3f(color[0] + 0.2f, color[1] + 0.2f, color[2] + 0.2f);
		gl.glVertex3f(0, y, 0);
		gl.glVertex3f(x, y, 0);
		gl.glVertex3f(x, 0, 0);
		gl.glVertex3f(x, y, 0);

		gl.glEnd();

		gl.glPopAttrib();
		popNames(gl);
	}

	@Override
	protected boolean permitsDisplayLists() {
		return false;
	}

}
