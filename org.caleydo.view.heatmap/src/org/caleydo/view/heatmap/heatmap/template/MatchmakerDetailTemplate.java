package org.caleydo.view.heatmap.heatmap.template;

import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

/**
 * Render LayoutTemplate for the detail view of the Matchmaker.
 * 
 * @author Alexander Lex
 * 
 */
public class MatchmakerDetailTemplate extends AHeatMapLayoutConfiguration {

	private boolean isLeft = true;

	public MatchmakerDetailTemplate(GLHeatMap heatMap, boolean isLeft) {
		super(heatMap);
		this.isLeft = isLeft;

	}

	@Override
	public void setStaticLayouts() {
		Column mainColumn = new Column();
		baseElementLayout = mainColumn;
		mainColumn.setRatioSizeX(1);
		mainColumn.setRatioSizeY(1);

		Row hmRow = new Row();
		// hmRow.grabY = true;
		// heat map
		heatMapLayout = new ElementLayout();
		heatMapLayout.setGrabX(true);
		heatMapLayout.setRatioSizeY(1f);
		heatMapLayout.setRenderer(heatMapRenderer);
		heatMapLayout.addForeGroundRenderer(recordSelectionRenderer);
		heatMapLayout.addForeGroundRenderer(dimensionSelectionRenderer);

		boolean renderCaptions = false;
		if (heatMap.isShowCaptions() || heatMap.isActive())
			renderCaptions = true;
		ElementLayout caption = null;
		ElementLayout spacing = null;
		if (renderCaptions) {
			// content cage

			spacing = new ElementLayout();
			spacing.setAbsoluteSizeX(0.01f);

			// content captions
			caption = new ElementLayout();
			caption.setRatioSizeX(0.29f);
			caption.setRatioSizeY(1);

			caption.setRenderer(recordCaptionRenderer);
			caption.addBackgroundRenderer(captionCageRenderer);

			// rendererParameters.add(caption);
		}

		if (isLeft) {
			if (renderCaptions) {

				hmRow.append(spacing);
				hmRow.append(caption);
			}

			hmRow.append(heatMapLayout);

		} else {

			hmRow.append(heatMapLayout);

			if (renderCaptions) {

				hmRow.append(spacing);
				hmRow.append(caption);
			}
		}

		mainColumn.append(hmRow);
		// if (isActive) {
		// ElementLayout toolBar;
		//
		// toolBar = new ElementLayout();
		// toolBar.setRatioSizeX(1);
		// toolBar.setAbsoluteSizeY(0.1f);
		//
		// toolBar.setRenderer(new DetailToolBar(heatMap));
		//
		// mainColumn.append(toolBar);
		// }

	}

}
