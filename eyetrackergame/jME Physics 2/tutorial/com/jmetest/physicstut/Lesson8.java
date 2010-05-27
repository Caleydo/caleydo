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
import com.jme.input.util.SyntheticButton;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.shape.Box;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.MaterialState;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.PhysicsUpdateCallback;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.contact.ContactInfo;
import com.jmex.physics.material.Material;
import com.jmex.physics.util.SimplePhysicsGame;

/**
 * This lesson shows a very simple example of modelling physics for a jump-and-run-like player character. It uses a box
 * with an altered center of gravity. Moving the player is done with surface motion, jumping by directly applying
 * forces.
 * <p/>
 * Important: this is, of course, not meant as <i>the</i> way to go! The actual physical representation that is chosen
 * for characters in a game strongly depends on the abilities the character should have and the other effects you want
 * to achieve. E.g. you could make a racing game with the approach shown here, but it will disregard many aspects you
 * might want to have in a racing game... 
 * <p/>
 * Especially not regarded here: character cannot be turned, rotation of the character is not restricted - it may turn
 * when acting a while.
 * @author Irrisor
 */
public class Lesson8 extends SimplePhysicsGame {

    protected void simpleInitGame() {
        // no magic here - just create a floor in that method
        createFloor();

        // second we create a box - as we create multiple boxes this time the code moved into a separate method
        player = createBox();
        player.setName( "player" );
        color( player, new ColorRGBA( 0, 0, 1, 1 ) );
        // the first box gets in the center above the floor
        player.getLocalTranslation().set( 8, 1, 0 );
        // move the center of mass down to let the box land on its 'feet'
        player.setCenterOfMass( new Vector3f( 0, -0.5f, 0 ) );
        // this box keeps the default material

        // to move the player around we create a special material for it
        // and apply surface motion on it
        final Material playerMaterial = new Material( "player material" );
        player.setMaterial( playerMaterial );
        // the actual motion is applied in the MoveAction (see below)

        // we map the MoveAction to the keys DELETE and PAGE DOWN for forward and backward
        input.addAction( new MoveAction( new Vector3f( -2, 0, 0 ) ),
                InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_DELETE, InputHandler.AXIS_NONE, false );
        input.addAction( new MoveAction( new Vector3f( 2, 0, 0 ) ),
                InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_PGDN, InputHandler.AXIS_NONE, false );

        // now the player should be able to jump
        // we do this by applying a single force vector when the HOME key is pressed
        // this should happen only while the player is touching the floor (we determine that below)
        input.addAction( new InputAction() {
            public void performAction( InputActionEvent evt ) {
                if ( playerOnFloor && evt.getTriggerPressed() ) {
                    player.addForce( new Vector3f( 0, 500, 0 ) );
                }
            }
        }, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_HOME, InputHandler.AXIS_NONE, false );

        // ok finally detect when the player is touching the ground:
        // a simple way to do this is making a boolean variable (playerOnFloor) which is
        // set to true on collision with the floor and to false before each physics computation

        // collision events analoguous to Lesson8
        SyntheticButton playerCollisionEventHandler = player.getCollisionEventHandler();
        input.addAction( new InputAction() {
            public void performAction( InputActionEvent evt ) {
                ContactInfo contactInfo = (ContactInfo) evt.getTriggerData();
                if ( contactInfo.getNode1() == floor || contactInfo.getNode2() == floor ) {
                    playerOnFloor = true;
                }
            }
        }, playerCollisionEventHandler, false ); //TODO: this should be true when physics supports release events

        // and a very simple callback to set the variable to false before each step
        getPhysicsSpace().addToUpdateCallbacks( new PhysicsUpdateCallback() {
            public void beforeStep( PhysicsSpace space, float time ) {
                playerOnFloor = false;
            }

            public void afterStep( PhysicsSpace space, float time ) {

            }
        } );

        // finally print a key-binding message
        Text infoText = Text.createDefaultTextLabel( "key info", "[del] and [page down] to move, [home] to jump" );
        infoText.getLocalTranslation().set( 0, 20, 0 );
        statNode.attachChild( infoText );
    }

    private void createFloor() {
        // first we will create the floor like in Lesson3, but put into into a field
        floor = getPhysicsSpace().createStaticNode();
        rootNode.attachChild( floor );
        final Box visualFloorBox = new Box( "floor", new Vector3f(), 5, 0.25f, 5 );
        floor.attachChild( visualFloorBox );
        // and not that steep
        visualFloorBox.getLocalRotation().fromAngleNormalAxis( 0.1f, new Vector3f( 0, 0, -1 ) );
        final Box visualFloorBox2 = new Box( "floor", new Vector3f(), 5, 0.25f, 5 );
        floor.attachChild( visualFloorBox2 );
        visualFloorBox2.getLocalTranslation().set( 9.7f, -0.5f, 0 );
        // and another one a bit on the left
        final Box visualFloorBox3 = new Box( "floor", new Vector3f(), 5, 0.25f, 5 );
        floor.attachChild( visualFloorBox3 );
        visualFloorBox3.getLocalTranslation().set( -11, 0, 0 );
        floor.generatePhysicsGeometry();
    }

    /**
     * Action called on key input for applying movement of the player.
     */
    private class MoveAction extends InputAction {
        /**
         * store direction this action instance will move.
         */
        private Vector3f direction;

        /**
         * @param direction direction this action instance will move
         */
        public MoveAction( Vector3f direction ) {
            this.direction = direction;
        }

        public void performAction( InputActionEvent evt ) {
            if ( evt.getTriggerPressed() ) {
                // key goes down - apply motion
                player.getMaterial().setSurfaceMotion( direction );
            } else {
                // key goes up - stand still
                player.getMaterial().setSurfaceMotion( ZERO );
                // note: for a game we usually won't want zero motion on key release but be able to combine
                //       keys in some way
            }
        }
    }

    /**
     * helper vector for zeroing motion.
     */
    private static final Vector3f ZERO = new Vector3f( 0, 0, 0 );

    /**
     * floor node - is a field to easily access it in the action and callback.
     */
    private StaticPhysicsNode floor;
    /**
     * player node - also a field to easily access it in the actions.
     */
    private DynamicPhysicsNode player;
    /**
     * variable for detecting if the player is touching the floor.
     */
    private boolean playerOnFloor = false;

    /**
     * Little helper method to color a spatial.
     *
     * @param spatial the spatial to be colored
     * @param color   desired color
     */
    private void color( Spatial spatial, ColorRGBA color ) {
        final MaterialState materialState = display.getRenderer().createMaterialState();
        materialState.setDiffuse( color );
        if ( color.a < 1 ) {
            final BlendState blendState = display.getRenderer().createBlendState();
            blendState.setEnabled( true );
            blendState.setBlendEnabled( true );
            blendState.setSourceFunction( BlendState.SourceFunction.SourceAlpha );
            blendState.setDestinationFunction( BlendState.DestinationFunction.OneMinusSourceAlpha );
            spatial.setRenderState( blendState );
            spatial.setRenderQueueMode( Renderer.QUEUE_TRANSPARENT );
        }
        spatial.setRenderState( materialState );
    }

    /**
     * Create a box like known from Lesson2.
     *
     * @return a physics node containing a box
     */
    private DynamicPhysicsNode createBox() {
        DynamicPhysicsNode dynamicNode = getPhysicsSpace().createDynamicNode();
        rootNode.attachChild( dynamicNode );
        final Box visualFallingBox = new Box( "falling box", new Vector3f(), 0.5f, 0.5f, 0.5f );
        dynamicNode.attachChild( visualFallingBox );
        dynamicNode.generatePhysicsGeometry();
        return dynamicNode;
    }

    @Override
    protected void simpleUpdate() {
        // move the cam where the player is
        cam.getLocation().x = player.getLocalTranslation().x;
        cam.update();
    }

    /**
     * The main method to allow starting this class as application.
     *
     * @param args command line arguments
     */
    public static void main( String[] args ) {
        Logger.getLogger( "" ).setLevel( Level.WARNING ); // to see the important stuff
        new Lesson8().start();
    }
}

/*
 * $log$
 */

