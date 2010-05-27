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

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.input.InputHandler;
import com.jme.input.KeyInput;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Text;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Sphere;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.contact.ContactCallback;
import com.jmex.physics.contact.PendingContact;
import com.jmex.physics.material.Material;
import com.jmex.physics.util.SimplePhysicsGame;

/**
 * A Small Test to demonstrate the use of custom
 * contact callbacks. Collisions of certain objects are ignored in this test.
 * <p/>
 * A new Ball can be spawned with SPACE and RETURN. One of them will not bounce
 * off the middle wall.
 *
 * @author Christoph Luder
 * @see TestCollisionGroups
 */
public class TestContactCallback extends SimplePhysicsGame {
    private Set<DynamicPhysicsNode> magicBalls = new HashSet<DynamicPhysicsNode>();
    private StaticPhysicsNode wall;

    /**
     * Setup the Scene.
     */
    @Override
    protected void simpleInitGame() {
        // a bit more gravity
        getPhysicsSpace().setDirectionalGravity( new Vector3f( 0, -50, 0 ) );

        // add an ContactCallback to our PhysicsSpace.
        // here we check if the ball collides with the Object named "invisWall"
        // if so, ignore the collision.
        ContactCallback myCallBack = new ContactCallback() {
            public boolean adjustContact( PendingContact c ) {
                if ( ( magicBalls.contains( c.getNode1() ) && c.getNode2() == wall ) ||
                        ( magicBalls.contains( c.getNode2() ) && c.getNode1() == wall ) ) {
                    // Collision MagicBall <--> Wall
                    c.setIgnored( true );
                    return true;
                }
                // everything normal, continue with next callback
                return false;
            }
        };
        // add our custom callback to the end of the List,
        // that way, our callback gets executed first
        getPhysicsSpace().getContactCallbacks().add( myCallBack );

        // create the floor
        StaticPhysicsNode staticFloor = getPhysicsSpace().createStaticNode();
        staticFloor.setName( "floor physics" );
        Box floor = new Box( "floor", new Vector3f(), 60, 0.25f, 40 );
        Utils.color( floor, ColorRGBA.green, 10 );
        floor.setLocalTranslation( 0, -15, 0 );
        staticFloor.attachChild( floor );
        staticFloor.generatePhysicsGeometry();

        // create the wallBox, because of our callback it will physically not react
        // to 'MagicBall'
        wall = getPhysicsSpace().createStaticNode();
        wall.setName( "wall physics" );
        Box wallBox = new Box( "wall", new Vector3f(), 0.5f, 10, 30 );
        Utils.color( wallBox, ColorRGBA.gray, 0 );
        wall.attachChild( wallBox );
        wall.generatePhysicsGeometry();

        // attach everything to the root node
        rootNode.attachChild( staticFloor );
        rootNode.attachChild( wall );
        rootNode.updateGeometricState( 0, true );
        rootNode.updateRenderState();

        // set the camera little bit backwards.
        cam.setLocation( new Vector3f( 20, 0, 70 ) );
        cam.lookAt( wallBox.getLocalTranslation(), Vector3f.UNIT_Y );

        // Set our keys
        input.addAction( new InputAction() {
            public void performAction( InputActionEvent evt ) {
                if ( evt.getTriggerPressed() ) {
                    spawnBall( "Ball", false );
                }
            }
        }, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_SPACE, InputHandler.AXIS_NONE, false );
        input.addAction( new InputAction() {
            public void performAction( InputActionEvent evt ) {
                if ( evt.getTriggerPressed() ) {
                    spawnBall( "MagicBall", true );
                }
            }
        }, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_RETURN, InputHandler.AXIS_NONE, false );

        Text label = Text.createDefaultTextLabel( "instructions", "Press [space] to spawn a ball, " +
                "[enter] to spawn a 'magic' ball." );
        label.setLocalTranslation( 0, 20, 0 );
        statNode.attachChild( label );
    }

    /**
     * Create a new Ball and attach it to the Scene.
     *
     * @param name  name of the PhysicsNode
     * @param magic true to add the node to the set of magic balls
     */
    private void spawnBall( String name, boolean magic ) {
        DynamicPhysicsNode node = getPhysicsSpace().createDynamicNode();
        node.setName( name );
        if ( magic ) {
            magicBalls.add( node );
        }
        Sphere ball = new Sphere( "ball", 10, 10, 2 );
        Utils.color( ball, ColorRGBA.yellow, 10 );
        node.attachChild( ball );
        node.generatePhysicsGeometry();
        node.setMaterial( Material.RUBBER );
        node.setLocalTranslation( 50, 20, 0 );
        node.addForce( new Vector3f( -4000, 0, 0 ) );
        rootNode.attachChild( node );
        rootNode.updateRenderState();
    }

    public static void main( String[] args ) {
        Logger.getLogger( "" ).setLevel( Level.WARNING ); // to see the important stuff
        new TestContactCallback().start();
    }
}

/*
 * $Log: TestContactCallback.java,v $
 * Revision 1.2  2007/09/22 14:28:36  irrisor
 * spongy contacts, improved material handling, center of mass correctly handled now, breakable joints, use odejava 0.3.1
 *
 * Revision 1.1  2007/09/02 18:41:58  irrisor
 * added TestContactCallback, thanks core-dump (modified a little)
 *
 */

