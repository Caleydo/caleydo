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

import com.jme.renderer.Camera;

/**
 * <code>FirsPersonController</code> defines an InputHandler that sets
 * input to be controlled similar to First Person Shooting games. By default the
 * commands are, WSAD moves the camera forward, backward and strafes. The
 * arrow keys rotate and tilt the camera and the mouse also rotates and tilts
 * the camera. <br>
 * This is a handler that is composed from {@link KeyboardLookHandler} and {@link MouseLookHandler}.
 * @author Mark Powell
 * @version $Id: FirstPersonHandler.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public class FirstPersonHandler extends InputHandler {
    private MouseLookHandler mouseLookHandler;
    private KeyboardLookHandler keyboardLookHandler;

    /**
     * @return handler for keyboard controls
     */
    public KeyboardLookHandler getKeyboardLookHandler() {
        return keyboardLookHandler;
    }

    /**
     * @return handler for mouse controls
     */
    public MouseLookHandler getMouseLookHandler() {
        return mouseLookHandler;
    }
    
    public void setButtonPressRequired(boolean value) {
        mouseLookHandler.requireButtonPress(value);
    }

    /**
     * Creates a first person handler.
     * @param cam The camera to move by this handler.
     */
    public FirstPersonHandler( Camera cam ) {
        mouseLookHandler = new MouseLookHandler( cam, 1 );
        addToAttachedHandlers( mouseLookHandler );
        keyboardLookHandler = new KeyboardLookHandler( cam, 0.5f, 0.01f );
        addToAttachedHandlers( keyboardLookHandler );
    }

    /**
     * Creates a first person handler.
     * @param cam The camera to move by this handler.
     * @param moveSpeed action speed for move actions
     * @param turnSpeed action speed for rotating actions
     */
    public FirstPersonHandler(Camera cam, float moveSpeed, float turnSpeed ) {
        mouseLookHandler = new MouseLookHandler( cam, turnSpeed );
        addToAttachedHandlers( mouseLookHandler );
        keyboardLookHandler = new KeyboardLookHandler( cam, moveSpeed, turnSpeed );
        addToAttachedHandlers( keyboardLookHandler );
    }
}
