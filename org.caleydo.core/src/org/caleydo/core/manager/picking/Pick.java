package org.caleydo.core.manager.picking;

import java.awt.Point;

/**
 * @author Alexander Lex
 */
public class Pick {

	private int iPickingID = 0;

	private EPickingMode ePickingMode = EPickingMode.CLICKED;

	private Point pickedPoint;

	private Point dragStartPoint;

	private float fDepth;

	/**
	 * Constructor.
	 */
	public Pick(int iPickingID) {

		this.iPickingID = iPickingID;
	}

	/**
	 * Constructor.
	 */
	// public Pick(int iPickingID, EPickingMode ePickingMode, Point pickedPoint) {
	//
	// this.iPickingID = iPickingID;
	// this.ePickingMode = ePickingMode;
	// this.pickedPoint = pickedPoint;
	// }
	/**
	 * Constructor.
	 */
	public Pick(int iPickingID, EPickingMode ePickingMode, Point pickedPoint, Point dragStartPoint,
		float fDepth) {

		this.iPickingID = iPickingID;
		this.ePickingMode = ePickingMode;
		this.pickedPoint = pickedPoint;
		this.dragStartPoint = dragStartPoint;
		this.fDepth = fDepth;
	}

	public int getPickingID() {

		return iPickingID;
	}

	public EPickingMode getPickingMode() {

		return ePickingMode;
	}

	public Point getPickedPoint() {

		return pickedPoint;
	}

	public Point getDragStartPoint() {
		return dragStartPoint;
	}

	public float getDepth() {
		return fDepth;
	}

}
