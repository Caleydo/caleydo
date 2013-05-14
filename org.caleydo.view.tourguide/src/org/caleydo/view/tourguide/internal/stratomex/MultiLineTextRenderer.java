package org.caleydo.view.tourguide.internal.stratomex;

import java.util.List;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.util.text.TextUtils;

public final class MultiLineTextRenderer implements IGLRenderer {
	private static int LINE_HEIGHT = 15;

	private final String text;
	private List<String> lines;

	public MultiLineTextRenderer(String text) {
		this.text = text;
	}

	@Override
	public void render(GLGraphics g, float w, float h, GLElement parent) {
		if (lines == null)
			lines = TextUtils.wrap(g.text, text, w, LINE_HEIGHT);
		g.drawText(lines, 0, (h - LINE_HEIGHT * lines.size()) * 0.5f, w, LINE_HEIGHT * lines.size(), 0, VAlign.CENTER);
	}
}