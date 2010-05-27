package com.jme.bounding;

import java.nio.FloatBuffer;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.OrientedBoundingBox;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;

/**
 * 
 */
public class BoundsTest extends junit.framework.TestCase {
    public void testMergeBox() {
        BoundingBox b1 = new BoundingBox( new Vector3f( 0, 0, -15 ), 10, 10, 0 );
        BoundingBox b2 = new BoundingBox( new Vector3f( 0, 0, -5 ), 10, 10, 0 );
        BoundingBox result = new BoundingBox();
        b1.clone( result );
        result.merge( b2 );
        assertEquals( "center x", 0, result.getCenter().x, FastMath.FLT_EPSILON );
        assertEquals( "center y", 0, result.getCenter().y, FastMath.FLT_EPSILON );
        assertEquals( "center z", -10, result.getCenter().z, FastMath.FLT_EPSILON );
        assertEquals( "extent x", 10, result.xExtent, FastMath.FLT_EPSILON );
        assertEquals( "extent y", 10, result.yExtent, FastMath.FLT_EPSILON );
        assertEquals( "extent z", 5, result.zExtent, FastMath.FLT_EPSILON );
    }

    public void testMergeSphereOBB() {
        BoundingSphere sphere = new BoundingSphere( 1, new Vector3f() );
        OrientedBoundingBox obb = new OrientedBoundingBox();
        obb.setCenter( new Vector3f( 1, 1, 0 ) );
        obb.setExtent( new Vector3f( 1, 1, 1 ) );

        BoundingSphere merged = (BoundingSphere) sphere.merge( obb );
        BoundingSphere merged2 = (BoundingSphere) merged.merge( obb );
        BoundingSphere merged3 = (BoundingSphere) merged2.merge( obb );
        assertEquals( "center", merged.getCenter(), merged2.getCenter() );
        assertEquals( "radius", merged.getRadius(), merged2.getRadius(), FastMath.FLT_EPSILON );
        assertEquals( "center", merged.getCenter(), merged3.getCenter() );
        assertEquals( "radius", merged.getRadius(), merged3.getRadius(), FastMath.FLT_EPSILON );
    }

    public void testComputeSphereFromPoints() {
        BoundingSphere sphere = new BoundingSphere();
        float radius = 2;
        checkPointsOnCircle( radius, sphere, 2 );
        checkPointsOnCircle( radius, sphere, 3 );
        checkPointsOnCircle( radius, sphere, 4 );
        checkPointsOnCircle( radius, sphere, 5 );
        checkPointsOnCircle( radius, sphere, 10 );

        checkPointsOnSphere( radius, sphere, 2 );
        checkPointsOnSphere( radius, sphere, 3 );
        checkPointsOnSphere( radius, sphere, 4 );
    }

    private void checkPointsOnCircle( float radius, BoundingSphere sphere, int points ) {
        FloatBuffer buffer;
        buffer = FloatBuffer.allocate( points*2*3 );
        for ( int i=0; i<points; i++ ) {
            buffer.put( FastMath.cos( 2*i*FastMath.PI/points ) * ( i * radius / points ) )
                    .put( FastMath.sin( 2*i*FastMath.PI/points ) * ( i * radius / points ) ).put( 0 );
        }
        for ( int i=0; i<points; i++ ) {
            buffer.put( FastMath.cos( 2*i*FastMath.PI/points ) * radius ).put( FastMath.sin( 2*i*FastMath.PI/points ) * radius ).put( 0 );
        }
        sphere.computeFromPoints( buffer );
        assertEquals( "radius from "+(points*2)+" points", radius, sphere.getRadius(), 0.001f );
    }

    private void checkPointsOnSphere( float radius, BoundingSphere sphere, int points ) {
        FloatBuffer buffer;
        buffer = FloatBuffer.allocate( points*2*3 );
        for ( int i=0; i<points; i++ ) {
            buffer.put( FastMath.cos( 2*i*FastMath.PI/points ) * ( radius * 0.9f ) ).put( FastMath.sin( 2*i*FastMath.PI/points ) * ( radius * 0.9f ) ).put( 0 );
        }
        for ( int i=0; i<points; i++ ) {
            buffer.put( 0 ).put( FastMath.cos( 2*i*FastMath.PI/points ) * radius ).put( FastMath.sin( 2*i*FastMath.PI/points ) * radius );
        }
        sphere.computeFromPoints( buffer );
        assertEquals( "radius from "+points+" points", radius, sphere.getRadius(), 0.001f );
    }
}