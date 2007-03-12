package cerberus.view.gui.awt;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Class indicates if the mouse was moved or not.
 * This mechanism is used to trigger the picking 
 * timer mechanism.
 * 
 * @author Marc Streit
 *
 */
public class PickingTriggerMouseAdapter 
extends MouseAdapter {
	
	protected boolean bMouseMoved = false;
	
	protected Point pickedPoint;
	
	protected boolean bMousePressed = false;
	
	public void mousePressed(MouseEvent mouseEvent) {

		if (mouseEvent.getButton() == MouseEvent.BUTTON2)
		{
			pickedPoint = mouseEvent.getPoint();
			bMousePressed = true;
		}
	}
	
    public void mouseMoved(MouseEvent e){
    	
    	bMouseMoved = true;
    	pickedPoint = e.getPoint();
    }
    
    public boolean wasMousePressed() {
    	
    	boolean bTmp = bMousePressed;
    	bMousePressed = false;
    	return bTmp;
    }
    
    public boolean wasMouseMoved() {
    	
    	boolean bTmp = bMouseMoved;
    	bMouseMoved = false;
    	return bTmp;
    }
    
    public Point getPickedPoint() {
    	
    	return pickedPoint;
    }
}
