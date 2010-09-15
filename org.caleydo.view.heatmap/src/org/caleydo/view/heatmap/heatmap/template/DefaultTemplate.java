package org.caleydo.view.heatmap.heatmap.template;


public class DefaultTemplate extends ATemplate {

	@Override
	public void setParameters() {

		templateRenderer.clearRenderers();

		Row row = new Row();
		row.sizeY = 1;
		// heat map
		RenderParameters heatMapLayout = new RenderParameters();
		// heatMapLayout.sizeX = 0.715f;
		heatMapLayout.sizeX = 0.806f;
		heatMapLayout.sizeY = 0.883f;
		heatMapLayout.renderer = heatMapRenderer;
		templateRenderer.addRenderer(heatMapLayout);
		templateRenderer.addHeatMapLayout(heatMapLayout);

		RenderParameters contentSelectionLayout = new RenderParameters();
		contentSelectionLayout.isBackground = true;
		contentSelectionLayout.sizeX = heatMapLayout.sizeX;
		contentSelectionLayout.renderer = contentSelectionRenderer;
		templateRenderer.addRenderer(contentSelectionLayout);

		RenderParameters storageSelectionLayout = new RenderParameters();
		storageSelectionLayout.isBackground = true;
		// contentSelectionLayout.sizeX = 1;
		storageSelectionLayout.sizeY = heatMapLayout.sizeY;
		storageSelectionLayout.renderer = storageSelectionRenderer;
		templateRenderer.addRenderer(storageSelectionLayout);
		row.appendElement(contentSelectionLayout);
		row.appendElement(storageSelectionLayout);
		row.appendElement(heatMapLayout);

		RenderParameters spacing = new RenderParameters();
		spacing.sizeX = 0.01f;
		row.appendElement(spacing);

		// content captions
		RenderParameters contentCaptionLayout = new RenderParameters();
		contentCaptionLayout.sizeX = 1 - heatMapLayout.sizeX;
		contentCaptionLayout.sizeY = heatMapLayout.sizeY;
		// heatMapLayout.grabY = true;
		contentCaptionLayout.transformX = 0.7f + templateRenderer.SPACING;
		contentCaptionLayout.renderer = contentCaptionRenderer;

		templateRenderer.addRenderer(contentCaptionLayout);

		row.appendElement(contentCaptionLayout);

		add(row);

		spacing = new RenderParameters();
		spacing.sizeY = 1 - heatMapLayout.sizeY;
		add(spacing);

	}

}
