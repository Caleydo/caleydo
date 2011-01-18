package org.caleydo.core.view.opengl.layout;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.camera.ViewFrustum;

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

		for (LayoutParameters parameters : template.getRenderParameters()) {

			updateSpacings(parameters);
		}

	}

	private void updateSpacings(LayoutParameters layout) {

		if (layout instanceof Row) {
			for (LayoutParameters nestedLayout : (Row) layout) {
				updateSpacings(nestedLayout);
			}
		}
		else {
			ARenderer renderer = layout.getRenderer();
			if (renderer == null)
				return;
			renderer.setLimits(layout.getSizeScaledX(), layout.getSizeScaledY());
			renderer.updateSpacing(template, layout);
		}

	}

	public void render(GL2 gl) {
		// FIXME: this should be called externally
		frustumChanged();
		for (LayoutParameters layout : template.getRenderParameters()) {
			recursiveRender(gl, layout);

		}
	}

	private void recursiveRender(GL2 gl, LayoutParameters layout) {
		if (layout instanceof Row) {
			for (LayoutParameters nestedLayout : (Row) layout) {
				recursiveRender(gl, nestedLayout);
			}
		}
		else {
			ARenderer renderer = layout.getRenderer();
			if (renderer == null)
				return;
			gl.glTranslatef(layout.getTransformScaledX(), layout.getTransformScaledY(), 0);
			renderer.render(gl);
			gl.glTranslatef(-layout.getTransformScaledX(), -layout.getTransformScaledY(), 0);
		}
	}
}
