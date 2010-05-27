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

import com.jme.input.action.KeyBackwardAction;
import com.jme.input.action.KeyForwardAction;
import com.jme.input.action.KeyLookDownAction;
import com.jme.input.action.KeyLookUpAction;
import com.jme.input.action.KeyRotateLeftAction;
import com.jme.input.action.KeyRotateRightAction;
import com.jme.input.action.KeyStrafeDownAction;
import com.jme.input.action.KeyStrafeLeftAction;
import com.jme.input.action.KeyStrafeRightAction;
import com.jme.input.action.KeyStrafeUpAction;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;

/**
 * <code>KeyboardLookHandler</code> defines an InputHandler that sets
 * input to be controlled similar to First Person Shooting games. By default the
 * commands are, WSAD moves the camera forward, backward and strafes. The
 * arrow keys rotate and tilt the camera.
 */
public class KeyboardLookHandler extends InputHandler {
    private KeyForwardAction forward;
    private KeyBackwardAction backward;
    private KeyStrafeLeftAction sLeft;
    private KeyStrafeRightAction sRight;
    private KeyRotateRightAction right;
    private KeyRotateLeftAction left;
    private KeyStrafeDownAction down;
    private KeyStrafeUpAction up;

    private float moveSpeed;
    
    public KeyboardLookHandler( Camera cam, float moveSpeed, float rotateSpeed ) {
        this.moveSpeed = moveSpeed;
        KeyBindingManager keyboard = KeyBindingManager.getKeyBindingManager();

        keyboard.set( "forward", KeyInput.KEY_W );
        keyboard.set( "backward", KeyInput.KEY_S );
        keyboard.set( "strafeLeft", KeyInput.KEY_A );
        keyboard.set( "strafeRight", KeyInput.KEY_D );
        keyboard.set( "lookUp", KeyInput.KEY_UP );
        keyboard.set( "lookDown", KeyInput.KEY_DOWN );
        keyboard.set( "turnRight", KeyInput.KEY_RIGHT );
        keyboard.set( "turnLeft", KeyInput.KEY_LEFT );
        keyboard.set( "elevateUp", KeyInput.KEY_Q);
        keyboard.set( "elevateDown", KeyInput.KEY_Z);

        forward = new KeyForwardAction( cam, moveSpeed );
        addAction( forward, "forward", true );
        backward = new KeyBackwardAction( cam, moveSpeed );
        addAction( backward, "backward", true );
        sLeft = new KeyStrafeLeftAction( cam, moveSpeed );
        addAction( sLeft, "strafeLeft", true );
        sRight = new KeyStrafeRightAction( cam, moveSpeed );
        addAction( sRight, "strafeRight", true );
        addAction( new KeyLookUpAction( cam, rotateSpeed ), "lookUp", true );
        addAction( new KeyLookDownAction( cam, rotateSpeed ), "lookDown", true );
        down = new KeyStrafeDownAction(cam, moveSpeed);
        Vector3f upVec = new Vector3f(cam.getUp());
        down.setUpVector(upVec);
        addAction(down, "elevateDown", true);
        up = new KeyStrafeUpAction( cam, moveSpeed );
        up.setUpVector(upVec);
        addAction( up, "elevateUp", true);
        right = new KeyRotateRightAction( cam, rotateSpeed );
        right.setLockAxis(new Vector3f(cam.getUp()));
        addAction(right, "turnRight", true );
        left = new KeyRotateLeftAction( cam, rotateSpeed );
        left.setLockAxis(new Vector3f(cam.getUp()));
        addAction( left, "turnLeft", true );
    }
    
    public void setLockAxis(Vector3f lock) {
        right.setLockAxis(new Vector3f(lock));
        left.setLockAxis(new Vector3f(lock));
    }
    
    public void setUpAxis(Vector3f upAxis) {
        up.setUpVector(upAxis);
        down.setUpVector(upAxis);
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public void setMoveSpeed(float moveSpeed) {
        if(moveSpeed < 0) {
            moveSpeed = 0;
        }
        this.moveSpeed = moveSpeed;
        
        forward.setSpeed(moveSpeed);
        backward.setSpeed(moveSpeed);
        sLeft.setSpeed(moveSpeed);
        sRight.setSpeed(moveSpeed);
        down.setSpeed(moveSpeed);
        up.setSpeed(moveSpeed);
    }
}
