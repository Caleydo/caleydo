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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.media.opengl.GL2;

/**
 * BaseClass for layouts which contain nested {@link ElementLayout}s.
 *
 * @author Alexander Lex
 */
public abstract class ALayoutContainer extends ElementLayout implements Iterable<ElementLayout> {

	/**
	 * Flag signaling whether the x-size of the container should be calculated as the sum of it's parts (true), or if
	 * some size indication (either scaled or not scaled) is given (false)
	 */
	protected boolean isXDynamic = false;

	/**
	 * Flag signaling whether the y-size of the container should be calculated as the sum of it's parts (true), or if
	 * some size indication (either scaled or not scaled) is given (false)
	 */
	protected boolean isYDynamic = false;

	protected boolean isBottomUp = true;
	protected boolean isLeftToRight = true;

	protected ArrayList<ElementLayout> elements = new ArrayList<ElementLayout>();

	/**
	 * The currently available bottom distance for the layout. Use if only this sub-part of the layout is updated
	 */
	protected float bottom;
	/**
	 * The currently available top distance for the layout. Use if only this sub-part of the layout is updated
	 */
	protected float top;
	/**
	 * The currently available left distance for the layout. Use if only this sub-part of the layout is updated
	 */
	protected float left;
	/**
	 * The currently available right distance for the layout. Use if only this sub-part of the layout is updated
	 */
	protected float right;

	/**
	 * Determines whether the layout elements of this container are rendered in an order according to their priority.
	 */
	protected boolean isPriorityRendereing = false;

	public ALayoutContainer() {
		super();
	}

	public ALayoutContainer(String layoutName) {
		super(layoutName);
	}

	@Override
	public void render(GL2 gl) {
		super.render(gl);
		if (isHidden)
			return;
		if (isPriorityRendereing) {
			List<ElementLayout> sortedList = new ArrayList<>(elements);
			Collections.sort(sortedList, new Comparator<ElementLayout>() {
				@Override
				public int compare(ElementLayout o1, ElementLayout o2) {
					return -1 * (o1.getRenderingPriority() - o2.getRenderingPriority());
				}
			});
			for (ElementLayout l : sortedList)
				l.render(gl);
		} else {
			for (ElementLayout element : elements) {
				element.render(gl);
			}
		}
	}

	/**
	 * Set flag signaling whether the x-size of the container should be calculated as the sum of it's parts (true), or
	 * if some size indication (either scaled or not scaled) is given (false)
	 */
	public void setXDynamic(boolean isXDynamic) {
		this.isXDynamic = isXDynamic;
	}

	/**
	 * Set flag signaling whether the y-size of the container should be calculated as the sum of it's parts (true), or
	 * if some size indication (either scaled or not scaled) is given (false)
	 */
	public void setYDynamic(boolean isYDynamic) {
		this.isYDynamic = isYDynamic;
	}

	/**
	 * Append an element to the container at the end
	 *
	 * @param elementLayout
	 */
	public void append(ElementLayout elementLayout) {
		add(elementLayout);
	}

	/**
	 * Append an element to the container at the end
	 *
	 * @param elementLayout
	 */
	public ALayoutContainer add(ElementLayout elementLayout) {
		elements.add(elementLayout);
		return this;
	}

	/**
	 * Add an element to the container at the specified index. Subsequent layouts will be shifted to the right.
	 *
	 * @param index
	 * @param elementLayout
	 */
	public void add(int index, ElementLayout elementLayout) {
		elements.add(index, elementLayout);
	}

	public ElementLayout get(int index) {
		return elements.get(index);
	}

	@Override
	public Iterator<ElementLayout> iterator() {
		return elements.iterator();
	}

	@Override
	public void updateSubLayout() {
		if (isHidden)
			return;
		calculateScales(totalWidth, totalHeight, dynamicSizeUnitsX, dynamicSizeUnitsY);
		updateSpacings();
		calculateTransforms(bottom, left, top, right);
	}

	@Override
	public String toString() {
		String name;
		if (layoutName == null)
			name = layoutName;
		else
			name = super.toString();

		return ("Container " + name + " with " + elements.size() + " elements. height: " + sizeScaledY + ", widht: " + sizeScaledX);
	}

	public int size() {
		return elements.size();
	}

	public void clear() {
		elements.clear();
	}

	public boolean remove(ElementLayout elementLayout) {
		return elements.remove(elementLayout);
	}

	// --------------------- End of Public Interface ---------------------

	protected abstract void calculateSubElementScales(float availableWidth, float availableHeight,
			Integer numberOfDynamicSizeUnitsX, Integer numberOfDynamicSizeUnitsY);

	protected void calculateTransforms(float bottom, float left, float top, float right) {
		this.bottom = bottom;
		this.left = left;
		this.top = top;
		this.right = right;
	}

	@Override
	void setRenderingDirty() {
		if (isHidden)
			return;
		super.setRenderingDirty();
		for (ElementLayout element : elements) {
			element.setRenderingDirty();
		}
	}

	@Override
	protected void updateSpacings() {
		if (isHidden)
			return;
		super.updateSpacings();
		for (ElementLayout element : elements) {
			element.updateSpacings();
		}
	}

	@Override
	void setLayoutManager(LayoutManager layoutManager) {
		super.setLayoutManager(layoutManager);
		for (ElementLayout element : elements) {
			element.setLayoutManager(layoutManager);
		}
	}

	@Override
	public void destroy(GL2 gl) {
		super.destroy(gl);

		for (ElementLayout elementLayout : elements) {
			elementLayout.destroy(gl);
		}
		elements.clear();
	}

	public ArrayList<ElementLayout> getElements() {
		return elements;
	}

	/**
	 * @return True, if this layout container renders its elements in an order (concerning the time) according to their
	 *         priority, false otherwise.
	 */
	public boolean isPriorityRendereing() {
		return isPriorityRendereing;
	}

	/**
	 * Sets whether this layout container renders its elements in an order (concerning the time) according to their
	 * priority.
	 *
	 * @param isPriorityRendereing
	 */
	public void setPriorityRendereing(boolean isPriorityRendereing) {
		this.isPriorityRendereing = isPriorityRendereing;
	}
}
