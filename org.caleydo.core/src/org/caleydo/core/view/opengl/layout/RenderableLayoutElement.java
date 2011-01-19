package org.caleydo.core.view.opengl.layout;

public class RenderableLayoutElement
	extends ElementLayout {

	public void setRenderer(ARenderer renderer) {
		this.renderer = renderer;
	}

	public ARenderer getRenderer() {
		return renderer;
	}

	ARenderer renderer;

}
