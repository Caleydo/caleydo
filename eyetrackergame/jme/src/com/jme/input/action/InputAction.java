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

package com.jme.input.action;

/**
 * An <code>InputAction</code> can be subscribed as an {@link com.jme.input.InputHandler} to get its
 * {@link InputActionInterface#performAction(InputActionEvent)} method called on specific event triggers.
 * It also defines an interface that sets the criteria for
 * input actions, e.g. the speed of the action.
 * @author Mark Powell
 * @version $Id: InputAction.java 4639 2009-08-29 00:34:05Z skye.book $
 */
public abstract class InputAction implements InputActionInterface {

    /** A speed value that, if desired, can change how actions are performed. */
    protected float speed = 0;

    /**
     *
     * <code>setSpeed</code> defines the speed at which this action occurs.
     *
     * @param speed
     *            the speed at which this action occurs.
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    /**
     * Returns the currently set speed. Speed is 0 by default.
     *
     * @return The current speed.
     */
    public float getSpeed() {
        return speed;
    }
}
