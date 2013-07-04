/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.event.view;

import org.caleydo.core.event.ADirectedEvent;

/**
 * Event triggered when scrolling the Composite of a view.
 *
 * @author Christian Partl
 *
 */
public class ViewScrollEvent extends ADirectedEvent {

	/**
	 * X coordinate of the origin (top left) within the composite
	 */
	private int originX;
	/**
	 * Y coordinate of the origin (top left) within the composite
	 */
	private int originY;

	/**
	 * Height of the composite
	 */
	private int height;
	/**
	 * Width of the composite
	 */
	private int width;

	public ViewScrollEvent() {
	}

	public ViewScrollEvent(int originX, int originY, int width, int height) {
		this.originX = originX;
		this.originY = originY;
		this.width = width;
		this.height = height;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

	/**
	 * @return the originX, see {@link #originX}
	 */
	public int getOriginX() {
		return originX;
	}

	/**
	 * @param originX
	 *            setter, see {@link originX}
	 */
	public void setOriginX(int originX) {
		this.originX = originX;
	}

	/**
	 * @return the originY, see {@link #originY}
	 */
	public int getOriginY() {
		return originY;
	}

	/**
	 * @param originY
	 *            setter, see {@link originY}
	 */
	public void setOriginY(int originY) {
		this.originY = originY;
	}

	/**
	 * @return the height, see {@link #height}
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height
	 *            setter, see {@link height}
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * @return the width, see {@link #width}
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width
	 *            setter, see {@link width}
	 */
	public void setWidth(int width) {
		this.width = width;
	}

}
