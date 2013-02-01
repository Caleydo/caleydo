package org.caleydo.core.view.opengl.layout.builder;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.util.PickingRenderer;

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


	public ElementLayoutBuilder pickable(String pickingType, int pickingId, AGLView view) {
		l.addBackgroundRenderer(new PickingRenderer(pickingType, pickingId, view));
		return this;
	}

	public ElementLayoutBuilder render(ALayoutRenderer renderer) {
		l.setRenderer(renderer);
		return this;
	}

	public ElementLayoutBuilder render(ILayoutRendererBuilder renderer) {
		l.setRenderer(renderer.build());
		return this;
	}
}