package org.caleydo.view.heatmap.heatmap.template;

import org.caleydo.core.view.opengl.layout.ATemplate;
import org.caleydo.core.view.opengl.layout.LayoutParameters;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.renderer.CaptionCageRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.ContentCaptionRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.ContentSelectionRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.HeatMapRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.StorageCaptionRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.StorageSelectionRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.spacing.ContentSpacing;

public abstract class AHeatMapTemplate extends ATemplate {

	protected HeatMapRenderer heatMapRenderer;
	protected ContentCaptionRenderer contentCaptionRenderer;
	protected StorageCaptionRenderer storageCaptionRenderer;
	protected ContentSelectionRenderer contentSelectionRenderer;
	protected StorageSelectionRenderer storageSelectionRenderer;
	protected CaptionCageRenderer captionCageRenderer;

	protected GLHeatMap heatMap;

	protected LayoutParameters heatMapLayout;

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
		storageCaptionRenderer = new StorageCaptionRenderer(heatMap);
		storageCaptionRenderer.setContentSpacing(contentSpacing);
		contentSelectionRenderer = new ContentSelectionRenderer(heatMap);
		contentSelectionRenderer.setContentSpacing(contentSpacing);
		storageSelectionRenderer = new StorageSelectionRenderer(heatMap);
		storageSelectionRenderer.setContentSpacing(contentSpacing);
		captionCageRenderer = new CaptionCageRenderer(heatMap);
		captionCageRenderer.setContentSpacing(contentSpacing);
	}

	public Float getYCoordinateByContentIndex(int contentIndex) {
		return heatMapLayout.getTransformY()
				+ ((HeatMapRenderer) heatMapLayout.getRenderer())
						.getYCoordinateByContentIndex(contentIndex);
	}

	public Float getXCoordinateByStorageIndex(int storageIndex) {

		return heatMapLayout.getTransformX()
				+ ((HeatMapRenderer) heatMapLayout.getRenderer())
						.getXCoordinateByStorageIndex(storageIndex);
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

	public float getElementWidth(int storageID) {
		return contentSpacing.getFieldWidth();
	}

	public float getMinSelectedFieldHeight() {
		return minSelectedFieldHeight;
	}

	public void setContentSpacing(ContentSpacing contentSpacing) {
		this.contentSpacing = contentSpacing;
	}

}
