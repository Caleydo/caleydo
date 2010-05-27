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

package com.jmex.effects.water;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jme.math.FastMath;
import com.jme.math.Matrix4f;
import com.jme.math.Quaternion;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.AbstractCamera;
import com.jme.renderer.Camera;
import com.jme.renderer.Renderer;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.util.Timer;
import com.jme.util.geom.BufferUtils;
import com.jmex.effects.ProjectedTextureUtil;

/**
 * <code>ProjectedGrid</code>
 * Projected grid mesh
 *
 * @author Rikard Herlitz (MrCoder)
 */
public class ProjectedGrid extends TriMesh {
    private static final long serialVersionUID = 1L;
    
	private int sizeX;
	private int sizeY;

	//x/z step
	private static Vector3f calcVec1 = new Vector3f();
	private static Vector3f calcVec2 = new Vector3f();
	private static Vector3f calcVec3 = new Vector3f();

	private FloatBuffer vertBuf;
	private FloatBuffer normBuf;
	private FloatBuffer texs;
	private IntBuffer indexBuffer;

	private float viewPortWidth = 0;
	private float viewPortHeight = 0;
	private float viewPortLeft = 0;
	private float viewPortBottom = 0;

	private Quaternion origin = new Quaternion();
	private Quaternion direction = new Quaternion();
	private Vector2f source = new Vector2f();

	private Matrix4f modelViewMatrix = new Matrix4f();
	private Matrix4f projectionMatrix = new Matrix4f();
	private Matrix4f modelViewProjectionInverse = new Matrix4f();
	private Quaternion intersectBottomLeft = new Quaternion();
	private Quaternion intersectTopLeft = new Quaternion();
	private Quaternion intersectTopRight = new Quaternion();
	private Quaternion intersectBottomRight = new Quaternion();

	private Matrix4f modelViewMatrix1 = new Matrix4f();
	private Matrix4f projectionMatrix1 = new Matrix4f();
	private Matrix4f modelViewProjection1 = new Matrix4f();
	private Matrix4f modelViewProjectionInverse1 = new Matrix4f();
	private Quaternion intersectBottomLeft1 = new Quaternion();
	private Quaternion intersectTopLeft1 = new Quaternion();
	private Quaternion intersectTopRight1 = new Quaternion();
	private Quaternion intersectBottomRight1 = new Quaternion();

	private Vector3f camloc = new Vector3f();
	private Vector3f camdir = new Vector3f();
	private Quaternion pointFinal = new Quaternion();
	private Quaternion pointTop = new Quaternion();
	private Quaternion pointBottom = new Quaternion();
	private Vector3f realPoint = new Vector3f();

	public boolean freezeProjector = false;
	public boolean useReal = false;
	private Vector3f projectorLoc = new Vector3f();
	private Timer timer;
	protected Camera cam;
	protected float fovY = 45.0f;

	private HeightGenerator heightGenerator;
	private float textureScale;

	private float[] vertBufArray;
	private float[] normBufArray;
	private float[] texBufArray;
	
	public ProjectedGrid( String name, Camera cam, int sizeX, int sizeY, float texureScale, HeightGenerator heightGenerator ) {
		super( name );
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.textureScale = texureScale;
		this.heightGenerator = heightGenerator;
		this.cam = cam;
		
		if (cam.getFrustumNear() > 0.0f) {
			fovY = FastMath.atan(cam.getFrustumTop() / cam.getFrustumNear())
					* 2.0f / FastMath.DEG_TO_RAD;
		}

		timer = Timer.getTimer();

		setVertexCount( sizeX * sizeY );

		vertBufArray = new float[getVertexCount()*3];
		normBufArray = new float[getVertexCount()*3];
		texBufArray = new float[getVertexCount()*2];

		buildVertices();
		buildTextureCoordinates();
		buildNormals();
	}

	public void switchFreeze() {
		freezeProjector = !freezeProjector;
	}

	public void draw( Renderer r ) {
		update();
		super.draw( r );
	}

	public void update() {
		if( freezeProjector ) return;

		float time = timer.getTimeInSeconds();

		camloc.set( cam.getLocation() );
		camdir.set( cam.getDirection() );

		AbstractCamera camera = (AbstractCamera) cam;

		viewPortWidth = camera.getWidth();
		viewPortHeight = camera.getHeight();
		viewPortLeft = camera.getViewPortLeft();
		viewPortBottom = camera.getViewPortBottom();
		modelViewMatrix.set( camera.getModelViewMatrix() );
		projectionMatrix.set( camera.getProjectionMatrix() );
		modelViewProjectionInverse.set( modelViewMatrix ).multLocal( projectionMatrix );
		modelViewProjectionInverse.invertLocal();

		source.set( 0.5f, 0.5f );
		getWorldIntersection( source, modelViewProjectionInverse, pointFinal );
		pointFinal.multLocal( 1.0f / pointFinal.w );
		realPoint.set( pointFinal.x, pointFinal.y, pointFinal.z );
		projectorLoc.set( cam.getLocation() );
		realPoint.set( projectorLoc ).addLocal( cam.getDirection() );

		Matrix4f rangeMatrix = null;
		if( useReal ) {
			Vector3f fakeLoc = new Vector3f( projectorLoc );
			Vector3f fakePoint = new Vector3f( realPoint );
			fakeLoc.addLocal( 0, 1000, 0 );

			rangeMatrix = getMinMax( fakeLoc, fakePoint, cam );
		}

		ProjectedTextureUtil.matrixLookAt( projectorLoc, realPoint, Vector3f.UNIT_Y, modelViewMatrix );
		ProjectedTextureUtil.matrixProjection( fovY + 10.0f, getAspectRatio(), cam.getFrustumNear(), cam.getFrustumFar(), projectionMatrix );
		modelViewProjectionInverse.set( modelViewMatrix ).multLocal( projectionMatrix );
		modelViewProjectionInverse.invertLocal();

		if( useReal && rangeMatrix != null ) {
			rangeMatrix.multLocal( modelViewProjectionInverse );
			modelViewProjectionInverse.set( rangeMatrix );
		}

		source.set( 0, 0 );
		getWorldIntersection( source, modelViewProjectionInverse, intersectBottomLeft );
		source.set( 0, 1 );
		getWorldIntersection( source, modelViewProjectionInverse, intersectTopLeft );
		source.set( 1, 1 );
		getWorldIntersection( source, modelViewProjectionInverse, intersectTopRight );
		source.set( 1, 0 );
		getWorldIntersection( source, modelViewProjectionInverse, intersectBottomRight );

		vertBuf.rewind();
		float du = 1.0f / (float) (sizeX - 1);
		float dv = 1.0f / (float) (sizeY - 1);
		float u = 0, v = 0;
		int index = 0;
		for( int y = 0; y < sizeY; y++ ) {
			for( int x = 0; x < sizeX; x++ ) {
				interpolate( intersectTopLeft, intersectTopRight, u, pointTop );
				interpolate( intersectBottomLeft, intersectBottomRight, u, pointBottom );
				interpolate( pointTop, pointBottom, v, pointFinal );
				pointFinal.x /= pointFinal.w;
				pointFinal.z /= pointFinal.w;
				realPoint.set( pointFinal.x,
							   heightGenerator.getHeight( pointFinal.x, pointFinal.z, time ),
							   pointFinal.z );

				vertBufArray[index++] = realPoint.x;
				vertBufArray[index++] = realPoint.y;
				vertBufArray[index++] = realPoint.z;

				u += du;
			}
			v += dv;
			u = 0;
		}
		vertBuf.put( vertBufArray );

		texs.rewind();
		for( int i = 0; i < getVertexCount(); i++ ) {
			texBufArray[i*2] = vertBufArray[i*3] * textureScale;
			texBufArray[i*2+1] = vertBufArray[i*3+2] * textureScale;
		}
		texs.put( texBufArray );

		normBuf.rewind();
		oppositePoint.set( 0, 0, 0 );
		adjacentPoint.set( 0, 0, 0 );
		rootPoint.set( 0, 0, 0 );
		tempNorm.set( 0, 0, 0 );
		int adj = 0, opp = 0, normalIndex = 0;
		for( int row = 0; row < sizeY; row++ ) {
			for( int col = 0; col < sizeX; col++ ) {
				if( row == sizeY - 1 ) {
					if( col == sizeX - 1 ) { // last row, last col
						// up cross left
						adj = normalIndex - sizeX;
						opp = normalIndex - 1;
					}
					else { // last row, except for last col
						// right cross up
						adj = normalIndex + 1;
						opp = normalIndex - sizeX;
					}
				}
				else {
					if( col == sizeX - 1 ) { // last column except for last row
						// left cross down
						adj = normalIndex - 1;
						opp = normalIndex + sizeX;
					}
					else { // most cases
						// down cross right
						adj = normalIndex + sizeX;
						opp = normalIndex + 1;
					}
				}
				rootPoint.set(vertBufArray[normalIndex*3],vertBufArray[normalIndex*3+1],vertBufArray[normalIndex*3+2]);
				adjacentPoint.set(vertBufArray[adj*3],vertBufArray[adj*3+1],vertBufArray[adj*3+2]);
				oppositePoint.set(vertBufArray[opp*3],vertBufArray[opp*3+1],vertBufArray[opp*3+2]);
				tempNorm.set( adjacentPoint ).subtractLocal( rootPoint )
						.crossLocal( oppositePoint.subtractLocal( rootPoint ) )
						.normalizeLocal();

				normBufArray[normalIndex*3] = tempNorm.x;
				normBufArray[normalIndex*3+1] = tempNorm.y;
				normBufArray[normalIndex*3+2] = tempNorm.z;

				normalIndex++;
			}
		}
		normBuf.put( normBufArray );
	}

	protected float getAspectRatio() {
	  return viewPortWidth / viewPortHeight;
	}
	
	private Matrix4f getMinMax( Vector3f fakeLoc, Vector3f fakePoint, Camera cam ) {
		Matrix4f rangeMatrix;
		ProjectedTextureUtil.matrixLookAt( fakeLoc, fakePoint, Vector3f.UNIT_Y, modelViewMatrix1 );
		ProjectedTextureUtil.matrixProjection( fovY, getAspectRatio(), cam.getFrustumNear(), cam.getFrustumFar(), projectionMatrix1 );
		modelViewProjection1.set( modelViewMatrix1 ).multLocal( projectionMatrix1 );
		modelViewProjectionInverse1.set( modelViewProjection1 ).invertLocal();

		source.set( 0, 0 );
		getWorldIntersection( source, modelViewProjectionInverse, intersectBottomLeft1 );
		source.set( 0, 1 );
		getWorldIntersection( source, modelViewProjectionInverse, intersectTopLeft1 );
		source.set( 1, 1 );
		getWorldIntersection( source, modelViewProjectionInverse, intersectTopRight1 );
		source.set( 1, 0 );
		getWorldIntersection( source, modelViewProjectionInverse, intersectBottomRight1 );

		Vector3f tmp = new Vector3f();
		tmp.set( intersectBottomLeft.x, intersectBottomLeft.y, intersectBottomLeft.z );
		modelViewProjection1.mult( tmp, tmp );
		intersectBottomLeft.x = tmp.x;
		intersectBottomLeft.y = tmp.y;
		intersectBottomLeft.z = tmp.z;

		tmp.set( intersectTopLeft1.x, intersectTopLeft1.y, intersectTopLeft1.z );
		modelViewProjection1.mult( tmp, tmp );
		intersectTopLeft1.x = tmp.x;
		intersectTopLeft1.y = tmp.y;
		intersectTopLeft1.z = tmp.z;

		tmp.set( intersectTopRight1.x, intersectTopRight1.y, intersectTopRight1.z );
		modelViewProjection1.mult( tmp, tmp );
		intersectTopRight1.x = tmp.x;
		intersectTopRight1.y = tmp.y;
		intersectTopRight1.z = tmp.z;

		tmp.set( intersectBottomRight1.x, intersectBottomRight1.y, intersectBottomRight1.z );
		modelViewProjection1.mult( tmp, tmp );
		intersectBottomRight1.x = tmp.x;
		intersectBottomRight1.y = tmp.y;
		intersectBottomRight1.z = tmp.z;

//			modelViewProjection1.mult( intersectBottomLeft1, intersectBottomLeft1 );
//			modelViewProjection1.mult( intersectTopLeft1, intersectTopLeft1 );
//			modelViewProjection1.mult( intersectTopRight1, intersectTopRight1 );
//			modelViewProjection1.mult( intersectBottomRight1, intersectBottomRight1 );

		float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, maxX = Float.MIN_VALUE, maxY = Float.MIN_VALUE;
		if( intersectBottomLeft1.x < minX ) minX = intersectBottomLeft1.x;
		if( intersectTopLeft1.x < minX ) minX = intersectTopLeft1.x;
		if( intersectTopRight1.x < minX ) minX = intersectTopRight1.x;
		if( intersectBottomRight1.x < minX ) minX = intersectBottomRight1.x;
		if( intersectBottomLeft1.x > maxX ) maxX = intersectBottomLeft1.x;
		if( intersectTopLeft1.x > maxX ) maxX = intersectTopLeft1.x;
		if( intersectTopRight1.x > maxX ) maxX = intersectTopRight1.x;
		if( intersectBottomRight1.x > maxX ) maxX = intersectBottomRight1.x;

		if( intersectBottomLeft1.y < minY ) minY = intersectBottomLeft1.y;
		if( intersectTopLeft1.y < minY ) minY = intersectTopLeft1.y;
		if( intersectTopRight1.y < minY ) minY = intersectTopRight1.y;
		if( intersectBottomRight1.y < minY ) minY = intersectBottomRight1.y;
		if( intersectBottomLeft1.y > maxY ) maxY = intersectBottomLeft1.y;
		if( intersectTopLeft1.y > maxY ) maxY = intersectTopLeft1.y;
		if( intersectTopRight1.y > maxY ) maxY = intersectTopRight1.y;
		if( intersectBottomRight1.y > maxY ) maxY = intersectBottomRight1.y;
		rangeMatrix = new Matrix4f(
				maxX - minX, 0, 0, minX,
				0, maxY - minY, 0, minY,
				0, 0, 1, 0,
				0, 0, 0, 1
		);
		rangeMatrix.transpose();
		return rangeMatrix;
	}

	private void interpolate( Quaternion beginVec, Quaternion finalVec, float changeAmnt, Quaternion resultVec ) {
		resultVec.x = (1 - changeAmnt) * beginVec.x + changeAmnt * finalVec.x;
//		resultVec.y = (1 - changeAmnt) * beginVec.y + changeAmnt * finalVec.y;
		resultVec.z = (1 - changeAmnt) * beginVec.z + changeAmnt * finalVec.z;
		resultVec.w = (1 - changeAmnt) * beginVec.w + changeAmnt * finalVec.w;
	}

	private void interpolate( Vector3f beginVec, Vector3f finalVec, float changeAmnt, Vector3f resultVec ) {
		resultVec.x = (1 - changeAmnt) * beginVec.x + changeAmnt * finalVec.x;
		resultVec.y = (1 - changeAmnt) * beginVec.y + changeAmnt * finalVec.y;
		resultVec.z = (1 - changeAmnt) * beginVec.z + changeAmnt * finalVec.z;
	}

	private void getWorldIntersection( Vector2f screenPosition, Matrix4f viewProjectionMatrix, Quaternion store ) {
		origin.set( screenPosition.x * 2 - 1, screenPosition.y * 2 - 1, -1, 1 );
		direction.set( screenPosition.x * 2 - 1, screenPosition.y * 2 - 1, 1, 1 );

		viewProjectionMatrix.mult( origin, origin );
		viewProjectionMatrix.mult( direction, direction );

		if( cam.getLocation().y > 0 ) {
			if( direction.y > 0 ) {
				direction.y = 0;
			}
		}
		else {
			if( direction.y < 0 ) {
				direction.y = 0;
			}
		}

		direction.subtractLocal( origin );

		float t = -origin.y / direction.y;

		direction.multLocal( t );
		store.set( origin );
		store.addLocal( direction );
	}

    private float homogenousIntersect(Quaternion a, Quaternion xa, Quaternion xb) {
//        float tx = -xb.w*(dotXYZ(a.xyz,xa.xyz)+xa.w*a.w);
//        float tw = dotXYZ(a,xa.w*xb.xyz-xb.w*xa.xyz);
//        return tx/tw;
        return 0;
    }

    private float dotXYZ(Quaternion a, Quaternion b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    private void mulXYZ(Quaternion a, Quaternion b) {
    }

	/**
     * <code>setDetailTexture</code> copies the texture coordinates from the
     * first texture channel to another channel specified by unit, mulitplying
     * by the factor specified by repeat so that the texture in that channel
     * will be repeated that many times across the block.
     * 
     * @param unit
     *            channel to copy coords to
     * @param repeat
     *            number of times to repeat the texture across and down the
     *            block
     */
	public void setDetailTexture( int unit, float repeat) {
		copyTextureCoordinates(0, unit, repeat);
	}


	/**
	 * <code>getSurfaceNormal</code> returns the normal of an arbitrary point
	 * on the terrain. The normal is linearly interpreted from the normals of
	 * the 4 nearest defined points. If the point provided is not within the
	 * bounds of the height map, null is returned.
	 *
	 * @param position the vector representing the location to find a normal at.
	 * @param store	the Vector3f object to store the result in. If null, a new one
	 *                 is created.
	 * @return the normal vector at the provided location.
	 */
	public Vector3f getSurfaceNormal( Vector2f position, Vector3f store ) {
		return getSurfaceNormal( position.x, position.y, store );
	}

	/**
	 * <code>getSurfaceNormal</code> returns the normal of an arbitrary point
	 * on the terrain. The normal is linearly interpreted from the normals of
	 * the 4 nearest defined points. If the point provided is not within the
	 * bounds of the height map, null is returned.
	 *
	 * @param position the vector representing the location to find a normal at. Only
	 *                 the x and z values are used.
	 * @param store	the Vector3f object to store the result in. If null, a new one
	 *                 is created.
	 * @return the normal vector at the provided location.
	 */
	public Vector3f getSurfaceNormal( Vector3f position, Vector3f store ) {
		return getSurfaceNormal( position.x, position.z, store );
	}

	/**
	 * <code>getSurfaceNormal</code> returns the normal of an arbitrary point
	 * on the terrain. The normal is linearly interpreted from the normals of
	 * the 4 nearest defined points. If the point provided is not within the
	 * bounds of the height map, null is returned.
	 *
	 * @param x	 the x coordinate to check.
	 * @param z	 the z coordinate to check.
	 * @param store the Vector3f object to store the result in. If null, a new one
	 *              is created.
	 * @return the normal unit vector at the provided location.
	 */
	public Vector3f getSurfaceNormal( float x, float z, Vector3f store ) {
//        x /= stepScale.x;
//        z /= stepScale.z;
		float col = FastMath.floor( x );
		float row = FastMath.floor( z );

		if( col < 0 || row < 0 || col >= sizeX - 1 || row >= sizeY - 1 ) {
			return null;
		}
		float intOnX = x - col, intOnZ = z - row;

		if( store == null ) store = new Vector3f();

		Vector3f topLeft = store, topRight = calcVec1, bottomLeft = calcVec2, bottomRight = calcVec3;

		int focalSpot = (int) (col + row * sizeX);

		// find the heightmap point closest to this position (but will always
		// be to the left ( < x) and above (< z) of the spot.
		BufferUtils.populateFromBuffer( topLeft, normBuf, focalSpot );

		// now find the next point to the right of topLeft's position...
		BufferUtils.populateFromBuffer( topRight, normBuf, focalSpot + 1 );

		// now find the next point below topLeft's position...
		BufferUtils.populateFromBuffer( bottomLeft, normBuf, focalSpot + sizeX );

		// now find the next point below and to the right of topLeft's
		// position...
		BufferUtils.populateFromBuffer( bottomRight, normBuf, focalSpot + sizeX
															  + 1 );

		// Use linear interpolation to find the height.
		topLeft.interpolate( topRight, intOnX );
		bottomLeft.interpolate( bottomRight, intOnX );
		topLeft.interpolate( bottomLeft, intOnZ );
		return topLeft.normalizeLocal();
	}

	/**
	 * <code>buildVertices</code> sets up the vertex and index arrays of the
	 * TriMesh.
	 */
	private void buildVertices() {
		vertBuf = BufferUtils.createVector3Buffer( vertBuf, getVertexCount() );
		setVertexBuffer( vertBuf );

		Vector3f point = new Vector3f();
		for( int x = 0; x < sizeX; x++ ) {
			for( int y = 0; y < sizeY; y++ ) {
				point.set( x, 0, y );
				BufferUtils.setInBuffer( point, vertBuf, (x + (y * sizeX)) );
			}
		}

		//set up the indices
		int triangleQuantity = ((sizeX - 1) * (sizeY - 1)) * 2;
		setTriangleQuantity( triangleQuantity );
		indexBuffer = BufferUtils.createIntBuffer( triangleQuantity * 3 );
		setIndexBuffer( indexBuffer );

		//go through entire array up to the second to last column.
		for( int i = 0; i < (sizeX * (sizeY - 1)); i++ ) {
			//we want to skip the top row.
			if( i % ((sizeX * (i / sizeX + 1)) - 1) == 0 && i != 0 ) {
//				logger.info("skip row: "+i+" cause: "+((sizeY * (i / sizeX + 1)) - 1));
				continue;
			}
			else {
//				logger.info("i: "+i);
			}
			//set the top left corner.
			indexBuffer.put( i );
			//set the bottom right corner.
			indexBuffer.put( (1 + sizeX) + i );
			//set the top right corner.
			indexBuffer.put( 1 + i );
			//set the top left corner
			indexBuffer.put( i );
			//set the bottom left corner
			indexBuffer.put( sizeX + i );
			//set the bottom right corner
			indexBuffer.put( (1 + sizeX) + i );
		}
	}

	/**
	 * <code>buildTextureCoordinates</code> calculates the texture coordinates
	 * of the terrain.
	 */
	private void buildTextureCoordinates() {
		texs = BufferUtils.createVector2Buffer( getVertexCount() );
		setTextureCoords( new TexCoords(texs), 0 );
		texs.clear();

		vertBuf.rewind();
		for( int i = 0; i < getVertexCount(); i++ ) {
			texs.put( vertBuf.get() * textureScale );
			vertBuf.get(); // ignore vert y coord.
			texs.put( vertBuf.get() * textureScale );
		}
	}

	/**
	 * <code>buildNormals</code> calculates the normals of each vertex that
	 * makes up the block of terrain.
	 */
	Vector3f oppositePoint = new Vector3f();
	Vector3f adjacentPoint = new Vector3f();
	Vector3f rootPoint = new Vector3f();
	Vector3f tempNorm = new Vector3f();

	private void buildNormals() {
		normBuf = BufferUtils.createVector3Buffer( normBuf, getVertexCount() );
		setNormalBuffer( normBuf );

		oppositePoint.set( 0, 0, 0 );
		adjacentPoint.set( 0, 0, 0 );
		rootPoint.set( 0, 0, 0 );
		tempNorm.set( 0, 0, 0 );
		int adj = 0, opp = 0, normalIndex = 0;
		for( int row = 0; row < sizeY; row++ ) {
			for( int col = 0; col < sizeX; col++ ) {
				BufferUtils.populateFromBuffer( rootPoint, vertBuf, normalIndex );
				if( row == sizeY - 1 ) {
					if( col == sizeX - 1 ) { // last row, last col
						// up cross left
						adj = normalIndex - sizeX;
						opp = normalIndex - 1;
					}
					else { // last row, except for last col
						// right cross up
						adj = normalIndex + 1;
						opp = normalIndex - sizeX;
					}
				}
				else {
					if( col == sizeY - 1 ) { // last column except for last row
						// left cross down
						adj = normalIndex - 1;
						opp = normalIndex + sizeX;
					}
					else { // most cases
						// down cross right
						adj = normalIndex + sizeX;
						opp = normalIndex + 1;
					}
				}
				BufferUtils.populateFromBuffer( adjacentPoint, vertBuf, adj );
				BufferUtils.populateFromBuffer( oppositePoint, vertBuf, opp );
				tempNorm.set( adjacentPoint ).subtractLocal( rootPoint )
						.crossLocal( oppositePoint.subtractLocal( rootPoint ) )
						.normalizeLocal();
				BufferUtils.setInBuffer( tempNorm, normBuf, normalIndex );
				normalIndex++;
			}
		}
	}
}
