/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout;

import java.util.ArrayList;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.opengl.layout.event.LayoutSizeCollisionEvent;

/**
 * Container for layouts that are stacked on top of each other. The column is a
 * {@link ElementLayout} and contains other ElementLayouts. It can be nested
 * into other containers
 *
 * @author Alexander Lex
 */
public class Column extends ALayoutContainer {

	public enum VAlign {
		LEFT, RIGHT, CENTER
	}

	private VAlign vAlign = VAlign.LEFT;

	public Column() {
		super();
	}

	/**
	 * Set flag signaling whether the content should be rendered from bottom to
	 * top (default, true) or from top to bottom (false)
	 *
	 * @param isBottomUp
	 */
	public void setBottomUp(boolean isBottomUp) {
		this.isBottomUp = isBottomUp;
	}

	public Column(String layoutName) {
		super(layoutName);
	}

	@Override
	public float getUnscalableElementHeight() {
		if (isHidden)
			return 0;
		if (!isYDynamic)
			return super.getUnscalableElementHeight();
		else {
			float unscalableHeight = 0;
			for (ElementLayout element : this) {
				unscalableHeight += element.getUnscalableElementHeight();
			}
			return unscalableHeight;
		}
	}

	@Override
	public float getUnscalableElementWidth() {
		if (isHidden)
			return 0;
		if (!isXDynamic)
			return super.getUnscalableElementWidth();
		else {
			float maxWidth = 0;
			for (ElementLayout element : this) {
				float elementWidth = element.getUnscalableElementWidth();
				if (elementWidth > maxWidth)
					maxWidth = elementWidth;

			}
			return maxWidth;
		}
	}

	public void setVAlign(VAlign vAlign) {
		this.vAlign = vAlign;
	}

	/**
	 * <p>
	 * Set flag signaling whether the x-size of the container should be set to
	 * the largest size in y of its sub-elements (true), or if some size
	 * indication (either scaled or not scaled) is given (false).
	 * </p>
	 * <p>
	 * Notice that for if this is set to true, sub-elements must not have a
	 * ratioSize of 1 (which is the default initialization). The reason for this
	 * is that it makes no sense, and catching it prevents errors.
	 */
	@Override
	public void setXDynamic(boolean isXDynamic) {
		super.setXDynamic(isXDynamic);
	}

	@Override
	protected void calculateTransforms(float bottom, float left, float top, float right) {
		if (isHidden)
			return;
		super.calculateTransforms(bottom, left, top, right);

		float x;
		if (isLeftToRight)
			x = left;
		else
			x = right;

		for (ElementLayout element : this) {
			float xTranslate = x;
			switch (vAlign) {
			case LEFT:
				element.setTranslateX(xTranslate);
				break;
			case CENTER:
				xTranslate += (getSizeScaledX() - element.getSizeScaledX()) / 2;
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
	void calculateScales(float totalWidth, float totalHeight,
			Integer numberOfDynamicSizeUnitsX, Integer numberOfDynamicSizeUnitsY) {
		if (isHidden)
			return;
		super.calculateScales(totalWidth, totalHeight, numberOfDynamicSizeUnitsX,
				numberOfDynamicSizeUnitsY);

		float availableWidth = getSizeScaledX();
		float availableHeight = getSizeScaledY();

		if (isXDynamic)
			availableWidth = totalWidth;

		if (isYDynamic)
			availableHeight = totalHeight;

		float widestElement = 0;
		// float sumUnscalabeElementHeights = 0;
		for (ElementLayout element : this) {
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
			if (managingClassID != -1 && layoutID != -1) {
				LayoutSizeCollisionEvent event = new LayoutSizeCollisionEvent();
				event.setToBigBy(Math.abs(availableHeight));
				event.tableIDs(managingClassID, layoutID);
				GeneralManager.get().getEventPublisher().triggerEvent(event);
			}

		}

		calculateSubElementScales(availableWidth, availableHeight,
				numberOfDynamicSizeUnitsX, numberOfDynamicSizeUnitsY);
	}

	@Override
	protected void calculateSubElementScales(float availableWidth, float availableHeight,
			Integer numberOfDynamicSizeUnitsX, Integer numberOfDynamicSizeUnitsY) {
		// the largest width of any element in the column (only relevant if
		// isXDynamic is true)
		float largestWidth = 0;
		// the height including dynamic and static heights
		float totalHeight = 0;
		// the height sum of only dynamic elements
		float dynamicHeight = 0;

		ArrayList<ElementLayout> greedyElements = new ArrayList<ElementLayout>();
		for (ElementLayout element : this) {
			if (element.grabY) {
				greedyElements.add(element);
				continue;
			}
			// check if this is a dynamic column in x and no sub-element has a
			// dynamic size of 1
			if (isXDynamic && !(element instanceof ALayoutContainer)
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

		if (isXDynamic)
			sizeScaledX = largestWidth;
		if (isYDynamic)
			sizeScaledY = totalHeight;

	}

	@Override
	int getDynamicSizeUnitsX() {
		int largestDynamicUnit = 0;
		for (ElementLayout layout : this) {
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
	int getDynamicSizeUnitsY() {
		int sumDynamicUnits = 0;
		for (ElementLayout layout : this) {
			sumDynamicUnits += layout.getDynamicSizeUnitsY();
		}
		return sumDynamicUnits;
	}
}
