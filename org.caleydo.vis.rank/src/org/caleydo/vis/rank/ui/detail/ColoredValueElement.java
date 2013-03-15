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
			Color c = new Color(base.getRed(), base.getGreen(), base.getBlue(), (int) (calpha * 255));
			g.decZ();
			g.color(c);
			g.fillRect(0, 0, w, h);
			g.incZ();
			repaint();
		}
		super.renderImpl(g, w, h);
	}
}
