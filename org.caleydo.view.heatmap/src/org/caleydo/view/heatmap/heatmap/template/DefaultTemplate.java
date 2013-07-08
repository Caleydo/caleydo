/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap.heatmap.template;

import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

/**
 * 
 * @author LayoutTemplate rendering a standalone heat map
 * 
 */
public class DefaultTemplate extends AHeatMapLayoutConfiguration {

	public DefaultTemplate(GLHeatMap heatMap) {
		super(heatMap);
	}

	@Override
	public void setStaticLayouts() {
		Column mainColumn = new Column();
		baseElementLayout = mainColumn;
		mainColumn.setRatioSizeX(1);
		mainColumn.setRatioSizeY(1);

		float heatMapSizeX = 0.806f;
		float heatMapSizeY = 0.883f;

		Row heatMapRow = new Row();
		heatMapRow.setRatioSizeY(1);
		// heat map
		heatMapLayout = new ElementLayout();
		// heatMapLayout.sizeX = 0.715f;
		heatMapLayout.setRatioSizeX(heatMapSizeX);
		heatMapLayout.setRatioSizeY(heatMapSizeY);
		heatMapLayout.setRenderer(heatMapRenderer);
		heatMapLayout.addForeGroundRenderer(recordSelectionRenderer);
		heatMapLayout.addForeGroundRenderer(dimensionSelectionRenderer);
		// rendererParameters.add(heatMapLayout);

		heatMapRow.append(heatMapLayout);

		ElementLayout spacing = new ElementLayout();
		spacing.setAbsoluteSizeX(0.01f);
		heatMapRow.append(spacing);

		// record captions
		ElementLayout recordCaptionLayout = new ElementLayout();
		recordCaptionLayout.setRatioSizeX(heatMapSizeX);
		recordCaptionLayout.setRatioSizeY(heatMapSizeY);
		recordCaptionLayout.setRenderer(recordCaptionRenderer);

		// rendererParameters.add(contentCaptionLayout);

		heatMapRow.append(recordCaptionLayout);

		mainColumn.append(heatMapRow);

		// spacing = new ElementLayout();
		// spacing.setSizeY(1 - heatMapLayout.getSizeY());
		// addRenderElement(spacing);

	}

}
