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
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.util.PhysicsPicker;
import com.jmex.physics.util.SimplePhysicsGame;

/**
 * A short test for the {@link SimpleRagDoll} class.
 * @author mud2005, irrisor
 */
public class TestSimpleRagDoll extends SimplePhysicsGame {

    protected void simpleInitGame() {
        addFloor();
        configurePhysicsPicker();
        adjustCameraView();
        registerCreateRagDollAction();

        Text label2 = Text.createDefaultTextLabel( "instructions", "Hit [space] to drop a ragdoll. [v] to toggle physics view." );
        label2.setLocalTranslation( 0, 20, 0 );
        statNode.attachChild( label2 );
    }

    private void addFloor() {
        StaticPhysicsNode staticNode = getPhysicsSpace().createStaticNode();

        Box floorBox = new Box( "", new Vector3f(), 100, 1, 100 );
        floorBox.setModelBound( new BoundingBox() );
        floorBox.updateModelBound();
        staticNode.attachChild( floorBox );

        rootNode.attachChild( staticNode );

        staticNode.getLocalTranslation().set( 0, -10, 0 );
        staticNode.updateGeometricState( 0, false );

        staticNode.generatePhysicsGeometry();
    }

    private void configurePhysicsPicker() {
        cameraInputHandler.setEnabled( false );
        new PhysicsPicker( input, rootNode, getPhysicsSpace(), true );
        MouseInput.get().setCursorVisible( true );
    }

    private void adjustCameraView() {
        cam.getLocation().y += 10;
        cam.getLocation().z += 10;
        cam.lookAt( new Vector3f(), new Vector3f( 0, 0, -1 ) );
    }

    private void registerCreateRagDollAction() {
        InputAction createAction = new InputAction() {
            public void performAction( InputActionEvent evt ) {
                if ( evt == null || evt.getTriggerPressed() ) {
                    SimpleRagDoll sr2 = new SimpleRagDoll( getPhysicsSpace() );
                    sr2.getRagdollNode().getLocalTranslation().y += 20;
                    rootNode.attachChild( sr2.getRagdollNode() );
                    rootNode.updateGeometricState( 0, false );
                    rootNode.updateRenderState();
                }
            }
        };
        input.addAction( createAction, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_SPACE, InputHandler.AXIS_NONE, false );
        createAction.performAction( null );
    }

    public static void main( String[] args ) {
        Logger.getLogger( "" ).setLevel( Level.WARNING );
        final TestSimpleRagDoll app = new TestSimpleRagDoll();
//        app.setDialogBehaviour( AbstractGame.ALWAYS_SHOW_PROPS_DIALOG );
        new Thread() {
            @Override
            public void run() {
                app.start();
            }
        }.start();
    }
}

/*
 * $Log: TestSimpleRagDoll.java,v $
 * Revision 1.2  2007/09/22 14:28:38  irrisor
 * spongy contacts, improved material handling, center of mass correctly handled now, breakable joints, use odejava 0.3.1
 *
 * Revision 1.1  2007/09/09 10:25:48  irrisor
 * added ragdoll from mud2005, new interface PhysicsSpatial for physics node and collision geometry, new api PhysicsSpace#collide(PhysicsSpatial, PhysicsSpatial)
 *
 */

