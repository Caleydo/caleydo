package org.caleydo.view.heatmap.heatmap.template;

import org.caleydo.core.view.opengl.layout.LayoutParameters;
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
		LayoutParameters hm = new LayoutParameters();
		hm.setGrabX(true);
		hm.setSizeY(1f);
		hm.setRenderer(heatMapRenderer);
		rendererParameters.add(hm);
		heatMapLayout = hm;

		LayoutParameters contentSelectionLayout = new LayoutParameters();
		contentSelectionLayout.setIsBackground(true);
		contentSelectionLayout.setSizeX(1);
		contentSelectionLayout.setSizeY(1);
		contentSelectionLayout.setRenderer(contentSelectionRenderer);
		rendererParameters.add(contentSelectionLayout);

		LayoutParameters storageSelectionLayout = new LayoutParameters();
		storageSelectionLayout.setIsBackground(true);
		// contentSelectionLayout.sizeX = 1;
		storageSelectionLayout.setSizeY(1);
		storageSelectionLayout.setRenderer(storageSelectionRenderer);
		rendererParameters.add(storageSelectionLayout);

		boolean renderCaptions = false;
		if (heatMap.isShowCaptions())
			renderCaptions = true;
		LayoutParameters caption = null;
		LayoutParameters spacing = null;
		LayoutParameters cage = null;
		if (renderCaptions) {
			// content cage

			cage = new LayoutParameters();
			cage.setSizeX(0.1f);
			cage.setSizeY(1f);
			cage.setIsBackground(true);

			cage.setRenderer(captionCageRenderer);
			rendererParameters.add(cage);

			spacing = new LayoutParameters();
			spacing.setSizeX(0.01f);

			// content captions
			caption = new LayoutParameters();
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
