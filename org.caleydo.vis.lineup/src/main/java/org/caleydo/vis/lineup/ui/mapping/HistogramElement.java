/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.ui.mapping;

import static org.caleydo.vis.lineup.ui.RenderStyle.LABEL_HEIGHT;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.format.Formatter;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementParent;
import org.caleydo.vis.lineup.model.SimpleHistogram;
import org.caleydo.vis.lineup.ui.RenderUtils;

class HistogramElement extends GLElement {
	private final Color color;
	private final Color backgroundColor;
	private final String text;

	public HistogramElement(String label, Color color, Color backgroundColor) {
		this.color = color;
		this.backgroundColor = backgroundColor;
		this.text = label;
	}

	protected void render(GLGraphics g, boolean vertical, double min, double max, float w, float h, double minM,
			double maxM, SimpleHistogram hist, boolean textBelowHist) {

		float histy = textBelowHist ? 0 : LABEL_HEIGHT;

		if (vertical) {
			g.save();
			g.asAdvanced().rotate(-90);
			g.move(-h, histy);
			float tmp = w;
			w = h;
			h = tmp;
			g.color(backgroundColor).fillRect(0, 0, w, h - LABEL_HEIGHT);
			g.color(color).drawRect(0, 0, w, h - LABEL_HEIGHT);
			float texty = textBelowHist ? (h - LABEL_HEIGHT) : -LABEL_HEIGHT;
			g.drawText(text, 0, texty, w, LABEL_HEIGHT - 5,
					VAlign.CENTER);
			g.drawText(Formatter.formatNumber(min), 0, texty + 3, w, 13, VAlign.LEFT);
			g.drawText(Formatter.formatNumber(max), 0, texty + 3, w, 13, VAlign.RIGHT);
			RenderUtils.renderHist(g, hist, w, h - LABEL_HEIGHT, -1, color, Color.BLACK, findRenderInfo());

			if (!Double.isNaN(minM)) {
				g.color(0, 0, 0, 0.25f).fillRect(0, 0, (float) minM * w, h - LABEL_HEIGHT);
			}
			if (!Double.isNaN(maxM)) {
				g.color(0, 0, 0, 0.25f).fillRect((float) maxM * w, 0, (float) (1 - maxM) * w, h - LABEL_HEIGHT);
			}
			g.restore();
		} else {
			float texty = textBelowHist ? (h - LABEL_HEIGHT) : 0;
			g.drawText(text, 0, texty, w, LABEL_HEIGHT - 5, VAlign.CENTER);
			g.drawText(Formatter.formatNumber(min), 0, texty + 3, w, 13, VAlign.LEFT);
			g.drawText(Formatter.formatNumber(max), 0, texty + 3, w, 13, VAlign.RIGHT);
			g.color(backgroundColor).fillRect(0, histy, w, h - LABEL_HEIGHT);
			g.color(color).drawRect(0, histy, w, h - LABEL_HEIGHT);
			g.move(0, histy);
			RenderUtils.renderHist(g, hist, w, h - LABEL_HEIGHT, -1, color, Color.BLACK, findRenderInfo());

			if (!Double.isNaN(minM)) {
				g.color(0, 0, 0, 0.25f).fillRect(0, 0, (float) minM * w, h - LABEL_HEIGHT);
			}
			if (!Double.isNaN(maxM)) {
				g.color(0, 0, 0, 0.25f).fillRect((float) maxM * w, 0, (float) (1 - maxM) * w, h - LABEL_HEIGHT);
			}
			g.move(0, -histy);
		}
	}

	/**
	 * @return
	 */
	private Color findRenderInfo() {
		IGLElementParent p = getParent();
		while (!(p instanceof IHasUIConfig) && p != null)
			p = p.getParent();
		if (p == null)
			return null;
		return ((IHasUIConfig) p).getConfig().getBarOutlineColor();
	}
}
