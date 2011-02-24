package org.caleydo.core.view.opengl.layout;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

public abstract class ATemplate {

	// super(heatMap);
	// initRenderers();

	public static final float SPACING = 0.01f;
	// protected ArrayList<ElementLayout> rendererParameters;

	protected ElementLayout baseElementLayout;

	// protected TemplateRenderer templateRenderer;

	private float yOverhead;

	protected float fontScaling = GeneralRenderStyle.SMALL_FONT_SCALING_FACTOR;

	protected boolean isActive;
	
	protected PixelGLConverter pixelGLConverter;
	
	public void setPixelGLConverter(PixelGLConverter pixelGLConverter) {
		this.pixelGLConverter = pixelGLConverter;
	}

	public ElementLayout getBaseLayoutElement() {
		return baseElementLayout;
	}

	/**
	 * For static layouts (for example for a particular view) the layouting should be done in a sub-class of
	 * ATemplate in this method. If the layout is generated dynamically, this typically should be empty.
	 */
	abstract public void setParameters();

	/**
	 * Calculate the size and positions of the layout elements in the template
	 * 
	 * @param totalWidth
	 * @param totalHeight
	 */
	public void calculateScales(float bottom, float left, float totalWidth, float totalHeight) {

//		baseElementLayout.setTransformX(left);
//		baseElementLayout.setTransformY(bottom);
		baseElementLayout.calculateScales(totalWidth, totalHeight);
		if (baseElementLayout instanceof LayoutContainer)
			((LayoutContainer) baseElementLayout).calculateTransforms(bottom, left, totalHeight, totalWidth);
	}

	/**
	 * Set the base element layout - which is the topmost layout containing all other element layouts
	 * 
	 * @param baseElementLayout
	 */
	public void setBaseElementLayout(ElementLayout baseElementLayout) {
		this.baseElementLayout = baseElementLayout;
	}

	/**
	 * 
	 */
	public void recalculateSpacings() {
		setParameters();
	}

	public float getYOverhead() {
		return yOverhead;
	}

	public void setActive(boolean isActive) {
		if (this.isActive != isActive) {
			this.isActive = isActive;
			recalculateSpacings();
		}
	}

	public float getFontScalingFactor() {
		return fontScaling;
	}
}
