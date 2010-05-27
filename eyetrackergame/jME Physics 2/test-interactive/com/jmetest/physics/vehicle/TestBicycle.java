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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.input.InputHandler;
import com.jme.input.KeyInput;
import com.jme.input.ChaseCamera;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.math.Vector3f;
import com.jme.scene.Text;
import com.jme.scene.shape.Box;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.Joint;
import com.jmex.physics.JointAxis;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.geometry.PhysicsBox;
import com.jmex.physics.material.Material;
import com.jmex.physics.util.SimplePhysicsGame;

/**
 * Very simple bicycle. Steering behaviour is quite crappy.
 */
public class TestBicycle extends SimplePhysicsGame {
    private ChaseCamera chaseCamera;

    protected void simpleInitGame() {
        createFloor();

        // create chassis
        final DynamicPhysicsNode chassis = getPhysicsSpace().createDynamicNode();
        chassis.createBox( "chassis" );
        chassis.setLocalScale( new Vector3f( 2, 0.8f, 0.1f ) );
        chassis.getLocalTranslation().set( 0, 1, 0 );
        rootNode.attachChild( chassis );

        // create rear tire
        DynamicPhysicsNode tire = getPhysicsSpace().createDynamicNode();
        tire.createCylinder( "tire geom" );
        tire.setMaterial( Material.RUBBER );
        tire.getLocalScale().set( 0.5f, 0.1f, 0.1f );
        tire.getLocalTranslation().set( -1, 0, 0 );
        tire.computeMass();
        tire.setMass( 100 );
        rootNode.attachChild( tire );

        // connect chassis and rear tire, including motor
        Joint joint = getPhysicsSpace().createJoint();
        joint.attach( tire, chassis );
        final JointAxis rotationAxis = joint.createRotationalAxis();
        rotationAxis.setDirection( new Vector3f( 0, 0, 1 ) );
        rotationAxis.setAvailableAcceleration( 2000 );

        input.addAction( new SteerAction( rotationAxis, -500 ), InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_NUMPAD8, InputHandler.AXIS_NONE, false );
        input.addAction( new SteerAction( rotationAxis, 500 ), InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_NUMPAD2, InputHandler.AXIS_NONE, false );

        // create handle
        DynamicPhysicsNode handle = getPhysicsSpace().createDynamicNode();
        PhysicsBox handlePart1 = handle.createBox( "handleG1" );
        handlePart1.getLocalScale().set( 0.1f, 1f, 0.1f  );
        handle.getLocalTranslation().set( 1.1f, 1, 0 );
        PhysicsBox handlePart2 = handle.createBox( "handleG2" );
        handlePart2.getLocalScale().set( 0.1f, 0.1f, 1  );
        handlePart2.getLocalTranslation().set( 0, 1, 0  );

        // join handle and chassis, including steering
        Joint handleJoint = getPhysicsSpace().createJoint();
        handleJoint.attach( handle, chassis );
        final JointAxis handleAxis = handleJoint.createRotationalAxis();
        handleAxis.setDirection( new Vector3f( 0, 1, 0 ) );
        handleAxis.setAvailableAcceleration( 500 );
        
        input.addAction( new SteerAction( handleAxis, 1 ), InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_NUMPAD4, InputHandler.AXIS_NONE, false );
        input.addAction( new SteerAction( handleAxis, -1 ), InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_NUMPAD6, InputHandler.AXIS_NONE, false );

        // create front tire
        DynamicPhysicsNode tire2 = getPhysicsSpace().createDynamicNode();
        tire2.createCylinder( "tire2 geom" );
        tire2.setMaterial( Material.RUBBER );
        tire2.getLocalScale().set( 0.5f, 0.1f, 0.1f );
        tire2.getLocalTranslation().set( 1, 0, 0 );
        tire2.computeMass();
        tire2.setMass( 200 );
        rootNode.attachChild( tire2 );

        // join front tire and handle
        Joint joint2 = getPhysicsSpace().createJoint();
        joint2.attach( tire2, handle );
        final JointAxis rotationAxis2 = joint2.createRotationalAxis();
        rotationAxis2.setDirection( new Vector3f( 0, 0, 1 ) );

        // some visual stuff
        Text label = Text.createDefaultTextLabel( "instructions", "Use Numpad to steer the bicycle." );
        label.setLocalTranslation( 0, 20, 0 );
        statNode.attachChild( label );

        chaseCamera = new ChaseCamera(cam, chassis);
        showPhysics = true;
    }

    private void createFloor() {
        final StaticPhysicsNode staticNode = getPhysicsSpace().createStaticNode();
        staticNode.attachChild( new Box("", new Vector3f(0, -2, 0), 250, 0.1f, 250 ) );
        staticNode.generatePhysicsGeometry();
        staticNode.setMaterial( Material.CONCRETE );
        rootNode.attachChild( staticNode );
    }

    protected void simpleRender() {
        chaseCamera.update( tpf );
    }

    public static void main( String[] args ) {
        Logger.getLogger( "" ).setLevel( Level.WARNING ); // to see the important stuff
        new TestBicycle().start();
    }

    private static class SteerAction extends InputAction {
        private final JointAxis axis1;
        private float velocity;

        public SteerAction( JointAxis axis1, float velocity ) {
            this.axis1 = axis1;
            this.velocity = velocity;
        }

        public void performAction( InputActionEvent evt ) {
            if ( evt.getTriggerPressed() ) {
                axis1.setDesiredVelocity( velocity );
            }
            else {
                axis1.setDesiredVelocity( 0 );
            }
        }
    }
}