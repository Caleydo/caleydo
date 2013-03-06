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
package org.caleydo.vis.rank.ui.mapping;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.caleydo.core.io.gui.dataimport.widget.ICallback;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.vis.rank.model.CategoricalRankRankColumnModel.CategoryInfo;
import org.caleydo.vis.rank.model.mapping.BaseCategoricalMappingFunction;
import org.caleydo.vis.rank.model.mapping.ICategoricalMappingFunction;
import org.caleydo.vis.rank.ui.RenderStyle;

/**
 * FIXME not implemented
 *
 * @author Samuel Gratzl
 *
 */
public class BaseCategoricalMappingFunctionUI<T> extends GLElementContainer implements IGLLayout {
	private static final float GAP = 10;
	private static final float PADDING = 5;

	private final BaseCategoricalMappingFunction<T> model;
	private final Map<T, Integer> data;
	private final List<T> order;
	private final Map<T, CategoryInfo> metaData;
	private final Color backgroundColor;
	private final ICallback<? super ICategoricalMappingFunction<?>> callback;

	public BaseCategoricalMappingFunctionUI(BaseCategoricalMappingFunction<T> model, Map<T, Integer> data,
			Map<T, CategoryInfo> metaData, Color bgColor, ICallback<? super ICategoricalMappingFunction<?>> callback) {
		this.model = model;
		this.data = data;
		this.metaData = metaData;
		this.backgroundColor = bgColor;
		this.callback = callback;
		this.order = new ArrayList<>(metaData.keySet());

		setLayout(this);

		// this.add(new RawHistogramElement());
		// this.add(new NormalizedHistogramElement());
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.color(backgroundColor).fillRect(0, 0, w, h);
		float y = 0;
		float hi = 14;
		for (CategoryInfo info : metaData.values()) {
			g.color(info.getColor()).fillRect(2, y + 1, 10, 10);
			g.drawText(info.getLabel(), 14, y, w - 14 - RenderStyle.HIST_HEIGHT * 2, hi);
			y += hi + 2;
		}
		// TODO idea:
		// left a list of the elements with color box and label
		// right a vertical histogram
		// lines between with points on the right
		// + text fields for editing the values (pickable)
		// at the right bottom a special area to map to NaN
		//
		super.renderImpl(g, w, h);
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		// TODO Auto-generated method stub

	}


}
