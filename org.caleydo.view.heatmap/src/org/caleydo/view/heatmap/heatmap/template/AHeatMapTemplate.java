package org.caleydo.view.heatmap.heatmap.template;

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

public abstract class AHeatMapTemplate extends LayoutConfiguration {

	protected AHeatMapRenderer heatMapRenderer;
	protected RecordCaptionRenderer recordCaptionRenderer;
	protected DimensionCaptionRenderer dimensionCaptionRenderer;
	protected RecordSelectionRenderer recordSelectionRenderer;
	protected DimensionSelectionRenderer dimensionSelectionRenderer;
	protected CaptionCageRenderer captionCageRenderer;

	protected GLHeatMap heatMap;

	protected ElementLayout heatMapLayout;
	protected ElementLayout barPlotLayout;

	public float minSelectedFieldHeight = 0.1f;
	// private float xOverheadToHeatMap;
	// private float yOverheadToHeatMap;

	protected RecordSpacing recordSpacing;

	private boolean renderAsTexture;

	public AHeatMapTemplate(GLHeatMap heatMap) {
		this(heatMap, false);
	}

	public AHeatMapTemplate(GLHeatMap heatMap, boolean renderAsTexture) {

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

	public void initRendererData() {

		if (renderAsTexture) {
			((HeatMapTextureRenderer) heatMapRenderer).init();
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
		// int recordIndex = heatMap.getRecordVA().indexOf(recordID);
		// if (recordIndex < 0)
		// return 0;
		return recordSpacing.getFieldHeight(recordID);
		// if (heatMap.getContentSelectionManager().checkStatus(
		// SelectionType.MOUSE_OVER, recordID)
		// || heatMap.getContentSelectionManager().checkStatus(
		// SelectionType.SELECTION, recordID))
		//
		// return contentSpacing.getSelectedFieldHeight();
		//
		// return contentSpacing.getNormalFieldHeight();
	}

	public float getElementWidth(int dimensionID) {
		return recordSpacing.getFieldWidth();
	}

	public float getMinSelectedFieldHeight() {
		return minSelectedFieldHeight;
	}

	public void setContentSpacing(RecordSpacing contentSpacing) {
		this.recordSpacing = contentSpacing;
	}
}
