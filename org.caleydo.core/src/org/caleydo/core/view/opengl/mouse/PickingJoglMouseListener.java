package org.caleydo.core.view.opengl.mouse;

import java.awt.Point;
import java.awt.event.MouseEvent;

/**
 * Mouse picking listener for JOGL views
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
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
		
		bMouseReleased = false;
		bLeftMouseButtonPressed = false;
		bRightMouseButtonPressed = false;
		
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

		bMouseDragged = false;
		
		if (mouseEvent.getButton() == MouseEvent.BUTTON1)
		{
			pickedPointCurrent = mouseEvent.getPoint();
		}

		bMouseReleased = true;
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
		boolean bTmp = bLeftMouseButtonPressed;
		bLeftMouseButtonPressed = false;
		return bTmp;
	}

	public final boolean wasMouseDoubleClicked()
	{
		return bMouseDoubleClick;
	}

	public final boolean wasRightMouseButtonPressed()
	{
		boolean bTmp = bRightMouseButtonPressed;
		bRightMouseButtonPressed = false;
		return bTmp;
	}

	public final boolean wasMouseMoved()
	{
		boolean bTmp = bMouseMoved;
		bMouseMoved = false;
		return bTmp;
	}

	public final boolean wasMouseReleased()
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
}
