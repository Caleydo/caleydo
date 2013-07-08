/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.picking;

import java.awt.Point;

/**
 * All data associated with a single pick
 *
 * @author Alexander Lex
 */
public class Pick {

	/** The id of the picked object. */
	private final int objectID;

	private final PickingMode ePickingMode;

	/** The coordinates in the plane where the pick occurred */
	private final Point pickedPoint;
	/** The coordinates in the plane where the drag action started */
	private final Point dragStartPoint;

	/** The z-value of the picked element */
	private final float depth;

	/**
	 * indicator set the by listener, that the picked object is currently dragged, thus listens to mouse events till the
	 * mouse was released, supported by {@link PickingManager2} and {@link SimplePickingManager}
	 */
	private boolean doDragging = false;

	/**
	 * indicator, whether for any current the {@link #doDragging} is set to true, supported by {@link PickingManager2}
	 * and {@link SimplePickingManager}
	 */
	private final boolean isAnyDragging;

	/**
	 * the mouse x delta between the last call, used by {@link PickingMode#MOUSE_MOVED} and {@link PickingMode#DRAGGED}
	 *
	 * supported by {@link PickingManager2} and {@link SimplePickingManager}
	 */
	private final int dx;
	/**
	 * the mouse y delta between the last call, used by {@link PickingMode#MOUSE_MOVED} and {@link PickingMode#DRAGGED}
	 *
	 * supported by {@link PickingManager2} and {@link SimplePickingManager}
	 */
	private final int dy;

	public Pick(int objectID, PickingMode ePickingMode, Point pickedPoint, Point dragStartPoint, float depth) {
		this(objectID, ePickingMode, pickedPoint, dragStartPoint, depth, 0, 0, false);
	}
	/**
	 * Constructor.
	 */
	public Pick(int objectID, PickingMode ePickingMode, Point pickedPoint, Point dragStartPoint, float depth, int dx,
			int dy, boolean isAnyDragging) {

		this.objectID = objectID;
		this.ePickingMode = ePickingMode;
		this.pickedPoint = pickedPoint;
		this.dragStartPoint = dragStartPoint;
		this.depth = depth;
		this.dx = dx;
		this.dy = dy;
		this.isAnyDragging = isAnyDragging;
	}

	/**
	 * @param doDragging
	 *            setter, see {@link doDragging}
	 */
	public void setDoDragging(boolean doDragging) {
		this.doDragging = doDragging;
	}

	/**
	 * @return the doDragging, see {@link #doDragging}
	 */
	public boolean isDoDragging() {
		return doDragging;
	}

	/**
	 * @return the isAnyDragging, see {@link #isAnyDragging}
	 */
	public boolean isAnyDragging() {
		return isAnyDragging;
	}

	/**
	 * @return the dx, see {@link #dx}
	 */
	public int getDx() {
		return dx;
	}

	/**
	 * @return the dy, see {@link #dy}
	 */
	public int getDy() {
		return dy;
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
