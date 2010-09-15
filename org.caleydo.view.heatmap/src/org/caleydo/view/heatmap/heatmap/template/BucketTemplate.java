package org.caleydo.view.heatmap.heatmap.template;

import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

/**
 * Render Template for GLBucketView
 * 
 * @author Alexander Lex
 * 
 */
public class BucketTemplate extends ATemplate {

	public BucketTemplate() {
		minSelectedFieldHeight = 0.5f;
		fontScaling = GeneralRenderStyle.SMALL_FONT_SCALING_FACTOR * 1.8f;
		
	}

	@Override
	public void setParameters() {
		contentCaptionRenderer.setFontScaling(fontScaling);
		templateRenderer.clearRenderers();
		verticalSpaceAllocations.clear();
		Row hmRow = new Row();
		// hmRow.grabY = true;
		// heat map
		RenderParameters hm = new RenderParameters();
		hm.grabX = true;
		hm.sizeY = 1f;
		hm.renderer = heatMapRenderer;
		templateRenderer.addRenderer(hm);
		templateRenderer.addHeatMapLayout(hm);

		RenderParameters contentSelectionLayout = new RenderParameters();
		contentSelectionLayout.isBackground = true;
		contentSelectionLayout.sizeX = 1;
		contentSelectionLayout.sizeY = 1;
		contentSelectionLayout.renderer = contentSelectionRenderer;
		templateRenderer.addRenderer(contentSelectionLayout);

		RenderParameters storageSelectionLayout = new RenderParameters();
		storageSelectionLayout.isBackground = true;
		// contentSelectionLayout.sizeX = 1;
		storageSelectionLayout.sizeY = 1;
		storageSelectionLayout.renderer = storageSelectionRenderer;
		templateRenderer.addRenderer(storageSelectionLayout);

		boolean renderCaptions = false;
		if (templateRenderer.heatMap.isShowCaptions())
			renderCaptions = true;
		RenderParameters caption = null;
		RenderParameters spacing = null;
		RenderParameters cage = null;
		if (renderCaptions) {
			// content cage

			cage = new RenderParameters();
			cage.sizeX = 0.1f;
			cage.sizeY = 1f;
			cage.isBackground = true;

			cage.renderer = captionCageRenderer;
			templateRenderer.addRenderer(cage);

			spacing = new RenderParameters();
			spacing.sizeX = 0.01f;

			// content captions
			caption = new RenderParameters();
			caption.sizeX = 0.09f;
			caption.sizeY = 1f;
			caption.renderer = contentCaptionRenderer;

			templateRenderer.addRenderer(caption);
		}

		hmRow.appendElement(contentSelectionLayout);

		hmRow.appendElement(storageSelectionLayout);
		hmRow.appendElement(hm);

		if (renderCaptions) {
			hmRow.appendElement(cage);
			hmRow.appendElement(spacing);
			hmRow.appendElement(caption);
		}

		add(hmRow);

	}

}
