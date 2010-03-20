package org.caleydo.view.heatmap.heatmap.template;

import org.caleydo.view.heatmap.heatmap.renderer.CaptionCageRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.ContentCaptionRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.ContentSelectionRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.DetailToolBar;
import org.caleydo.view.heatmap.heatmap.renderer.HeatMapRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.StorageSelectionRenderer;

public class ComparerDetailTemplate extends ATemplate {

	private boolean isLeft = true;

	public ComparerDetailTemplate(boolean isLeft) {
		this.isLeft = isLeft;
	}

	@Override
	public void setParameters() {

		templateRenderer.clearRenderers();
		verticalSpaceAllocations.clear();
		Row hmRow = new Row();
		// hmRow.grabY = true;
		// heat map
		RenderParameters hm = new RenderParameters();
		hm.grabX = true;
		hm.sizeY = 1f;
		hm.renderer = new HeatMapRenderer(templateRenderer.heatMap);
		templateRenderer.addRenderer(hm);
		templateRenderer.addHeatMapLayout(hm);

		RenderParameters contentSelectionLayout = new RenderParameters();
		contentSelectionLayout.isBackground = true;
		contentSelectionLayout.sizeX = 1;
		contentSelectionLayout.sizeY = 1;
		contentSelectionLayout.renderer = new ContentSelectionRenderer(
				templateRenderer.heatMap);
		templateRenderer.addRenderer(contentSelectionLayout);

		RenderParameters storageSelectionLayout = new RenderParameters();
		storageSelectionLayout.isBackground = true;
		// contentSelectionLayout.sizeX = 1;
		storageSelectionLayout.sizeY = 1;
		storageSelectionLayout.renderer = new StorageSelectionRenderer(
				templateRenderer.heatMap);
		templateRenderer.addRenderer(storageSelectionLayout);

		boolean renderCaptions = false;
		if (templateRenderer.heatMap.isShowCaptions()
				|| templateRenderer.heatMap.isActive())
			renderCaptions = true;
		RenderParameters caption = null;
		RenderParameters spacing = null;
		RenderParameters cage = null;
		if (renderCaptions) {
			// content cage

			cage = new RenderParameters();
			cage.sizeX = 0.3f;
			cage.sizeY = 1f;
			cage.isBackground = true;
			CaptionCageRenderer captionCageRenderer = new CaptionCageRenderer(
					templateRenderer.heatMap);

			cage.renderer = captionCageRenderer;
			templateRenderer.addRenderer(cage);

			spacing = new RenderParameters();
			spacing.sizeX = 0.01f;

			// content captions
			caption = new RenderParameters();
			caption.sizeX = 0.29f;
			caption.sizeY = 1f;
			ContentCaptionRenderer contentCaptionRenderer = new ContentCaptionRenderer(
					templateRenderer.heatMap);
			caption.renderer = contentCaptionRenderer;

			templateRenderer.addRenderer(caption);
		}

		hmRow.appendElement(contentSelectionLayout);
		if (isLeft) {
			if (renderCaptions) {
				hmRow.appendElement(cage);
				hmRow.appendElement(spacing);
				hmRow.appendElement(caption);
			}	
			hmRow.appendElement(storageSelectionLayout);
			hmRow.appendElement(hm);
		

		} else {
			hmRow.appendElement(storageSelectionLayout);
			hmRow.appendElement(hm);
		
			if (renderCaptions) {
				hmRow.appendElement(cage);
				hmRow.appendElement(spacing);
				hmRow.appendElement(caption);
			}
		}

		if (isActive) {
			RenderParameters toolBar;

			toolBar = new RenderParameters();
			toolBar.sizeX = 1f;
			toolBar.sizeY = 0.1f;

			toolBar.scaleY = false;

			toolBar.renderer = new DetailToolBar(templateRenderer.heatMap);

			templateRenderer.addRenderer(toolBar);
			add(hmRow);
			add(toolBar);
		} else
			add(hmRow);

	}

}
