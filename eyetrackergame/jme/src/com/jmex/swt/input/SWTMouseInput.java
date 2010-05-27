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
package com.jmex.swt.input;

import java.net.URL;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.jme.image.Image;
import com.jme.input.MouseInput;
import com.jme.input.MouseInputListener;

/**
 * Note: portions originally from the jme-swt source.
 */
public class SWTMouseInput extends MouseInput implements MouseListener,
		MouseMoveListener, Listener, DragDetectListener {

	private int currentWheelDelta;
	private int wheelDelta;
	private int wheelRotation;
	private boolean enabled = true;
    private boolean dragOnly = false;
	private boolean dragging=false;
	private boolean doubleclick=false;
	private BitSet buttons = new BitSet(3);

	private Point absPoint = new Point(0,0);
	private Point lastPoint = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
	private Point currentDeltaPoint = new Point(0,0);
	private Point deltaPoint = new Point(0,0);

	protected SWTMouseInput() {
        // Nothing to do
    }

	@Override
	protected void destroy() {
        // Nothing to do
	}

	@Override
	public int getButtonIndex(String buttonName) {
		if ("MOUSE0".equalsIgnoreCase(buttonName)) return 0;
		else if ("MOUSE1".equalsIgnoreCase(buttonName)) return 1;
		else if ("MOUSE2".equalsIgnoreCase(buttonName)) return 2;

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
        return deltaPoint.x;
	}

	@Override
	public int getYDelta() {
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
     * SWT events are put in here in the swing thread and removed from it in the update method.
     * To flatline memory usage the LinkedList could be replaced by two ArrayLists but then one
     * would need to synchronize insertions.
     */
    private List<MouseEvent> swtEvents = new LinkedList<MouseEvent>();
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
            while ( !swtEvents.isEmpty() )
            {
                MouseEvent event = swtEvents.remove( 0 );

                switch ( (EventType)event.data ) {
                    case MOUSE_DRAGGED:
                    case MOUSE_MOVED:
                        for ( int i = 0; i < listeners.size(); i++ ) {
                            MouseInputListener listener = listeners.get( i );
                            listener.onMove( event.x - x, y - event.y, event.x, event.y );
                        }
                        x = event.x;
                        y = event.y;
                        break;
                    case MOUSE_PRESSED:
                    case MOUSE_RELEASED:
                        for ( int i = 0; i < listeners.size(); i++ ) {
                            MouseInputListener listener = listeners.get( i );
                            listener.onButton( getJMEButtonIndex( event.button ), EventType.MOUSE_PRESSED.equals(event.data), event.x, event.y );
                			if (doubleclick && (listener instanceof SWTMouseInputListener)) {
                				((SWTMouseInputListener) listener).onDoubleClick(lastPoint.x, lastPoint.y);
                			}
                        }
                        break;
                    case MOUSE_WHEEL:
                        for ( int i = 0; i < listeners.size(); i++ ) {
                            MouseInputListener listener = listeners.get( i );
                            listener.onWheel( event.count, event.x, event.y );
                        }
                        break;
                    default:
                }
            }
        }
        else
        {
            swtEvents.clear();
        }

        lastEventX = x;
        lastEventY = y;
        wheelDelta = currentWheelDelta;
        currentWheelDelta = 0;
		deltaPoint.x=currentDeltaPoint.x;
		deltaPoint.y=currentDeltaPoint.y;
		currentDeltaPoint.x=0;
		currentDeltaPoint.y=0;
		doubleclick = false;
    }

	@Override
	public void setCursorVisible(boolean v) {
		// Ignored.
	}

	@Override
	public boolean isCursorVisible() {
        // always true
		return true;
	}

    @Override
	public void setHardwareCursor(URL file) {
		// Ignored.
	}

    @Override
	public void setHardwareCursor(URL file, int xHotspot, int yHotspot) {
		// Ignored.
	}

    @Override
    public void setHardwareCursor(URL file, Image[] images, int[] delays, int xHotspot, int yHotspot) {
		// Ignored.
    }

	@Override
	public void setCursorPosition(int x, int y) {
		Display.getCurrent().setCursorLocation(x, y);
	}

	@Override
	public int getWheelRotation() {
		return wheelRotation;
	}

	@Override
	public int getButtonCount() {
		return 3;
	}

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
    // org.eclipse.swt.events.MouseListener methods
    // **********************************

	public void mouseDoubleClick(MouseEvent e) {
		if (!enabled) return;
		doubleclick = true;
		if (e.data == null) {
			e.data = EventType.MOUSE_PRESSED;
		}
		swtEvents.add( e );
	}

	public void mouseDown(MouseEvent e) {

		if (!enabled) return;
		lastPoint.x=e.x;
		lastPoint.y=e.y;

		buttons.set(e.button-1, true);
		if (e.data == null) {
			e.data = EventType.MOUSE_PRESSED;
		}
		swtEvents.add( e );
	}

	public void mouseUp(MouseEvent e) {
		if (!enabled) {
			return;
		}
		dragging = false;
		
		currentDeltaPoint.x = 0;
		currentDeltaPoint.y = 0;
		buttons.set(e.button - 1, false);
		if (e.data == null) {
			e.data = EventType.MOUSE_RELEASED;
		}

		swtEvents.add( e );
	}

	
    // **********************************
    // org.eclipse.swt.events.MouseMoveListener methods
    // **********************************
	
	public void mouseMove(MouseEvent e) {
		if (!enabled || (dragOnly && !dragging)) {
			return;
		}
		absPoint.x=e.x;
		absPoint.y=e.y;
		if (lastPoint.x == Integer.MIN_VALUE) {
			lastPoint.x=absPoint.x;
			lastPoint.y=absPoint.y;
		}

		currentDeltaPoint.x = absPoint.x-lastPoint.x;
		currentDeltaPoint.y = -(absPoint.y-lastPoint.y);

		lastPoint.x= e.x;
		lastPoint.y= e.y;

		if (e.data == null) {
			if (dragging)
				e.data = EventType.MOUSE_DRAGGED;
			else
				e.data = EventType.MOUSE_MOVED;
		}
		swtEvents.add( e );
	}

	
    // **********************************
    // org.eclipse.swt.events.DragDetectListener methods
    // **********************************
	
	public void dragDetected(DragDetectEvent e) {
		if (!enabled) {
			return;
		}

		dragging = true;
	}

	
    // **********************************
    // org.eclipse.swt.widgets.Listener methods
    // **********************************

	//mouse wheel
	public void handleEvent(Event e){
		if (!enabled) {
			return;
		}

		int c = e.count;
		currentWheelDelta += c;
        wheelRotation += c;
		
		if (e.data == null) {
			e.data = EventType.MOUSE_DRAGGED;
		}
		swtEvents.add( new MouseEvent(e) );
	} 


    /**
     * Set up a canvas to fire mouse events via the input system.
     * @param glCanvas canvas that should be listened to
     * @param dragOnly true to enable mouse input to jME only when the mouse is dragged
     */
    public static void setup( Composite canvas, boolean dragOnly ) {
    	if (!MouseInput.isInited()) {
    		setProvider(SWTMouseInput.class.getCanonicalName());
    	}
        SWTMouseInput swtMouseInput = ( (SWTMouseInput) get() );
        swtMouseInput.setDragOnly( dragOnly );
        canvas.addMouseListener((SWTMouseInput) MouseInput.get());
        canvas.addMouseMoveListener((SWTMouseInput) MouseInput.get());
        canvas.addDragDetectListener((SWTMouseInput) MouseInput.get());
        canvas.addListener(SWT.MouseWheel, (SWTMouseInput) MouseInput.get());
    }

	
    public void clear() {
    	this.buttons.clear();
    }
    
    public void clearButton(int buttonCode) {
    	buttons.set(buttonCode, false);
    }

    private int getJMEButtonIndex( int swtButton ) {
        int index;
        switch (swtButton) {
            default:
            case 1: //left
                index = 0;
                break;
            case 2: //middle
                index = 2;
                break;
            case 3: //right
                index = 1;
                break;
        }
        return index;
    }

    enum EventType {
    	MOUSE_DRAGGED,
    	MOUSE_MOVED,
    	MOUSE_PRESSED,
    	MOUSE_RELEASED,
    	MOUSE_WHEEL
    }
}
