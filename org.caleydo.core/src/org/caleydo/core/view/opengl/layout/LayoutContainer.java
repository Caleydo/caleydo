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
		elements = new ArrayList<ElementLayout>();
	}

	@Override
	public void render(GL2 gl) {
		for (ElementLayout element : elements) {

			if (element instanceof LayoutContainer) {
				// render the frame around the container
				gl.glTranslatef(element.getTransformX(), element.getTransformY(), 0);
				super.render(gl);
				gl.glTranslatef(-element.getTransformX(), -element.getTransformY(), 0);
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

		for (ElementLayout element : elements) {
			totalHeight -= element.getUnscalableElementHeight();
			totalWidth -= element.getUnscalableElementWidth();
		}

		float availableWidth, availableHeight;

		if (isXDynamic)
			availableWidth = totalWidth;
		else if (scaleX)
			availableWidth = totalWidth * sizeX;
		else
			availableWidth = sizeX;

		if (isYDynamic)
			availableHeight = totalHeight;
		else if (scaleY)
			availableHeight = totalHeight * sizeY;
		else
			availableHeight = sizeY;

		super.calculateScales(totalWidth, totalHeight);
		calculateSubElementScales(availableWidth, availableHeight);

	}

	@Override
	public float getUnscalableElementHeight() {
		if (!scaleY)
			return sizeY;
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
		if (!scaleX)
			return sizeX;
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
		return ("Container with " + elements.size() + " elements. height: " + sizeScaledY + ", widht: " + sizeScaledX);
	}

	@Override
	protected void updateSpacings(ATemplate template) {
		for (ElementLayout element : elements) {
			element.updateSpacings(template);
		}
	}
}
