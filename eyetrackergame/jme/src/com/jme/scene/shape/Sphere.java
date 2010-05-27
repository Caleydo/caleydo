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
// $Id: Sphere.java 4639 2009-08-29 00:34:05Z skye.book $
package com.jme.scene.shape;

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
 * <code>Sphere</code> represents a 3D object with all points equidistant
 * from a center point.
 * 
 * @author Joshua Slack
 * @version $Revision: 4639 $, $Date: 2009-08-29 02:34:05 +0200 (Sa, 29 Aug 2009) $
 */
public class Sphere extends TriMesh {

    private static final long serialVersionUID = 1L;

    @Deprecated
    public static final int TEX_ORIGINAL = 0;

    // Spherical projection mode, donated by Ogli from the jME forums.
    @Deprecated
    public static final int TEX_PROJECTED = 1;
        
    public enum TextureMode {
        /** Wrap texture radially and along z-axis */
        Original,
        /** Wrap texture radially, but spherically project along z-axis */
        Projected,
        /** Apply texture to each pole.  Eliminates polar distortion,
         * but mirrors the texture across the equator 
         */
        Polar
    }

    protected int zSamples;

    protected int radialSamples;

    protected boolean useEvenSlices;

    /** the distance from the center point each point falls on */
    public float radius;
    /** the center of the sphere */
    public Vector3f center;

    private static Vector3f tempVa = new Vector3f();

    private static Vector3f tempVb = new Vector3f();

    private static Vector3f tempVc = new Vector3f();

    protected TextureMode textureMode = TextureMode.Original;

    public Sphere() {
    }

    /**
     * Constructs a sphere. By default the Sphere has no geometry data or
     * center.
     * 
     * @param name
     *            The name of the sphere.
     */
    public Sphere(String name) {
        super(name);
    }

    /**
     * Constructs a sphere with center at the origin. For details, see the other
     * constructor.
     * 
     * @param name
     *            Name of sphere.
     * @param zSamples
     *            The samples along the Z.
     * @param radialSamples
     *            The samples along the radial.
     * @param radius
     *            Radius of the sphere.
     * @see #Sphere(java.lang.String, com.jme.math.Vector3f, int, int, float)
     */
    public Sphere(String name, int zSamples, int radialSamples, float radius) {
        this(name, new Vector3f(0, 0, 0), zSamples, radialSamples, radius);
    }

    /**
     * Constructs a sphere. All geometry data buffers are updated automatically.
     * Both zSamples and radialSamples increase the quality of the generated
     * sphere.
     * 
     * @param name
     *            Name of the sphere.
     * @param center
     *            Center of the sphere.
     * @param zSamples
     *            The number of samples along the Z.
     * @param radialSamples
     *            The number of samples along the radial.
     * @param radius
     *            The radius of the sphere.
     */
    public Sphere(String name, Vector3f center, int zSamples,
            int radialSamples, float radius) {
        this(name, center, zSamples, radialSamples, radius, false);
    }

    /**
     * Constructs a sphere. Additional arguments to evenly space
     * latitudinal slices
     * 
     * @param name
     *            Name of the sphere.
     * @param center
     *            Center of the sphere.
     * @param zSamples
     *            The number of samples along the Z.
     * @param radialSamples
     *            The number of samples along the radial.
     * @param radius
     *            The radius of the sphere.
     * @param useEvenSlices
     *            Slice sphere evenly along the Z axis
     */
    public Sphere(String name, Vector3f center, int zSamples,
            int radialSamples, float radius, boolean useEvenSlices) {
        super(name);
        updateGeometry(center, zSamples, radialSamples, radius, useEvenSlices);
    }

    /**
     * Returns the center of this sphere.
     * 
     * @return The sphere's center.
     */
    public Vector3f getCenter() {
        return center;
    }

    public int getRadialSamples() {
        return radialSamples;
    }

    public float getRadius() {
        return radius;
    }

    /**
     * @return Returns the textureMode.
     */
    public TextureMode getTextureMode() {
        return textureMode;
    }

    public int getZSamples() {
        return zSamples;
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        zSamples = capsule.readInt("zSamples", 0);
        radialSamples = capsule.readInt("radialSamples", 0);
        radius = capsule.readFloat("radius", 0);
        center = (Vector3f) capsule
                .readSavable("center", Vector3f.ZERO.clone());
        useEvenSlices = capsule.readBoolean("useEvenSlices", false);
        textureMode = capsule.readEnum("textureMode", TextureMode.class, TextureMode.Original);
    }

    /**
     * @deprecated Use {@link #updateGeometry(Vector3f,int,int,float)} instead
     */
    public void setCenter(Vector3f aCenter) {
        center = aCenter;
    }
    
    /**
     * @deprecated Use {@link #updateGeometry(Vector3f,int,int,float)} instead
     */
    public void setData(Vector3f center, int zSamples, int radialSamples, float radius) {
        updateGeometry(center, zSamples, radialSamples, radius, false);
    }

    /**
     * builds the vertices based on the radius, center and radial and zSamples.
     */
    private void setGeometryData() {
        // allocate vertices
        setVertexCount((zSamples - 2) * (radialSamples + 1) + 2);
        setVertexBuffer(BufferUtils.createVector3Buffer(getVertexBuffer(),
                getVertexCount()));

        // allocate normals if requested
        setNormalBuffer(BufferUtils.createVector3Buffer(getNormalBuffer(),
                getVertexCount()));

        // allocate texture coordinates
        setTextureCoords(new TexCoords(BufferUtils.createVector2Buffer(getVertexCount())));

        // generate geometry
        float fInvRS = 1.0f / radialSamples;
        float fZFactor = 2.0f / (zSamples - 1);

        // Generate points on the unit circle to be used in computing the mesh
        // points on a sphere slice.
        float[] afSin = new float[(radialSamples + 1)];
        float[] afCos = new float[(radialSamples + 1)];
        for (int iR = 0; iR < radialSamples; iR++) {
            float fAngle = FastMath.TWO_PI * fInvRS * iR;
            afCos[iR] = FastMath.cos(fAngle);
            afSin[iR] = FastMath.sin(fAngle);
        }
        afSin[radialSamples] = afSin[0];
        afCos[radialSamples] = afCos[0];

        // generate the sphere itself
        int i = 0;
        for (int iZ = 1; iZ < (zSamples - 1); iZ++) {
            float fAFraction = FastMath.HALF_PI * (-1.0f + fZFactor * iZ); // in (-pi/2, pi/2)
            float fZFraction;
            if (useEvenSlices)
                fZFraction = -1.0f + fZFactor * iZ; // in (-1, 1)
            else
                fZFraction = FastMath.sin(fAFraction); // in (-1,1)

            float fZ = radius * fZFraction;

            // compute center of slice
            Vector3f kSliceCenter = tempVb.set(center);
            kSliceCenter.z += fZ;

            // compute radius of slice
            float fSliceRadius = FastMath.sqrt(FastMath.abs(radius * radius
                    - fZ * fZ));

            // compute slice vertices with duplication at end point
            Vector3f kNormal;
            int iSave = i;
            for (int iR = 0; iR < radialSamples; iR++) {
                float fRadialFraction = iR * fInvRS; // in [0,1)
                Vector3f kRadial = tempVc.set(afCos[iR], afSin[iR], 0);
                kRadial.mult(fSliceRadius, tempVa);
                getVertexBuffer().put(kSliceCenter.x + tempVa.x).put(
                        kSliceCenter.y + tempVa.y).put(
                        kSliceCenter.z + tempVa.z);

                BufferUtils.populateFromBuffer(tempVa, getVertexBuffer(), i);
                kNormal = tempVa.subtractLocal(center);
                kNormal.normalizeLocal();
                if (true) // later we may allow interior texture vs. exterior
                    getNormalBuffer().put(kNormal.x).put(kNormal.y).put(
                            kNormal.z);
                else
                    getNormalBuffer().put(-kNormal.x).put(-kNormal.y).put(
                            -kNormal.z);

                if (textureMode == TextureMode.Original)
                    getTextureCoords().get(0).coords.put(fRadialFraction).put(
                            0.5f * (fZFraction + 1.0f));
                else if (textureMode == TextureMode.Projected)
                    getTextureCoords().get(0).coords.put(fRadialFraction).put(
                            FastMath.INV_PI
                                    * (FastMath.HALF_PI + FastMath
                                            .asin(fZFraction)));
                else if (textureMode == TextureMode.Polar) {
                    float r = (FastMath.HALF_PI - FastMath.abs(fAFraction)) / FastMath.PI;
                    float u = r * afCos[iR] + 0.5f;
                    float v = r * afSin[iR] + 0.5f;
                    getTextureCoords().get(0).coords.put(u).put(v);
                }

                i++;
            }

            BufferUtils.copyInternalVector3(getVertexBuffer(), iSave, i);
            BufferUtils.copyInternalVector3(getNormalBuffer(), iSave, i);

            if (textureMode == TextureMode.Original)
                getTextureCoords().get(0).coords.put(1.0f).put(
                        0.5f * (fZFraction + 1.0f));
            else if (textureMode == TextureMode.Projected)
                getTextureCoords().get(0).coords.put(1.0f)
                        .put(
                                FastMath.INV_PI
                                        * (FastMath.HALF_PI + FastMath
                                                .asin(fZFraction)));
            else if (textureMode == TextureMode.Polar) {
                float r = (FastMath.HALF_PI - FastMath.abs(fAFraction)) / FastMath.PI;
                getTextureCoords().get(0).coords.put(r+0.5f).put(0.5f);
            }

            i++;
        }

        // south pole
        getVertexBuffer().position(i * 3);
        getVertexBuffer().put(center.x).put(center.y).put(center.z - radius);

        getNormalBuffer().position(i * 3);
        if (true)
            getNormalBuffer().put(0).put(0).put(-1); // allow for inner
                                                        // texture orientation
                                                        // later.
        else
            getNormalBuffer().put(0).put(0).put(1);

        getTextureCoords().get(0).coords.position(i * 2);

        if (textureMode == TextureMode.Polar) {
            getTextureCoords().get(0).coords.put(0.5f).put(0.5f);
        }
        else {
            getTextureCoords().get(0).coords.put(0.5f).put(0.0f);
        }

        i++;

        // north pole
        getVertexBuffer().put(center.x).put(center.y).put(center.z + radius);

        if (true)
            getNormalBuffer().put(0).put(0).put(1);
        else
            getNormalBuffer().put(0).put(0).put(-1);

        if (textureMode == TextureMode.Polar) {
            getTextureCoords().get(0).coords.put(0.5f).put(0.5f);
        }
        else {
            getTextureCoords().get(0).coords.put(0.5f).put(1.0f);
        }
    }

    /**
     * sets the indices for rendering the sphere.
     */
    private void setIndexData() {
        // allocate connectivity
        setTriangleQuantity(2 * (zSamples - 2) * radialSamples);
        setIndexBuffer(BufferUtils.createIntBuffer(3 * getTriangleCount()));

        // generate connectivity
        int index = 0;
        for (int iZ = 0, iZStart = 0; iZ < (zSamples - 3); iZ++) {
            int i0 = iZStart;
            int i1 = i0 + 1;
            iZStart += (radialSamples + 1);
            int i2 = iZStart;
            int i3 = i2 + 1;
            for (int i = 0; i < radialSamples; i++, index += 6) {
                if (true) {
                    getIndexBuffer().put(i0++);
                    getIndexBuffer().put(i1);
                    getIndexBuffer().put(i2);
                    getIndexBuffer().put(i1++);
                    getIndexBuffer().put(i3++);
                    getIndexBuffer().put(i2++);
                } else // inside view
                {
                    getIndexBuffer().put(i0++);
                    getIndexBuffer().put(i2);
                    getIndexBuffer().put(i1);
                    getIndexBuffer().put(i1++);
                    getIndexBuffer().put(i2++);
                    getIndexBuffer().put(i3++);
                }
            }
        }

        // south pole triangles
        for (int i = 0; i < radialSamples; i++, index += 3) {
            if (true) {
                getIndexBuffer().put(i);
                getIndexBuffer().put(getVertexCount() - 2);
                getIndexBuffer().put(i + 1);
            } else { // inside view
                getIndexBuffer().put(i);
                getIndexBuffer().put(i + 1);
                getIndexBuffer().put(getVertexCount() - 2);
            }
        }

        // north pole triangles
        int iOffset = (zSamples - 3) * (radialSamples + 1);
        for (int i = 0; i < radialSamples; i++, index += 3) {
            if (true) {
                getIndexBuffer().put(i + iOffset);
                getIndexBuffer().put(i + 1 + iOffset);
                getIndexBuffer().put(getVertexCount() - 1);
            } else { // inside view
                getIndexBuffer().put(i + iOffset);
                getIndexBuffer().put(getVertexCount() - 1);
                getIndexBuffer().put(i + 1 + iOffset);
            }
        }
    }

    /**
     * @param textureMode
     *            The textureMode to set.
     * @Deprecated Use enum version of setTextureMode
     */
    @Deprecated
    public void setTextureMode(int textureMode) {
        if (textureMode == TEX_ORIGINAL)
            this.textureMode = TextureMode.Original;
        else if (textureMode == TEX_PROJECTED)
            this.textureMode = TextureMode.Projected;
        setGeometryData();
    }

    /**
     * @param textureMode
     *            The textureMode to set.
     */
    public void setTextureMode(TextureMode textureMode) {
        this.textureMode = textureMode;
        setGeometryData();
    }

    /**
     * Changes the information of the sphere into the given values.
     * 
     * @param center the center of the sphere.
     * @param zSamples the number of zSamples of the sphere.
     * @param radialSamples the number of radial samples of the sphere.
     * @param radius the radius of the sphere.
     */
    public void updateGeometry(Vector3f center, int zSamples, int radialSamples, float radius) {
        updateGeometry(center, zSamples, radialSamples, radius, false);
    }

    public void updateGeometry(Vector3f center, int zSamples, int radialSamples, float radius, boolean useEvenSlices) {
        this.center = center != null ? center : new Vector3f();
        this.zSamples = zSamples;
        this.radialSamples = radialSamples;
        this.radius = radius;
        this.useEvenSlices = useEvenSlices;
        setGeometryData();
        setIndexData();
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(zSamples, "zSamples", 0);
        capsule.write(radialSamples, "radialSamples", 0);
        capsule.write(radius, "radius", 0);
        capsule.write(center, "center", Vector3f.ZERO);
        capsule.write(useEvenSlices, "useEvenSlices", false);
        capsule.write(textureMode, "textureMode", TextureMode.Original);
    }

}
