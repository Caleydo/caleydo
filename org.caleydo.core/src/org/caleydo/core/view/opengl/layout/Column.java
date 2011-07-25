package org.caleydo.core.view.opengl.layout;

import java.util.ArrayList;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.opengl.layout.event.LayoutSizeCollisionEvent;

/**
 * Container for layouts that are stacked on top of each other. The column is a {@link ElementLayout} and
 * contains other ElementLayouts. It can be nested into other containers
 * 
 * @author Alexander Lex
 */
public class Column
	extends LayoutContainer {

	public enum VAlign {
		LEFT,
		RIGHT,
		CENTER
	}

	private VAlign vAlign = VAlign.LEFT;

	public Column() {
		super();
	}

	/**
	 * Set flag signaling whether the content should be rendered from bottom to top (default, true) or from
	 * top to bottom (false)
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

	public void setVAlign(VAlign vAlign) {
		this.vAlign = vAlign;
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

				if (element instanceof LayoutContainer) {
					((LayoutContainer) element).calculateTransforms(bottom, xTranslate,
						bottom + element.getSizeScaledY(), right);
				}

				element.setTranslateY(bottom);
				bottom += element.getSizeScaledY();

			}
			else {
				bottom = top - element.getSizeScaledY();
				if (element instanceof LayoutContainer) {
					((LayoutContainer) element).calculateTransforms(bottom, left, top, right);
				}

				top -= element.getSizeScaledY();
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

		if (availableHeight < -0.5) {
			// Logger.log(new Status(Status.ERROR, "org.caleydo.core", "Layout elements in " + this
			// + "don't fit by " + availableHeight));
			if (managingClassID != -1 && layoutID != -1) {
				LayoutSizeCollisionEvent event = new LayoutSizeCollisionEvent();
				event.setToBigBy(Math.abs(availableHeight));
				event.tableIDs(managingClassID, layoutID);
				GeneralManager.get().getEventPublisher().triggerEvent(event);
			}

		}

		calculateSubElementScales(availableWidth, availableHeight);
	}

	@Override
	protected void calculateSubElementScales(float availableWidth, float availableHeight) {
		// the largest width of any element in the column (only relevant if isXDynamic is true)
		float largestWidth = 0;
		// the height including dynamic and static heights
		float totalHeight = 0;
		// the height sum of only dynamic elements
		float dynamicHeight = 0;

		ArrayList<ElementLayout> greedyElements = new ArrayList<ElementLayout>();
		for (ElementLayout element : elements) {
			if (element.grabY) {
				greedyElements.add(element);
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
				dynamicHeight += element.getSizeScaledY() - element.getUnscalableElementHeight();

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
				element.calculateScales(availableWidth, greedyHeight);
			}
		}

		if (isXDynamic)
			sizeScaledX = largestWidth;
		if (isYDynamic)
			sizeScaledY = totalHeight;

	}
}
