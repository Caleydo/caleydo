/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.ui.detail;


import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.ui.RenderStyle;

/**
 * @author Samuel Gratzl
 *
 */
public class ColoredValueElement extends ValueElement {
	private int rankDelta;
	private int remaining;
	private int duration;

	@Override
	public void setRow(IRow row) {
		super.setRow(row);
		duration = 0;
		remaining = 0;
	}
	/**
	 * @param rankDelta
	 *            setter, see {@link rankDelta}
	 */
	public void setRankDelta(int rankDelta) {
		this.rankDelta = rankDelta;
		if (rankDelta != 0 && rankDelta != Integer.MAX_VALUE) {
			this.duration = RenderStyle.hightlightAnimationDuration(rankDelta);
			this.remaining = duration;
			repaint();
		}
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		if (remaining > 0) {
			remaining -= g.getDeltaTimeMs();
			if (remaining < 0)
				remaining = 0;
			float alpha = 1 - (remaining / (float) duration);

			float calpha = RenderStyle.computeHighlightAlpha(alpha, rankDelta);
			Color base = rankDelta < 0 ? Color.GREEN : Color.RED;
			Color c = new Color(base.r, base.g, base.b, calpha);
			g.decZ();
			g.color(c);
			g.fillRect(0, 0, w, h);
			g.incZ();
			repaint();
		}
		super.renderImpl(g, w, h);
	}
}
