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
package com.jme.input.controls.binding;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.input.controls.Binding;

/**
 * <code>KeyAndMouseButtonBinding</code> is akin to merging
 * <code>KeyboardBinding</code> with <code>MouseButtonMinding</code>,
 * allowing for a KeyboardButton + Mouse Button (i.e: "CTRL+Click")
 * @author <a href="mailto:skye.book@gmail.com">Skye Book
 */
public class KeyAndMouseButtonBinding implements Binding {
    private static final long serialVersionUID = 1L;
    
    private int button;
    private int key;
    
    public KeyAndMouseButtonBinding(int key, int button) {
        this.button = button;
        this.key = key;
    }
    
	public String getName() {
		return "Mouse Button: " + Mouse.getButtonName(button) + "; Key: " + Keyboard.getKeyName(key);
	}
	
	public float getValue() {
		return (MouseInput.get().isButtonDown(button) && KeyInput.get().isKeyDown(key)) ? 1.0f : 0.0f;
	}

	public String toString() {
		return getName();
	}
}