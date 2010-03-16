package org.caleydo.view.heatmap.heatmap.template;

public class DefaultTemplate extends ATemplate {

	@Override
	public void setParameters() {
		// heat map
		RenderParameters parameters = new RenderParameters();
		parameters.sizeX = 0.7f;
		parameters.sizeY = 1f;
		templateRenderer.heatMapParameters = parameters;

		// content captions
		parameters = new RenderParameters();
		parameters.sizeX = 0.29f;
		parameters.sizeY = 1f;
		parameters.transformX = templateRenderer.heatMapParameters.sizeX
				+ templateRenderer.SPACING;
		templateRenderer.contentCaptionParameters = parameters;

	}

}
