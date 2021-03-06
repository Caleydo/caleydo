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
package org.caleydo.view.entourage;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.util.GLElementWindow;
import org.caleydo.core.view.opengl.picking.Pick;

/**
 * Renders a pickable background behind pathway elements.
 *
 * @author Christian Partl
 *
 */
public class GLPathwayBackground extends PickableGLElement {

	public static final Color DEFAULT_COLOR = new Color(0.95f, 0.95f, 0.95f, 1f);
	// protected boolean hovered = false;
	protected final GLElementWindow parentWindow;
	protected Color color = DEFAULT_COLOR;

	public GLPathwayBackground(GLElementWindow parentWindow) {
		this.parentWindow = parentWindow;
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		super.renderImpl(g, w, h);

		// if (hovered) {
		// g.color(new Color(255, 234, 183));
		// } else {
		//
		// }
		g.color(color);
		g.incZ(-0.2f);
		g.fillRoundedRect(0, 0, w, h, 7);
		g.incZ(0.2f);

	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		g.incZ(-0.2f);
		super.renderPickImpl(g, w, h);
		g.incZ(0.2f);
	}

	@Override
	protected void onMouseOver(Pick pick) {
		// parentWindow.setActive(true);
		// GLPathwayBackground currentActiveBackground = view.getCurrentActiveBackground();
		//
		// if (currentActiveBackground != null && currentActiveBackground != this) {
		// currentActiveBackground.hovered = false;
		// // currentActiveBackground.bar.setVisibility(EVisibility.NONE);
		// currentActiveBackground.repaint();
		// }
		// view.setCurrentActiveBackground(this);
		// // bar.setVisibility(EVisibility.VISIBLE);
		// hovered = true;
		// repaint();
	}

	/**
	 * @param color
	 *            setter, see {@link color}
	 */
	public void setColor(Color color) {
		this.color = color;
		repaint();
	}

	// /**
	// * @param hovered
	// * setter, see {@link hovered}
	// */
	// public void setHovered(boolean hovered) {
	// if (hovered != this.hovered) {
	// this.hovered = hovered;
	// repaint();
	// }
	// }

}
