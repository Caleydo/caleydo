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
		super.calculateTransforms(bottom, left, top, right);

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

		float dynamicWidth = 0;
		float totalWidth = 0;
		// the largest height of any element in the row (only relevant if isZDynamic is true)
		float largestHeight = 0;
		
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
			if (!element.isHeightStatic())
				dynamicWidth += element.getSizeScaledX();
			if (largestHeight < element.getSizeScaledY())
				largestHeight = element.getSizeScaledY();

		}
		if (greedyElement != null) {
			greedyElement.setAbsoluteSizeX(availableWidth - dynamicWidth);
			// the first argument is irrelevant, since this is set static
			greedyElement.calculateScales(availableWidth - dynamicWidth, availableHeight);
			dynamicWidth = availableWidth;
		}
		if (isXDynamic)
			sizeScaledX = totalWidth;
		if (isYDynamic)
			sizeScaledY = largestHeight;
	}

}
