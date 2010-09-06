package org.caleydo.view.heatmap.heatmap.template;

import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.renderer.AContentRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.ARenderer;
import org.caleydo.view.heatmap.heatmap.renderer.HeatMapRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.spacing.ContentSpacing;

public class TemplateRenderer {

	ArrayList<RenderParameters> renderers;

	protected float SPACING = 0.01f;

	protected float spacing;

	private ATemplate template;

	ViewFrustum viewFrustum;
	GLHeatMap heatMap;
	float totalWidth;
	float totalHeight;

	private RenderParameters heatMapLayout;
	// private float xOverheadToHeatMap;
	// private float yOverheadToHeatMap;

	ContentSpacing contentSpacing = null;

	public TemplateRenderer(GLHeatMap heatMap) {

		this.heatMap = heatMap;
		renderers = new ArrayList<RenderParameters>();
	}

	public void setTemplate(ATemplate template) {

		this.template = template;
		template.setTemplateRenderer(this);
		template.setParameters();
	}

	public void addRenderer(RenderParameters parameters) {
		renderers.add(parameters);
	}

	public void addHeatMapLayout(RenderParameters heatMapLayout) {
		this.heatMapLayout = heatMapLayout;
	}

	public void clearRenderers() {
		renderers.clear();
	}

	public void frustumChanged() {

		viewFrustum = heatMap.getViewFrustum();
		totalWidth = viewFrustum.getRight() - viewFrustum.getLeft();
		totalHeight = viewFrustum.getTop() - viewFrustum.getBottom();

		spacing = totalHeight * SPACING;

		contentSpacing = null;

		template.recalculateSpacings();
		template.calculateScales(totalWidth, totalHeight);

		for (RenderParameters parameters : renderers) {

			ARenderer renderer = parameters.renderer;

			renderer.setLimits(parameters.sizeScaledX, parameters.sizeScaledY);
			if (renderer instanceof AContentRenderer) {
				if (contentSpacing == null) {
					contentSpacing = new ContentSpacing(heatMap);

					int contentElements = heatMap.getContentVA().size();

					ContentSelectionManager selectionManager = heatMap
							.getContentSelectionManager();
					if (heatMap.isHideElements()) {

						contentElements -= selectionManager
								.getNumberOfElements(GLHeatMap.SELECTION_HIDDEN);
					}

					contentSpacing.calculateContentSpacing(contentElements, heatMap
							.getStorageVA().size(), parameters.sizeScaledX,
							parameters.sizeScaledY);

				}
				((AContentRenderer) renderer).setContentSpacing(contentSpacing);
			}
		}

	}

	public void render(GL gl) {
		// FIXME: this should be called externally
		frustumChanged();
		for (RenderParameters parameters : renderers) {
			ARenderer renderer = parameters.renderer;
			gl.glTranslatef(parameters.transformScaledX, parameters.transformScaledY, 0);
			renderer.render(gl);
			gl.glTranslatef(-parameters.transformScaledX, -parameters.transformScaledY, 0);
		}
	}

	public Float getYCoordinateByContentIndex(int contentIndex) {
		return heatMapLayout.transformY
				+ ((HeatMapRenderer) heatMapLayout.renderer)
						.getYCoordinateByContentIndex(contentIndex);
	}

	public Float getXCoordinateByStorageIndex(int storageIndex) {

		return heatMapLayout.transformX
				+ ((HeatMapRenderer) heatMapLayout.renderer)
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
}
