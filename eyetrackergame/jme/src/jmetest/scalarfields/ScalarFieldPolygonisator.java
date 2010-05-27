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

package jmetest.scalarfields;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.util.geom.BufferUtils;



/**
 * Based on Paul Bourke's code from "Polygonising a Scalar Field Using Tetrahedrons"
 * http://local.wasp.uwa.edu.au/~pbourke/geometry/polygonise/
 *
 * @author Daniel Gronau
 * @author Irrisor (replaced array lists by using buffers)
 * @author basixs (replaced creation of objects with global 'calc' vectors, for better performance)
 */
public class ScalarFieldPolygonisator {

    private final Vector3f boxSize;
    private final float cubeSize;
    private final float[][][] values;
    private final Vector3f[] cellPoints = new Vector3f[]{
        new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f(),
        new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f()
    };
    private final int[] cellIso = new int[ 8 ];
    private final Map<Edge, Integer> interpol = new HashMap<Edge, Integer>( 5000 );
    private final Edge tmpEdge = new Edge();
    private final int xSize;
    private final int ySize;
    private final int zSize;
    private final ScalarField field;
    private FloatBuffer vertexBuffer;
    private FloatBuffer normalBuffer;
    private IntBuffer indexBuffer;
    private FloatBuffer textureBuffer;
    private FloatBuffer colorBuffer;
    //
    // Temporary calc vars
    private final ColorRGBA calcColor = new ColorRGBA();
    private final Vector3f calcVector = new Vector3f();
    private final Vector3f calcVector1 = new Vector3f();
    private final Vector2f calcVector2f = new Vector2f();

    public ScalarFieldPolygonisator( Vector3f boxSize, float cubeSize, final ScalarField field ) {
        this.boxSize = boxSize;
        this.cubeSize = cubeSize;
        xSize = (int) Math.ceil( 2 * boxSize.x / cubeSize );
        ySize = (int) Math.ceil( 2 * boxSize.y / cubeSize );
        zSize = (int) Math.ceil( 2 * boxSize.z / cubeSize );
        values = new float[ xSize + 1 ][ ySize + 1 ][ zSize + 1 ];
        this.field = field;
    }

    public void calculate( TriMesh mesh, float iso ) {

        vertexBuffer = mesh.getVertexBuffer();
        if( vertexBuffer == null ){
            vertexBuffer = BufferUtils.createFloatBuffer( 16000 );
        } else{
            vertexBuffer.clear();
        }

        normalBuffer = mesh.getNormalBuffer();
        if( normalBuffer == null ){
            normalBuffer = BufferUtils.createFloatBuffer( 16000 );
        } else{
            normalBuffer.clear();
        }

        colorBuffer = mesh.getColorBuffer();
        if( colorBuffer == null ){
            colorBuffer = BufferUtils.createFloatBuffer( 16000 );
        } else{
            colorBuffer.clear();
        }

        final TexCoords texCoords = mesh.getTextureCoords( 0 );
        if( texCoords != null ){
            textureBuffer = texCoords.coords;
        } else{
            textureBuffer = null;
        }
        if( textureBuffer == null ){
            textureBuffer = BufferUtils.createFloatBuffer( 16000 );
        } else{
            textureBuffer.clear();
        }

        indexBuffer = mesh.getIndexBuffer();
        if( indexBuffer == null ){
            indexBuffer = BufferUtils.createIntBuffer( 16000 );
        } else{
            indexBuffer.clear();
        }
        clearField();

        populateFieldArray( field );

        calculateCells( iso );

        indexBuffer.flip();
        mesh.setIndexBuffer( indexBuffer );
        vertexBuffer.flip();
        mesh.setVertexBuffer( vertexBuffer );
        normalBuffer.flip();
        mesh.setNormalBuffer( normalBuffer );
        colorBuffer.flip();
        mesh.setColorBuffer( colorBuffer );

        mesh.setTriangleQuantity( indexBuffer.limit() / 3 );
        mesh.setVertexCount( vertexBuffer.limit() / 3 );

        textureBuffer.flip();
        mesh.setTextureCoords( new TexCoords( textureBuffer ) );
    }

    protected void clearField() {
        interpol.clear();

        for( int x = 0; x < xSize; x++ ){
            for( int y = 0; y < ySize; y++ ){
                for( int z = 0; z < zSize; z++ ){
                    values[x][y][z] = 0;
                }
            }
        }
    }

    protected void gridToWorld( final int x, final int y, final int z, final Vector3f store ) {
        store.setX( x * cubeSize - boxSize.x );
        store.setY( y * cubeSize - boxSize.y );
        store.setZ( z * cubeSize - boxSize.z );
    }

    protected void worldToGrid( final Vector3f world, final int[] store ) {
        worldToGrid( world.getX(), world.getY(), world.getZ(), store );
    }

    protected void worldToGrid( final float x, final float y, final float z, final int[] store ) {
        store[0] = new Float( ( x + boxSize.x ) / cubeSize + 0.5f ).intValue();
        store[1] = new Float( ( y + boxSize.y ) / cubeSize + 0.5f ).intValue();
        store[2] = new Float( ( z + boxSize.z ) / cubeSize + 0.5f ).intValue();
    }


    protected void calculateCells( float iso ) {
        for( int xk = 0; xk < xSize; xk++ ){
            for( int yk = 0; yk < ySize; yk++ ){
                for( int zk = 0; zk < zSize; zk++ ){
                    calculateCell( iso, xk, yk, zk );
                }
            }
        }
    }

    protected void populateFieldArray( ScalarField field ) {
        for( int x = 0; x < xSize; x++ ){
            for( int y = 0; y < ySize; y++ ){
                for( int z = 0; z < zSize; z++ ){
                    gridToWorld( x, y, z, calcVector );
                    values[x][y][z] = field.calculate( calcVector );
                }
            }
        }
    }

    protected int calculateCell( final float iso, final int xk, final int yk, final int zk ) {

        int bits = 0;

        cellIso[0] = values[xk][yk][zk] > iso ? 1 : 0;
        cellIso[1] = values[xk + 1][yk][zk] > iso ? 1 : 0;
        cellIso[2] = values[xk + 1][yk][zk + 1] > iso ? 1 : 0;
        cellIso[3] = values[xk][yk][zk + 1] > iso ? 1 : 0;
        cellIso[4] = values[xk][yk + 1][zk] > iso ? 1 : 0;
        cellIso[5] = values[xk + 1][yk + 1][zk] > iso ? 1 : 0;
        cellIso[6] = values[xk + 1][yk + 1][zk + 1] > iso ? 1 : 0;
        cellIso[7] = values[xk][yk + 1][zk + 1] > iso ? 1 : 0;

        for( int i = 0; i < cellIso.length; ++i ){
            bits |= cellIso[i] == 1 ? ( 1 << i ) : 0;
        }

        if( bits == 0 || bits == 255 ){
            return bits;
        }

        calcVector.x = xk * cubeSize - boxSize.x;
        calcVector.y = yk * cubeSize - boxSize.y;
        calcVector.z = zk * cubeSize - boxSize.z;
        calcVector1.x = calcVector.x + cubeSize;
        calcVector1.y = calcVector.y + cubeSize;
        calcVector1.z = calcVector.z + cubeSize;

        cellPoints[0].set( calcVector.x, calcVector.y, calcVector.z );
        cellPoints[1].set( calcVector1.x, calcVector.y, calcVector.z );
        cellPoints[2].set( calcVector1.x, calcVector.y, calcVector1.z );
        cellPoints[3].set( calcVector.x, calcVector.y, calcVector1.z );
        cellPoints[4].set( calcVector.x, calcVector1.y, calcVector.z );
        cellPoints[5].set( calcVector1.x, calcVector1.y, calcVector.z );
        cellPoints[6].set( calcVector1.x, calcVector1.y, calcVector1.z );
        cellPoints[7].set( calcVector.x, calcVector1.y, calcVector1.z );

        calculateTetra( iso, 0, 4, 7, 6, xk, yk, zk );
        calculateTetra( iso, 0, 4, 6, 5, xk, yk, zk );
        calculateTetra( iso, 0, 2, 6, 3, xk, yk, zk );
        calculateTetra( iso, 0, 1, 6, 2, xk, yk, zk );
        calculateTetra( iso, 0, 3, 6, 7, xk, yk, zk );
        calculateTetra( iso, 0, 1, 5, 6, xk, yk, zk );

        // return the cell triangle bits
        return bits;
    }

    private void calculateTetra( final float iso, final int v0, final int v1,
            final int v2, final int v3, final int xk, final int yk, final int zk ) {

        final int triindex = cellIso[v0] + cellIso[v1] * 2 + cellIso[v2] * 4 + cellIso[v3] * 8;

        /* Form the vertices of the triangles for each case */
        switch( triindex ){
            case 0x00:
            case 0x0F:
                break;
            case 0x0E:
                addIndex( interpolate( iso, v0, v3, xk, yk, zk ) );
                addIndex( interpolate( iso, v0, v2, xk, yk, zk ) );
                addIndex( interpolate( iso, v0, v1, xk, yk, zk ) );
                break;
            case 0x01:
                addIndex( interpolate( iso, v0, v2, xk, yk, zk ) );
                addIndex( interpolate( iso, v0, v3, xk, yk, zk ) );
                addIndex( interpolate( iso, v0, v1, xk, yk, zk ) );
                break;
            case 0x0D:
                addIndex( interpolate( iso, v1, v2, xk, yk, zk ) );
                addIndex( interpolate( iso, v1, v3, xk, yk, zk ) );
                addIndex( interpolate( iso, v0, v1, xk, yk, zk ) );
                break;
            case 0x02:
                addIndex( interpolate( iso, v1, v3, xk, yk, zk ) );
                addIndex( interpolate( iso, v1, v2, xk, yk, zk ) );
                addIndex( interpolate( iso, v0, v1, xk, yk, zk ) );
                break;
            case 0x0C: {
                int temp1 = interpolate( iso, v0, v2, xk, yk, zk );
                int temp2 = interpolate( iso, v1, v3, xk, yk, zk );
                addIndex( temp1 );
                addIndex( temp2 );
                addIndex( interpolate( iso, v0, v3, xk, yk, zk ) );
                addIndex( temp1 );
                addIndex( interpolate( iso, v1, v2, xk, yk, zk ) );
                addIndex( temp2 );
                break;
            }
            case 0x03: {
                int temp1 = interpolate( iso, v0, v2, xk, yk, zk );
                int temp2 = interpolate( iso, v1, v3, xk, yk, zk );
                addIndex( temp2 );
                addIndex( temp1 );
                addIndex( interpolate( iso, v0, v3, xk, yk, zk ) );
                addIndex( interpolate( iso, v1, v2, xk, yk, zk ) );
                addIndex( temp1 );
                addIndex( temp2 );
                break;
            }
            case 0x0B:
                addIndex( interpolate( iso, v2, v3, xk, yk, zk ) );
                addIndex( interpolate( iso, v1, v2, xk, yk, zk ) );
                addIndex( interpolate( iso, v0, v2, xk, yk, zk ) );
                break;
            case 0x04:
                addIndex( interpolate( iso, v1, v2, xk, yk, zk ) );
                addIndex( interpolate( iso, v2, v3, xk, yk, zk ) );
                addIndex( interpolate( iso, v0, v2, xk, yk, zk ) );
                break;
            case 0x0A: {
                int temp1 = interpolate( iso, v0, v1, xk, yk, zk );
                int temp2 = interpolate( iso, v2, v3, xk, yk, zk );
                addIndex( interpolate( iso, v0, v3, xk, yk, zk ) );
                addIndex( temp2 );
                addIndex( temp1 );
                addIndex( temp2 );
                addIndex( interpolate( iso, v1, v2, xk, yk, zk ) );
                addIndex( temp1 );
                break;
            }
            case 0x05: {
                int temp1 = interpolate( iso, v0, v1, xk, yk, zk );
                int temp2 = interpolate( iso, v2, v3, xk, yk, zk );
                addIndex( temp2 );
                addIndex( interpolate( iso, v0, v3, xk, yk, zk ) );
                addIndex( temp1 );
                addIndex( interpolate( iso, v1, v2, xk, yk, zk ) );
                addIndex( temp2 );
                addIndex( temp1 );
                break;
            }
            case 0x09: {
                int temp1 = interpolate( iso, v0, v1, xk, yk, zk );
                int temp2 = interpolate( iso, v2, v3, xk, yk, zk );
                addIndex( temp2 );
                addIndex( interpolate( iso, v1, v3, xk, yk, zk ) );
                addIndex( temp1 );
                addIndex( interpolate( iso, v0, v2, xk, yk, zk ) );
                addIndex( temp2 );
                addIndex( temp1 );
                break;
            }
            case 0x06: {
                int temp1 = interpolate( iso, v0, v1, xk, yk, zk );
                int temp2 = interpolate( iso, v2, v3, xk, yk, zk );
                addIndex( interpolate( iso, v1, v3, xk, yk, zk ) );
                addIndex( temp2 );
                addIndex( temp1 );
                addIndex( temp2 );
                addIndex( interpolate( iso, v0, v2, xk, yk, zk ) );
                addIndex( temp1 );
                break;
            }
            case 0x07:
                addIndex( interpolate( iso, v1, v3, xk, yk, zk ) );
                addIndex( interpolate( iso, v2, v3, xk, yk, zk ) );
                addIndex( interpolate( iso, v0, v3, xk, yk, zk ) );
                break;
            case 0x08:
                addIndex( interpolate( iso, v2, v3, xk, yk, zk ) );
                addIndex( interpolate( iso, v1, v3, xk, yk, zk ) );
                addIndex( interpolate( iso, v0, v3, xk, yk, zk ) );
                break;
        }
    }

    private void addIndex( int index ) {
        indexBuffer = enlargeIfNeeded( indexBuffer );
        indexBuffer.put( index );
    }

    private void addVertex( float x, float y, float z ) {
        vertexBuffer = enlargeIfNeeded( vertexBuffer );
        vertexBuffer.put( x ).put( y ).put( z );
    }

    private void addNormal( float x, float y, float z ) {
        normalBuffer = enlargeIfNeeded( normalBuffer );
        normalBuffer.put( x ).put( y ).put( z );
    }

    private void addColor( float r, float g, float b, float a ) {
        colorBuffer = enlargeIfNeeded( colorBuffer );
        colorBuffer.put( r ).put( g ).put( b ).put( a );
    }

    private void addTextureCoord( float x, float y ) {
        textureBuffer = enlargeIfNeeded( textureBuffer );
        textureBuffer.put( x ).put( y );
    }

    private IntBuffer enlargeIfNeeded( IntBuffer buffer ) {
        if( buffer.capacity() == buffer.position() ){
            final IntBuffer oldBuffer = buffer;
            buffer = BufferUtils.createIntBuffer( oldBuffer.capacity() * 2 );
            oldBuffer.flip();
            buffer.put( oldBuffer );
        }
        return buffer;
    }

    private FloatBuffer enlargeIfNeeded( FloatBuffer buffer ) {
        if( buffer.capacity() < buffer.position() + 4 ){
            final FloatBuffer oldBuffer = buffer;
            buffer = BufferUtils.createFloatBuffer( oldBuffer.capacity() * 2 );
            oldBuffer.flip();
            buffer.put( oldBuffer );
        }
        return buffer;
    }

    private int interpolate( float iso, int v1, int v2, int xk, int yk, int zk ) {
        if( v1 > v2 ){
            final int tmp = v2;
            v2 = v1;
            v1 = tmp;
        }
        switch( v1 ){
            default:
                tmpEdge.x1 = xk;
                tmpEdge.y1 = yk;
                tmpEdge.z1 = zk;
                break;
            case 1:
                tmpEdge.x1 = xk + 1;
                tmpEdge.y1 = yk;
                tmpEdge.z1 = zk;
                break;
            case 2:
                tmpEdge.x1 = xk + 1;
                tmpEdge.y1 = yk;
                tmpEdge.z1 = zk + 1;
                break;
            case 3:
                tmpEdge.x1 = xk;
                tmpEdge.y1 = yk;
                tmpEdge.z1 = zk + 1;
                break;
            case 4:
                tmpEdge.x1 = xk;
                tmpEdge.y1 = yk + 1;
                tmpEdge.z1 = zk;
                break;
            case 5:
                tmpEdge.x1 = xk + 1;
                tmpEdge.y1 = yk + 1;
                tmpEdge.z1 = zk;
                break;
            case 6:
                tmpEdge.x1 = xk + 1;
                tmpEdge.y1 = yk + 1;
                tmpEdge.z1 = zk + 1;
                break;
            case 7:
                tmpEdge.x1 = xk;
                tmpEdge.y1 = yk + 1;
                tmpEdge.z1 = zk + 1;
                break;
        }

        switch( v2 ){
            default:
                tmpEdge.x2 = xk;
                tmpEdge.y2 = yk;
                tmpEdge.z2 = zk;
                break;
            case 1:
                tmpEdge.x2 = xk + 1;
                tmpEdge.y2 = yk;
                tmpEdge.z2 = zk;
                break;
            case 2:
                tmpEdge.x2 = xk + 1;
                tmpEdge.y2 = yk;
                tmpEdge.z2 = zk + 1;
                break;
            case 3:
                tmpEdge.x2 = xk;
                tmpEdge.y2 = yk;
                tmpEdge.z2 = zk + 1;
                break;
            case 4:
                tmpEdge.x2 = xk;
                tmpEdge.y2 = yk + 1;
                tmpEdge.z2 = zk;
                break;
            case 5:
                tmpEdge.x2 = xk + 1;
                tmpEdge.y2 = yk + 1;
                tmpEdge.z2 = zk;
                break;
            case 6:
                tmpEdge.x2 = xk + 1;
                tmpEdge.y2 = yk + 1;
                tmpEdge.z2 = zk + 1;
                break;
            case 7:
                tmpEdge.x2 = xk;
                tmpEdge.y2 = yk + 1;
                tmpEdge.z2 = zk + 1;
                break;
        }

        final Integer index = interpol.get( tmpEdge );
        if( index != null ){
            return index;
        }
        float ratio = 0.5f;
        final float value1 = tmpEdge.getValue1( this );
        final float value2 = tmpEdge.getValue2( this );
        if( value1 != value2 ){
            ratio = ( value1 - iso ) / ( value1 - value2 );
        }

        final int currentVertexIndex = vertexBuffer.position() / 3;

        calcVector1.set( cellPoints[v2].mult( ratio ) );
        calcVector.set( cellPoints[v1] ).multLocal( 1 - ratio ).addLocal( calcVector1 );
        addVertex( calcVector.x, calcVector.y, calcVector.z );

        field.normal( calcVector, calcVector1 );
        addNormal( calcVector1.x, calcVector1.y, calcVector1.z );

        field.textureCoords( calcVector1, calcVector2f );
        addTextureCoord( calcVector2f.x, calcVector2f.y );

        field.color( calcVector, calcColor );
        addColor( calcColor.r, calcColor.g, calcColor.b, calcColor.a );

        interpol.put( new Edge( tmpEdge ), currentVertexIndex );

        return currentVertexIndex;
    }



    static class Edge {

        int x1, y1, z1, x2, y2, z2;

        private Edge() {
        }

        public Edge( Edge e ) {
            x1 = e.x1;
            y1 = e.y1;
            z1 = e.z1;
            x2 = e.x2;
            y2 = e.y2;
            z2 = e.z2;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 59 * hash + this.x1;
            hash = 61 * hash + this.y1;
            hash = 67 * hash + this.z1;
            hash = 71 * hash + this.x2;
            hash = 73 * hash + this.y2;
            hash = 79 * hash + this.z2;
            return hash;
        }

        @Override
        public boolean equals( Object o ) {
            Edge e = (Edge) o;
            return x1 == e.x1 && y1 == e.y1 && z1 == e.z1 &&
                    x2 == e.x2 && y2 == e.y2 && z2 == e.z2;
        }

        public float getValue1( ScalarFieldPolygonisator t ) {
            return t.values[x1][y1][z1];
        }

        public float getValue2( ScalarFieldPolygonisator t ) {
            return t.values[x2][y2][z2];
        }
    }
}