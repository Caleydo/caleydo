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

import com.jme.input.joystick.JoystickInput;


/**
 * <code>InputSystem</code> creates the required input objects (mouse and
 * keyboard, disabled by default: joystick) depending on the API desired for the handling
 * of the input. This will allow the client application to only deal with
 * <code>KeyInput</code>, <code>MouseInput</code> and <code>JoystickInput</code> not
 * having to worry about the API specifics.
 * 
 * @see com.jme.input.KeyInput
 * @see com.jme.input.MouseInput
 * @author Mark Powell
 * @version $Id: InputSystem.java 4666 2009-09-04 17:22:52Z skye.book $
 */
public class InputSystem {

    public static final String INPUT_SYSTEM_LWJGL = "LWJGL";
    public static final String INPUT_SYSTEM_AWT = "AWT";
    public static final String INPUT_SYSTEM_DUMMY = "DUMMY";

    /**
     * Update the core input system - mouse, keyboard and joystick.
     * Thus all events are handled within this method call.<br>
     * To disable joystick support call {@link JoystickInput#setProvider(String)} with {@link #INPUT_SYSTEM_DUMMY} as
     * parameter prior to creating the display.
     * @see KeyInput#update()
     * @see MouseInput#update()
     * @see JoystickInput#update()
     */
    public static void update()
    {
        MouseInput.get().update();
        KeyInput.get().update();
        JoystickInput.get().update();
    }
}
