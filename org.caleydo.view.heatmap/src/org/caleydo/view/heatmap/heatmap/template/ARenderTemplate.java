package org.caleydo.view.heatmap.heatmap.template;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.view.heatmap.heatmap.CaptionCageRenderer;
import org.caleydo.view.heatmap.heatmap.ContentCaptionRenderer;
import org.caleydo.view.heatmap.heatmap.ContentSpacing;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.HeatMapRenderer;

public abstract class ARenderTemplate {

	protected HeatMapRenderer heatMapRenderer;

	protected boolean useContentCaptions = true;
	protected ContentCaptionRenderer contentCaptionRenderer;

	protected boolean useCaptionCage = false;
	protected CaptionCageRenderer captionCageRenderer;

	// TODO: add finals...
	protected float HEAT_MAP_X = 0.7f;
	protected float HEAT_MAP_Y = 1f;

	protected float heatMapX;
	protected float heatMapY;

	protected float CONTENT_CAPTION_X = 0.29f;
	protected float CONTENT_CAPTION_Y = HEAT_MAP_Y;

	protected float contentCaptionX;
	protected float contentCaptionY;

	protected float CAGE_X = 0.3f;
	protected float CAGE_Y = HEAT_MAP_Y;

	protected float cageX;
	protected float cageY;

	protected float SPACING = 0.01f;

	protected float spacing;

	IViewFrustum viewFrustum;
	GLHeatMap heatMap;
	float totalWidth;
	float totalHeight;

	public ARenderTemplate(GLHeatMap heatMap) {

		this.heatMap = heatMap;
	}

	public void initRenderers() {
		heatMapRenderer = new HeatMapRenderer(heatMap);
		if (useContentCaptions)
			contentCaptionRenderer = new ContentCaptionRenderer(heatMap);
		if (useCaptionCage)
			captionCageRenderer = new CaptionCageRenderer(heatMap);
	}

	public void frustumChanged() {

		viewFrustum = heatMap.getViewFrustum();
		totalWidth = viewFrustum.getRight() - viewFrustum.getLeft();
		totalHeight = viewFrustum.getTop() - viewFrustum.getBottom();

		heatMapX = totalWidth * HEAT_MAP_X;
		heatMapY = totalHeight * HEAT_MAP_Y;

		spacing = totalHeight * SPACING;

		heatMapRenderer.setLimits(heatMapX, heatMapY);
		ContentSpacing contentSpacing = new ContentSpacing();
		contentSpacing.calculateContentSpacing(heatMap.getContentVA().size(),
				heatMap.getStorageVA().size(), heatMapX, heatMapY);
		heatMapRenderer.setContentSpacing(contentSpacing);

		if (useContentCaptions) {
			contentCaptionX = totalWidth * CONTENT_CAPTION_X;
			contentCaptionY = totalHeight * CONTENT_CAPTION_Y;
			contentCaptionRenderer.setLimits(contentCaptionX, contentCaptionY);
			contentCaptionRenderer.setContentSpacing(contentSpacing);

		}

		if (useCaptionCage) {
			cageX = totalWidth * CAGE_X;
			cageY = totalHeight * CAGE_Y;
			captionCageRenderer.setLimits(cageX, cageY);
			captionCageRenderer.setContentSpacing(contentSpacing);
		}

	}

	public abstract void render(GL gl);

}
