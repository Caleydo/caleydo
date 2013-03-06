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

import static org.caleydo.vis.rank.ui.RenderStyle.HIST_HEIGHT;
import gleem.linalg.Vec2f;

import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.vis.rank.model.mapping.IMappingFunction;
import org.caleydo.vis.rank.ui.RenderStyle;

/**
 * @author Samuel Gratzl
 *
 */
public class MappingCrossUI<T extends IMappingFunction> extends AMappingFunctionMode<T> {
	private static final float GAP = 10;

	protected final boolean isNormalLeft; // otherwise right

	public MappingCrossUI(T model, boolean isNormalLeft) {
		super(model);
		this.isNormalLeft = isNormalLeft;
	}

	@Override
	public String getIcon() {
		return isNormalLeft ? RenderStyle.ICON_MAPPING_CROSS_LEFT : RenderStyle.ICON_MAPPING_CROSS_RIGHT;
	}

	@Override
	public void reset() {

	}

	@Override
	public void doLayout(IGLLayoutElement raw, IGLLayoutElement norm, IGLLayoutElement canvas, float x, float y,
			float w, float h) {
		Vec2f rawL;
		Vec2f normL;
		Vec2f canvasL;
		if (isNormalLeft) {
			rawL = new Vec2f(x + HIST_HEIGHT + GAP, y + h - HIST_HEIGHT);
			normL = new Vec2f(x, y + GAP + x);
			canvasL = new Vec2f(HIST_HEIGHT + GAP + x, y + GAP + x);
		} else {
			rawL = new Vec2f(x, y + h - HIST_HEIGHT);
			normL = new Vec2f(w - x - HIST_HEIGHT, y + GAP);
			canvasL = new Vec2f(x, y + GAP);
		}

		raw.setBounds(rawL.x(), rawL.y(), w - HIST_HEIGHT - GAP * 2,
				HIST_HEIGHT);
		norm.setBounds(normL.x(), normL.y(), HIST_HEIGHT, h - HIST_HEIGHT - GAP * 2);

		float x_canvas = canvasL.x();
		float y_canvas = canvasL.y();
		float w_canvas = w - HIST_HEIGHT - GAP * 2;
		float h_canvas = h - HIST_HEIGHT - GAP * 2;
		canvas.setBounds(x_canvas, y_canvas, w_canvas, h_canvas);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		renderMapping(g, w, h, true, isNormalLeft);

		super.renderImpl(g, w, h);
	}
}

