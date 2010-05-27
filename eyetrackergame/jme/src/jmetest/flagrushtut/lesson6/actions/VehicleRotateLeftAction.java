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

package jmetest.flagrushtut.lesson6.actions;

import jmetest.flagrushtut.lesson6.Vehicle;

import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;
import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;

/**
 * VehicleRotateLeftAction turns the vehicle to the left (while 
 * traveling forward).
 * @author Mark Powell
 *
 */
public class VehicleRotateLeftAction extends KeyInputAction {
    //temporary variables to handle rotation
    private static final Matrix3f incr = new Matrix3f();
    private static final Matrix3f tempMa = new Matrix3f();
    private static final Matrix3f tempMb = new Matrix3f();

    //we are using +Y as our up
    private Vector3f upAxis = new Vector3f(0,1,0);
    //the node to manipulate
    private Vehicle vehicle;
   
    /**
     * create a new action with the vehicle to turn.
     * @param vehicle the vehicle to turn
     */
    public VehicleRotateLeftAction(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    /**
     * turn the vehicle by its turning speed. If the vehicle is traveling 
     * backwards, swap direction.
     */
    public void performAction(InputActionEvent evt) {
        //we want to turn differently depending on which direction we are traveling in.
        if(vehicle.getVelocity() < 0) {
            incr.fromAngleNormalAxis(-vehicle.getTurnSpeed() * evt.getTime(), upAxis);
        } else {
            incr.fromAngleNormalAxis(vehicle.getTurnSpeed() * evt.getTime(), upAxis);
        }
        vehicle.getLocalRotation().fromRotationMatrix(
                incr.mult(vehicle.getLocalRotation().toRotationMatrix(tempMa),
                        tempMb));
        vehicle.getLocalRotation().normalize();
    }
}