package org.caleydo.view.heatmap.heatmap.template;

import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutTemplate;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.renderer.CaptionCageRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.ContentCaptionRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.ContentSelectionRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.HeatMapRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.DimensionCaptionRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.DimensionSelectionRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.spacing.ContentSpacing;

public abstract class AHeatMapTemplate extends LayoutTemplate {

	
	protected HeatMapRenderer heatMapRenderer;
	protected ContentCaptionRenderer contentCaptionRenderer;
	protected DimensionCaptionRenderer dimensionCaptionRenderer;
	protected ContentSelectionRenderer contentSelectionRenderer;
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
		
		contentCaptionRenderer = new ContentCaptionRenderer(heatMap);
		contentCaptionRenderer.setContentSpacing(contentSpacing);
		dimensionCaptionRenderer = new DimensionCaptionRenderer(heatMap);
		dimensionCaptionRenderer.setContentSpacing(contentSpacing);
		contentSelectionRenderer = new ContentSelectionRenderer(heatMap);
		contentSelectionRenderer.setContentSpacing(contentSpacing);
		dimensionSelectionRenderer = new DimensionSelectionRenderer(heatMap);
		dimensionSelectionRenderer.setContentSpacing(contentSpacing);
		captionCageRenderer = new CaptionCageRenderer(heatMap);
		captionCageRenderer.setContentSpacing(contentSpacing);
	}

	public Float getYCoordinateByContentIndex(int contentIndex) {
		return heatMapLayout.getTranslateY()
				+ ((HeatMapRenderer) heatMapLayout.getRenderer())
						.getYCoordinateByContentIndex(contentIndex);
	}

	public Float getXCoordinateByDimensionIndex(int dimensionIndex) {

		return heatMapLayout.getTranslateX()
				+ ((HeatMapRenderer) heatMapLayout.getRenderer())
						.getXCoordinateByDimensionIndex(dimensionIndex);
	}

	public float getElementHeight(int contentID) {
		// int contentIndex = heatMap.getContentVA().indexOf(contentID);
		// if (contentIndex < 0)
		// return 0;
		return contentSpacing.getFieldHeight(contentID);
		// if (heatMap.getContentSelectionManager().checkStatus(
		// SelectionType.MOUSE_OVER, contentID)
		// || heatMap.getContentSelectionManager().checkStatus(
		// SelectionType.SELECTION, contentID))
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
