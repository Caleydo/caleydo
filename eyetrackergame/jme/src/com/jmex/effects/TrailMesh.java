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

package com.jmex.effects;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.jme.util.geom.BufferUtils;

/**
 * <code>TrailMesh</code>
 * 
 * @author Rikard Herlitz (MrCoder)
 */
public class TrailMesh extends TriMesh implements Savable {
    private static final long serialVersionUID = 1L;

    private int nrTrailSections;
    private int trailVertices;

    public enum UpdateMode {
        Step, Interpolate
    }

    /**
     * Update mode defines how the trailmesh vertices are to be updated. Shifted
     * position by position or interpolated for smoother movement.
     */
    private UpdateMode updateMode = UpdateMode.Step;

    public enum FacingMode {
        Tangent, Billboard
    }

    /*
     * Facing mode defines the orientation of the trailmesh. Tangent uses the
     * tangent value specified when calling setTrailFront and Billboard
     * automatically orients the trailmesh to always face the camera.
     */
    private FacingMode facingMode = FacingMode.Billboard;

    /**
     * Storage for each section in the trailmesh.
     */
    public class TrailData {
        public Vector3f position = new Vector3f();
        public Vector3f tangent;
        public float width;
        public Vector3f interpolatedPosition;
    }

    private LinkedList<TrailData> trailVectors;

    private float throttle = Float.MAX_VALUE;

    // How often to update the trail front (controlling section spacing)
    private float updateSpeed = 20.0f;

    // Whether the TrailData is updated or not    
    private boolean invalid;

    // Temporary vectors
    private final Vector3f trailCamVec = new Vector3f();
    private final Vector3f trailDirection = new Vector3f();

    /**
     * Creates a new TrailMesh.
     * 
     * @param name
     *            Name of Spatial
     * @param nrTrailSections
     *            Number of sections the TrailMesh should consist of. Number of
     *            vertices in the mesh will be nrTrailSections * 2.
     */
    public TrailMesh(String name, int nrTrailSections) {
        super(name);
        this.nrTrailSections = nrTrailSections;
        this.trailVertices = nrTrailSections * 2;

        this.trailVectors = new LinkedList<TrailData>();
        for (int i = 0; i < nrTrailSections; i++) {
            trailVectors.add(new TrailData());
        }

        setData();
    }

    /**
     * Update the front position of the trail.
     * 
     * @param position
     *            New position of the trail front
     * @param width
     *            Width of the trail
     * @param tpf
     *            Current time per frame
     */
    public void setTrailFront(Vector3f position, float width, float tpf) {
        setTrailFront(position, null, width, tpf);
    }

    /**
     * Update the front position of the trail.
     * 
     * @param position
     *            New position of the trail front
     * @param tangent
     *            Specifies the gradient of the trail (if facingmode is set to
     *            tangent)
     * @param width
     *            Width of the trail
     * @param tpf
     *            Current time per frame
     */
    public void setTrailFront(Vector3f position, Vector3f tangent, float width,
            float tpf) {

        TrailData trail = null;

        // Check if time to add or wrap the trail sections
        throttle += tpf * updateSpeed;
        if (throttle > 1.0f) {
            throttle %= 1.0f;

            trail = trailVectors.removeLast();
            trailVectors.addFirst(trail);
        } else {
            trail = trailVectors.getFirst();
        }

        if (trail == null) {
            return;
        }

        // Always update the front section
        trail.position.set(position.x, position.y, position.z);
        if (tangent != null) {
            if (trail.tangent == null) {
                trail.tangent = new Vector3f();
            }
            trail.tangent.set(tangent.x, tangent.y, tangent.z);
        }
        trail.width = width;
        invalid = true;
    }

    /**
     * Update the vertices of the trail.
     * 
     * @param camPos
     *            Camera position used for billboarding.
     */
    public void update(Vector3f camPos) {
        if (trailVectors.size() < 2) {
            return;
        }

        if (invalid || facingMode == FacingMode.Billboard) {
            if (updateMode == UpdateMode.Step) {
                updateStep(camPos);
            } else {
                updateInterpolate(camPos);
            }
            invalid = false;
        }
    }
    
    public void invalidate() {
        invalid = true;
    }

    private void updateStep(Vector3f camPos) {
        FloatBuffer vertBuf = getVertexBuffer();
        vertBuf.rewind();

        for (int i = 0; i < nrTrailSections; i++) {
            TrailData trailData = trailVectors.get(i);
            Vector3f trailVector = trailData.position;

            if (facingMode == FacingMode.Billboard) {
                if (i == 0) {
                    trailDirection.set(trailVectors.get(i + 1).position)
                            .subtractLocal(trailVector);
                } else if (i == nrTrailSections - 1) {
                    trailDirection.set(trailVector).subtractLocal(
                            trailVectors.get(i - 1).position);
                } else {
                    trailDirection.set(trailVectors.get(i + 1).position)
                            .subtractLocal(trailVectors.get(i - 1).position);
                }

                trailCamVec.set(trailVector).subtractLocal(camPos);
                trailDirection.crossLocal(trailCamVec);
                trailDirection.normalizeLocal().multLocal(
                        trailData.width * 0.5f);
            } else if (trailData.tangent != null) {
                trailDirection.set(trailData.tangent).multLocal(
                        trailData.width * 0.5f);
            } else {
                trailDirection.set(trailData.width * 0.5f, 0, 0);
            }

            vertBuf.put(trailVector.x - trailDirection.x);
            vertBuf.put(trailVector.y - trailDirection.y);
            vertBuf.put(trailVector.z - trailDirection.z);

            vertBuf.put(trailVector.x + trailDirection.x);
            vertBuf.put(trailVector.y + trailDirection.y);
            vertBuf.put(trailVector.z + trailDirection.z);
        }
    }

    private void updateInterpolate(Vector3f camPos) {
        for (int i = 0; i < nrTrailSections; i++) {
            TrailData trailData = trailVectors.get(i);

            Vector3f interpolationVector = trailData.interpolatedPosition;
            if (trailData.interpolatedPosition == null) {
                trailData.interpolatedPosition = new Vector3f();
                interpolationVector = trailData.interpolatedPosition;
            }

            interpolationVector.set(trailData.position);

            if (i > 0) {
                interpolationVector.interpolate(interpolationVector,
                        trailVectors.get(i - 1).position, throttle);
            }
        }

        for (int i = 0; i < nrTrailSections; i++) {
            TrailData trailData = trailVectors.get(i);
            Vector3f trailVector = trailData.interpolatedPosition;

            if (facingMode == FacingMode.Billboard) {
                if (i == 0) {
                    trailDirection.set(
                            trailVectors.get(i + 1).interpolatedPosition)
                            .subtractLocal(trailVector);
                } else if (i == nrTrailSections - 1) {
                    trailDirection.set(trailVector).subtractLocal(
                            trailVectors.get(i - 1).interpolatedPosition);
                } else {
                    trailDirection
                            .set(trailVectors.get(i + 1).interpolatedPosition)
                            .subtractLocal(
                                    trailVectors.get(i - 1).interpolatedPosition);
                }

                trailCamVec.set(trailVector).subtractLocal(camPos);
                trailDirection.crossLocal(trailCamVec);
                trailDirection.normalizeLocal().multLocal(
                        trailData.width * 0.5f);
            } else if (trailData.tangent != null) {
                trailDirection.set(trailData.tangent).multLocal(
                        trailData.width * 0.5f);
            } else {
                trailDirection.set(trailData.width * 0.5f, 0, 0);
            }

            vertBuf.put(trailVector.x - trailDirection.x);
            vertBuf.put(trailVector.y - trailDirection.y);
            vertBuf.put(trailVector.z - trailDirection.z);

            vertBuf.put(trailVector.x + trailDirection.x);
            vertBuf.put(trailVector.y + trailDirection.y);
            vertBuf.put(trailVector.z + trailDirection.z);
        }
    }

    public void resetPosition(Vector3f position) {
        for (int i = 0; i < nrTrailSections; i++) {
            trailVectors.get(i).position.set(position);
        }
    }

    private void setData() {
        setVertexBuffer(BufferUtils.createVector3Buffer(getVertexBuffer(),
                trailVertices));
        setNormalBuffer(BufferUtils.createVector3Buffer(getNormalBuffer(),
                trailVertices));
        setDefaultColor(ColorRGBA.white.clone());
        setTextureData();
        setIndexData();
    }

    private void setTextureData() {
        if (getTextureCoords(0) == null) {
            FloatBuffer tex = BufferUtils.createVector2Buffer(trailVertices);
            setTextureCoords(new TexCoords(tex, 2), 0);
            for (int i = 0; i < nrTrailSections; i++) {
                tex.put((float) i / nrTrailSections).put(0);
                tex.put((float) i / nrTrailSections).put(1);
            }
        }
    }

    private void setIndexData() {
        setMode(TriMesh.Mode.Strip);
        if (getIndexBuffer() == null) {
            setIndexBuffer(BufferUtils.createIntBuffer(trailVertices));
            IntBuffer indexBuf = getIndexBuffer();
            for (int i = 0; i < trailVertices; i++) {
                indexBuf.put(i);
            }
        }
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(nrTrailSections, "nrTrailSections", 0);
        capsule.write(trailVertices, "trailVertices", 0);
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        nrTrailSections = capsule.readInt("nrTrailSections", 0);
        trailVertices = capsule.readInt("trailVertices", 0);
    }

    public void setUpdateSpeed(float updateSpeed) {
        this.updateSpeed = updateSpeed;
    }

    public float getUpdateSpeed() {
        return updateSpeed;
    }

    public void setUpdateMode(UpdateMode updateMode) {
        this.updateMode = updateMode;
    }

    public UpdateMode getUpdateMode() {
        return updateMode;
    }

    public void setFacingMode(FacingMode facingMode) {
        this.facingMode = facingMode;
    }

    public FacingMode getFacingMode() {
        return facingMode;
    }
    
    /**
     * Get the mesh data to modify it manually.
     * If data is modified, invalidate() method call is required.
     * @return
     */
    public LinkedList<TrailData> getTrailData() {
        return trailVectors;
    }

}