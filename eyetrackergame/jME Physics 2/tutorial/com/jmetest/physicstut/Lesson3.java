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

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.MaterialState;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.contact.MutableContactInfo;
import com.jmex.physics.material.Material;
import com.jmex.physics.util.SimplePhysicsGame;

/**
 * Learn about physics materials, friction, bounciness etc.
 *
 * @author Irrisor
 */
public class Lesson3 extends SimplePhysicsGame {
    protected void simpleInitGame() {
        // first we will create the floor like in Lesson2
        StaticPhysicsNode staticNode = getPhysicsSpace().createStaticNode();
        rootNode.attachChild( staticNode );
        final Box visualFloorBox = new Box( "floor", new Vector3f(), 5, 0.25f, 5 );
        staticNode.attachChild( visualFloorBox );
        // this time we tilt it a bit
        visualFloorBox.getLocalRotation().fromAngleNormalAxis( 0.3f, new Vector3f( 0, 0, -1 ) );
        // and create another part below
        final Box visualFloorBox2 = new Box( "floor", new Vector3f(), 5, 0.25f, 5 );
        staticNode.attachChild( visualFloorBox2 );
        visualFloorBox2.getLocalTranslation().set( 9.7f, -1.5f, 0 );
        staticNode.generatePhysicsGeometry();

        // second we create a box - as we create multiple boxes this time the code moved into a separate method
        DynamicPhysicsNode dynamicNode = createBox();
        // the first box gets in the center above the floor
        dynamicNode.getLocalTranslation().set( 0, 5, 0 );
        // this box keeps the default material

        // ok first we will explore some predefined materials
        // lets create an ice block...
        final DynamicPhysicsNode iceQube = createBox();
        iceQube.setMaterial( Material.ICE );
        // changing material means changing desity
        // you may choose to compute the mass from the volume and density of an object
        iceQube.computeMass();
        // color the visual representation in transparent blue
        color( iceQube, new ColorRGBA( 0.5f, 0.5f, 0.9f, 0.6f ) );
        // move the iceQube besides the first box
        iceQube.getLocalTranslation().set( 0, 5, 1.5f );

        // ... a rubber ...
        final DynamicPhysicsNode rubber = createBox();
        rubber.setMaterial( Material.RUBBER );
        rubber.computeMass();
        // color the visual representation in yellow
        color( rubber, new ColorRGBA( 0.9f, 0.9f, 0.3f, 1 ) );
        // move the rubber on the other side of the first box
        rubber.getLocalTranslation().set( 0, 5, -1.5f );

        // finally we define a custom material
        final Material customMaterial = new Material( "supra-stopper" );
        // we make it really light
        customMaterial.setDensity( 0.05f );
        // a material should define contact detail pairs for each other material it could collide with in the scene
        // do that just for the floor material - the DEFAULT material
        MutableContactInfo contactDetails = new MutableContactInfo();
        // our material should not bounce on DEFAULT
        contactDetails.setBounce( 0 );
        // and should never slide on DEFAULT
        contactDetails.setMu( 1000 ); // todo: Float.POSITIVE_INFINITY seems to cause issues on Linux (only o_O)
        // now set that behaviour
        customMaterial.putContactHandlingDetails( Material.DEFAULT, contactDetails );

        // ... finally test our supra-stopper with a red cube
        final DynamicPhysicsNode stopper = createBox();
        stopper.setMaterial( customMaterial );
        // don't forget to compute mass from density
        stopper.computeMass();
        // color the visual representation in yellow
        color( stopper, new ColorRGBA( 1, 0, 0, 1 ) );
        // move the stopper to the front
        stopper.getLocalTranslation().set( 0, 5, 3 );

        // start paused - press P to start the action :)
        pause = true;
    }

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

    /**
     * The main method to allow starting this class as application.
     *
     * @param args command line arguments
     */
    public static void main( String[] args ) {
        Logger.getLogger( "" ).setLevel( Level.WARNING ); // to see the important stuff
        new Lesson3().start();
    }
}

/*
 * $log$
 */

