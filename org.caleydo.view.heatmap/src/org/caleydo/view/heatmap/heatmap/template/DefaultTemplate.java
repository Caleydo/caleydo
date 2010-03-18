package org.caleydo.view.heatmap.heatmap.template;

import org.caleydo.view.heatmap.heatmap.renderer.ContentCaptionRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.HeatMapRenderer;

public class DefaultTemplate extends ATemplate {

	@Override
	public void setParameters() {
		Row row = new Row();
		row.sizeY = 1;
		// heat map
		RenderParameters parameters = new RenderParameters();
		parameters.sizeX = 0.7f;
		parameters.sizeY = 1;
		parameters.renderer = new HeatMapRenderer(templateRenderer.heatMap);
		templateRenderer.addRenderer(parameters);

		row.appendElement(parameters);

		// content captions
		parameters = new RenderParameters();
		parameters.sizeX = 0.29f;
		parameters.sizeY = 1f;
		parameters.transformX = 0.7f + templateRenderer.SPACING;
		parameters.renderer = new ContentCaptionRenderer(
				templateRenderer.heatMap);

		templateRenderer.addRenderer(parameters);

		row.appendElement(parameters);

		add(row);

	}

}
