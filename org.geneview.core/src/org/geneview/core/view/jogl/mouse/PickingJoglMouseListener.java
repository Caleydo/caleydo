/**
 * 
 */
package org.geneview.core.view.jogl.mouse;

import java.awt.Point;
import java.awt.event.MouseEvent;

import org.geneview.core.view.jogl.IJoglMouseListener;

/**
 * @author Michael Kalkusch
 *
 */
public class PickingJoglMouseListener extends JoglMouseListener {

	protected boolean bMouseMoved = false;
	
	protected Point pickedPointDragStart;
	
	protected Point pickedPointCurrent;
	
	protected boolean bMousePressed = false;
	
	protected boolean bMouseReleased = false;
	
	protected boolean bMouseDragged = false;
	
	/**
	 * @param refParentGearsMain
	 */
	public PickingJoglMouseListener(final IJoglMouseListener refParentGearsMain) {

		super(refParentGearsMain);
		
		pickedPointDragStart = new Point();
	}
	
	public void mousePressed(MouseEvent mouseEvent) {

		super.mousePressed(mouseEvent);
		
		if (mouseEvent.getButton() == MouseEvent.BUTTON1_MASK)
		{
			/** Left mouse button clicked.. */
			bMousePressed = true;
		}
		
		pickedPointDragStart.setLocation(mouseEvent.getPoint());
	}
	
    public void mouseMoved(MouseEvent mouseEvent){
    	
    	super.mouseMoved(mouseEvent);
    	
    	bMouseMoved = true;
    	pickedPointCurrent = mouseEvent.getPoint();
    }
    
	public void mouseReleased(MouseEvent mouseEvent) {
	
		super.mouseReleased(mouseEvent);
		
		bMouseReleased = true;
		pickedPointCurrent = mouseEvent.getPoint();
	}
	
	public void mouseDragged(MouseEvent mouseEvent) {
	
		super.mouseDragged(mouseEvent);
		
		bMouseDragged = true;
		pickedPointCurrent = mouseEvent.getPoint();
	}
    
    public final boolean wasMousePressed() {
    	
    	boolean bTmp = bMousePressed;
    	bMousePressed = false;
    	return bTmp;
    }
    
    public final boolean wasMouseMoved() {
    	
    	boolean bTmp = bMouseMoved;
    	bMouseMoved = false;
    	return bTmp;
    }
    
    public final boolean wasMouseReleased() {
    	
    	boolean bTmp = bMouseLeftButtonDown;
    	bMouseLeftButtonDown = false;
    	return bTmp;
    }
    
    public final boolean wasMouseDragged() {
    	
    	boolean bTmp = bMouseDragged;
    	bMouseDragged = false;
    	return bTmp;
    }
    
    public final Point getPickedPoint() {
    	
    	return pickedPointCurrent;
    }
    
    public final Point getPickedPointDragStart() {
    	
    	return pickedPointDragStart;
    }

}
