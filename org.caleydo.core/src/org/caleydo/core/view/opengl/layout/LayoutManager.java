package org.caleydo.core.view.opengl.layout;

import javax.media.opengl.GL2;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

/**
 * The LayoutManager is responsible for rendering all the elements specified in its set {@link #template}. It
 * contains a reference to the view frustum and initializes the calculation of spacing once the view frustum
 * is changed.
 * 
 * @author Alexander Lex
 */
public class LayoutManager {

	private ViewFrustum viewFrustum;
	private float totalWidth;
	private float totalHeight;

	LayoutConfiguration layoutConfiguration;

	/** The entry point to the recursively defined layout */
	protected ElementLayout baseElementLayout;

	protected float fontScaling = GeneralRenderStyle.SMALL_FONT_SCALING_FACTOR;

	protected boolean isActive;

	protected PixelGLConverter pixelGLConverter;

	public LayoutManager(ViewFrustum viewFrustum, PixelGLConverter pixelGLConverter) {
		if (viewFrustum == null || pixelGLConverter == null)
			throw new IllegalArgumentException("Arguments viewFrustum or pixelGLConverter were null");
		this.viewFrustum = viewFrustum;
		this.pixelGLConverter = pixelGLConverter;
	}

	public PixelGLConverter getPixelGLConverter() {
		return pixelGLConverter;
	}

	public void setViewFrustum(ViewFrustum viewFrustum) {
		this.viewFrustum = viewFrustum;

		updateLayout();
	}

	/**
	 * Set a static layout configuration which contains an ElementLayout which is accessible using
	 * {@link LayoutConfiguration#getBaseElementLayout()}, which is set as the {@link #baseElementLayout} of
	 * this {@link LayoutManager}.
	 */
	public void setStaticLayoutConfiguration(LayoutConfiguration layoutConfiguration) {
		this.layoutConfiguration = layoutConfiguration;
		layoutConfiguration.setStaticLayouts();
		setBaseElementLayout(layoutConfiguration.getBaseElementLayout());
	}

	/**
	 * Recursively update the whole layout of this renderer. The dimensions are extracted from the viewFrustum
	 * provided in the constructor. Since the viewFrustum is passed by reference, changes to the viewFrustum,
	 * e.g. by a reshape of the window are reflected. FIXME: this should be split into two different methods,
	 * one for updating the layout due to size changes, and one for updating the layout through to new
	 * elements
	 */
	public void updateLayout() {

		totalWidth = viewFrustum.getRight() - viewFrustum.getLeft();
		totalHeight = viewFrustum.getTop() - viewFrustum.getBottom();

		// template.getBaseLayoutElement().destroy();

		// if (layoutConfiguration != null) {
		// layoutConfiguration.setStaticLayouts();
		// setBaseElementLayout(layoutConfiguration.getBaseElementLayout());
		// }
		// should we do this here? we could integrate this with another traversal
		baseElementLayout.setLayoutManager(this);
		calculateScales(viewFrustum.getLeft(), viewFrustum.getBottom(), totalWidth, totalHeight);

		baseElementLayout.updateSpacings();
	}

	/**
	 * Recursively render the layout of all elements
	 * 
	 * @param gl
	 */
	public void render(GL2 gl) {
		baseElementLayout.render(gl);
	}

	/**
	 * Calculate the size and positions of the layout elements in the template
	 * 
	 * @param totalWidth
	 * @param totalHeight
	 */
	void calculateScales(float bottom, float left, float totalWidth, float totalHeight) {

		baseElementLayout.setTranslateX(left);
		baseElementLayout.setTranslateY(bottom);
		baseElementLayout.calculateScales(totalWidth, totalHeight);
		if (baseElementLayout instanceof ALayoutContainer)
			((ALayoutContainer) baseElementLayout).calculateTransforms(bottom, left, totalHeight, totalWidth);
	}

	/**
	 * Set the base element layout - which is the topmost layout containing all other element layouts
	 * 
	 * @param baseElementLayout
	 */
	public void setBaseElementLayout(ElementLayout baseElementLayout) {
		this.baseElementLayout = baseElementLayout;
	}

}
