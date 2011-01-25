package org.caleydo.core.view.opengl.layout;

public class Column
	extends LayoutContainer {

	@Override
	protected void calculateTransforms(float bottom, float left, float top, float right) {

		float x;
		if (isLeftToRight)
			x = left;
		else
			x = right;

		for (ElementLayout element : elements) {
			//
			// else {
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
		// TODO Auto-generated method stub

		float actualWidth = 0;
		float actualHeight = 0;
		for (ElementLayout element : elements) {
			element.calculateScales(availableWidth - actualWidth, availableHeight - actualHeight);
			actualHeight += element.getSizeScaledY();
			if (actualWidth < element.getSizeScaledX())
				actualWidth = element.getSizeScaledX();

		}
		if (isXDynamic)
			sizeScaledX = actualWidth;
		if (isYDynamic)
			sizeScaledY = actualHeight;
	}

}
