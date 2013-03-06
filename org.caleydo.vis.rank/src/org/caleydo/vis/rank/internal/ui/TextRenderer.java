package org.caleydo.vis.rank.internal.ui;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.vis.rank.model.ACompositeRankColumnModel;
import org.caleydo.vis.rank.model.ARankColumnModel;


public class TextRenderer implements IGLRenderer {
	private final String prefix;
	private final ACompositeRankColumnModel model;


	public TextRenderer(String prefix, ACompositeRankColumnModel model) {
		this.prefix = prefix;
		this.model = model;
	}

	@Override
	public void render(GLGraphics g, float w, float h, GLElement parent) {
		g.drawText(toString(), 0, 0, w, h, VAlign.LEFT);
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder(prefix + " ");
		for (ARankColumnModel r : model) {
			b.append(r.getHeaderRenderer().toString()).append(", ");
		}
		b.setLength(b.length() - 2);
		return b.toString();
	}
}