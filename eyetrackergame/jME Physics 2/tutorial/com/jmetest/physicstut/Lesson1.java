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

import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.geometry.PhysicsBox;
import com.jmex.physics.util.SimplePhysicsGame;

/**
 * This class shows the most simple physics: A dynamic box falling onto a static floor (also a box).
 * <p/>
 * Prerequisites:
 * <ul>
 * <li> Java 5 and general programming skill, of course </li>
 * <li> you should have a general understanding of jME (including OpenGL as LWJGL is the default rederer) </li>
 * <li> read up a bit on general physics simulation (e.g. chapter 3 of the ODE user guide on http://ode.org) </li>
 * <li> already got jME Physics 2 API + implementation compiled - run TestGenerateGeometry to verify that </li>
 * <ul>
 *
 * @author Irrisor
 */
public class Lesson1 extends SimplePhysicsGame {
    protected void simpleInitGame() {
        // first we will create the floor
        // as the floor can't move we create a _static_ physics node
        StaticPhysicsNode staticNode = getPhysicsSpace().createStaticNode();

        // attach the node to the root node to have it updated each frame
        rootNode.attachChild( staticNode );

        // now we create a collision geometry for the floor - a box
        PhysicsBox floorBox = staticNode.createBox( "floor" );

        // the box is already attached to our static node
        // it currently has height, width and depth of 1
        // resize it to be 10x10 thin (0.5) floor
        floorBox.getLocalScale().set( 10, 0.5f, 10 );

        // second we create a box that should fall down on the floor
        // as the new box should move we create a _dynamic_ physics node
        DynamicPhysicsNode dynamicNode = getPhysicsSpace().createDynamicNode();

        // also attach it to the scene
        rootNode.attachChild( dynamicNode );

        // create another collision geometry, now for the falling box
        dynamicNode.createBox( "falling box" );
        // we don't scale this one, size (1,1,1) is fine

        // we have to move our dynamic node upwards such that is does not start in but above the floor
        dynamicNode.getLocalTranslation().set( 0, 5, 0 );
        // note: we do not move the collision geometry but the physics node!

        // ok we have created some physics stuff but no actual meshes that could be seen
        // thus we activate the debug mode to allow us to see anything (can be toggled in game with key V)
        showPhysics = true;
    }

    /**
     * The main method to allow starting this class as application.
     *
     * @param args command line arguments
     */
    public static void main( String[] args ) {
        Logger.getLogger( "" ).setLevel( Level.WARNING ); // to see the important stuff
        new Lesson1().start();
    }
}

/*
 * $log$
 */

