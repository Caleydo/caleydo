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

package com.jmex.audio;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.scene.Spatial;

/**
 * Represents the listener in space. Use extensions of this class to control the
 * speed, position and orientation of your listener.
 * 
 * @author Joshua Slack
 * @version $Id: Ear.java 4133 2009-03-19 20:40:11Z blaine.dev $
 */
public abstract class Ear {
    public static float EAR_THROTTLE = .033f;

    private Vector3f position = new Vector3f();

    private Vector3f upVector = new Vector3f(0, 0, 1);
    private Vector3f facingVector = new Vector3f(0, -1, 0);
    private Vector3f currVelocity = new Vector3f();

    private EarTracker positionTracker = null;
    private EarTracker orientTracker = null;

    public Ear() {
    }

    public Vector3f getCurrVelocity() {
        return currVelocity;
    }

    public void setCurrVelocity(Vector3f currVelocity) {
        this.currVelocity.set(currVelocity);
    }

    public Vector3f getFacingVector() {
        return facingVector;
    }

    public void setFacingVector(Vector3f facingVector) {
        this.facingVector.set(facingVector);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position.set(position);
    }

    public Vector3f getUpVector() {
        return upVector;
    }

    public void setUpVector(Vector3f upVector) {
        this.upVector.set(upVector);
    }

    public void trackPosition(Camera cam) {
        positionTracker = new EarTracker(cam);
        update(1);
    }

    public void trackPosition(Spatial spat) {
        positionTracker = new EarTracker(spat);
        update(1);
    }

    public void trackOrientation(Camera cam) {
        orientTracker = new EarTracker(cam);
        update(1);
    }

    public void trackOrientation(Spatial spat) {
        orientTracker = new EarTracker(spat);
        update(1);
    }

    float elapsed = 0;
    public void update(float dt) {
        elapsed += dt;
        if (elapsed < EAR_THROTTLE) return;

        // if we are tracking something...
        if (positionTracker != null)
            positionTracker.applyPosition(getPosition(), getCurrVelocity(), elapsed);

        if (orientTracker != null)
            orientTracker.applyOrientation(getUpVector(), getFacingVector());
            
        elapsed = 0;
    }
    
    class EarTracker {
        public Camera trackedCamera = null;
        public Spatial trackedSpatial = null;
        private Vector3f lastPosition = new Vector3f();
        
        public EarTracker(Camera cam) {
            trackedCamera = cam;
            lastPosition.set(cam.getLocation());
        }
        
        public EarTracker(Spatial spat) {
            trackedSpatial = spat;
            lastPosition.set(spat.getWorldTranslation());
        }

        public void applyPosition(Vector3f position, Vector3f velocity, float dt) {
            lastPosition.set(position);
            
            if (trackedCamera != null)
                position.set(trackedCamera.getLocation());
            else
                position.set(trackedSpatial.getWorldTranslation());
            
            if (velocity != null) {
                // update instantaneous velocity
                velocity.set(position).subtractLocal(lastPosition)
                        .divideLocal(
                                dt * AudioSystem.getSystem().getUnitsPerMeter());
                
                // XXX: REMOVE ME.  THIS IS A HACK FOR MARK'S COMPUTER FOR NOW.
                if (velocity.lengthSquared() > 2500) {
                    velocity.normalizeLocal();
                    velocity.multLocal(50);
                }
            }
        }
        
        public void applyOrientation(Vector3f up, Vector3f facing) {
            if (trackedCamera != null) {
                up.set(trackedCamera.getUp());
                facing.set(trackedCamera.getDirection());
            } else {
                Quaternion axes = trackedSpatial.getWorldRotation();
                axes.getRotationColumn(1, up);
                axes.getRotationColumn(2, facing);
            }
        }
    }
}
