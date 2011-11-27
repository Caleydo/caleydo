package org.caleydo.view.heatmap.heatmap.template;

import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.view.heatmap.HeatMapRenderStyle;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

/**
 * Render LayoutTemplate for HeatMap in GLBucketView
 * 
 * @author Alexander Lex
 * 
 */
public class BrickHeatMapTemplate extends AHeatMapTemplate {

	public BrickHeatMapTemplate(GLHeatMap heatMap) {
		super(heatMap);
		minSelectedFieldHeight = 0.1f;

	}

	@Override
	public void setStaticLayouts() {
		Column mainColumn = new Column();
		baseElementLayout = mainColumn;
		mainColumn.setRatioSizeX(1);
		mainColumn.setRatioSizeY(1);

		minSelectedFieldHeight = HeatMapRenderStyle.MIN_SELECTED_FIELD_HEIGHT;

		Row hmRow = new Row();
		hmRow.setPriorityRendereing(true);
		// hmRow.grabY = true;
		// heat map
		heatMapLayout = new ElementLayout();
		heatMapLayout.setGrabX(true);
		heatMapLayout.setRatioSizeY(1f);
		heatMapLayout.setRenderer(heatMapRenderer);
		heatMapLayout.addForeGroundRenderer(recordSelectionRenderer);
		heatMapLayout.addForeGroundRenderer(dimensionSelectionRenderer);
		heatMapLayout.setRenderingPriority(1);

		boolean renderCaptions = false;
		if (heatMap.isShowCaptions() || heatMap.isActive())
			renderCaptions = true;
		ElementLayout caption = null;
		ElementLayout spacing = null;

		hmRow.append(heatMapLayout);

		if (renderCaptions) {

			spacing = new ElementLayout();
			spacing.setAbsoluteSizeX(0.01f);

			// content captions
			caption = new ElementLayout();
			caption.setPixelSizeX(80);
			caption.setRatioSizeY(1);

			caption.setRenderer(recordCaptionRenderer);

			hmRow.append(spacing);
			hmRow.append(caption);
		}

		mainColumn.append(hmRow);

	}

}
