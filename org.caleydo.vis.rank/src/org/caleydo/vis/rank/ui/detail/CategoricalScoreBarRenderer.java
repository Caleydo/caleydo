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
package org.caleydo.vis.rank.ui.detail;

import java.awt.Color;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.vis.rank.model.CategoricalRankRankColumnModel;
import org.caleydo.vis.rank.model.IRow;

/**
 * 
 * 
 * @author Samuel Gratzl
 * 
 */
public class CategoricalScoreBarRenderer implements IGLRenderer {
	private final CategoricalRankRankColumnModel<?> model;

	public CategoricalScoreBarRenderer(CategoricalRankRankColumnModel<?> model) {
		this.model = model;
	}

	@Override
	public void render(GLGraphics g, float w, float h, GLElement parent) {
		final IRow r = parent.getLayoutDataAs(IRow.class, null); // current row
		float v = model.applyPrimitive(r);
		boolean inferred = model.isValueInferred(r);
		Color color = model.getColor(r);
		ScoreBarRenderer.renderValue(g, w, h, parent, r, v, inferred, model, false, color, color);
	}
}