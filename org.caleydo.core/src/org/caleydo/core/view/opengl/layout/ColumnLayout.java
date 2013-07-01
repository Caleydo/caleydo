/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout;

import java.util.ArrayList;

import org.caleydo.core.view.opengl.layout.Column.VAlign;

/**
 * Container for layouts that are stacked on top of each other. The column is a
 * {@link ElementLayout} and contains other ElementLayouts. It can be nested
 * into other containers
 *
 * @author Alexander Lex
 */
public class ColumnLayout implements ILayout {
	private VAlign vAlign = VAlign.LEFT;
	private boolean isBottomUp = true;

	public void setVAlign(VAlign vAlign) {
		this.vAlign = vAlign;
	}

	/**
	 * Set flag signaling whether the content should be rendered from bottom to top (default, true) or from top to
	 * bottom (false)
	 *
	 * @param isBottomUp
	 */
	public void setBottomUp(boolean isBottomUp) {
		this.isBottomUp = isBottomUp;
	}

	@Override
	public float getUnscalableElementHeight(ALayoutContainer parent) {
		float unscalableHeight = 0;
		for (ElementLayout element : parent) {
			unscalableHeight += element.getUnscalableElementHeight();
		}
		return unscalableHeight;
	}

	@Override
	public float getUnscalableElementWidth(ALayoutContainer parent) {
		float maxWidth = 0;
		for (ElementLayout element : parent) {
			float elementWidth = element.getUnscalableElementWidth();
			if (elementWidth > maxWidth)
				maxWidth = elementWidth;

		}
		return maxWidth;
	}

	@Override
	public void calculateTransforms(ALayoutContainer parent, float bottom, float left, float top, float right) {
		float x = left;

		for (ElementLayout element : parent) {
			float xTranslate = x;
			switch (vAlign) {
			case LEFT:
				element.setTranslateX(xTranslate);
				break;
			case CENTER:
				xTranslate += (parent.getSizeScaledX() - element.getSizeScaledX()) / 2;
				element.setTranslateX(xTranslate);
				break;
			case RIGHT:
				break;
			}

			if (isBottomUp) {

				if (element instanceof ALayoutContainer) {
					((ALayoutContainer) element).calculateTransforms(bottom, xTranslate,
							bottom + element.getSizeScaledY(), right);
				}

				element.setTranslateY(bottom);
				bottom += element.getSizeScaledY();

			} else {
				bottom = top - element.getSizeScaledY();
				if (element instanceof ALayoutContainer) {
					((ALayoutContainer) element).calculateTransforms(bottom, left, top,
							right);
				}

				top -= element.getSizeScaledY();
				element.setTranslateY(bottom);
			}

		}
	}

	@Override
	public void calculateScales(ALayoutContainer parent, float totalWidth, float totalHeight,
			Integer numberOfDynamicSizeUnitsX, Integer numberOfDynamicSizeUnitsY) {
		float availableWidth = parent.getSizeScaledX();
		float availableHeight = parent.getSizeScaledY();

		if (parent.isXDynamic)
			availableWidth = totalWidth;

		if (parent.isYDynamic)
			availableHeight = totalHeight;

		float widestElement = 0;
		// float sumUnscalabeElementHeights = 0;
		for (ElementLayout element : parent) {
			float tempWidth = element.getUnscalableElementWidth();
			if (tempWidth > widestElement)
				widestElement = tempWidth;

			float unscalabelElementHeight = element.getUnscalableElementHeight();
			availableHeight -= unscalabelElementHeight;
			// sumUnscalabeElementHeights += unscalabelElementHeight;
		}

		if (availableHeight < -0.5) {
			// Logger.log(new Status(IStatus.ERROR, "org.caleydo.core",
			// "Layout elements in " + this
			// + "don't fit by " + availableHeight));
			parent.triggerLayoutCollision(Math.abs(availableHeight));
		}

		calculateSubElementScales(parent, availableWidth, availableHeight,
				numberOfDynamicSizeUnitsX, numberOfDynamicSizeUnitsY);
	}

	private void calculateSubElementScales(ALayoutContainer parent, float availableWidth, float availableHeight,
			Integer numberOfDynamicSizeUnitsX, Integer numberOfDynamicSizeUnitsY) {
		// the largest width of any element in the column (only relevant if
		// isXDynamic is true)
		float largestWidth = 0;
		// the height including dynamic and static heights
		float totalHeight = 0;
		// the height sum of only dynamic elements
		float dynamicHeight = 0;

		ArrayList<ElementLayout> greedyElements = new ArrayList<ElementLayout>();
		for (ElementLayout element : parent) {
			if (element.grabY) {
				greedyElements.add(element);
				continue;
			}
			// check if this is a dynamic column in x and no sub-element has a
			// dynamic size of 1
			if (parent.isXDynamic && !(element instanceof ALayoutContainer)
					&& !element.isWidthStatic() && element.ratioSizeX == 1)
				throw new IllegalStateException("Specified column " + this
						+ " as dynamic in x, but the sub-element " + element
						+ " has a ratioSize of 1, which is illegal.");

			element.calculateScales(availableWidth, availableHeight,
					numberOfDynamicSizeUnitsX, numberOfDynamicSizeUnitsY);

			totalHeight += element.getSizeScaledY();

			// if an element is set in absolute size, the available size is
			// already reduced by that value
			if (!element.isHeightStatic())
				dynamicHeight += element.getSizeScaledY()
						- element.getUnscalableElementHeight();

			// determining the largest element in X
			if (largestWidth < element.getSizeScaledX())
				largestWidth = element.getSizeScaledX();

		}
		if (greedyElements.size() != 0l) {
			float remainingSpace = availableHeight - dynamicHeight;
			float greedyHeight = remainingSpace / greedyElements.size();
			for (ElementLayout element : greedyElements) {
				element.setAbsoluteSizeY(greedyHeight);
				// the second argument is irrelevant since this is static
				element.calculateScales(availableWidth, greedyHeight,
						numberOfDynamicSizeUnitsX, numberOfDynamicSizeUnitsY);
			}
		}

		if (parent.isXDynamic)
			parent.sizeScaledX = largestWidth;
		if (parent.isYDynamic)
			parent.sizeScaledY = totalHeight;

	}

	@Override
	public int getDynamicSizeUnitsX(ALayoutContainer container) {
		int largestDynamicUnit = 0;
		for (ElementLayout layout : container) {
			int currentDynamicUnit = layout.getDynamicSizeUnitsX();
			if(currentDynamicUnit > largestDynamicUnit)
				largestDynamicUnit = currentDynamicUnit;
		}
		return largestDynamicUnit;
	}

	/**
	 * Recursively calculates the number of dynamic size units for its children
	 * and returns the sum
	 */
	@Override
	public int getDynamicSizeUnitsY(ALayoutContainer container) {
		int sumDynamicUnits = 0;
		for (ElementLayout layout : container) {
			sumDynamicUnits += layout.getDynamicSizeUnitsY();
		}
		return sumDynamicUnits;
	}
}
