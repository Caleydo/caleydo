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

package com.jme.input;

import com.jme.input.action.InputActionInterface;

/**
 * Devices are used in {@link InputHandler} to create different types of {@link ActionTrigger}s. The method
 * {@link #createTriggers}(...) is called by InputHandler when actions are registered via
 * {@link InputHandler#addAction(com.jme.input.action.InputActionInterface,String,int,int,boolean)}.
 *
 * @see com.jme.input.mouse.MouseInputHandlerDevice
 * @see com.jme.input.keyboard.KeyboardInputHandlerDevice
 * @see com.jme.input.joystick.JoystickInputHandlerDevice
 */
public abstract class InputHandlerDevice {
    /**
     * Store name of this device. The name may not change, because it is used as key.
     */
    private final String name;

    /**
     * @param name name of the device
     */
    protected InputHandlerDevice( String name ) {
        if ( name == null ) {
            throw new NullPointerException();
        }
        this.name = name;
    }

    /**
     * Query name of this device. Note: The name may not change, because it is used as key, that's why this method
     * is final (avoid overriding and returning another name).
     *
     * @return name of this device
     */
    public final String getName() {
        return name;
    }

    /**
     * Creates device specific trigger(s) for specified axes and buttons (the triggers register themselves at the
     * inputHandler).
     *
     * @param action action that will be invoked upon trigger activation
     * @param axis axis this trigger is responsible for
     * @param button button this trigger is responsible for
     * @param allowRepeats true to allow repeats
     * @param inputHandler input handler for the triggers
     */
    protected abstract void createTriggers( InputActionInterface action, int axis, int button, boolean allowRepeats,
                                            InputHandler inputHandler );

    @Override
    public String toString() {
        return "InputHandlerDevice: " + getName();
    }
}
