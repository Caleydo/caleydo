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
 * KeyToggleBoolean switches a boolean value from true to false based on a
 * trigger.
 * 
 * Created on Jul 21, 2004
 * 
 * @author Joel Schuster
 * @version $Id: KeyToggleBoolean.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public class KeyToggleBoolean extends KeyInputAction {

    //the value to switch
    private boolean value = false;

    /**
     * Instantiates a new KeyToggleBoolean object. The initial value is
     * supplied.
     * 
     * @param value
     *            the initial value to use for the toggle.
     */
    public KeyToggleBoolean(boolean value) {
        this.value = value;
    }

    /**
     * switches the value from true to false, or false to true.
     * 
     * @param evt
     *            the event that called this action.
     */
    public void performAction(InputActionEvent evt) {
        value = !value;
    }

    /**
     * returns the value.
     * 
     * @return Returns the value.
     */
    public boolean isValue() {
        return value;
    }

    /**
     * sets the value.
     * 
     * @param value
     *            The value to set.
     */
    public void setValue(boolean value) {
        this.value = value;
    }
}