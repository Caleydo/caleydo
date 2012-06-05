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
package org.caleydo.view.stratomex.brick;

/**
 * This class holds visual parameters of a brick, allowing to reconstruct a
 * brick's appearance.
 * 
 * @author Christian Partl
 * 
 */
public class BrickState {

	private EContainedViewType viewType;
	private float height;
	private float width;

	public BrickState(EContainedViewType viewType, float height, float width) {
		this.viewType = viewType;
		this.height = height;
		this.width = width;
	}

	public EContainedViewType getViewType() {
		return viewType;
	}

	public void setViewType(EContainedViewType viewType) {
		this.viewType = viewType;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

}
