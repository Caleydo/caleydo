/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.ui.detail;


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
import org.caleydo.vis.lineup.model.StackedRankColumnModel;
import org.caleydo.vis.lineup.ui.RenderStyle;

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
