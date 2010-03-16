package org.caleydo.view.heatmap.heatmap.template;

import javax.media.opengl.GL;

public abstract class ATemplate {

	// super(heatMap);
	// initRenderers();

	protected TemplateRenderer templateRenderer;

	public abstract void setParameters();

	public void setTemplateRenderer(TemplateRenderer templateRenderer) {
		this.templateRenderer = templateRenderer;
	}

}
