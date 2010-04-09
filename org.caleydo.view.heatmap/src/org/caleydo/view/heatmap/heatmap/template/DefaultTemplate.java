package org.caleydo.view.heatmap.heatmap.template;

import org.caleydo.view.heatmap.heatmap.renderer.ContentCaptionRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.ContentSelectionRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.HeatMapRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.StorageSelectionRenderer;

public class DefaultTemplate extends ATemplate {

	@Override
	public void setParameters() {
		
		templateRenderer.clearRenderers();
		
		Row row = new Row();
		row.sizeY = 1;
		// heat map
		RenderParameters heatMapLayout = new RenderParameters();
		heatMapLayout.sizeX = 0.715f;
		heatMapLayout.sizeY = 0.84f;
		heatMapLayout.renderer = new HeatMapRenderer(templateRenderer.heatMap);
		templateRenderer.addRenderer(heatMapLayout);
		templateRenderer.addHeatMapLayout(heatMapLayout);

		RenderParameters contentSelectionLayout = new RenderParameters();
		contentSelectionLayout.isBackground = true;
		contentSelectionLayout.sizeX = 0.72f;
		contentSelectionLayout.renderer = new ContentSelectionRenderer(
				templateRenderer.heatMap);
		templateRenderer.addRenderer(contentSelectionLayout);

		RenderParameters storageSelectionLayout = new RenderParameters();
		storageSelectionLayout.isBackground = true;
		// contentSelectionLayout.sizeX = 1;
		storageSelectionLayout.sizeY = 0.84f;
		storageSelectionLayout.renderer = new StorageSelectionRenderer(
				templateRenderer.heatMap);
		templateRenderer.addRenderer(storageSelectionLayout);
		row.appendElement(contentSelectionLayout);
		row.appendElement(storageSelectionLayout);
		row.appendElement(heatMapLayout);
	

		RenderParameters spacing = new RenderParameters();
		spacing.sizeX = 0.01f;
		row.appendElement(spacing);
		
		// content captions
		RenderParameters contentCaptionLayout = new RenderParameters();
		contentCaptionLayout.sizeX = 0.29f;
		contentCaptionLayout.sizeY = 0.84f;
		// heatMapLayout.grabY = true;
		contentCaptionLayout.transformX = 0.7f + templateRenderer.SPACING;
		contentCaptionLayout.renderer = new ContentCaptionRenderer(
				templateRenderer.heatMap);

		templateRenderer.addRenderer(contentCaptionLayout);

		row.appendElement(contentCaptionLayout);

		add(row);

		spacing = new RenderParameters();
		spacing.sizeY = 0.16f;
		add(spacing);

	}

}
