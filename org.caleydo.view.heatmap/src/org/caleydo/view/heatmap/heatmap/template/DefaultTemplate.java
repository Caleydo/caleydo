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
