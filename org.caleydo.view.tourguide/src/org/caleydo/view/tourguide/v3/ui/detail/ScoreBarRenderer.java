package org.caleydo.view.tourguide.v3.ui.detail;

import org.caleydo.core.util.format.Formatter;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.view.tourguide.v3.model.IRow;
import org.caleydo.view.tourguide.v3.model.mixin.IMappedColumnMixin;
import org.caleydo.view.tourguide.v3.model.mixin.IRankableColumnMixin;

public class ScoreBarRenderer implements IGLRenderer {
	private final IRankableColumnMixin model;

	public ScoreBarRenderer(IRankableColumnMixin model) {
		this.model = model;
	}

	@Override
	public void render(GLGraphics g, float w, float h, GLElement parent) {
		final IRow r = parent.getLayoutDataAs(IRow.class, null);
		float v = model.getValue(r);
		if (Float.isNaN(v))
			return;
		if (w < 20) {
			float[] c = model.getColor().getColorComponents(null);
			g.color(c[0], c[1], c[2], v).fillRect(w * 0.1f, h * 0.1f, w * 0.8f, h * 0.8f);
		} else {
			g.color(model.getColor()).fillRect(0, h * 0.1f, w * v, h * 0.8f);
			if (model.getTable().getSelectedRow() == r) {
				String text = (model instanceof IMappedColumnMixin) ? ((IMappedColumnMixin) model).getRawValue(r)
						: Formatter.formatNumber(v);
				renderLabel(g, h * 0.2f, w, h * 0.45f, text, v);
			}
		}
	}

	static void renderLabel(GLGraphics g, float y, float w, float h, String text, float v) {
		if (h < 7)
			return;
		float rw = g.text.getRequiredTextWidthWithMax(text, h, w * v);
		if (rw < w * v)
			g.drawText(text, 1, y, w * v - 2, h, VAlign.RIGHT);
		else
			g.drawText(text, w * v + 1, y, w - w * v, h);
	}
}