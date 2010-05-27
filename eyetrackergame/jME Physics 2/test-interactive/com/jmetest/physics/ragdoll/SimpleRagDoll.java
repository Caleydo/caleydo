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
package com.jmetest.physics.ragdoll;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.shape.Capsule;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.Joint;
import com.jmex.physics.JointAxis;
import com.jmex.physics.PhysicsSpace;

/**
 * Builds a ragdoll with capsules looking like this:
 * <pre>
 *           o          head
 *      ~- - = - -~     lHand, lArmLower, lArmUpper, upperTorso, rArmUpper, rArmLower, rHand
 *           I          midTorso
 *           -          lowerTorso
 *          l l         lLegUpper, rLegUpper
 *          l l         lLegLower, rLegLower
 *          . .         lFoot, rFoot
 * </pre>
 * Use {@link #getRagdollNode()} to obtain a node to attach to your scene.
 * @author mud2005, irrisor
 */
public class SimpleRagDoll {

    private Node ragdollNode;
    private PhysicsSpace physicsSpace;

    public SimpleRagDoll( PhysicsSpace physicsSpace ) {
        this.physicsSpace = physicsSpace;
        ragdollNode = new Node();
        buildRagdoll();
    }

    public void buildRagdoll() {
        DynamicPhysicsNode head = physicsSpace.createDynamicNode();
        ragdollNode.attachChild( head );
        final Capsule headCapsule = new Capsule( "head", 9, 9, 9, .4f, .4f );
        headCapsule.setModelBound( new BoundingSphere() );
        headCapsule.updateModelBound();
        head.attachChild( headCapsule );
        head.generatePhysicsGeometry();
        head.getLocalTranslation().set( 0, .7f, 0 );

        DynamicPhysicsNode upperTorso = createNodeWithCapsule( "torsoUpper", .4f, 1.75f, Vector3f.UNIT_Z, 0, .8f, 0f );
        DynamicPhysicsNode midTorso = createNodeWithCapsule( "torsoMid", .4f, 1.75f, null, 0, -.35f, 0f );
        DynamicPhysicsNode lowerTorso = createNodeWithCapsule( "torsolower", .3f, 1.5f, Vector3f.UNIT_Z, 0, -1.5f, 0f );

        DynamicPhysicsNode lArmUpper = createNodeWithCapsule( "larmupper", .3f, 1.2f, Vector3f.UNIT_Z, -1.8f, .9f, 0 );
        DynamicPhysicsNode lArmLower = createNodeWithCapsule( "larmlower", .3f, 1.2f, Vector3f.UNIT_Z, -3.2f, .9f, 0 );
        DynamicPhysicsNode rArmUpper = createNodeWithCapsule( "rarmupper", .3f, 1.2f, Vector3f.UNIT_Z, 1.8f, .9f, 0 );
        DynamicPhysicsNode rArmLower = createNodeWithCapsule( "rarmlower", .3f, 1.2f, Vector3f.UNIT_Z, 3.2f, .9f, 0 );

        DynamicPhysicsNode lLegUpper = createNodeWithCapsule( "llegupper", .3f, 1.6f, null, -.75f, -2.5f, 0f );
        DynamicPhysicsNode lLegLower = createNodeWithCapsule( "lleglower", .3f, 1.6f, null, -.75f, -4.3f, 0f );
        DynamicPhysicsNode rLegUpper = createNodeWithCapsule( "rlegupper", .3f, 1.6f, null, .75f, -2.5f, 0f );
        DynamicPhysicsNode rLegLower = createNodeWithCapsule( "rleglower", .3f, 1.6f, null, .75f, -4.3f, 0f );

        DynamicPhysicsNode lFoot = createNodeWithCapsule( "lfoot", .3f, .4f, Vector3f.UNIT_X, -.75f, -5.3f, .2f );
        DynamicPhysicsNode rFoot = createNodeWithCapsule( "rfoot", .3f, .4f, Vector3f.UNIT_X, .75f, -5.3f, .2f );

        DynamicPhysicsNode lHand = createNodeWithCapsule( "lhand", .3f, .3f, Vector3f.UNIT_Z, -4.1f, .9f, 0f );
        DynamicPhysicsNode rHand = createNodeWithCapsule( "rhand", .3f, .3f, Vector3f.UNIT_Z, 4.1f, .9f, 0f );

        Joint neckJoint = physicsSpace.createJoint();
        neckJoint.attach( head, upperTorso );
        JointAxis neckJointAxis = neckJoint.createTranslationalAxis();
        neckJointAxis.setDirection( Vector3f.UNIT_Y );
        neckJointAxis.setPositionMinimum( .9f );
        neckJointAxis.setPositionMaximum( 1f );

        join( upperTorso, lArmUpper, new Vector3f( -1f, .2f, 0 ), Vector3f.UNIT_Z, -1.4f, .9f );
        join( upperTorso, rArmUpper, new Vector3f( 1, .2f, 0 ), Vector3f.UNIT_Z, -.9f, 1.4f );

        join( upperTorso, midTorso, new Vector3f( 0, -.5f, 0 ), Vector3f.UNIT_X, 0f, .2f );
        join( midTorso, lowerTorso, new Vector3f( 0, 0 - .7f, 0 ), Vector3f.UNIT_X, 0f, .2f );

        join( rArmUpper, rArmLower, new Vector3f( .8f, 0, 0 ), Vector3f.UNIT_Y, 0f, 2f );
        join( lArmUpper, lArmLower, new Vector3f( -.8f, 0, 0 ), Vector3f.UNIT_Y, -2f, 0f );

        join( lowerTorso, lLegUpper, new Vector3f( -.75f, 0f, 0 ), Vector3f.UNIT_X, 0f, 1.8f );
        join( lowerTorso, rLegUpper, new Vector3f( .75f, 0f, 0f ), Vector3f.UNIT_X, 0f, 1.8f );
        join( lLegUpper, lLegLower, new Vector3f( 0f, -.9f, 0f ), Vector3f.UNIT_X, -2f, 0f );
        join( rLegUpper, rLegLower, new Vector3f( 0f, -.9f, 0f ), Vector3f.UNIT_X, -2f, 0f );

        join( rLegLower, rFoot, new Vector3f( 0f, -1f, 0f ), Vector3f.UNIT_X, 0f, .5f );
        join( lLegLower, lFoot, new Vector3f( 0f, -1f, 0f ), Vector3f.UNIT_X, 0f, .5f );

        join( rArmLower, rHand, new Vector3f( .7f, 0f, 0f ), Vector3f.UNIT_Z, 0f, 1f );
        join( lArmLower, lHand, new Vector3f( -.7f, 0f, 0f ), Vector3f.UNIT_Z, -1f, 0f );
    }

    private DynamicPhysicsNode createNodeWithCapsule( String name, float radius, float height, Vector3f rotate90Axis,
                                                      float x, float y, float z ) {
        DynamicPhysicsNode node = physicsSpace.createDynamicNode();
        final Capsule capsule = new Capsule( name, 9, 9, 9, radius, height );
        capsule.setModelBound( new BoundingBox() );
        capsule.updateModelBound();
        node.attachChild( capsule );
        if ( rotate90Axis != null ) {
            capsule.getLocalRotation().fromAngleAxis( FastMath.PI / 2, rotate90Axis );
        }
        node.generatePhysicsGeometry();
        node.getLocalTranslation().set( x, y, z );
        ragdollNode.attachChild( node );
        return node;
    }

    private void join( DynamicPhysicsNode node1, DynamicPhysicsNode node2, Vector3f anchor, Vector3f direction, float min, float max ) {
        Joint joint = physicsSpace.createJoint();
        joint.attach( node1, node2 );
        joint.setAnchor( anchor );
        JointAxis leftShoulderAxis = joint.createRotationalAxis();
        leftShoulderAxis.setDirection( direction );
        leftShoulderAxis.setPositionMinimum( min );
        leftShoulderAxis.setPositionMaximum( max );
    }

    public Node getRagdollNode() {
        return ragdollNode;
    }

}

/*
 * $Log: SimpleRagDoll.java,v $
 * Revision 1.2  2007/09/22 14:28:38  irrisor
 * spongy contacts, improved material handling, center of mass correctly handled now, breakable joints, use odejava 0.3.1
 *
 * Revision 1.1  2007/09/09 10:25:48  irrisor
 * added ragdoll from mud2005, new interface PhysicsSpatial for physics node and collision geometry, new api PhysicsSpace#collide(PhysicsSpatial, PhysicsSpatial)
 *
 */
