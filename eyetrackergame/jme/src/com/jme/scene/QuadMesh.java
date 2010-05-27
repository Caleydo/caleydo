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

package com.jme.scene;

import java.io.IOException;
import java.io.Serializable;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.logging.Logger;

import com.jme.intersection.CollisionResults;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.system.JmeException;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.geom.BufferUtils;

/**
 * <code>QuadMesh</code> defines a geometry mesh. This mesh defines a three
 * dimensional object via a collection of points, colors, normals and textures.
 * The points are referenced via a indices array. This array instructs the
 * renderer the order in which to draw the points, creating quads based on the mode set.
 * 
 * @author Joshua Slack
 * @version $Id: QuadMesh.java 4821 2010-02-09 19:41:45Z thomas.trocha $
 */
public class QuadMesh extends Geometry implements Serializable {
    private static final Logger logger = Logger.getLogger(QuadMesh.class
            .getName());

    private static final long serialVersionUID = 2L;

    public enum Mode {
        /**
         * Every four vertices referenced by the indexbuffer will be considered
         * a stand-alone quad.
         */
        Quads,
        /**
         * The first four vertices referenced by the indexbuffer create a
         * triangle, from there, every two additional vertices are paired with
         * the two preceding vertices to make a new quad.
         */
        Strip;
    }

    protected transient IntBuffer indexBuffer;
    protected Mode mode = Mode.Quads;
    protected int quadQuantity;

    private static Vector3f[] quads;

    /**
     * Empty Constructor to be used internally only.
     */
    public QuadMesh() {
        super();
    }

    /**
     * Constructor instantiates a new <code>TriMesh</code> object.
     * 
     * @param name
     *            the name of the scene element. This is required for
     *            identification and comparison purposes.
     */
    public QuadMesh(String name) {
        super(name);
    }

    /**
     * Constructor instantiates a new <code>TriMesh</code> object. Provided
     * are the attributes that make up the mesh all attributes may be null,
     * except for vertices and indices.
     * 
     * @param name
     *            the name of the scene element. This is required for
     *            identification and comparison purposes.
     * @param vertices
     *            the vertices of the geometry.
     * @param normal
     *            the normals of the geometry.
     * @param color
     *            the colors of the geometry.
     * @param coords
     *            the texture coordinates of the mesh.
     * @param indices
     *            the indices of the vertex array.
     */
    public QuadMesh(String name, FloatBuffer vertices, FloatBuffer normal,
            FloatBuffer color, TexCoords coords, IntBuffer indices) {

        super(name);

        reconstruct(vertices, normal, color, coords);

        if (null == indices) {
            logger.severe("Indices may not be null.");
            throw new JmeException("Indices may not be null.");
        }
        setIndexBuffer(indices);
        logger.fine("QuadMesh created.");
    }

    /**
     * Recreates the geometric information of this TriMesh from scratch. The
     * index and vertex array must not be null, but the others may be. Every 3
     * indices define an index in the <code>vertices</code> array that
     * References a vertex of a triangle.
     * 
     * @param vertices
     *            The vertex information for this TriMesh.
     * @param normal
     *            The normal information for this TriMesh.
     * @param color
     *            The color information for this TriMesh.
     * @param coords
     *            The texture information for this TriMesh.
     * @param indices
     *            The index information for this TriMesh.
     */
    public void reconstruct(FloatBuffer vertices, FloatBuffer normal,
            FloatBuffer color, TexCoords coords, IntBuffer indices) {
        super.reconstruct(vertices, normal, color, coords);

        if (null == indices) {
            logger.severe("Indices may not be null.");
            throw new JmeException("Indices may not be null.");
        }
        setIndexBuffer(indices);
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Mode getMode() {
        return mode;
    }

    public IntBuffer getIndexBuffer() {
        return indexBuffer;
    }

    public void setIndexBuffer(IntBuffer indices) {
        this.indexBuffer = indices;
        recalcQuadQuantity();
    }

    protected void recalcQuadQuantity() {
        if (indexBuffer == null) {
            quadQuantity = 0;
            return;
        }
        
        switch (mode) {
            case Quads:
                quadQuantity = indexBuffer.limit() / 4;
                break;
            case Strip:
                quadQuantity = indexBuffer.limit() / 2 - 1;
                break;
        }
    }

    /**
     * Returns the number of triangles contained in this mesh.
     */
    public int getQuadCount() {
        return quadQuantity;
    }

    public void setQuadQuantity(int quadQuantity) {
        this.quadQuantity = quadQuantity;
    }

    /**
     * Clears the buffers of this QuadMesh. The buffers include its indexBuffer
     * only.
     */
    public void clearBuffers() {
        super.clearBuffers();
        setIndexBuffer(null);
    }
    
    public static Vector3f[] getQuads() {
        return quads;
    }

    public static void setQuads(Vector3f[] quads) {
        QuadMesh.quads = quads;
    }

    /**
     * Stores in the <code>storage</code> array the indices of quad
     * <code>i</code>. If <code>i</code> is an invalid index, or if
     * <code>storage.length<4</code>, then nothing happens
     * 
     * @param i
     *            The index of the quad to get.
     * @param storage
     *            The array that will hold the i's indexes.
     */
    public void getQuad(int i, int[] storage) {
        if (i < getQuadCount() && storage.length >= 4) {
            IntBuffer indices = getIndexBuffer();
            storage[0] = indices.get(getVertIndex(i, 0));
            storage[1] = indices.get(getVertIndex(i, 1));
            storage[2] = indices.get(getVertIndex(i, 2));
            storage[3] = indices.get(getVertIndex(i, 3));

        }
    }

    /**
     * Stores in the <code>vertices</code> array the vertex values of quad
     * <code>i</code>. If <code>i</code> is an invalid quad index,
     * nothing happens.
     * 
     * @param i
     * @param vertices
     */
    public void getQuad(int i, Vector3f[] vertices) {
        if (i < getQuadCount() && i >= 0) {
            for (int x = 0; x < 4; x++) {
                if (vertices[x] == null)
                    vertices[x] = new Vector3f();
                
                BufferUtils.populateFromBuffer(vertices[x], getVertexBuffer(),
                        getIndexBuffer().get(getVertIndex(i, x)));
            }
        }
    }

    /**
     * Return this mesh object as quads. Every 4 vertices returned compose a
     * single quad.
     * 
     * @param verts
     *            a storage array to place the results in
     * @return view of current mesh as group of quad vertices
     */
    public Vector3f[] getMeshAsQuadsVertices(Vector3f[] verts) {
        int maxCount = getQuadCount() * 4;
        if (verts == null
                || verts.length != maxCount)
            verts = new Vector3f[maxCount];
        getIndexBuffer().rewind();
        for (int i = 0; i < maxCount; i++) {
            if (verts[i] == null)
                verts[i] = new Vector3f();
            int index = getVertIndex(i/4, i%4);
            BufferUtils.populateFromBuffer(verts[i], getVertexBuffer(),
                    getIndexBuffer().get(index));
        }
        return verts;
    }
    
    protected int getVertIndex(int quad, int point) {
        switch (mode) {
            case Quads:
                return quad * 4 + point;
            case Strip:
                return quad * 2 + point;
            default:
                throw new JmeException("mode is set to invalid type: "+mode);
        }
    }
    
    public int getMaxIndex() {
        if (indexBuffer == null) return -1;
        
        switch (mode) {
            case Quads:
                return quadQuantity * 4;
            case Strip:
                return 2 + (quadQuantity * 2);
        }
        
        return 0;
    }    

    /**
     * Used with Serialization. Do not call this directly.
     * 
     * @param s
     * @throws IOException
     * @see java.io.Serializable
     */
    private void writeObject(java.io.ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        if (getIndexBuffer() == null)
            s.writeInt(0);
        else {
            s.writeInt(getIndexBuffer().limit());
            getIndexBuffer().rewind();
            for (int x = 0, len = getIndexBuffer().limit(); x < len; x++)
                s.writeInt(getIndexBuffer().get());
        }
    }

    /**
     * Used with Serialization. Do not call this directly.
     * 
     * @param s
     * @throws IOException
     * @throws ClassNotFoundException
     * @see java.io.Serializable
     */
    private void readObject(java.io.ObjectInputStream s) throws IOException,
            ClassNotFoundException {
        s.defaultReadObject();
        int len = s.readInt();
        if (len == 0) {
            setIndexBuffer(null);
        } else {
            IntBuffer buf = BufferUtils.createIntBuffer(len);
            for (int x = 0; x < len; x++)
                buf.put(s.readInt());
            setIndexBuffer(buf);            
        }
    }
    
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(indexBuffer, "indexBuffer", null);
        capsule.write(mode, "mode", Mode.Quads);
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        indexBuffer = capsule.readIntBuffer("indexBuffer", null);
        recalcQuadQuantity();
        mode = (Mode) capsule.readEnum("mode", Mode.class, Mode.Quads);
    }

    public void draw(Renderer r) {
        if (!r.isProcessingQueue()) {
            if (r.checkAndAdd(this))
                return;
        }

        r.draw(this);
    }

    @Override
    public void findCollisions(
            Spatial scene, CollisionResults results, int requiredOnBits) {
        throw new RuntimeException("method findCollision(...) not available for QuadMesh, please use TriMesh for collision-detection! ("+toString()+")");
    }

    @Override
    public boolean hasCollision(
            Spatial scene, boolean checkTriangles, int requiredOnBits) {
        throw new RuntimeException("method hasCollision(...) not available for QuadMesh, please use TriMesh for collision-detection! ("+toString()+")");
    }
}

