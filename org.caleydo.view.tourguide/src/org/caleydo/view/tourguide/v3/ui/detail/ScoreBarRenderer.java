package org.caleydo.view.tourguide.v3.ui.detail;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.view.tourguide.v3.model.IRow;
import org.caleydo.view.tourguide.v3.model.mixin.IRankableColumnMixin;

public class ScoreBarRenderer implements IGLRenderer {
	private final IRankableColumnMixin model;

	public ScoreBarRenderer(IRankableColumnMixin model) {
		this.model = model;
	}

	@Override
	public void render(GLGraphics g, float w, float h, GLElement parent) {
		float v = model.getValue(parent.getLayoutDataAs(IRow.class, null));
		if (Float.isNaN(v))
			return;
		if (w < 20) {
			float[] c = model.getColor().getColorComponents(null);
			g.color(c[0], c[1], c[2], v).fillRect(w * 0.1f, h * 0.1f, w * 0.8f, h * 0.8f);
		} else {

			g.color(model.getColor()).fillRect(0, h * 0.1f, w * v, h * 0.8f);
		}
	}
}