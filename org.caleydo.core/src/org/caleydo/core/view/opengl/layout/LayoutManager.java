package org.caleydo.core.view.opengl.layout;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * The LayoutManager is responsible for rendering all the elements specified in its set {@link #template}.
 * It contains a reference to the view frustum and initializes the calculation of spacing once the view
 * frustum is changed.
 * 
 * @author Alexander Lex
 */
public class LayoutManager {

	private LayoutTemplate template;

	private ViewFrustum viewFrustum;
	private float totalWidth;
	private float totalHeight;

	public LayoutManager(ViewFrustum viewFrustum) {
		this.viewFrustum = viewFrustum;
	}
	
	public void setViewFrustum(ViewFrustum viewFrustum) {
		this.viewFrustum = viewFrustum;
		if(template != null)
			updateLayout();
	}

	public void setTemplate(LayoutTemplate template) {
		this.template = template;
		template.setStaticLayouts();
	}

	/**
	 * Recursively update the whole layout of this renderer. The dimensions are extracted from the viewFrustum
	 * provided in the constructor. Since the viewFrustum is passed by reference, changes to the viewFrustum,
	 * e.g. by a reshape of the window are reflected.
	 */
	public void updateLayout() {
		totalWidth = viewFrustum.getRight() - viewFrustum.getLeft();
		totalHeight = viewFrustum.getTop() - viewFrustum.getBottom();

		template.setStaticLayouts();
		template.calculateScales(0, 0, totalWidth, totalHeight);

		template.getBaseLayoutElement().updateSpacings();
	}

	/**
	 * Recursively render the layout of all elements
	 * 
	 * @param gl
	 */
	public void render(GL2 gl) {
		template.getBaseLayoutElement().render(gl);
	}
}
