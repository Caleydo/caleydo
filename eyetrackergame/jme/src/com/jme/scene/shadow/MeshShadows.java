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

package com.jme.scene.shadow;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.BitSet;

import com.jme.light.DirectionalLight;
import com.jme.light.Light;
import com.jme.light.PointLight;
import com.jme.math.Plane;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.state.LightState;
import com.jme.util.geom.BufferUtils;

/**
 * <code>MeshShadows</code> A grouping of the ShadowVolumes for a single
 * TriMesh.
 * 
 * @author Mike Talbot (some code from a shadow implementation written Jan 2005)
 * @author Joshua Slack
 * @version $Id: MeshShadows.java 4639 2009-08-29 00:34:05Z skye.book $
 */
public class MeshShadows {
    private static final long serialVersionUID = 1L;

    /** the distance to which shadow volumes will be projected */
    protected float projectionLength = 1000;

    /** The triangles of our occluding mesh (one per triangle in the mesh) */
    protected ArrayList<ShadowTriangle> faces;

    /** A bitset used for storing directional flags. */
    protected BitSet facing;

    /** The mesh that is the target of this shadow volume */
    protected TriMesh target = null;

    /** The arraylist of shadowvolumes in this grouping */
    protected ArrayList<ShadowVolume> volumes = new ArrayList<ShadowVolume>();

    /** The world rotation of the target at the last mesh construction */
    protected Quaternion oldWorldRotation = new Quaternion();

    /** The world translation of the trimesh at the last mesh construction */
    protected Vector3f oldWorldTranslation = new Vector3f();

    /** The world scale of the trimesh at the last mesh construction */
    protected Vector3f oldWorldScale = new Vector3f();

    private int maxIndex;

    private int vertCount;

    public static long throttle = 1000 / 50; // 50 x a sec
    private long lastTime;
    private boolean nextTime = true;

    /** Static computation field */
    protected static Vector3f compVect = new Vector3f();

    /**
     * Constructor for <code>MeshShadows</code>
     * 
     * @param target
     *            the mesh that will be the target of the shadow volumes held in
     *            this grouping
     */
    public MeshShadows(TriMesh target) {
        this.target = target;
        recreateFaces();
    }

    /**
     * <code>createGeometry</code> creates or updates the ShadowVolume
     * geometries for the target TriMesh - one for each applicable Light in the
     * given LightState. Only Directional and Point lights are currently
     * supported. ShadowVolume geometry is only regenerated when light or occluder
     * aspects change.
     * 
     * @param lightState
     *            is the current lighting state
     */
    public void createGeometry(LightState lightState) {
        if (target.getTriangleCount() != maxIndex
                || target.getVertexCount() != vertCount) {
            recreateFaces();
        }

        // Holds a copy of the mesh vertices transformed to world coordinates
        FloatBuffer vertex = null;

        // Ensure that we have some potential lights to cast shadows!
        if (lightState.getQuantity() != 0) {
            LightState lights = lightState;

            // Update the cache of lights - if still sane, return
            if (updateCache(lights))
                return;

            // Now scan through each light and create the shadow volume
            for (int l = 0; l < lights.getQuantity(); l++) {
                Light light = lights.get(l);

                // Make sure we can (or want to) handle this light
                if (!light.isShadowCaster()
                        || (!(light.getType() == Light.Type.Directional) && !(light
                                .getType() == Light.Type.Point)))
                    continue;

                // Get the volume assoicated with this light
                ShadowVolume lv = getShadowVolume(light);

                // See if this light has not been seen before!
                if (lv == null) {
                    // Create a new light volume
                    lv = new ShadowVolume(light);
                    volumes.add(lv);
                    lv.setUpdate(true);
                }

                // See if the volume requires updating
                if (lv.isUpdate()) {
                    lv.setUpdate(false);

                    if (!target.isCastsShadows()) {
                        lv.setCullHint(Spatial.CullHint.Always);
                        continue;
                    }

                    lv.setCullHint(Spatial.CullHint.Dynamic);

                    // Translate the vertex information from the mesh to
                    // world
                    // coordinates if
                    // we are going to do any work
                    if (vertex == null) {
                        vertex = target.getWorldCoords(null);
                    }

                    // Find out which triangles are facing the light
                    // triangle will be set true for faces towards the light
                    processFaces(vertex, light, target);

                    // Get the edges that are in shadow
                    ShadowEdge[] edges = getShadowEdges();

                    // Now we need to develop a mesh based on projecting
                    // these
                    // edges
                    // to infinity in the direction of the light
                    int length = edges.length;

                    // Create arrays to hold the shadow mesh
                    FloatBuffer shadowVertex = lv.getVertexBuffer();
                    if (shadowVertex == null
                            || shadowVertex.capacity() < length * 12)
                        shadowVertex = BufferUtils
                                .createVector3Buffer(length * 4);
                    FloatBuffer shadowNormal = lv.getNormalBuffer();
                    if (shadowNormal == null
                            || shadowNormal.capacity() < length * 12)
                        shadowNormal = BufferUtils
                                .createVector3Buffer(length * 4);
                    IntBuffer shadowIndex = lv.getIndexBuffer();
                    if (shadowIndex == null
                            || shadowIndex.capacity() < length * 6)
                        shadowIndex = BufferUtils.createIntBuffer(length * 6);

                    shadowVertex.limit(length * 12);
                    shadowNormal.limit(length * 12);
                    shadowIndex.limit(length * 6);

                    // Create quads out of the edge vertices
                    createShadowQuads(vertex, edges, shadowVertex,
                            shadowNormal, shadowIndex, light);

                    // Rebuild the TriMesh
                    lv.reconstruct(shadowVertex, shadowNormal, null, null,
                            shadowIndex);
                    shadowVertex.rewind();
                    lv.setVertexCount(shadowVertex.remaining() / 3);
                    shadowIndex.rewind();
                    lv.setTriangleQuantity(
                            shadowIndex.remaining() / 3);
                    lv.updateModelBound();
                    if ((target.getLocks() & Spatial.LOCKED_SHADOWS) != 0)
                        lv.lock();
                }

            }

        } else {
            // There are no volumes
            volumes.clear();
        }

    }

    /**
     * void <code>createShadowQuad</code> Creates projected quads from a
     * series of edges and vertices and stores them in the output shadowXXXX
     * arrays
     * 
     * @param vertex
     *            array of world coordinate vertices for the target TriMesh
     * @param edges
     *            a collection of edges that will be projected
     * @param shadowVertex
     * @param shadowNormal
     * @param shadowIndex
     * @param light
     *            light casting shadow
     */
    private void createShadowQuads(FloatBuffer vertex, ShadowEdge[] edges,
            FloatBuffer shadowVertex, FloatBuffer shadowNormal,
            IntBuffer shadowIndex, Light light) {
        Vector3f p0 = new Vector3f(), p1 = new Vector3f(), p2 = new Vector3f(), p3 = new Vector3f();

        // Setup a flag to indicate which type of light this is
        boolean directional = (light.getType() == Light.Type.Directional);

        Vector3f direction = new Vector3f();
        Vector3f location = new Vector3f();
        if (directional) {
            direction = ((DirectionalLight) light).getDirection();
        } else {
            location = ((PointLight) light).getLocation();
        }

        // Loop for each edge
        for (int e = 0; e < edges.length; e++) {
            // get the two known vertices
            BufferUtils.populateFromBuffer(p0, vertex, edges[e].p0);
            BufferUtils.populateFromBuffer(p3, vertex, edges[e].p1);

            // Calculate the projection of p0
            if (!directional) {
                direction = p0.subtract(location, direction).normalizeLocal();
            }
            // Project the other edges to infinity
            p1 = direction.mult(projectionLength, p1).addLocal(p0);
            if (!directional) {
                direction = p3.subtract(location, direction).normalizeLocal();
            }
            p2 = direction.mult(projectionLength).addLocal(p3);

            // Now we need to add a quad to the model
            int vertexOffset = e * 4;
            BufferUtils.setInBuffer(p0, shadowVertex, vertexOffset);
            BufferUtils.setInBuffer(p1, shadowVertex, vertexOffset + 1);
            BufferUtils.setInBuffer(p2, shadowVertex, vertexOffset + 2);
            BufferUtils.setInBuffer(p3, shadowVertex, vertexOffset + 3);

            // Calculate the normal
            Vector3f n = p1.subtract(p0).normalizeLocal().crossLocal(
                    p3.subtract(p0).normalizeLocal()).normalizeLocal();
            BufferUtils.setInBuffer(n, shadowNormal, vertexOffset);
            BufferUtils.setInBuffer(n, shadowNormal, vertexOffset + 1);
            BufferUtils.setInBuffer(n, shadowNormal, vertexOffset + 2);
            BufferUtils.setInBuffer(n, shadowNormal, vertexOffset + 3);

            // Add the indices
            int indexOffset = e * 6;
            shadowIndex.put(indexOffset + 0, vertexOffset + 0);
            shadowIndex.put(indexOffset + 1, vertexOffset + 1);
            shadowIndex.put(indexOffset + 2, vertexOffset + 3);
            shadowIndex.put(indexOffset + 3, vertexOffset + 3);
            shadowIndex.put(indexOffset + 4, vertexOffset + 1);
            shadowIndex.put(indexOffset + 5, vertexOffset + 2);
        }
    }

    // Get the intersection of a line segment and a plane in terms of t>=0 t<=1
    // for positions within the segment
    protected float getIntersectTime(Plane p, Vector3f p0, Vector3f v) {

        float divider = p.normal.dot(v);
        if (divider == 0)
            return -Float.MAX_VALUE;
        return p.normal.dot(p.normal.mult(p.constant, compVect).subtractLocal(
                p0))
                / divider;

    }

    /**
     * <code>getShadowEdges</code>
     * 
     * @return an array of the edges which are in shadow
     */
    private ShadowEdge[] getShadowEdges() {
        // Create a dynamic structure to contain the vertices
        ArrayList<ShadowEdge> shadowEdges = new ArrayList<ShadowEdge>();
        // Now work through the faces
        for (int t = 0; t < maxIndex; t++) {
            // Check whether this is a front facing triangle
            if (facing.get(t)) {
                ShadowTriangle tri = faces.get(t);
                // If it is then check if any of the edges are connected to a
                // back facing triangle or are unconnected
                checkAndAdd(tri.edge1, shadowEdges);
                checkAndAdd(tri.edge2, shadowEdges);
                checkAndAdd(tri.edge3, shadowEdges);
            }
        }
        return shadowEdges.toArray(new ShadowEdge[0]);
    }

    private void checkAndAdd(ShadowEdge edge, ArrayList<ShadowEdge> shadowEdges) {
        // Is the edge connected
        if (edge.triangle == ShadowTriangle.INVALID_TRIANGLE) {
            // if not then add the edge
            shadowEdges.add(edge);

        }
        // check if the connected triangle is back facing
        else if (!facing.get(edge.triangle)) {
            // if it is then add the edge
            shadowEdges.add(edge);

        }
    }

    /**
     * <code>processFaces</code> Determines whether faces of a TriMesh face
     * the light
     * 
     * @param triangle
     *            an array of boolean values that will indicate whether a
     *            triangle is front or back facing
     * @param light
     *            the light to use
     * @param target
     *            the TriMesh that will be shadowed and holds the triangles for
     *            testing
     */
    private void processFaces(FloatBuffer vertex, Light light, TriMesh target) {
        Vector3f v0 = new Vector3f();
        Vector3f v1 = new Vector3f();
        boolean directional = light.getType() == Light.Type.Directional;
        Vector3f vLight = null;
        int[] index = BufferUtils.getIntArray(target.getIndexBuffer());
        if (index == null) return;

        if (directional) {
            vLight = ((DirectionalLight) light).getDirection();
        }

        // Loop through each triangle and see if it is back or front facing
        for (int t = 0, tri = 0; t < index.length; tri++, t += 3) {
            // Calculate a normal to the plane
            BufferUtils.populateFromBuffer(compVect, vertex, index[t]);
            BufferUtils.populateFromBuffer(v0, vertex, index[t + 1]);
            BufferUtils.populateFromBuffer(v1, vertex, index[t + 2]);
            v1.subtractLocal(v0).normalizeLocal();
            v0.subtractLocal(compVect).normalizeLocal();
            Vector3f n = v1.cross(v0);

            // Some kind of bodge for a direction to a point light - TODO
            // improve this
            if (!directional) {
                vLight = compVect.subtract(((PointLight) light).getLocation())
                        .normalizeLocal();
            }
            // See if it is back facing
            facing.set(tri, (n.dot(vLight) >= 0));
        }
    }

    /**
     * <code>updateCache</code> Updates the cache to show which models need
     * rebuilding
     * 
     * @param lights
     *            a LightState containing the lights to check against
     * @return returns <code>true</code> if the cache was not invalidated
     */
    private boolean updateCache(LightState lights) {
        boolean voidLights = false;
        boolean same = true;

        float passTime = System.currentTimeMillis() - lastTime;

        if (nextTime) {
            if (passTime > throttle) {
                voidLights = true;
                nextTime = false;
            }
        } else {
            // First see if we need to void all volumes as the target has
            // changed
            if (!target.getWorldRotation().equals(oldWorldRotation))
                voidLights = true;
            else if (!target.getWorldScale().equals(oldWorldScale))
                voidLights = true;
            else if (!target.getWorldTranslation().equals(
                    oldWorldTranslation))
                voidLights = true;
        }
        // Configure the current settings
        oldWorldRotation.set(target.getWorldRotation());
        oldWorldScale.set(target.getWorldScale());
        oldWorldTranslation.set(target.getWorldTranslation());

        if (target.hasDirtyVertices()) {
            target.setHasDirtyVertices(false);
            if (!voidLights)
                if (passTime > throttle) {
                    voidLights = true;
                    nextTime = false;
                } else
                    nextTime = true;
        }

        // See if we need to update all of the volumes
        if (voidLights) {
            for (int v = 0, vSize = volumes.size(); v < vSize; v++) {
                ShadowVolume sv = volumes.get(v);
                sv.setUpdate(true);
            }
            lastTime = System.currentTimeMillis();
            nextTime = false;
            return false;
        }

        // Loop through the lights to see if any have changed
        for (int i = lights.getQuantity(); --i >= 0;) {
            Light testLight = lights.get(i);
            if (!testLight.isShadowCaster())
                continue;
            ShadowVolume v = getShadowVolume(testLight);
            if (v != null) {
                if (testLight.getType() == Light.Type.Directional) {
                    DirectionalLight dl = (DirectionalLight) testLight;
                    if (!v.direction.equals(dl.getDirection())) {
                        v.setUpdate(true);
                        v.getDirection().set(dl.getDirection());
                        same = false;
                    }
                } else if (testLight.getType() == Light.Type.Point) {
                    PointLight pl = (PointLight) testLight;
                    if (!v.getPosition().equals(pl.getLocation())) {
                        v.setUpdate(true);
                        v.getPosition().set(pl.getLocation());
                        same = false;
                    }
                }
            } else
                return false;
        }
        return same;
    }

    // Checks whether two edges are connected and sets triangle field if they
    // are.
    private void edgeConnected(int face, IntBuffer index, int index1,
            int index2, ShadowEdge edge) {
        edge.p0 = index1;
        edge.p1 = index2;

        index.rewind();

        for (int t = 0; t < maxIndex; t++) {
            if (t != face) {
                int offset = t * 3;
                int t0 = index.get(offset), t1 = index.get(offset + 1), t2 = index
                        .get(offset + 2);
                if ((t0 == index1 && t1 == index2)
                        || (t1 == index1 && t2 == index2)
                        || (t2 == index1 && t0 == index2)
                        || (t0 == index2 && t1 == index1)
                        || (t1 == index2 && t2 == index1)
                        || (t2 == index2 && t0 == index1)) {
                    // Edges are connected
                    edge.triangle = t;
                    return;
                }
            }
        }
    }

    /**
     * <code>recreateFaces</code> creates a triangle array for every triangle
     * in the target occluder mesh and stores it in the faces field. This is
     * only done rarely in general.
     */
    public void recreateFaces() {
        // make a copy of the original indices
        maxIndex = 0;
        facing = new BitSet();
        IntBuffer index = BufferUtils.clone(target.getIndexBuffer());
        if (index == null) {
            return;
        }
        index.clear();

        // Create a ShadowTriangle object for each face
        faces = new ArrayList<ShadowTriangle>();

        maxIndex = index.capacity() / 3;
        vertCount = target.getVertexCount();

        // Create a bitset for holding direction flags
        facing = new BitSet(maxIndex);

        // Loop through all of the triangles
        for (int t = 0; t < maxIndex; t++) {
            ShadowTriangle tri = new ShadowTriangle();
            faces.add(tri);
            int offset = t * 3;
            int t0 = index.get(offset), t1 = index.get(offset + 1), t2 = index
                    .get(offset + 2);
            edgeConnected(t, index, t0, t1, tri.edge1);
            edgeConnected(t, index, t1, t2, tri.edge2);
            edgeConnected(t, index, t2, t0, tri.edge3);
        }
    }

    /**
     * <code>getShadowVolume</code> returns the shadow volume contained in
     * this grouping for a particular light
     * 
     * @param light
     *            the light whose shadow volume should be returned
     * @return a shadow volume for the light or null if one does not exist
     */
    public ShadowVolume getShadowVolume(Light light) {
        for (int v = 0, vSize = volumes.size(); v < vSize; v++) {
            ShadowVolume vol = volumes.get(v);
            if (vol.light.equals(light))
                return vol;
        }
        return null;
    }

    /**
     * @return Returns the projectionLength.
     */
    public float getProjectionLength() {
        return projectionLength;
    }

    /**
     * @param projectionLength
     *            The projectionLength to set.
     */
    public void setProjectionLength(float projectionLength) {
        this.projectionLength = projectionLength;
        // force update of volumes
        for (int v = 0, vSize = volumes.size(); v < vSize; v++) {
            volumes.get(v).setUpdate(true);
        }
    }

    /**
     * @return Returns the volumes.
     */
    public ArrayList<ShadowVolume> getVolumes() {
        return volumes;
    }

}
