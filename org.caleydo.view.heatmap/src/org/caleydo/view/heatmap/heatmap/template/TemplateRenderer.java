package org.caleydo.view.heatmap.heatmap.template;

import java.util.ArrayList;

import javax.media.opengl.GL2;

import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.layout.ATemplate;
import org.caleydo.view.heatmap.heatmap.layout.RenderParameters;
import org.caleydo.view.heatmap.heatmap.renderer.AContentRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.ARenderer;
import org.caleydo.view.heatmap.heatmap.renderer.HeatMapRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.spacing.ContentSpacing;

public class TemplateRenderer {

	protected float spacing;

	private ATemplate template;

	ViewFrustum viewFrustum;
	// GLHeatMap heatMap;
	float totalWidth;
	float totalHeight;

	public TemplateRenderer(ViewFrustum viewFrustum) {

		this.viewFrustum = viewFrustum;
	}

	public void setTemplate(ATemplate template) {

		this.template = template;
		// template.setTemplateRenderer(this);
		template.setParameters();
	}

	// public void clearRenderers() {
	// template.clearRenderers();
	// }

	public void frustumChanged() {

		totalWidth = viewFrustum.getRight() - viewFrustum.getLeft();
		totalHeight = viewFrustum.getTop() - viewFrustum.getBottom();

		spacing = totalHeight * ATemplate.SPACING;

		template.recalculateSpacings();
		template.calculateScales(totalWidth, totalHeight);

		for (RenderParameters parameters : template.getRenderParameters()) {

			ARenderer renderer = parameters.getRenderer();

			renderer.setLimits(parameters.getSizeScaledX(), parameters.getSizeScaledY());
			renderer.updateSpacing(template, parameters);
		}

	}

	public void render(GL2 gl) {
		// FIXME: this should be called externally
		frustumChanged();
		for (RenderParameters parameters : template.getRenderParameters()) {
			ARenderer renderer = parameters.getRenderer();
			gl.glTranslatef(parameters.getTransformScaledX(),
					parameters.getTransformScaledY(), 0);
			renderer.render(gl);
			gl.glTranslatef(-parameters.getTransformScaledX(),
					-parameters.getTransformScaledY(), 0);
		}
	}

}
