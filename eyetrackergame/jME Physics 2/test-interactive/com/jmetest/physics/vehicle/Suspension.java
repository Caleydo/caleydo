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
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.TranslationalJointAxis;
import com.jmex.physics.contact.ContactCallback;
import com.jmex.physics.contact.PendingContact;
import com.jmex.physics.material.Material;

/**
 * @author Erick B Passos
 */
public class Suspension extends Node {

    private static final Vector3f DAMPER_AXIS = new Vector3f( 0, 1, 0 );

    private static final long serialVersionUID = 1L;

    private Wheel leftWheel, rightWheel;

    private DynamicPhysicsNode leftBase, rightBase, chassis;
    private Vector3f offset;

    public Suspension( final PhysicsSpace pSpace, final DynamicPhysicsNode chassis, Vector3f offset, boolean steer ) {
        super( "suspension" );
        this.chassis = chassis;
        this.offset = offset;
        createSuspensionBases( pSpace );
        createWheels( steer );
        createContactCallbacks( pSpace );
    }

    /**
     * Creates the contact callbacks that avoid the wheels colliding with the chassis
     *
     * @param space physics space
     */
    private void createContactCallbacks( PhysicsSpace space ) {
        space.getContactCallbacks().add( new CollisionAvoider( chassis, leftWheel.getPhysicsNode() ) );
        space.getContactCallbacks().add( new CollisionAvoider( chassis, rightWheel.getPhysicsNode() ) );
    }

    /**
     * Creates both left and right wheels.
     * Each wheel is a Wheel node and has it's attached to its own suspension base with a joint.
     *
     * @param steer if these wheels should have the steering rotational axis
     */
    private void createWheels( final boolean steer ) {
        leftWheel = new Wheel( leftBase, CarData.WHEEL_Z_OFFSET, steer );
        this.attachChild( leftWheel );
        rightWheel = new Wheel( rightBase, -CarData.WHEEL_Z_OFFSET, steer );
        this.attachChild( rightWheel );
    }

    /**
     * Creates two suspension bases as bridges between wheels and the chassis
     *
     * @param pSpace physics space
     */
    private void createSuspensionBases( final PhysicsSpace pSpace ) {
        leftBase = createBase( pSpace, offset );
        createSuspensionJoint( pSpace, leftBase );
        rightBase = createBase( pSpace, offset.subtract( 0, 0, offset.z * 2 ) );
        createSuspensionJoint( pSpace, rightBase );
    }

    /**
     * Suspension bases are GHOST physics node that serve only as a bridge between wheels and the chassis.
     * They are necessary because we need one translational axis for the suspensions and two rotational axis
     * for the wheels and it's not possible to put these in only one joint.
     *
     * @param pSpace physics space
     * @param relativePosition from the chassis center
     * @return new node representing the suspension base
     */
    private DynamicPhysicsNode createBase( final PhysicsSpace pSpace, final Vector3f relativePosition ) {
        DynamicPhysicsNode suspensionBase = pSpace.createDynamicNode();
        suspensionBase.setName( "suspensionBase" );
        suspensionBase.setLocalTranslation( chassis.getLocalTranslation().add( relativePosition ) );
        suspensionBase.createBox( "baseBox" );
        suspensionBase.setLocalScale( 0.1f );
        suspensionBase.setMass( CarData.SUSPENSION_MASS );
        suspensionBase.setMaterial( Material.GHOST );
        this.attachChild( suspensionBase );

        return suspensionBase;
    }

    /**
     * Creates the joint between the chassis and a suspension base.
     * Also creates the translational axis to represent the spring and damper for that particular suspension base.
     *
     * @param pSpace physics space
     * @param suspensionBase node between chassis and wheel
     * @return joint for the suspension
     */
    private Joint createSuspensionJoint( final PhysicsSpace pSpace, DynamicPhysicsNode suspensionBase ) {
        Joint suspensionJoint = pSpace.createJoint();
        suspensionJoint.attach( suspensionBase, chassis );

        TranslationalJointAxis springAndDamper = suspensionJoint.createTranslationalAxis();
        springAndDamper.setPositionMaximum( CarData.SUSPENSION_COURSE / 2 );
        springAndDamper.setPositionMinimum( -CarData.SUSPENSION_COURSE / 2 );
        springAndDamper.setAvailableAcceleration( CarData.SUSPENSION_STIFFNESS );
        springAndDamper.setDesiredVelocity( -CarData.SUSPENSION_RESISTANCE );
        springAndDamper.setDirection( DAMPER_AXIS );
        return suspensionJoint;
    }

    /**
     * Accelerates both wheels in the desired direction.
     *
     * @param direction 1 for ahead and -1 for backwards
     */
    public void accelerate( final int direction ) {
        leftWheel.accelerate( direction );
        rightWheel.accelerate( direction );
    }

    /**
     * Releases both wheels.
     */
    public void releaseAccel() {
        leftWheel.releaseAccel();
        rightWheel.releaseAccel();
    }

    /**
     * Steers both wheels in the desired direction.
     *
     * @param direction 1 for right and -1 for left
     */
    public void steer( final float direction ) {
        leftWheel.steer( direction );
        rightWheel.steer( direction );
    }

    /**
     * Releases the steering of the wheels.
     */
    public void unsteer() {
        leftWheel.unsteer();
        rightWheel.unsteer();
    }

    void resetPosition() {
        leftBase.clearDynamics();
        leftBase.getLocalRotation().loadIdentity();
        leftBase.getLocalTranslation().set( offset );
        chassis.getLocalRotation().multLocal( leftBase.getLocalTranslation() );
        leftBase.getLocalRotation().set( chassis.getLocalRotation() );
        leftBase.getLocalTranslation().addLocal( chassis.getLocalTranslation() );
        rightBase.clearDynamics();
        rightBase.getLocalRotation().loadIdentity();
        rightBase.getLocalTranslation().set( offset ).z *= -1;
        chassis.getLocalRotation().multLocal( rightBase.getLocalTranslation() );
        rightBase.getLocalRotation().set( chassis.getLocalRotation() );
        rightBase.getLocalTranslation().addLocal( chassis.getLocalTranslation() );
        leftWheel.resetPosition();
        rightWheel.resetPosition();
    }

    /**
     * ContactCallback that avoid a collision between two nodes.
     * Here it's used to avoid collision between a car chassis and it's own wheels,
     * necessary for really low rides.
     * Straightforward implementation.
     */
    private class CollisionAvoider implements ContactCallback {

        DynamicPhysicsNode node1, node2;

        public CollisionAvoider( DynamicPhysicsNode node1, DynamicPhysicsNode node2 ) {
            super();
            this.node1 = node1;
            this.node2 = node2;
        }

        public boolean adjustContact( PendingContact contact ) {
            boolean isNode2 = contact.getNode1() == node2 || contact.getNode2() == node2;
            boolean isNode1 = contact.getNode1() == node1 || contact.getNode2() == node1;
			if (isNode2 && isNode1){
				contact.setIgnored(true);
				return true;
			}
			return false;
		}	
	}
	
}
