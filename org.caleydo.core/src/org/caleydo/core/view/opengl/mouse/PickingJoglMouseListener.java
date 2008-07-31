package org.caleydo.core.view.opengl.mouse;

import java.awt.Point;
import java.awt.event.MouseEvent;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;

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

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.view.jogl.mouse.JoglMouseListener#mousePressed(java.
	 * awt.event.MouseEvent)
	 */
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

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.view.jogl.mouse.JoglMouseListener#mouseMoved(java.awt
	 * .event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent mouseEvent)
	{

		super.mouseMoved(mouseEvent);

		bMouseMoved = true;
		pickedPointCurrent = mouseEvent.getPoint();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.view.jogl.mouse.JoglMouseListener#mouseReleased(java
	 * .awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent mouseEvent)
	{

		super.mouseReleased(mouseEvent);

		if (mouseEvent.getButton() == MouseEvent.BUTTON1)
		{
			bMouseReleased = true;
			pickedPointCurrent = mouseEvent.getPoint();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.view.jogl.mouse.JoglMouseListener#mouseDragged(java.
	 * awt.event.MouseEvent)
	 */
	public void mouseDragged(MouseEvent mouseEvent)
	{

		super.mouseDragged(mouseEvent);

		bMouseDragged = true;
		pickedPointCurrent = mouseEvent.getPoint();
	}

	public final boolean wasLeftMouseButtonPressed()
	{

		boolean bTmp = bLeftMouseButtonPressed;

		return bTmp;
	}

	public final boolean wasRightMouseButtonPressed()
	{

		boolean bTmp = bRightMouseButtonPressed;

		return bTmp;
	}

	public final boolean wasMouseMoved()
	{

		boolean bTmp = bMouseMoved;

		return bTmp;
	}

	// FIXME: Hack to conserve the mouse state - discuss mouse states
	// public final boolean wasMouseReleased()
	// {
	// return wasMouseReleased(false);
	// }

	public final boolean wasMouseReleased()// boolean bDoConserveState)
	{

		boolean bTmp = bMouseReleased;
		// if(!bDoConserveState)

		return bTmp;
	}

	public final boolean wasMouseDragged()
	{

		boolean bTmp = bMouseDragged;

		return bTmp;
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
