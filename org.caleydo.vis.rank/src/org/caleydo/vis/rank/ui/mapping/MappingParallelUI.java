/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.ui.mapping;

import static org.caleydo.vis.rank.ui.RenderStyle.HIST_HEIGHT;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.format.Formatter;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.vis.rank.model.mapping.IMappingFunction;
import org.caleydo.vis.rank.ui.RenderStyle;

/**
 * @author Samuel Gratzl
 *
 */
public class MappingParallelUI<T extends IMappingFunction> extends AMappingFunctionMode<T> {
	protected static final float GAP = 10;
	protected boolean isHorizontal;

	public MappingParallelUI(T model, boolean isHorizontal) {
		super(model);
		this.isHorizontal = isHorizontal;
	}

	@Override
	public String getIcon() {
		return isHorizontal ? RenderStyle.ICON_MAPPING_PAR_HOR : RenderStyle.ICON_MAPPING_PAR_VERT;
	}

	@Override
	public String getName() {
		return isHorizontal ? "Horizontal Bars" : "Vertical Bars";
	}

	@Override
	public void reset() {

	}

	@Override
	public void doLayout(IGLLayoutElement raw, IGLLayoutElement norm, IGLLayoutElement canvas, float x, float y,
			float w, float h) {
		final float histHeight = HIST_HEIGHT + RenderStyle.LABEL_HEIGHT;
		if (isHorizontal) {
			norm.setBounds(x + GAP, y, w - GAP * 2, histHeight);
			raw.setBounds(x + GAP, y + h - histHeight, w - GAP * 2, histHeight);
			canvas.setBounds(x + GAP, y + histHeight + GAP, w - GAP * 2, h - histHeight * 2 - GAP * 2);
		} else {
			raw.setBounds(x, y + GAP, histHeight, h - GAP * 2);
			norm.setBounds(x + w - histHeight, y + GAP, histHeight, h - GAP * 2);
			canvas.setBounds(x + histHeight + GAP, y + GAP, x + w - histHeight * 2 - GAP * 2, h - GAP * 2);
		}
		raw.asElement().setLayoutData(isHorizontal);
		norm.asElement().setLayoutData(!isHorizontal);
	}

	protected void drawHint(GLGraphics g, float w, float h, float from, float to) {
		g.textColor(Color.GRAY);
		if (isHorizontal) {
			g.drawText(Formatter.formatNumber(to), to * w + 5, -GAP - 2, 40, 11);
			g.drawText(Formatter.formatNumber(from), normalizeRaw(from) * w + 5, h - 12 + GAP, 40, 11);
		} else {
			g.drawText(Formatter.formatNumber(to), w - 1 + GAP, (1 - to) * h + 1, 40, 11);
			g.drawText(Formatter.formatNumber(from), 1 - GAP, (1 - normalizeRaw(from)) * h + 1, 40, 11);
		}
		g.textColor(Color.BLACK);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		// render all point mappings
		renderMapping(g, w, h, false, isHorizontal);

		super.renderImpl(g, w, h);
	}
}

