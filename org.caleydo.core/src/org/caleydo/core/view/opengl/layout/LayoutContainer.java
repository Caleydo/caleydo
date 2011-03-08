package org.caleydo.core.view.opengl.layout;

import java.util.ArrayList;
import java.util.Iterator;

import javax.media.opengl.GL2;

/**
 * BaseClass for layouts which contain nested {@link ElementLayout}s.
 * 
 * @author Alexander Lex
 */
public abstract class LayoutContainer
	extends ElementLayout
	implements Iterable<ElementLayout> {

	/**
	 * Flag signaling whether the x-size of the container should be calculated as the sum of it's parts
	 * (true), or if some size indication (either scaled or not scaled) is given (false)
	 */
	protected boolean isXDynamic = false;

	/**
	 * Flag signaling whether the y-size of the container should be calculated as the sum of it's parts
	 * (true), or if some size indication (either scaled or not scaled) is given (false)
	 */
	protected boolean isYDynamic = false;

	protected boolean isBottomUp = true;
	protected boolean isLeftToRight = true;

	protected ArrayList<ElementLayout> elements;

	/**
	 * The currently available bottom distance for the layout. Use if only this sub-part of the layout is
	 * updated
	 */
	protected float bottom;
	/**
	 * The currently available top distance for the layout. Use if only this sub-part of the layout is updated
	 */
	protected float top;
	/**
	 * The currently available left distance for the layout. Use if only this sub-part of the layout is
	 * updated
	 */
	protected float left;
	/**
	 * The currently available right distance for the layout. Use if only this sub-part of the layout is
	 * updated
	 */
	protected float right;

	public LayoutContainer() {
		super();
		// elements = new ArrayList<ElementLayout>();
	}

	public LayoutContainer(String layoutName) {
		super(layoutName);
	}

	{
		elements = new ArrayList<ElementLayout>();
	}

	@Override
	public void render(GL2 gl) {
		super.render(gl);
		for (ElementLayout element : elements) {
			element.render(gl);
		}
	}

	/**
	 * Set flag signaling whether the x-size of the container should be calculated as the sum of it's parts
	 * (true), or if some size indication (either scaled or not scaled) is given (false)
	 */
	public void setXDynamic(boolean isXDynamic) {
		this.isXDynamic = isXDynamic;
	}

	/**
	 * Set flag signaling whether the y-size of the container should be calculated as the sum of it's parts
	 * (true), or if some size indication (either scaled or not scaled) is given (false)
	 */
	public void setYDynamic(boolean isYDynamic) {
		this.isYDynamic = isYDynamic;
	}

	/**
	 * Set flag signaling whether the content should be rendered from bottom to top (default, true) or from
	 * top to bottom (false)
	 * 
	 * @param isBottomUp
	 */
	public void setBottomUp(boolean isBottomUp) {
		this.isBottomUp = isBottomUp;
	}

	/**
	 * Set the flag signaling whether the content should be rendered from left to right (true, default) or
	 * from right to left (false)
	 */
	public void setLeftToRight(boolean isLeftToRight) {
		this.isLeftToRight = isLeftToRight;
	}

	/**
	 * Add an element to the container
	 * 
	 * @param renderParameter
	 */
	public void appendElement(ElementLayout renderParameter) {
		elements.add(renderParameter);
	}

	@Override
	public Iterator<ElementLayout> iterator() {
		return elements.iterator();
	}

	@Override
	public float getUnscalableElementHeight() {
		if (!isYDynamic)
			return super.getUnscalableElementHeight();
		else {
			float unscalableHeight = 0;
			for (ElementLayout element : elements) {
				unscalableHeight += element.getUnscalableElementHeight();
			}
			return unscalableHeight;
		}
	}

	@Override
	public float getUnscalableElementWidth() {
		if (!isXDynamic)
			return super.getUnscalableElementWidth();
		else {
			float unscalableWidth = 0;
			for (ElementLayout element : elements) {
				unscalableWidth += element.getUnscalableElementWidth();
			}
			return unscalableWidth;
		}
	}

	@Override
	public void updateSubLayout() {
		calculateScales(totalWidth, totalHeight);
		updateSpacings();
		calculateTransforms(bottom, left, top, right);
	}

	@Override
	public String toString() {
		String name;
		if (layoutName == null)
			name = layoutName;
		else
			name = super.toString();

		return ("Container " + name + " with " + elements.size() + " elements. height: " + sizeScaledY
			+ ", widht: " + sizeScaledX);
	}

	public int size() {
		return elements.size();
	}

	public void clear() {
		elements.clear();
	}

	// --------------------- End of Public Interface ---------------------

	@Override
	void calculateScales(float totalWidth, float totalHeight) {
		super.calculateScales(totalWidth, totalHeight);
		for (ElementLayout element : elements) {
			totalHeight -= element.getUnscalableElementHeight();
			totalWidth -= element.getUnscalableElementWidth();
		}

		float availableWidth, availableHeight;

		if (isXDynamic)
			availableWidth = totalWidth;

		else if (pixelSizeX != Integer.MIN_VALUE)
			availableWidth = pixelGLConverter.getGLWidthForPixelWidth(pixelSizeX);
		else if (!Float.isNaN(absoluteSizeX))
			availableWidth = absoluteSizeX;
		else
			availableWidth = totalWidth * ratioSizeX;

		if (isYDynamic)
			availableHeight = totalHeight;

		else if (pixelSizeY != Integer.MIN_VALUE)
			availableHeight = pixelGLConverter.getGLHeightForPixelHeight(pixelSizeY);
		else if (!Float.isNaN(absoluteSizeY))
			availableHeight = absoluteSizeY;
		else
			availableHeight = totalHeight * ratioSizeY;

		calculateSubElementScales(availableWidth, availableHeight);

	}

	protected abstract void calculateSubElementScales(float availableWidth, float availableHeight);

	protected void calculateTransforms(float bottom, float left, float top, float right) {
		this.bottom = bottom;
		this.left = left;
		this.top = top;
		this.right = right;
	}

	@Override
	protected void updateSpacings() {
		for (ElementLayout element : elements) {
			element.updateSpacings();
		}
	}
	
	public ArrayList<ElementLayout> getElements() {
		return elements;
	}
}
