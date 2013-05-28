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
