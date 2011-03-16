package org.caleydo.core.view.opengl.layout;

import java.util.ArrayList;

/**
 * Container for layouts that are rendered side by side. The row is a {@link ElementLayout} and contains other
 * ElementLayouts. It can be nested into other containers
 * 
 * @author Alexander Lex
 */
public class Row
	extends LayoutContainer {

	public Row() {
		super();
	}

	public Row(String layoutName) {
		super(layoutName);
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
	public float getUnscalableElementHeight() {
		if (!isYDynamic)
			return super.getUnscalableElementHeight();
		else {
			float maxHeight = 0;
			for (ElementLayout element : elements) {
				float elementHeight = element.getUnscalableElementHeight();
				if (elementHeight > maxHeight)
					maxHeight = elementHeight;

			}
			return maxHeight;
		}
	}

	/**
	 * <p>
	 * Set flag signaling whether the y-size of the container should be set to the largest size in y of its
	 * sub-elements (true), or if some size indication (either scaled or not scaled) is given (false).
	 * </p>
	 * <p>
	 * Notice that for if this is set to true, sub-elements must not have a ratioSize of 1 (which is the
	 * default initialization). The reason for this is that it makes no sense, and catching it prevents
	 * errors.
	 */
	@Override
	public void setYDynamic(boolean isYDynamic) {
		super.setYDynamic(isYDynamic);
	}

	@Override
	protected void calculateTransforms(float bottom, float left, float top, float right) {
		super.calculateTransforms(bottom, left, top, right);

		float y;
		if (isBottomUp)
			y = bottom;
		else
			y = top;

		// FIXME this is probably wrong for nestsed elements look at Column on how to do it correctly
		for (ElementLayout element : elements) {
			if (element instanceof LayoutContainer) {
				((LayoutContainer) element).calculateTransforms(bottom, left, top, right);
			}

			if (isLeftToRight) {
				element.setTranslateX(left);
				element.setTranslateY(y);
				left += element.getSizeScaledX();
			}
			else {
				element.setTranslateX(right);
				element.setTranslateY(y);
				right -= element.getSizeScaledX();
			}
		}
	}

	@Override
	void calculateScales(float totalWidth, float totalHeight) {
		super.calculateScales(totalWidth, totalHeight);

		float availableWidth = getSizeScaledX();
		float availableHeight = getSizeScaledY();

		if (isXDynamic)
			availableWidth = totalWidth;

		if (isYDynamic)
			availableHeight = totalHeight;

		float highestElement = 0;
		for (ElementLayout element : elements) {
			float tempHeight = element.getUnscalableElementHeight();
			if (tempHeight > highestElement)
				highestElement = tempHeight;

			availableWidth -= element.getUnscalableElementWidth();
		}
		availableHeight -= highestElement;
		calculateSubElementScales(availableWidth, availableHeight);
	}

	@Override
	protected void calculateSubElementScales(float availableWidth, float availableHeight) {

		float dynamicWidth = 0;
		float totalWidth = 0;
		// the largest height of any element in the row (only relevant if isZDynamic is true)
		float largestHeight = 0;

		ArrayList<ElementLayout> greedyElements = new ArrayList<ElementLayout>();

		for (ElementLayout element : elements) {
			if (element.grabX) {
				greedyElements.add(element);

				continue;
			}
			if (isYDynamic && !element.isHeightStatic() && element.ratioSizeY == 1)
				throw new IllegalStateException("Specified column " + this
					+ " as dynamic in y, but the sub-element " + element
					+ " has a ratioSize of 1, which is illegal.");
			element.calculateScales(availableWidth, availableHeight);
			totalWidth += element.getSizeScaledX();
			// if an element is set in absolute size, the available size is already reduced by that value

			dynamicWidth += element.getSizeScaledX() - element.getUnscalableElementWidth();
			if (largestHeight < element.getSizeScaledY())
				largestHeight = element.getSizeScaledY();

		}
		if (greedyElements.size() > 0) {
			float remainingSpace = availableWidth - dynamicWidth;
			float greedyWidth = remainingSpace / greedyElements.size();
			for (ElementLayout greedyElement : greedyElements) {

				greedyElement.setAbsoluteSizeX(greedyWidth);
				// the first argument is irrelevant, since this is set static
				greedyElement.calculateScales(greedyWidth, availableHeight);
			}
			dynamicWidth = availableWidth;
		}
		if (isXDynamic)
			sizeScaledX = totalWidth;
		if (isYDynamic)
			sizeScaledY = largestHeight;
	}

}
