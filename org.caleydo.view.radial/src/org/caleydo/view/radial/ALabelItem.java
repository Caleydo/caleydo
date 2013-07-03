/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.radial;

import gleem.linalg.Vec2f;

import javax.media.opengl.GL2;

/**
 * Abstract base class for all label items. Label items hold the actual content
 * of a label and provide methods to render that content.
 * 
 * @author Christian Partl
 */
public abstract class ALabelItem {

	/**
	 * Height of the label item.
	 */
	protected float fHeight;
	/**
	 * Width of the label item.
	 */
	protected float fWidth;
	/**
	 * Position of the label item.
	 */
	protected Vec2f vecPosition;

	/**
	 * Constructor.
	 */
	public ALabelItem() {
		vecPosition = new Vec2f(0, 0);
	}

	/**
	 * Draws the label item in a certain way determined by the concrete class.
	 * 
	 * @param gl
	 *            GL2 object that shall be used for drawing.
	 */
	public abstract void draw(GL2 gl);

	/**
	 * @return Type of the label item.
	 */
	public abstract int getLabelItemType();

	/**
	 * @return Height of the label item.
	 */
	public float getHeight() {
		return fHeight;
	}

	/**
	 * Sets the height of the label item to the specified value.
	 * 
	 * @param fHeight
	 *            Value the label item|s height shall be set to.
	 */
	public void setHeight(float fHeight) {
		this.fHeight = fHeight;
	}

	/**
	 * @return Height of the label item.
	 */
	public float getWidth() {
		return fWidth;
	}

	/**
	 * Sets the height of the label item to the specified value.
	 * 
	 * @param fHeight
	 *            Value the label item|s height shall be set to.
	 */
	public void setWidth(float fWidth) {
		this.fWidth = fWidth;
	}

	/**
	 * Sets the position of the label item.
	 * 
	 * @param fXPosition
	 *            X coordinate of the position.
	 * @param fYPosition
	 *            Y coordinate of the position.
	 */
	public void setPosition(float fXPosition, float fYPosition) {
		vecPosition.set(fXPosition, fYPosition);
	}

	/**
	 * @return Position of the label item.
	 */
	public Vec2f getPosition() {
		return vecPosition;
	}
}
