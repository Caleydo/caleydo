package org.caleydo.view.heatmap.heatmap.template;

import org.caleydo.core.view.opengl.layout.RenderParameters;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

/**
 * Render Template for HeatMap in GLBucketView
 * 
 * @author Alexander Lex
 * 
 */
public class BucketTemplate extends AHeatMapTemplate {

	public BucketTemplate(GLHeatMap heatMap) {
		super(heatMap);
		minSelectedFieldHeight = 0.5f;
		fontScaling = GeneralRenderStyle.SMALL_FONT_SCALING_FACTOR * 1.8f;

	}

	@Override
	public void setParameters() {
		contentCaptionRenderer.setFontScaling(fontScaling);
		rendererParameters.clear();
		verticalSpaceAllocations.clear();
		Row hmRow = new Row();
		// hmRow.grabY = true;
		// heat map
		RenderParameters hm = new RenderParameters();
		hm.setGrabX(true);
		hm.setSizeY(1f);
		hm.setRenderer(heatMapRenderer);
		rendererParameters.add(hm);
		heatMapLayout = hm;

		RenderParameters contentSelectionLayout = new RenderParameters();
		contentSelectionLayout.setIsBackground(true);
		contentSelectionLayout.setSizeX(1);
		contentSelectionLayout.setSizeY(1);
		contentSelectionLayout.setRenderer(contentSelectionRenderer);
		rendererParameters.add(contentSelectionLayout);

		RenderParameters storageSelectionLayout = new RenderParameters();
		storageSelectionLayout.setIsBackground(true);
		// contentSelectionLayout.sizeX = 1;
		storageSelectionLayout.setSizeY(1);
		storageSelectionLayout.setRenderer(storageSelectionRenderer);
		rendererParameters.add(storageSelectionLayout);

		boolean renderCaptions = false;
		if (heatMap.isShowCaptions())
			renderCaptions = true;
		RenderParameters caption = null;
		RenderParameters spacing = null;
		RenderParameters cage = null;
		if (renderCaptions) {
			// content cage

			cage = new RenderParameters();
			cage.setSizeX(0.1f);
			cage.setSizeY(1f);
			cage.setIsBackground(true);

			cage.setRenderer(captionCageRenderer);
			rendererParameters.add(cage);

			spacing = new RenderParameters();
			spacing.setSizeX(0.01f);

			// content captions
			caption = new RenderParameters();
			caption.setSizeX(0.09f);
			caption.setSizeY(1f);
			caption.setRenderer(contentCaptionRenderer);

			rendererParameters.add(caption);
		}

		hmRow.appendElement(contentSelectionLayout);

		hmRow.appendElement(storageSelectionLayout);
		hmRow.appendElement(hm);

		if (renderCaptions) {
			hmRow.appendElement(cage);
			hmRow.appendElement(spacing);
			hmRow.appendElement(caption);
		}

		addRenderElement(hmRow);

	}

}
