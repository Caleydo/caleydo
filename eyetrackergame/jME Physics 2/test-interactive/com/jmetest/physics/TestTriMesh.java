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
import com.jme.scene.Text;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Sphere;
import com.jme.scene.shape.Torus;
import com.jme.scene.state.CullState;
import com.jme.scene.state.CullState.Face;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.geometry.PhysicsMesh;
import com.jmex.physics.geometry.PhysicsSphere;
import com.jmex.physics.util.PhysicsPicker;
import com.jmex.physics.util.SimplePhysicsGame;


/**
 * @author Irrisor
 */
public class TestTriMesh extends SimplePhysicsGame {
    protected StaticPhysicsNode staticNode;

    protected void simpleInitGame() {

        staticNode = getPhysicsSpace().createStaticNode();
        TriMesh trimesh = new Box( "trimesh", new Vector3f(), 15, 0.5f, 15 );
        trimesh.setModelBound( new BoundingBox() );
        trimesh.updateModelBound();
//        PhysicsMesh mesh = staticNode.createMesh( "mesh" );
//        mesh.copyFrom( trimesh );
        staticNode.attachChild( trimesh );
        staticNode.generatePhysicsGeometry( false );

        staticNode.getLocalTranslation().set( 0, -5, 0 );
        rootNode.attachChild( staticNode );

        final DynamicPhysicsNode dynamicNode1 = getPhysicsSpace().createDynamicNode();
        TriMesh mesh1 = new Sphere( "meshsphere", 10, 10, 2 );
        mesh1.setModelBound( new BoundingSphere() );
        mesh1.updateModelBound();
        PhysicsMesh sphere = dynamicNode1.createMesh( "sphere mesh" );
        sphere.getLocalTranslation().set( -1, 0, 0 );
        mesh1.getLocalTranslation().set( -1, 0, 0 );
        sphere.copyFrom( mesh1 );
        dynamicNode1.attachChild( mesh1 );
        final PhysicsSphere sphere2 = dynamicNode1.createSphere( "sphere physics" );
        sphere2.getLocalTranslation().set( 0.3f, 0, 0 );
        dynamicNode1.detachChild( sphere2 );
        rootNode.attachChild( dynamicNode1 );
        dynamicNode1.computeMass();

        final DynamicPhysicsNode dynamicNode2 = getPhysicsSpace().createDynamicNode();
        TriMesh mesh2 = new Torus( "torus", 15, 10, 1, 4 );
        mesh2.setModelBound( new BoundingSphere() );
        mesh2.updateModelBound();
        PhysicsMesh physicsMesh2 = dynamicNode2.createMesh( "torus phyics geometry" );
        physicsMesh2.copyFrom( mesh2 );
        dynamicNode2.attachChild( mesh2 );
        CullState cs = display.getRenderer().createCullState();
        cs.setCullFace(Face.Back);
        mesh2.setRenderState( cs );

        rootNode.attachChild( dynamicNode2 );
        dynamicNode2.computeMass();

        final InputAction resetAction = new InputAction() {
            public void performAction( InputActionEvent evt ) {
                if ( evt == null || evt.getTriggerPressed() ) {
                    dynamicNode1.getLocalTranslation().set( 0, 3, 0 );
                    dynamicNode1.getLocalRotation().set( 0, 0, 0, 1 );
                    dynamicNode1.clearDynamics();

                    dynamicNode2.getLocalTranslation().set( 0, 5f, 0 );
                    dynamicNode2.getLocalRotation().fromAngleNormalAxis( FastMath.PI/2 - 0.2f, new Vector3f( 1, 0, 0 ) );
                    dynamicNode2.clearDynamics();
                }
            }
        };
        input.addAction( resetAction, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_R, InputHandler.AXIS_NONE, false );
        resetAction.performAction( null );

        InputAction detachAction = new InputAction() {
            public void performAction( InputActionEvent evt ) {
                if ( sphere2.getParent() != null ) {
                    dynamicNode1.detachChild( sphere2 );
                } else {
                    dynamicNode1.attachChild( sphere2 );
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

        cameraInputHandler.setEnabled( false );
        new PhysicsPicker( input, rootNode, getPhysicsSpace() );
        MouseInput.get().setCursorVisible( true );

        Text label = Text.createDefaultTextLabel( "instructions", "[r] to reset. Hold [ins] to attach second sphere." );
        label.setLocalTranslation( 0, 20, 0 );
        statNode.attachChild( label );
    }

    public static void main( String[] args ) {
        Logger.getLogger( "" ).setLevel( Level.WARNING ); // to see the important stuff
        new TestTriMesh().start();
    }
}

/*
 * $Log: TestTriMesh.java,v $
 * Revision 1.7  2007/09/23 12:44:14  irrisor
 * updates for odejava-jni 0.3.1, lin+osx natives pending again
 *
 * Revision 1.6  2007/09/22 14:28:36  irrisor
 * spongy contacts, improved material handling, center of mass correctly handled now, breakable joints, use odejava 0.3.1
 *
 * Revision 1.5  2007/08/29 07:26:21  irrisor
 * work on pick, missing files from last commit
 *
 * Revision 1.4  2007/08/28 12:19:37  irrisor
 * renamed autodisable to autorest, added unrest method, set root logger to warning level instead of physics logger only
 *
 * Revision 1.3  2007/08/03 11:26:46  irrisor
 * adapted to new logging (JUL)
 *
 * Revision 1.2  2006/12/23 22:07:00  irrisor
 * Ray added, Picking interface (natives pending), JOODE implementation added, license header added
 *
 * Revision 1.1  2006/12/15 16:23:24  irrisor
 * Test for PhysicsRay and PhysicsMesh (mesh doesn't work smoothly on linux and mac until natives are rebuilt)
 *
 */

