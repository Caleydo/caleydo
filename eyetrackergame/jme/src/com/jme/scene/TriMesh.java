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
import java.util.ArrayList;
import java.util.logging.Logger;

import com.jme.bounding.CollisionTree;
import com.jme.bounding.CollisionTreeManager;
import com.jme.intersection.CollisionResults;
import com.jme.math.FastMath;
import com.jme.math.Ray;
import com.jme.math.Triangle;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.system.JmeException;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.geom.BufferUtils;

/**
 * <code>TriMesh</code> defines a geometry mesh. This mesh defines a three
 * dimensional object via a collection of points, colors, normals and textures.
 * The points are referenced via a indices array. This array instructs the
 * renderer the order in which to draw the points, creating triangles based on the mode set.
 * 
 * @author Mark Powell
 * @author Joshua Slack
 * @version $Id: TriMesh.java 4796 2010-01-19 23:28:53Z thomas.trocha $
 */
public class TriMesh extends Geometry implements Serializable {
    private static final Logger logger = Logger.getLogger(TriMesh.class
            .getName());

    private static final long serialVersionUID = 2L;

    public enum Mode {
        /**
         * Every three vertices referenced by the indexbuffer will be considered
         * a stand-alone triangle.
         */
        Triangles,
        /**
         * The first three vertices referenced by the indexbuffer create a
         * triangle, from there, every additional vertex is paired with the two
         * preceding vertices to make a new triangle.
         */
        Strip, 
        /**
         * The first three vertices (V0, V1, V2) referenced by the indexbuffer
         * create a triangle, from there, every additional vertex is paired with
         * the preceding vertex and the initial vertex (V0) to make a new
         * triangle.
         */
        Fan;
    }

    protected transient IntBuffer indexBuffer;
    protected Mode mode = Mode.Triangles;
    protected int triangleQuantity;

    /**
     * Empty Constructor to be used internally only.
     */
    public TriMesh() {
        super();
    }

    /**
     * Constructor instantiates a new <code>TriMesh</code> object.
     * 
     * @param name
     *            the name of the scene element. This is required for
     *            identification and comparison purposes.
     */
    public TriMesh(String name) {
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
    public TriMesh(String name, FloatBuffer vertices, FloatBuffer normal,
            FloatBuffer color, TexCoords coords, IntBuffer indices) {

        super(name);

        reconstruct(vertices, normal, color, coords);

        if (null == indices) {
            logger.severe("Indices may not be null.");
            throw new JmeException("Indices may not be null.");
        }
        setIndexBuffer(indices);
        logger.fine("TriMesh created.");
    }

    /**
     * Recreates the geometric information of this TriMesh from scratch. The
     * index and vertex array must not be null, but the others may be. Every 3
     * indices define an index in the <code>vertices</code> array that
     * references a vertex of a triangle.
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
        recalcTriangleQuantity();
    }

    protected void recalcTriangleQuantity() {
        if (indexBuffer == null) {
            triangleQuantity = 0;
            return;
        }

        switch (mode) {
            case Triangles:
                triangleQuantity = indexBuffer.limit() / 3;
                break;
            case Strip:
            case Fan:
                triangleQuantity = indexBuffer.limit() - 2;
                break;
        }
    }

    /**
     * Returns the number of triangles contained in this mesh.
     */
    @Override
    public int getTriangleCount() {
        return triangleQuantity;
    }

    public void setTriangleQuantity(int triangleQuantity) {
        this.triangleQuantity = triangleQuantity;
    }

    /**
     * <code>draw</code> calls super to set the render state then passes
     * itself to the renderer. LOGIC: 1. If we're not RenderQueue calling draw
     * goto 2, if we are, goto 3 2. If we are supposed to use queue, add to
     * queue and RETURN, else 3 3. call super draw 4. tell renderer to draw me.
     * 
     * @param r
     *            the renderer to display
     */
    public void draw(Renderer r) {
        if (!r.isProcessingQueue()) {
            if (r.checkAndAdd(this))
                return;
        }

        super.draw(r);
        r.draw(this);
    }

    /**
     * Clears the buffers of this TriMesh. The buffers include its indexBuffer
     * only.
     */
    public void clearBuffers() {
        super.clearBuffers();
        setIndexBuffer(null);
    }

    /**
     * determines if a collision between this trimesh and a given spatial occurs
     * if it has true is returned, otherwise false is returned.
     */
    public boolean hasCollision(
            Spatial scene, boolean checkTriangles, int requiredOnBits) {
        if (this == scene || !isCollidable(requiredOnBits)
                || !scene.isCollidable(requiredOnBits)) {
            return false;
        }
        if (getWorldBound().intersects(scene.getWorldBound())) {
            if (scene instanceof Node) {
                Node parent = (Node) scene;
                for (int i = 0; i < parent.getQuantity(); i++) {
                    if (hasCollision(parent.getChild(i),
                                checkTriangles, requiredOnBits)) {
                        return true;
                    }
                }

                return false;
            }

            if (!checkTriangles) {
                return true;
            }

            return hasTriangleCollision((TriMesh) scene, requiredOnBits);
        }

        return false;
    }

    /**
     * determines if this TriMesh has made contact with the give scene. The
     * scene is recursively transversed until a trimesh is found, at which time
     * the two trimesh OBBTrees are then compared to find the triangles that
     * hit.
     */
    public void findCollisions(
            Spatial scene, CollisionResults results, int requiredOnBits) {
        if (this == scene || !isCollidable(requiredOnBits)
                || !scene.isCollidable(requiredOnBits)) {
            return;
        }

        if (getWorldBound().intersects(scene.getWorldBound())) {
            if (scene instanceof Node) {
                Node parent = (Node) scene;
                for (int i = 0; i < parent.getQuantity(); i++) {
                    findCollisions(parent.getChild(i), results, requiredOnBits);
                }
            } else {
                results.addCollision(this, (Geometry) scene);
            }
        }
    }

    /**
     * Convenience wrapper for hasTriangleCollision(TriMesh, int) using default
     * collision mask (bit 1 set).
     */
    final public boolean hasTriangleCollision(TriMesh toCheck) {
        return hasTriangleCollision(toCheck, 1);
    }

    /**
     * This function checks for intersection between this trimesh and the given
     * one. On the first intersection, true is returned.
     * 
     * @param toCheck The intersection testing mesh.
     * @param requiredOnBits Collision will only be considered if both 'this'
     *        and 'toCheck' have these bits of their collision masks set.
     * @return True if they intersect.
     */
    public boolean hasTriangleCollision(TriMesh toCheck, int requiredOnBits) {
        CollisionTree thisCT = CollisionTreeManager.getInstance()
                .getCollisionTree(this);
        CollisionTree checkCT = CollisionTreeManager.getInstance()
                .getCollisionTree(toCheck);

        if (thisCT == null || checkCT == null || !isCollidable(requiredOnBits)
                || !toCheck.isCollidable(requiredOnBits)) {
            return false;
        }
        thisCT.getBounds().transform(worldRotation, worldTranslation,
                worldScale, thisCT.getWorldBounds());
        return thisCT.intersect(checkCT);
    }

    /**
     * This function finds all intersections between this trimesh and the
     * checking one. The intersections are stored as Integer objects of Triangle
     * indexes in each of the parameters.
     * 
     * @param toCheck
     *            The TriMesh to check.
     * @param thisIndex
     *            The array of triangle indexes intersecting in this mesh.
     * @param otherIndex
     *            The array of triangle indexes intersecting in the given mesh.
     */
    public void findTriangleCollision(TriMesh toCheck,
            ArrayList<Integer> thisIndex, ArrayList<Integer> otherIndex) {

        CollisionTree myTree = CollisionTreeManager.getInstance()
                .getCollisionTree(this);
        CollisionTree otherTree = CollisionTreeManager.getInstance()
                .getCollisionTree(toCheck);

        if (myTree == null || otherTree == null) {
            return;
        }

        myTree.getBounds().transform(worldRotation, worldTranslation,
                worldScale, myTree.getWorldBounds());
        myTree.intersect(otherTree, thisIndex, otherIndex);
    }

    /**
     * Stores in the <code>storage</code> array the indices of triangle
     * <code>i</code>. If <code>i</code> is an invalid index, or if
     * <code>storage.length<3</code>, then nothing happens
     * 
     * @param i
     *            The index of the triangle to get.
     * @param storage
     *            The array that will hold the i's indexes.
     */
    public void getTriangle(int i, int[] storage) {
        if (i < getTriangleCount() && storage.length >= 3) {
            IntBuffer indices = getIndexBuffer();
            storage[0] = indices.get(getVertIndex(i, 0));
            storage[1] = indices.get(getVertIndex(i, 1));
            storage[2] = indices.get(getVertIndex(i, 2));
        }
    }

    /**
     * Stores in the <code>vertices</code> array the vertex values of triangle
     * <code>i</code>. If <code>i</code> is an invalid triangle index,
     * nothing happens.
     * 
     * @param i
     * @param vertices
     */
    public void getTriangle(int i, Vector3f[] vertices) {
        if (vertices == null) {
            vertices = new Vector3f[3];
        }
        if (i < getTriangleCount() && i >= 0) {
            for (int x = 0; x < 3; x++) {
                if (vertices[x] == null) {
                    vertices[x] = new Vector3f();
                }

                BufferUtils.populateFromBuffer(vertices[x], getVertexBuffer(),
                        getIndexBuffer().get(getVertIndex(i, x)));
            }
        }
    }

    
    /**
     * Convenience wrapper for findTrianglePick(Ray, ArrayList<Integer>,int) using default
     * collision mask (bit 1 set).
     */
    public void findTrianglePick(Ray toTest, ArrayList<Integer> results) {
    	findTrianglePick(toTest, results, 1);
    }

    
    /**
     * <code>findTrianglePick</code> determines the triangles of this trimesh
     * that are being touched by the ray. The indices of the triangles are
     * stored in the provided ArrayList.
     * 
     * @param toTest
     *            the ray to test. The direction of the ray must be normalized
     *            (length 1).
     * @param results
     *            the indices to the triangles.
     * @param requiredOnBits Pick will only be considered if 'this'
     *            has these bits of its collision masks set.
     */
    public void findTrianglePick(Ray toTest, ArrayList<Integer> results,int requiredOnBits) {
        if (worldBound == null || !isCollidable(requiredOnBits)) {
            return;
        }

        if (worldBound.intersects(toTest)) {
            CollisionTree ct = CollisionTreeManager.getInstance()
                    .getCollisionTree(this);
            if (ct != null) {
                ct.getBounds().transform(getWorldRotation(),
                        getWorldTranslation(), getWorldScale(),
                        ct.getWorldBounds());
                ct.intersect(toTest, results);
            }
        }
    }

    /**
     * Return this mesh object as triangles. Every 3 vertices returned compose a
     * single triangle.
     * 
     * @param verts
     *            a storage array to place the results in
     * @return view of current mesh as group of triangle vertices
     */
    public Vector3f[] getMeshAsTrianglesVertices(Vector3f[] verts) {
        int maxCount = getTriangleCount() * 3;
        if (verts == null || verts.length != maxCount)
            verts = new Vector3f[maxCount];
        getIndexBuffer().rewind();
        for (int i = 0; i < maxCount; i++) {
            if (verts[i] == null)
                verts[i] = new Vector3f();
            int index = getVertIndex(i / 3, i % 3);
            BufferUtils.populateFromBuffer(verts[i], getVertexBuffer(),
                    getIndexBuffer().get(index));
        }
        return verts;
    }

    protected int getVertIndex(int triangle, int point) {
        int index = 0, i = (triangle * 3) + point;
        switch (mode) {
            case Triangles:
                index = i;
                break;
            case Strip:
                index = (i / 3) + (i % 3);
                break;
            case Fan:
                if (i % 3 == 0)
                    index = 0;
                else {
                    index = (i % 3);
                    index = ((i - index) / 3) + index;
                }
                break;
        }
        return index;
    }

    public int[] getTriangleIndices(int[] indices) {
        int maxCount = getTriangleCount();
        if (indices == null || indices.length != maxCount)
            indices = new int[maxCount];

        for (int i = 0, tLength = maxCount; i < tLength; i++) {
            indices[i] = i;
        }
        return indices;
    }

    public Triangle[] getMeshAsTriangles(Triangle[] tris) {
        int maxCount = getTriangleCount();
        if (tris == null || tris.length != maxCount)
            tris = new Triangle[maxCount];

        for (int i = 0, tLength = maxCount; i < tLength; i++) {
            Vector3f vec1 = new Vector3f();
            Vector3f vec2 = new Vector3f();
            Vector3f vec3 = new Vector3f();

            Triangle t = tris[i];
            if (t == null) {
                t = new Triangle(getVector(i * 3 + 0, vec1), getVector(
                        i * 3 + 1, vec2), getVector(i * 3 + 2, vec3));
                tris[i] = t;
            } else {
                t.set(0, getVector(i * 3 + 0, vec1));
                t.set(1, getVector(i * 3 + 1, vec2));
                t.set(2, getVector(i * 3 + 2, vec3));
            }
            // t.calculateCenter();
            t.setIndex(i);
        }
        return tris;
    }

    private Vector3f getVector(int index, Vector3f store) {
        int vertIndex = getVertIndex(index / 3, index % 3);
        BufferUtils.populateFromBuffer(store, getVertexBuffer(),
                getIndexBuffer().get(vertIndex));
        return store;
    }

    public int getMaxIndex() {
        if (indexBuffer == null)
            return -1;

        switch (mode) {
            case Triangles:
                return triangleQuantity * 3;
            case Strip:
            case Fan:
                triangleQuantity = indexBuffer.limit() - 2;
                return triangleQuantity + 2;
        }
        return -1;
    }

    /**
     * Returns a random point on the surface of a randomly selected triangle on
     * the mesh
     * 
     * @param fill
     *            The resulting selected point
     * @param work
     *            Used in calculations to minimize memory creation overhead
     * @return The resulting selected point
     */
    public Vector3f randomPointOnTriangles(Vector3f fill, Vector3f work) {
        if (getVertexBuffer() == null || getIndexBuffer() == null)
            return null;
        int tri = (int) (FastMath.nextRandomFloat() * getTriangleCount());
        int pntA = getIndexBuffer().get(getVertIndex(tri, 0));
        int pntB = getIndexBuffer().get(getVertIndex(tri, 1));
        int pntC = getIndexBuffer().get(getVertIndex(tri, 2));

        float b = FastMath.nextRandomFloat();
        float c = FastMath.nextRandomFloat();

        if (b + c > 1) {
            b = 1 - b;
            c = 1 - c;
        }

        float a = 1 - b - c;

        if (fill == null)
            fill = new Vector3f();

        BufferUtils.populateFromBuffer(work, getVertexBuffer(), pntA);
        work.multLocal(a);
        fill.set(work);

        BufferUtils.populateFromBuffer(work, getVertexBuffer(), pntB);
        work.multLocal(b);
        fill.addLocal(work);

        BufferUtils.populateFromBuffer(work, getVertexBuffer(), pntC);
        work.multLocal(c);
        fill.addLocal(work);

        localToWorld(fill, fill);

        return fill;
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
        capsule.write(mode, "mode", Mode.Triangles);
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        indexBuffer = capsule.readIntBuffer("indexBuffer", null);
        recalcTriangleQuantity();
        mode = (Mode) capsule.readEnum("mode", Mode.class, Mode.Triangles);
    }
}
