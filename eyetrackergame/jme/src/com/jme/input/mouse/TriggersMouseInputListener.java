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

package com.jme.input.mouse;

import java.util.ArrayList;

import com.jme.input.ActionTrigger;
import com.jme.input.MouseInput;
import com.jme.input.MouseInputListener;
import com.jme.system.DisplaySystem;

/**
 * Each {@link MouseInputHandlerDevice} has an instance of this class which is subscribed at the
 * {@link com.jme.input.MouseInput} to receive mouse events and forward them to the mouse triggers.
 */
class TriggersMouseInputListener implements MouseInputListener {
    private float maxWheel = 120;
    public static final boolean DO_CLAMP = false;

    public TriggersMouseInputListener() {
    }

    public void activate() {
        MouseInput.get().addListener( this );
    }
    
    public boolean isActivated() {
        return MouseInput.get().containsListener(this);
    }
    
    public void deactivate() {
        MouseInput.get().removeListener( this );
    }

    private ArrayList<MouseInputHandlerDevice.MouseButtonTrigger> buttonTriggers = new ArrayList<MouseInputHandlerDevice.MouseButtonTrigger>();
    private ArrayList<MouseInputHandlerDevice.MouseAxisTrigger> axisTriggers = new ArrayList<MouseInputHandlerDevice.MouseAxisTrigger>();

    // javadoc copied from overwritten method
    public void onButton( int button, boolean pressed, int x, int y ) {
        for ( int i = buttonTriggers.size() - 1; i >= 0; i-- ) {
            final ActionTrigger trigger = buttonTriggers.get( i );
            trigger.checkActivation( '\0', button, Float.NaN, Float.NaN, pressed, null );
        }
    }

    // javadoc copied from overwritten method
    public void onWheel( int wheelDelta, int x, int y ) {
        float pos = clamp( MouseInput.get().getWheelRotation() / maxWheel );
        float delta = clamp( wheelDelta / maxWheel );
        for ( int i = axisTriggers.size() - 1; i >= 0; i-- ) {
            final ActionTrigger trigger = axisTriggers.get( i );
            trigger.checkActivation( '\0', 2, pos, delta, false, null );
        }
    }

    // javadoc copied from overwritten method
    public void onMove( int xDelta, int yDelta, int newX, int newY ) {
    	float posX = clamp( newX / (float)DisplaySystem.getDisplaySystem().getWidth() );
        float posY = clamp( newY / (float)DisplaySystem.getDisplaySystem().getHeight() );
        float deltaX = clamp( xDelta / (float)DisplaySystem.getDisplaySystem().getWidth() );
        float deltaY = clamp( yDelta / (float)DisplaySystem.getDisplaySystem().getHeight() );
        for ( int i = axisTriggers.size() - 1; i >= 0; i-- ) {
            final ActionTrigger trigger = axisTriggers.get( i );
            if ( xDelta != 0 ) {
                trigger.checkActivation( '\0', 0, posX, deltaX, DO_CLAMP, null );
            }
            if ( yDelta != 0 ) {
                trigger.checkActivation( '\0', 1, posY, deltaY, DO_CLAMP, null );
            }
        }
    }

    void add( MouseInputHandlerDevice.MouseButtonTrigger trigger ) {
        buttonTriggers.add( trigger );
    }

    void remove( MouseInputHandlerDevice.MouseButtonTrigger trigger ) {
        buttonTriggers.remove( trigger );
    }

    void add( MouseInputHandlerDevice.MouseAxisTrigger trigger ) {
        axisTriggers.add( trigger );
    }

    void remove( MouseInputHandlerDevice.MouseAxisTrigger trigger ) {
        axisTriggers.remove( trigger );
    }

    /**
     * @param value any float value
     * @return float value clamped to [-1;1] if {@link #DO_CLAMP} is true, otherwise returns value
     */
    private static float clamp( float value ) {
        if ( DO_CLAMP ) {
            if ( value > 1 ) {
                return 1;
            }
            if ( value < -1 ) {
                return -1;
            }
        }
        return value;
    }
}
