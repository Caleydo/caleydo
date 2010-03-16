package org.caleydo.view.heatmap.heatmap.template;

import org.caleydo.view.heatmap.heatmap.renderer.ContentCaptionRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.HeatMapRenderer;

public class DefaultTemplate extends ATemplate {

	@Override
	public void setParameters() {
		Row row = new Row();
		// heat map
		RenderParameters parameters = new RenderParameters();
		parameters.sizeX = 0.7f;
		parameters.sizeY = 1f;
		templateRenderer.addRenderer(new HeatMapRenderer(
				templateRenderer.heatMap), parameters);

		row.appendElement(parameters);

		// content captions
		parameters = new RenderParameters();
		parameters.sizeX = 0.29f;
		parameters.sizeY = 1f;
		parameters.transformX = 0.7f + templateRenderer.SPACING;
		templateRenderer.addRenderer(new ContentCaptionRenderer(
				templateRenderer.heatMap), parameters);

		row.appendElement(parameters);

		add(row);

	}

}
