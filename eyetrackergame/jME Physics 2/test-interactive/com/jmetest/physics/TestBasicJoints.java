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
package com.jmetest.physics;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.input.InputHandler;
import com.jme.input.KeyInput;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.math.Vector3f;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.Joint;
import com.jmex.physics.JointAxis;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.geometry.PhysicsSphere;
import com.jmex.physics.util.SimplePhysicsGame;

/**
 * @author Irrisor
 */
public class TestBasicJoints extends SimplePhysicsGame {
    protected void simpleInitGame() {
        rootNode.getLocalRotation().fromAngleNormalAxis( -0.1f, new Vector3f( 0, 0, 1 ) );

        final StaticPhysicsNode staticNode = getPhysicsSpace().createStaticNode();
        staticNode.createBox( "box physics" );
        staticNode.setLocalScale( new Vector3f( 10, 0.2f, 10 ) );
        staticNode.getLocalTranslation().set( 0, -5, 0 );
        staticNode.getLocalScale().multLocal( 1.2f );
        rootNode.attachChild( staticNode );

        final DynamicPhysicsNode dynamicNode1 = getPhysicsSpace().createDynamicNode();
        PhysicsSphere sphere = dynamicNode1.createSphere( "sphere physics" );
        sphere.setLocalScale( 2 );
        sphere.getLocalTranslation().set( -1, 0, 0 );
        dynamicNode1.setLocalScale( 0.8f );
        rootNode.attachChild( dynamicNode1 );
        dynamicNode1.computeMass();

        final DynamicPhysicsNode dynamicNode2 = getPhysicsSpace().createDynamicNode();
        final PhysicsSphere sphere2 = dynamicNode2.createSphere( "sphere physics" );
        sphere2.getLocalTranslation().set( 0.3f, 0, 0 );
        rootNode.attachChild( dynamicNode2 );
        dynamicNode2.computeMass();
//        dynamicNode1.setCenterOfMass( new Vector3f( -1, 0, 0 ) );

        showPhysics = true;

        final InputAction resetAction = new InputAction() {
            public void performAction( InputActionEvent evt ) {
                dynamicNode1.getLocalTranslation().set( 0, 3, 0 );
                dynamicNode1.getLocalRotation().set( 0, 0, 0, 1 );
                dynamicNode1.clearDynamics();
                dynamicNode2.getLocalTranslation().set( 0, 0, 0 );
                dynamicNode2.getLocalRotation().set( 0, 0, 0, 1 );
                dynamicNode2.clearDynamics();

                dynamicNode1.getLocalRotation().fromAngleNormalAxis( 1, new Vector3f( 0, 0, 1 ) );
            }
        };
        input.addAction( resetAction, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_R, InputHandler.AXIS_NONE, false );
        resetAction.performAction( null );
        dynamicNode1.getLocalRotation().set( 0, 0, 0, 1 );

        InputAction detachAction = new InputAction() {
            public void performAction( InputActionEvent evt ) {
                if ( sphere2.getParent() != null ) {
                    dynamicNode1.detachChild( sphere2 );
                }
                else {
                    dynamicNode1.attachChild( sphere2 );
                }
            }
        };
        input.addAction( detachAction, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_INSERT, InputHandler.AXIS_NONE, false );

        final Joint joint = getPhysicsSpace().createJoint();
        joint.attach( dynamicNode1 );
        JointAxis axis = joint.createTranslationalAxis();
        axis.setDirection( new Vector3f( 1, 1, 0 ) );

        Joint joint2 = getPhysicsSpace().createJoint();
        joint2.attach( dynamicNode1, dynamicNode2 );
        JointAxis axis2 = joint2.createTranslationalAxis();
        axis2.setDirection( new Vector3f( 0, 1, 0 ) );
        joint2.setCollisionEnabled( true );

        InputAction removeAction = new InputAction() {
            public void performAction( InputActionEvent evt ) {
                if ( evt.getTriggerPressed() ) {
                    staticNode.setActive( !staticNode.isActive() );
                }
            }
        };
        input.addAction( removeAction, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_DELETE, InputHandler.AXIS_NONE, false );
    }

    public static void main( String[] args ) {
        Logger.getLogger( "" ).setLevel( Level.WARNING ); // to see the important stuff
        new TestBasicJoints().start();
    }
}

/*
 * $log$
 */

