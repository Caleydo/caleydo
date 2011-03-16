package org.caleydo.core.view.opengl.layout;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.opengl.layout.event.LayoutSizeCollisionEvent;
import org.eclipse.core.runtime.Status;

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
	public float getUnscalableElementHeight() {
		if (!isYDynamic)
			return super.getUnscalableElementHeight();
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
		if (!isXDynamic)
			return super.getUnscalableElementWidth();
		else {
			float maxWidth = 0;
			for (ElementLayout element : elements) {
				float elementWidth = element.getUnscalableElementWidth();
				if (elementWidth > maxWidth)
					maxWidth = elementWidth;

			}
			return maxWidth;
		}
	}

	/**
	 * <p>
	 * Set flag signaling whether the x-size of the container should be set to the largest size in y of its
	 * sub-elements (true), or if some size indication (either scaled or not scaled) is given (false).
	 * </p>
	 * <p>
	 * Notice that for if this is set to true, sub-elements must not have a ratioSize of 1 (which is the
	 * default initialization). The reason for this is that it makes no sense, and catching it prevents
	 * errors.
	 */
	@Override
	public void setXDynamic(boolean isXDynamic) {
		super.setXDynamic(isXDynamic);
	}

	@Override
	protected void calculateTransforms(float bottom, float left, float top, float right) {
		super.calculateTransforms(bottom, left, top, right);

		float x;
		if (isLeftToRight)
			x = left;
		else
			x = right;

		for (ElementLayout element : elements) {
			if (isBottomUp) {

				if (element instanceof LayoutContainer) {
					((LayoutContainer) element).calculateTransforms(bottom, left,
						bottom + element.getSizeScaledY(), right);
				}
				element.setTranslateX(x);
				element.setTranslateY(bottom);

				bottom += element.getSizeScaledY();

			}
			else {
				bottom = top - element.getSizeScaledY();
				if (element instanceof LayoutContainer) {
					((LayoutContainer) element).calculateTransforms(bottom, left, top, right);
				}

				top -= element.getSizeScaledY();
				element.setTranslateX(x);
				element.setTranslateY(bottom);
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

		float widestElement = 0;
		// float sumUnscalabeElementHeights = 0;
		for (ElementLayout element : elements) {
			float tempWidth = element.getUnscalableElementWidth();
			if (tempWidth > widestElement)
				widestElement = tempWidth;

			float unscalabelElementHeight = element.getUnscalableElementHeight();
			availableHeight -= unscalabelElementHeight;
			// sumUnscalabeElementHeights += unscalabelElementHeight;
		}
		availableWidth -= widestElement;

		if (availableHeight < -0.0001) {
//			Logger.log(new Status(Status.ERROR, "org.caleydo.core", "Layout elements in " + this
//				+ "don't fit by " + availableHeight));
			if (managingClassID != -1 && layoutID != -1) {
				LayoutSizeCollisionEvent event = new LayoutSizeCollisionEvent();
				event.setToBigBy(Math.abs(availableHeight));
				event.setIDs(managingClassID, layoutID);
				GeneralManager.get().getEventPublisher().triggerEvent(event);
			}

		}

		calculateSubElementScales(availableWidth, availableHeight);

		// if (element.getUnscalableElementHeight() > availableHeight + 0.01f) {
		//
		// if (!Float.isNaN(element.minSizeY))
		// {
		// System.out.println(element.toString() + element.minSizeY + " " + element.hashCode() + " "
		// + (element.getUnscalableElementHeight() - availableHeight));
		// element.useMinSize(true);
		// }
		// }
		// else
		// element.useMinSize(false);

	}

	@Override
	protected void calculateSubElementScales(float availableWidth, float availableHeight) {
		// the largest width of any element in the column (only relevant if isXDynamic is true)
		float largestWidth = 0;
		// the height including dynamic and static heights
		float totalHeight = 0;
		// the height sum of only dynamic elements
		float dynamicHeight = 0;

		ElementLayout greedyElement = null;
		for (ElementLayout element : elements) {
			if (element.grabY) {
				if (greedyElement != null)
					throw new IllegalStateException("Specified more than one greedy element for " + this);
				greedyElement = element;
				continue;
			}
			// check if this is a dynamic column in x and no sub-element has a dynamic size of 1
			if (isXDynamic && !(element instanceof LayoutContainer) && !element.isWidthStatic()
				&& element.ratioSizeX == 1)
				throw new IllegalStateException("Specified column " + this
					+ " as dynamic in x, but the sub-element " + element
					+ " has a ratioSize of 1, which is illegal.");

			element.calculateScales(availableWidth, availableHeight);

			totalHeight += element.getSizeScaledY();

			// if an element is set in absolute size, the available size is already reduced by that value
			if (!element.isHeightStatic())
				dynamicHeight += element.getSizeScaledY();

			// determining the largest element in X
			if (largestWidth < element.getSizeScaledX())
				largestWidth = element.getSizeScaledX();

		}
		if (greedyElement != null) {
			greedyElement.setAbsoluteSizeY(availableHeight - dynamicHeight);
			// the second argument is irrelevant since this is static
			greedyElement.calculateScales(availableWidth, availableHeight - dynamicHeight);
		}

		if (isXDynamic)
			sizeScaledX = largestWidth;
		if (isYDynamic)
			sizeScaledY = totalHeight;

	}
}
