package org.caleydo.view.heatmap.heatmap.template;

import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

/**
 * Render LayoutTemplate for HeatMap in GLBucketView
 * 
 * @author Alexander Lex
 * 
 */
public class BucketTemplate extends AHeatMapLayoutConfiguration {

	public BucketTemplate(GLHeatMap heatMap) {
		super(heatMap);

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
		if (heatMap.isShowCaptions())
			renderCaptions = true;
		ElementLayout caption = null;
		ElementLayout spacing = null;
		if (renderCaptions) {

			spacing = new ElementLayout();
			spacing.setAbsoluteSizeX(0.01f);

			// content captions
			caption = new ElementLayout();
			caption.setAbsoluteSizeX(1f);
			caption.setRatioSizeY(1f);
			caption.setRenderer(recordCaptionRenderer);
			caption.addBackgroundRenderer(captionCageRenderer);

			// rendererParameters.add(caption);
		}

		hmRow.append(heatMapLayout);

		if (renderCaptions) {
			hmRow.append(spacing);
			hmRow.append(caption);
		}

		mainColumn.append(hmRow);
		ElementLayout headingSpacing = new ElementLayout();
		if (renderCaptions)
			headingSpacing.setAbsoluteSizeY(0f);
		else
			headingSpacing.setAbsoluteSizeY(0.3f);

		mainColumn.append(headingSpacing);

	}

}
