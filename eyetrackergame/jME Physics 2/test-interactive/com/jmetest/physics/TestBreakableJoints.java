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
package com.jmetest.physics;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.bounding.BoundingBox;
import com.jme.input.InputHandler;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.math.Vector3f;
import com.jme.scene.Text;
import com.jme.scene.shape.Box;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.Joint;
import com.jmex.physics.JointAxis;
import com.jmex.physics.util.PhysicsPicker;
import com.jmex.physics.util.SimplePhysicsGame;

/**
 * @author mud2005, Irrisor
 */
public class TestBreakableJoints extends SimplePhysicsGame {


    protected void simpleInitGame() {
        DynamicPhysicsNode lastNode = null;
        final int numberOfBoxes = 10;
        for ( int i = 0; i <= numberOfBoxes; i++ ) {
            getPhysicsSpace().setAccuracy( 0.005f );

            final DynamicPhysicsNode node;
            if ( i < numberOfBoxes ) {
                node = getPhysicsSpace().createDynamicNode();
                Box box1 = new Box( "box", new Vector3f(), 2f, .1f, 4f );
                box1.setModelBound( new BoundingBox() );
                box1.updateModelBound();
                node.getLocalTranslation().set( i * 5, 0, 0 );
                node.attachChild( box1 );
                rootNode.attachChild( node );
                node.generatePhysicsGeometry();
            } else {
                node = null;
            }

            Joint joint = getPhysicsSpace().createJoint();
            joint.setSpring( 2000, 400 );
            joint.setBreakingLinearForce( 11000 );

            if ( lastNode == null ) {
                // attach the first box to the static environment
                joint.attach( node );
            } else if ( node == null ) {
                // attach the last box to the static environment
                joint.attach( lastNode );
                joint.setAnchor( lastNode.getLocalTranslation() );
            } else {
                // connect dynamic nodes with joints
                joint.attach( node, lastNode );
            }

            JointAxis axis = joint.createRotationalAxis();
            axis.setDirection( new Vector3f( 0, 0, 1 ) );

            lastNode = node;
        }

        // drop boxes with SPACE
        input.addAction( new InputAction() {
            public void performAction( InputActionEvent evt ) {
                if ( evt.getTriggerPressed() ) {
                    DynamicPhysicsNode dynamicBoxNode = getPhysicsSpace()
                            .createDynamicNode();
                    Box box = new Box( "", new Vector3f(), 1, 1, 1 );
                    box.setModelBound( new BoundingBox() );
                    box.updateModelBound();
                    dynamicBoxNode.attachChild( box );
                    dynamicBoxNode.getLocalTranslation().y += 6;
                    dynamicBoxNode.getLocalTranslation().x += 22;
                    dynamicBoxNode.generatePhysicsGeometry();
                    rootNode.attachChild( dynamicBoxNode );
                    dynamicBoxNode.setMass( 20 );
                }
            }
        }, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_SPACE, InputHandler.AXIS_NONE, false );

        Text label = Text.createDefaultTextLabel( "instructions",
                "[space] to drop box, Left mouse button to hold/drag stuff, "
                        + "[v] to toggle physics view." );
        label.setLocalTranslation( 0, 20, 0 );
        statNode.attachChild( label );

        showPhysics = true;
        cameraInputHandler.setEnabled( false );
        cam.getLocation().x += numberOfBoxes*2;
        new PhysicsPicker( input, rootNode, getPhysicsSpace() );
        MouseInput.get().setCursorVisible( true );
    }

    public static void main( String[] args ) {
        Logger.getLogger( "" ).setLevel( Level.WARNING ); // to see the important stuff
        TestBreakableJoints app = new TestBreakableJoints();
//        app.setDialogBehaviour( AbstractGame.ALWAYS_SHOW_PROPS_DIALOG );
        app.start();
    }
}

/*
* $log$
*/