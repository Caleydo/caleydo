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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture.MagnificationFilter;
import com.jme.image.Texture.MinificationFilter;
import com.jme.image.Texture.WrapMode;
import com.jme.input.MouseInput;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Line;
import com.jme.scene.SharedMesh;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Extrusion;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jme.util.geom.BufferUtils;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.util.PhysicsPicker;
import com.jmex.physics.util.SimplePhysicsGame;


public class TestDomino extends SimplePhysicsGame {
    protected void simpleInitGame() {
        getPhysicsSpace().setAutoRestThreshold( 0.2f );
        setPhysicsSpeed( 4 );

        Spatial floorVisual = new Box( "floor", new Vector3f(), 1000, 0.1f, 1000 );
        floorVisual.setModelBound( new BoundingBox() );
        floorVisual.updateModelBound();
        StaticPhysicsNode floor = getPhysicsSpace().createStaticNode();
        floor.attachChild( floorVisual );
        floor.generatePhysicsGeometry();
        floor.setLocalTranslation( new Vector3f( 0, -0.1f, 0 ) );
        rootNode.attachChild( floor );

        final TextureState wallTextureState = display.getRenderer().createTextureState();
        wallTextureState.setTexture( TextureManager.loadTexture( jmetest.TestChooser.class.getResource( "data/texture/wall.jpg" ),
                MinificationFilter.Trilinear, MagnificationFilter.Bilinear) );
        wallTextureState.getTexture().setScale( new Vector3f( 30, 30, 1 ) );
        wallTextureState.getTexture().setWrap( WrapMode.Repeat );
        floorVisual.setRenderState( wallTextureState );

        TriMesh dominoBrickVisual[] = new TriMesh[4];
        for ( int i = 0; i < dominoBrickVisual.length; i++ ) {
            dominoBrickVisual[i] = new Box( "brick", new Vector3f(), 1f, 2, 0.2f );
            dominoBrickVisual[i].setModelBound( new BoundingBox() );
            dominoBrickVisual[i].updateModelBound();
            dominoBrickVisual[i].lockMeshes();
            //color them blue, red, green, yellow, ...
            Utils.color( dominoBrickVisual[i], new ColorRGBA( i&1, ( i & 2 ) >> 1, i==0?1:0, 1 ), 128 );
            dominoBrickVisual[i].updateRenderState();
        }

        List<Vector3f> points = new ArrayList<Vector3f>();
        points.add( new Vector3f( 0, 0, 0 ) );
        points.add( new Vector3f( 0, 0, 50 ) );
        points.add( new Vector3f( 50, 0, 100 ) );
        points.add( new Vector3f( 50, 0, 150 ) );
        points.add( new Vector3f( 0, 0, 175 ) );
        points.add( new Vector3f( -50, 0, 200 ) );
        points.add( new Vector3f( -30, 0, 250 ) );
        points.add( new Vector3f( 0, 0, 300 ) );
        points.add( new Vector3f( 0, 0, 300 ) );
        Line dot = new Line();
        dot.appendCircle( 0.3f, 0, 0, 1, false );
        Extrusion track = new Extrusion( "track" );
        Vector3f up = new Vector3f( 0, 1, 0 );
        track.updateGeometry( dot, points, 25, up );

        Vector3f[] trackpoints = BufferUtils.getVector3Array( track.getVertexBuffer() );
        Vector3f last = null;
        Vector3f dir = new Vector3f();
        // iterate over the extrusion points, taking every second vertice
        for ( int i = 0; i < trackpoints.length; i += 2 ) {
            Vector3f trackpoint = trackpoints[i];
            DynamicPhysicsNode dominoBrick = getPhysicsSpace().createDynamicNode();
            SharedMesh sharedMesh = new SharedMesh( "shared brick", dominoBrickVisual[i/2%dominoBrickVisual.length] );
            dominoBrick.attachChild( sharedMesh );
            dominoBrick.generatePhysicsGeometry();
            dominoBrick.getLocalTranslation().set( trackpoint ).addLocal( 0, 2, 0 );
            if ( last != null ) {
                dir.set( last ).subtractLocal( trackpoint );
                dominoBrick.getLocalRotation().lookAt( dir, up );
            }
            rootNode.attachChild( dominoBrick );
            dominoBrick.rest();
            last = trackpoint;
        }
        System.out.println( trackpoints.length / 2 + " domino bricks created" );

        Sphere ballVisual = new Sphere( "ball", new Vector3f(), 10, 10, 2 );
        ballVisual.setModelBound( new BoundingSphere() );
        ballVisual.updateModelBound();
        DynamicPhysicsNode ball = getPhysicsSpace().createDynamicNode();
        ball.attachChild( ballVisual );
        ball.generatePhysicsGeometry();
        ball.setLocalTranslation( new Vector3f( 0, 3, -1.5f ) );
        rootNode.attachChild( ball );

        MouseInput.get().setCursorVisible( true );
        new PhysicsPicker( input, rootNode, getPhysicsSpace() );

        cam.setLocation( new Vector3f( 100, 100, -50 ) );
        cam.lookAt( new Vector3f( 0, 0, 50 ), new Vector3f( 0, 1, 0 ) );

        Text label = Text.createDefaultTextLabel( "instructions", "Left mouse button to hold/drag stuff, " +
                "right mouse button+WASD to move the camera." );
        label.setLocalTranslation( 0, 20, 0 );
        statNode.attachChild( label );
    }

    @Override
    protected void simpleUpdate() {
        cameraInputHandler.setEnabled( MouseInput.get().isButtonDown( 1 ) );
    }

    public static void main( String[] args ) {
        Logger.getLogger( "" ).setLevel( Level.WARNING ); // to see the important stuff
        // create a new thread to benefit from -Xss setting to increase stack size
        new Thread() {
            @Override
            public void run() {
                new TestDomino().start();
            }
        }.start();
    }
}

/*
 * $Log: TestDomino.java,v $
 * Revision 1.5  2007/09/22 14:28:36  irrisor
 * spongy contacts, improved material handling, center of mass correctly handled now, breakable joints, use odejava 0.3.1
 *
 * Revision 1.4  2007/09/02 20:44:09  irrisor
 * node picking, timer fix
 *
 * Revision 1.3  2007/08/28 12:19:37  irrisor
 * renamed autodisable to autorest, added unrest method, set root logger to warning level instead of physics logger only
 *
 * Revision 1.2  2007/08/03 11:26:46  irrisor
 * adapted to new logging (JUL)
 *
 * Revision 1.1  2007/06/16 14:17:01  irrisor
 * Contacts got an 'applied' flag to avoid application but still generate events -> GHOST material working;
 * New test with dominos, some optimizations concerning enabled nodes; physics speed can be adjusted in SimplePhysicsGame
 *
 */

