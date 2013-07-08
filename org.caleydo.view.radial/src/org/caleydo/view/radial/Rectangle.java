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

/**
 * Helper class that defines a rectangle using float values.
 * 
 * @author Christian Partl
 */
public class Rectangle {

	private float fMinX;
	private float fMinY;
	private float fMaxX;
	private float fMaxY;

	public Rectangle(float fMinX, float fMinY, float fMaxX, float fMaxY) {
		this.fMinX = fMinX;
		this.fMinY = fMinY;
		this.fMaxX = fMaxX;
		this.fMaxY = fMaxY;
	}

	public void setRectangle(float fMinX, float fMinY, float fMaxX, float fMaxY) {
		this.fMinX = fMinX;
		this.fMinY = fMinY;
		this.fMaxX = fMaxX;
		this.fMaxY = fMaxY;
	}

	public float getMinX() {
		return fMinX;
	}

	public void setMinX(float fMinX) {
		this.fMinX = fMinX;
	}

	public float getMinY() {
		return fMinY;
	}

	public void setMinY(float fMinY) {
		this.fMinY = fMinY;
	}

	public float getMaxX() {
		return fMaxX;
	}

	public void setMaxX(float fMaxX) {
		this.fMaxX = fMaxX;
	}

	public float getMaxY() {
		return fMaxY;
	}

	public void setMaxY(float fMaxY) {
		this.fMaxY = fMaxY;
	}

}
