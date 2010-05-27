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
// $Id: Cylinder.java 4131 2009-03-19 20:15:28Z blaine.dev $
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
 * A simple cylinder, defined by it's height and radius.
 * 
 * @author Mark Powell
 * @version $Revision: 4131 $, $Date: 2009-03-19 21:15:28 +0100 (Do, 19 Mär 2009) $
 */
public class Cylinder extends TriMesh {

    private static final long serialVersionUID = 1L;

    private int axisSamples;

    private int radialSamples;

    private float radius;
    private float radius2;

    private float height;
    private boolean closed;
    private boolean inverted;

    public Cylinder() {
    }

    /**
     * Creates a new Cylinder. By default its center is the origin. Usually, a
     * higher sample number creates a better looking cylinder, but at the cost
     * of more vertex information.
     * 
     * @param name
     *            The name of this Cylinder.
     * @param axisSamples
     *            Number of triangle samples along the axis.
     * @param radialSamples
     *            Number of triangle samples along the radial.
     * @param radius
     *            The radius of the cylinder.
     * @param height
     *            The cylinder's height.
     */
    public Cylinder(String name, int axisSamples, int radialSamples,
            float radius, float height) {
        this(name, axisSamples, radialSamples, radius, height, false);
    }

    /**
     * Creates a new Cylinder. By default its center is the origin. Usually, a
     * higher sample number creates a better looking cylinder, but at the cost
     * of more vertex information. <br>
     * If the cylinder is closed the texture is split into axisSamples parts:
     * top most and bottom most part is used for top and bottom of the cylinder,
     * rest of the texture for the cylinder wall. The middle of the top is
     * mapped to texture coordinates (0.5, 1), bottom to (0.5, 0). Thus you need
     * a suited distorted texture.
     * 
     * @param name
     *            The name of this Cylinder.
     * @param axisSamples
     *            Number of triangle samples along the axis.
     * @param radialSamples
     *            Number of triangle samples along the radial.
     * @param radius
     *            The radius of the cylinder.
     * @param height
     *            The cylinder's height.
     * @param closed
     *            true to create a cylinder with top and bottom surface
     */
    public Cylinder(String name, int axisSamples, int radialSamples,
            float radius, float height, boolean closed) {
        this(name, axisSamples, radialSamples, radius, height, closed, false);
    }

    /**
     * Creates a new Cylinder. By default its center is the origin. Usually, a
     * higher sample number creates a better looking cylinder, but at the cost
     * of more vertex information. <br>
     * If the cylinder is closed the texture is split into axisSamples parts:
     * top most and bottom most part is used for top and bottom of the cylinder,
     * rest of the texture for the cylinder wall. The middle of the top is
     * mapped to texture coordinates (0.5, 1), bottom to (0.5, 0). Thus you need
     * a suited distorted texture.
     * 
     * @param name
     *            The name of this Cylinder.
     * @param axisSamples
     *            Number of triangle samples along the axis.
     * @param radialSamples
     *            Number of triangle samples along the radial.
     * @param radius
     *            The radius of the cylinder.
     * @param height
     *            The cylinder's height.
     * @param closed
     *            true to create a cylinder with top and bottom surface
     * @param inverted
     *            true to create a cylinder that is meant to be viewed from the
     *            interior.
     */
    public Cylinder(String name, int axisSamples, int radialSamples,
            float radius, float height, boolean closed, boolean inverted) {
        this(name, axisSamples, radialSamples, radius, radius, height, closed, inverted);
    }

    public Cylinder(String name, int axisSamples, int radialSamples,
            float radius, float radius2, float height, boolean closed, boolean inverted) {
        super(name);
        updateGeometry(axisSamples, radialSamples, radius, radius2, height, closed, inverted);
    }

    /**
     * @return the number of samples along the cylinder axis
     */
    public int getAxisSamples() {
        return axisSamples;
    }

    /**
     * @return Returns the height.
     */
    public float getHeight() {
        return height;
    }

    /**
     * @return number of samples around cylinder
     */
    public int getRadialSamples() {
        return radialSamples;
    }

//    /**
//     * Set the height of the cylinder.
//     * <p>
//     * <strong>Note:</strong> this method causes the tri-mesh geometry data
//     * to be recalculated, see <a href="package-summary.html#mutator-methods">
//     * the package description</a> for more information about this.
//     * 
//     * @param height the new height.
//     */
//    public void setHeight(float height) {
//        this.height = height;
//        allocateVertices();
//    }

    /**
     * @return Returns the radius.
     */
    public float getRadius() {
        return radius;
    }
    
    public float getRadius2() {
        return radius2;
    }

    /**
     * @return true if end caps are used.
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * @return true if normals and uvs are created for interior use
     */
    public boolean isInverted() {
        return inverted;
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        axisSamples = capsule.readInt("axisSamples", 0);
        radialSamples = capsule.readInt("radialSamples", 0);
        radius = capsule.readFloat("radius", 0);
        radius2 = capsule.readFloat("radius2", 0);
        height = capsule.readFloat("height", 0);
        closed = capsule.readBoolean("closed", false);
        inverted = capsule.readBoolean("inverted", false);
    }

    /**
     * @deprecated use {@link #updateGeometry(int, int, float, float, float, boolean, boolean)}.
     */
    public void recomputeGeometry(int axisSamples, int radialSamples,
            float radius, float height, boolean closed, boolean inverted) {
        updateGeometry(axisSamples, radialSamples, radius, radius, height,
                closed, inverted);
    }

    /**
     * Set the half angle of the cone.
     * 
     * @param radians
     */
    public void setHalfAngle(float radians) {
        updateGeometry(getAxisSamples(), getRadialSamples(), FastMath.tan(radians), getRadius2(), getHeight(), isClosed(), isInverted());
    }

    /**
     * Set the radius of this cylinder.
     * <p>
     * This will also reset any second radius value on the cylinder.
     * <p>
     * <strong>Note:</strong> this method causes the tri-mesh geometry data
     * to be recalculated, see <a href="package-summary.html#mutator-methods">
     * the package description</a> for more information about this.
     * 
     * @param radius the new radius.
     * @deprecated use {@link #recomputeGeometry(int, int, float, float, boolean, boolean)}.
     */
    public void setRadius(float radius) {
        updateGeometry(axisSamples, radialSamples, radius, radius, height, closed, inverted);
    }

    /**
     * Set the top radius of the 'cylinder' to differ from the bottom radius.
     * <p>
     * <strong>Note:</strong> this method causes the tri-mesh geometry data
     * to be recalculated, see <a href="package-summary.html#mutator-methods">
     * the package description</a> for more information about this.
     * 
     * @param radius the first radius to set.
     * @see {@link Cone}
     * @deprecated use {@link #recomputeGeometry(int, int, float, float, boolean, boolean)}.
     */
    public void setRadius1(float radius) {
        updateGeometry(axisSamples, radialSamples, radius, radius2, height, closed, inverted);
    }

    /**
     * Set the bottom radius of the 'cylinder' to differ from the top radius.
     * This makes the Geometry be a frustum of pyramid, or if set to 0, a cone.
     * <p>
     * <strong>Note:</strong> this method causes the tri-mesh geometry data
     * to be recalculated, see <a href="package-summary.html#mutator-methods">
     * the package description</a> for more information about this.
     * 
     * @param radius the second radius to set.
     * @see {@link Cone}
     * @deprecated use {@link #recomputeGeometry(int, int, float, float, boolean, boolean)}.
     */
    public void setRadius2(float radius2) {
        updateGeometry(axisSamples, radialSamples, radius, radius2, height, closed, inverted);
    }

    /**
     * Rebuilds the cylinder based on a new set of parameters.
     * 
     * @param axisSamples the number of samples along the axis.
     * @param radialSamples the number of samples around the radial.
     * @param radius the radius of the bottom of the cylinder.
     * @param radius2 the radius of the top of the cylinder.
     * @param height the cylinder's height.
     * @param closed should the cylinder have top and bottom surfaces.
     * @param inverted is the cylinder is meant to be viewed from the inside.
     */
    public void updateGeometry(int axisSamples, int radialSamples,
            float radius, float radius2, float height, boolean closed, boolean inverted) {
        this.axisSamples = axisSamples + (closed ? 2 : 0);
        this.radialSamples = radialSamples;
        this.radius = radius;
        this.radius2 = radius2;
        this.height = height;
        this.closed = closed;
        this.inverted = inverted;
        // Vertices
        setVertexCount(axisSamples * (radialSamples + 1) + (closed ? 2 : 0));
        setVertexBuffer(createVector3Buffer(getVertexBuffer(), getVertexCount()));
        
        // Normals
        setNormalBuffer(createVector3Buffer(getNormalBuffer(), getVertexCount()));
        
        // Texture co-ordinates
        getTextureCoords().set(0, new TexCoords(createVector2Buffer(getVertexCount())));
        
        setTriangleQuantity(((closed ? 2 : 0) + 2 * (axisSamples - 1)) * radialSamples);
        setIndexBuffer(createIntBuffer(getIndexBuffer(), 3 * getTriangleCount()));
        
        // generate geometry
        float inverseRadial = 1.0f / radialSamples;
        float inverseAxisLess = 1.0f / (closed ? axisSamples - 3 : axisSamples - 1);
        float inverseAxisLessTexture = 1.0f / (axisSamples - 1);
        float halfHeight = 0.5f * height;
        
        // Generate points on the unit circle to be used in computing the mesh
        // points on a cylinder slice.
        float[] sin = new float[radialSamples + 1];
        float[] cos = new float[radialSamples + 1];
        
        for (int radialCount = 0; radialCount < radialSamples; radialCount++) {
            float angle = FastMath.TWO_PI * inverseRadial * radialCount;
            cos[radialCount] = FastMath.cos(angle);
            sin[radialCount] = FastMath.sin(angle);
        }
        sin[radialSamples] = sin[0];
        cos[radialSamples] = cos[0];
        
        // generate the cylinder itself
        Vector3f tempNormal = new Vector3f();
        for (int axisCount = 0, i = 0; axisCount < axisSamples; axisCount++, i++) {
            float axisFraction;
            float axisFractionTexture;
            int topBottom = 0;
            if (!closed) {
                axisFraction = axisCount * inverseAxisLess; // in [0,1]
                axisFractionTexture = axisFraction;
            } else {
                if (axisCount == 0) {
                    topBottom = -1; // bottom
                    axisFraction = 0;
                    axisFractionTexture = inverseAxisLessTexture;
                } else if (axisCount == axisSamples - 1) {
                    topBottom = 1; // top
                    axisFraction = 1;
                    axisFractionTexture = 1 - inverseAxisLessTexture;
                } else {
                    axisFraction = (axisCount - 1) * inverseAxisLess;
                    axisFractionTexture = axisCount * inverseAxisLessTexture;
                }
            }
            float z = -halfHeight + height * axisFraction;
        
            // compute center of slice
            Vector3f sliceCenter = new Vector3f(0, 0, z);
        
            // compute slice vertices with duplication at end point
            int save = i;
            for (int radialCount = 0; radialCount < radialSamples; radialCount++, i++) {
                float radialFraction = radialCount * inverseRadial; // in [0,1)
                tempNormal.set(cos[radialCount], sin[radialCount], 0);
                if (topBottom == 0) {
                    if (!inverted)
                        getNormalBuffer().put(tempNormal.x).put(tempNormal.y).put(tempNormal.z);
                    else
                        getNormalBuffer().put(-tempNormal.x).put(-tempNormal.y).put(-tempNormal.z);
                } else {
                    getNormalBuffer().put(0).put(0).put(topBottom * (inverted ? -1 : 1));
                }
        
                tempNormal.multLocal((radius - radius2) * axisFraction + radius2)
                        .addLocal(sliceCenter);
                getVertexBuffer().put(tempNormal.x).put(tempNormal.y).put(tempNormal.z);
        
                getTextureCoords().get(0).coords.put((inverted ? 1 - radialFraction : radialFraction))
                        .put(axisFractionTexture);
            }
        
            BufferUtils.copyInternalVector3(getVertexBuffer(), save, i);
            BufferUtils.copyInternalVector3(getNormalBuffer(), save, i);
        
            getTextureCoords().get(0).coords.put((inverted ? 0.0f : 1.0f))
                    .put(axisFractionTexture);
        }
        
        if (closed) {
            getVertexBuffer().put(0).put(0).put(-halfHeight); // bottom center
            getNormalBuffer().put(0).put(0).put(-1 * (inverted ? -1 : 1));
            getTextureCoords().get(0).coords.put(0.5f).put(0);
            getVertexBuffer().put(0).put(0).put(halfHeight); // top center
            getNormalBuffer().put(0).put(0).put(1 * (inverted ? -1 : 1));
            getTextureCoords().get(0).coords.put(0.5f).put(1);
        }
        
        // Connectivity
        for (int axisCount = 0, axisStart = 0; axisCount < axisSamples - 1; axisCount++) {
            int i0 = axisStart;
            int i1 = i0 + 1;
            axisStart += radialSamples + 1;
            int i2 = axisStart;
            int i3 = i2 + 1;
            for (int i = 0; i < radialSamples; i++) {
                if (closed && axisCount == 0) {
                    if (!inverted) {
                        getIndexBuffer().put(i0++);
                        getIndexBuffer().put(getVertexCount() - 2);
                        getIndexBuffer().put(i1++);
                    } else {
                        getIndexBuffer().put(i0++);
                        getIndexBuffer().put(i1++);
                        getIndexBuffer().put(getVertexCount() - 2);
                    }
                } else if (closed && axisCount == axisSamples - 2) {
                    getIndexBuffer().put(i2++);
                    getIndexBuffer().put(inverted ? getVertexCount() - 1 : i3++);
                    getIndexBuffer().put(inverted ? i3++ : getVertexCount() - 1);
                } else {
                    getIndexBuffer().put(i0++);
                    getIndexBuffer().put(inverted ? i2 : i1);
                    getIndexBuffer().put(inverted ? i1 : i2);
                    getIndexBuffer().put(i1++);
                    getIndexBuffer().put(inverted ? i2++ : i3++);
                    getIndexBuffer().put(inverted ? i3++ : i2++);
                }
            }
        }
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(axisSamples, "axisSamples", 0);
        capsule.write(radialSamples, "radialSamples", 0);
        capsule.write(radius, "radius", 0);
        capsule.write(radius2, "radius2", 0);
        capsule.write(height, "height", 0);
        capsule.write(closed, "closed", false);
		capsule.write(inverted, "inverted", false);
    }
}
