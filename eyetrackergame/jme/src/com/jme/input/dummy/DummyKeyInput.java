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

package com.jme.input.dummy;

import org.lwjgl.input.Keyboard;

import com.jme.input.KeyInput;

/**
 * <code>DummyKeyInput</code> simulates a Keyinput system, usable for Applications that can be headless or not.
 * @author Kai Börnert
 * @version $Id: DummyKeyInput.java 4310 2009-05-01 13:53:42Z blaine.dev $
 */
public class DummyKeyInput extends KeyInput {
    
    /**
     * Constructor instantiates a new <code>DummyKeyInput</code> object, it does nothing at all.
     */
    protected DummyKeyInput() {
    }

    /**
     * This always returns false
     * @see com.jme.input.KeyInput#isKeyDown(int)
     */
    public boolean isKeyDown(int key) {
        return false;
    }

    /**
     * <code>getKeyName</code> returns the string representation of the key
     * code.
     * @see com.jme.input.KeyInput#getKeyName(int)
     */
    public String getKeyName(int key) {
        return Keyboard.getKeyName(key);
    }

    /**
     * <code>getKeyIndex</code> returns the value of the key name
     * @param name the name of the key
     * @return the value of the key
     */
    public int getKeyIndex( String name) {
        return Keyboard.getKeyIndex( name);
    }

    /**
     * <code>update</code> The Dummy does nothing at all.
     * @see com.jme.input.KeyInput#update()
     */
    public void update() {

    }

    /**
     * <code>destroy</code> nothing to destroy.
     * @see com.jme.input.KeyInput#destroy()
     */
    public void destroy() {
    }

    @Override
    public void clear() {
    }

    @Override
    public void clearKey(int keycode) {
    }
}