package org.caleydo.view.heatmap.heatmap.template;

import org.caleydo.view.heatmap.heatmap.renderer.ContentCaptionRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.DetailToolBar;
import org.caleydo.view.heatmap.heatmap.renderer.HeatMapRenderer;

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
		// verticalSpaceAllocations.add(parameters);

		boolean renderCaptions = templateRenderer.heatMap.isShowCaptions();
		RenderParameters caption = null;
		if (renderCaptions) {
			// content captions
			caption = new RenderParameters();
			caption.sizeX = 0.29f;
			caption.sizeY = 1f;
			caption.renderer = new ContentCaptionRenderer(
					templateRenderer.heatMap);

			templateRenderer.addRenderer(caption);
		}
		// content cage
		// RenderParameters cage;
		// cage = new RenderParameters();
		// cage.sizeX = 0.3f;
		// cage.sizeY = 1f;
		//
		//
		// templateRenderer.addRenderer(new CaptionCageRenderer(
		// templateRenderer.heatMap), cage);
		// hmRow.appendElement(parameters);

		if (isLeft) {
			if (renderCaptions)
				hmRow.appendElement(caption);
			hmRow.appendElement(hm);

		} else {
			hmRow.appendElement(hm);
			if (renderCaptions)
				hmRow.appendElement(caption);
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
