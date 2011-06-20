package org.caleydo.core.view.opengl.layout;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

/**
 * Class managing the entry point into the recursively specified layouts. Also this class is intended to be
 * sub-classed and the {@link #setStaticLayouts()} to be overridden, specifying static layouts if desired.
 * 
 * @author Alexander Lex
 */
public class LayoutTemplate {

	/** The entry point to the recursively defined layout */
	protected ElementLayout baseElementLayout;

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
	 * <p>
	 * Sets the static layouts that can be specified in a sub-class.
	 * </p>
	 * <p>
	 * For static layouts (for example for a particular view) the layouting should be done in a sub-class of
	 * ATemplate in this method. If the layout is generated dynamically, this typically should be empty.
	 * </p>
	 */
	public void setStaticLayouts() {
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

	public void setActive(boolean isActive) {
		if (this.isActive != isActive) {
			this.isActive = isActive;
			setStaticLayouts();
		}
	}
}
