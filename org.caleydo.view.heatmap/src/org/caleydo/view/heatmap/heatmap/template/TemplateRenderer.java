package org.caleydo.view.heatmap.heatmap.template;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.renderer.ARenderer;
import org.caleydo.view.heatmap.heatmap.renderer.CaptionCageRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.ContentCaptionRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.ContentSpacing;
import org.caleydo.view.heatmap.heatmap.renderer.HeatMapRenderer;

public class TemplateRenderer {

	protected HeatMapRenderer heatMapRenderer;
	RenderParameters heatMapParameters;

	protected ContentCaptionRenderer contentCaptionRenderer;
	RenderParameters contentCaptionParameters;

	protected CaptionCageRenderer captionCageRenderer;
	RenderParameters captionCageParameters;

	// TODO: add finals...
	// protected float HEAT_MAP_X = 0.7f;
	// protected float HEAT_MAP_Y = 1f;
	//
	// protected float heatMapX;
	// protected float heatMapY;
	//
	// private float heatMapTransformX = 0;
	// private float heatMapTransformY = 0;
	//
	// protected float CONTENT_CAPTION_X = 0.29f;
	// protected float CONTENT_CAPTION_Y = HEAT_MAP_Y;
	//
	// protected float contentCaptionX;
	// protected float contentCaptionY;
	//
	// private float contentCaptionTransformX = 0;
	// private float contentCaptionTransformY = 0;
	//
	// protected float CAGE_X = 0.3f;
	// protected float CAGE_Y = HEAT_MAP_Y;
	//
	// protected float cageX;
	// protected float cageY;

	protected float SPACING = 0.01f;

	protected float spacing;

	private ATemplate template;

	IViewFrustum viewFrustum;
	GLHeatMap heatMap;
	float totalWidth;
	float totalHeight;

	public TemplateRenderer(GLHeatMap heatMap) {

		this.heatMap = heatMap;
	}

	public void setTemplate(ATemplate template) {
		this.template = template;
		template.setTemplateRenderer(this);
		template.setParameters();
		initRenderers();
	}

	public void initRenderers() {
		if (heatMapParameters != null)
			heatMapRenderer = new HeatMapRenderer(heatMap);
		if (contentCaptionParameters != null)
			contentCaptionRenderer = new ContentCaptionRenderer(heatMap);
		if (captionCageParameters != null)
			captionCageRenderer = new CaptionCageRenderer(heatMap);
	}

	public void frustumChanged() {

		viewFrustum = heatMap.getViewFrustum();
		totalWidth = viewFrustum.getRight() - viewFrustum.getLeft();
		totalHeight = viewFrustum.getTop() - viewFrustum.getBottom();

		heatMapParameters.calculateScales(totalWidth, totalHeight);

		spacing = totalHeight * SPACING;

		ContentSpacing contentSpacing = new ContentSpacing();
		contentSpacing.calculateContentSpacing(heatMap.getContentVA().size(),
				heatMap.getStorageVA().size(), heatMapParameters.sizeScaledX,
				heatMapParameters.sizeScaledY);

		if (heatMapParameters != null) {
			heatMapRenderer.setLimits(heatMapParameters.sizeScaledX,
					heatMapParameters.sizeScaledY);

			heatMapRenderer.setContentSpacing(contentSpacing);
		}
		if (contentCaptionParameters != null) {

			contentCaptionParameters.calculateScales(totalWidth, totalHeight);
			contentCaptionRenderer.setLimits(
					contentCaptionParameters.sizeScaledX,
					contentCaptionParameters.sizeScaledY);
			contentCaptionRenderer.setContentSpacing(contentSpacing);

		}

		if (captionCageParameters != null) {
			captionCageParameters.calculateScales(totalWidth, totalHeight);
			captionCageRenderer.setLimits(captionCageParameters.sizeScaledX,
					captionCageParameters.sizeScaledY);
			captionCageRenderer.setContentSpacing(contentSpacing);
		}

	}

	public void render(GL gl) {
		// FIXME: this should be called externally
		frustumChanged();

		// GLHelperFunctions.drawViewFrustum(gl, viewFrustum);
		if (heatMapParameters != null)
			renderTempalte(gl, heatMapRenderer, heatMapParameters);
		if (contentCaptionParameters != null)
			renderTempalte(gl, contentCaptionRenderer, contentCaptionParameters);
		if (captionCageParameters != null)
			renderTempalte(gl, captionCageRenderer, captionCageParameters);
	}

	private void renderTempalte(GL gl, ARenderer renderer,
			RenderParameters parameters) {
		gl.glTranslatef(parameters.transformScaledX,
				parameters.transformScaledY, 0);
		renderer.render(gl);
		gl.glTranslatef(-parameters.transformScaledX,
				-parameters.transformScaledY, 0);
	}

}
