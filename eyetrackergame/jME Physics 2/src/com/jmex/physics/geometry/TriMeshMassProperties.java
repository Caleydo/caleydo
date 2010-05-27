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

package com.jmex.physics.geometry;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;


/**
 * Allows to compute the volume, surface, center of mass and the inertia matrix of a TriMesh. This class assumes
 * that the mass is uniformly distributed amoungst the trimesh volume for computing the center of mass and inertia,
 * obviously.
 * @author Thomas Magnaud
 * @author Irrisor (ported class from Thomas to jME, thanks Thomas!)
 *
 * @see #TriMeshMassProperties(TriMesh)
 * @see #getVolume()
 * @see #getCenterOfMass()
 * @see #getInertia()
 * @see #getSurface()
 */
public class TriMeshMassProperties {
    private final TriMesh mesh;

    private Matrix3f inertia = new Matrix3f();
    private float volume = 0;
    private float surface = 0;
    private Vector3f centerOfMass = new Vector3f();


    /**
     * Create and compute TriMesh properties. The trimeshs world vectors should already be adjusted
     * (especially world scale).
     * @param mesh TriMesh to compute properties for
     */
    public TriMeshMassProperties( TriMesh mesh ) {
        this.mesh = mesh;
        computeMass();
    }

    /**
     * @return inertia matrix at (0,0,0) for the trimesh (for uniformly distributed mass in the volume),
     *                           world coordinates
     */
    public Matrix3f getInertia() {
        return inertia;
    }

    /**
     * @return center of mass for the trimesh (for uniformly distributed mass in the volume) in world coordinates
     */
    public Vector3f getCenterOfMass() {
        return centerOfMass.mult( 1f/volume );
    }

    /**
     * @return volume of the trimesh
     */
    public float getVolume() {
        return volume;
    }

    /**
     * @return surface of the trimesh
     */
    public float getSurface() {
        return surface;
    }

    /*
      * Computes the mass properties
      * and store the in the different fields
      */
    public void computeMass() {
        FloatBuffer pts = mesh.getVertexBuffer(  );
        FloatBuffer normals = mesh.getNormalBuffer(  );
        IntBuffer indices = mesh.getIndexBuffer(  );

        float x2 = 0, y2 = 0, z2 = 0;
        float xy = 0, xz = 0, yz = 0;
        float v = 0, xg = 0, yg = 0, zg = 0, s = 0;

        Vector3f a = new Vector3f();
        Vector3f b = new Vector3f();
        Vector3f c = new Vector3f();
        Vector3f na = new Vector3f();
        Vector3f nb = new Vector3f();
        Vector3f nc = new Vector3f();
        Vector3f normal = new Vector3f();
        Vector3f ab = new Vector3f();
        Vector3f ac = new Vector3f();
        Vector3f bc = new Vector3f();
        Vector3f abn = new Vector3f();
        Vector3f acn = new Vector3f();
        Vector3f bcn = new Vector3f();
        Vector3f n = new Vector3f();
        Vector3f crossTmp = new Vector3f();

        int numTriangles = indices.limit() / 3;
        for ( int i = 0; i < numTriangles; i++ ) {
            /*
             *  get the three edges of the triangle and the direction of the normal that points out of the mesh
             * the normal can point to then inner of the mesh it doesn't matter as long as they all point outside or inside
             */
            int v1 = indices.get( i * 3 );
            int v2 = indices.get( i * 3 + 1 );
            int v3 = indices.get( i * 3 + 2 );
            a.set( pts.get( v1 * 3 ), pts.get( v1 * 3 + 1 ), pts.get( v1 * 3 + 2 ) );
            b.set( pts.get( v2 * 3 ), pts.get( v2 * 3 + 1 ), pts.get( v2 * 3 + 2 ) );
            c.set( pts.get( v3 * 3 ), pts.get( v3 * 3 + 1 ), pts.get( v3 * 3 + 2 ) );
            na.set( normals.get( v1 * 3 ), normals.get( v1 * 3 + 1 ), normals.get( v1 * 3 + 2 ) );
            nb.set( normals.get( v2 * 3 ), normals.get( v2 * 3 + 1 ), normals.get( v2 * 3 + 2 ) );
            nc.set( normals.get( v3 * 3 ), normals.get( v3 * 3 + 1 ), normals.get( v3 * 3 + 2 ) );
            mesh.localToWorld( a, a );
            mesh.localToWorld( b, b );
            mesh.localToWorld( c, c );
            mesh.localToWorld( na, na );
            mesh.localToWorld( nb, nb );
            mesh.localToWorld( nc, nc );
            na.normalizeLocal();
            nb.normalizeLocal();
            nc.normalizeLocal();
            normal.set( na ).addLocal( nb ).addLocal( nc ).multLocal( 1 / 3f );

            /*
             * After having talked to my Math teacher we think that the results depends on the
             * precision of the calculus of the Area of the tri
             * and if one of the angle is close to 0 degree or 180 degree
             * there may be a problem!
             * so we calculate the three angle and take the max one to be the origine of calculus
             * if the max angle is close to 180 degree we must devide the triangle into 2 right-angled triangle
             */
            ab.set( b ).subtractLocal( a );
            ac.set( c ).subtractLocal( a );
            bc.set( c ).subtractLocal( b );

            abn.set( ab ).normalizeLocal();
            acn.set( ac ).normalizeLocal();
            bcn.set( bc ).normalizeLocal();

            /*
             * we must calculate the normal to the triangle which is not the one of any vertex
             * the accurancy of the result depends on the precision of the normal
             */
            n.set( acn ).cross( abn, n ).normalizeLocal();
            if ( n.dot( normal ) > 0 ) {
                n.multLocal( -1 );
            }

            float alphaA = abn.cross( acn, crossTmp ).length();
            float alphaB = abn.cross( bcn, crossTmp ).length();
            float alphaC = bcn.cross( acn, crossTmp ).length();

            float alphaMax = Math.max( Math.max( alphaA, alphaB ), alphaC );
            if ( alphaMax > 2.44 ) {
                Vector3f a1, b1, c1;
                if ( alphaMax == alphaA ) {
                    a1 = a;
                    b1 = b;
                    c1 = c;
                } else if ( alphaMax == alphaB ) {
                    a1 = b;
                    b1 = c;
                    c1 = a;
                } else {
                    a1 = c;
                    b1 = a;
                    c1 = b;
                }

                Vector3f ac1 = c1.subtract( a1 );
                Vector3f cb1 = b1.subtract( c1 );

                float t = -ac1.dot( cb1 ) / cb1.lengthSquared();

                Vector3f m = b1.mult( t ).add( c1.mult( 1 - t ) );


                float r[] = calculTri( m, a1, b1, n );

                s += r[0];
                v += r[1];

                xg += r[2];
                yg += r[3];
                zg += r[4];

                x2 += r[5];
                y2 += r[6];
                z2 += r[7];

                xy += r[8];
                xz += r[9];
                yz += r[10];


                r = calculTri( m, a1, c1, n );

                s += r[0];
                v += r[1];

                xg += r[2];
                yg += r[3];
                zg += r[4];

                x2 += r[5];
                y2 += r[6];
                z2 += r[7];

                xy += r[8];
                xz += r[9];
                yz += r[10];

            } else {
                if ( alphaMax == alphaA ) {
                    float r[] = calculTri( a, b, c, n );

                    s += r[0];
                    v += r[1];

                    xg += r[2];
                    yg += r[3];
                    zg += r[4];

                    x2 += r[5];
                    y2 += r[6];
                    z2 += r[7];

                    xy += r[8];
                    xz += r[9];
                    yz += r[10];
                } else if ( alphaMax == alphaB ) {
                    float r[] = calculTri( b, c, a, n );

                    s += r[0];
                    v += r[1];

                    xg += r[2];
                    yg += r[3];
                    zg += r[4];

                    x2 += r[5];
                    y2 += r[6];
                    z2 += r[7];

                    xy += r[8];
                    xz += r[9];
                    yz += r[10];
                } else {
                    float r[] = calculTri( c, a, b, n );

                    s += r[0];
                    v += r[1];

                    xg += r[2];
                    yg += r[3];
                    zg += r[4];

                    x2 += r[5];
                    y2 += r[6];
                    z2 += r[7];

                    xy += r[8];
                    xz += r[9];
                    yz += r[10];
                }

            }
        }

        inertia.m00 = y2 + z2;
        inertia.m10 = -xy;
        inertia.m20 = -xz;

        inertia.m01 = -xy;
        inertia.m11 = x2 + z2;
        inertia.m21 = -yz;

        inertia.m02 = -xz;
        inertia.m12 = -yz;
        inertia.m22 = x2 + y2;

        surface = s;
        volume = v;
        centerOfMass = new Vector3f( xg, yg, zg );
    }

    private float[] calculTri( Vector3f a, Vector3f b, Vector3f c, Vector3f n ) {

        Vector3f ab = b.subtract( a );
        Vector3f ac = c.subtract( a );

        /*
           * Then I transforme the triangle to a simplier tri (0,0,0) , (1,0,0) , (0,1,0)
           * to do the calculations
           */
        Matrix3f m = new Matrix3f();
        m.m00 = ab.x;
        m.m10 = ab.y;
        m.m20 = ab.z;

        m.m01 = ac.x;
        m.m11 = ac.y;
        m.m21 = ac.z;

        m.m02 = n.x;
        m.m12 = n.y;
        m.m22 = n.z;

        Vector3f a1 = m.invert().mult( a );
        float s = ab.cross( ac ).length() / 2;
        /*
           * the m.determinant() is there to go from the simplier tri to the real one
           * it shouldn't be equal to 0 as long as the triangle is a real one (defines a plan)
           */
        float x2 = x2z( a1, m ) * m.determinant() * n.z;
        float y2 = y2z( a1, m ) * m.determinant() * n.z;
        float z2 = z2x( a1, m ) * m.determinant() * n.x;

        float xy = xyz( a1, m ) * m.determinant() * n.z;
        float xz = xyz( a1, m ) * m.determinant() * n.y;
        float yz = xyz( a1, m ) * m.determinant() * n.x;

        float v = x( a1, m ) * m.determinant() * n.x;

        float xg = x2( a1, m ) * m.determinant() * n.x;
        float yg = y2( a1, m ) * m.determinant() * n.y;
        float zg = z2( a1, m ) * m.determinant() * n.z;

        return new float[]{s, v, xg, yg, zg, x2, y2, z2, xy, xz, yz};
    }


    /*
      *don't even try to understand the calculus under this point...
      *It comes from a symbolic calculator and has been tested
      *there should be no mistakes
      *if you are interested in how I came to theses formula I'll be glad
      *to give you more informations
      */
    private float x( Vector3f a, Matrix3f m ) {
        float x = (float) ( 1d / 3 * ( -m.m01 + 1d / 2 * m.m00 ) * ( Math.pow( ( a.y + 1 ), 3 ) - Math.pow( a.y, 3 ) ) + 1d / 2 * ( -m.m02 * a.z + m.m01 * ( a.y + 1 ) + 1d / 2
                * m.m00 * ( -2 * a.x - 2 * a.y - 2 ) ) * ( ( a.y + 1 ) * ( a.y + 1 ) - a.y * a.y ) + 1d / 2 * m.m00 * ( ( a.x + a.y + 1 ) * ( a.x + a.y + 1 ) - a.x * a.x )
                + m.m02 * a.z * ( a.y + 1 ) );
        return x;
    }


    private float x2( Vector3f a, Matrix3f m ) {
        float x2 = (float) ( 1d / 4 * ( -1d / 6 * m.m00 * m.m00 + 1d / 2 * m.m01 * m.m00 - 1d / 2 * m.m01 * m.m01 ) * ( Math.pow( ( a.y + 1 ), 4 ) - Math.pow( a.y, 4 ) )
                + 1d / 3 * ( 1d / 6 * m.m00 * m.m00 * ( 3 * a.x + 3 * a.y + 3 ) + 1d / 2 * m.m02 * a.z * m.m00 + 1d / 2 * m.m01 * m.m00 * ( -2 * a.x - 2 * a.y - 2 ) - m.m02 * a.z * m.m01
                + 1d / 2 * m.m01 * m.m01 * ( a.y + 1 ) ) * ( Math.pow( ( a.y + 1 ), 3 ) - Math.pow( a.y, 3 ) )
                + 1d / 2 * ( 1d / 6 * m.m00 * m.m00 * ( ( a.x + a.y + 1 ) * ( -2 * a.x - 2 * a.y - 2 ) - Math.pow( ( a.x + a.y + 1 ), 2 ) )
                + 1d / 2 * m.m02 * a.z * m.m00 * ( -2 * a.x - 2 * a.y - 2 ) + 1d / 2 * m.m01 * m.m00 * ( Math.pow( ( a.x + a.y + 1 ), 2 ) - Math.pow( a.x, 2 ) )
                - 1d / 2 * m.m02 * m.m02 * a.z * a.z + m.m02 * a.z * m.m01 * ( a.y + 1 ) ) * ( Math.pow( ( a.y + 1 ), 2 ) - Math.pow( a.y, 2 ) )
                + 1d / 6 * m.m00 * m.m00 * ( Math.pow( ( a.x + a.y + 1 ), 3 ) - Math.pow( a.x, 3 ) )
                + 1d / 2 * m.m02 * a.z * m.m00 * ( Math.pow( ( a.x + a.y + 1 ), 2 ) - Math.pow( a.x, 2 ) )
                + 1d / 2 * m.m02 * m.m02 * a.z * a.z * ( a.y + 1 ) );

        return x2;
    }

    private float y2( Vector3f a, Matrix3f m ) {
        float y2 = (float) ( 1d / 4 * ( -1d / 6 * m.m10 * m.m10 + 1d / 2 * m.m11 * m.m10 - 1d / 2 * m.m11 * m.m11 ) * ( Math.pow( ( a.y + 1 ), 4 ) - Math.pow( a.y, 4 ) )
                + 1d / 3 * ( 1d / 6 * m.m10 * m.m10 * ( 3 * a.x + 3 * a.y + 3 )
                + 1d / 2 * m.m12 * a.z * m.m10 + 1d / 2 * m.m11 * m.m10 * ( -2 * a.x - 2 * a.y - 2 ) - m.m12 * a.z * m.m11
                + 1d / 2 * m.m11 * m.m11 * ( a.y + 1 ) ) * ( Math.pow( ( a.y + 1 ), 3 ) - Math.pow( a.y, 3 ) )
                + 1d / 2 * ( 1d / 6 * m.m10 * m.m10 * ( ( a.x + a.y + 1 ) * ( -2 * a.x - 2 * a.y - 2 ) - Math.pow( ( a.x + a.y + 1 ), 2 ) )
                + 1d / 2 * m.m12 * a.z * m.m10 * ( -2 * a.x - 2 * a.y - 2 )
                + 1d / 2 * m.m11 * m.m10 * ( Math.pow( ( a.x + a.y + 1 ), 2 ) - Math.pow( a.x, 2 ) )
                - 1d / 2 * m.m12 * m.m12 * a.z * a.z + m.m12 * a.z * m.m11 * ( a.y + 1 ) ) * ( Math.pow( ( a.y + 1 ), 2 ) - Math.pow( a.y, 2 ) )
                + 1d / 6 * m.m10 * m.m10 * ( Math.pow( ( a.x + a.y + 1 ), 3 ) - Math.pow( a.x, 3 ) )
                + 1d / 2 * m.m12 * a.z * m.m10 * ( Math.pow( ( a.x + a.y + 1 ), 2 ) - Math.pow( a.x, 2 ) )
                + 1d / 2 * m.m12 * m.m12 * a.z * a.z * ( a.y + 1 ) );
        return y2;
    }

    private float z2( Vector3f a, Matrix3f m ) {
        float z2 = (float) ( 1d / 4 * ( -1d / 6 * m.m20 * m.m20 + 1d / 2 * m.m21 * m.m20 - 1d / 2 * m.m21 * m.m21 ) * ( Math.pow( ( a.y + 1 ), 4 ) - Math.pow( a.y, 4 ) )
                + 1d / 3 * ( 1d / 6 * m.m20 * m.m20 * ( 3 * a.x + 3 * a.y + 3 ) + 1d / 2 * m.m22 * a.z * m.m20
                + 1d / 2 * m.m21 * m.m20 * ( -2 * a.x - 2 * a.y - 2 ) - m.m22 * a.z * m.m21
                + 1d / 2 * m.m21 * m.m21 * ( a.y + 1 ) ) * ( Math.pow( ( a.y + 1 ), 3 ) - Math.pow( a.y, 3 ) )
                + 1d / 2 * ( 1d / 6 * m.m20 * m.m20 * ( ( a.x + a.y + 1 ) * ( -2 * a.x - 2 * a.y - 2 ) - Math.pow( ( a.x + a.y + 1 ), 2 ) )
                + 1d / 2 * m.m22 * a.z * m.m20 * ( -2 * a.x - 2 * a.y - 2 )
                + 1d / 2 * m.m21 * m.m20 * ( Math.pow( ( a.x + a.y + 1 ), 2 ) - Math.pow( a.x, 2 ) )
                - 1d / 2 * m.m22 * m.m22 * a.z * a.z + m.m22 * a.z * m.m21 * ( a.y + 1 ) ) * ( Math.pow( ( a.y + 1 ), 2 ) - Math.pow( a.y, 2 ) )
                + 1d / 6 * m.m20 * m.m20 * ( Math.pow( ( a.x + a.y + 1 ), 3 ) - Math.pow( a.x, 3 ) ) + 1d / 2 * m.m22 * a.z * m.m20 * ( Math.pow( ( a.x + a.y + 1 ), 2 ) - Math.pow( a.x, 2 ) )
                + 1d / 2 * m.m22 * m.m22 * a.z * a.z * ( a.y + 1 ) );
        return z2;
    }


    private float x2z( Vector3f a, Matrix3f m ) {

        float x2z = (float) ( 1d / 5 * ( -2d / 3 * m.m01 * m.m00 * m.m20 - 1d / 3 * m.m00 * m.m00 * m.m21 + 1d / 4 * m.m00 * m.m00 * m.m20
                + 1d / 2 * m.m01 * m.m01 * m.m20 + m.m01 * m.m00 * m.m21 - m.m01 * m.m01 * m.m21 ) * ( Math.pow( ( a.y + 1 ), 5 ) - Math.pow( a.y, 5 ) )
                + 1d / 4 * ( m.m02 * a.z * m.m01 * m.m20 + m.m02 * a.z * m.m00 * m.m21 + m.m01 * m.m00 * m.m22 * a.z
                + 1d / 2 * ( m.m01 * m.m01 * m.m20 + 2 * m.m01 * m.m00 * m.m21 ) * ( -2 * a.x - 2 * a.y - 2 )
                - 2d / 3 * m.m02 * a.z * m.m00 * m.m20 - 1d / 3 * m.m00 * m.m00 * m.m22 * a.z + 1d / 3 * ( 2 * m.m01 * m.m00 * m.m20 + m.m00 * m.m00 * m.m21 ) * ( 3 * a.x + 3 * a.y + 3 )
                - 2 * m.m02 * a.z * m.m01 * m.m21 - m.m01 * m.m01 * m.m22 * a.z + m.m01 * m.m01 * m.m21 * ( a.y + 1 )
                + 1d / 4 * m.m00 * m.m00 * m.m20 * ( -4 * a.x - 4 * a.y - 4 ) ) * ( Math.pow( ( a.y + 1 ), 4 ) - Math.pow( a.y, 4 ) )
                + 1d / 3 * ( -m.m02 * m.m02 * Math.pow( a.z, 2 ) * m.m21 - 2 * m.m02 * Math.pow( a.z, 2 ) * m.m01 * m.m22
                + ( 2 * m.m02 * a.z * m.m01 * m.m21 + m.m01 * m.m01 * m.m22 * a.z ) * ( a.y + 1 )
                + 1d / 2 * m.m02 * m.m02 * Math.pow( a.z, 2 ) * m.m20 + m.m02 * Math.pow( a.z, 2 ) * m.m00 * m.m22
                + 1d / 2 * ( 2 * m.m02 * a.z * m.m01 * m.m20 + 2 * m.m02 * a.z * m.m00 * m.m21 + 2 * m.m01 * m.m00 * m.m22 * a.z ) * ( -2 * a.x - 2 * a.y - 2 )
                + 1d / 2 * ( m.m01 * m.m01 * m.m20 + 2 * m.m01 * m.m00 * m.m21 ) * ( Math.pow( ( a.x + a.y + 1 ), 2 ) - Math.pow( a.x, 2 ) )
                + 1d / 3 * ( 2 * m.m02 * a.z * m.m00 * m.m20 + m.m00 * m.m00 * m.m22 * a.z ) * ( 3 * a.x + 3 * a.y + 3 )
                + 1d / 3 * ( 2 * m.m01 * m.m00 * m.m20 + m.m00 * m.m00 * m.m21 ) * ( ( a.x + a.y + 1 ) * ( -2 * a.x - 2 * a.y - 2 ) - Math.pow( ( a.x + a.y + 1 ), 2 ) )
                + 1d / 4 * m.m00 * m.m00 * m.m20 * ( 2 * Math.pow( ( a.x + a.y + 1 ), 2 ) + Math.pow( ( -2 * a.x - 2 * a.y - 2 ), 2 ) ) ) * ( Math.pow( ( a.y + 1 ), 3 ) - Math.pow( a.y, 3 ) )
                + 1d / 2 * ( -m.m02 * m.m02 * Math.pow( a.z, 3 ) * m.m22 + ( m.m02 * m.m02 * Math.pow( a.z, 2 ) * m.m21 + 2 * m.m02 * Math.pow( a.z, 2 ) * m.m01 * m.m22 ) * ( a.y + 1 )
                + 1d / 2 * m.m00 * m.m00 * m.m20 * Math.pow( ( a.x + a.y + 1 ), 2 ) * ( -2 * a.x - 2 * a.y - 2 )
                + 1d / 2 * ( m.m02 * m.m02 * Math.pow( a.z, 2 ) * m.m20 + 2 * m.m02 * Math.pow( a.z, 2 ) * m.m00 * m.m22 ) * ( -2 * a.x - 2 * a.y - 2 )
                + 1d / 2 * ( 2 * m.m02 * a.z * m.m01 * m.m20 + 2 * m.m02 * a.z * m.m00 * m.m21 + 2 * m.m01 * m.m00 * m.m22 * a.z ) * ( Math.pow( ( a.x + a.y + 1 ), 2 ) - Math.pow( a.x, 2 ) )
                + 1d / 3 * ( 2 * m.m02 * a.z * m.m00 * m.m20 + m.m00 * m.m00 * m.m22 * a.z ) * ( ( a.x + a.y + 1 ) * ( -2 * a.x - 2 * a.y - 2 ) - Math.pow( ( a.x + a.y + 1 ), 2 ) )
                + 1d / 3 * ( 2 * m.m01 * m.m00 * m.m20 + m.m00 * m.m00 * m.m21 ) * ( Math.pow( ( a.x + a.y + 1 ), 3 ) - Math.pow( a.x, 3 ) ) ) * ( Math.pow( ( a.y + 1 ), 2 ) - Math.pow( a.y, 2 ) )
                + 1d / 4 * m.m00 * m.m00 * m.m20 * ( Math.pow( ( a.x + a.y + 1 ), 4 ) - Math.pow( a.x, 4 ) )
                + 1d / 3 * ( 2 * m.m02 * a.z * m.m00 * m.m20 + m.m00 * m.m00 * m.m22 * a.z ) * ( Math.pow( ( a.x + a.y + 1 ), 3 ) - Math.pow( a.x, 3 ) ) + m.m02 * m.m02 * Math.pow( a.z, 3 ) * m.m22 * ( a.y + 1 )
                + 1d / 2 * ( m.m02 * m.m02 * Math.pow( a.z, 2 ) * m.m20 + 2 * m.m02 * Math.pow( a.z, 2 ) * m.m00 * m.m22 ) * ( Math.pow( ( a.x + a.y + 1 ), 2 ) - Math.pow( a.x, 2 ) ) );
        return x2z;
    }

    private float y2z( Vector3f a, Matrix3f m ) {

        float y2z = (float) ( 1d / 5 * ( -2d / 3 * m.m11 * m.m10 * m.m20 - 1d / 3 * m.m10 * m.m10 * m.m21
                + 1d / 4 * m.m10 * m.m10 * m.m20 + 1d / 2 * m.m11 * m.m11 * m.m20 + m.m11 * m.m10 * m.m21 - m.m11 * m.m11 * m.m21 ) * ( Math.pow( ( a.y + 1 ), 5 ) - Math.pow( a.y, 5 ) )
                + 1d / 4 * ( m.m12 * a.z * m.m11 * m.m20 + m.m12 * a.z * m.m10 * m.m21 + m.m11 * m.m10 * m.m22 * a.z
                + 1d / 2 * ( m.m11 * m.m11 * m.m20 + 2 * m.m11 * m.m10 * m.m21 ) * ( -2 * a.x - 2 * a.y - 2 ) - 2d / 3 * m.m12 * a.z * m.m10 * m.m20
                - 1d / 3 * m.m10 * m.m10 * m.m22 * a.z + 1d / 3 * ( 2 * m.m11 * m.m10 * m.m20 + m.m10 * m.m10 * m.m21 ) * ( 3 * a.x + 3 * a.y + 3 )
                - 2 * m.m12 * a.z * m.m11 * m.m21 - m.m11 * m.m11 * m.m22 * a.z + m.m11 * m.m11 * m.m21 * ( a.y + 1 )
                + 1d / 4 * m.m10 * m.m10 * m.m20 * ( -4 * a.x - 4 * a.y - 4 ) ) * ( Math.pow( ( a.y + 1 ), 4 ) - Math.pow( a.y, 4 ) )
                + 1d / 3 * ( -m.m12 * m.m12 * Math.pow( a.z, 2 ) * m.m21 - 2 * m.m12 * Math.pow( a.z, 2 ) * m.m11 * m.m22
                + ( 2 * m.m12 * a.z * m.m11 * m.m21 + m.m11 * m.m11 * m.m22 * a.z ) * ( a.y + 1 ) + 1d / 2 * m.m12 * m.m12 * Math.pow( a.z, 2 ) * m.m20
                + m.m12 * Math.pow( a.z, 2 ) * m.m10 * m.m22 + 1d / 2 * ( 2 * m.m12 * a.z * m.m11 * m.m20
                + 2 * m.m12 * a.z * m.m10 * m.m21 + 2 * m.m11 * m.m10 * m.m22 * a.z ) * ( -2 * a.x - 2 * a.y - 2 )
                + 1d / 2 * ( m.m11 * m.m11 * m.m20 + 2 * m.m11 * m.m10 * m.m21 ) * ( Math.pow( ( a.x + a.y + 1 ), 2 ) - Math.pow( a.x, 2 ) )
                + 1d / 3 * ( 2 * m.m12 * a.z * m.m10 * m.m20 + m.m10 * m.m10 * m.m22 * a.z ) * ( 3 * a.x + 3 * a.y + 3 )
                + 1d / 3 * ( 2 * m.m11 * m.m10 * m.m20 + m.m10 * m.m10 * m.m21 ) * ( ( a.x + a.y + 1 ) * ( -2 * a.x - 2 * a.y - 2 ) - Math.pow( ( a.x + a.y + 1 ), 2 ) )
                + 1d / 4 * m.m10 * m.m10 * m.m20 * ( 2 * Math.pow( ( a.x + a.y + 1 ), 2 ) + Math.pow( ( -2 * a.x - 2 * a.y - 2 ), 2 ) ) ) * ( Math.pow( ( a.y + 1 ), 3 ) - Math.pow( a.y, 3 ) )
                + 1d / 2 * ( -m.m12 * m.m12 * Math.pow( a.z, 3 ) * m.m22 + ( m.m12 * m.m12 * Math.pow( a.z, 2 ) * m.m21 + 2 * m.m12 * Math.pow( a.z, 2 ) * m.m11 * m.m22 ) * ( a.y + 1 )
                + 1d / 2 * m.m10 * m.m10 * m.m20 * Math.pow( ( a.x + a.y + 1 ), 2 ) * ( -2 * a.x - 2 * a.y - 2 )
                + 1d / 2 * ( m.m12 * m.m12 * Math.pow( a.z, 2 ) * m.m20 + 2 * m.m12 * Math.pow( a.z, 2 ) * m.m10 * m.m22 ) * ( -2 * a.x - 2 * a.y - 2 )
                + 1d / 2 * ( 2 * m.m12 * a.z * m.m11 * m.m20 + 2 * m.m12 * a.z * m.m10 * m.m21 + 2 * m.m11 * m.m10 * m.m22 * a.z ) * ( Math.pow( ( a.x + a.y + 1 ), 2 ) - Math.pow( a.x, 2 ) )
                + 1d / 3 * ( 2 * m.m12 * a.z * m.m10 * m.m20 + m.m10 * m.m10 * m.m22 * a.z ) * ( ( a.x + a.y + 1 ) * ( -2 * a.x - 2 * a.y - 2 ) - Math.pow( ( a.x + a.y + 1 ), 2 ) )
                + 1d / 3 * ( 2 * m.m11 * m.m10 * m.m20 + m.m10 * m.m10 * m.m21 ) * ( Math.pow( ( a.x + a.y + 1 ), 3 ) - Math.pow( a.x, 3 ) ) ) * ( Math.pow( ( a.y + 1 ), 2 ) - Math.pow( a.y, 2 ) )
                + 1d / 4 * m.m10 * m.m10 * m.m20 * ( Math.pow( ( a.x + a.y + 1 ), 4 ) - Math.pow( a.x, 4 ) )
                + 1d / 3 * ( 2 * m.m12 * a.z * m.m10 * m.m20 + m.m10 * m.m10 * m.m22 * a.z ) * ( Math.pow( ( a.x + a.y + 1 ), 3 ) - Math.pow( a.x, 3 ) ) + m.m12 * m.m12 * Math.pow( a.z, 3 ) * m.m22 * ( a.y + 1 )
                + 1d / 2 * ( m.m12 * m.m12 * Math.pow( a.z, 2 ) * m.m20 + 2 * m.m12 * Math.pow( a.z, 2 ) * m.m10 * m.m22 ) * ( Math.pow( ( a.x + a.y + 1 ), 2 ) - Math.pow( a.x, 2 ) ) );
        return y2z;
    }

    private float z2x( Vector3f a, Matrix3f m ) {

        float z2x = (float) ( 1d / 5 * ( -2d / 3 * m.m21 * m.m20 * m.m00 - 1d / 3 * m.m20 * m.m20 * m.m01
                + 1d / 4 * m.m20 * m.m20 * m.m00 + 1d / 2 * m.m21 * m.m21 * m.m00 + m.m21 * m.m20 * m.m01 - m.m21 * m.m21 * m.m01 ) * ( Math.pow( ( a.y + 1 ), 5 ) - Math.pow( a.y, 5 ) )
                + 1d / 4 * ( m.m22 * a.z * m.m21 * m.m00 + m.m22 * a.z * m.m20 * m.m01 + m.m21 * m.m20 * m.m02 * a.z
                + 1d / 2 * ( m.m21 * m.m21 * m.m00 + 2 * m.m21 * m.m20 * m.m01 ) * ( -2 * a.x - 2 * a.y - 2 ) - 2d / 3 * m.m22 * a.z * m.m20 * m.m00
                - 1d / 3 * m.m20 * m.m20 * m.m02 * a.z + 1d / 3 * ( 2 * m.m21 * m.m20 * m.m00 + m.m20 * m.m20 * m.m01 ) * ( 3 * a.x + 3 * a.y + 3 )
                - 2 * m.m22 * a.z * m.m21 * m.m01 - m.m21 * m.m21 * m.m02 * a.z + m.m21 * m.m21 * m.m01 * ( a.y + 1 )
                + 1d / 4 * m.m20 * m.m20 * m.m00 * ( -4 * a.x - 4 * a.y - 4 ) ) * ( Math.pow( ( a.y + 1 ), 4 ) - Math.pow( a.y, 4 ) )
                + 1d / 3 * ( -m.m22 * m.m22 * Math.pow( a.z, 2 ) * m.m01 - 2 * m.m22 * Math.pow( a.z, 2 ) * m.m21 * m.m02 + ( 2 * m.m22 * a.z * m.m21 * m.m01 + m.m21 * m.m21 * m.m02 * a.z ) * ( a.y + 1 )
                + 1d / 2 * m.m22 * m.m22 * Math.pow( a.z, 2 ) * m.m00 + m.m22 * Math.pow( a.z, 2 ) * m.m20 * m.m02
                + 1d / 2 * ( 2 * m.m22 * a.z * m.m21 * m.m00 + 2 * m.m22 * a.z * m.m20 * m.m01 + 2 * m.m21 * m.m20 * m.m02 * a.z ) * ( -2 * a.x - 2 * a.y - 2 )
                + 1d / 2 * ( m.m21 * m.m21 * m.m00 + 2 * m.m21 * m.m20 * m.m01 ) * ( Math.pow( ( a.x + a.y + 1 ), 2 ) - Math.pow( a.x, 2 ) )
                + 1d / 3 * ( 2 * m.m22 * a.z * m.m20 * m.m00 + m.m20 * m.m20 * m.m02 * a.z ) * ( 3 * a.x + 3 * a.y + 3 )
                + 1d / 3 * ( 2 * m.m21 * m.m20 * m.m00 + m.m20 * m.m20 * m.m01 ) * ( ( a.x + a.y + 1 ) * ( -2 * a.x - 2 * a.y - 2 ) - Math.pow( ( a.x + a.y + 1 ), 2 ) )
                + 1d / 4 * m.m20 * m.m20 * m.m00 * ( 2 * Math.pow( ( a.x + a.y + 1 ), 2 ) + Math.pow( ( -2 * a.x - 2 * a.y - 2 ), 2 ) ) ) * ( Math.pow( ( a.y + 1 ), 3 ) - Math.pow( a.y, 3 ) )
                + 1d / 2 * ( -m.m22 * m.m22 * Math.pow( a.z, 3 ) * m.m02 + ( m.m22 * m.m22 * Math.pow( a.z, 2 ) * m.m01 + 2 * m.m22 * Math.pow( a.z, 2 ) * m.m21 * m.m02 ) * ( a.y + 1 )
                + 1d / 2 * m.m20 * m.m20 * m.m00 * Math.pow( ( a.x + a.y + 1 ), 2 ) * ( -2 * a.x - 2 * a.y - 2 )
                + 1d / 2 * ( m.m22 * m.m22 * Math.pow( a.z, 2 ) * m.m00 + 2 * m.m22 * Math.pow( a.z, 2 ) * m.m20 * m.m02 ) * ( -2 * a.x - 2 * a.y - 2 )
                + 1d / 2 * ( 2 * m.m22 * a.z * m.m21 * m.m00 + 2 * m.m22 * a.z * m.m20 * m.m01 + 2 * m.m21 * m.m20 * m.m02 * a.z ) * ( Math.pow( ( a.x + a.y + 1 ), 2 ) - Math.pow( a.x, 2 ) )
                + 1d / 3 * ( 2 * m.m22 * a.z * m.m20 * m.m00 + m.m20 * m.m20 * m.m02 * a.z ) * ( ( a.x + a.y + 1 ) * ( -2 * a.x - 2 * a.y - 2 ) - Math.pow( ( a.x + a.y + 1 ), 2 ) )
                + 1d / 3 * ( 2 * m.m21 * m.m20 * m.m00 + m.m20 * m.m20 * m.m01 ) * ( Math.pow( ( a.x + a.y + 1 ), 3 ) - Math.pow( a.x, 3 ) ) ) * ( Math.pow( ( a.y + 1 ), 2 ) - Math.pow( a.y, 2 ) )
                + 1d / 4 * m.m20 * m.m20 * m.m00 * ( Math.pow( ( a.x + a.y + 1 ), 4 ) - Math.pow( a.x, 4 ) )
                + 1d / 3 * ( 2 * m.m22 * a.z * m.m20 * m.m00 + m.m20 * m.m20 * m.m02 * a.z ) * ( Math.pow( ( a.x + a.y + 1 ), 3 ) - Math.pow( a.x, 3 ) ) + m.m22 * m.m22 * Math.pow( a.z, 3 ) * m.m02 * ( a.y + 1 )
                + 1d / 2 * ( m.m22 * m.m22 * Math.pow( a.z, 2 ) * m.m00 + 2 * m.m22 * Math.pow( a.z, 2 ) * m.m20 * m.m02 ) * ( Math.pow( ( a.x + a.y + 1 ), 2 ) - Math.pow( a.x, 2 ) ) );
        return z2x;
    }

    private float xyz( Vector3f a, Matrix3f m ) {

        float xyz = (float) ( 1d / 5 * ( -1d / 3 * ( m.m11 * m.m20 + m.m10 * m.m21 ) * m.m00
                - 1d / 3 * m.m10 * m.m20 * m.m01 + 1d / 4 * m.m10 * m.m20 * m.m00
                + 1d / 2 * m.m11 * m.m21 * m.m00 + 1d / 2 * ( m.m11 * m.m20 + m.m10 * m.m21 ) * m.m01 - m.m11 * m.m21 * m.m01 ) * ( Math.pow( ( a.y + 1 ), 5 ) - Math.pow( a.y, 5 ) )
                + 1d / 4 * ( 1d / 2 * ( m.m12 * a.z * m.m21 + m.m11 * m.m22 * a.z ) * m.m00 + 1d / 2 * ( m.m12 * a.z * m.m20 + m.m10 * m.m22 * a.z ) * m.m01
                + 1d / 2 * ( m.m11 * m.m20 + m.m10 * m.m21 ) * m.m02 * a.z + 1d / 2 * ( m.m11 * m.m21 * m.m00 + ( m.m11 * m.m20 + m.m10 * m.m21 ) * m.m01 ) * ( -2 * a.x - 2 * a.y - 2 )
                - 1d / 3 * ( m.m12 * a.z * m.m20 + m.m10 * m.m22 * a.z ) * m.m00 - 1d / 3 * m.m10 * m.m20 * m.m02 * a.z
                + 1d / 3 * ( ( m.m11 * m.m20 + m.m10 * m.m21 ) * m.m00 + m.m10 * m.m20 * m.m01 ) * ( 3 * a.x + 3 * a.y + 3 )
                - ( m.m12 * a.z * m.m21 + m.m11 * m.m22 * a.z ) * m.m01 - m.m11 * m.m21 * m.m02 * a.z + m.m11 * m.m21 * m.m01 * ( a.y + 1 )
                + 1d / 4 * m.m10 * m.m20 * m.m00 * ( -4 * a.x - 4 * a.y - 4 ) ) * ( Math.pow( ( a.y + 1 ), 4 ) - Math.pow( a.y, 4 ) )
                + 1d / 3 * ( -m.m12 * Math.pow( a.z, 2 ) * m.m22 * m.m01 - ( m.m12 * a.z * m.m21 + m.m11 * m.m22 * a.z ) * m.m02 * a.z
                + ( ( m.m12 * a.z * m.m21 + m.m11 * m.m22 * a.z ) * m.m01 + m.m11 * m.m21 * m.m02 * a.z ) * ( a.y + 1 )
                + 1d / 2 * m.m12 * Math.pow( a.z, 2 ) * m.m22 * m.m00 + 1d / 2 * ( m.m12 * a.z * m.m20 + m.m10 * m.m22 * a.z ) * m.m02 * a.z
                + 1d / 2 * ( ( m.m12 * a.z * m.m21 + m.m11 * m.m22 * a.z ) * m.m00 + ( m.m12 * a.z * m.m20 + m.m10 * m.m22 * a.z ) * m.m01 + ( m.m11 * m.m20 + m.m10 * m.m21 ) * m.m02 * a.z ) * ( -2 * a.x - 2 * a.y - 2 )
                + 1d / 2 * ( m.m11 * m.m21 * m.m00 + ( m.m11 * m.m20 + m.m10 * m.m21 ) * m.m01 ) * ( Math.pow( ( a.x + a.y + 1 ), 2 ) - Math.pow( a.x, 2 ) )
                + 1d / 3 * ( ( m.m12 * a.z * m.m20 + m.m10 * m.m22 * a.z ) * m.m00 + m.m10 * m.m20 * m.m02 * a.z ) * ( 3 * a.x + 3 * a.y + 3 )
                + 1d / 3 * ( ( m.m11 * m.m20 + m.m10 * m.m21 ) * m.m00 + m.m10 * m.m20 * m.m01 ) * ( ( a.x + a.y + 1 ) * ( -2 * a.x - 2 * a.y - 2 ) - Math.pow( ( a.x + a.y + 1 ), 2 ) )
                + 1d / 4 * m.m10 * m.m20 * m.m00 * ( 2 * Math.pow( ( a.x + a.y + 1 ), 2 ) + Math.pow( ( -2 * a.x - 2 * a.y - 2 ), 2 ) ) ) * ( Math.pow( ( a.y + 1 ), 3 ) - Math.pow( a.y, 3 ) )
                + 1d / 2 * ( -m.m12 * Math.pow( a.z, 3 ) * m.m22 * m.m02 + ( m.m12 * Math.pow( a.z, 2 ) * m.m22 * m.m01 + ( m.m12 * a.z * m.m21 + m.m11 * m.m22 * a.z ) * m.m02 * a.z ) * ( a.y + 1 )
                + 1d / 2 * m.m10 * m.m20 * m.m00 * Math.pow( ( a.x + a.y + 1 ), 2 ) * ( -2 * a.x - 2 * a.y - 2 )
                + 1d / 2 * ( m.m12 * Math.pow( a.z, 2 ) * m.m22 * m.m00 + ( m.m12 * a.z * m.m20 + m.m10 * m.m22 * a.z ) * m.m02 * a.z ) * ( -2 * a.x - 2 * a.y - 2 )
                + 1d / 2 * ( ( m.m12 * a.z * m.m21 + m.m11 * m.m22 * a.z ) * m.m00 + ( m.m12 * a.z * m.m20 + m.m10 * m.m22 * a.z ) * m.m01
                + ( m.m11 * m.m20 + m.m10 * m.m21 ) * m.m02 * a.z ) * ( Math.pow( ( a.x + a.y + 1 ), 2 ) - Math.pow( a.x, 2 ) )
                + 1d / 3 * ( ( m.m12 * a.z * m.m20 + m.m10 * m.m22 * a.z ) * m.m00 + m.m10 * m.m20 * m.m02 * a.z ) * ( ( a.x + a.y + 1 ) * ( -2 * a.x - 2 * a.y - 2 ) - Math.pow( ( a.x + a.y + 1 ), 2 ) )
                + 1d / 3 * ( ( m.m11 * m.m20 + m.m10 * m.m21 ) * m.m00 + m.m10 * m.m20 * m.m01 ) * ( Math.pow( ( a.x + a.y + 1 ), 3 ) - Math.pow( a.x, 3 ) ) ) * ( Math.pow( ( a.y + 1 ), 2 ) - Math.pow( a.y, 2 ) )
                + 1d / 4 * m.m10 * m.m20 * m.m00 * ( Math.pow( ( a.x + a.y + 1 ), 4 ) - Math.pow( a.x, 4 ) )
                + 1d / 3 * ( ( m.m12 * a.z * m.m20 + m.m10 * m.m22 * a.z ) * m.m00 + m.m10 * m.m20 * m.m02 * a.z ) * ( Math.pow( ( a.x + a.y + 1 ), 3 ) - Math.pow( a.x, 3 ) )
                + m.m12 * Math.pow( a.z, 3 ) * m.m22 * m.m02 * ( a.y + 1 ) + 1d / 2 * ( m.m12 * Math.pow( a.z, 2 ) * m.m22 * m.m00 + ( m.m12 * a.z * m.m20 + m.m10 * m.m22 * a.z ) * m.m02 * a.z ) * ( Math.pow( ( a.x + a.y + 1 ), 2 ) - Math.pow( a.x, 2 ) ) );
        return xyz;
    }


}
