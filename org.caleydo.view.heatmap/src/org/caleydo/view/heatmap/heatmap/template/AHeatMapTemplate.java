package org.caleydo.view.heatmap.heatmap.template;

import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutTemplate;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.renderer.CaptionCageRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.DimensionCaptionRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.DimensionSelectionRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.HeatMapRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.RecordCaptionRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.RecordSelectionRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.spacing.ContentSpacing;

public abstract class AHeatMapTemplate extends LayoutTemplate {
	
	protected HeatMapRenderer heatMapRenderer;
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

	ContentSpacing contentSpacing;
	

	public AHeatMapTemplate(GLHeatMap heatMap) {
		this.heatMap = heatMap;
		contentSpacing = new ContentSpacing(heatMap);

		heatMapRenderer = new HeatMapRenderer(heatMap);
		heatMapRenderer.setContentSpacing(contentSpacing);
		
		recordCaptionRenderer = new RecordCaptionRenderer(heatMap);
		recordCaptionRenderer.setContentSpacing(contentSpacing);
		dimensionCaptionRenderer = new DimensionCaptionRenderer(heatMap);
		dimensionCaptionRenderer.setContentSpacing(contentSpacing);
		recordSelectionRenderer = new RecordSelectionRenderer(heatMap);
		recordSelectionRenderer.setContentSpacing(contentSpacing);
		dimensionSelectionRenderer = new DimensionSelectionRenderer(heatMap);
		dimensionSelectionRenderer.setContentSpacing(contentSpacing);
		captionCageRenderer = new CaptionCageRenderer(heatMap);
		captionCageRenderer.setContentSpacing(contentSpacing);
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
		return contentSpacing.getFieldHeight(recordID);
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
		return contentSpacing.getFieldWidth();
	}

	public float getMinSelectedFieldHeight() {
		return minSelectedFieldHeight;
	}

	public void setContentSpacing(ContentSpacing contentSpacing) {
		this.contentSpacing = contentSpacing;
	}

	
}
