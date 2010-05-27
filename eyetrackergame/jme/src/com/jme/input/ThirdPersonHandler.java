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

package com.jme.input;

import java.util.HashMap;

import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;
import com.jme.input.thirdperson.MovementPermitter;
import com.jme.input.thirdperson.ThirdPersonBackwardAction;
import com.jme.input.thirdperson.ThirdPersonForwardAction;
import com.jme.input.thirdperson.ThirdPersonJoystickPlugin;
import com.jme.input.thirdperson.ThirdPersonLeftAction;
import com.jme.input.thirdperson.ThirdPersonRightAction;
import com.jme.input.thirdperson.ThirdPersonStrafeLeftAction;
import com.jme.input.thirdperson.ThirdPersonStrafeRightAction;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.scene.Spatial;

/**
 * <code>ThirdPersonHandler</code> defines an InputHandler that sets input to
 * be controlled similar to games such as Zelda Windwaker and Mario 64, etc.
 * 
 * @author <a href="mailto:josh@renanse.com">Joshua Slack</a>
 * @version $Revision: 4666 $
 */

public class ThirdPersonHandler extends InputHandler {
    public static final String PROP_TURNSPEED = "turnSpeed";
    public static final String PROP_DOGRADUAL = "doGradual";
    public static final String PROP_ROTATEONLY = "rotateOnly";
    public static final String PROP_PERMITTER = "permitter";
    public static final String PROP_UPVECTOR = "upVector";
    public static final String PROP_LOCKBACKWARDS = "lockBackwards";
    public static final String PROP_CAMERAALIGNEDMOVE = "cameraAlignedMovement";
    public static final String PROP_STRAFETARGETALIGN = "targetAlignStrafe";

    public static final String PROP_KEY_FORWARD = "fwdKey";
    public static final String PROP_KEY_BACKWARD = "backKey";
    public static final String PROP_KEY_LEFT = "leftKey";
    public static final String PROP_KEY_RIGHT = "rightKey";
    public static final String PROP_KEY_STRAFELEFT = "strfLeftKey";
    public static final String PROP_KEY_STRAFERIGHT = "strfRightKey";
    
    
    /** Default character turn speed is 1.5pi per sec. */
    public static final float DEFAULT_TURNSPEED = 1.5f * FastMath.PI;

    public static float angleEpsilon = 0.001f;
    
    protected float speed = 0;
    
    /** The Spatial we are controlling with this handler. */
    protected Spatial targetSpatial;

    /**
     * The previous location of the target node... used to maintain where the
     * node is before actions are run. This allows a comparison to see where the
     * node wants to be taken.
     */
    protected Vector3f prevLoc = new Vector3f();

    /**
     * The previous rotation of the target node... used to maintain the node's
     * rotation before actions are run. This is used when target aligned
     * movement is used and strafing is done.
     */
    protected Quaternion prevRot = new Quaternion();

    /**
     * Stores the new location of the node after actions. used internally by
     * update method
     */
    protected Vector3f loc = new Vector3f();

    /**
     * The current facing direction of the controlled target in radians in terms
     * of relationship to the world.
     */
    protected float faceAngle;

    /**
     * How fast the character can turn per second. Used when doGradualRotation
     * is set to true.
     */
    protected float turnSpeed = DEFAULT_TURNSPEED;

    /**
     * When true, the controlled target will do turns by moving forward and
     * turning at the same time. When false, a turn will cause immediate
     * rotation to the given angle.
     */
    protected boolean doGradualRotation = true;

    /**
     * When not null, gives a means for denying movement to the controller. See
     * MovementPermitter javadoc for more.
     */
    protected MovementPermitter permitter;

    /** World up vector.  Currently 0,1,0 is the only guaranteed value to work. */
    protected Vector3f upVector = new Vector3f(0, 1, 0);

    /** An internal vector used for calculations to prevent object creation. */
    protected Vector3f calcVector = new Vector3f();

    /** The camera this handler uses for determining action movement. */
    protected Camera camera;
    
    /**
     * if true, backwards movement will not cause the target to rotate around to
     * point backwards. (useful for vehicle movement) Default is false.
     */
    protected boolean lockBackwards;
    
    /**
     * if true, strafe movements will always be target aligned, even if other
     * movement is camera aligned.  Default is false.
     */
    protected boolean strafeAlignTarget;

    /**
     * if true, left and right keys will rotate the target instead of moving them.
     * Default is false.
     */
    protected boolean rotateOnly;
    
    /**
     * if true, movements of the character are in relation to the current camera
     * view. If false, they are in relation to the current target's facing
     * vector. Default is true.
     */
    protected boolean cameraAlignedMovement;
    
    /**
     * internally used boolean for denoting that a backwards action is currently
     * being performed.
     */
    protected boolean walkingBackwards;
    
    /**
     * internally used boolean for denoting that a forward action is currently
     * being performed.
     */
    protected boolean walkingForward;
    
    /**
     * internally used boolean for denoting that a turning action is currently
     * being performed.
     */
    protected boolean nowTurning;
    
    /**
     * internally used boolean for denoting that a turning action is currently
     * being performed.
     */
    protected boolean nowStrafing;

    protected ThirdPersonJoystickPlugin plugin = null;
    
    protected KeyInputAction actionForward;
    protected KeyInputAction actionBack;
    protected KeyInputAction actionRight;
    protected KeyInputAction actionLeft;
    protected KeyInputAction actionStrafeRight;
    protected KeyInputAction actionStrafeLeft;

    /**
     * Basic constructor for the ThirdPersonHandler. Sets all non specified args
     * to their defaults.
     * 
     * @param target
     *            the target to move
     * @param cam
     *            the camera for movements to be in relation to
     */
    public ThirdPersonHandler(Spatial target, Camera cam) {
        this(target, cam, null);
    }
    
    /**
     * Full constructor for the ThirdPersonHandler. Properties in the props arg
     * will be used to set handler fields if set, otherwise default values are
     * used.
     * 
     * @param target
     *            the target to move
     * @param cam
     *            the camera for movements to be in relation to
     * @param props
     *            a hashmap of properties used to set handler characteristics
     *            where the key is one of this class's static PROP_XXXX fields.
     */
    public ThirdPersonHandler(Spatial target, Camera cam, HashMap<String, Object> props) {
        this.targetSpatial = target;
        this.camera = cam;

        updateProperties(props);
        setActions();
    }

    /**
     * 
     * <code>setProperties</code> sets up class fields from the given hashmap.
     * It also calls updateKeyBindings for you.
     * 
     * @param props
     */
    public void updateProperties(HashMap<String, Object> props) {
        turnSpeed = getFloatProp(props, PROP_TURNSPEED, DEFAULT_TURNSPEED);
        doGradualRotation = getBooleanProp(props, PROP_DOGRADUAL, true);
        lockBackwards = getBooleanProp(props, PROP_LOCKBACKWARDS, false);
        strafeAlignTarget = getBooleanProp(props, PROP_STRAFETARGETALIGN, false);
        cameraAlignedMovement = getBooleanProp(props, PROP_CAMERAALIGNEDMOVE, true);
        rotateOnly = getBooleanProp(props, PROP_ROTATEONLY, false);
        permitter = (MovementPermitter)getObjectProp(props, PROP_PERMITTER, null);
        upVector = (Vector3f)getObjectProp(props, PROP_UPVECTOR, Vector3f.UNIT_Y.clone());
        updateKeyBindings(props);
    }

    /**
     * 
     * <code>updateKeyBindings</code> allows a user to update the keys mapped to the various actions.
     * 
     * @param props
     */
    public void updateKeyBindings(HashMap<String, Object> props) {
        KeyBindingManager keyboard = KeyBindingManager.getKeyBindingManager();
        keyboard.set(PROP_KEY_FORWARD, getIntProp(props, PROP_KEY_FORWARD, KeyInput.KEY_W));
        keyboard.set(PROP_KEY_BACKWARD, getIntProp(props, PROP_KEY_BACKWARD, KeyInput.KEY_S));
        keyboard.set(PROP_KEY_LEFT, getIntProp(props, PROP_KEY_LEFT, KeyInput.KEY_A));
        keyboard.set(PROP_KEY_RIGHT, getIntProp(props, PROP_KEY_RIGHT, KeyInput.KEY_D));
        keyboard.set(PROP_KEY_STRAFELEFT, getIntProp(props, PROP_KEY_STRAFELEFT, KeyInput.KEY_Q));
        keyboard.set(PROP_KEY_STRAFERIGHT, getIntProp(props, PROP_KEY_STRAFERIGHT, KeyInput.KEY_E));        
    }

    /**
     * 
     * <code>setActions</code> sets the keyboard actions with the
     * corresponding key command.
     *
     */
    protected void setActions() {
        actionForward = new ThirdPersonForwardAction( this, 100f );
        actionBack = new ThirdPersonBackwardAction( this, 100f );
        actionRight = new ThirdPersonRightAction( this, 100f );
        actionLeft = new ThirdPersonLeftAction( this, 100f );
        actionStrafeRight = new ThirdPersonStrafeRightAction( this, 100f );
        actionStrafeLeft = new ThirdPersonStrafeLeftAction( this, 100f );
        addAction( actionForward, PROP_KEY_FORWARD, true );
        addAction( actionBack, PROP_KEY_BACKWARD, true );
        addAction( actionRight, PROP_KEY_RIGHT, true );
        addAction( actionLeft, PROP_KEY_LEFT, true );
        addAction( actionStrafeRight, PROP_KEY_STRAFERIGHT, true );
        addAction( actionStrafeLeft, PROP_KEY_STRAFELEFT, true );
    }

    /**
     * <code>update</code> updates the position and rotation of the target
     * based on the movement requested by the user.
     * 
     * @param time
     * @see com.jme.input.InputHandler#update(float)
     */
    public void update(float time) {
        if ( !isEnabled() ) return;

        walkingForward = false;
        walkingBackwards = false;
        nowTurning = false;
        nowStrafing = false;

        prevLoc.set(targetSpatial.getLocalTranslation());
        loc.set(prevLoc);

        doInputUpdate(time);
        if (walkingBackwards && walkingForward && !nowStrafing && !nowTurning) {
            targetSpatial.getLocalTranslation().set(prevLoc);
            return;
        }
        targetSpatial.getLocalTranslation().subtract(loc, loc);
        if (!loc.equals(Vector3f.ZERO)) {
            float distance = loc.length();
            if (distance != 0 && distance != 1.0f)
                loc.divideLocal(distance); // this is same as normalizeLocal.
            
            float actAngle = 0;
            targetSpatial.getLocalRotation().getRotationColumn(2, calcVector);
            if (upVector.y == 1) {
                actAngle = FastMath.atan2(loc.z, loc.x);
                if (!nowTurning && !nowStrafing) {
                    faceAngle = FastMath.atan2(calcVector.z, calcVector.x);
                }
            } else if (upVector.x == 1) {
                actAngle = FastMath.atan2(loc.z, loc.y);
                if (!nowTurning && !nowStrafing)
                    faceAngle = FastMath.atan2(calcVector.z, calcVector.y);
            } else if (upVector.z == 1) {
                actAngle = FastMath.atan2(loc.x, loc.y) - FastMath.HALF_PI;
                if (!nowTurning && !nowStrafing)
                    faceAngle = FastMath.atan2(calcVector.x, calcVector.y) - FastMath.HALF_PI;
            }
            
            float oldFace = faceAngle;
            calcFaceAngle(actAngle, time);
            if (nowStrafing) {
                faceAngle = actAngle;
                prevRot.set(targetSpatial.getLocalRotation());
            }
            targetSpatial.getLocalRotation().fromAngleNormalAxis(-(faceAngle - FastMath.HALF_PI), upVector);
            targetSpatial.getLocalRotation().getRotationColumn(2, calcVector).multLocal(distance);

            if (nowStrafing) {
                if (!strafeAlignTarget && cameraAlignedMovement) {
                    if (upVector.y == 1) {
                        faceAngle = FastMath.atan2(camera.getDirection().z, camera.getDirection().x);
                    } else if (upVector.x == 1) {
                        faceAngle = FastMath.atan2(camera.getDirection().z, camera.getDirection().y);
                    } else if (upVector.z == 1) {
                        faceAngle = FastMath.atan2(camera.getDirection().x, camera.getDirection().y) - FastMath.HALF_PI;
                    }
                    targetSpatial.getLocalRotation().fromAngleNormalAxis(-faceAngle, upVector);
                } else {
                    targetSpatial.getLocalRotation().set(prevRot);
                    faceAngle = oldFace;
                }
            }

            targetSpatial.getLocalTranslation().set(prevLoc);
            if (lockBackwards && walkingBackwards && !nowStrafing)
                targetSpatial.getLocalTranslation().subtractLocal(calcVector);
            else if (rotateOnly && nowTurning && !walkingBackwards && !walkingForward)
                ; // no translation
            else 
                targetSpatial.getLocalTranslation().addLocal(calcVector);
        }
    }

    protected void doInputUpdate(float time) {
        super.update(time);
        updateFromJoystick(time);
    }

    protected void updateFromJoystick(float time) {
        if (plugin == null) return;
        float xAmnt = plugin.getJoystick().getAxisValue(plugin.getXAxis());
        float yAmnt = plugin.getJoystick().getAxisValue(plugin.getYAxis());
        
        InputActionEvent evt = new InputActionEvent();
        if (xAmnt > 0) {
            evt.setTime(time*xAmnt);
            actionRight.performAction(evt);
        } else if (xAmnt < 0) {
            evt.setTime(time*-xAmnt);
            actionLeft.performAction(evt);            
        }
        
        if (yAmnt > 0) {
            evt.setTime(time*yAmnt);
            actionBack.performAction(evt);
        } else if (yAmnt < 0) {
            evt.setTime(time*-yAmnt);
            actionForward.performAction(evt);            
        }
    }

    /**
     * <code>calcFaceAngle</code>
     * @param actAngle
     * @param time
     */
    protected void calcFaceAngle(float actAngle, float time) {
        if (doGradualRotation) {
            faceAngle = FastMath.normalize(faceAngle, -FastMath.PI, FastMath.PI);
            float oldAct = actAngle;

            // Check the difference between action angle and current facing angle.
            actAngle -= faceAngle;
            actAngle = FastMath.normalize(actAngle, -FastMath.PI, FastMath.PI);
            if (FastMath.abs(actAngle) <= angleEpsilon) {
                return;
            }

            boolean above = faceAngle > oldAct;
            if (lockBackwards && walkingBackwards) {
                // update faceangle rotation towards action angle
                if (actAngle > angleEpsilon && actAngle < FastMath.PI)
                    faceAngle -= time * turnSpeed;
                else if (actAngle < -angleEpsilon || actAngle > FastMath.PI)
                    faceAngle += time * turnSpeed;
            } else {
                // update faceangle rotation towards action angle
                if (actAngle > angleEpsilon && actAngle < FastMath.PI) {
                    faceAngle += time * turnSpeed;
                    if (!above && faceAngle > oldAct) faceAngle = oldAct;
                } else if (actAngle < -angleEpsilon || actAngle > FastMath.PI) {
                    faceAngle -= time * turnSpeed;
                    if (above && faceAngle < oldAct) faceAngle = oldAct;
                }
            }
        } else {
            if (lockBackwards && walkingBackwards)
                faceAngle = FastMath.PI + actAngle;
            else
                faceAngle = actAngle;
        }
    }

    /**
     * @return Returns the turnSpeed.
     */
    public float getTurnSpeed() {
        return turnSpeed;
    }

    /**
     * @param turnSpeed
     *            The turnSpeed to set.
     */
    public void setTurnSpeed(float turnSpeed) {
        this.turnSpeed = turnSpeed;
    }

    /**
     * @return Returns the upAngle.
     */
    public Vector3f getUpVector() {
        return upVector;
    }

    /**
     * @param upAngle
     *            The upAngle to set (as copy)
     */
    public void setUpVector(Vector3f upAngle) {
        this.upVector.set(upAngle);
    }

    /**
     * @return Returns the faceAngle (in radians)
     */
    public float getFaceAngle() {
        return faceAngle;
    }

    /**
     * @return Returns the doGradualRotation.
     */
    public boolean isDoGradualRotation() {
        return doGradualRotation;
    }

    /**
     * @param doGradualRotation
     *            The doGradualRotation to set.
     */
    public void setDoGradualRotation(boolean doGradualRotation) {
        this.doGradualRotation = doGradualRotation;
    }

    public MovementPermitter getPermitter() {
        return permitter;
    }

    public Spatial getTarget() {
        return targetSpatial;
    }

    public void setTarget(Spatial target) {
        this.targetSpatial = target;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setStrafeAlignTarget(boolean b) {
        strafeAlignTarget = b;
    }

    public boolean isStrafeAlignTarget() {
        return strafeAlignTarget;
    }

    public void setLockBackwards(boolean b) {
        lockBackwards = b;
    }

    public boolean isLockBackwards() {
        return lockBackwards;
    }

    public void setRotateOnly(boolean b) {
        rotateOnly = b;
    }

    public boolean isRotateOnly() {
        return rotateOnly;
    }

    public void setCameraAlignedMovement(boolean b) {
        cameraAlignedMovement = b;
    }

    public boolean isCameraAlignedMovement() {
        return cameraAlignedMovement;
    }

    /**
     * Internal method used to let the handler know that the target is currently
     * moving forward (via use of the forward key.)
     * 
     * @param forward
     */
    public void setGoingForward(boolean forward) {
        walkingForward = forward;
    }

    /**
     * Internal method used to let the handler know that the target is currently
     * moving backwards (via use of the back key.)
     * 
     * @param backwards
     */
    public void setGoingBackwards(boolean backwards) {
        walkingBackwards = backwards;
    }

    /**
     * Internal method used to let the handler know that the target is currently
     * turning/moving left/right (via use of the left/right keys.)
     * 
     * @param turning
     */
    public void setTurning(boolean turning) {
        nowTurning = turning;
    }

    /**
     * Internal method used to let the handler know that the target is currently
     * strafing left/right (via use of the strafe left/right keys.)
     * 
     * @param strafe
     */
    public void setStrafing(boolean strafe) {
        nowStrafing = strafe;
    }

    /**
     * @return true if last update of handler included a turn left/right action.
     */
    public boolean isNowTurning() {
        return nowTurning;
    }

    /**
     * @return true if last update of handler included a walk backwards action.
     */
    public boolean isWalkingBackwards() {
        return walkingBackwards;
    }

    /**
     * @return true if last update of handler included a walk forward action.
     */
    public boolean isWalkingForward() {
        return walkingForward;
    }

    /**
     * @return true if last update of handler included a walk forward action.
     */
    public boolean isStrafing() {
        return nowStrafing;
    }

    public void setActionSpeed(float speed) {
        super.setActionSpeed(speed);
        this.speed = speed;
    }
    
    public float getSpeed() {
        return speed;
    }

    /**
     * @return Returns the joystick plugin or null if not set.
     */
    public ThirdPersonJoystickPlugin getJoystickPlugin() {
        return plugin;
    }

    /**
     * @param plugin The joystick plugin to set.
     */
    public void setJoystickPlugin(ThirdPersonJoystickPlugin plugin) {
        this.plugin = plugin;
    }
}
