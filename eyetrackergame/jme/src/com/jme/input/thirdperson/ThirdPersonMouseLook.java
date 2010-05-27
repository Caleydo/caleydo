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

package com.jme.input.thirdperson;

import java.util.Map;

import com.jme.input.ChaseCamera;
import com.jme.input.InputHandler;
import com.jme.input.MouseInput;
import com.jme.input.RelativeMouse;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.MouseInputAction;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;

public class ThirdPersonMouseLook extends MouseInputAction {
    
    public static final String PROP_MAXASCENT = "maxAscent";
    public static final String PROP_MINASCENT = "minAscent";
    public static final String PROP_MAXROLLOUT = "maxRollOut";
    public static final String PROP_MINROLLOUT = "minRollOut";
    public static final String PROP_MOUSEXMULT = "mouseXMult";
    public static final String PROP_MOUSEYMULT = "mouseYMult";
    public static final String PROP_MOUSEROLLMULT = "mouseRollMult";
    public static final String PROP_INVERTEDY = "invertedY";
    public static final String PROP_LOCKASCENT = "lockAscent";
    public static final String PROP_ROTATETARGET = "rotateTarget";
    public static final String PROP_ENABLED = "lookEnabled";
    public static final String PROP_TARGETTURNSPEED = "targetTurnSpeed";
    public static final String PROP_MOUSEBUTTON_FOR_LOOKING = "lookButton";
    public static final String PROP_INVERTROTATE = "invertRotate";
    
    public static final float DEFAULT_MOUSEXMULT = 2;
    public static final float DEFAULT_MOUSEYMULT = 30;
    public static final float DEFAULT_MOUSEROLLMULT = 50;
    public static final float DEFAULT_MAXASCENT = 45 * FastMath.DEG_TO_RAD;
    public static final float DEFAULT_MINASCENT = -15 * FastMath.DEG_TO_RAD;
    public static final float DEFAULT_MAXROLLOUT = 240;
    public static final float DEFAULT_MINROLLOUT = 20;
    public static final float DEFAULT_TARGETTURNSPEED = FastMath.TWO_PI;
    public static final boolean DEFAULT_INVERTEDY = false;
    public static final boolean DEFAULT_LOCKASCENT = false;
    public static final boolean DEFAULT_ENABLED = true;
    public static final boolean DEFAULT_ROTATETARGET = false;
    public static final boolean DEFAULT_INVERTROTATE = false;
    public static final int DEFAULT_MOUSEBUTTON_FOR_LOOKING = -1;

    protected float maxAscent = DEFAULT_MAXASCENT;
    protected float minAscent = DEFAULT_MINASCENT;
    protected float maxRollOut = DEFAULT_MAXROLLOUT;
    protected float minRollOut = DEFAULT_MINROLLOUT;
    protected float mouseXMultiplier = DEFAULT_MOUSEXMULT;
    protected float mouseYMultiplier = DEFAULT_MOUSEYMULT;
    protected float mouseRollMultiplier = DEFAULT_MOUSEROLLMULT;
    protected float mouseXSpeed = DEFAULT_MOUSEXMULT;
    protected float mouseYSpeed = DEFAULT_MOUSEYMULT;
    protected float rollInSpeed = DEFAULT_MOUSEROLLMULT;
    protected float targetTurnSpeed = DEFAULT_TARGETTURNSPEED;
    protected ChaseCamera camera;
    protected Spatial target;
    protected boolean updated = false;
    protected boolean invertedY = DEFAULT_INVERTEDY;
    protected boolean lockAscent = DEFAULT_LOCKASCENT;
    protected boolean enabled = DEFAULT_ENABLED;
    protected boolean rotateTarget = DEFAULT_ROTATETARGET;
    protected boolean invertRotate = DEFAULT_INVERTROTATE;
    protected int lookMouse = DEFAULT_MOUSEBUTTON_FOR_LOOKING;
    protected Vector3f difTemp = new Vector3f();
    protected Vector3f sphereTemp = new Vector3f();
    protected Vector3f rightTemp = new Vector3f();
    protected Quaternion rotTemp = new Quaternion();
    protected Vector3f worldUpVec = new Vector3f(ChaseCamera.DEFAULT_WORLDUPVECTOR);
    protected ThirdPersonJoystickPlugin plugin = null;
    
    /**
     * Constructor creates a new <code>MouseLook</code> object. It takes the
     * mouse, camera and speed of the looking.
     * 
     * @param mouse
     *            the mouse to calculate view changes.
     * @param camera
     *            the camera to move.
     */
    public ThirdPersonMouseLook(RelativeMouse mouse, ChaseCamera camera, Spatial target) {
        this.mouse = mouse;
        this.camera = camera;
        this.target = target;

        // force update of the 3 speeds.
        setSpeed(1);
    }

    /**
     * 
     * <code>updateProperties</code>
     * @param props
     */
    public void updateProperties(Map<String, Object> props) {
        maxAscent = InputHandler.getFloatProp(props, PROP_MAXASCENT, DEFAULT_MAXASCENT);
        minAscent = InputHandler.getFloatProp(props, PROP_MINASCENT, DEFAULT_MINASCENT);
        maxRollOut = InputHandler.getFloatProp(props, PROP_MAXROLLOUT, DEFAULT_MAXROLLOUT);
        minRollOut = InputHandler.getFloatProp(props, PROP_MINROLLOUT, DEFAULT_MINROLLOUT);
        targetTurnSpeed = InputHandler.getFloatProp(props, PROP_TARGETTURNSPEED, DEFAULT_TARGETTURNSPEED);
        setMouseXMultiplier(InputHandler.getFloatProp(props, PROP_MOUSEXMULT, DEFAULT_MOUSEXMULT));
        setMouseYMultiplier(InputHandler.getFloatProp(props, PROP_MOUSEYMULT, DEFAULT_MOUSEYMULT));
        setMouseRollMultiplier(InputHandler.getFloatProp(props, PROP_MOUSEROLLMULT, DEFAULT_MOUSEROLLMULT));
        invertedY = InputHandler.getBooleanProp(props, PROP_INVERTEDY, DEFAULT_INVERTEDY);
        lockAscent = InputHandler.getBooleanProp(props, PROP_LOCKASCENT, DEFAULT_LOCKASCENT);
        rotateTarget = InputHandler.getBooleanProp(props, PROP_ROTATETARGET, DEFAULT_ROTATETARGET);
        invertRotate = InputHandler.getBooleanProp(props, PROP_INVERTROTATE, DEFAULT_INVERTROTATE);
        enabled = InputHandler.getBooleanProp(props, PROP_ENABLED, DEFAULT_ENABLED);
        lookMouse = InputHandler.getIntProp(props, PROP_MOUSEBUTTON_FOR_LOOKING, DEFAULT_MOUSEBUTTON_FOR_LOOKING);
        worldUpVec = (Vector3f)InputHandler.getObjectProp(props, ChaseCamera.PROP_WORLDUPVECTOR, ChaseCamera.DEFAULT_WORLDUPVECTOR);
    }

    /**
     * 
     * <code>setSpeed</code> sets the speed of the mouse look.
     * 
     * @param speed
     *            the speed of the mouse look.
     */
    public void setSpeed(float speed) {
        super.setSpeed( speed );
        mouseXSpeed = mouseXMultiplier * speed;
        mouseYSpeed = mouseYMultiplier * speed;
        rollInSpeed = mouseRollMultiplier * speed;
    }

    /**
     * <code>performAction</code> checks for any movement of the mouse, and
     * calls the appropriate method to alter the camera's orientation when
     * applicable.
     * 
     * @see com.jme.input.action.InputActionInterface#performAction(InputActionEvent)
     */
    public void performAction(InputActionEvent event) {
        if (!enabled)
            return;

        float time = 0.01f;
        if (lookMouse == -1 || MouseInput.get().isButtonDown(lookMouse)) {
            camera.setLooking(true);
            if (mouse.getLocalTranslation().x != 0) {
            	float amount;
            	if(invertRotate) {
            		amount = -time * mouse.getLocalTranslation().x;
            	} else {
            		amount = time * mouse.getLocalTranslation().x;
            	}
                rotateRight(amount, time);
                updated = true;
            } else if (rotateTarget)
                rotateRight(0, time);
            if (!lockAscent && mouse.getLocalTranslation().y != 0) {
                float amount = time * mouse.getLocalTranslation().y;
                rotateUp(amount);
                updated = true;
            }
        } else camera.setLooking(false);

        updateFromJoystick(time);
        
        int wdelta = MouseInput.get().getWheelDelta();
        if (wdelta != 0) {
            float amount = time * -wdelta;
            rollIn(amount);
            updated = true;
        }

        if (updated)
            camera.getCamera().onFrameChange();
    }

    protected void updateFromJoystick(float time) {
        //XXX: Get the evil constants out of this method...
        if (plugin != null) {
            float xAmnt = plugin.getJoystick().getAxisValue(plugin.getRotateAxis());
            float yAmnt = plugin.getJoystick().getAxisValue(plugin.getAscentAxis());
            
            if (xAmnt != 0) {
                rotateRight(xAmnt*.02f, time);
                updated = true;
            }
            if (!lockAscent && yAmnt != 0) {
                rotateUp(-yAmnt*.02f);
                updated = true;
            }
        }
    }

    /**
     * <code>rotateRight</code> updates the azimuth value of the camera's
     * spherical coordinates.
     * 
     * @param amount
     */
    private void rotateRight(float amount, float time) {
        Vector3f camPos = camera.getCamera().getLocation();
        Vector3f targetPos = target.getWorldTranslation();

        float azimuthAccel = (amount * mouseXSpeed);
        difTemp.set(camPos).subtractLocal(targetPos);
        
        if (worldUpVec.z == 1) {
            float y = difTemp.y;
            difTemp.y = difTemp.z;
            difTemp.z = y;
        }

        FastMath.cartesianToSpherical(difTemp, sphereTemp);
        sphereTemp.y = FastMath.normalize(sphereTemp.y + (azimuthAccel),
                -FastMath.TWO_PI, FastMath.TWO_PI);
        FastMath.sphericalToCartesian(sphereTemp, rightTemp);

        if (worldUpVec.z == 1) {
            float y = rightTemp.y;
            rightTemp.y = rightTemp.z;
            rightTemp.z = y;
        }

        rightTemp.addLocal(targetPos);
        camPos.set(rightTemp);
        if (camera.isMaintainAzimuth()) {
            camera.setForceAzimuthUpdate(true);
        }
        if (rotateTarget) {
            //First figure out the current facing vector.
            target.getLocalRotation().getRotationColumn(0, rightTemp);
            
            // get angle between vectors
            rightTemp.normalizeLocal();
            difTemp.y = 0;
            difTemp.negateLocal().normalizeLocal();
            float angle = rightTemp.angleBetween(difTemp);
            
            // calc how much angle we'll do
            float maxAngle = targetTurnSpeed * time;
            if (angle < 0 && -maxAngle > angle) {
                angle = -maxAngle;
            } else if (angle > 0 && maxAngle < angle) {
                angle = maxAngle;
            }

            //figure out rotation axis by taking cross product
            Vector3f rotAxis = rightTemp.crossLocal(difTemp).normalizeLocal();

            // Build a rotation quat and apply current local rotation.
            Quaternion q = rotTemp;
            q.fromAngleNormalAxis(angle, rotAxis);
            q.mult(target.getLocalRotation(), target.getLocalRotation());
        }
    }

    /**
     * <code>rotateRight</code> updates the altitude/polar value of the
     * camera's spherical coordinates.
     * 
     * @param amount
     */
    private void rotateUp(float amount) {
        if (invertedY)
            amount *= -1;
        Vector3f camPos = camera.getCamera().getLocation();
        Vector3f targetPos = target.getWorldTranslation();

        float thetaAccel = (amount * mouseYSpeed);
        difTemp.set(camPos).subtractLocal(targetPos).subtractLocal(
                camera.getTargetOffset());

        if (worldUpVec.z == 1) {
            float y = difTemp.y;
            difTemp.y = difTemp.z;
            difTemp.z = y;
        }
            
        FastMath.cartesianToSpherical(difTemp, sphereTemp);
        camera.getIdealSphereCoords().z = clampUpAngle(sphereTemp.z
                + (thetaAccel));
    }

    /**
     * <code>rollIn</code> updates the radius value of the camera's spherical
     * coordinates.
     * 
     * @param amount
     */
    private void rollIn(float amount) {
        camera.getIdealSphereCoords().x = clampRollIn(camera
                .getIdealSphereCoords().x
                + (amount * rollInSpeed));
    }

    /**
     * clampUpAngle
     * 
     * @param r
     *            float
     * @return float
     */
    private float clampUpAngle(float r) {
        if (Float.isInfinite(r) || Float.isNaN(r))
            return r;
        if (r > maxAscent)
            r = maxAscent;
        else if (r < minAscent)
            r = minAscent;
        return r;
    }

    /**
     * clampRollIn
     * 
     * @param r
     *            float
     * @return float
     */
    private float clampRollIn(float r) {
        if (Float.isInfinite(r) || Float.isNaN(r))
            return 100f;
        if (r > maxRollOut)
            r = maxRollOut;
        else if (r < minRollOut)
            r = minRollOut;
        return r;
    }

    /**
     * 
     * @param invertY
     *            true if mouse control should be inverted vertically
     */
    public void setInvertedY(boolean invertY) {
        this.invertedY = invertY;
    }

    /**
     * Returns whether vertical control is inverted (ie pulling down on the
     * mouse causes the camera to look up)
     * 
     * @return true if vertical control is inverted (aircraft style)
     */
    public boolean isInvertedY() {
        return invertedY;
    }

    /**
     * @return Returns the maxAscent.
     */
    public float getMaxAscent() {
        return maxAscent;
    }

    /**
     * @param maxAscent
     *            The maxAscent to set.
     */
    public void setMaxAscent(float maxAscent) {
        this.maxAscent = maxAscent;
        rotateUp(0);
    }

    /**
     * @return Returns the minAscent.
     */
    public float getMinAscent() {
        return minAscent;
    }

    /**
     * @param minAscent
     *            The minAscent to set.
     */
    public void setMinAscent(float minAscent) {
        this.minAscent = minAscent;
        rotateUp(0);
    }

    /**
     * @return Returns the maxRollOut.
     */
    public float getMaxRollOut() {
        return maxRollOut;
    }

    /**
     * @param maxRollOut
     *            The maxRollOut to set.
     */
    public void setMaxRollOut(float maxRollOut) {
        this.maxRollOut = maxRollOut;
        rollIn(0);
    }

    /**
     * @return Returns the minRollOut.
     */
    public float getMinRollOut() {
        return minRollOut;
    }

    /**
     * @param minRollOut
     *            The minRollOut to set.
     */
    public void setMinRollOut(float minRollOut) {
        this.minRollOut = minRollOut;
        rollIn(0);
    }

    /**
     * @return how quickly to turn the target in radians per second - only
     *         applicable if rotateTarget is true.
     */
    public float getTargetTurnSpeed() {
        return targetTurnSpeed;
    }

    /**
     * @param speed
     *            how quickly to turn the target in radians per second - only
     *            applicable if rotateTarget is true.
     */
    public void setTargetTurnSpeed(float speed) {
        this.targetTurnSpeed = speed;
    }

    /**
     * @return Returns the mouseXMultiplier.
     */
    public float getMouseXMultiplier() {
        return mouseXMultiplier;
    }

    /**
     * @param mouseXMultiplier
     *            The mouseXMultiplier to set. Updates mouseXSpeed as well.
     */
    public void setMouseXMultiplier(float mouseXMultiplier) {
        this.mouseXMultiplier = mouseXMultiplier;
        mouseXSpeed = speed * mouseXMultiplier;
    }

    /**
     * @return Returns the mouseYMultiplier.
     */
    public float getMouseYMultiplier() {
        return mouseYMultiplier;
    }

    /**
     * @param mouseYMultiplier
     *            The mouseYMultiplier to set. Updates mouseYSpeed as well.
     */
    public void setMouseYMultiplier(float mouseYMultiplier) {
        this.mouseYMultiplier = mouseYMultiplier;
        mouseYSpeed = speed * mouseYMultiplier;
    }

    /**
     * @return Returns the mouseRollMultiplier.
     */
    public float getMouseRollMultiplier() {
        return mouseRollMultiplier;
    }

    /**
     * @param mouseRollMultiplier
     *            The mouseRollMultiplier to set. Updates rollInSpeed as well.
     */
    public void setMouseRollMultiplier(float mouseRollMultiplier) {
        this.mouseRollMultiplier = mouseRollMultiplier;
        rollInSpeed = speed * mouseRollMultiplier;
    }

    /**
     * @param lock
     *            true if camera's polar angle / ascent value should never
     *            change.
     */
    public void setLockAscent(boolean lock) {
        lockAscent = lock;
    }
    
    /**
     * @return true if camera's polar angle / ascent value should never change.
     */
    public boolean isLockAscent() {
        return lockAscent;
    }

    /**
     * @return true if mouselook is enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled
     *            true to allow mouselook to affect camera.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return true if turning mouse should cause the target to turn as well.
     */
    public boolean isRotateTarget() {
        return rotateTarget;
    }

    /**
     * @param rotateTarget
     *            true if turning mouse should cause the target to turn as well.
     */
    public void setRotateTarget(boolean rotateTarget) {
        this.rotateTarget = rotateTarget;
    }

    /**
     * @return the index of the button that must be pressed to activate looking
     *         or -1 if no button is needed
     */
    public int getLookMouseButton() {
        return lookMouse;
    }

    /**
     * Sets the button to use for look actions. For example, if set to 0, the
     * left button must be held down to move the camera around.
     * 
     * @param button
     *            index of required button or -1 (default) if none
     */
    public void setLookMouseButton(int button) {
        this.lookMouse = button;
    }

    /**
     * @param worldUpVec The worldUpVec to set (as copy)
     */
    public void setWorldUpVec(Vector3f worldUpVec) {
        this.worldUpVec.set(worldUpVec);
    }

    /**
     * @return Returns the joystick plugin or null if not set.
     */
    public ThirdPersonJoystickPlugin getJoystickPlugin() {
        return plugin;
    }

    /**
     * @param joystick The joystick plugin to set.
     */
    public void setJoystickPlugin(ThirdPersonJoystickPlugin joystick) {
        this.plugin = joystick;
    }

	public ChaseCamera getChaseCamera() {
		return camera;
	}

	public void setChaseCamera(ChaseCamera camera) {
		this.camera = camera;
	}

	public Spatial getTarget() {
		return target;
	}

	public void setTarget(Spatial target) {
		this.target = target;
	}
}
