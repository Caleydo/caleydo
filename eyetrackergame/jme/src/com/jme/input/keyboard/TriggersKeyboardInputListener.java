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

package com.jme.input.keyboard;

import java.util.ArrayList;

import com.jme.input.ActionTrigger;
import com.jme.input.KeyInput;
import com.jme.input.KeyInputListener;

/**
 * Each {@link KeyboardInputHandlerDevice} has an instance of this class which is subscribed at the
 * {@link com.jme.input.KeyInput} to receive keyboard events and forward them to the keyboard triggers.
 */
class TriggersKeyboardInputListener implements KeyInputListener {

    public TriggersKeyboardInputListener() {
    }

    public void activate() {
        KeyInput.get().addListener( this );
    }

    public void deactivate() {
        KeyInput.get().removeListener( this );
    }

    private ArrayList<KeyboardInputHandlerDevice.KeyTrigger> buttonTriggers = new ArrayList<KeyboardInputHandlerDevice.KeyTrigger>();

    public void onKey( char character, int keyCode, boolean pressed ) {
        for ( int i = buttonTriggers.size() - 1; i >= 0; i-- ) {
            final ActionTrigger trigger = buttonTriggers.get( i );
            trigger.checkActivation( character, keyCode, Float.NaN, Float.NaN, pressed, null );
        }

    }

    void add( KeyboardInputHandlerDevice.KeyTrigger trigger ) {
        buttonTriggers.add( trigger );
    }

    void remove( KeyboardInputHandlerDevice.KeyTrigger trigger ) {
        buttonTriggers.remove( trigger );
    }
}
