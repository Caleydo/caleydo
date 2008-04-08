package org.caleydo.core.manager.view;

import java.awt.Point;

/**
 * 
 * @author Alexander Lex
 *
 */

public class Pick {
	
	private int iPickingID = 0;
	private EPickingMode ePickingMode = EPickingMode.CLICKED;
	private Point pickedPoint;
	private Point dragStartPoint;
	
	public Pick(int iPickingID)
	{
		this.iPickingID = iPickingID;
	}
	
	public Pick(int iPickingID, EPickingMode ePickingMode, Point pickedPoint)
	{
		this.iPickingID = iPickingID;
		this.ePickingMode = ePickingMode;
		this.pickedPoint = pickedPoint;
	}
	
	public Pick(int iPickingID, EPickingMode ePickingMode, Point pickedPoint, Point dragStartPoint)
	{
		this.iPickingID = iPickingID;
		this.ePickingMode = ePickingMode;
		this.pickedPoint = pickedPoint;
		this.dragStartPoint = dragStartPoint;
		
		// TODO: throw usefull exception if dragStartPoint is not set, 
		// or if it doesn't match the picking mode
	}
	
	public int getPickingID()
	{
		return iPickingID;
	}
	
	public EPickingMode getPickingMode()
	{
		return ePickingMode;
	}
	
	public Point getPickedPoint()
	{
		return pickedPoint;
	}
	
	public Point getDragStartPoint()
	{
		// TODO: throw usefull exception if point is not set
		return dragStartPoint;
	}

}
