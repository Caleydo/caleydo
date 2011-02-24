package org.caleydo.core.view.opengl.layout;

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
	protected void calculateTransforms(float bottom, float left, float top, float right) {

		float y;
		if (isBottomUp)
			y = bottom;
		else
			y = top;

		for (ElementLayout element : elements) {
			if (element instanceof LayoutContainer) {
				((LayoutContainer) element).calculateTransforms(bottom, left, top, right);
			}
			// else {
			if (isLeftToRight) {
				element.setTransformX(left);
				element.setTransformY(y);
				left += element.getSizeScaledX();
			}
			else {
				element.setTransformX(right);
				element.setTransformY(y);
				right -= element.getSizeScaledX();
			}
			// }
		}
	}

	@Override
	protected void calculateSubElementScales(float availableWidth, float availableHeight) {

		float actualWidth = 0;
		float totalWidth = 0;
		float actuahHeight = 0;
		ElementLayout greedyElement = null;
		for (ElementLayout element : elements) {
			if (element.grabX) {
				if (greedyElement != null)
					throw new IllegalStateException("Specified more than one greedy element for " + this);
				greedyElement = element;
				continue;
			}
			element.calculateScales(availableWidth, availableHeight);
			totalWidth += element.getSizeScaledX();
			// if an element is set in absolute size, the available size is already reduced by that value
			if (!(!Float.isNaN(element.absoluteSizeX) || Integer.MIN_VALUE != element.pixelSizeX))
				actualWidth += element.getSizeScaledX();
			if (actuahHeight < element.getSizeScaledY())
				actuahHeight = element.getSizeScaledY();

		}
		if (greedyElement != null) {
			greedyElement.setAbsoluteSizeX(availableWidth - actualWidth);
			greedyElement.calculateScales(availableWidth - actualWidth, availableHeight);
			actualWidth = availableWidth;
		}
		if (isXDynamic)
			sizeScaledX = totalWidth;
		if (isYDynamic)
			sizeScaledY = actuahHeight;
	}

}
