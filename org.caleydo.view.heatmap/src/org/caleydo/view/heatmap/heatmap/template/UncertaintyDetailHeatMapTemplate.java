/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.heatmap.heatmap.template;

import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.renderer.BarPlotRenderer;
import org.caleydo.view.heatmap.uncertainty.GLUncertaintyHeatMap;

/**
 * Render template for the embedded heat map of the uncertainty heat map.
 * Assumes a static spacing at the bottom.
 * 
 * @author Alexander Lex
 * 
 */
public class UncertaintyDetailHeatMapTemplate extends AHeatMapLayoutConfiguration {

	protected GLUncertaintyHeatMap uncertaintyHeatMap;

	protected BarPlotRenderer barPlotRenderer;

	public UncertaintyDetailHeatMapTemplate(GLHeatMap heatMap,
			GLUncertaintyHeatMap uncertaintyHeatMap) {
		super(heatMap);

		this.uncertaintyHeatMap = uncertaintyHeatMap;

		if (uncertaintyHeatMap.isMaxUncertaintyCalculated()) {
			barPlotRenderer = new BarPlotRenderer(heatMap, uncertaintyHeatMap);
			barPlotRenderer.setRecordSpacing(recordSpacing);
		}

		minSelectedFieldHeight *= 2;
	}

	public float bottomSpacing = 0;

	@Override
	public void setStaticLayouts() {
		Column mainColumn = new Column("mainColumn");
		baseElementLayout = mainColumn;
		mainColumn.setRatioSizeX(1);
		mainColumn.setRatioSizeY(1);
		mainColumn.setBottomUp(false);
		// rendererParameters.clear();

		Row mainRow = new Row("heatMapRow");
		mainRow.setGrabY(true);
		mainRow.setRatioSizeX(1);

		int barPlotPixelWidth = 60;
		if (uncertaintyHeatMap.isMaxUncertaintyCalculated()) {

			barPlotLayout = new ElementLayout("BarPlotLayout");
			barPlotLayout.setRenderer(barPlotRenderer);
			barPlotLayout.setPixelSizeX(barPlotPixelWidth);
			// barPlotLayout.addForeGroundRenderer(contentSelectionRenderer);
			// barPlotLayout.addForeGroundRenderer(dimensionSelectionRenderer);

			mainRow.append(barPlotLayout);
		}

		heatMapLayout = new ElementLayout("hmlayout");
		heatMapLayout.setRenderer(heatMapRenderer);
		heatMapLayout.addForeGroundRenderer(recordSelectionRenderer);
		heatMapLayout.addForeGroundRenderer(dimensionSelectionRenderer);

		mainRow.append(heatMapLayout);

		ElementLayout spacing = new ElementLayout();
		spacing.setAbsoluteSizeX(0.12f);
		mainRow.append(spacing);

		// content captions

		int contentCaptionPixelWidth = 200;
		ElementLayout contentCaptionLayout = new ElementLayout("contentCaption");
		// contentCaptionLayout.setRatioSizeX(heatMapSizeX);
		contentCaptionLayout.setPixelSizeX(contentCaptionPixelWidth);
		contentCaptionLayout.setRenderer(recordCaptionRenderer);

		mainRow.append(contentCaptionLayout);

		mainColumn.append(mainRow);

		ElementLayout ySpacing = new ElementLayout();
		ySpacing.setAbsoluteSizeY(0.05f);
		mainColumn.append(ySpacing);

		Row dimensionCaptionRow = new Row("dimensionCaptionRow");
		dimensionCaptionRow.setAbsoluteSizeY(0.35f);

		ElementLayout dimensionCaptionLayout = new ElementLayout("dimensionCaption");
		dimensionCaptionLayout.setRatioSizeY(1);
		dimensionCaptionLayout.setGrabX(true);
		dimensionCaptionLayout.setRenderer(dimensionCaptionRenderer);

		if (uncertaintyHeatMap.isMaxUncertaintyCalculated()) {
			ElementLayout leadSpacingLayout = new ElementLayout();
			leadSpacingLayout.setPixelSizeX(barPlotPixelWidth);
			dimensionCaptionRow.append(leadSpacingLayout);
		}

		dimensionCaptionRow.append(dimensionCaptionLayout);

		ElementLayout postSpacingLayout = new ElementLayout();
		postSpacingLayout.setPixelSizeX(contentCaptionPixelWidth);

		dimensionCaptionRow.append(postSpacingLayout);

		mainColumn.append(dimensionCaptionRow);
	}

	/**
	 * set the static spacing at the bottom (for the caption). This needs to be
	 * done before it is rendered the first time.
	 * 
	 * @param bottomSpacing
	 */
	public void setBottomSpacing(float bottomSpacing) {
		this.bottomSpacing = bottomSpacing;
	}

}
