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
package com.jmetest.physicstut;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.input.util.SyntheticButton;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.contact.ContactInfo;
import com.jmex.physics.geometry.PhysicsBox;

/**
 * Learn how to use a physics collision as event in an Application.
 * <p/>
 * This class extends Lesson5, meaning it does everything that's done in Lesson5 plus the new stuff defined here.
 *
 * @author Irrisor
 */
public class Lesson6 extends Lesson5 {
    private StaticPhysicsNode lowerFloor;

    @Override
    protected void simpleInitGame() {
        // create all the stuff and behaviour from Lesson5
        super.simpleInitGame();

        // now create an additional floor below the existing one
        lowerFloor = getPhysicsSpace().createStaticNode();
        rootNode.attachChild( lowerFloor );
        PhysicsBox floorBox = lowerFloor.createBox( "floor" );
        floorBox.getLocalScale().set( 50, 0.5f, 50 );
        lowerFloor.getLocalTranslation().set( 0, -10, 0 );

        // ok now we are interested in collision events with the lower floor
        // jME Physics 2 uses SyntheticButtons to allow application to listen to such events
        // lets obtain such a button for our lower floor
        final SyntheticButton collisionEventHandler = lowerFloor.getCollisionEventHandler();
        // we can subscribe for such an event with an input handler of our choice now
        input.addAction( new MyCollisionAction(), collisionEventHandler, false );
    }

    /**
     * The main method to allow starting this class as application.
     *
     * @param args command line arguments
     */
    public static void main( String[] args ) {
        Logger.getLogger( "" ).setLevel( Level.WARNING ); // to see the important stuff
        new Lesson6().start();
    }

    /**
     * This action was registered above to be invoked upon collision with the lower floor.
     */
    private class MyCollisionAction extends InputAction {
        public void performAction( InputActionEvent evt ) {
            // something collided with th lower floor
            // we want to put everything that collides with the lower floor up again

            // as we know this action is handling collision we can cast the data to ContactInfo
            final ContactInfo contactInfo = ( (ContactInfo) evt.getTriggerData() );
            DynamicPhysicsNode sphere;
            // the contact could be sphere <-> floor or floor <-> sphere
            if ( contactInfo.getNode2() instanceof DynamicPhysicsNode ) {
                // ok it's floor <-> sphere
                sphere = (DynamicPhysicsNode) contactInfo.getNode2();
            }
            else if ( contactInfo.getNode1() instanceof DynamicPhysicsNode ) {
                // ok it's sphere <-> floor
                sphere = (DynamicPhysicsNode) contactInfo.getNode1();
            }
            else {
                // no dynamic node - should not happen, but we ignore it
                return;
            }
            // put the sphere back up
            sphere.clearDynamics();
            sphere.getLocalTranslation().set( 0, 5, 0 );
        }
    }
}

/*
 * $log$
 */

