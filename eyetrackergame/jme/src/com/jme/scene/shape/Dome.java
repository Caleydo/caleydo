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
// $Id: Dome.java 4639 2009-08-29 00:34:05Z skye.book $
package com.jme.scene.shape;

import static com.jme.util.geom.BufferUtils.*;

import java.io.IOException;

import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.geom.BufferUtils;

/**
 * A hemisphere.
 * 
 * @author Peter Andersson
 * @author Joshua Slack (Original sphere code that was adapted)
 * @version $Revision: 4639 $, $Date: 2009-08-29 02:34:05 +0200 (Sa, 29 Aug 2009) $
 */
public class Dome extends TriMesh {

    private static final long serialVersionUID = 1L;

    private int planes;

    private int radialSamples;

    /** The radius of the dome */
    private float radius;

    /** The center of the dome */
    private Vector3f center;
    
    private boolean outsideView = true;

    private static Vector3f tempVa = new Vector3f();

    private static Vector3f tempVb = new Vector3f();

    private static Vector3f tempVc = new Vector3f();

    /** <strong>NOT API:</strong> for internal use, do not call from user code. */
    public Dome() {}

    /**
     * Constructs a dome. By default the dome has no geometry data or center.
     * 
     * @param name
     *            The name of the dome.
     */
    public Dome(String name) {
        super(name);
    }

    /**
     * Constructs a dome with it's center at the origin. For details, see the other
     * constructor.
     * 
     * @param name
     *            Name of dome.
     * @param planes
     *            The number of planes along the Z-axis.
     * @param radialSamples
     *            The samples along the radial.
     * @param radius
     *            Radius of the dome.
     * @see #Dome(java.lang.String, com.jme.math.Vector3f, int, int, float)
     */
    public Dome(String name, int planes, int radialSamples, float radius) {
        this(name, new Vector3f(0, 0, 0), planes, radialSamples, radius);
    }

    /**
     * Constructs a dome. All geometry data buffers are updated automatically.
     * Both planes and radialSamples increase the quality of the generated dome.
     * 
     * @param name
     *            Name of the dome.
     * @param center
     *            Center of the dome.
     * @param planes
     *            The number of planes along the Z-axis.
     * @param radialSamples
     *            The number of samples along the radial.
     * @param radius
     *            The radius of the dome.
     */
    public Dome(String name, Vector3f center, int planes, int radialSamples,
            float radius) {
        super(name);
        updateGeometry(center, planes, radialSamples, radius, true);
    }

    /**
     * Constructs a dome. All geometry data buffers are updated automatically.
     * Both planes and radialSamples increase the quality of the generated dome.
     * 
     * @param name
     *            Name of the dome.
     * @param center
     *            Center of the dome.
     * @param planes
     *            The number of planes along the Z-axis.
     * @param radialSamples
     *            The number of samples along the radial.
     * @param radius
     *            The radius of the dome.
     * @param outsideView
     *            If true, the triangles will be connected for a view outside of
     *            the dome.
     */
    public Dome(String name, Vector3f center, int planes, int radialSamples,
            float radius, boolean outsideView) {
        super(name);
        updateGeometry(center, planes, radialSamples, radius, outsideView);
    }

    public Vector3f getCenter() {
        return center;
    }

    /** Get the number of planar segments along the z-axis of the dome. */
    public int getPlanes() {
        return planes;
    }

    /** Get the number of samples radially around the main axis of the dome. */
    public int getRadialSamples() {
        return radialSamples;
    }

    /** Get the radius of the dome. */
    public float getRadius() {
        return radius;
    }

    /**
     * Are the triangles connected in such a way as to present a view out from the
     * dome or not.
     * 
     * @return
     */
    public boolean isOutsideView() {
        return outsideView;
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        planes = capsule.readInt("planes", 0);
        radialSamples = capsule.readInt("radialSamples", 0);
        radius = capsule.readFloat("radius", 0);
        center = (Vector3f) capsule.readSavable("center", Vector3f.ZERO.clone());
    }

    /**
     * @deprecated use {@link #updateGeometry(Vector3f, int, int, float, boolean)}.
     */
    public void setData(Vector3f center, int planes, int radialSamples,
            float radius, boolean updateBuffers, boolean outsideView) {
        updateGeometry(center, planes, radialSamples, radius, outsideView);
    }

    /**
     * Rebuilds the dome with a new set of parameters.
     * 
     * @param center the new center of the dome.
     * @param planes the number of planes along the Z-axis.
     * @param radialSamples the new number of radial samples of the dome.
     * @param radius the new radius of the dome.
     * @param outsideView should the dome be set up to be viewed from the inside looking out.
     */
    public void updateGeometry(Vector3f center, int planes,
            int radialSamples, float radius, boolean outsideView) {
        this.outsideView = outsideView;
        this.center = center != null ? center : new Vector3f(0, 0, 0);
        this.planes = planes;
        this.radialSamples = radialSamples;
        this.radius = radius;

        // Allocate vertices, allocating one extra in each radial to get the
        // correct texture coordinates
        setVertexCount(((planes - 1) * (radialSamples + 1)) + 1);
        setVertexBuffer(createVector3Buffer(getVertexCount()));

        // allocate normals
        setNormalBuffer(createVector3Buffer(getVertexCount()));

        // allocate texture coordinates
        getTextureCoords().set(0, new TexCoords(createVector2Buffer(getVertexCount())));

        // generate geometry
        float fInvRS = 1.0f / radialSamples;
        float fYFactor = 1.0f / (planes - 1);

        // Generate points on the unit circle to be used in computing the mesh
        // points on a dome slice.
        float[] afSin = new float[(radialSamples)];
        float[] afCos = new float[(radialSamples)];
        for (int iR = 0; iR < radialSamples; iR++) {
            float fAngle = FastMath.TWO_PI * fInvRS * iR;
            afCos[iR] = FastMath.cos(fAngle);
            afSin[iR] = FastMath.sin(fAngle);
        }

        // generate the dome itself
        int i = 0;
        for (int iY = 0; iY < (planes - 1); iY++, i++) {
            float fYFraction = fYFactor * iY; // in (0,1)
            float fY = radius * fYFraction;
            // compute center of slice
            Vector3f kSliceCenter = tempVb.set(center);
            kSliceCenter.y += fY;

            // compute radius of slice
            float fSliceRadius = FastMath.sqrt(FastMath.abs(radius * radius - fY * fY));

            // compute slice vertices
            Vector3f kNormal;
            int iSave = i;
            for (int iR = 0; iR < radialSamples; iR++, i++) {
                float fRadialFraction = iR * fInvRS; // in [0,1)
                Vector3f kRadial = tempVc.set(afCos[iR], 0, afSin[iR]);
                kRadial.mult(fSliceRadius, tempVa);
                getVertexBuffer().put(kSliceCenter.x + tempVa.x).put(
                        kSliceCenter.y + tempVa.y).put(
                        kSliceCenter.z + tempVa.z);

                populateFromBuffer(tempVa, getVertexBuffer(), i);
                kNormal = tempVa.subtractLocal(center);
                kNormal.normalizeLocal();
                if (outsideView)
                    getNormalBuffer().put(kNormal.x).put(kNormal.y).put(kNormal.z);
                else
                    getNormalBuffer().put(-kNormal.x).put(-kNormal.y).put(-kNormal.z);

                getTextureCoords().get(0).coords.put(fRadialFraction).put(fYFraction);
            }
            BufferUtils.copyInternalVector3(getVertexBuffer(), iSave, i);
            BufferUtils.copyInternalVector3(getNormalBuffer(), iSave, i);
            getTextureCoords().get(0).coords.put(1.0f).put(fYFraction);
        }

        // pole
        getVertexBuffer().put(center.x).put(center.y + radius).put(center.z);
        getNormalBuffer().put(0).put(outsideView ? 1 : -1).put(0);
        getTextureCoords().get(0).coords.put(0.5f).put(1.0f);

        // allocate connectivity
        setTriangleQuantity((planes - 2) * radialSamples * 2 + radialSamples);
        setIndexBuffer(BufferUtils.createIntBuffer(3 * getTriangleCount()));

        // generate connectivity
        int index = 0;
        // Generate only for middle planes
        for (int plane = 1; plane < (planes - 1); plane++) {
            int bottomPlaneStart = (plane - 1) * (radialSamples + 1);
            int topPlaneStart = plane * (radialSamples + 1);
            for (int sample = 0; sample < radialSamples; sample++, index += 6) {
                getIndexBuffer().put(bottomPlaneStart + sample);
                getIndexBuffer().put(topPlaneStart + sample);
                getIndexBuffer().put(bottomPlaneStart + sample + 1);
                getIndexBuffer().put(bottomPlaneStart + sample + 1);
                getIndexBuffer().put(topPlaneStart + sample);
                getIndexBuffer().put(topPlaneStart + sample + 1);
            }
        }

        // pole triangles
        int bottomPlaneStart = (planes - 2) * (radialSamples + 1);
        for (int samples = 0; samples < radialSamples; samples++, index += 3) {
            getIndexBuffer().put(bottomPlaneStart + samples);
            getIndexBuffer().put(getVertexCount() - 1);
            getIndexBuffer().put(bottomPlaneStart + samples + 1);
        }
    }

    /**
     * Generates the connections
     */
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(planes, "planes", 0);
        capsule.write(radialSamples, "radialSamples", 0);
        capsule.write(radius, "radius", 0);
        capsule.write(center, "center", Vector3f.ZERO);
    }

}