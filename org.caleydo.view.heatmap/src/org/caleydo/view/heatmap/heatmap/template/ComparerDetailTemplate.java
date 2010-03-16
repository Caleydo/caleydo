package org.caleydo.view.heatmap.heatmap.template;

public class ComparerDetailTemplate extends ATemplate {

	private boolean isLeft = true;

	public ComparerDetailTemplate(boolean isLeft) {
		this.isLeft = isLeft;
	}

	@Override
	public void setParameters() {
		// heat map
		RenderParameters parameters = new RenderParameters();
		parameters.sizeX = 0.7f;
		parameters.sizeY = 1f;
		if (isLeft)
			parameters.transformX = 0.3f;
		templateRenderer.heatMapParameters = parameters;

		// content captions
		parameters = new RenderParameters();
		parameters.sizeX = 0.29f;
		parameters.sizeY = 1f;
		if (isLeft)
			parameters.transformX = templateRenderer.SPACING;

		else
			parameters.transformX = templateRenderer.heatMapParameters.sizeX
					+ templateRenderer.SPACING;

		templateRenderer.contentCaptionParameters = parameters;

		// content cage
		parameters = new RenderParameters();
		parameters.sizeX = 0.3f;
		parameters.sizeY = 1f;
		if (!isLeft)
			parameters.transformX = templateRenderer.heatMapParameters.sizeX;

		templateRenderer.captionCageParameters = parameters;

	}
}
