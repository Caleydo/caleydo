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

package com.jmex.terrain;

import java.io.IOException;
import java.nio.FloatBuffer;

import com.jme.math.FastMath;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.scene.VBOInfo;
import com.jme.system.DisplaySystem;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.geom.BufferUtils;

/**
 * <code>TerrainBlock</code> defines the lowest level of the terrain system.
 * <code>TerrainBlock</code> is the actual part of the terrain system that
 * renders to the screen. The terrain is built from a heightmap defined by a one
 * dimenensional int array. The step scale is used to define the amount of units
 * each block line will extend. By directly creating a <code>TerrainBlock</code>
 * yourself, you can generate a brute force terrain. This is many times
 * sufficient for small terrains on modern hardware. If terrain is to be large,
 * it is recommended that you make use of the <code>TerrainPage</code> class.
 * 
 * @author Mark Powell
 * @author Joshua Slack
 */
public class TerrainBlock extends TriMesh {

    private static final long serialVersionUID = 1L;

    // size of the block, totalSize is the total size of the heightmap if this
    // block is just a small section of it.
    private int size;

    private int totalSize;

    private short quadrant = 1;

    // x/z step
    private Vector3f stepScale;

    // center of the block in relation to (0,0,0)
    private Vector2f offset;

    // amount the block has been shifted.
    private float offsetAmount;

    // heightmap values used to create this block
    private float[] heightMap;

    private float[] oldHeightMap;

    private static Vector3f calcVec1 = new Vector3f();

    private static Vector3f calcVec2 = new Vector3f();

    private static Vector3f calcVec3 = new Vector3f();

    /**
     * Empty Constructor to be used internally only.
     */
    public TerrainBlock() {
    }

    /**
     * For internal use only. Creates a new TerrainBlock with the given name by
     * simply calling super(name)
     * 
     * @param name
     *            The name.
     */
    public TerrainBlock(String name) {
        super(name);
    }

    /**
     * Constructor instantiates a new <code>TerrainBlock</code> object. The
     * parameters and heightmap data are then processed to generate a
     * <code>TriMesh</code> object for rendering.
     * 
     * @param name
     *            the name of the terrain block.
     * @param size
     *            the size of the heightmap.
     * @param stepScale
     *            the scale for the axes.
     * @param heightMap
     *            the height data.
     * @param origin
     *            the origin offset of the block.
     */
    public TerrainBlock(String name, int size, Vector3f stepScale,
    		float[] heightMap, Vector3f origin) {
        this(name, size, stepScale, heightMap, origin, size,
                new Vector2f(), 0);
    }

    /**
     * Constructor instantiates a new <code>TerrainBlock</code> object. The
     * parameters and heightmap data are then processed to generate a
     * <code>TriMesh</code> object for renderering.
     * 
     * @param name
     *            the name of the terrain block.
     * @param size
     *            the size of the block.
     * @param stepScale
     *            the scale for the axes.
     * @param heightMap
     *            the height data.
     * @param origin
     *            the origin offset of the block.
     * @param totalSize
     *            the total size of the terrain. (Higher if the block is part of
     *            a <code>TerrainPage</code> tree.
     * @param offset
     *            the offset for texture coordinates.
     * @param offsetAmount
     *            the total offset amount. Used for texture coordinates.
     */
    public TerrainBlock(String name, int size, Vector3f stepScale,
    		float[] heightMap, Vector3f origin, int totalSize,
            Vector2f offset, float offsetAmount) {
        super(name);
        this.size = size;
        this.stepScale = stepScale;
        this.totalSize = totalSize;
        this.offsetAmount = offsetAmount;
        this.offset = offset;
        this.heightMap = heightMap;

        setLocalTranslation(origin);

        buildVertices();
        buildTextureCoordinates();
        buildNormals();

        VBOInfo vbo = new VBOInfo(true);
        setVBOInfo(vbo);
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
    public void setDetailTexture(int unit, float repeat) {
        copyTextureCoordinates(0, unit, repeat);
    }

    /**
     * <code>getHeight</code> returns the height of an arbitrary point on the
     * terrain. If the point is between height point values, the height is
     * linearly interpolated. This provides smooth height calculations. If the
     * point provided is not within the bounds of the height map, the NaN float
     * value is returned (Float.NaN).
     * 
     * @param position
     *            the vector representing the height location to check.
     * @return the height at the provided location.
     */
    public float getHeight(Vector2f position) {
        return getHeight(position.x, position.y);
    }

    /**
     * <code>getHeight</code> returns the height of an arbitrary point on the
     * terrain. If the point is between height point values, the height is
     * linearly interpolated. This provides smooth height calculations. If the
     * point provided is not within the bounds of the height map, the NaN float
     * value is returned (Float.NaN).
     * 
     * @param position
     *            the vector representing the height location to check. Only the
     *            x and z values are used.
     * @return the height at the provided location.
     */
    public float getHeight(Vector3f position) {
        return getHeight(position.x, position.z);
    }

    /**
     * <code>getHeight</code> returns the height of an arbitrary point on the
     * terrain. If the point is between height point values, the height is
     * linearly interpolated. This provides smooth height calculations. If the
     * point provided is not within the bounds of the height map, the NaN float
     * value is returned (Float.NaN).
     * 
     * @param x
     *            the x coordinate to check.
     * @param z
     *            the z coordinate to check.
     * @return the height at the provided location.
     */
    public float getHeight(float x, float z) {
        x /= stepScale.x;
        z /= stepScale.z;
        float col = FastMath.floor(x);
        float row = FastMath.floor(z);

        if (col < 0 || row < 0 || col >= size - 1 || row >= size - 1) {
            return Float.NaN;
        }
        float intOnX = x - col, intOnZ = z - row;

        float topLeft, topRight, bottomLeft, bottomRight;

        int focalSpot = (int) (col + row * size);

        // find the heightmap point closest to this position (but will always
        // be to the left ( < x) and above (< z) of the spot.
        topLeft = heightMap[focalSpot] * stepScale.y;

        // now find the next point to the right of topLeft's position...
        topRight = heightMap[focalSpot + 1] * stepScale.y;

        // now find the next point below topLeft's position...
        bottomLeft = heightMap[focalSpot + size] * stepScale.y;

        // now find the next point below and to the right of topLeft's
        // position...
        bottomRight = heightMap[focalSpot + size + 1] * stepScale.y;
        
        // Use linear interpolation to find the height.
        if(intOnX>intOnZ)
            return (1-intOnX)*topLeft + (intOnX-intOnZ)*topRight + (intOnZ)*bottomRight;
        else 
            return (1-intOnZ)*topLeft + (intOnZ-intOnX)*bottomLeft + (intOnX)*bottomRight;
    }

    /**
     * <code>getHeightFromWorld</code> returns the height of an arbitrary
     * point on the terrain when given world coordinates. If the point is
     * between height point values, the height is linearly interpolated. This
     * provides smooth height calculations. If the point provided is not within
     * the bounds of the height map, the NaN float value is returned
     * (Float.NaN).
     * 
     * @param position
     *            the vector representing the height location to check.
     * @return the height at the provided location.
     */
    public float getHeightFromWorld(Vector3f position) {
        Vector3f locationPos = calcVec1.set(position).subtractLocal(
                localTranslation);

        return getHeight(locationPos.x, locationPos.z);
    }

    /**
     * <code>getSurfaceNormal</code> returns the normal of an arbitrary point
     * on the terrain. The normal is linearly interpreted from the normals of
     * the 4 nearest defined points. If the point provided is not within the
     * bounds of the height map, null is returned.
     * 
     * @param position
     *            the vector representing the location to find a normal at.
     * @param store
     *            the Vector3f object to store the result in. If null, a new one
     *            is created.
     * @return the normal vector at the provided location.
     */
    public Vector3f getSurfaceNormal(Vector2f position, Vector3f store) {
        return getSurfaceNormal(position.x, position.y, store);
    }

    /**
     * <code>getSurfaceNormal</code> returns the normal of an arbitrary point
     * on the terrain. The normal is linearly interpreted from the normals of
     * the 4 nearest defined points. If the point provided is not within the
     * bounds of the height map, null is returned.
     * 
     * @param position
     *            the vector representing the location to find a normal at. Only
     *            the x and z values are used.
     * @param store
     *            the Vector3f object to store the result in. If null, a new one
     *            is created.
     * @return the normal vector at the provided location.
     */
    public Vector3f getSurfaceNormal(Vector3f position, Vector3f store) {
        return getSurfaceNormal(position.x, position.z, store);
    }

    /**
     * <code>getSurfaceNormal</code> returns the normal of an arbitrary point
     * on the terrain. The normal is linearly interpreted from the normals of
     * the 4 nearest defined points. If the point provided is not within the
     * bounds of the height map, null is returned.
     * 
     * @param x
     *            the x coordinate to check.
     * @param z
     *            the z coordinate to check.
     * @param store
     *            the Vector3f object to store the result in. If null, a new one
     *            is created.
     * @return the normal unit vector at the provided location.
     */
    public Vector3f getSurfaceNormal(float x, float z, Vector3f store) {
        x /= stepScale.x;
        z /= stepScale.z;
        float col = FastMath.floor(x);
        float row = FastMath.floor(z);

        if (col < 0 || row < 0 || col >= size - 1 || row >= size - 1) {
            return null;
        }
        float intOnX = x - col, intOnZ = z - row;

        if (store == null)
            store = new Vector3f();

        Vector3f topLeft = store, topRight = calcVec1, bottomLeft = calcVec2, bottomRight = calcVec3;

        int focalSpot = (int) (col + row * size);

        // find the heightmap point closest to this position (but will always
        // be to the left ( < x) and above (< z) of the spot.
        BufferUtils.populateFromBuffer(topLeft, getNormalBuffer(), focalSpot);

        // now find the next point to the right of topLeft's position...
        BufferUtils.populateFromBuffer(topRight, getNormalBuffer(),
                focalSpot + 1);

        // now find the next point below topLeft's position...
        BufferUtils.populateFromBuffer(bottomLeft, getNormalBuffer(), focalSpot
                + size);

        // now find the next point below and to the right of topLeft's
        // position...
        BufferUtils.populateFromBuffer(bottomRight, getNormalBuffer(),
                focalSpot + size + 1);

        // Use linear interpolation to find the height.
        topLeft.interpolate(topRight, intOnX);
        bottomLeft.interpolate(bottomRight, intOnX);
        topLeft.interpolate(bottomLeft, intOnZ);
        return topLeft.normalizeLocal();
    }

    /**
     * <code>buildVertices</code> sets up the vertex and index arrays of the
     * TriMesh.
     */
    private void buildVertices() {
        setVertexCount(heightMap.length);
        setVertexBuffer(BufferUtils.createVector3Buffer(getVertexBuffer(),
                getVertexCount()));
        Vector3f point = new Vector3f();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                point.set(x * stepScale.x, heightMap[x + (y * size)]
                        * stepScale.y, y * stepScale.z);
                BufferUtils.setInBuffer(point, getVertexBuffer(),
                        (x + (y * size)));
            }
        }

        // set up the indices
        setTriangleQuantity(((size - 1) * (size - 1)) * 2);
        setIndexBuffer(BufferUtils.createIntBuffer(getTriangleCount() * 3));

        // go through entire array up to the second to last column.
        for (int i = 0; i < (size * (size - 1)); i++) {
            // we want to skip the top row.
            if (i % ((size * (i / size + 1)) - 1) == 0 && i != 0) {
                continue;
            }
            // set the top left corner.
            getIndexBuffer().put(i);
            // set the bottom right corner.
            getIndexBuffer().put((1 + size) + i);
            // set the top right corner.
            getIndexBuffer().put(1 + i);
            // set the top left corner
            getIndexBuffer().put(i);
            // set the bottom left corner
            getIndexBuffer().put(size + i);
            // set the bottom right corner
            getIndexBuffer().put((1 + size) + i);

        }
    }

    /**
     * <code>buildTextureCoordinates</code> calculates the texture coordinates
     * of the terrain.
     */
    public void buildTextureCoordinates() {
        float offsetX = offset.x + (offsetAmount * stepScale.x);
        float offsetY = offset.y + (offsetAmount * stepScale.z);

        setTextureCoords(new TexCoords(BufferUtils.createVector2Buffer(getVertexCount())));
        FloatBuffer texs = getTextureCoords(0).coords;
        texs.clear();

        getVertexBuffer().rewind();
        for (int i = 0; i < getVertexCount(); i++) {
            texs.put((getVertexBuffer().get() + offsetX)
                    / (stepScale.x * (totalSize - 1)));
            getVertexBuffer().get(); // ignore vert y coord.
            texs.put((getVertexBuffer().get() + offsetY)
                    / (stepScale.z * (totalSize - 1)));
        }
    }

    /**
     * <code>buildNormals</code> calculates the normals of each vertex that
     * makes up the block of terrain.
     */
    private void buildNormals() {
        setNormalBuffer(BufferUtils.createVector3Buffer(getNormalBuffer(),
                getVertexCount()));
        Vector3f oppositePoint = new Vector3f();
        Vector3f adjacentPoint = new Vector3f();
        Vector3f rootPoint = new Vector3f();
        Vector3f tempNorm = new Vector3f();
        int adj = 0, opp = 0, normalIndex = 0;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                BufferUtils.populateFromBuffer(rootPoint, getVertexBuffer(),
                        normalIndex);
                if (row == size - 1) {
                    if (col == size - 1) { // last row, last col
                        // up cross left
                        adj = normalIndex - size;
                        opp = normalIndex - 1;
                    } else { // last row, except for last col
                        // right cross up
                        adj = normalIndex + 1;
                        opp = normalIndex - size;
                    }
                } else {
                    if (col == size - 1) { // last column except for last row
                        // left cross down
                        adj = normalIndex - 1;
                        opp = normalIndex + size;
                    } else { // most cases
                        // down cross right
                        adj = normalIndex + size;
                        opp = normalIndex + 1;
                    }
                }
                BufferUtils.populateFromBuffer(adjacentPoint,
                        getVertexBuffer(), adj);
                BufferUtils.populateFromBuffer(oppositePoint,
                        getVertexBuffer(), opp);
                tempNorm.set(adjacentPoint).subtractLocal(rootPoint)
                        .crossLocal(oppositePoint.subtractLocal(rootPoint))
                        .normalizeLocal();
                BufferUtils.setInBuffer(tempNorm, getNormalBuffer(),
                        normalIndex);
                normalIndex++;
            }
        }
    }

    /**
     * Returns the height map this terrain block is using.
     * 
     * @return This terrain block's height map.
     */
    public float[] getHeightMap() {
        return heightMap;
    }

    /**
     * Returns the offset amount this terrain block uses for textures.
     * 
     * @return The current offset amount.
     */
    public float getOffsetAmount() {
        return offsetAmount;
    }

    /**
     * Returns the step scale that stretches the height map.
     * 
     * @return The current step scale.
     */
    public Vector3f getStepScale() {
        return stepScale;
    }

    /**
     * Returns the total size of the terrain.
     * 
     * @return The terrain's total size.
     */
    public int getTotalSize() {
        return totalSize;
    }

    /**
     * Returns the size of this terrain block.
     * 
     * @return The current block size.
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns the current offset amount. This is used when building texture
     * coordinates.
     * 
     * @return The current offset amount.
     */
    public Vector2f getOffset() {
        return offset;
    }

    /**
     * Sets the value for the current offset amount to use when building texture
     * coordinates. Note that this does <b>NOT </b> rebuild the terrain at all.
     * This is mostly used for outside constructors of terrain blocks.
     * 
     * @param offset
     *            The new texture offset.
     */
    public void setOffset(Vector2f offset) {
        this.offset = offset;
    }

    /**
     * Sets the size of this terrain block. Note that this does <b>NOT </b>
     * rebuild the terrain at all. This is mostly used for outside constructors
     * of terrain blocks.
     * 
     * @param size
     *            The new size.
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Sets the total size of the terrain . Note that this does <b>NOT </b>
     * rebuild the terrain at all. This is mostly used for outside constructors
     * of terrain blocks.
     * 
     * @param totalSize
     *            The new total size.
     */
    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    /**
     * Sets the step scale of this terrain block's height map. Note that this
     * does <b>NOT </b> rebuild the terrain at all. This is mostly used for
     * outside constructors of terrain blocks.
     * 
     * @param stepScale
     *            The new step scale.
     */
    public void setStepScale(Vector3f stepScale) {
        this.stepScale = stepScale;
    }

    /**
     * Sets the offset of this terrain texture map. Note that this does <b>NOT
     * </b> rebuild the terrain at all. This is mostly used for outside
     * constructors of terrain blocks.
     * 
     * @param offsetAmount
     *            The new texture offset.
     */
    public void setOffsetAmount(float offsetAmount) {
        this.offsetAmount = offsetAmount;
    }

    /**
     * Sets the terrain's height map. Note that this does <b>NOT </b> rebuild
     * the terrain at all. This is mostly used for outside constructors of
     * terrain blocks.
     * 
     * @param heightMap
     *            The new height map.
     */
    public void setHeightMap(float[] heightMap) {
        this.heightMap = heightMap;
    }

    /**
     * Updates the block's vertices and normals from the current height map
     * values.
     */
    public void updateFromHeightMap() {
        if (!hasChanged())
            return;
        Vector3f point = new Vector3f();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                point.set(x * stepScale.x, heightMap[x + (y * size)]
                        * stepScale.y, y * stepScale.z);
                BufferUtils.setInBuffer(point, getVertexBuffer(),
                        (x + (y * size)));
            }
        }
        buildNormals();

        if (getVBOInfo() != null) {
            getVBOInfo().setVBOVertexID(-1);
            getVBOInfo().setVBONormalID(-1);
            DisplaySystem.getDisplaySystem().getRenderer().deleteVBO(
                    getVertexBuffer());
            DisplaySystem.getDisplaySystem().getRenderer().deleteVBO(
                    getNormalBuffer());
        }
    }

    /**
     * <code>setHeightMapValue</code> sets the value of this block's height
     * map at the given coords
     * 
     * @param x
     * @param y
     * @param newVal
     */
    public void setHeightMapValue(int x, int y, float newVal) {
        heightMap[x + (y * size)] = newVal;
    }

    /**
     * <code>setHeightMapValue</code> adds to the value of this block's height
     * map at the given coords
     * 
     * @param x
     * @param y
     * @param toAdd
     */
    public void addHeightMapValue(int x, int y, float toAdd) {
        heightMap[x + (y * size)] += toAdd;
    }

    /**
     * <code>setHeightMapValue</code> multiplies the value of this block's
     * height map at the given coords by the value given.
     * 
     * @param x
     * @param y
     * @param toMult
     */
    public void multHeightMapValue(int x, int y, float toMult) {
        heightMap[x + (y * size)] *= toMult;
    }

    public boolean hasChanged() {
        boolean update = false;
        if (oldHeightMap == null) {
            oldHeightMap = new float[heightMap.length];
            update = true;
        }

        for (int x = 0; x < oldHeightMap.length; x++)
            if (oldHeightMap[x] != heightMap[x] || update) {
                update = true;
                oldHeightMap[x] = heightMap[x];
            }

        return update;
    }

    /**
     * @return Returns the quadrant.
     */
    public short getQuadrant() {
        return quadrant;
    }

    /**
     * @param quadrant
     *            The quadrant to set.
     */
    public void setQuadrant(short quadrant) {
        this.quadrant = quadrant;
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(size, "size", 0);
        capsule.write(totalSize, "totalSize", 0);
        capsule.write(quadrant, "quadrant", (short) 1);
        capsule.write(stepScale, "stepScale", Vector3f.ZERO);
        capsule.write(offset, "offset", new Vector2f());
        capsule.write(offsetAmount, "offsetAmount", 0);
        capsule.write(heightMap, "heightMap", null);
        capsule.write(oldHeightMap, "oldHeightMap", null);
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        size = capsule.readInt("size", 0);
        totalSize = capsule.readInt("totalSize", 0);
        quadrant = capsule.readShort("quadrant", (short) 1);
        stepScale = (Vector3f) capsule.readSavable("stepScale", Vector3f.ZERO
                .clone());
        offset = (Vector2f) capsule.readSavable("offset", new Vector2f());
        offsetAmount = capsule.readFloat("offsetAmount", 0);
        heightMap = capsule.readFloatArray("heightMap", null);
        oldHeightMap = capsule.readFloatArray("oldHeightMap", null);
    }
}