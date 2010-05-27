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

package jmetest.flagrushtut.lesson9;

import jmetest.flagrushtut.lesson9.actions.DriftAction;
import jmetest.flagrushtut.lesson9.actions.ForwardAndBackwardAction;
import jmetest.flagrushtut.lesson9.actions.VehicleRotateAction;

import com.jme.input.InputHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;

/**
 * Input Handler for the Flag Rush game. This controls a supplied spatial
 * allowing us to move it forward, backward and rotate it left and right.
 * @author Mark Powell
 *
 */
public class FlagRushHandler extends InputHandler {
    //the vehicle we are going to control
    private Vehicle vehicle;
    //the default action
    private DriftAction drift;
    
    public void update(float time) {
        if ( !isEnabled() ) return;

        super.update(time);
        //we always want to allow friction to control the drift
        drift.performAction(event);
        vehicle.update(time);
    }
    
    /**
     * Supply the node to control and the api that will handle input creation.
     * @param vehicle the node we wish to move
     * @param api the library that will handle creation of the input.
     */
    public FlagRushHandler(Vehicle vehicle, String api) {
        this.vehicle = vehicle;
        setKeyBindings(api);
        setActions(vehicle);

    }

    /**
     * creates the keyboard object, allowing us to obtain the values of a keyboard as keys are
     * pressed. It then sets the actions to be triggered based on if certain keys are pressed (WSAD).
     * @param api the library that will handle creation of the input.
     */
    private void setKeyBindings(String api) {
        KeyBindingManager keyboard = KeyBindingManager.getKeyBindingManager();

        keyboard.set("forward", KeyInput.KEY_W);
        keyboard.set("backward", KeyInput.KEY_S);
        keyboard.set("turnRight", KeyInput.KEY_D);
        keyboard.set("turnLeft", KeyInput.KEY_A);
    }

    /**
     * assigns action classes to triggers. These actions handle moving the node forward, backward and 
     * rotating it. It also creates an action for drifting that is not assigned to key trigger, this
     * action will occur each frame.
     * @param node the node to control.
     */
    private void setActions(Vehicle node) {
        ForwardAndBackwardAction forward = new ForwardAndBackwardAction(node, ForwardAndBackwardAction.FORWARD);
        addAction(forward, "forward", true);
        ForwardAndBackwardAction backward = new ForwardAndBackwardAction(node, ForwardAndBackwardAction.BACKWARD);
        addAction(backward, "backward", true);
        VehicleRotateAction rotateLeft = new VehicleRotateAction(node, VehicleRotateAction.LEFT);
        addAction(rotateLeft, "turnLeft", true);
        VehicleRotateAction rotateRight = new VehicleRotateAction(node, VehicleRotateAction.RIGHT);
        addAction(rotateRight, "turnRight", true);
        
        //not triggered by keyboard
        drift = new DriftAction(node);
    }
}
