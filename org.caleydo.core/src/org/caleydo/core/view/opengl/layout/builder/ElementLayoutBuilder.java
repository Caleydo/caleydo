package org.caleydo.core.view.opengl.layout.builder;

import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.layout.ElementLayout;

public class ElementLayoutBuilder {
	protected ElementLayout l = new ElementLayout();

	public ElementLayout build() {
		return l;
	}

	public ElementLayoutBuilder width(int width) {
		if (width < 0)
			l.setGrabX(true);
		else
			l.setPixelSizeX(width);
		return this;
	}

	public ElementLayoutBuilder height(int height) {
		if (height < 0)
			l.setGrabY(true);
		else
			l.setPixelSizeY(height);
		return this;
	}


	public ElementLayoutBuilder render(ALayoutRenderer renderer) {
		l.setRenderer(renderer);
		return this;
	}
}