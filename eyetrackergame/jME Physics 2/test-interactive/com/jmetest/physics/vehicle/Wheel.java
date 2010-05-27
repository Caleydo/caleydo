/*
 * Copyright (c) 2005-2007 jME Physics 2
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of 'jME Physics 2' nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
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

package com.jmetest.physics.vehicle;

import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.Joint;
import com.jmex.physics.RotationalJointAxis;
import com.jmex.physics.material.Material;

/**
 * A wheel for the vehicle.
 * Contains physics, visual representation and helper methods for controlling it.
 *
 * @author Erick B Passos
 */
public class Wheel extends Node {

    private static final Vector3f TRACTION_AXIS_VECTOR = new Vector3f( 0, 0, 1 );
    private static final Vector3f STEER_AXIS_VECTOR = new Vector3f( 0, 1, 0 );
    private static final long serialVersionUID = 1L;

    // Better not to change these much... Check some customizable ones at CarData.java
    // Speed that will be applied to the wheel steering rotational axis
    private static final float TURN_SPEED = 12f;
    // Max turn angle
    private static final float MAX_TURN = 0.25f;
    // Acceleration available to the wheel steering rotational axis
    private static final float TURN_ACCEL = 50;

    private DynamicPhysicsNode node;
    private Joint wheelJoint;
    private RotationalJointAxis tractionAxis, steerAxis;
    private Vector3f relativePosition;
    private final DynamicPhysicsNode suspensionBase;

    public Wheel( final DynamicPhysicsNode suspensionBase, float zOffset, boolean steer ) {
        super( "WheelNode" );

        relativePosition = new Vector3f( 0, 0, zOffset );

        this.suspensionBase = suspensionBase;
        createWheel();

        createJointAndAxis( steer );

        this.attachChild( node );
    }

    /**
     * Creates the joint and the rotational axis for wheel sppining and steering (if available)
     *
     * @param steer          to determine if should create the steering rotational axis
     */
    private void createJointAndAxis( boolean steer ) {
        wheelJoint = suspensionBase.getSpace().createJoint();
        wheelJoint.attach( suspensionBase, node );
        wheelJoint.setAnchor( suspensionBase.getWorldRotation().getRotationColumn( 2 ).mult( relativePosition ) );

        // If it's a steerable wheel, first creates the steering rotational axis
        if ( steer ) {
            steerAxis = wheelJoint.createRotationalAxis();
            steerAxis.setDirection( STEER_AXIS_VECTOR );
            steerAxis.setAvailableAcceleration( TURN_ACCEL );
        }

        // The wheel sppining rotational axis, always available
        tractionAxis = wheelJoint.createRotationalAxis();
        tractionAxis.setDirection( TRACTION_AXIS_VECTOR );
        tractionAxis.setAvailableAcceleration( CarData.WHEEL_ACCEL );

        // If there are two axis, the second must be relative to the second object (the wheel itself)
        if ( steer ) {
            tractionAxis.setRelativeToSecondObject( true );
            unsteer();
        }
    }

    /**
     * Creates physics node and visual representations.
     * The physics is based on a pure sphere collision geometry.
     *
     */
    private void createWheel() {
        node = suspensionBase.getSpace().createDynamicNode();
        node.setName( "wheel" );
        resetPosition();
        node.createSphere( "wheelCollisionSphere" );
        node.setLocalScale( CarData.WHEEL_SCALE );

        node.generatePhysicsGeometry();
        // Fancy wheel model being loaded
        Node wheelModel = Util.loadModel( CarData.WHEEL_MODEL );
        wheelModel.setName( "WheelModel" );
        node.attachChild( wheelModel );
        node.setMaterial( Material.RUBBER );
        node.setMass( CarData.WHEEL_MASS );
    }

    /**
     * Accelerates this wheel by setting the desired velocity for its traction axis.
     *
     * @param direction 1 for ahead and -1 for back.
     */
    public void accelerate( final int direction ) {
        tractionAxis.setDesiredVelocity( direction * CarData.WHEEL_SPEED );
    }

    /**
     * Releases the desired velocity of the traction axis.
     */
    public void releaseAccel() {
        tractionAxis.setDesiredVelocity( 0 );
    }

    /**
     * Steers this wheel by setting a velocity to its steering rotational axis.
     * Also sets the maximum and minimum steering angles.
     *
     * @param direction 1 for right and -1 for left
     */
    public void steer( final float direction ) {
        steerAxis.setDesiredVelocity( direction * TURN_SPEED );
        steerAxis.setPositionMaximum( MAX_TURN );
        steerAxis.setPositionMinimum( -MAX_TURN );
    }

    /**
     * Locks the wheels by setting 0 for the velocity and disabling changes in the position.
     */
    public void unsteer() {
        steerAxis.setDesiredVelocity( 0 );
        steerAxis.setPositionMaximum( 0 );
        steerAxis.setPositionMinimum( 0 );
    }

    /**
     * Needed by the custom CollisionCallback that disables collisions with the chassis.
     *
     * @return physics node of this wheel
     */
	public DynamicPhysicsNode getPhysicsNode() {
		return node;
	}

    public void resetPosition() {
        node.clearDynamics();
        node.getLocalRotation().loadIdentity();
        node.getLocalTranslation().set( relativePosition );
        suspensionBase.getLocalRotation().multLocal( suspensionBase.getLocalRotation() );
        node.getLocalTranslation().addLocal( suspensionBase.getLocalTranslation() );
        node.getLocalRotation().set( suspensionBase.getLocalRotation() );
    }
}
