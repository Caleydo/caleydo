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

import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsDebugger;
import com.jmex.physics.contact.ContactInfo;
import com.jmex.physics.geometry.PhysicsRay;


public class TestRay extends TestTriMesh {
    protected PhysicsRay ray;

    @Override
    protected void simpleInitGame() {
        super.simpleInitGame();

        ray = getPhysicsSpace().createStaticNode().createRay( "ray" );
        ray.getLocalScale().set( 0, 10, 0 );
        ray.getLocalTranslation().set( 10, 5, 0 );
        ray.updateWorldVectors();

        input.addAction( new InputAction() {
            public void performAction( InputActionEvent evt ) {
                ContactInfo info = (ContactInfo) evt.getTriggerData();
                PhysicsCollisionGeometry picked;
                if ( info.getGeometry1() == ray ) {
                    picked = info.getGeometry2();
                } else {
                    picked = info.getGeometry1();
                }
                System.out.println( "picked: " + picked );
            }
        }, ray.getCollisionEventHandler(), false );

        showPhysics = true;
    }

    @Override
    protected void simpleUpdate() {
        super.simpleUpdate();
        getPhysicsSpace().pick( ray );
    }

    @Override
    protected void simpleRender() {
        super.simpleRender();
        if ( showPhysics ) {
            PhysicsDebugger.drawPhysics( ray, display.getRenderer() );
        }
    }

    public static void main( String[] args ) {
        Logger.getLogger( "" ).setLevel( Level.WARNING ); // to see the important stuff
        new TestRay().start();
    }
}

/*
 * $Log: TestRay.java,v $
 * Revision 1.7  2007/09/22 14:28:36  irrisor
 * spongy contacts, improved material handling, center of mass correctly handled now, breakable joints, use odejava 0.3.1
 *
 * Revision 1.6  2007/09/02 20:44:10  irrisor
 * node picking, timer fix
 *
 * Revision 1.5  2007/08/28 12:19:36  irrisor
 * renamed autodisable to autorest, added unrest method, set root logger to warning level instead of physics logger only
 *
 * Revision 1.4  2007/08/03 11:26:46  irrisor
 * adapted to new logging (JUL)
 *
 * Revision 1.3  2007/03/24 09:58:33  irrisor
 * tests corrected
 *
 * Revision 1.2  2006/12/23 22:07:00  irrisor
 * Ray added, Picking interface (natives pending), JOODE implementation added, license header added
 *
 * Revision 1.1  2006/12/15 16:23:24  irrisor
 * Test for PhysicsRay and PhysicsMesh (mesh doesn't work smoothly on linux and mac until natives are rebuilt)
 *
 */

