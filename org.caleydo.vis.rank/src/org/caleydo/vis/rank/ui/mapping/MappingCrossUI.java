/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
	public String getName() {
		return isNormalLeft ? "Function" : "Mirrored Function";
	}

	@Override
	public void reset() {

	}

	@Override
	public void doLayout(IGLLayoutElement raw, IGLLayoutElement norm, IGLLayoutElement canvas, float x, float y,
			float w, float h) {
		final float histHeight = HIST_HEIGHT + RenderStyle.LABEL_HEIGHT;
		Vec2f rawL;
		Vec2f normL;
		Vec2f canvasL;
		if (isNormalLeft) {
			rawL = new Vec2f(x + histHeight + GAP, y + h - histHeight);
			normL = new Vec2f(x, y + GAP);
			canvasL = new Vec2f(histHeight + GAP + x, y + GAP);
		} else {
			rawL = new Vec2f(x, y + h - histHeight);
			normL = new Vec2f(w - x - histHeight, y + GAP);
			canvasL = new Vec2f(x, y + GAP);
		}

		raw.setBounds(rawL.x(), rawL.y(), w - histHeight - GAP * 2, histHeight);
		raw.asElement().setLayoutData(Boolean.TRUE);
		norm.setBounds(normL.x(), normL.y(), histHeight, h - histHeight - GAP * 2);
		norm.asElement().setLayoutData(!isNormalLeft);
		float x_canvas = canvasL.x();
		float y_canvas = canvasL.y();
		float w_canvas = w - histHeight - GAP * 2;
		float h_canvas = h - histHeight - GAP * 2;
		canvas.setBounds(x_canvas, y_canvas, w_canvas, h_canvas);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		renderMapping(g, w, h, true, isNormalLeft);

		super.renderImpl(g, w, h);
	}
}

