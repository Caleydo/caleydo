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
 * <code>RelativeMouse</code> defines a mouse controller that only maintains
 * the relative change from one poll to the next. This does not maintain the
 * position of a mouse in a rendering window. This type of controller is
 * typically useful for a first person mouse look or similar.
 * 
 * @author Mark Powell
 * @version $Id: RelativeMouse.java 4666 2009-09-04 17:22:52Z skye.book $
 */
public class RelativeMouse extends Mouse {

    private static final long serialVersionUID = 1L;
    private InputAction updateAction = new InputAction() {
        public void performAction( InputActionEvent evt ) {
            localTranslation.x = MouseInput.get().getXDelta() * _speed;
            localTranslation.y = MouseInput.get().getYDelta() * _speed;
            worldTranslation.set(localTranslation);
            hotSpotLocation.set(localTranslation).addLocal(hotSpotOffset);
        }
    };
    private InputHandler registeredInputHandler;
    protected float _speed = 1.0f;

    /**
     * Constructor creates a new <code>RelativeMouse</code> object.
     * 
     * @param name
     *            the name of the scene element. This is required for
     *            identification and comparison purposes.
     */
    public RelativeMouse(String name) {
        super(name);
    }

    public void registerWithInputHandler( InputHandler inputHandler ) {

        if ( registeredInputHandler != null )
        {
            registeredInputHandler.removeAction( updateAction );
        }
        registeredInputHandler = inputHandler;
        if ( inputHandler != null )
        {
            inputHandler.addAction( updateAction, InputHandler.DEVICE_MOUSE, InputHandler.BUTTON_NONE, 0, true );
        }
    }

    /**
     * Sets the speed multiplier for updating the cursor position
     *
     * @param speed
     */
    public void setSpeed(float speed) {
        _speed = speed;
    }
}