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
package org.caleydo.view.subgraph;

import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.picking.Pick;

/**
 * Renders a pickable background behind pathway elements.
 *
 * @author Christian Partl
 *
 */
public class GLPathwayBackground extends PickableGLElement {

	protected boolean hovered = false;

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		super.renderImpl(g, w, h);

		if (hovered)
			g.color(0, 0, 0, 1).drawRoundecRect(0, 0, w, h, 10);

	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		// g.incZ(-1f);
		super.renderPickImpl(g, w, h);
		// g.incZ(1f);
	}

	@Override
	protected void onMouseOver(Pick pick) {
		hovered = true;
		repaint();
		repaintPick();
	}

	@Override
	protected void onMouseOut(Pick pick) {
		hovered = false;
		repaint();
		repaintPick();
	}
}
