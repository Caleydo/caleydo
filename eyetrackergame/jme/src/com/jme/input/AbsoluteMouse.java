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

/*
 * EDIT:  02/08/2004 - Added update(boolean updateState) to allow for a
 *                      WidgetViewport to update an AbstractInputHandler
 *                      without polling the mouse.  GOP
 */

package com.jme.input;

import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;

/**
 * <code>AbsoluteMouse</code> defines a mouse object that maintains a position
 * within the window. Each call to update adjusts the current position by the
 * change in position since the previous update. The mouse is forced to be
 * contained within the values provided during construction (typically these
 * correspond to the width and height of the window).
 *
 * @author Mark Powell
 * @author Gregg Patton
 * @version $Id: AbsoluteMouse.java 4666 2009-09-04 17:22:52Z skye.book $
 */
public class AbsoluteMouse extends Mouse {

    private static final long serialVersionUID = 1L;

    private boolean usingDelta = false;
    private int limitHeight, limitWidth;
    /**
     * @return true if mouse position delta are used to compute the absolute position, false if the absolute
     * mouse coordinates are used directly
     */
    public boolean isUsingDelta() {
        return usingDelta;
    }

    /**
     * @param usingDelta true to compute the absolute position from mouse position delta, false to use the absolute
     * mouse coordinates directly
     */
    public void setUsingDelta( boolean usingDelta ) {
        this.usingDelta = usingDelta;
    }

    //position
    private InputAction xUpdateAction = new InputAction() {
        public void performAction( InputActionEvent evt ) {
            if ( isUsingDelta() ) {
                localTranslation.x += evt.getTriggerDelta() * limitWidth * speed; //speed of the action!
            } else {
                localTranslation.x = evt.getTriggerPosition() * limitWidth * speed - hotSpotOffset.x;
            }

            if ( localTranslation.x + hotSpotOffset.x < 0 ) {
                localTranslation.x = -hotSpotOffset.x;
            }
            else if ( localTranslation.x + hotSpotOffset.x > limitWidth ) {
                localTranslation.x = width - hotSpotOffset.x;
            }
            worldTranslation.x = localTranslation.x;
            hotSpotLocation.x = localTranslation.x + hotSpotOffset.x;
        }
    };
    private InputAction yUpdateAction = new InputAction() {
        public void performAction( InputActionEvent evt ) {
            if ( isUsingDelta() ) {
                localTranslation.y += evt.getTriggerDelta() * limitHeight * speed;  //speed of the action!
            } else {
                localTranslation.y = evt.getTriggerPosition() * limitHeight * speed - hotSpotOffset.y;
            }

            if ( localTranslation.y + hotSpotOffset.y < 0 /*- imageHeight*/ ) {
                localTranslation.y = 0/* - imageHeight*/ - hotSpotOffset.y;
            }
            else if ( localTranslation.y + hotSpotOffset.y > limitHeight ) {
                localTranslation.y = height - hotSpotOffset.y;
            }
            worldTranslation.y = localTranslation.y;
            hotSpotLocation.y = localTranslation.y + hotSpotOffset.y;
        }
    };
    private InputHandler registeredInputHandler;

    /**
     * Constructor instantiates a new <code>AbsoluteMouse</code> object. The
     * limits of the mouse movements are provided.
     *
     * @param name   the name of the scene element. This is required for
     *               identification and comparison purposes.
     * @param limitWidth  the width of the mouse's limit.
     * @param limitHeight the height of the mouse's limit.
     */
    public AbsoluteMouse( String name, int limitWidth, int limitHeight ) {
        super( name );
        this.limitWidth = limitWidth;
        this.limitHeight = limitHeight;
        getXUpdateAction().setSpeed( 1 );
        getYUpdateAction().setSpeed( 1 );
    }

    /**
     * set the mouse's limit.
     *
     * @param limitWidth  the width of the mouse's limit.
     * @param limitHeight the height of the mouse's limit.
     */
    public void setLimit( int limitWidth, int limitHeight ) {
        this.limitWidth = limitWidth;
        this.limitHeight = limitHeight;
    }

    public void setSpeed( float speed ) {
        getXUpdateAction().setSpeed( speed );
        getYUpdateAction().setSpeed( speed );
    }

    /**
     * Registers the xUpdateAction and the yUpdateAction with axis 0 and 1 of the mouse.
     * Note: you can register the actions with other devices, too, instead of calling this method.
     * @param inputHandler handler to register with (null to unregister)
     * @see #getXUpdateAction()
     * @see #getYUpdateAction()
     */
    public void registerWithInputHandler( InputHandler inputHandler ) {
        if ( registeredInputHandler != null ) {
            registeredInputHandler.removeAction( getXUpdateAction() );
            registeredInputHandler.removeAction( getYUpdateAction() );
        }
        registeredInputHandler = inputHandler;
        if ( inputHandler != null ) {
            inputHandler.addAction( getXUpdateAction(), InputHandler.DEVICE_MOUSE, InputHandler.BUTTON_NONE, 0, false );
            inputHandler.addAction( getYUpdateAction(), InputHandler.DEVICE_MOUSE, InputHandler.BUTTON_NONE, 1, false );
        }
    }

    public InputAction getXUpdateAction() {
        return xUpdateAction;
    }

    public InputAction getYUpdateAction() {
        return yUpdateAction;
    }
}