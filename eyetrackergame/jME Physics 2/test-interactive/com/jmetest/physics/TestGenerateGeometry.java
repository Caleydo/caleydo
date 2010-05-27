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

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.input.InputHandler;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Text;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Sphere;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.util.PhysicsPicker;
import com.jmex.physics.util.SimplePhysicsGame;

/**
 * @author Irrisor
 */
public class TestGenerateGeometry extends SimplePhysicsGame {

    protected void simpleInitGame() {
//        rootNode.getLocalRotation().fromAngleNormalAxis( -0.1f, new Vector3f( 0, 0, 1 ) );

        final StaticPhysicsNode staticNode = getPhysicsSpace().createStaticNode();
        staticNode.setName( "floor ");

        TriMesh trimesh = new Box( "trimesh", new Vector3f(), 15, 0.5f, 15 );
        staticNode.attachChild( trimesh );
        trimesh.setModelBound( new BoundingBox() );
        trimesh.updateModelBound();

        staticNode.getLocalTranslation().set( 0, -5, 0 );

        rootNode.attachChild( staticNode );
        staticNode.generatePhysicsGeometry();

        final DynamicPhysicsNode dynamicNode = getPhysicsSpace().createDynamicNode();
        dynamicNode.setName( "shpere(s)" );

        Sphere meshSphere = new Sphere( "meshsphere", 9, 9, 2 );
        meshSphere.getLocalTranslation().set( -1, 0, 0 );
        meshSphere.setModelBound( new BoundingSphere() );
        meshSphere.updateModelBound();
        dynamicNode.attachChild( meshSphere );

        Node sphere2Node = new Node( "2" );
        sphere2Node.getLocalTranslation().set( 0.25f, 0, 0 );
        sphere2Node.getLocalRotation().fromAngleNormalAxis( -FastMath.PI / 2, new Vector3f( 0, 1, 0 ) );
        Sphere meshSphere2 = new Sphere( "meshsphere2", 9, 9, 1 );
        meshSphere2.getLocalTranslation().set( 0.5f, 0, 0 );
        meshSphere2.setModelBound( new BoundingSphere() );
        meshSphere2.updateModelBound();
        sphere2Node.attachChild( meshSphere2 );
        dynamicNode.attachChild( sphere2Node );

        dynamicNode.generatePhysicsGeometry();

        rootNode.attachChild( dynamicNode );
        dynamicNode.computeMass();

        final DynamicPhysicsNode dynamicNode3 = getPhysicsSpace().createDynamicNode();
        dynamicNode3.setName( "box" );

        Box meshBox3 = new Box( "meshbox3", new Vector3f(), 2, 2, 2 );
        meshBox3.setModelBound( new BoundingBox() );
        meshBox3.updateModelBound();
        dynamicNode3.attachChild( meshBox3 );
        dynamicNode3.generatePhysicsGeometry();

        rootNode.attachChild( dynamicNode3 );
        dynamicNode3.computeMass();

        showPhysics = true;

        final InputAction resetAction = new InputAction() {
            public void performAction( InputActionEvent evt ) {
                dynamicNode.getLocalTranslation().set( 0, 5, 0 );
                dynamicNode.getLocalRotation().set( 0, 0, 0, 1 );
                dynamicNode.clearDynamics();

                dynamicNode3.getLocalTranslation().set( 0, -2.5f, 0 );
                dynamicNode3.getLocalRotation().set( 0, 0, 0, 1 );
                dynamicNode3.clearDynamics();
            }
        };
        input.addAction( resetAction, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_R, InputHandler.AXIS_NONE, false );
        resetAction.performAction( null );

        InputAction removeAction = new InputAction() {
            public void performAction( InputActionEvent evt ) {
                staticNode.setActive( !staticNode.isActive() );
            }
        };
        input.addAction( removeAction, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_DELETE, InputHandler.AXIS_NONE, false );

        MouseInput.get().setCursorVisible( true );
        new PhysicsPicker( input, rootNode, getPhysicsSpace() );

        Text label = Text.createDefaultTextLabel( "instructions", "Left mouse button to hold/drag stuff, " +
                "V to toggle physics view." );
        label.setLocalTranslation( 0, 20, 0 );
        statNode.attachChild( label );
    }

    @Override
    protected void simpleUpdate() {
        cameraInputHandler.setEnabled( MouseInput.get().isButtonDown( 1 ) );
    }

    public static void main( String[] args ) {
        Logger.getLogger( "" ).setLevel( Level.WARNING ); // to see the important stuff
        new TestGenerateGeometry().start();
    }
}

/*
 * $log$
 */

