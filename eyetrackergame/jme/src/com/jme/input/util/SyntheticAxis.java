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

package com.jme.input.util;

import java.util.ArrayList;

import com.jme.input.ActionTrigger;
import com.jme.input.InputHandler;
import com.jme.input.action.InputActionInterface;

/**
 * This class can be used to create synthetic axes for {@link InputHandler}s. As an example see {@link TwoButtonAxis}.
 */
public class SyntheticAxis extends SyntheticTriggerContainer {
    /**
     * name of this axis.
     */
    protected final String name;
    /**
     * store the index in util device
     */
    private int index;
    /**
     * list of triggers
     */
    private ArrayList<SyntheticTrigger> axisTriggers = new ArrayList<SyntheticTrigger>();

    public SyntheticAxis( String name ) {
        this.name = name;
        UtilInputHandlerDevice.get().addAxis( this );
    }

    /**
     * @return the name of this axis
     */
    public String getName() {
        return name;
    }

    /**
     * @return index of this axis (used when registering with InputHandler)
     * @see #getDeviceName()
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * setter for field axis
     * @param value index of this axis
     */
    void setIndex(final int value) {
        this.index = value;
    }

    /**
     * @return name of the virtual device this axis is attached to (used when registering with InputHandler)
     * @see #getIndex()
     */
    public final String getDeviceName() {
        return UtilInputHandlerDevice.DEVICE_UTIL;
    }

    protected void createTrigger( InputHandler inputHandler, InputActionInterface action, boolean allowRepeats ) {
        new SyntheticTrigger( this, inputHandler, action, allowRepeats, true );
    }

    /**
     * @param trigger what to add to list of triggers
     */
    void add( SyntheticTrigger trigger ) {
        axisTriggers.add( trigger );
    }

    /**
     * @param trigger what to remove from list of triggers
     */
    void remove( SyntheticTrigger trigger ) {
        axisTriggers.remove( trigger );
    }

    /**
     * check all triggers
     * @see com.jme.input.ActionTrigger#checkActivation(char, int, float, float, boolean, Object)
     */
    public void trigger( float delta, char character, float value, boolean pressed, Object data ) {
        for ( int i = axisTriggers.size() - 1; i >= 0; i-- ) {
            final ActionTrigger trigger = axisTriggers.get( i );
            trigger.checkActivation( character, getIndex(), value, delta, pressed, data );
        }
    }
}
