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

import com.jme.input.MouseInput;
import com.jme.input.controls.Binding;

/**
 * @author Matthew D. Hicks
 */
public class MouseAxisBinding implements Binding {
    
    private static final long serialVersionUID = 1L;

    public static final int AXIS_X = 1;
    public static final int AXIS_Y = 2;
    public static final int AXIS_W = 3;
    
    private int axis;
    private boolean reverse;
    
    public MouseAxisBinding(int axis, boolean reverse) {
        this.axis = axis;
        this.reverse = reverse;
    }
    
	public String getName() {
		if (reverse) {
            return "Mouse" + getAxisString(axis) + "(-)";
        } else {
            return "Mouse" + getAxisString(axis) + "(+)";
        }
	}

	public float getValue() {
        float value;
		if (axis == AXIS_X) {
		    value = convert(MouseInput.get().getXDelta());
        } else if (axis == AXIS_Y) {
            value = convert(MouseInput.get().getYDelta());
        } else {
            value = convert(MouseInput.get().getWheelDelta());
        }
        return value;
	}
    
    private float convert(int value) {
        if ((value < 0) && (!reverse)) return 0.0f;
        if ((value > 0) && (reverse)) return 0.0f;
        return Math.abs((float)value * 0.1f);
    }

    public String toString() {
		return getName();
	}
    
    private static final String getAxisString(int axis) {
        if (AXIS_X == axis) return "X";
        else if (AXIS_Y == axis) return "Y";
        else if (AXIS_W == axis) return "W";
        return "Unknown";
    }
}
