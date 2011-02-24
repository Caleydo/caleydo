package org.caleydo.core.view.opengl.layout;

import java.util.ArrayList;
import java.util.Iterator;

import javax.media.opengl.GL2;

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

	ArrayList<ElementLayout> elements;

	public LayoutContainer() {
		super();
		elements = new ArrayList<ElementLayout>();
	}

	public LayoutContainer(String layoutName) {
		super(layoutName);
		elements = new ArrayList<ElementLayout>();
	}

	@Override
	public void render(GL2 gl) {
		gl.glTranslatef(getTransformX(), getTransformY(), 0);
		super.render(gl);
		gl.glTranslatef(-getTransformX(), -getTransformY(), 0);
		for (ElementLayout element : elements) {

			if (element instanceof LayoutContainer) {
				element.render(gl);
			}
			else {
				gl.glTranslatef(element.getTransformX(), element.getTransformY(), 0);
				element.render(gl);
				gl.glTranslatef(-element.getTransformX(), -element.getTransformY(), 0);
			}
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

	protected abstract void calculateSubElementScales(float availableWidth, float availableHeight);

	protected abstract void calculateTransforms(float bottom, float left, float top, float right);

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

	@Override
	protected void updateSpacings(Template template) {
		for (ElementLayout element : elements) {
			element.updateSpacings(template);
		}
	}
}
