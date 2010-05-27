/*
 * Copyright (c) 2003-2009 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
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

package jmetest.stress.swarm;

import java.util.Random;
import java.util.logging.Logger;

import jmetest.stress.StressApp;

import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.scene.Text;

/**
 * This is a stress test with following charactersitics:
 * very high number of fast rendering geoms (boxes), with many changes to position (on/off), flat/organized tree
 * <br>
 * Many {@link Fish} are swarming around...
 *
 * @author Irrisor
 * @created 21.11.2004, 12:28:12
 */
public class TestSwarm extends StressApp {
    private static final Logger logger = Logger.getLogger(TestSwarm.class
            .getName());
    
    /**
     * Flag for toggling flat/organized.
     */
    private boolean doReorganizeScenegraph = true;

    /**
     * Manager for scene graph (flat/organized).
     */
    private CollisionTreeManager collisionTreeManager;
    /**
     * command string for flat/organized toggle.
     */
    private static final String COMMAND_REORGANIZATION = "toggle_reorganization";
    /**
     * Total number of fish created.
     */
    private static final int NUMBER_OF_FISH = 1000;
    /**
     * command string for full behaviour / independent behaviour
     */
    private static final String COMMAND_COLLISION = "toggle_collision";

    private long startTime;
    private int frame;

    /**
     * Called near end of initGame(). Must be defined by derived classes.
     */
    protected void simpleInitGame() {
        long initStartTime = System.currentTimeMillis(); //todo: replace by nanoTime() when JDK1.5 required
        Random random = new Random( 1 );

        collisionTreeManager = new CollisionTreeManager( rootNode, new float[]{0.2f, 1.2f} );
//        collisionTreeManager = new CollisionTreeManager( rootNode, new float[]{0.1f, 1.0f} );

        //create some fish
        for ( int i = 0; i < NUMBER_OF_FISH / 10; ++i ) {
            final Fish fish = new Fish(
                    0 + random.nextFloat() - 0.5f, 0 + random.nextFloat() - 0.5f, 0,
                    random.nextFloat() - 0.5f, random.nextFloat() - 0.5f, 0,
                    0.001f, rootNode );
            collisionTreeManager.add( fish );
        }

        for ( int i = 0; i < NUMBER_OF_FISH * 2 / 10; ++i ) {
            final Fish fish = new Fish(
                    0 + random.nextFloat() - 0.5f, random.nextFloat() - 0.5f, 0,
                    random.nextFloat() - 0.5f, random.nextFloat() - 0.5f, 0,
                    0.005f, rootNode );
            collisionTreeManager.add( fish );
        }

        for ( int i = 0; i < NUMBER_OF_FISH * 7 / 10; ++i ) {
            final Fish fish = new Fish(
                    0 + random.nextFloat() - 0.5f, 0 + random.nextFloat() - 0.5f, 0,
                    random.nextFloat() - 0.5f, random.nextFloat() - 0.5f, 0,
                    0.01f, rootNode );
            collisionTreeManager.add( fish );
        }

        //get view nearer
        cam.getLocation().set( 0, 0, 5 );
        cam.update();

        KeyBindingManager.getKeyBindingManager().set(
                COMMAND_REORGANIZATION,
                KeyInput.KEY_R );
        final Text text = createText( "Press R to toggle scene graph reorganization (node tree / flat)" );
        text.getLocalTranslation().set( 0, 20, 0 );
        statNode.attachChild( text );

        KeyBindingManager.getKeyBindingManager().set(
                COMMAND_COLLISION,
                KeyInput.KEY_U );
        final Text text2 = createText( "Press U to toggle collision detection use (fish perception on/off)" );
        text2.getLocalTranslation().set( 0, 40, 0 );
        statNode.attachChild( text2 );
        long initTime = System.currentTimeMillis() - initStartTime;
        logger.info( "Setup took " + initTime + " ms (below 100 ms very inaccurate)." );
        startTime = System.currentTimeMillis();
    }

    /**
     * Can be defined in derived classes for custom updating.
     * Called every frame in update.
     */
    protected void simpleUpdate() {
        if ( KeyBindingManager
                .getKeyBindingManager()
                .isValidCommand( COMMAND_REORGANIZATION, false ) ) {
            doReorganizeScenegraph = !doReorganizeScenegraph;
            if ( !doReorganizeScenegraph ) {
                collisionTreeManager.disable();
            }
        }
        if ( KeyBindingManager
                .getKeyBindingManager()
                .isValidCommand( COMMAND_COLLISION, false ) ) {
            Fish.useCollisionDetection = !Fish.useCollisionDetection;
        }
        if ( doReorganizeScenegraph ) {
            collisionTreeManager.reorganize();
        }
        frame++;
        if ( frame == 100 ) {
            long time = System.currentTimeMillis() - startTime;
            logger.info( "First 100 frames took " + time + " ms." );
        }
    }

    /**
     * Main.
     *
     * @param args command line arguments
     */
    public static void main( String[] args ) {
        TestSwarm app = new TestSwarm();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }
}
