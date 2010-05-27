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

import com.jme.input.controls.Binding;
import com.jme.input.joystick.Joystick;
import com.jme.input.joystick.JoystickInput;

/**
 * @author Matthew D. Hicks
 */
public class JoystickAxisBinding implements Binding {

    private static final long serialVersionUID = 1L;
    
    private transient Joystick joystick;
    private String name;
    private int axis;
    private boolean reverse;
    
    public JoystickAxisBinding(Joystick joystick, int axis, boolean reverse) {
    	name = joystick.getName();
        this.joystick = joystick;
        this.axis = axis;
        this.reverse = reverse;
    }
    
	public String getName() {
        return "JS:X" + axis + (reverse ? "(-)" : "(+)");
	}

	public float getValue() {
		if (joystick == null) {
			loadJoystick();
		}
		float value = joystick.getAxisValue(axis);
        if ((value < 0.0f) && (!reverse)) return 0.0f;
        if ((value > 0.0f) && (reverse)) return 0.0f;
        return Math.abs(value);
	}
	
	private void loadJoystick() {
		for (int i = 0; i < JoystickInput.get().getJoystickCount(); i++) {
			if (JoystickInput.get().getJoystick(i).getName().equals(name)) {
				joystick = JoystickInput.get().getJoystick(i);
			}
		}
	}
	
	public String toString() {
		return joystick.getName() + ":Axis" + axis + (reverse ? "(-)" : "(+)");
	}
}