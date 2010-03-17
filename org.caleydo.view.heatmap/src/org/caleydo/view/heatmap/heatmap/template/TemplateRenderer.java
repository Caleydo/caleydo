package org.caleydo.view.heatmap.heatmap.template;

import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.renderer.AContentRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.ARenderer;
import org.caleydo.view.heatmap.heatmap.renderer.ContentSpacing;
import org.caleydo.view.heatmap.heatmap.renderer.HeatMapRenderer;
import org.eclipse.core.runtime.Platform;

public class TemplateRenderer {

	ArrayList<RenderParameters> renderers;

	protected float SPACING = 0.01f;

	protected float spacing;

	private ATemplate template;

	IViewFrustum viewFrustum;
	GLHeatMap heatMap;
	float totalWidth;
	float totalHeight;

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

	public void clearRenderers() {
		renderers.clear();
	}

	public void frustumChanged() {

		viewFrustum = heatMap.getViewFrustum();
		totalWidth = viewFrustum.getRight() - viewFrustum.getLeft();
		totalHeight = viewFrustum.getTop() - viewFrustum.getBottom();

		spacing = totalHeight * SPACING;

		ContentSpacing contentSpacing = null;

		template.calculateScales(totalWidth, totalHeight);

		for (RenderParameters parameters : renderers) {

			ARenderer renderer = parameters.renderer;

			renderer.setLimits(parameters.sizeScaledX, parameters.sizeScaledY);
			if (renderer instanceof AContentRenderer) {
				if (contentSpacing == null) {
					contentSpacing = new ContentSpacing();

					int contentElements = heatMap.getContentVA().size();

					ContentSelectionManager selectionManager = heatMap
							.getContentSelectionManager();
					if (heatMap.isHideElements()) {
						for (int contentID : heatMap.getContentVA()) {
							if (selectionManager.checkStatus(
									GLHeatMap.SELECTION_HIDDEN, contentID))

								contentElements--;
						}
						// contentElements = totalElements
						// - heatMap.getContentSelectionManager()
						// .getNumberOfElements(
						// GLHeatMap.SELECTION_HIDDEN);
					}

					contentSpacing.calculateContentSpacing(contentElements,
							heatMap.getStorageVA().size(),
							parameters.sizeScaledX, parameters.sizeScaledY);
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
			gl.glTranslatef(parameters.transformScaledX,
					parameters.transformScaledY, 0);
			renderer.render(gl);
			gl.glTranslatef(-parameters.transformScaledX,
					-parameters.transformScaledY, 0);
		}
	}

	public Float getYCoordinateByContentIndex(int contentIndex) {
		boolean belowHM = false;
		float sizeOverhead = 0;
		float positionInHM = 0;
		for (RenderParameters parameters : template.verticalSpaceAllocations) {
			// RenderParameters parameters = renderPair.getSecond();
			ARenderer renderer = parameters.renderer;

			if (belowHM)
				sizeOverhead += parameters.sizeScaledY;

			if (parameters instanceof Row) {
				Row row = (Row) parameters;
				// boolean hmInRow = false;

				for (RenderParameters rowElements : row) {
					renderer = rowElements.renderer;
					if (renderer instanceof HeatMapRenderer) {
						// hmInRow = true;
						belowHM = true;
						positionInHM = ((HeatMapRenderer) renderer)
								.getYCoordinateByContentIndex(contentIndex);
					}
				}

			}
			if (renderer instanceof HeatMapRenderer) {
				belowHM = true;
				positionInHM = ((HeatMapRenderer) renderer)
						.getYCoordinateByContentIndex(contentIndex);
			}
		}
		return positionInHM;
	}
}
