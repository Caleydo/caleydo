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

/**
 * Render template for the embedded heat map of the hierarchical heat map.
 * Assumes a static spacing at the bottom.
 * 
 * @author Alexander Lex
 * 
 */
public class HierarchicalHeatMapTemplate extends AHeatMapLayoutConfiguration {

	public HierarchicalHeatMapTemplate(GLHeatMap heatMap) {
		super(heatMap);
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

		// float heatMapSizeX = 0.806f;

		Row mainRow = new Row("heatMapRow");
		mainRow.setGrabY(true);
		mainRow.setRatioSizeX(1);

		heatMapLayout = new ElementLayout("hmlayout");
		heatMapLayout.setGrabX(true);
		heatMapLayout.setRenderer(heatMapRenderer);
		heatMapLayout.addForeGroundRenderer(recordSelectionRenderer);
		heatMapLayout.addForeGroundRenderer(dimensionSelectionRenderer);

		mainRow.append(heatMapLayout);

		ElementLayout spacing = new ElementLayout();
		spacing.setAbsoluteSizeX(0.12f);
		mainRow.append(spacing);

		// record captions
		ElementLayout recordCaptionLayout = new ElementLayout("recordCaption");
		recordCaptionLayout.setRatioSizeY(1);
		recordCaptionLayout.setAbsoluteSizeX(0.6f);
		recordCaptionLayout.setRenderer(recordCaptionRenderer);
		mainRow.append(recordCaptionLayout);

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
		dimensionCaptionRow.append(dimensionCaptionLayout);

		ElementLayout spacingLayout = new ElementLayout();
		spacingLayout.setAbsoluteSizeX(0.65f);

		dimensionCaptionRow.append(spacingLayout);

		mainColumn.append(dimensionCaptionRow);

	}

	/**
	 * set the static spacing at the bottom (for the caption). This needs to be
	 * done before it is renedered the first time.
	 * 
	 * @param bottomSpacing
	 */
	public void setBottomSpacing(float bottomSpacing) {
		this.bottomSpacing = bottomSpacing;
	}

}
