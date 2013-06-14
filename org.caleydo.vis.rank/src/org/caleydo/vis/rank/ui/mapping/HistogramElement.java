package org.caleydo.vis.rank.ui.mapping;

import static org.caleydo.vis.rank.ui.RenderStyle.LABEL_HEIGHT;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.format.Formatter;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.vis.rank.model.SimpleHistogram;
import org.caleydo.vis.rank.ui.RenderUtils;

class HistogramElement extends GLElement {
	private final Color color;
	private final Color backgroundColor;
	private final String text;

	public HistogramElement(String label, Color color, Color backgroundColor) {
		this.color = color;
		this.backgroundColor = backgroundColor;
		this.text = label;
	}

	protected void render(GLGraphics g, boolean vertical, float min, float max, float w, float h, float minM,
			float maxM, SimpleHistogram hist, boolean textBelowHist) {

		float histy = textBelowHist ? 0 : LABEL_HEIGHT;

		if (vertical) {
			g.save();
			g.gl.glRotatef(-90, 0, 0, 1);
			g.move(-h, histy);
			float tmp = w;
			w = h;
			h = tmp;
			g.color(backgroundColor).fillRect(0, 0, w, h - LABEL_HEIGHT);
			g.color(color).drawRect(0, 0, w, h - LABEL_HEIGHT);
			float texty = textBelowHist ? (h - LABEL_HEIGHT) : -LABEL_HEIGHT;
			g.drawText(text, 0, texty, w, LABEL_HEIGHT - 5,
					VAlign.CENTER);
			g.drawText(Formatter.formatNumber(min), 0, texty + 3, w, 12, VAlign.LEFT);
			g.drawText(Formatter.formatNumber(max), 0, texty + 3, w, 12, VAlign.RIGHT);
			RenderUtils.renderHist(g, hist, w, h - LABEL_HEIGHT, -1, color, Color.BLACK);

			if (!Float.isNaN(minM)) {
				g.color(0, 0, 0, 0.25f).fillRect(0, 0, minM * w, h - LABEL_HEIGHT);
			}
			if (!Float.isNaN(maxM)) {
				g.color(0, 0, 0, 0.25f).fillRect(maxM * w, 0, (1 - maxM) * w, h - LABEL_HEIGHT);
			}
			g.restore();
		} else {
			float texty = textBelowHist ? (h - LABEL_HEIGHT) : 0;
			g.drawText(text, 0, texty, w, LABEL_HEIGHT - 5, VAlign.CENTER);
			g.drawText(Formatter.formatNumber(min), 0, texty + 3, w, 12, VAlign.LEFT);
			g.drawText(Formatter.formatNumber(max), 0, texty + 3, w, 12, VAlign.RIGHT);
			g.color(backgroundColor).fillRect(0, histy, w, h - LABEL_HEIGHT);
			g.color(color).drawRect(0, histy, w, h - LABEL_HEIGHT);
			g.move(0, histy);
			RenderUtils.renderHist(g, hist, w, h - LABEL_HEIGHT, -1, color, Color.BLACK);

			if (!Float.isNaN(minM)) {
				g.color(0, 0, 0, 0.25f).fillRect(0, 0, minM * w, h - LABEL_HEIGHT);
			}
			if (!Float.isNaN(maxM)) {
				g.color(0, 0, 0, 0.25f).fillRect(maxM * w, 0, (1 - maxM) * w, h - LABEL_HEIGHT);
			}
			g.move(0, -histy);
		}
	}
}