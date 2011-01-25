package org.caleydo.view.heatmap.heatmap.template;

import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
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
		Column mainColumn = new Column();
		setBaseElementLayout(mainColumn);
		mainColumn.setSizeX(1);
		mainColumn.setSizeY(1);

		contentCaptionRenderer.setFontScaling(fontScaling);
		minSelectedFieldHeight = HeatMapRenderStyle.MIN_SELECTED_FIELD_HEIGHT;

		Row hmRow = new Row();
		// hmRow.grabY = true;
		// heat map
		heatMapLayout = new ElementLayout();
		heatMapLayout.setGrabX(true);
		heatMapLayout.setSizeY(1f);
		heatMapLayout.setRenderer(heatMapRenderer);
		heatMapLayout.addForeGroundRenderer(contentSelectionRenderer);
		heatMapLayout.addForeGroundRenderer(storageSelectionRenderer);

		boolean renderCaptions = false;
		if (heatMap.isShowCaptions() || heatMap.isActive())
			renderCaptions = true;
		ElementLayout caption = null;
		ElementLayout spacing = null;
		if (renderCaptions) {
			// content cage

			spacing = new ElementLayout();
			spacing.setSizeX(0.01f);

			// content captions
			caption = new ElementLayout();
			caption.setSizeX(0.29f);
			caption.setSizeY(1f);

			caption.setRenderer(contentCaptionRenderer);
			caption.addBackgroundRenderer(captionCageRenderer);

			// rendererParameters.add(caption);
		}

		if (isLeft) {
			if (renderCaptions) {

				hmRow.appendElement(spacing);
				hmRow.appendElement(caption);
			}

			hmRow.appendElement(heatMapLayout);

		} else {

			hmRow.appendElement(heatMapLayout);

			if (renderCaptions) {

				hmRow.appendElement(spacing);
				hmRow.appendElement(caption);
			}
		}

		mainColumn.appendElement(hmRow);
		if (isActive) {
			ElementLayout toolBar;

			toolBar = new ElementLayout();
			toolBar.setSizeX(1f);
			toolBar.setSizeY(0.1f);

			toolBar.setScaleY(false);

			toolBar.setRenderer(new DetailToolBar(heatMap));

			mainColumn.appendElement(toolBar);
		}

	}

}
