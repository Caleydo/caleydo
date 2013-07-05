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


import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.function.IFloatList;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.basic.GLSlider;
import org.caleydo.core.view.opengl.layout2.basic.GLSlider.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.layout.GLFlowLayout;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.vis.rank.model.StackedRankColumnModel;
import org.caleydo.vis.rank.ui.RenderStyle;

/**
 * @author Samuel Gratzl
 *
 */
public class ScoreFilter2 extends GLElementContainer implements ISelectionCallback {
	protected final StackedRankColumnModel model;

	/**
	 *
	 */
	public ScoreFilter2(StackedRankColumnModel model, IFloatList data, GLElement summary) {
		this.model = model;
		setLayout(new GLFlowLayout(false, 3, new GLPadding(10, 2, 10, 2)));
		GLElementContainer help = new GLElementContainer(new GLFlowLayout(false, 5, new GLPadding(5, 2)));
		help.add(new GLElement(GLRenderers.drawText("maximal Inferring Percentage:")).setSize(Float.NaN, 12));
		GLSlider slider = new GLSlider(0, 1, model.getFilterInferredPercentage());
		slider.setCallback(this);
		help.add(slider);
		add(help.setSize(Float.NaN, 40));
		add(new ScoreFilter(model, data, summary));

		setSize(Float.NaN, 40 + RenderStyle.HIST_HEIGHT * 1.5f + 10);
	}

	@Override
	public void onSelectionChanged(GLSlider slider, float value) {
		model.setFilterInferredPercentage(value);
		get(1).repaintAll();
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.color(model.getBgColor()).fillRect(0, 0, w, h);
		g.color(Color.BLACK).drawRect(9, 43, w - 18, RenderStyle.HIST_HEIGHT * 1.5f + 3);
		super.renderImpl(g, w, h);
	}
}