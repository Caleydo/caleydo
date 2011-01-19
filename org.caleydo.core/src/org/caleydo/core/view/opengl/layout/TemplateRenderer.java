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

		for (ElementLayout elementLayout : template.getVerticalLayoutElements()) {

			updateSpacings(elementLayout);
		}

	}

	private void updateSpacings(ElementLayout layout) {

		if (layout instanceof Row) {
			for (ElementLayout nestedLayout : (Row) layout) {
				updateSpacings(nestedLayout);
			}
		}
		else if (layout instanceof RenderableLayoutElement) {
			ARenderer renderer = ((RenderableLayoutElement) layout).getRenderer();
			if (renderer == null)
				return;
			renderer.setLimits(layout.getSizeScaledX(), layout.getSizeScaledY());
			renderer.updateSpacing(template, layout);
		}
		else 
		{
//			throw new IllegalStateException("unknown layout type" + layout);
		}

	}

	public void render(GL2 gl) {
		// FIXME: this should be called externally
		frustumChanged();
		for (ElementLayout layout : template.getVerticalLayoutElements()) {
			recursiveRender(gl, layout);

		}
	}

	private void recursiveRender(GL2 gl, ElementLayout element) {
		if (element instanceof Row) {
			for (ElementLayout nestedLayout : (Row) element) {
				recursiveRender(gl, nestedLayout);
			}
		}
		else if (element instanceof RenderableLayoutElement) {
			ARenderer renderer = ((RenderableLayoutElement)element).getRenderer();
			if (renderer == null)
				return;
			gl.glTranslatef(element.getTransformScaledX(), element.getTransformScaledY(), 0);
			renderer.render(gl);
			gl.glTranslatef(-element.getTransformScaledX(), -element.getTransformScaledY(), 0);
		}
	}
}
