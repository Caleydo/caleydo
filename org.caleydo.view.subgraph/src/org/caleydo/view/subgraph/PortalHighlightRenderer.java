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

import java.awt.geom.Rectangle2D;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;

/**
 * @author Christian
 *
 */
public class PortalHighlightRenderer extends GLElement {

	private final Rectangle2D location;

	/**
	 *
	 */
	public PortalHighlightRenderer(Rectangle2D location) {
		this.location = location;
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.incZ(0.5f);
		g.color(1, 0, 0, 1)
				.lineWidth(2)
				.drawRoundedRect((float) location.getX() + 1, (float) location.getY() + 1,
						(float) location.getWidth() + 1,
						(float) location.getHeight() + 1, 8);
		g.lineWidth(1);
		g.incZ(-0.5f);
	}

}
