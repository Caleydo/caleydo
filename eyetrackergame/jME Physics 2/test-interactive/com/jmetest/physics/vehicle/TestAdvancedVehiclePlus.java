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
package com.jmetest.physics.vehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Geometry;
import com.jme.scene.Line;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Extrusion;
import com.jme.scene.shape.Sphere;
import com.jmetest.physics.Utils;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.material.Material;

/**
 * Same as TestAdvancedVehicle (extended) but with some obstacles and a loop.
 */
public class TestAdvancedVehiclePlus extends TestAdvancedVehicle {
    @Override
    protected void simpleInitGame() {
        super.simpleInitGame();

        createRamp( 0, 10, -100, 0.2f, Vector3f.UNIT_X, 50, 50 );
        createRamp( 0, 10, -198, -0.2f, Vector3f.UNIT_X, 50, 50 );

        /*for ( int i = -25; i < 25; i++ ) {
            createStuff( i * 20, 5, 500, 0, null, new Sphere( "", 20, 20, 5 ) );
            createStuff( i * 20, 5, -500, 0, null, new Box( "", new Vector3f(), 5, 5, 5 ) );
            createStuff( 500, 5, i * 20, 0, null, new Box( "", new Vector3f(), 5, 5, 5 ) );
            createStuff( -500, 5, i * 20, 0, null, new Box( "", new Vector3f(), 5, 5, 5 ) );
        }*/
        for ( int y = 0; y < 5; y++ ) {
        for ( int x = -6+y; x < 6-y; x++ ) {
            createStuff( x * 10, 5+y*10, 500, 0, null, new Sphere( "", 20, 20, 5 ) );
            createStuff( x * 10, 5+y*10, -500, 0, null, new Box( "", new Vector3f(), 5, 5, 5 ) );
            createStuff( 500, 5+y*10, x * 10, 0, null, new Box( "", new Vector3f(), 5, 5, 5 ) );
            createStuff( -500, 5+y*10, x * 10, 0, null, new Box( "", new Vector3f(), 5, 5, 5 ) );
        } }

        createLoop();

        //simulate one second before frame timer starts to avoid ChaseCam going crazy
        rootNode.updateGeometricState( 1, true );
        getPhysicsSpace().update( 1 );

        //debug: reproduce the NaN problem
        car.setPosition( 396.34573f, 5.2607946f, -3.0353582f );
        car.setRotation( -0.04183941f, -0.6998149f, 0.0071339696f, -0.7130623f );
    }

    private void createLoop() {
        List<Vector3f> points = new ArrayList<Vector3f>();
        int segments = 20;
        float radius = 80;
        float angle = FastMath.PI;
        float step = FastMath.PI * 2 / segments;
        for ( int i = 0; i < segments+2; i++ ) {
            float dx = FastMath.cos( angle ) * radius;
            float dy = FastMath.sin( angle ) * radius;
            Vector3f point = new Vector3f( -i*1.5f, dx+radius, dy );
            points.add( point );
            angle += step;
        }

        Extrusion mesh = new Extrusion();
        mesh.updateGeometry( new Line( "", new Vector3f[]{new Vector3f( 0, -15, 0 ), new Vector3f( 0, 15, 0 )},
                new Vector3f[]{new Vector3f( 1, 0, 0 ), new Vector3f( 1, 0, 0 )}, null, null ),
                points, 10, false, new Vector3f( 1, 0, 0 ).normalizeLocal() );
        mesh.setModelBound( new BoundingSphere() );
        mesh.updateModelBound();

        StaticPhysicsNode loop = getPhysicsSpace().createStaticNode();
        loop.attachChild( mesh );
        loop.generatePhysicsGeometry( true );
        loop.setMaterial( Material.CONCRETE );
        loop.getLocalTranslation().set(400, 0.1f, -25 );
        loop.getLocalRotation().fromAngleNormalAxis( FastMath.PI/2, Vector3f.UNIT_Y );
        rootNode.attachChild( loop );
    }

    private void createRamp( float x, float y, float z, float angle, Vector3f axis, float sx, float sz ) {
        final StaticPhysicsNode node = getPhysicsSpace().createStaticNode();
        final Box box = new Box( "ramp", new Vector3f(), sx, 0.1f, sz );
        node.attachChild( box );
        box.setModelBound( new BoundingBox() );
        box.updateModelBound();
        node.setMaterial( Material.IRON );
        Utils.color( node, new ColorRGBA( 0.8f, 0.8f, 0.8f, 1 ), 128 );
        node.generatePhysicsGeometry();
        node.getLocalRotation().fromAngleNormalAxis( angle, axis );
        node.getLocalTranslation().set( x, y, z );
        rootNode.attachChild( node );
    }

    private void createStuff( float x, float y, float z, float angle, Vector3f axis, Geometry geom ) {
        final DynamicPhysicsNode node = getPhysicsSpace().createDynamicNode();
        node.attachChild( geom );
        node.setMaterial( Material.PLASTIC );
        Utils.color( node, new ColorRGBA(
                FastMath.rand.nextFloat(),
                1, 0, 1 ), 128 );
        node.generatePhysicsGeometry();
        geom.setModelBound( new BoundingBox() );
        geom.updateModelBound();
        if ( axis != null ) {
            node.getLocalRotation().fromAngleNormalAxis( angle, axis );
        }
        node.getLocalTranslation().set( x, y, z );
        node.rest();
        rootNode.attachChild( node );
    }

    public static void main( String[] args ) {
        Logger.getLogger( "" ).setLevel( Level.WARNING );
        new TestAdvancedVehiclePlus().start();
    }
}

/*
 * $Log: TestAdvancedVehiclePlus.java,v $
 * Revision 1.2  2007/10/18 14:41:03  irrisor
 * allow collision detection for joint nodes
 *
 * Revision 1.1  2007/10/06 16:01:08  irrisor
 * additional advances vehicle test with some stuff on the plane
 *
 */

