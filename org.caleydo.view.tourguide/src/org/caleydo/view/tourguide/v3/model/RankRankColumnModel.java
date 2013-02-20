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
package org.caleydo.view.tourguide.v3.model;

import java.awt.Color;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
/**
 * @author Samuel Gratzl
 *
 */
public class RankRankColumnModel extends ARankColumnModel {

	public RankRankColumnModel() {
		super(Color.GRAY, Color.GRAY);
		setHeaderRenderer(GLRenderers.drawText("Rank", VAlign.CENTER));
		setValueRenderer(new IGLRenderer() {
			@Override
			public void render(GLGraphics g, float w, float h, GLElement parent) {
				if (h < 5 || w < 15)
					return;
				String value = String.format("%2d.", parent.getLayoutDataAs(IRow.class, null).getRank() + 1);
				g.drawText(value, 1, 1, w - 2, h - 2);
			}
		});
		setWeight(20);
	}

	@Override
	public GLElement createSummary() {
		return new GLElement(); // dummy
	}
}
