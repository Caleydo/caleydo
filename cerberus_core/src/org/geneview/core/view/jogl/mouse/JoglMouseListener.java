package org.geneview.core.view.jogl.mouse;

import gleem.linalg.Vec3f;
import gleem.linalg.Rotf;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.Point;

import org.geneview.core.view.jogl.IJoglMouseListener;
import org.geneview.core.data.view.camera.IViewCamera;
import org.geneview.core.math.MathUtil;


/**
 * Jogl mouse event listener
 * 
 * @author Michael Kalkusch
 */

public class JoglMouseListener implements MouseListener, MouseMotionListener {
	
	
  private IJoglMouseListener refParentGearsMain;
  
  protected float fZoomScale = 0.072f;
  
  protected float fPanScale = 3.1f;

  protected int prevMouseX, prevMouseY;
  
  protected Point pressedMousePosition;
  
  protected boolean bMouseRightButtonDown = false;
  
  protected boolean bMouseMiddleButtonDown = false;
  
  protected boolean bMouseLeft_StandbyZoom = false;
  
  protected boolean bMouseRight_StandbyRotate = false;

  /**
   * Define mouse sensitivity. 
   * Higher value indicates more degrees of rotation.
   * Default value 1.0
   * 
   */
  protected float fMouseSensitivityRotation = 1.0f;

  
	public JoglMouseListener( final IJoglMouseListener refParentGearsMain) {
		assert refParentGearsMain != null : "Can not handle null-pointer";
		
		this.refParentGearsMain = refParentGearsMain;
		
		pressedMousePosition = new Point();
	}
	
	public final IJoglMouseListener getJoglMouseListener() {
		return this.refParentGearsMain;
	}
	
	public final void setJoglMouseListener(final IJoglMouseListener setJoglMouseListener) {
		this.refParentGearsMain = setJoglMouseListener;
	}
	
  public final void setMouseSensitivityRotation( float fSetMouseSensitivityRotation ) {
	  this.fMouseSensitivityRotation = fSetMouseSensitivityRotation;
  }
  
  public final float getMouseSensitivityRotation() {
	  return this.fMouseSensitivityRotation;
  }
	
  public final boolean isMouseRightButtondown() {
	  return bMouseRightButtonDown;
  }
  
  public final boolean isMouseMiddleButtondown() {
	  return bMouseMiddleButtonDown;
  }  

  public final Point getPressedMousePosition() {
	  return pressedMousePosition;
  }
  
  /**
   * Register this Listener to the canvas as MouseListener and MouseMotionListener.
   * @param canvas
   */
//  public void addMouseListenerAll(GLAutoDrawable canvas) {
//	  canvas.addMouseListener(this);
//	  canvas.addMouseMotionListener(this);
//  }

  /* Methods required for the implementation of MouseListener */
  public void mouseEntered(MouseEvent e) {}
  
  public void mouseExited(MouseEvent e) {}
  
  public void mouseClicked(MouseEvent e) {}
  
  public void mouseMoved(MouseEvent e) {}  
  
  public void mousePressed(MouseEvent e) {
    prevMouseX = e.getX();
    prevMouseY = e.getY();
    
    pressedMousePosition.x = prevMouseX;
    pressedMousePosition.y = prevMouseY;
    
    /* --- Left -- Mouse Button --- */
    if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
    	//System.err.println(" -- Left --");
    	
    	if (bMouseRight_StandbyRotate) {
    		/* first button was "right" and 
    		 * "right"-button is still pressed */
    		bMouseRightButtonDown = false;
    	}
    	else {
    		/* enable standby zoom...
    		 * First button pressed is "left" */
    		bMouseLeft_StandbyZoom = true;
    	}
    }
    
    /* --- Right -- Mouse Button --- */
    if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
    	//System.err.println(" -- Right --");
    	
    	if ( bMouseLeft_StandbyZoom ) 
    	{
    		/* first button pressed was "left" and 
    		 * it is still pressed, but also "right" button is pressed now..
    		 * ==> emmulate middle mouse button! */
    		bMouseMiddleButtonDown = true;
    	}
    	else {
    		/* first button pressed was NOT "left" and 
    		 * but now "right" button is pressed.. */
    	
    		bMouseRightButtonDown = true;
    		bMouseRight_StandbyRotate = true;
    	}
    }
    
    /* --- Middle -- Mouse Button --- */
    if ((e.getModifiers() & MouseEvent.BUTTON2_MASK) != 0) {    	
    	bMouseMiddleButtonDown = true;    	
    	//System.err.println(" -- Middle --");
      }
  }
    
  public void mouseReleased(MouseEvent e) {

		if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0)
		{
			bMouseLeft_StandbyZoom = false;
			bMouseMiddleButtonDown = false;
			
			if (bMouseRight_StandbyRotate) 
			{
				/* first button pressed was "right" and
				 * now "left" button is released
				 * ==> same state as if only "right" button was pressed. */
				bMouseRightButtonDown = true;
			}
			//System.err.println(" -- End Left --");
		}

		if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0)
		{

			if ( bMouseLeft_StandbyZoom ) 
			{
				/* First "left" button was pressed and not released since and
				 * "right" button was pressed now
				 * ==> emmulate "middle" button
				 */
				bMouseMiddleButtonDown = false;
			}
			else {
				bMouseRightButtonDown = false;
				
				/* Now "right" button is released
				 * ==> no more standby RightButton. */
				bMouseRight_StandbyRotate = false;
			}
			//System.err.println(" -- End Right --");
		}
		
		if ((e.getModifiers() & MouseEvent.BUTTON2_MASK) != 0)
		{
			bMouseMiddleButtonDown = false;
			//System.err.println(" -- END Middle --");
		}
	}
  
  
  // Methods required for the implementation of MouseMotionListener
  public void mouseDragged(MouseEvent e) {
    int x = e.getX();
    int y = e.getY();
    Dimension size = e.getComponent().getSize();

    //System.out.println("JoglMouseListener  +--> dragging... " + this.toString() );
    
    if  ( ! bMouseRightButtonDown ) {
    	
//    	if ( ! bMouseMiddleButtonDown ) {
//	    
//		/**
//    	 *   --- ROTATION ---
//    	 */
//    		
//    	Rotf currentRotX = new Rotf();
//    	Rotf currentRotY = new Rotf();
//   	    
//    	float fpercentX = (float)(x-prevMouseX)/(float)(size.width) 
//    		* fMouseSensitivityRotation;
//    	
//    	float fpercentY = (float)(y-prevMouseY)/(float)(size.height) 
//    		* fMouseSensitivityRotation;
//    	
//    	
//   	    currentRotX.set( Vec3f.Y_AXIS, 
//   	    		fpercentX* MathUtil.PI);
//   	    
//   	    currentRotY.set( Vec3f.X_AXIS, 
//	    		fpercentY* MathUtil.PI);
//   	    
//   	    /* concatinate roations.. */
//   	    currentRotX = currentRotX.times(currentRotY);
//   	    
//	    prevMouseX = x;
//	    prevMouseY = y;
//	    
//	    /* set new paramters to ViewCamera */
//	    refParentGearsMain.getViewCamera().addCameraRotation(currentRotX);
//	    
//    	}
//	    else
//    	{
//	    	/**
//	    	 *   --- ZOOMING ---
//	    	 */
//	    	
////	    	float zoomX = fZoomScale * ( (float)(x-prevMouseX)/(float)size.width);
////		    float zoomY = fZoomScale * ( (float)(prevMouseY-y)/(float)size.height);
//		    
//		    float zoomX = fZoomScale * (float)(x-prevMouseX);
//		    float zoomY = fZoomScale * (float)(prevMouseY-y);
//		    
//		    /* take abs(zoomX) */
//		    if ((zoomX < 0.0f )&&(zoomY>0.0f)) {
//		    	zoomX = -zoomX;
//		    }
//		    
//	    	prevMouseX = x;
//		    prevMouseY = y;
//		    
//		    /* set new paramters to ViewCamera */
//		    IViewCamera bufferCamera = refParentGearsMain.getViewCamera();
//		    bufferCamera.addCameraScale(
//		    		new Vec3f( 0, 
//		    				0, 
//		    				zoomY +zoomX) );
//		    
//		    System.err.println(" zoom:A " + bufferCamera.getId() + "] ==> " + 
//		    		refParentGearsMain.toString() + " ==> " +
//		    		refParentGearsMain.getViewCamera().toString() + 
//		    		" <== " + refParentGearsMain.getViewCamera().getId());
//		    
//		    System.err.println(" zoom:B " + bufferCamera.getId() + "] ==> " + 
//		    		bufferCamera.toString() + " ==> " +
//		    		bufferCamera.getId() + 
//		    		" <== " + refParentGearsMain.getViewCamera().getId());
//
//    	}
    	
    }
    else 
    {
    	/**
    	 *   --- PANING ---
    	 */
		Vec3f addVec3f = new Vec3f( 
				fPanScale * ( (float)(x-prevMouseX)/(float)size.width),
				fPanScale * ( (float)(prevMouseY-y)/(float)size.height),
				0.0f);
	    
	    prevMouseX = x;
	    prevMouseY = y;	    
	    
	    /* set new paramters to ViewCamera */
	    IViewCamera bufferCamera = refParentGearsMain.getViewCamera();
	    
	    //System.err.println("  pan: " + bufferCamera.getId() + "]");
	    
	    bufferCamera.addCameraPosition(addVec3f);	   
    }
  }
  
}

