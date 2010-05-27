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
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsDebugger;
import com.jmex.physics.contact.ContactInfo;
import com.jmex.physics.geometry.PhysicsBox;
import com.jmex.physics.geometry.PhysicsSphere;
import com.jmex.physics.material.Material;

public class TestNodePick extends TestTriMesh {
    protected DynamicPhysicsNode pickNode;

    @Override
    protected void simpleInitGame() {
        super.simpleInitGame(); // create the trimesh stuff from the other test to keepthis short

        pickNode = getPhysicsSpace().createDynamicNode();
        pickNode.setMaterial( Material.GHOST );
        final PhysicsBox pickBox = pickNode.createBox( "pickBox" );
        final PhysicsSphere pickSphere = pickNode.createSphere( "pickSphere" );
        pickSphere.setLocalTranslation( -3, 0, 0 );
        pickNode.getLocalTranslation().set( 10, 5, 0 );
        pickNode.setAffectedByGravity( false );
        pickNode.setActive( false );
        pickNode.updateGeometricState( 0, true );

        input.addAction( new InputAction() {
            public void performAction( InputActionEvent evt ) {
                ContactInfo info = (ContactInfo) evt.getTriggerData();
                PhysicsCollisionGeometry picked;
                PhysicsCollisionGeometry with;
                if ( info.getNode1() == pickNode ) {
                    picked = info.getGeometry2();
                    with = info.getGeometry1();
                } else {
                    picked = info.getGeometry1();
                    with = info.getGeometry2();
                }
                if ( with == pickBox )
                {
                    Utils.color( picked.getPhysicsNode(), new ColorRGBA( 1, 0, 0, 1 ), 128 );
                }
                else
                {
                    Utils.color( picked.getPhysicsNode(), new ColorRGBA( 0, 1, 0, 1 ), 128 );
                }
                picked.getPhysicsNode().updateRenderState();
                System.out.println( "picked: " + picked + " with " + with );
            }
        }, pickNode.getCollisionEventHandler(), false );

        final InputAction resetAction = new InputAction() {
            public void performAction( InputActionEvent evt ) {
                if ( evt == null || evt.getTriggerPressed() ) {
                    for ( Spatial spatial : rootNode.getChildren() ) {
                        Utils.color( spatial, new ColorRGBA( 1, 1, 1, 1 ), 128 );
                        spatial.updateRenderState();
                    }
                }
            }
        };
        input.addAction( resetAction, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_R, InputHandler.AXIS_NONE, false );
        resetAction.performAction( null );

        Text label = Text.createDefaultTextLabel( "instructions", "Drag the meshes into the red " +
                "physics collision geometries to color them." );
        label.setLocalTranslation( 0, 40, 0 );
        statNode.attachChild( label );

        showPhysics = true;
    }

    @Override
    protected void simpleUpdate() {
        super.simpleUpdate();
        getPhysicsSpace().pick( pickNode );
    }

    @Override
    protected void simpleRender() {
        super.simpleRender();
        if ( showPhysics ) {
            PhysicsDebugger.drawPhysics( pickNode, display.getRenderer() );
        }
    }

    public static void main( String[] args ) {
        Logger.getLogger( "" ).setLevel( Level.WARNING ); // to see the important stuff
        new TestNodePick().start();
    }
}


/*
* $Log: TestNodePick.java,v $
* Revision 1.2  2007/09/22 14:28:36  irrisor
* spongy contacts, improved material handling, center of mass correctly handled now, breakable joints, use odejava 0.3.1
*
* Revision 1.1  2007/09/02 20:44:10  irrisor
* node picking, timer fix
*
*
*/