package org.caleydo.view.heatmap.heatmap.template;

import org.caleydo.view.heatmap.heatmap.renderer.CaptionCageRenderer;
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

		// verticalSpaceAllocations.clear();
		Row hmRow = new Row();
		// heat map
		RenderParameters hm = new RenderParameters();
		hm.sizeX = 0.7f;
		hm.sizeY = 1f;

		templateRenderer.addRenderer(new HeatMapRenderer(
				templateRenderer.heatMap), hm);
		// verticalSpaceAllocations.add(parameters);

		RenderParameters caption;
		// content captions
		caption = new RenderParameters();
		caption.sizeX = 0.29f;
		caption.sizeY = 1f;

		templateRenderer.addRenderer(new ContentCaptionRenderer(
				templateRenderer.heatMap), caption);

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
			hmRow.appendElement(caption);
			hmRow.appendElement(hm);

		} else {
			hmRow.appendElement(hm);
			hmRow.appendElement(caption);
		}

		RenderParameters toolBar;

		toolBar = new RenderParameters();
		toolBar.sizeX = 1f;
		toolBar.sizeY = 0.1f;

		toolBar.scaleY = false;

		templateRenderer.addRenderer(
				new DetailToolBar(templateRenderer.heatMap), toolBar);
		add(hmRow);
		add(toolBar);

	}
}
