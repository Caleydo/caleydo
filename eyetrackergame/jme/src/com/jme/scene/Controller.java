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

package com.jme.scene;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * <code>Controller</code> provides a base class for creation of controllers
 * to modify nodes and render states over time. The base controller provides a
 * repeat type, min and max time, as well as speed. Subclasses of this will
 * provide the update method that takes the time between the last call and the
 * current one and modifies an object in a application specific way.
 * 
 * @author Mark Powell
 * @version $Id: Controller.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public abstract class Controller implements Serializable, Savable {

    /**
     * A clamped repeat type signals that the controller should look like its
     * final state when it's done <br>
     * Example: 0 1 5 8 9 10 10 10 10 10 10 10 10 10 10 10...
     */
    public static final int RT_CLAMP = 0;

    /**
     * A wrapped repeat type signals that the controller should start back at
     * the begining when it's final state is reached <br>
     * Example: 0 1 5 8 9 10 0 1 5 8 9 10 0 1 5 ....
     *  
     */
    public static final int RT_WRAP = 1;

    /**
     * A cycled repeat type signals that the controller should cycle it's states
     * forwards and backwards <br>
     * Example: 0 1 5 8 9 10 9 8 5 1 0 1 5 8 9 10 9 ....
     */
    public static final int RT_CYCLE = 2;

    /**
     * Defines how this controller should repeat itself. This can be one of
     * RT_CLAMP, RT_WRAP, RT_CYCLE, or an application specific repeat flag.
     */
    private int repeatType;

    /**
     * The controller's minimum cycle time
     */
    private float minTime;

    /**
     * The controller's maximum cycle time
     */
    private float maxTime;

    /**
     * The 'speed' of this Controller. Genericly, less than 1 is slower, more
     * than 1 is faster, and 1 represents the base speed
     */
    private float speed = 1;

    /**
     * True if this controller is active, false otherwise
     */
    private boolean active = true;

    private static final long serialVersionUID = 1;
    
    /**
     * Returns the speed of this controller. Speed is 1 by default.
     * 
     * @return
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * Sets the speed of this controller
     * 
     * @param speed
     *            The new speed
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    /**
     * Returns the current maximum time for this controller.
     * 
     * @return This controller's maximum time.
     */
    public float getMaxTime() {
        return maxTime;
    }

    /**
     * Sets the maximum time for this controller
     * 
     * @param maxTime
     *            The new maximum time
     */
    public void setMaxTime(float maxTime) {
        this.maxTime = maxTime;
    }

    /**
     * Returns the current minimum time of this controller
     * 
     * @return This controller's minimum time
     */
    public float getMinTime() {
        return minTime;
    }

    /**
     * Sets the minimum time of this controller
     * 
     * @param minTime
     *            The new minimum time.
     */
    public void setMinTime(float minTime) {
        this.minTime = minTime;
    }

    /**
     * Returns the current repeat type of this controller.
     * 
     * @return The current repeat type
     */
    public int getRepeatType() {
        return repeatType;
    }

    /**
     * Sets the repeat type of this controller.
     * 
     * @param repeatType
     *            The new repeat type.
     */
    public void setRepeatType(int repeatType) {
        this.repeatType = repeatType;
    }

    /**
     * Sets the active flag of this controller. Note: updates on controllers are
     * still called even if this flag is set to false. It is the responsibility
     * of the extending class to check isActive if it wishes to be
     * turn-off-able.
     * 
     * @param active
     *            The new active state.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Returns if this Controller is active or not.
     * 
     * @return True if this controller is set to active, false if not.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Defined by extending classes, <code>update</code> is a signal to
     * Controller that it should update whatever object(s) it is controlling.
     * 
     * @param time
     *            The time in seconds between the last call to update and the
     *            current one
     */
    public abstract void update(float time);

    public void write(JMEExporter e) throws IOException {
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(repeatType, "repeatType", RT_CLAMP);
        capsule.write(minTime, "minTime", 0);
        capsule.write(maxTime, "maxTime", 0);
        capsule.write(speed, "speed", 1);
        capsule.write(active, "active", true);
    }
    
    public void read(JMEImporter e) throws IOException {
        InputCapsule capsule = e.getCapsule(this);
        repeatType = capsule.readInt("repeatType", RT_CLAMP);
        minTime = capsule.readFloat("minTime", 0);
        maxTime = capsule.readFloat("maxTime", 0);
        speed = capsule.readFloat("speed", 1);
        active = capsule.readBoolean("active", true);
    }
    
    public Class getClassTag() {
        return this.getClass();
    }
    
    public void getControllerValues(HashMap<String, Object> store) {
        
    }
    
    public void setControllerValues(HashMap<String, Object> values) {
        
    }
}