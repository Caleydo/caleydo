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
import com.jme.input.MouseInput;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.math.Vector3f;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.contact.ContactInfo;
import com.jmex.physics.geometry.PhysicsSphere;
import com.jmex.physics.util.SimplePhysicsGame;

/**
 * @author Irrisor
 */
public class TestBasicRepresentation extends SimplePhysicsGame {
    protected void simpleInitGame() {
//        rootNode.getLocalRotation().fromAngleNormalAxis( -0.1f, new Vector3f( 0, 0, 1 ) );

        final StaticPhysicsNode staticNode = getPhysicsSpace().createStaticNode();
        final PhysicsCollisionGeometry floorGeom = staticNode.createBox( "box physics" );
        staticNode.setLocalScale( new Vector3f( 30, 1, 30 ) );

        staticNode.getLocalTranslation().set( 0, -5, 0 );
//        staticNode.getLocalScale().multLocal( 1.2f );
        rootNode.attachChild( staticNode );

        final DynamicPhysicsNode dynamicNode = getPhysicsSpace().createDynamicNode();

        PhysicsSphere sphere = dynamicNode.createSphere( "sphere physics" );
        sphere.setLocalScale( 2 );
        sphere.getLocalTranslation().set( -1, 0, 0 );

        final PhysicsSphere sphere2 = dynamicNode.createSphere( "sphere physics" );
        sphere2.getLocalTranslation().set( 0.3f, 0, 0 );
        dynamicNode.detachChild( sphere2 );
//        dynamicNode.setLocalScale( 0.8f );
        rootNode.attachChild( dynamicNode );
        dynamicNode.computeMass();
//        dynamicNode.setCenterOfMass( new Vector3f( -1, 0, 0 ) );
//        TODO: test this: dynamicNode.setMaterial( Material.GHOST );

        final DynamicPhysicsNode dynamicNode3 = getPhysicsSpace().createDynamicNode();
        PhysicsSphere sphere3 = dynamicNode3.createSphere( "sphere physics" );
        sphere3.setLocalScale( 2 );

        rootNode.attachChild( dynamicNode3 );
        dynamicNode3.computeMass();

        final DynamicPhysicsNode cylinderNode = getPhysicsSpace().createDynamicNode();
        cylinderNode.createCylinder( "cylinder" );
        rootNode.attachChild( cylinderNode );
        cylinderNode.setLocalScale( 2 );

        showPhysics = true;

        final InputAction resetAction = new InputAction() {
            public void performAction( InputActionEvent evt ) {
                if ( evt == null || evt.getTriggerPressed() ) {
                    dynamicNode.getLocalTranslation().set( 0, 3, 0 );
                    dynamicNode.getLocalRotation().set( 0, 0, 0, 1 );
                    dynamicNode.clearDynamics();

                    dynamicNode3.getLocalTranslation().set( 0, -2.5f, 0 );
                    dynamicNode3.getLocalRotation().set( 0, 0, 0, 1 );
                    dynamicNode3.clearDynamics();

                    cylinderNode.getLocalTranslation().set( 2, 2, 4 );
                    cylinderNode.getLocalRotation().fromAngleNormalAxis( 0.5f, new Vector3f( 1, 0, 0 ) );
                    cylinderNode.clearDynamics();
                }
            }
        };
        input.addAction( resetAction, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_R, InputHandler.AXIS_NONE, false );
        resetAction.performAction( null );

        InputAction detachAction = new InputAction() {
            public void performAction( InputActionEvent evt ) {
                if ( sphere2.getParent() != null ) {
                    dynamicNode.detachChild( sphere2 );
                } else {
                    dynamicNode.attachChild( sphere2 );
                }
            }
        };
        input.addAction( detachAction, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_INSERT, InputHandler.AXIS_NONE, false );

        InputAction removeAction = new InputAction() {
            public void performAction( InputActionEvent evt ) {
                staticNode.setActive( !staticNode.isActive() );
            }
        };
        input.addAction( removeAction, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_DELETE, InputHandler.AXIS_NONE, false );

        input.addAction( new InputAction() {
            public void performAction( InputActionEvent evt ) {
                ContactInfo info = (ContactInfo) evt.getTriggerData();
                if ( info.getGeometry1() != floorGeom && info.getGeometry2() != floorGeom ) {
                    System.out.println( evt.getTriggerData() );
                }
            }
        }, getPhysicsSpace().getCollisionEventHandler(), false );

        cameraInputHandler.setEnabled( false );
        MouseInput.get().setCursorVisible( true );
    }

    public static void main( String[] args ) {
        Logger.getLogger( "" ).setLevel( Level.WARNING ); // to see the important stuff
        new TestBasicRepresentation().start();
    }
}

/*
 * $log$
 */

