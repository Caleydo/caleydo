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

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingVolume;
import com.jme.math.FastMath;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

/**
 * <code>TerrainPage</code> is used to build a quad tree of terrain blocks. The
 * <code>TerrainPage</code> will have four children, either four pages or four
 * blocks. The size of the page must be (2^N + 1), to allow for even splitting
 * of the blocks. Organization of the page into a quad tree allows for very fast
 * culling of the terrain. The total size of the heightmap is provided, as well
 * as the desired end size for a block. Appropriate values for the end block
 * size is completely dependent on the application. In some cases, a large size
 * will give performance gains, in others, a small size is the best option. It
 * is recommended that different combinations are tried.
 * 
 * @author Mark Powell
 * @author Joshua Slack
 */
public class TerrainPage extends Node {

    private static final long serialVersionUID = 1L;

    private Vector2f offset;

    private int totalSize;

    private int size;

    private Vector3f stepScale;

    private float offsetAmount;

    private short quadrant = 1;

    private static Vector3f calcVec1 = new Vector3f();

    /**
     * Empty Constructor to be used internally only.
     */
    public TerrainPage() {
    }

    /**
     * Creates a TerrainPage to be filled later. Usually, users don't want to
     * call this function unless they have a terrain page already built.
     * 
     * @param name
     *            The name of the page node.
     */
    public TerrainPage(String name) {
        super(name);
    }

    /**
     * Constructor instantiates a new <code>TerrainPage</code> object. The
     * data is then split into either 4 new <code>TerrainPages</code> or 4 new
     * <code>TerrainBlock</code>.
     * 
     * @param name
     *            the name of the page.
     * @param blockSize
     *            the size of the leaf nodes. This is used to determine if four
     *            new <code>TerrainPage</code> objects should be the child or
     *            four new <code>TerrainBlock</code> objects.
     * @param size
     *            the size of the heightmap for this page.
     * @param stepScale
     *            the scale of the axes.
     * @param heightMap
     *            the height data.
     */
    public TerrainPage(String name, int blockSize, int size,
            Vector3f stepScale, float[] heightMap) {
        this(name, blockSize, size, stepScale, heightMap, size,
                new Vector2f(), 0);
        fixNormals();
    }

    /**
     * Constructor instantiates a new <code>TerrainPage</code> object. The
     * data is then split into either 4 new <code>TerrainPages</code> or 4 new
     * <code>TerrainBlock</code>.
     * 
     * @param name
     *            the name of the page.
     * @param blockSize
     *            the size of the leaf nodes. This is used to determine if four
     *            new <code>TerrainPage</code> objects should be the child or
     *            four new <code>TerrainBlock</code> objects.
     * @param size
     *            the size of the heightmap for this page.
     * @param stepScale
     *            the scale of the axes.
     * @param heightMap
     *            the height data.
     * @param totalSize
     *            the total terrain size, used if the page is an internal node
     *            of a terrain system.
     * @param offset
     *            the texture offset for the page.
     * @param offsetAmount
     *            the amount of the offset.
     */
    protected TerrainPage(String name, int blockSize, int size,
            Vector3f stepScale, float[] heightMap, int totalSize,
            Vector2f offset, float offsetAmount) {
        super(name);
        if (!FastMath.isPowerOfTwo(size - 1)) {
            throw new JmeException("size given: " + size
                    + "  Terrain page sizes may only be (2^N + 1)");
        }

        this.offset = offset;
        this.offsetAmount = offsetAmount;
        this.totalSize = totalSize;
        this.size = size;
        this.stepScale = stepScale;
        split(blockSize, heightMap);
    }

    /**
     * <code>setDetailTexture</code> sets the detail texture coordinates to be
     * applied on top of the normal terrain texture.
     * 
     * @param unit
     *            the texture unit to set the coordinates.
     * @param repeat
     *            the number of tiling for the texture.
     */
    public void setDetailTexture(int unit, int repeat) {
        for (int i = 0; i < this.getQuantity(); i++) {
            if (this.getChild(i) instanceof TerrainPage) {
                ((TerrainPage) getChild(i)).setDetailTexture(unit, repeat);
            } else if (this.getChild(i) instanceof TerrainBlock) {
                ((TerrainBlock) getChild(i)).setDetailTexture(unit, repeat);

            }
        }
    }

    /**
     * <code>setModelBound</code> sets the model bounds for the terrain
     * blocks.
     * 
     * @param v
     *            the bounding volume to set for the terrain blocks.
     */
    public void setModelBound(BoundingVolume v) {
        for (int i = 0; i < this.getQuantity(); i++) {
            if (this.getChild(i) instanceof TerrainPage) {
                ((TerrainPage) getChild(i)).setModelBound(v.clone(null));
            } else if (this.getChild(i) instanceof TerrainBlock) {
                ((TerrainBlock) getChild(i)).setModelBound(v.clone(null));

            }
        }
    }

    /**
     * <code>updateModelBound</code> updates the model bounds (generates the
     * bounds from the current vertices).
     */
    public void updateModelBound() {
        for (int i = 0; i < this.getQuantity(); i++) {
            if (this.getChild(i) instanceof TerrainPage) {
                ((TerrainPage) getChild(i)).updateModelBound();
            } else if (this.getChild(i) instanceof TerrainBlock) {
                ((TerrainBlock) getChild(i)).updateModelBound();

            }
        }
    }

    /**
     * <code>updateFromHeightMap</code> updates the verts of all sub blocks
     * from the contents of their heightmaps.
     */
    public void updateFromHeightMap() {
        for (int i = 0; i < this.getQuantity(); i++) {
            if (this.getChild(i) instanceof TerrainPage) {
                ((TerrainPage) getChild(i)).updateFromHeightMap();
            } else if (this.getChild(i) instanceof TerrainBlock) {
                ((TerrainBlock) getChild(i)).updateFromHeightMap();

            }
        }
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
        // determine which quadrant this is in.
        Spatial child = null;
        int split = (size - 1) >> 1;
        float halfmapx = split * stepScale.x, halfmapz = split * stepScale.z;
        float newX = 0, newZ = 0;
        if (x == 0)
            x += .001f;
        if (z == 0)
            z += .001f;
        if (x > 0) {
            if (z > 0) {
                // upper right
                child = getChild(3);
                newX = x;
                newZ = z;
            } else {
                // lower right
                child = getChild(2);
                newX = x;
                newZ = z + halfmapz;
            }
        } else {
            if (z > 0) {
                // upper left
                child = getChild(1);
                newX = x + halfmapx;
                newZ = z;
            } else {
                // lower left...
                child = getChild(0);
                if (x == 0)
                    x -= .1f;
                if (z == 0)
                    z -= .1f;
                newX = x + halfmapx;
                newZ = z + halfmapz;
            }
        }
        if (child instanceof TerrainBlock)
            return ((TerrainBlock) child).getHeight(newX, newZ);
        else if (child instanceof TerrainPage)
            return ((TerrainPage) child).getHeight(x
                    - ((TerrainPage) child).getLocalTranslation().x, z
                    - ((TerrainPage) child).getLocalTranslation().z);
        return Float.NaN;
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
                localTranslation).divideLocal(stepScale);
        locationPos.multLocal(getStepScale());

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
     * @return the normal vector at the provided location.
     */
    public Vector3f getSurfaceNormal(float x, float z, Vector3f store) {
        // determine which quadrant this is in.
        Spatial child = null;
        int split = (size - 1) >> 1;
        float halfmapx = split * stepScale.x, halfmapz = split * stepScale.z;
        float newX = 0, newZ = 0;
        if (x == 0)
            x += .001f;
        if (z == 0)
            z += .001f;
        if (x > 0) {
            if (z > 0) {
                // upper right
                child = getChild(3);
                newX = x;
                newZ = z;
            } else {
                // lower right
                child = getChild(2);
                newX = x;
                newZ = z + halfmapz;
            }
        } else {
            if (z > 0) {
                // upper left
                child = getChild(1);
                newX = x + halfmapx;
                newZ = z;
            } else {
                // lower left...
                child = getChild(0);
                if (x == 0)
                    x -= .1f;
                if (z == 0)
                    z -= .1f;
                newX = x + halfmapx;
                newZ = z + halfmapz;
            }
        }
        if (child instanceof TerrainBlock)
            return ((TerrainBlock) child).getSurfaceNormal(newX, newZ, store);
        else if (child instanceof TerrainPage)
            return ((TerrainPage) child).getSurfaceNormal(x
                    - ((TerrainPage) child).getLocalTranslation().x, z
                    - ((TerrainPage) child).getLocalTranslation().z, store);
        return null;
    }

    /**
     * <code>split</code> divides the heightmap data for four children. The
     * children are either pages or blocks. This is dependent on the size of the
     * children. If the child's size is less than or equal to the set block
     * size, then blocks are created, otherwise, pages are created.
     * 
     * @param blockSize
     *            the blocks size to test against.
     * @param heightMap
     *            the height data.
     */
    private void split(int blockSize, float[] heightMap) {
        if ((size >> 1) + 1 <= blockSize) {
            createQuadBlock(heightMap);
        } else {
            createQuadPage(blockSize, heightMap);
        }

    }

    /**
     * <code>createQuadPage</code> generates four new pages from this page.
     */
    private void createQuadPage(int blockSize, float[] heightMap) {
        // create 4 terrain pages
        int quarterSize = size >> 2;

        int split = (size + 1) >> 1;

        Vector2f tempOffset = new Vector2f();
        offsetAmount += quarterSize;

        // 1 upper left
        float[] heightBlock1 = createHeightSubBlock(heightMap, 0, 0, split);

        Vector3f origin1 = new Vector3f(-quarterSize * stepScale.x, 0,
                -quarterSize * stepScale.z);

        tempOffset.x = offset.x;
        tempOffset.y = offset.y;
        tempOffset.x += origin1.x;
        tempOffset.y += origin1.z;

        TerrainPage page1 = new TerrainPage(getName() + "Page1", blockSize,
                split, stepScale, heightBlock1, totalSize, tempOffset,
                offsetAmount);
        page1.setLocalTranslation(origin1);
        page1.quadrant = 1;
        this.attachChild(page1);

        // 2 lower left
        float[] heightBlock2 = createHeightSubBlock(heightMap, 0, split - 1,
                split);

        Vector3f origin2 = new Vector3f(-quarterSize * stepScale.x, 0,
                quarterSize * stepScale.z);

        tempOffset.x = offset.x;
        tempOffset.y = offset.y;
        tempOffset.x += origin2.x;
        tempOffset.y += origin2.z;

        TerrainPage page2 = new TerrainPage(getName() + "Page2", blockSize,
                split, stepScale, heightBlock2, totalSize, tempOffset,
                offsetAmount);
        page2.setLocalTranslation(origin2);
        page2.quadrant = 2;
        this.attachChild(page2);

        // 3 upper right
        float[] heightBlock3 = createHeightSubBlock(heightMap, split - 1, 0,
                split);

        Vector3f origin3 = new Vector3f(quarterSize * stepScale.x, 0,
                -quarterSize * stepScale.z);

        tempOffset.x = offset.x;
        tempOffset.y = offset.y;
        tempOffset.x += origin3.x;
        tempOffset.y += origin3.z;

        TerrainPage page3 = new TerrainPage(getName() + "Page3", blockSize,
                split, stepScale, heightBlock3, totalSize, tempOffset,
                offsetAmount);
        page3.setLocalTranslation(origin3);
        page3.quadrant = 3;
        this.attachChild(page3);
        // //
        // 4 lower right
        float[] heightBlock4 = createHeightSubBlock(heightMap, split - 1,
                split - 1, split);

        Vector3f origin4 = new Vector3f(quarterSize * stepScale.x, 0,
                quarterSize * stepScale.z);

        tempOffset.x = offset.x;
        tempOffset.y = offset.y;
        tempOffset.x += origin4.x;
        tempOffset.y += origin4.z;

        TerrainPage page4 = new TerrainPage(getName() + "Page4", blockSize,
                split, stepScale, heightBlock4, totalSize, tempOffset,
                offsetAmount);
        page4.setLocalTranslation(origin4);
        page4.quadrant = 4;
        this.attachChild(page4);

    }

    /**
     * <code>createQuadBlock</code> creates four child blocks from this page.
     */
    private void createQuadBlock(float[] heightMap) {
        // create 4 terrain blocks
        int quarterSize = size >> 2;
        int halfSize = size >> 1;
        int split = (size + 1) >> 1;

        Vector2f tempOffset = new Vector2f();
        offsetAmount += quarterSize;

        // 1 upper left
        float[] heightBlock1 = createHeightSubBlock(heightMap, 0, 0, split);

        Vector3f origin1 = new Vector3f(-halfSize * stepScale.x, 0, -halfSize
                * stepScale.z);

        tempOffset.x = offset.x;
        tempOffset.y = offset.y;
        tempOffset.x += origin1.x / 2;
        tempOffset.y += origin1.z / 2;

        TerrainBlock block1 = new TerrainBlock(getName() + "Block1", split,
                stepScale, heightBlock1, origin1, totalSize, tempOffset,
                offsetAmount);
        block1.setQuadrant((short) 1);
        this.attachChild(block1);
        block1.setModelBound(new BoundingBox());
        block1.updateModelBound();

        // 2 lower left
        float[] heightBlock2 = createHeightSubBlock(heightMap, 0, split - 1,
                split);

        Vector3f origin2 = new Vector3f(-halfSize * stepScale.x, 0, 0);

        tempOffset.x = offset.x;
        tempOffset.y = offset.y;
        tempOffset.x += origin1.x / 2;
        tempOffset.y += quarterSize * stepScale.z;

        TerrainBlock block2 = new TerrainBlock(getName() + "Block2", split,
                stepScale, heightBlock2, origin2, totalSize, tempOffset,
                offsetAmount);
        block2.setQuadrant((short) 2);
        this.attachChild(block2);
        block2.setModelBound(new BoundingBox());
        block2.updateModelBound();

        // 3 upper right
        float[] heightBlock3 = createHeightSubBlock(heightMap, split - 1, 0,
                split);

        Vector3f origin3 = new Vector3f(0, 0, -halfSize * stepScale.z);

        tempOffset.x = offset.x;
        tempOffset.y = offset.y;
        tempOffset.x += quarterSize * stepScale.x;
        tempOffset.y += origin3.z / 2;

        TerrainBlock block3 = new TerrainBlock(getName() + "Block3", split,
                stepScale, heightBlock3, origin3, totalSize, tempOffset,
                offsetAmount);
        block3.setQuadrant((short) 3);
        this.attachChild(block3);
        block3.setModelBound(new BoundingBox());
        block3.updateModelBound();

        // 4 lower right
        float[] heightBlock4 = createHeightSubBlock(heightMap, split - 1,
                split - 1, split);

        Vector3f origin4 = new Vector3f(0, 0, 0);

        tempOffset.x = offset.x;
        tempOffset.y = offset.y;
        tempOffset.x += quarterSize * stepScale.x;
        tempOffset.y += quarterSize * stepScale.z;

        TerrainBlock block4 = new TerrainBlock(getName() + "Block4", split,
                stepScale, heightBlock4, origin4, totalSize, tempOffset,
                offsetAmount);
        block4.setQuadrant((short) 4);
        this.attachChild(block4);
        block4.setModelBound(new BoundingBox());
        block4.updateModelBound();
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
     * Returns the total size of the terrain.
     * 
     * @return The terrain's total size.
     */
    public int getTotalSize() {
        return totalSize;
    }

    /**
     * Returns the size of this terrain page.
     * 
     * @return The current block size.
     */
    public int getSize() {
        return size;
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
     * Returns the offset amount this terrain block uses for textures.
     * 
     * @return The current offset amount.
     */
    public float getOffsetAmount() {
        return offsetAmount;
    }

    /**
     * Sets the value for the current offset amount to use when building texture
     * coordinates. Note that this does <b>NOT</b> rebuild the terrain at all.
     * This is mostly used for outside constructors of terrain blocks.
     * 
     * @param offset
     *            The new texture offset.
     */
    public void setOffset(Vector2f offset) {
        this.offset = offset;
    }

    /**
     * Sets the total size of the terrain . Note that this does <b>NOT</b>
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
     * Sets the size of this terrain page. Note that this does <b>NOT</b>
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
     * Sets the step scale of this terrain page's height map. Note that this
     * does <b>NOT</b> rebuild the terrain at all. This is mostly used for
     * outside constructors of terrain blocks.
     * 
     * @param stepScale
     *            The new step scale.
     */
    public void setStepScale(Vector3f stepScale) {
        this.stepScale = stepScale;
    }

    /**
     * Sets the offset of this terrain texture map. Note that this does <b>NOT</b>
     * rebuild the terrain at all. This is mostly used for outside constructors
     * of terrain blocks.
     * 
     * @param offsetAmount
     *            The new texture offset.
     */
    public void setOffsetAmount(float offsetAmount) {
        this.offsetAmount = offsetAmount;
    }

    /*
     * Deletes the VBO for this normal buffer, if any is present.
     */
    private void deleteNormalVBO(TerrainBlock block) {
        if (block.getVBOInfo() != null
                && block.getVBOInfo().getVBOIndexID() > 0) {
            DisplaySystem.getDisplaySystem().getRenderer().deleteVBO(
                    block.getNormalBuffer());
            block.getVBOInfo().setVBONormalID(-1);
        }
    }

    public void fixNormals() {
        if (children != null) {
            for (int x = children.size(); --x >= 0;) {
                Spatial child = children.get(x);
                if (child instanceof TerrainPage) {
                    ((TerrainPage) child).fixNormals();
                } else if (child instanceof TerrainBlock) {
                    TerrainBlock tb = (TerrainBlock) child;
                    TerrainBlock right = _findRightBlock(tb);
                    TerrainBlock down = _findDownBlock(tb);
                    int tbSize = tb.getSize();
                    if (right != null) {
                        float[] normData = new float[3];
                        for (int y = 0; y < tbSize; y++) {
                            int index1 = ((y + 1) * tbSize) - 1;
                            int index2 = (y * tbSize);
                            right.getNormalBuffer().position(index2 * 3);
                            right.getNormalBuffer().get(normData);
                            tb.getNormalBuffer().position(index1 * 3);
                            tb.getNormalBuffer().put(normData);
                        }
                        deleteNormalVBO(right);

                    }
                    if (down != null) {
                        int rowStart = ((tbSize - 1) * tbSize);
                        float[] normData = new float[3];
                        for (int z = 0; z < tbSize; z++) {
                            int index1 = rowStart + z;
                            int index2 = z;
                            down.getNormalBuffer().position(index2 * 3);
                            down.getNormalBuffer().get(normData);
                            tb.getNormalBuffer().position(index1 * 3);
                            tb.getNormalBuffer().put(normData);
                        }
                        deleteNormalVBO(down);
                    }
                    deleteNormalVBO(tb);
                }
            }
        }
    }

    private TerrainBlock getBlock(int quad) {
        if (children != null)
            for (int x = children.size(); --x >= 0;) {
                Spatial child = children.get(x);
                if (child instanceof TerrainBlock) {
                    TerrainBlock tb = (TerrainBlock) child;
                    if (tb.getQuadrant() == quad)
                        return tb;
                }
            }
        return null;
    }

    private TerrainPage getPage(int quad) {
        if (children != null)
            for (int x = children.size(); --x >= 0;) {
                Spatial child = children.get(x);
                if (child instanceof TerrainPage) {
                    TerrainPage tp = (TerrainPage) child;
                    if (tp.getQuadrant() == quad)
                        return tp;
                }
            }
        return null;
    }

    private TerrainBlock _findRightBlock(TerrainBlock tb) {
        if (tb.getQuadrant() == 1)
            return getBlock(3);
        else if (tb.getQuadrant() == 2)
            return getBlock(4);
        else if (tb.getQuadrant() == 3) {
            // find the page to the right and ask it for child 1.
            TerrainPage tp = _findRightPage();
            if (tp != null)
                return tp.getBlock(1);
        } else if (tb.getQuadrant() == 4) {
            // find the page to the right and ask it for child 2.
            TerrainPage tp = _findRightPage();
            if (tp != null)
                return tp.getBlock(2);
        }

        return null;
    }

    private TerrainBlock _findDownBlock(TerrainBlock tb) {
        if (tb.getQuadrant() == 1)
            return getBlock(2);
        else if (tb.getQuadrant() == 3)
            return getBlock(4);
        else if (tb.getQuadrant() == 2) {
            // find the page below and ask it for child 1.
            TerrainPage tp = _findDownPage();
            if (tp != null)
                return tp.getBlock(1);
        } else if (tb.getQuadrant() == 4) {
            TerrainPage tp = _findDownPage();
            if (tp != null)
                return tp.getBlock(3);
        }

        return null;
    }

    private TerrainPage _findRightPage() {
        if (getParent() == null || !(getParent() instanceof TerrainPage))
            return null;

        TerrainPage pPage = (TerrainPage) getParent();

        if (quadrant == 1)
            return pPage.getPage(3);
        else if (quadrant == 2)
            return pPage.getPage(4);
        else if (quadrant == 3) {
            TerrainPage tp = pPage._findRightPage();
            if (tp != null)
                return tp.getPage(1);
        } else if (quadrant == 4) {
            TerrainPage tp = pPage._findRightPage();
            if (tp != null)
                return tp.getPage(2);
        }

        return null;
    }

    private TerrainPage _findDownPage() {
        if (getParent() == null || !(getParent() instanceof TerrainPage))
            return null;

        TerrainPage pPage = (TerrainPage) getParent();

        if (quadrant == 1)
            return pPage.getPage(2);
        else if (quadrant == 3)
            return pPage.getPage(4);
        else if (quadrant == 2) {
            TerrainPage tp = pPage._findDownPage();
            if (tp != null)
                return tp.getPage(1);
        } else if (quadrant == 4) {
            TerrainPage tp = pPage._findDownPage();
            if (tp != null)
                return tp.getPage(3);
        }

        return null;
    }

    public static final float[] createHeightSubBlock(float[] heightMap, int x,
            int y, int side) {
    	float[] rVal = new float[side * side];
        int bsize = (int) FastMath.sqrt(heightMap.length);
        int count = 0;
        for (int i = y; i < side + y; i++) {
            for (int j = x; j < side + x; j++) {
                if (j < bsize && i < bsize)
                    rVal[count] = heightMap[j + (i * bsize)];
                count++;
            }
        }
        return rVal;
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
        int quad = findQuadrant(x, y);
        int split = (size + 1) >> 1;
        if (children != null)
            for (int i = children.size(); --i >= 0;) {
                Spatial spat = children.get(i);
                int col = x;
                int row = y;
                boolean match = false;

                // get the childs quadrant
                int childQuadrant = 0;
                if (spat instanceof TerrainPage) {
                    childQuadrant = ((TerrainPage) spat).getQuadrant();
                } else if (spat instanceof TerrainBlock) {
                    childQuadrant = ((TerrainBlock) spat).getQuadrant();
                }

                if (childQuadrant == 1 && (quad & 1) != 0) {
                    match = true;
                } else if (childQuadrant == 2 && (quad & 2) != 0) {
                    row = y - split + 1;
                    match = true;
                } else if (childQuadrant == 3 && (quad & 4) != 0) {
                    col = x - split + 1;
                    match = true;
                } else if (childQuadrant == 4 && (quad & 8) != 0) {
                    col = x - split + 1;
                    row = y - split + 1;
                    match = true;
                }

                if (match) {
                    if (spat instanceof TerrainPage) {
                        ((TerrainPage) spat)
                                .setHeightMapValue(col, row, newVal);
                    } else if (spat instanceof TerrainBlock) {
                        ((TerrainBlock) spat).setHeightMapValue(col, row,
                                newVal);
                    }
                }

            }
    }

    /**
     * <code>addHeightMapValue</code> adds to the value of this block's height
     * map at the given coords
     * 
     * @param x
     * @param y
     * @param toAdd
     */
    public void addHeightMapValue(int x, int y, float toAdd) {
        int quad = findQuadrant(x, y);
        int split = (size + 1) >> 1;
        if (children != null)
            for (int i = children.size(); --i >= 0;) {
                Spatial spat = children.get(i);
                int col = x;
                int row = y;
                boolean match = false;

                // get the childs quadrant
                int childQuadrant = 0;
                if (spat instanceof TerrainPage) {
                    childQuadrant = ((TerrainPage) spat).getQuadrant();
                } else if (spat instanceof TerrainBlock) {
                    childQuadrant = ((TerrainBlock) spat).getQuadrant();
                }

                if (childQuadrant == 1 && (quad & 1) != 0) {
                    match = true;
                } else if (childQuadrant == 2 && (quad & 2) != 0) {
                    row = y - split + 1;
                    match = true;
                } else if (childQuadrant == 3 && (quad & 4) != 0) {
                    col = x - split + 1;
                    match = true;
                } else if (childQuadrant == 4 && (quad & 8) != 0) {
                    col = x - split + 1;
                    row = y - split + 1;
                    match = true;
                }

                if (match) {
                    if (spat instanceof TerrainPage) {
                        ((TerrainPage) spat).addHeightMapValue(col, row, toAdd);
                    } else if (spat instanceof TerrainBlock) {
                        ((TerrainBlock) spat)
                                .addHeightMapValue(col, row, toAdd);
                    }
                }
            }
    }

    /**
     * <code>multHeightMapValue</code> multiplies the value of this block's
     * height map at the given coords by the value given.
     * 
     * @param x
     * @param y
     * @param toMult
     */
    public void multHeightMapValue(int x, int y, float toMult) {
        int quad = findQuadrant(x, y);
        int split = (size + 1) >> 1;
        if (children != null)
            for (int i = children.size(); --i >= 0;) {
                Spatial spat = children.get(i);
                int col = x;
                int row = y;
                boolean match = false;

                // get the childs quadrant
                int childQuadrant = 0;
                if (spat instanceof TerrainPage) {
                    childQuadrant = ((TerrainPage) spat).getQuadrant();
                } else if (spat instanceof TerrainBlock) {
                    childQuadrant = ((TerrainBlock) spat).getQuadrant();
                }

                if (childQuadrant == 1 && (quad & 1) != 0) {
                    match = true;
                } else if (childQuadrant == 2 && (quad & 2) != 0) {
                    row = y - split + 1;
                    match = true;
                } else if (childQuadrant == 3 && (quad & 4) != 0) {
                    col = x - split + 1;
                    match = true;
                } else if (childQuadrant == 4 && (quad & 8) != 0) {
                    col = x - split + 1;
                    row = y - split + 1;
                    match = true;
                }

                if (match) {
                    if (spat instanceof TerrainPage) {
                        ((TerrainPage) spat).multHeightMapValue(col, row,
                                toMult);
                    } else if (spat instanceof TerrainBlock) {
                        ((TerrainBlock) spat).multHeightMapValue(col, row,
                                toMult);
                    }
                }
            }
    }

    // a position can be in multiple quadrants, so use a bit anded value.
    private int findQuadrant(int x, int y) {
        int split = (size + 1) >> 1;
        int quads = 0;
        if (x < split && y < split)
            quads |= 1;
        if (x < split && y >= split - 1)
            quads |= 2;
        if (x >= split - 1 && y < split)
            quads |= 4;
        if (x >= split - 1 && y >= split - 1)
            quads |= 8;
        return quads;
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
        capsule.write(offset, "offset", Vector3f.ZERO);
        capsule.write(totalSize, "totalSize", 0);
        capsule.write(size, "size", 0);
        capsule.write(stepScale, "stepScale", new Vector2f());
        capsule.write(offsetAmount, "offsetAmount", 0);
        capsule.write(quadrant, "quadrant", (short) 1);
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        offset = (Vector2f) capsule.readSavable("offset", new Vector2f());
        totalSize = capsule.readInt("totalSize", 0);
        size = capsule.readInt("size", 0);
        stepScale = (Vector3f) capsule.readSavable("stepScale", new Vector3f());
        offsetAmount = capsule.readFloat("offsetAmount", 0);
        quadrant = capsule.readShort("quadrant", (short) 1);
    }
}