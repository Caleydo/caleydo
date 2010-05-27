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

package com.jme.input.joystick;

/**
 * Represents a single joystick device.
 * @author Matthew D. Hicks, Irrisor
 */
public interface Joystick {
    /**
     * Query the names of the joysticks axes. Indices correspond with {@link #getAxisValue(int)}.
     * @return an array of axis names
     */
    String[] getAxisNames();

    /**
     * @return number of axes this joystick has
     */
    int getAxisCount();

    /**
     * Query the current position of a single axis.
     * Remember to call {@link com.jme.input.joystick.JoystickInput#update()} prior to using these method.
     * @param axis index of the axis of interest
     * @return the current position of the axis between -1 and 1
     */
    float getAxisValue( int axis );

    /**
     * @return number of buttons this joystick has
     */
    int getButtonCount();

    /**
     * Query state of a button.
     * Remember to call {@link com.jme.input.joystick.JoystickInput#update()} prior to using these method.
     * @param button index of a button (0 <= index < {@link #getButtonCount()})
     * @return true if button is currently pressed
     */
    boolean isButtonPressed( int button );

    /**
     * @return name of this joystick
     */
    String getName();

    /**
     * Cause the rumbler (if existent) for specified axis to change force.
     * @param axis index of the axis to be rumbled
     * @param intensity new force intensity
     */
    void rumble( int axis, float intensity );

    /**
     * Look through the axis for a given name.
     * @param name the name of the axis we are looking for
     * @return the index of the matching axis or -1 if none.
     */
    int findAxis(String name);
}
