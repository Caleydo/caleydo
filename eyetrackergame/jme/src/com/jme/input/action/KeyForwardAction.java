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

import com.jme.math.Vector3f;
import com.jme.renderer.Camera;

/**
 * <code>KeyForwardAction</code> provides the action of moving a camera along
 * it's direction vector. How fast the camera moves in a single frame is defined
 * by the speed of the camera times the time between frames. The speed of the
 * camera can be thought of as how many units per second the camera can travel.
 * 
 * @author Mark Powell
 * @version $Id: KeyForwardAction.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public class KeyForwardAction extends KeyInputAction {

    //temp holder for the multiplication of the direction and time
    private static final Vector3f tempVa = new Vector3f();
    //the camera to manipulate
    private Camera camera;

    /**
     * Constructor instantiates a new <code>KeyForwardAction</code> object.
     * 
     * @param camera
     *            the camera that will be affected by this action.
     * @param speed
     *            the speed at which the camera can move.
     */
    public KeyForwardAction(Camera camera, float speed) {
        this.camera = camera;
        this.speed = speed;
    }

    /**
     * <code>performAction</code> moves the camera along it's positive
     * direction vector at a speed of movement speed * time. Where time is the
     * time between frames and 1 corresponds to 1 second.
     * 
     * @see com.jme.input.action.KeyInputAction#performAction(InputActionEvent)
     */
    public void performAction(InputActionEvent evt) {
        Vector3f loc = camera.getLocation();
        if ( !camera.isParallelProjection() ) {
            loc.addLocal(camera.getDirection().mult(speed * evt.getTime(), tempVa));
        } else {
            // move up instead of forward if in parallel mode
            loc.addLocal(camera.getUp().mult(speed * evt.getTime(), tempVa));
        }
        camera.update();
    }
}