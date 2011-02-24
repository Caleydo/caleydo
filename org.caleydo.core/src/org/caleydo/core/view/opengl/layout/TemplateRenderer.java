package org.caleydo.core.view.opengl.layout;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * The TemplateRenderer is responsible for rendering all the elements specified in its set {@link #template}.
 * It contains a reference to the view frustum and initializes the calculation of spacing once the view
 * frustum is changed.
 * 
 * @author Alexander Lex
 */
public class TemplateRenderer {

	/** Flag for showing debug frames for container layout elements */
	public static final boolean DEBUG_CONTAINERS = false;
	/** Flag for showing debug frames for actual layout elements */
	public static final boolean DEBUG_ELEMENTS = true;

	// protected float spacing;

	private ATemplate template;

	ViewFrustum viewFrustum;
	float totalWidth;
	float totalHeight;

	public TemplateRenderer(ViewFrustum viewFrustum) {

		this.viewFrustum = viewFrustum;
	}

	public void setTemplate(ATemplate template) {

		this.template = template;
		template.setParameters();
	}

	public void updateLayout() {

		totalWidth = viewFrustum.getRight() - viewFrustum.getLeft();
		totalHeight = viewFrustum.getTop() - viewFrustum.getBottom();

		// spacing = totalHeight * ATemplate.SPACING;

		template.recalculateSpacings();
		template.calculateScales(0, 0, totalWidth, totalHeight);

		updateSpacings();

	}

	private void updateSpacings() {
		template.getBaseLayoutElement().updateSpacings(template);
	}

	public void render(GL2 gl) {
		// FIXME: this should be called externally
//		frustumChanged();
		gl.glTranslatef(template.getBaseLayoutElement().getTransformX(), template.getBaseLayoutElement()
			.getTransformY(), 0);
		template.getBaseLayoutElement().render(gl);
		gl.glTranslatef(-template.getBaseLayoutElement().getTransformX(), -template.getBaseLayoutElement()
			.getTransformY(), 0);

	}

}
