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

package com.jme.input.joystick;

import java.util.ArrayList;

import com.jme.input.ActionTrigger;

/**
 * Each {@link JoystickInputHandlerDevice} has an instance of this class which is subscribed at the
 * {@link JoystickInput} to receive joystick events and forward them to the joystick triggers.
 */
class TriggersJoystickInputListener implements JoystickInputListener {

    public TriggersJoystickInputListener() {
    }

    public void activate() {
        JoystickInput.get().addListener( this );
    }

    public void deactivate() {
        JoystickInput.get().removeListener( this );
    }

    private ArrayList<JoystickInputHandlerDevice.JoystickButtonTrigger> buttonTriggers = new ArrayList<JoystickInputHandlerDevice.JoystickButtonTrigger>();
    private ArrayList<JoystickInputHandlerDevice.JoystickAxisTrigger> axisTriggers = new ArrayList<JoystickInputHandlerDevice.JoystickAxisTrigger>();

    public void onAxis( Joystick controller, int axis, float axisValue ) {
        float pos = axisValue;
        float delta = Float.NaN;
        for ( int i = axisTriggers.size() - 1; i >= 0; i-- ) {
            final ActionTrigger trigger = axisTriggers.get( i );
            trigger.checkActivation( '\0', axis, pos, delta, false, controller );
        }
    }

    public void onButton( Joystick controller, int button, boolean pressed ) {
        for ( int i = buttonTriggers.size() - 1; i >= 0; i-- ) {
            final ActionTrigger trigger = buttonTriggers.get( i );
            trigger.checkActivation( '\0', button, Float.NaN, Float.NaN, pressed, controller );
        }

    }

    void add( JoystickInputHandlerDevice.JoystickButtonTrigger trigger ) {
        buttonTriggers.add( trigger );
    }

    void remove( JoystickInputHandlerDevice.JoystickButtonTrigger trigger ) {
        buttonTriggers.remove( trigger );
    }

    void add( JoystickInputHandlerDevice.JoystickAxisTrigger trigger ) {
        axisTriggers.add( trigger );
    }

    void remove( JoystickInputHandlerDevice.JoystickAxisTrigger trigger ) {
        axisTriggers.remove( trigger );
    }
}
