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

import com.jme.input.ThirdPersonHandler;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;
import com.jme.math.Vector3f;

/**
 * 
 * <code>ThirdPersonBackwardAction</code>
 * 
 * @author Joshua Slack
 * @version $Revision: 4131 $
 */
public class ThirdPersonBackwardAction extends KeyInputAction {

    private Vector3f rot;

    private ThirdPersonHandler handler;

    /**
     * Constructor creates a new <code>ThirdPersonBackwardAction</code>
     * object. During construction, the node to direct and the speed at which to
     * move the node is set.
     * 
     * @param handler
     *            the associated handler
     * @param speed
     *            the speed at which the camera can move.
     */
    public ThirdPersonBackwardAction(ThirdPersonHandler handler, float speed) {
        this.handler = handler;
        this.speed = speed;
        rot = new Vector3f();
    }

    /**
     * <code>performAction</code> moves the node along it's positive direction
     * vector at a speed of movement speed * time. Where time is the time
     * between frames and 1 corresponds to 1 second.
     * 
     * @see com.jme.input.action.InputActionInterface#performAction(InputActionEvent)
     */
    public void performAction(InputActionEvent event) {
        if (handler.getPermitter() != null
                && !handler.getPermitter().canBeMoved())
            return;
        handler.setGoingBackwards(true);
        Vector3f loc = handler.getTarget().getLocalTranslation();
        if (handler.isCameraAlignedMovement()) {
            rot.set(handler.getCamera().getDirection());
            rot.y = 0;
        } else {
            handler.getTarget().getLocalRotation().getRotationColumn(2, rot);
        }
        rot.normalizeLocal();
        loc.subtractLocal(rot.multLocal((speed * event.getTime())));
    }
}
