package org.caleydo.core.view.opengl.mouse;

import java.awt.Point;
import java.awt.event.MouseEvent;

/**
 * Mouse picking listener for JOGL views
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class PickingJoglMouseListener
	extends JoglMouseListener
{

	protected boolean bMouseMoved = false;

	protected Point pickedPointDragStart;

	protected Point pickedPointCurrent;

	protected boolean bLeftMouseButtonPressed = false;

	protected boolean bRightMouseButtonPressed = false;

	protected boolean bMouseReleased = false;

	protected boolean bMouseDragged = false;

	/**
	 * Constructor.
	 */
	public PickingJoglMouseListener()
	{
		super();
		pickedPointDragStart = new Point();
	}

	@Override
	public void mousePressed(MouseEvent mouseEvent)
	{
		super.mousePressed(mouseEvent);

		if (mouseEvent.getButton() == MouseEvent.BUTTON1)
		{
			bLeftMouseButtonPressed = true;
		}
		else if (mouseEvent.getButton() == MouseEvent.BUTTON3)
		{
			bRightMouseButtonPressed = true;
		}

		pickedPointDragStart.setLocation(mouseEvent.getPoint());
	}

	@Override
	public void mouseMoved(MouseEvent mouseEvent)
	{
		super.mouseMoved(mouseEvent);

		bMouseMoved = true;
		pickedPointCurrent = mouseEvent.getPoint();
	}

	@Override
	public void mouseReleased(MouseEvent mouseEvent)
	{
		super.mouseReleased(mouseEvent);

		if (mouseEvent.getButton() == MouseEvent.BUTTON1)
		{
			bMouseReleased = true;
			pickedPointCurrent = mouseEvent.getPoint();
		}
	}

	@Override
	public void mouseDragged(MouseEvent mouseEvent)
	{
		super.mouseDragged(mouseEvent);

		bMouseDragged = true;
		pickedPointCurrent = mouseEvent.getPoint();
	}

	public final boolean wasLeftMouseButtonPressed()
	{
		return bLeftMouseButtonPressed;
	}

	public final boolean wasRightMouseButtonPressed()
	{
		return bRightMouseButtonPressed;
	}

	public final boolean wasMouseMoved()
	{
		return bMouseMoved;
	}

	// FIXME: Hack to conserve the mouse state - discuss mouse states
	// public final boolean wasMouseReleased()
	// {
	// return wasMouseReleased(false);
	// }

	public final boolean wasMouseReleased()// boolean bDoConserveState)
	{
		return bMouseReleased;
	}

	public final boolean wasMouseDragged()
	{
		return bMouseDragged;
	}

	public final Point getPickedPoint()
	{
		return pickedPointCurrent;
	}

	public final Point getPickedPointDragStart()
	{
		return pickedPointDragStart;
	}

	public final void resetEvents()
	{
		bMouseDragged = false;
		bMouseReleased = false;
		bMouseMoved = false;
		bLeftMouseButtonPressed = false;
		bRightMouseButtonPressed = false;
	}
}
