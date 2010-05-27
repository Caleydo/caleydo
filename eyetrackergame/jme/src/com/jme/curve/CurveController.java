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

package com.jme.curve;

import java.io.IOException;

import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.Spatial;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

/**
 * <code>CurveController</code> defines a controller that moves a supplied
 * <code>Spatial</code> object along a curve. Attributes of the curve are set
 * such as the up vector (if not set, the spacial object will roll along the
 * curve), the orientation precision defines how accurate the orientation of the
 * spatial will be.
 * @author Mark Powell
 * @version $Id: CurveController.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public class CurveController extends Controller {
    private static final long serialVersionUID = 1L;
	private Spatial mover;
    private Curve curve;
    private Vector3f up;
    private float orientationPrecision = 0.1f;
    private float currentTime = 0.0f;
    private float deltaTime = 0.0f;

    private boolean cycleForward = true;
    private boolean autoRotation = false;

    /**
     * Constructor instantiates a new <code>CurveController</code> object.
     * The curve object that the controller operates on and the spatial object
     * that is moved is specified during construction.
     * @param curve the curve to operate on.
     * @param mover the spatial to move.
     */
    public CurveController(Curve curve, Spatial mover) {
        this.curve = curve;
        this.mover = mover;
        setUpVector(new Vector3f(0,1,0));
        setMinTime(0);
        setMaxTime(Float.MAX_VALUE);
        setRepeatType(Controller.RT_CLAMP);
        setSpeed(1.0f);
    }

    /**
     * Constructor instantiates a new <code>CurveController</code> object.
     * The curve object that the controller operates on and the spatial object
     * that is moved is specified during construction. The game time to
     * start and the game time to finish is also supplied.
     * @param curve the curve to operate on.
     * @param mover the spatial to move.
     * @param minTime the time to start the controller.
     * @param maxTime the time to end the controller.
     */
    public CurveController(
        Curve curve,
        Spatial mover,
        float minTime,
        float maxTime) {
        this.curve = curve;
        this.mover = mover;
        setMinTime(minTime);
        setMaxTime(maxTime);
        setRepeatType(Controller.RT_CLAMP);
    }

    /**
     *
     * <code>setUpVector</code> sets the locking vector for the spatials up
     * vector. This prevents rolling along the curve and allows for a better
     * tracking.
     * @param up the vector to lock as the spatials up vector.
     */
    public void setUpVector(Vector3f up) {
        this.up = up;
    }

    /**
     *
     * <code>setOrientationPrecision</code> sets a precision value for the
     * spatials orientation. The smaller the number the higher the precision.
     * By default 0.1 is used, and typically does not require changing.
     * @param value the precision value of the spatial's orientation.
     */
    public void setOrientationPrecision(float value) {
        orientationPrecision = value;
    }

    /**
     *
     * <code>setAutoRotation</code> determines if the object assigned to
     * the controller will rotate with the curve or just following the
     * curve.
     * @param value true if the object is to rotate with the curve, false
     *      otherwise.
     */
    public void setAutoRotation(boolean value) {
        autoRotation = value;
    }

    /**
     *
     * <code>isAutoRotating</code> returns true if the object is rotating with
     * the curve and false if it is not.
     * @return true if the object is following the curve, false otherwise.
     */
    public boolean isAutoRotating() {
        return autoRotation;
    }

    /**
     * <code>update</code> moves a spatial along the given curve for along a
     * time period.
     * @see com.jme.scene.Controller#update(float)
     */
    public void update(float time) {
        if(mover == null || curve == null || up == null) {
            return;
        }
        currentTime += time * getSpeed();

        if (currentTime >= getMinTime() && currentTime <= getMaxTime()) {

            if (getRepeatType() == RT_CLAMP) {
                deltaTime = currentTime - getMinTime();
                mover.setLocalTranslation(curve.getPoint(deltaTime,mover.getLocalTranslation()));
                if(autoRotation) {
                    mover.setLocalRotation(
                        curve.getOrientation(
                            deltaTime,
                            orientationPrecision,
                            up));
                }
            } else if (getRepeatType() == RT_WRAP) {
                deltaTime = (currentTime - getMinTime()) % 1.0f;
                if (deltaTime > 1) {
                    currentTime = 0;
                    deltaTime = 0;
                }
                mover.setLocalTranslation(curve.getPoint(deltaTime,mover.getLocalTranslation()));
                if(autoRotation) {
                    mover.setLocalRotation(
                        curve.getOrientation(
                            deltaTime,
                            orientationPrecision,
                            up));
                }
            } else if (getRepeatType() == RT_CYCLE) {
                float prevTime = deltaTime;
                deltaTime = (currentTime - getMinTime()) % 1.0f;
                if (prevTime > deltaTime) {
                    cycleForward = !cycleForward;
                }
                if (cycleForward) {

                    mover.setLocalTranslation(curve.getPoint(deltaTime,mover.getLocalTranslation()));
                    if(autoRotation) {
                        mover.setLocalRotation(
                            curve.getOrientation(
                                deltaTime,
                                orientationPrecision,
                                up));
                    }
                } else {
                    mover.setLocalTranslation(
                        curve.getPoint(1.0f - deltaTime,mover.getLocalTranslation()));
                    if(autoRotation) {
                        mover.setLocalRotation(
                            curve.getOrientation(
                                1.0f - deltaTime,
                                orientationPrecision,
                                up));
                    }
                }
            } else {
                return;
            }
        }
    }
    
    public void reset() {
    	this.currentTime = 0;
    }
    
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(mover, "mover", null);
        capsule.write(curve, "Curve", null);
        capsule.write(up, "up", null);
        capsule.write(orientationPrecision, "orientationPrecision", 0.1f);
        capsule.write(cycleForward, "cycleForward", true);
        capsule.write(autoRotation, "autoRotation", false);
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        
        mover = (Spatial)capsule.readSavable("mover", null);
        curve = (Curve)capsule.readSavable("curve", null);
        up = (Vector3f)capsule.readSavable("up", null);
        orientationPrecision = capsule.readFloat("orientationPrecision", 0.1f);
        cycleForward = capsule.readBoolean("cycleForward", true);
        autoRotation = capsule.readBoolean("autoRotation", false);
    }
}
