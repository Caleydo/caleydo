/*
 * Copyright (c) 2005-2006 jME Physics 2
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
package com.jmetest.physicstut;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.math.Vector3f;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.Joint;
import com.jmex.physics.RotationalJointAxis;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.TranslationalJointAxis;
import com.jmex.physics.geometry.PhysicsBox;
import com.jmex.physics.material.Material;
import com.jmex.physics.util.SimplePhysicsGame;

/**
 * Learn about joints. This application shows a simple example with two joints: a swinging sphere tied to the
 * environment and two boxes tied together.
 *
 * @author Irrisor
 */
public class Lesson7 extends SimplePhysicsGame {
    protected void simpleInitGame() {
        // first we will create a floor and sphere like in Lesson4
        StaticPhysicsNode staticNode = getPhysicsSpace().createStaticNode();
        rootNode.attachChild( staticNode );
        PhysicsBox floorBox = staticNode.createBox( "floor" );
        floorBox.getLocalScale().set( 10, 0.5f, 10 );
        DynamicPhysicsNode dynamicSphereNode = getPhysicsSpace().createDynamicNode();
        rootNode.attachChild( dynamicSphereNode );
        dynamicSphereNode.createSphere( "swinging sphere" );
        // but put the sphere a little to the side
        dynamicSphereNode.getLocalTranslation().set( 3.4f, 5, 0 );

        // now we are going to tie the sphere to the environment
        // thus we need a joint
        final Joint jointForSphere = getPhysicsSpace().createJoint();
        // right now that joint does not allow any degree of freedom
        // we want the sphere to act like a pendulum thus we need one rotational degree of freedom:
        final RotationalJointAxis rotationalAxis = jointForSphere.createRotationalAxis();
        // the axis of rotation for the sphere should point into z direction
        rotationalAxis.setDirection( new Vector3f( 0, 0, 1 ) );
        // ok now we attach the joint to our sphere
        jointForSphere.attach( dynamicSphereNode );
        // as we used the attach method with only one parameter the other side of the joint is attached to the world
        // this means it is fixed!
        // the anchor point in the world should be above the floor to let the sphere swing right above it
        jointForSphere.setAnchor( new Vector3f( 0, 5, 0 ) );

        // then create thos two boxes
        DynamicPhysicsNode dynamicBoxNode1 = getPhysicsSpace().createDynamicNode();
        rootNode.attachChild( dynamicBoxNode1 );
        dynamicBoxNode1.createBox( "box1" );
        DynamicPhysicsNode dynamicBoxNode2 = getPhysicsSpace().createDynamicNode();
        rootNode.attachChild( dynamicBoxNode2 );
        dynamicBoxNode2.createBox( "box2" );
        // move the first box above the floor
        dynamicBoxNode1.getLocalTranslation().set( 0, 1, 0 );
        // move the second box a little to the right
        dynamicBoxNode2.getLocalTranslation().set( 0.7f, 1, 0 );
        // additionally the first first box gets more weight
        dynamicBoxNode1.setMass( 5 );

        // these boxes do intersect a little bit this would be problematic without joints but we do join them now
        final Joint jointForBoxes = getPhysicsSpace().createJoint();
        // the boxes shall be able to shift into each other
        // so create one translational degree of free in x direction
        final TranslationalJointAxis translationalAxis = jointForBoxes.createTranslationalAxis();
        translationalAxis.setDirection( new Vector3f( 1, 0, 0 ) );
        // and attach the joint to the two boxes
        jointForBoxes.attach( dynamicBoxNode1, dynamicBoxNode2 );

        // the joint currently can extend up to infinity - as this is quite unnatural we restrict that
        translationalAxis.setPositionMinimum( 0 );
        translationalAxis.setPositionMaximum( 10 );

        // to allow the second box to slide above the floor we make it a little slippery
        dynamicBoxNode2.setMaterial( Material.ICE );

        // ok no visuals here - switch on debug mode
        showPhysics = true;

        // what you will note when starting this program:
        // - joined nodes do not collide with each other
        // - joints themselfes do not have a collision volume and can go through other objects
    }

    /**
     * The main method to allow starting this class as application.
     *
     * @param args command line arguments
     */
    public static void main( String[] args ) {
        Logger.getLogger( "" ).setLevel( Level.WARNING ); // to see the important stuff
        new Lesson7().start();
    }
}

/*
 * $log$
 */

