package org.caleydo.view.heatmap.heatmap.template;

import org.caleydo.core.view.opengl.layout.LayoutParameters;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.view.heatmap.HeatMapRenderStyle;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.renderer.DetailToolBar;

/**
 * Render Template for the detail view of the Matchmaker.
 * 
 * @author Alexander Lex
 * 
 */
public class MatchmakerDetailTemplate extends AHeatMapTemplate {

	private boolean isLeft = true;

	public MatchmakerDetailTemplate(GLHeatMap heatMap, boolean isLeft) {
		super(heatMap);
		this.isLeft = isLeft;
		fontScaling = 0.03f / 1.2f;

	}

	@Override
	public void setParameters() {
		contentCaptionRenderer.setFontScaling(fontScaling);
		minSelectedFieldHeight = HeatMapRenderStyle.MIN_SELECTED_FIELD_HEIGHT;
		rendererParameters.clear();
		verticalSpaceAllocations.clear();
		Row hmRow = new Row();
		// hmRow.grabY = true;
		// heat map
		heatMapLayout = new LayoutParameters();
		heatMapLayout.setGrabX(true);
		heatMapLayout.setSizeY(1f);
		heatMapLayout.setRenderer(heatMapRenderer);
		rendererParameters.add(heatMapLayout);

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
		if (heatMap.isShowCaptions()
				|| heatMap.isActive())
			renderCaptions = true;
		LayoutParameters caption = null;
		LayoutParameters spacing = null;
		LayoutParameters cage = null;
		if (renderCaptions) {
			// content cage

			cage = new LayoutParameters();
			cage.setSizeX(0.3f);
			cage.setSizeY(1f);
			cage.setIsBackground(true);

			cage.setRenderer(captionCageRenderer);
			rendererParameters.add(cage);

			spacing = new LayoutParameters();
			spacing.setSizeX(0.01f);

			// content captions
			caption = new LayoutParameters();
			caption.setSizeX(0.29f);
			caption.setSizeY(1f);

			caption.setRenderer(contentCaptionRenderer);

			rendererParameters.add(caption);
		}

		hmRow.appendElement(contentSelectionLayout);
		if (isLeft) {
			if (renderCaptions) {
				hmRow.appendElement(cage);
				hmRow.appendElement(spacing);
				hmRow.appendElement(caption);
			}
			hmRow.appendElement(storageSelectionLayout);
			hmRow.appendElement(heatMapLayout);

		} else {
			hmRow.appendElement(storageSelectionLayout);
			hmRow.appendElement(heatMapLayout);

			if (renderCaptions) {
				hmRow.appendElement(cage);
				hmRow.appendElement(spacing);
				hmRow.appendElement(caption);
			}
		}

		if (isActive) {
			LayoutParameters toolBar;

			toolBar = new LayoutParameters();
			toolBar.setSizeX(1f);
			toolBar.setSizeY(0.1f);

			toolBar.setScaleY(false);

			toolBar.setRenderer(new DetailToolBar(heatMap));

			rendererParameters.add(toolBar);
			addRenderElement(hmRow);
			addRenderElement(toolBar);
		} else
			addRenderElement(hmRow);

	}

}
