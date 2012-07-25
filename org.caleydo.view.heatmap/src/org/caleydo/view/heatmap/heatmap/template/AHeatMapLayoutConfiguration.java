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

import javax.media.opengl.GL2;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutConfiguration;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.renderer.AHeatMapRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.CaptionCageRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.DimensionCaptionRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.DimensionSelectionRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.HeatMapRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.RecordCaptionRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.RecordSelectionRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.spacing.RecordSpacing;
import org.caleydo.view.heatmap.heatmap.renderer.texture.HeatMapTextureRenderer;

public abstract class AHeatMapLayoutConfiguration extends LayoutConfiguration {

	protected AHeatMapRenderer heatMapRenderer;
	protected RecordCaptionRenderer recordCaptionRenderer;
	protected DimensionCaptionRenderer dimensionCaptionRenderer;
	protected RecordSelectionRenderer recordSelectionRenderer;
	protected DimensionSelectionRenderer dimensionSelectionRenderer;
	protected CaptionCageRenderer captionCageRenderer;

	protected GLHeatMap heatMap;

	protected ElementLayout heatMapLayout;
	protected ElementLayout barPlotLayout;

	public int minSelectedFieldHeight = 16;
	// private float xOverheadToHeatMap;
	// private float yOverheadToHeatMap;

	protected RecordSpacing recordSpacing;

	private boolean renderAsTexture;

	public AHeatMapLayoutConfiguration(GLHeatMap heatMap) {
		this(heatMap, false);
	}

	public AHeatMapLayoutConfiguration(GLHeatMap heatMap, boolean renderAsTexture) {

		this.heatMap = heatMap;
		recordSpacing = new RecordSpacing(heatMap);
		this.renderAsTexture = renderAsTexture;

		if (renderAsTexture)
			heatMapRenderer = new HeatMapTextureRenderer(heatMap);
		else
			heatMapRenderer = new HeatMapRenderer(heatMap);

		heatMapRenderer.setRecordSpacing(recordSpacing);
		recordCaptionRenderer = new RecordCaptionRenderer(heatMap);
		recordCaptionRenderer.setRecordSpacing(recordSpacing);
		dimensionCaptionRenderer = new DimensionCaptionRenderer(heatMap);
		dimensionCaptionRenderer.setRecordSpacing(recordSpacing);
		recordSelectionRenderer = new RecordSelectionRenderer(heatMap);
		recordSelectionRenderer.setRecordSpacing(recordSpacing);
		dimensionSelectionRenderer = new DimensionSelectionRenderer(heatMap);
		dimensionSelectionRenderer.setRecordSpacing(recordSpacing);
		captionCageRenderer = new CaptionCageRenderer(heatMap);
		captionCageRenderer.setRecordSpacing(recordSpacing);
	}

	public void initRendererData(GL2 gl) {

		if (renderAsTexture) {
			((HeatMapTextureRenderer) heatMapRenderer).initialize(gl);
		}
	}

	public Float getYCoordinateByContentIndex(int recordIndex) {
		return heatMapLayout.getTranslateY()
				+ ((HeatMapRenderer) heatMapLayout.getRenderer())
						.getYCoordinateByContentIndex(recordIndex);
	}

	public Float getXCoordinateByDimensionIndex(int dimensionIndex) {

		return heatMapLayout.getTranslateX()
				+ ((HeatMapRenderer) heatMapLayout.getRenderer())
						.getXCoordinateByDimensionIndex(dimensionIndex);
	}

	public float getElementHeight(int recordID) {
		return recordSpacing.getFieldHeight(recordID);
	}

	public float getElementWidth(int dimensionID) {
		return recordSpacing.getFieldWidth();
	}

	public int getMinSelectedFieldHeight() {
		return minSelectedFieldHeight;
	}

	public void setContentSpacing(RecordSpacing contentSpacing) {
		this.recordSpacing = contentSpacing;
	}

	public void updateSpacing() {
		heatMapRenderer.updateSpacing();
	}
}
