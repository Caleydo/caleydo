/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 * 
 */
package org.caleydo.view.stratomex.brick.ui;

/**
 * @author alexsb
 * 
 */
public class RectangleCoordinates {

	private float left;
	private float width;
	private float height;
	private float bottom;

	/**
	 * @return the left, see {@link #left}
	 */
	public float getLeft() {
		return left;
	}

	/**
	 * @param left
	 *            setter, see {@link #left}
	 */
	public void setLeft(float left) {
		this.left = left;
	}

	/**
	 * @return the bottom, see {@link #bottom}
	 */
	public float getBottom() {
		return bottom;
	}

	/**
	 * @param bottom
	 *            setter, see {@link #bottom}
	 */
	public void setBottom(float bottom) {
		this.bottom = bottom;
	}

	/**
	 * @return the width, see {@link #width}
	 */
	public float getWidth() {
		return width;
	}

	/**
	 * @param width
	 *            setter, see {@link #width}
	 */
	public void setWidth(float width) {
		this.width = width;
	}

	/**
	 * @return the height, see {@link #height}
	 */
	public float getHeight() {
		return height;
	}

	/**
	 * @param height
	 *            setter, see {@link #height}
	 */
	public void setHeight(float height) {
		this.height = height;
	}

}
