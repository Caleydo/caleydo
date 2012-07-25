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
package org.caleydo.core.view.opengl.picking;

import java.awt.Point;

/**
 * All data associated with a single pick
 * 
 * @author Alexander Lex
 */
public class Pick {

	/** The id of the picked object. */
	private int objectID = 0;

	private PickingMode ePickingMode = PickingMode.CLICKED;

	/** The coordinates in the plane where the pick occurred */
	private Point pickedPoint;
	/** The coordinates in the plane where the drag action started */
	private Point dragStartPoint;

	/** The z-value of the picked element */
	private float depth;

	/**
	 * Constructor.
	 */
	public Pick(int objectID, PickingMode ePickingMode, Point pickedPoint,
			Point dragStartPoint, float depth) {

		this.objectID = objectID;
		this.ePickingMode = ePickingMode;
		this.pickedPoint = pickedPoint;
		this.dragStartPoint = dragStartPoint;
		this.depth = depth;
	}

	/**
	 * @return the objectID, see {@link #objectID}
	 */
	public int getObjectID() {
		return objectID;
	}

	/**
	 * Returns the mode of the pick (eg. MOUSE_OVER or CLICKED)
	 * 
	 * @return
	 */
	public PickingMode getPickingMode() {

		return ePickingMode;
	}

	/**
	 * The 2D screen coordinates of the mouse position at the time the pick
	 * occurred.
	 */
	public Point getPickedPoint() {

		return pickedPoint;
	}

	/**
	 * The 2D screen coordinates of the mouse position where the user started
	 * the drag action.
	 */
	public Point getDragStartPoint() {

		return dragStartPoint;
	}

	/**
	 * @return the depth, see {@link #depth}
	 */
	public float getDepth() {
		return depth;
	}
}
