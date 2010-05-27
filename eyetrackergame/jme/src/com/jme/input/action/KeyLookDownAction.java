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

import com.jme.math.Matrix3f;
import com.jme.renderer.Camera;

/**
 * <code>KeyLookDownAction</code> tilts a camera down a given angle. This
 * angle should be represented as a radian.
 * 
 * @author Mark Powell
 * @version $Id: KeyLookDownAction.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public class KeyLookDownAction extends KeyInputAction {
    //maintains the rotation matrix
    private static final Matrix3f incr = new Matrix3f();
    //the camera to manipulate
    private Camera camera;

    /**
     * Constructor instantiates a new <code>KeyLookDownAction</code> object.
     * 
     * @param camera
     *            the camera to tilt.
     * @param speed
     *            the speed at which the camera tilts.
     */
    public KeyLookDownAction(Camera camera, float speed) {
        this.camera = camera;
        this.speed = speed;
    }

    /**
     * <code>performAction</code> adjusts the view of the camera to tilt down
     * a given angle. This angle is determined by the camera's speed and the
     * time which has passed.
     * 
     * @see com.jme.input.action.KeyInputAction#performAction(InputActionEvent)
     */
    public void performAction(InputActionEvent evt) {
        incr.fromAngleNormalAxis(speed * evt.getTime(), camera.getLeft());
        incr.mult(camera.getLeft(), camera.getLeft());
        incr.mult(camera.getDirection(), camera.getDirection());
        incr.mult(camera.getUp(), camera.getUp());
        camera.normalize();
        camera.update();
    }
}