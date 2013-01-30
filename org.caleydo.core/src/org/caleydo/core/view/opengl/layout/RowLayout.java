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

import org.caleydo.core.view.opengl.layout.Row.HAlign;


public class RowLayout implements ILayout {
	private HAlign hAlign = HAlign.TOP;
	private boolean isLeftToRight = true;

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
	public float getUnscalableElementWidth(ALayoutContainer container) {
		float unscalableWidth = 0;
		for (ElementLayout element : container) {
			unscalableWidth += element.getUnscalableElementWidth();
		}
		return unscalableWidth;
	}

	@Override
	public float getUnscalableElementHeight(ALayoutContainer container) {
		float maxHeight = 0;
		for (ElementLayout element : container) {
			float elementHeight = element.getUnscalableElementHeight();
			if (elementHeight > maxHeight)
				maxHeight = elementHeight;

		}
		return maxHeight;
	}


	@Override
	public void calculateTransforms(ALayoutContainer container, float bottom, float left, float top, float right) {
		float y = bottom;

		// FIXME this is probably wrong for nested elements look at Column on
		// how to do it correctly
		for (ElementLayout element : container) {
			float yTranslate = y;
			switch (hAlign) {
			case TOP:
				element.setTranslateY(yTranslate);
				break;
			case CENTER:
				yTranslate += (container.getSizeScaledY() - element.getSizeScaledY()) / 2;
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
	public void calculateScales(ALayoutContainer container, float totalWidth, float totalHeight,
			Integer numberOfDynamicSizeUnitsX, Integer numberOfDynamicSizeUnitsY) {
		float availableWidth = container.getSizeScaledX();
		float availableHeight = container.getSizeScaledY();

		if (container.isXDynamic)
			availableWidth = totalWidth;

		if (container.isYDynamic)
			availableHeight = totalHeight;

		float highestElement = 0;
		for (ElementLayout element : container) {
			float tempHeight = element.getUnscalableElementHeight();
			if (tempHeight > highestElement)
				highestElement = tempHeight;

			availableWidth -= element.getUnscalableElementWidth();
		}
		calculateSubElementScales(container, availableWidth, availableHeight,
				numberOfDynamicSizeUnitsX, numberOfDynamicSizeUnitsY);
	}

	private void calculateSubElementScales(ALayoutContainer container, float availableWidth, float availableHeight,
			Integer numberOfDynamicSizeUnitsX, Integer numberOfDynamicSizeUnitsY) {
		float dynamicWidth = 0;
		float totalWidth = 0;
		// the largest height of any element in the row (only relevant if
		// isZDynamic is true)
		float largestHeight = 0;

		ArrayList<ElementLayout> greedyElements = new ArrayList<ElementLayout>();

		for (ElementLayout element : container) {
			if (element.grabX) {
				greedyElements.add(element);

				continue;
			}
			if (container.isYDynamic
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
			totalWidth = availableWidth;
		}
		if (container.isXDynamic)
			container.sizeScaledX = totalWidth;
		if (container.isYDynamic)
			container.sizeScaledY = largestHeight;
	}

	/**
	 * @return the dynamicSizeUnitsX, see {@link #dynamicSizeUnitsX}
	 */
	@Override
	public int getDynamicSizeUnitsX(ALayoutContainer container) {
		int sumDynamicUnits = 0;
		for (ElementLayout layout : container) {
			sumDynamicUnits += layout.getDynamicSizeUnitsX();
		}
		return sumDynamicUnits;
	}

	/**
	 * @return the dynamicSizeUnitsY, see {@link #dynamicSizeUnitsY}
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
