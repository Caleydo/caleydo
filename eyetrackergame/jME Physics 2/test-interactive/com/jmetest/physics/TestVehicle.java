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
import com.jme.scene.Text;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.Joint;
import com.jmex.physics.JointAxis;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.material.Material;
import com.jmex.physics.util.SimplePhysicsGame;

/**
 * This test shows a very basic method to create a physical vehicle.
 * More advanced techniques can be found in {@link com.jmetest.physics.vehicle.TestAdvancedVehicle}.
 * @author Irrisor
 */
public class TestVehicle extends SimplePhysicsGame {
    protected void simpleInitGame() {
        createFloor();

        final DynamicPhysicsNode chassis = getPhysicsSpace().createDynamicNode();
        chassis.createBox( "chassis" );
        chassis.setLocalScale( new Vector3f( 1, 0.1f, 1 ) );
        rootNode.attachChild( chassis );

        for ( int i = 0; i < 4; i++ ) {
            DynamicPhysicsNode tire = getPhysicsSpace().createDynamicNode();
            tire.createCapsule( "tire geom" );
            tire.setMaterial( Material.RUBBER );
            tire.setLocalScale( 0.3f );
            tire.getLocalTranslation().set( ( 0.5f - ( i & 1 ) ), 0, ( 1 - ( i & 2 ) ) * 0.5f );
            tire.computeMass();
            tire.setMass( 100 );
            rootNode.attachChild( tire );

            Joint joint = getPhysicsSpace().createJoint();
            joint.attach( chassis, tire );
            joint.setAnchor( tire.getLocalTranslation() );
            final JointAxis axis1 = joint.createRotationalAxis();
            axis1.setDirection( new Vector3f( 0, 1, 0 ) );
            axis1.setPositionMinimum( -0.5f );
            axis1.setPositionMaximum( 0.5f );
            axis1.setAvailableAcceleration( 100 );
            axis1.setDesiredVelocity( 0 );
            final JointAxis axis2 = joint.createRotationalAxis();
            axis2.setDirection( new Vector3f( 0, 0, 1 ) );
            axis2.setAvailableAcceleration( 100 );
            axis2.setRelativeToSecondObject( true );

            if ( ( i & 1 ) == 0 ) {
                input.addAction( new SteerAction( axis1, 10 ), InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_NUMPAD6, InputHandler.AXIS_NONE, false );
                input.addAction( new SteerAction( axis1, -10 ), InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_NUMPAD4, InputHandler.AXIS_NONE, false );
            }
            input.addAction( new SteerAction( axis2, 5 ), InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_NUMPAD8, InputHandler.AXIS_NONE, false );
            input.addAction( new SteerAction( axis2, -5 ), InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_NUMPAD2, InputHandler.AXIS_NONE, false );
        }

        showPhysics = true;
        pause = true;

        Text label = Text.createDefaultTextLabel( "instructions", "Press P to release pause. Use Numpad to steer the vehicle." );
        label.setLocalTranslation( 0, 20, 0 );
        statNode.attachChild( label );
    }

    private void createFloor() {
        final StaticPhysicsNode staticNode = getPhysicsSpace().createStaticNode();
        staticNode.createBox( "box physics" );
        staticNode.setLocalScale( new Vector3f( 10, 0.2f, 10 ) );
        staticNode.getLocalTranslation().set( 0, -2, 0 );
        staticNode.getLocalScale().multLocal( 1.2f );
        staticNode.setMaterial( Material.CONCRETE );
        rootNode.attachChild( staticNode );
    }

    public static void main( String[] args ) {
        Logger.getLogger( "" ).setLevel( Level.WARNING ); // to see the important stuff
        new TestVehicle().start();
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

/*
 * $log$
 */

