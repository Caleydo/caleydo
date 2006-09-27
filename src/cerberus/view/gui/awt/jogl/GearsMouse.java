package cerberus.view.gui.awt.jogl;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;


/**
 * Gears.java <BR>
 * author: Brian Paul (converted to Java by Ron Cemer and Sven Goethel) <P>
 *
 * This version is equal to Brian Paul's version 1.2 1999/10/21
 */

public class GearsMouse implements MouseListener, MouseMotionListener {
  

  private IJoglMouseListener refGearsMain;

  //private GearsMain refGearsMain;

  private float view_rotx = 20.0f, view_roty = 30.0f, view_rotz = 0.0f;
  
  private float view_x = 0.0f, view_y = 0.0f, view_z = 0.0f;
  
  private float fZoomSacle = 0.9f;
  
  //private float angle = 0.0f;

  private int prevMouseX, prevMouseY;
  
  private boolean bMouseRightButtonDown = false;
  
  private boolean bMouseMiddleButtonDown = false;

	public GearsMouse( final IJoglMouseListener refParentGearsMain) {
		this.refGearsMain = refParentGearsMain;
	}
	
	
  public boolean isMouseRightButtondown() {
	  return bMouseRightButtonDown;
  }
  
  public boolean isMouseMiddleButtondown() {
	  return bMouseMiddleButtonDown;
  }
  
  public float getViewRotX() {
	  return view_rotx;
  }
  
  public float getViewRotY() {
	  return view_roty;
  }
  
  public float getViewRotZ() {
	  return view_rotz;
  }

  // Methods required for the implementation of MouseListener
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}

  public void mousePressed(MouseEvent e) {
    prevMouseX = e.getX();
    prevMouseY = e.getY();
    if ((e.getModifiers() & e.BUTTON3_MASK) != 0) {
      bMouseRightButtonDown = true;
    }
    
    if ((e.getModifiers() & e.BUTTON2_MASK) != 0) {
    	bMouseMiddleButtonDown = true;
    	System.err.println(" -- Middle --");
      }
  }
    
  public void mouseReleased(MouseEvent e) {
    if ((e.getModifiers() & e.BUTTON3_MASK) != 0) {
      bMouseRightButtonDown = false;
    }
    if ((e.getModifiers() & e.BUTTON2_MASK) != 0) {
	    bMouseMiddleButtonDown = false;
	    System.err.println(" -- END Middle --");
	  }
  }
    
  public void mouseClicked(MouseEvent e) {}
    
  // Methods required for the implementation of MouseMotionListener
  public void mouseDragged(MouseEvent e) {
    int x = e.getX();
    int y = e.getY();
    Dimension size = e.getComponent().getSize();

    if  ( ! bMouseRightButtonDown ) {
	    float thetaY = 360.0f * ( (float)(x-prevMouseX)/(float)size.width);
	    float thetaX = 360.0f * ( (float)(prevMouseY-y)/(float)size.height);
	    
	    prevMouseX = x;
	    prevMouseY = y;
	
	    view_rotx += thetaX;
	    view_roty += thetaY;
	    
	    
	    //System.out.println("dragging...");
	    
	    refGearsMain.setViewAngles( view_rotx, view_roty, view_rotz );	
    	
    }
    else 
    {
    	if ( ! bMouseMiddleButtonDown ) {
		    float thetaX = fZoomSacle * ( (float)(x-prevMouseX)/(float)size.width);
		    float thetaY = fZoomSacle * ( (float)(prevMouseY-y)/(float)size.height);
		    
		    prevMouseX = x;
		    prevMouseY = y;
				    
		    view_x += thetaX;
		    view_y += thetaY;
		    
		    
		    System.out.println("dragging -PAN-...");
		    
		    refGearsMain.setTranslation( view_x, view_y, 0.0f );
		    // refGearsMain.setTranslation( view_x, view_y, view_z );
    	}
    	else
    	{
	    	float zoomX= fZoomSacle * ( (float)(x-prevMouseX)/(float)size.width);
		    float zoomY = fZoomSacle * ( (float)(prevMouseY-y)/(float)size.height);
		    
	    	prevMouseX = x;
		    prevMouseY = y;
		    
		    float fBuffer = ((float) Math.sqrt( zoomX*zoomX + zoomY * zoomY )) - 1;

		    if ( fZoomSacle < 0 ) 
		    {
		    	fBuffer *= -1;
		    }
		    
		    System.out.println("dragging -zoom-...");
		    
		    refGearsMain.setTranslation( view_x, view_y, fBuffer );
    	}
    }
  }
    
  public void mouseMoved(MouseEvent e) {}
}

