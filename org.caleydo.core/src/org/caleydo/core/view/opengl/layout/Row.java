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

/**
 * Container for layouts that are rendered side by side. The row is a
 * {@link ElementLayout} and contains other ElementLayouts. It can be nested
 * into other containers
 *
 * @author Alexander Lex
 */
public class Row extends ALayoutContainer {

	public enum HAlign {
		TOP, BOTTOM, CENTER
	}

	private HAlign hAlign = HAlign.TOP;

	public Row() {
		super();
	}

	public Row(String layoutName) {
		super(layoutName);
	}

	/**
	 * Set the flag signaling whether the content should be rendered from left
	 * to right (true, default) or from right to left (false)
	 */
	public void setLeftToRight(boolean isLeftToRight) {
		this.isLeftToRight = isLeftToRight;
	}

	public void sethAlign(HAlign hAlign) {
		this.hAlign = hAlign;
	}

	@Override
	public float getUnscalableElementWidth() {
		if (isHidden)
			return 0;
		if (!isXDynamic)
			return super.getUnscalableElementWidth();
		else {
			float unscalableWidth = 0;
			for (ElementLayout element : this) {
				unscalableWidth += element.getUnscalableElementWidth();
			}
			return unscalableWidth;
		}
	}

	@Override
	public float getUnscalableElementHeight() {
		if (isHidden)
			return 0;
		if (!isYDynamic)
			return super.getUnscalableElementHeight();
		else {
			float maxHeight = 0;
			for (ElementLayout element : this) {
				float elementHeight = element.getUnscalableElementHeight();
				if (elementHeight > maxHeight)
					maxHeight = elementHeight;

			}
			return maxHeight;
		}
	}

	/**
	 * <p>
	 * Set flag signaling whether the y-size of the container should be set to
	 * the largest size in y of its sub-elements (true), or if some size
	 * indication (either scaled or not scaled) is given (false).
	 * </p>
	 * <p>
	 * Notice that for if this is set to true, sub-elements must not have a
	 * ratioSize of 1 (which is the default initialization). The reason for this
	 * is that it makes no sense, and catching it prevents errors.
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

		// FIXME this is probably wrong for nested elements look at Column on
		// how to do it correctly
		for (ElementLayout element : this) {
			float yTranslate = y;
			switch (hAlign) {
			case TOP:
				element.setTranslateY(yTranslate);
				break;
			case CENTER:
				yTranslate += (getSizeScaledY() - element.getSizeScaledY()) / 2;
				element.setTranslateY(yTranslate);
				break;
			case BOTTOM:
				// FIXME this is wrong
				element.setTranslateY(yTranslate);
				break;
			}

			if (element instanceof ALayoutContainer) {
				((ALayoutContainer) element)
						.calculateTransforms(bottom, left, top, right);
			}

			if (isLeftToRight) {
				element.setTranslateX(left);
				left += element.getSizeScaledX();
			} else {
				element.setTranslateX(right);
				right -= element.getSizeScaledX();
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

		float highestElement = 0;
		for (ElementLayout element : this) {
			float tempHeight = element.getUnscalableElementHeight();
			if (tempHeight > highestElement)
				highestElement = tempHeight;

			availableWidth -= element.getUnscalableElementWidth();
		}
		calculateSubElementScales(availableWidth, availableHeight,
				numberOfDynamicSizeUnitsX, numberOfDynamicSizeUnitsY);
	}

	@Override
	protected void calculateSubElementScales(float availableWidth, float availableHeight,
			Integer numberOfDynamicSizeUnitsX, Integer numberOfDynamicSizeUnitsY) {

		float dynamicWidth = 0;
		float totalWidth = 0;
		// the largest height of any element in the row (only relevant if
		// isZDynamic is true)
		float largestHeight = 0;

		ArrayList<ElementLayout> greedyElements = new ArrayList<ElementLayout>();

		for (ElementLayout element : this) {
			if (element.grabX) {
				greedyElements.add(element);

				continue;
			}
			if (isYDynamic
					&& ((!element.isHeightStatic() && element.ratioSizeY == 1) || (element instanceof ALayoutContainer && ((ALayoutContainer) (element)).isYDynamic)))
				throw new IllegalStateException("Specified column " + this
						+ " as dynamic in y, but the sub-element " + element
						+ " has a ratioSize of 1, which is illegal.");
			element.calculateScales(availableWidth, availableHeight,
					numberOfDynamicSizeUnitsX, numberOfDynamicSizeUnitsY);
			totalWidth += element.getSizeScaledX();
			// if an element is set in absolute size, the available size is
			// already reduced by that value

			dynamicWidth += element.getSizeScaledX()
					- element.getUnscalableElementWidth();
			if (largestHeight < element.getSizeScaledY())
				largestHeight = element.getSizeScaledY();

		}
		if (greedyElements.size() > 0) {
			float remainingSpace = availableWidth - dynamicWidth;
			float greedyWidth = remainingSpace / greedyElements.size();
			for (ElementLayout greedyElement : greedyElements) {

				greedyElement.setAbsoluteSizeX(greedyWidth);
				// the first argument is irrelevant, since this is set static
				greedyElement.calculateScales(greedyWidth, availableHeight,
						numberOfDynamicSizeUnitsX, numberOfDynamicSizeUnitsY);
			}
			dynamicWidth = availableWidth;
		}
		if (isXDynamic)
			sizeScaledX = totalWidth;
		if (isYDynamic)
			sizeScaledY = largestHeight;
	}

	/**
	 * @return the dynamicSizeUnitsX, see {@link #dynamicSizeUnitsX}
	 */
	@Override
	int getDynamicSizeUnitsX() {
		int sumDynamicUnits = 0;
		for (ElementLayout layout : this) {
			sumDynamicUnits += layout.getDynamicSizeUnitsX();
		}
		return sumDynamicUnits;
	}

	/**
	 * @return the dynamicSizeUnitsY, see {@link #dynamicSizeUnitsY}
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
