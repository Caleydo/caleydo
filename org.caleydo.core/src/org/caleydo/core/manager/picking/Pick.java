package org.caleydo.core.manager.picking;

import java.awt.Point;

/**
 * All data associated with a single pick
 * 
 * @author Alexander Lex
 */
public class Pick {

	private int iExternalID = 0;

	private EPickingMode ePickingMode = EPickingMode.CLICKED;

	private Point pickedPoint;

	private Point dragStartPoint;

	private float fDepth;

	/**
	 * Constructor.
	 */
	public Pick(int iExternalID, EPickingMode ePickingMode, Point pickedPoint, Point dragStartPoint,
		float fDepth) {

		this.iExternalID = iExternalID;
		this.ePickingMode = ePickingMode;
		this.pickedPoint = pickedPoint;
		this.dragStartPoint = dragStartPoint;
		this.fDepth = fDepth;
	}

	/**
	 * Returns the ID which was previously specified as externalID in the
	 * {@link PickingManager#getPickingID(int, EPickingType, int)} method
	 * 
	 * @return
	 */
	public int getID() {

		return iExternalID;
	}

	/**
	 * Returns the mode of the pick (eg. MOUSE_OVER or CLICKED)
	 * 
	 * @return
	 */
	public EPickingMode getPickingMode() {

		return ePickingMode;
	}

	/**
	 * The 2D screen coordinates of the mouse position at the time the pick occurred.
	 */
	public Point getPickedPoint() {

		return pickedPoint;
	}

	/**
	 * The 2D screen coordinates of the mouse position where the user started the drag action.
	 */
	public Point getDragStartPoint() {

		return dragStartPoint;
	}

	/**
	 * The z-value of the picked element
	 * 
	 * @return
	 */
	public float getDepth() {
		return fDepth;
	}

}
