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

import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;

/**
 * This is a utility class to simulate an axis from two buttons (or keys). For a usage example see TestInputHandler.
 */
public class TwoButtonAxis extends SyntheticAxis {

    /**
     * @param name the name of this new axis
     */
    public TwoButtonAxis(String name) {
        super( name );
        getDecreaseAction().setSpeed( 1 );
        getIncreaseAction().setSpeed( 1 );
    }

    /**
     * current value of this axis
     */
    private float value = 0;

    /**
     * @return current value/position of this axis
     */
    public float getValue() {
        return value;
    }

    /**
     * @see #isDiscreet()
     */
    private boolean discreet = false;

    /**
     * @return true if a single keystoke changes the axis value by the action speed, false if continuous
     * (multiplied with frame time)
     */
    public boolean isDiscreet() {
        return discreet;
    }

    /**
     * @param discreet true if a single keystoke should change the axis value by the action speed, false if continuous
     * (multiplied with frame time)
     */
    public void setDiscreet(boolean discreet) {
        this.discreet = discreet;
    }

    /**
     * action to decrease the value
     */
    private final InputAction decreaseAction = new InputAction() {
        public void performAction(InputActionEvent evt) {
            if ( discreet )
            {
                float increase = -getSpeed();
                value += increase;
                trigger(increase, '\0', TwoButtonAxis.this.value, false, null );
            }
            else
            {
                float increase = -getSpeed() * evt.getTime();
                value += increase;
                trigger(increase, '\0', TwoButtonAxis.this.value, false, null );
            }
        }
    };

    /**
     * action to increase the value
     */
    private final InputAction increaseAction = new InputAction() {
        public void performAction(InputActionEvent evt) {
            if ( discreet )
            {
                float increase = getSpeed();
                value += increase;
                trigger(increase, '\0', TwoButtonAxis.this.value, false, null );
            }
            else
            {
                float increase = getSpeed() * evt.getTime();
                value += increase;
                trigger(increase, '\0', TwoButtonAxis.this.value, false, null );
            }
        }
    };

    /**
     * The returned action should be subscribed with an InputHandler to be invoked any time the axis should decrease.
     * When {@link #isDiscreet()} is true it is commonly registed with allowRepeats==false, while allowRepeats should be
     * true when {@link #isDiscreet()} is false.
     * @return the action that decreases the axis value
     */
    public InputAction getDecreaseAction() {
        return decreaseAction;
    }

    /**
     * The returned action should be subscribed with an InputHandler to be invoked any time the axis should increase.
     * When {@link #isDiscreet()} is true it is commonly registed with allowRepeats==false, while allowRepeats should be
     * true when {@link #isDiscreet()} is false.
     * @return the action that increases the axis value
     */
    public InputAction getIncreaseAction() {
        return increaseAction;
    }
}
