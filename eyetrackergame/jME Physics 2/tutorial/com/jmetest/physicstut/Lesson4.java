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

import com.jme.input.InputHandler;
import com.jme.input.KeyInput;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.math.Vector3f;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.geometry.PhysicsBox;
import com.jmex.physics.util.SimplePhysicsGame;

/**
 * This class shows first interaction with physics: A sphere falling onto a floor. The sphere can be moved with forces
 * by pressing a key. It is put back onto the floor if it falls down.
 * <p/>
 * Note: We are not just changing the location of sphere but apply forces. This allows the physics system to take
 * care that nodes do not move straight through each other. The physics system might even get quite unstable if
 * we move nodes into each other just by changing location.
 *
 * @author Irrisor
 */
public class Lesson4 extends SimplePhysicsGame {
    private DynamicPhysicsNode dynamicNode;

    protected void simpleInitGame() {
        // first we will create a floor like in Lesson1
        StaticPhysicsNode staticNode = getPhysicsSpace().createStaticNode();
        rootNode.attachChild( staticNode );
        PhysicsBox floorBox = staticNode.createBox( "floor" );
        floorBox.getLocalScale().set( 10, 0.5f, 10 );
        // note: we did not call floorBox.setLocalScale( new Vector( 10, 0.5f, 10 ) ) as this creates a vector
        //       this is especially important (not creating object) when you do it every frame

        // second we create a sphere that should fall down on the floor - analoguous to the box from Lesson1
        dynamicNode = getPhysicsSpace().createDynamicNode();
        rootNode.attachChild( dynamicNode );
        dynamicNode.createSphere( "rolling sphere" );
        dynamicNode.getLocalTranslation().set( 0, 5, 0 );
        // note: we do not move the collision geometry but the physics node!

        // now we add an input action to move the sphere on a key event
        input.addAction( new MyInputAction(),
                InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_HOME, InputHandler.AXIS_NONE, false );
        // the action is defined below
        // we register it to be invoked _once_ of every stroke of the HOME key (POS1)

        // again we have created only physics - activate physics debug mode to see something
        showPhysics = true;
    }

    /**
     * An action that get's invoked on a keystroke (once per stroke).
     */
    private class MyInputAction extends InputAction {
        /**
         * This method gets invoked upon key event
         *
         * @param evt more data about the event (we don't need it)
         */
        public void performAction( InputActionEvent evt ) {
            // the only really important line: apply a force to the moved node
            dynamicNode.addForce( new Vector3f( 50, 0, 0 ) );
            // note: forces are applied every physics step and accumulate until then
            //       to apply a constant force we would have to do it for each physics step! (see next lesson)
        }
    }

    @Override
    protected void simpleUpdate() {
        // as the user can steer the sphere only in one direction it will fall off the floor after a short time
        // we want to put it back up then
        if ( dynamicNode.getWorldTranslation().y < -20 ) {
            // ok it has definately fallen off the floor
            // clear speed and forces
            dynamicNode.clearDynamics();
            // then put it over the floor again
            dynamicNode.getLocalTranslation().set( 0, 5, 0 );
        }
    }

    /**
     * The main method to allow starting this class as application.
     *
     * @param args command line arguments
     */
    public static void main( String[] args ) {
        Logger.getLogger( "" ).setLevel( Level.WARNING ); // to see the important stuff
        new Lesson4().start();
    }
}

/*
 * $log$
 */