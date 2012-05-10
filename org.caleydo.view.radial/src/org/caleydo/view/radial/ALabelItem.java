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
