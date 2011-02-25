package org.caleydo.core.view.opengl.layout;

/**
 * Container for layouts that are stacked on top of each other. The column is a {@link ElementLayout} and
 * contains other ElementLayouts. It can be nested into other containers
 * 
 * @author Alexander Lex
 */
public class Column
	extends LayoutContainer {

	public Column() {
		super();
	}

	public Column(String layoutName) {
		super(layoutName);
	}

	@Override
	protected void calculateTransforms(float bottom, float left, float top, float right) {

		float x;
		if (isLeftToRight)
			x = left;
		else
			x = right;

		for (ElementLayout element : elements) {
			if (isBottomUp) {

				if (element instanceof LayoutContainer) {
					((LayoutContainer) element).calculateTransforms(bottom, left, top, right);
				}
				element.setTransformX(x);
				element.setTransformY(bottom);

				bottom += element.getSizeScaledY();

			}
			else {
				bottom = top - element.getSizeScaledY();
				if (element instanceof LayoutContainer) {
					((LayoutContainer) element).calculateTransforms(bottom, left, top, right);
				}

				top -= element.getSizeScaledY();
				element.setTransformX(x);
				element.setTransformY(top);
			}
		}
	}

	@Override
	protected void calculateSubElementScales(float availableWidth, float availableHeight) {
		float actualWidth = 0;
		float actualHeight = 0;
		float totalHeight = 0;
		ElementLayout greedyElement = null;
		for (ElementLayout element : elements) {
			if (element.grabY) {
				if (greedyElement != null)
					throw new IllegalStateException("Specified more than one greedy element for " + this);
				greedyElement = element;
				continue;
			}
			element.calculateScales(availableWidth, availableHeight);
			// if an element is set in absolute size, the available size is already reduced by that value
			totalHeight += element.getSizeScaledY();
			if (!(!Float.isNaN(element.absoluteSizeY) || Integer.MIN_VALUE != element.pixelSizeY))
				actualHeight += element.getSizeScaledY();
			if (actualWidth < element.getSizeScaledX())
				actualWidth = element.getSizeScaledX();
		}
		if (greedyElement != null) {
			greedyElement.setAbsoluteSizeY(availableHeight - actualHeight);
			greedyElement.calculateScales(availableWidth, availableHeight - actualHeight);
			actualHeight = availableHeight;
		}

		if (isXDynamic)
			sizeScaledX = actualWidth;
		if (isYDynamic)
			sizeScaledY = totalHeight;
	}
}
