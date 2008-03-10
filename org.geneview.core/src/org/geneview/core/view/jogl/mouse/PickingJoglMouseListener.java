package org.geneview.core.view.jogl.mouse;

import java.awt.Point;
import java.awt.event.MouseEvent;

import org.geneview.core.view.jogl.JoglCanvasForwarder;

/**
 * Mouse picking listener for JOGL views
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class PickingJoglMouseListener 
extends JoglMouseListener {

	protected boolean bMouseMoved = false;
	
	protected Point pickedPointDragStart;
	
	protected Point pickedPointCurrent;
	
	protected boolean bMousePressed = false;
	
	protected boolean bMouseReleased = false;
	
	protected boolean bMouseDragged = false;
	
	/**
	 * Constructor.
	 *
	 */
	public PickingJoglMouseListener() {

		super();
		pickedPointDragStart = new Point();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.jogl.mouse.JoglMouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent mouseEvent) {

		super.mousePressed(mouseEvent);
		
		if (mouseEvent.getButton() == MouseEvent.BUTTON1_MASK)
		{
			/** Left mouse button clicked.. */
			bMousePressed = true;
		}
		
		pickedPointDragStart.setLocation(mouseEvent.getPoint());
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.jogl.mouse.JoglMouseListener#mouseMoved(java.awt.event.MouseEvent)
	 */
    public void mouseMoved(MouseEvent mouseEvent){
    	
    	super.mouseMoved(mouseEvent);
    	
    	bMouseMoved = true;
    	pickedPointCurrent = mouseEvent.getPoint();
    }
    
    /*
     * (non-Javadoc)
     * @see org.geneview.core.view.jogl.mouse.JoglMouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
	public void mouseReleased(MouseEvent mouseEvent) {
	
		super.mouseReleased(mouseEvent);
		
		bMouseReleased = true;
		pickedPointCurrent = mouseEvent.getPoint();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.jogl.mouse.JoglMouseListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	public void mouseDragged(MouseEvent mouseEvent) {
	
		super.mouseDragged(mouseEvent);
		
		bMouseDragged = true;
		pickedPointCurrent = mouseEvent.getPoint();
	}
    
    public final boolean wasMousePressed() {
    	
    	boolean bTmp = bMousePressed;
    	
    	return bTmp;
    }
    
    public final boolean wasMouseMoved() {
    	
    	boolean bTmp = bMouseMoved;
    
    	return bTmp;
    }
    // FIXME: Hack to conserve the mouse state - discuss mouse states
//    public final boolean wasMouseReleased()
//    {
//    	return wasMouseReleased(false);
//    }
    
    public final boolean wasMouseReleased()//boolean bDoConserveState) 
    {
    	boolean bTmp = bMouseReleased;
    	//if(!bDoConserveState)
    	
    	return bTmp;
    }
    
    public final boolean wasMouseDragged() {
    	
    	boolean bTmp = bMouseDragged;
    
    	return bTmp;
    }
    
    public final Point getPickedPoint() {
    	
    	return pickedPointCurrent;
    }
    
    public final Point getPickedPointDragStart() {
    	
    	return pickedPointDragStart;
    }
    
    public final void resetEvents()
    {
    	bMouseDragged = false;
    	bMouseReleased = false;
    	bMouseMoved = false;
    	bMousePressed = false;
    }

}
