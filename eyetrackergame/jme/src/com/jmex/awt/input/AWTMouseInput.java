/*
 * Copyright (c) 2003-2009 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jmex.awt.input;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;

import com.jme.image.Image;
import com.jme.input.InputSystem;
import com.jme.input.MouseInput;
import com.jme.input.MouseInputListener;

/**
 * <code>AWTMouseInput</code>
 * 
 * @author Joshua Slack
 * @version $Revision: 4133 $
 */
public class AWTMouseInput extends MouseInput implements MouseListener, MouseWheelListener, MouseMotionListener {

    public static int WHEEL_AMP = 40;   // arbitrary...  Java's mouse wheel seems to report something a lot lower than lwjgl's

    private int currentWheelDelta;
    private int wheelDelta;
    private int wheelRotation;
    private boolean enabled = true;
    private boolean dragOnly = false;
    private BitSet buttons = new BitSet(3);

    private Point absPoint = new Point();
    private Point lastPoint = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
    private Point currentDeltaPoint = new Point(0,0);
    private Point deltaPoint = new Point(0,0);

    private Component deltaRelative;
    
    private boolean isCursorVisible = true;      
    private static Cursor transparentCursor = null;   
    private Cursor opaqueCursor = null;

    protected AWTMouseInput() {
        // Nothing to do
    }

    @Override
    protected void destroy() {
        // Nothing to do
    }

    @Override
    public int getButtonIndex(String buttonName) {
		if ("MOUSE0".equalsIgnoreCase(buttonName)) {
			return 0;
		}
		else if ("MOUSE1".equalsIgnoreCase(buttonName)) {
			return 1;
		}
		else if ("MOUSE2".equalsIgnoreCase(buttonName)) {
			return 2;
		}

        throw new IllegalArgumentException("invalid buttonName: "+buttonName);
    }

    @Override
    public boolean isButtonDown(int buttonCode) {
        return buttons.get(buttonCode);
    }

    @Override
    public String getButtonName(int buttonIndex) {
        switch (buttonIndex) {
        case 0:
            return "MOUSE0";
        case 1:
            return "MOUSE1";
        case 2:
            return "MOUSE2";
        }
        throw new IllegalArgumentException("invalid buttonIndex: "+buttonIndex);
    }

    @Override
    public int getWheelDelta() {
        return wheelDelta;
    }

    @Override
    public int getXDelta() {
        if (deltaRelative != null) {
			if (!enabled) {
				return 0;
			}
            int rVal = (deltaRelative.getWidth() / 2) - absPoint.x;
            return (int)(rVal * -0.01f);
        } 
             
        return deltaPoint.x;
    }

    @Override
    public int getYDelta() {
        if (deltaRelative != null) {
			if (!enabled) {
				return 0;
			}
            int rVal = (deltaRelative.getHeight() / 2) - absPoint.y;
            return (int)(rVal * 0.05f);
        } 
            
        return deltaPoint.y;
    }

    @Override
    public int getXAbsolute() {
        return absPoint.x;
    }

    @Override
    public int getYAbsolute() {
        return absPoint.y;
    }

    /**
     * Swing events are put in here in the swing thread and removed from it in the update method.
     * To flatline memory usage the LinkedList could be replaced by two ArrayLists but then one
     * would need to synchronize insertions.
     */
    private List<MouseEvent> swingEvents = new LinkedList<MouseEvent>();
    /**
     * x position of last event that was processed by {@link #update}
     */
    private int lastEventX;
    /**
     * y position of last event that was processed by {@link #update}
     */
    private int lastEventY;

    @Override
    public void update() {
        int x = lastEventX;
        int y = lastEventY;

        if ( listeners != null && !listeners.isEmpty() )
        {
            while ( !swingEvents.isEmpty() )
            {
                MouseEvent event = swingEvents.remove( 0 );

                switch ( event.getID() ) {
                    case MouseEvent.MOUSE_DRAGGED:
                    case MouseEvent.MOUSE_MOVED:
                        for ( int i = 0; i < listeners.size(); i++ ) {
                            MouseInputListener listener = listeners.get( i );
                            listener.onMove( event.getX() - x, y - event.getY(), event.getX(), event.getY() );
                        }
                        x = event.getX();
                        y = event.getY();
                        break;
                    case MouseEvent.MOUSE_PRESSED:
                    case MouseEvent.MOUSE_RELEASED:
                        for ( int i = 0; i < listeners.size(); i++ ) {
                            MouseInputListener listener = listeners.get( i );
                            listener.onButton( getJMEButtonIndex( event ), event.getID() == MouseEvent.MOUSE_PRESSED, event.getX(), event.getY() );
                        }
                        break;
                    case MouseEvent.MOUSE_WHEEL:
                        for ( int i = 0; i < listeners.size(); i++ ) {
                            MouseInputListener listener = listeners.get( i );
                            listener.onWheel( ((MouseWheelEvent)event).getUnitsToScroll()*WHEEL_AMP, event.getX(), event.getY() );
                        }
                        break;
                    default:
                }
            }
        }
        else
        {
            swingEvents.clear();
        }

        lastEventX = x;
        lastEventY = y;
        wheelDelta = currentWheelDelta;
        currentWheelDelta = 0;
        deltaPoint.setLocation(currentDeltaPoint);
        currentDeltaPoint.setLocation(0,0);
    }

	@Override
    public void setCursorVisible(boolean v) {
        Frame[] framesArray = Frame.getFrames( );
        //FIXME: get only the ownerless windows?        
        this.isCursorVisible = v;
        for( Frame applicationFrame : framesArray ) {
             setCursorRecursively( applicationFrame );
        } 
    }
	
	/**
	 * Sets the cursor of the component and its subcomponents
	 * @param component
	 */
	private final void setCursorRecursively( Component component ) {
	    component.setCursor( isCursorVisible ? opaqueCursor : getTransparentCursor() );
	    if( component instanceof Container ) {
	        for( Component subComponent : ( ( Container ) component ).getComponents() ) {
	            setCursorRecursively( subComponent );
	        }
	    }
	} 
	   
	private static final Cursor getTransparentCursor() {
	    if( transparentCursor == null ) {
	        BufferedImage cursorImage=new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
	        cursorImage.setRGB(0,0,0);
	        transparentCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImage,new Point(0,0),"empty cursor");      
	    }
	    return( transparentCursor );
	}
	   
	

	@Override
    public boolean isCursorVisible() {
        return( isCursorVisible );
    }

    @Override
	public void setHardwareCursor(URL file) {
	    setHardwareCursor( file , 0 , 0 );
	}

    public void setHardwareCursor( Cursor cursor ) {
        opaqueCursor = cursor;
    }
    
    @Override
	public void setHardwareCursor(URL file, int xHotspot, int yHotspot) {
	    //Create the image from the provided url
	    java.awt.Image cursorImage = new ImageIcon( file ).getImage( );
	    //Create a custom cursor with this image
	    opaqueCursor = Toolkit.getDefaultToolkit().createCustomCursor( cursorImage , new Point( xHotspot , yHotspot ) , "custom cursor" );
	    //Use this cursor
	    setCursorVisible( isCursorVisible );
	}

    @Override
    public void setHardwareCursor(URL file, Image[] images, int[] delays, int xHotspot, int yHotspot) {
		// Ignored.
    }
    
    @Override
    public void setCursorPosition(int x, int y) {
    	absPoint.setLocation( x,y);
    }

    @Override
    public int getWheelRotation() {
        return wheelRotation;
    }

    @Override
    public int getButtonCount() {
        return 3;
    }

    public void setRelativeDelta(Component c) {
        deltaRelative = c;
    }

    /**
     * @return Returns the enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled The enabled to set.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return Returns the dragOnly.
     */
    public boolean isDragOnly() {
        return dragOnly;
    }

    /**
     * @param dragOnly The dragOnly to set.
     */
    public void setDragOnly(boolean dragOnly) {
        this.dragOnly = dragOnly;
    }

    // **********************************
    // java.awt.event.MouseListener methods
    // **********************************

    public void mouseClicked(MouseEvent arg0) {
        ; // ignore
    }

    public void mousePressed(MouseEvent arg0) {
		if (!enabled) {
			return;
		}
        lastPoint.setLocation(arg0.getPoint());

        buttons.set( getJMEButtonIndex( arg0 ), true);

        swingEvents.add( arg0 );
    }

    private int getJMEButtonIndex( MouseEvent arg0 ) {
        int index;
        switch (arg0.getButton()) {
            default:
            case MouseEvent.BUTTON1: //left
                index = 0;
                break;
            case MouseEvent.BUTTON2: //middle
                index = 2;
                break;
            case MouseEvent.BUTTON3: //right
                index = 1;
                break;
        }
        return index;
    }

    public void mouseReleased(MouseEvent arg0) {
		if (!enabled) {
			return;
		}
        currentDeltaPoint.setLocation(0,0);
        if (deltaRelative != null) {
            absPoint.setLocation(deltaRelative.getWidth() >> 1, deltaRelative.getHeight() >> 1);
        }

        buttons.set(getJMEButtonIndex( arg0 ), false);

        swingEvents.add( arg0 );
    }

    public void mouseEntered(MouseEvent arg0) {
        ; // ignore for now
    }

    public void mouseExited(MouseEvent arg0) {
        ; // ignore for now
    }


    // **********************************
    // java.awt.event.MouseWheelListener methods
    // **********************************

    public void mouseWheelMoved(MouseWheelEvent arg0) {
		if (!enabled) {
			return;
		}

        final int delta = arg0.getUnitsToScroll() * WHEEL_AMP;
        currentWheelDelta -= delta;
        wheelRotation -= delta;

        swingEvents.add( arg0 );
    }


    // **********************************
    // java.awt.event.MouseMotionListener methods
    // **********************************

    public void mouseDragged(MouseEvent arg0) {
		if (!enabled) {
			return;
		}

        absPoint.setLocation(arg0.getPoint());
		if (lastPoint.x == Integer.MIN_VALUE) {
			lastPoint.setLocation(absPoint.x, absPoint.y);
		}
        currentDeltaPoint.x = absPoint.x-lastPoint.x;
        currentDeltaPoint.y = -(absPoint.y-lastPoint.y);
        lastPoint.setLocation(arg0.getPoint());

        swingEvents.add( arg0 );
    }

    public void mouseMoved(MouseEvent arg0) {
		if (enabled && !dragOnly) {
			mouseDragged(arg0);
		}
    }

    
    

    /**
     * Set up a canvas to fire mouse events via the input system.
     * @param glCanvas canvas that should be listened to
     * @param dragOnly true to enable mouse input to jME only when the mouse is dragged
     */
    public static void setup( Canvas glCanvas, boolean dragOnly ) {
    	if (!isInited()) {
    		setProvider( InputSystem.INPUT_SYSTEM_AWT );
    	}
        AWTMouseInput awtMouseInput = ( (AWTMouseInput) get() );
        awtMouseInput.setEnabled( !dragOnly );
        awtMouseInput.setDragOnly( dragOnly );
        awtMouseInput.setRelativeDelta( glCanvas );
        glCanvas.addMouseListener(awtMouseInput);
        glCanvas.addMouseWheelListener(awtMouseInput);
        glCanvas.addMouseMotionListener(awtMouseInput);
    }
    
    public void clear() {
    	this.buttons.clear();
    }
    
    public void clearButton(int buttonCode) {
    	buttons.set(buttonCode, false);
    }
}
