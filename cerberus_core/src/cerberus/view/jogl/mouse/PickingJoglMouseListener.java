/**
 * 
 */
package cerberus.view.jogl.mouse;

import java.awt.Point;
import java.awt.event.MouseEvent;

import cerberus.view.jogl.IJoglMouseListener;

/**
 * @author Michael Kalkusch
 *
 */
public class PickingJoglMouseListener extends JoglMouseListener {

	protected boolean bMouseMoved = false;
	
	protected Point pickedPoint;
	
	protected boolean bMousePressed = false;
	
	protected boolean bMouseReleased = false;
	
	protected boolean bMouseDragged = false;
	
	/**
	 * @param refParentGearsMain
	 */
	public PickingJoglMouseListener(final IJoglMouseListener refParentGearsMain) {

		super(refParentGearsMain);
	}
	
	public void mousePressed(MouseEvent mouseEvent) {

		super.mousePressed(mouseEvent);
		
		if (mouseEvent.getButton() == MouseEvent.BUTTON2)
		{
			pickedPoint = mouseEvent.getPoint();
			bMousePressed = true;
		}
	}
	
    public void mouseMoved(MouseEvent mouseEvent){
    	
    	super.mouseMoved(mouseEvent);
    	
    	bMouseMoved = true;
    	pickedPoint = mouseEvent.getPoint();
    }
    
	public void mouseReleased(MouseEvent mouseEvent) {
	
		super.mouseReleased(mouseEvent);
		
		bMouseReleased = true;
		pickedPoint = mouseEvent.getPoint();
	}
	
	public void mouseDragged(MouseEvent mouseEvent) {
	
		super.mouseDragged(mouseEvent);
		
		bMouseDragged = true;
		pickedPoint = mouseEvent.getPoint();
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
    	
    	boolean bTmp = bMouseReleased;
    	bMouseReleased = false;
    	return bTmp;
    }
    
    public final boolean wasMouseDragged() {
    	
    	boolean bTmp = bMouseDragged;
    	bMouseDragged = false;
    	return bTmp;
    }
    
    public final Point getPickedPoint() {
    	
    	return pickedPoint;
    }

}
