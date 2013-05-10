package org.caleydo.view.tourguide.api.state;

import java.util.Arrays;
import java.util.List;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;

public final class MultiLineTextRenderer implements IGLRenderer {
	private static int LINE_HEIGHT = 15;

	private final List<String> l;

	public MultiLineTextRenderer(String text) {
		this(Arrays.asList(text.split("\n")));
	}

	public MultiLineTextRenderer(List<String> l) {
		this.l = l;
	}

	@Override
	public void render(GLGraphics g, float w, float h, GLElement parent) {
		g.drawText(l, 0, (h - LINE_HEIGHT * l.size()) * 0.5f, w, LINE_HEIGHT * l.size(), 0, VAlign.CENTER);
	}
}