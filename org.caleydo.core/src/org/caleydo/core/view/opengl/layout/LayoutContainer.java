package org.caleydo.core.view.opengl.layout;

import java.util.ArrayList;
import java.util.Iterator;

import javax.media.opengl.GL2;

/**
 * BaseClass for layouts which contain nested {@link ElementLayout}s.
 * 
 * @author Alexander Lex
 */
public abstract class LayoutContainer
	extends ElementLayout
	implements Iterable<ElementLayout> {

	/**
	 * Flag signaling whether the x-size of the container should be calculated as the sum of it's parts
	 * (true), or if some size indication (either scaled or not scaled) is given (false)
	 */
	protected boolean isXDynamic = false;

	/**
	 * Flag signaling whether the y-size of the container should be calculated as the sum of it's parts
	 * (true), or if some size indication (either scaled or not scaled) is given (false)
	 */
	protected boolean isYDynamic = false;

	protected boolean isBottomUp = true;
	protected boolean isLeftToRight = true;

	protected ArrayList<ElementLayout> elements = new ArrayList<ElementLayout>();;

	/**
	 * The currently available bottom distance for the layout. Use if only this sub-part of the layout is
	 * updated
	 */
	protected float bottom;
	/**
	 * The currently available top distance for the layout. Use if only this sub-part of the layout is updated
	 */
	protected float top;
	/**
	 * The currently available left distance for the layout. Use if only this sub-part of the layout is
	 * updated
	 */
	protected float left;
	/**
	 * The currently available right distance for the layout. Use if only this sub-part of the layout is
	 * updated
	 */
	protected float right;

	public LayoutContainer() {
		super();
	}

	@Override
	public void destroy() {
		super.destroy();
		for (ElementLayout element : elements) {
			element.destroy();
		}
	}

	public LayoutContainer(String layoutName) {
		super(layoutName);
	}

	@Override
	public void render(GL2 gl) {
		super.render(gl);
		for (ElementLayout element : elements) {
			element.render(gl);
		}
	}

	/**
	 * Set flag signaling whether the x-size of the container should be calculated as the sum of it's parts
	 * (true), or if some size indication (either scaled or not scaled) is given (false)
	 */
	public void setXDynamic(boolean isXDynamic) {
		this.isXDynamic = isXDynamic;
	}

	/**
	 * Set flag signaling whether the y-size of the container should be calculated as the sum of it's parts
	 * (true), or if some size indication (either scaled or not scaled) is given (false)
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
		elements.add(elementLayout);
	}

	/**
	 * Add an element to the container at the specified index. Subsequent layouts will be shifted to the
	 * right.
	 * 
	 * @param index
	 * @param elementLayout
	 */
	public void add(int index, ElementLayout elementLayout) {
		elements.add(index, elementLayout);
	}

	@Override
	public Iterator<ElementLayout> iterator() {
		return elements.iterator();
	}

	@Override
	public void updateSubLayout() {
		calculateScales(totalWidth, totalHeight);
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

		return ("Container " + name + " with " + elements.size() + " elements. height: " + sizeScaledY
			+ ", widht: " + sizeScaledX);
	}

	public int size() {
		return elements.size();
	}

	public void clear() {
		elements.clear();
	}

	public ElementLayout remove(int index) {
		return elements.remove(index);
	}

	// --------------------- End of Public Interface ---------------------

	protected abstract void calculateSubElementScales(float availableWidth, float availableHeight);

	protected void calculateTransforms(float bottom, float left, float top, float right) {
		this.bottom = bottom;
		this.left = left;
		this.top = top;
		this.right = right;
	}

	@Override
	protected void updateSpacings() {
		super.updateSpacings();
		for (ElementLayout element : elements) {
			element.updateSpacings();
		}
	}

	public ArrayList<ElementLayout> getElements() {
		return elements;
	}
}
