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
// $Id: OrientedBox.java 4639 2009-08-29 00:34:05Z skye.book $
package com.jme.scene.shape;

import java.io.IOException;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.jme.util.geom.BufferUtils;

/**
 * Started Date: Aug 22, 2004 <br>
 * <br>
 * This primitive represents a box that has options to orient it according to its
 * X/Y/Z axis. It is used to create an OrientedBoundingBox mostly.
 * 
 * @author Jack Lindamood
 * @version $Revision: 4639 $, $Date: 2009-08-29 02:34:05 +0200 (Sa, 29 Aug 2009) $
 */
public class OrientedBox extends TriMesh {

    private static final long serialVersionUID = 1L;

    private static final Vector3f tempVa = new Vector3f();
    private static final Vector3f tempVb = new Vector3f();
    private static final Vector3f tempVc = new Vector3f();

    /** Center of the Oriented Box. */
    protected Vector3f center;

    /**
     * Have the corners of the box (as stored in the {@code #vectorStore} array)
     * been set to correctly represent the box’s corners or not.
     */
    public boolean correctCorners;

    /** Extents of the box along the x,y,z axis. */
    protected Vector3f extent = new Vector3f(0, 0, 0);

    /** Texture coordinate values for the corners of the box. */
    protected Vector2f texTopRight, texTopLeft, texBotRight, texBotLeft;

    /** Vector array used to store the array of the 8 corners the box has. */
    public Vector3f[] vectorStore;

    /** X axis of the Oriented Box. */
    protected Vector3f xAxis = new Vector3f(1, 0, 0);

    /** Y axis of the Oriented Box. */
    protected Vector3f yAxis = new Vector3f(0, 1, 0);

    /** Z axis of the Oriented Box. */
    protected Vector3f zAxis = new Vector3f(0, 0, 1);

    /**
     * Creates a new OrientedBox with the given name.
     * 
     * @param name
     *            The name of the new box.
     */
    public OrientedBox(String name) {
        this(name, new Vector3f(), new Vector2f(1, 1), new Vector2f(1, 0), new Vector2f(0, 1), new Vector2f(0, 0));
    }

    /**
     * Create a new oriented box.
     * <p>
     * The box is initially configured based on the supplied texture co-ordinate points.
     * 
     * @param name the name of the box.
     * @param center point at the center of the box.
     * @param topRight the top right hand corner of the box.
     * @param topLeft the top left hand corner of the box.
     * @param bottomRight the bottom right hand corner of the box.
     * @param bottomLeft the bottom left hand corner of the box.
     */
    public OrientedBox(String name, Vector3f center, Vector2f topRight, Vector2f topLeft, Vector2f bottomRight, Vector2f bottomLeft) {
        super(name);
        updateGeometry(center, topRight, topLeft, bottomRight, bottomLeft);
    }
    
    /**
     * Sets the vectorStore information to the 8 corners of the box.
     * @deprecated will be made private.
     */
    public void computeCorners() {
        correctCorners = true;

        tempVa.set(xAxis).multLocal(extent.x);
        tempVb.set(yAxis).multLocal(extent.y);
        tempVc.set(zAxis).multLocal(extent.z);

        vectorStore[0].set(center).addLocal(tempVa).addLocal(tempVb).addLocal(tempVc);
        vectorStore[1].set(center).addLocal(tempVa).subtractLocal(tempVb).addLocal(tempVc);
        vectorStore[2].set(center).addLocal(tempVa).addLocal(tempVb).subtractLocal(tempVc);
        vectorStore[3].set(center).subtractLocal(tempVa).addLocal(tempVb).addLocal(tempVc);
        vectorStore[4].set(center).addLocal(tempVa).subtractLocal(tempVb).subtractLocal(tempVc);
        vectorStore[5].set(center).subtractLocal(tempVa).subtractLocal(tempVb).addLocal(tempVc);
        vectorStore[6].set(center).subtractLocal(tempVa).addLocal(tempVb).subtractLocal(tempVc);
        vectorStore[7].set(center).subtractLocal(tempVa).subtractLocal(tempVb).subtractLocal(tempVc);
    }

    /**
     * Takes the plane and center information and creates the correct
     * vertex,normal,color,texture,index information to represent the
     * OrientedBox.
     * @deprecated will be made private.
     */
    public void computeInformation() {
        updateGeometryVertices();
        updateGeometryNormals();
        updateGeometryTextures();
        updateGeometryIndices();
    }

    /**
     * Returns the center of the box.
     * 
     * @return The box's center.
     */
    public Vector3f getCenter() {
        return center;
    }

    /**
     * Returns the box's extent vector along the x,y,z.
     * 
     * @return The box's extent vector.
     */
    public Vector3f getExtent() {
        return extent;
    }

    /** @deprecated Use {@link #getXAxis()} instead */
    public Vector3f getxAxis() {
        return getXAxis();
    }

    public Vector3f getXAxis() {
        return xAxis;
    }

    /** @deprecated Use {@link #getYAxis()} instead */
    public Vector3f getyAxis() {
        return getYAxis();
    }

    public Vector3f getYAxis() {
        return yAxis;
    }

    /** @deprecated Use {@link #getZAxis()} instead */
    public Vector3f getzAxis() {
        return getZAxis();
    }

    public Vector3f getZAxis() {
        return zAxis;
    }

    /**
     * Have the corners of the box been set correctly.
     * 
     * @return {@code true} if the vector store is correct,
     *      {@code false} otherwise.
     */
    public boolean isCorrectCorners() {
        return correctCorners;
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);

        center = (Vector3f) capsule.readSavable("center", Vector3f.ZERO.clone());
        xAxis = (Vector3f) capsule.readSavable("xAxis", Vector3f.UNIT_X.clone());
        yAxis = (Vector3f) capsule.readSavable("yAxis", Vector3f.UNIT_Y.clone());
        zAxis = (Vector3f) capsule.readSavable("zAxis", Vector3f.UNIT_Z.clone());
        extent = (Vector3f) capsule.readSavable("extent", Vector3f.ZERO.clone());
        texTopRight = (Vector2f) capsule.readSavable("texTopRight", new Vector2f(1, 1));
        texTopLeft = (Vector2f) capsule.readSavable("texTopLeft", new Vector2f(1, 0));
        texBotRight = (Vector2f) capsule.readSavable("texBotRight", new Vector2f(0, 1));
        texBotLeft = (Vector2f) capsule.readSavable("texBotLeft", new Vector2f(0, 0));

        Savable[] savs = capsule.readSavableArray("vectorStore", new Vector3f[8]);
        if (savs == null) {
            vectorStore = null;
        } else {
            vectorStore = new Vector3f[savs.length];
            for (int x = 0; x < savs.length; x++) {
                vectorStore[x] = (Vector3f) savs[x];
            }
        }

        correctCorners = capsule.readBoolean("correctCorners", false);
    }

    /** @deprecated use {@link #updateGeometry(Vector3f, Vector2f, Vector2f, Vector2f, Vector2f, boolean)} instead. */
    public void setCenter(Vector3f center) {
        this.center = center;
    }

    /** @deprecated use {@link #updateGeometry(Vector3f, Vector2f, Vector2f, Vector2f, Vector2f, boolean)} instead. */
    public void setExtent(Vector3f extent) {
        this.extent = extent;
    }

    /** @deprecated use {@link #updateGeometry(Vector3f, Vector2f, Vector2f, Vector2f, Vector2f, boolean)} instead. */
    public void setxAxis(Vector3f xAxis) {
        this.xAxis = xAxis;
    }

    /** @deprecated use {@link #updateGeometry(Vector3f, Vector2f, Vector2f, Vector2f, Vector2f, boolean)} instead. */
    public void setyAxis(Vector3f yAxis) {
        this.yAxis = yAxis;
    }

    /** @deprecated use {@link #updateGeometry(Vector3f, Vector2f, Vector2f, Vector2f, Vector2f, boolean)} instead. */
    public void setzAxis(Vector3f zAxis) {
        this.zAxis = zAxis;
    }

    /** Update the box’s geometry after a property has been altered directly. */
    public void updateGeometry() {
        updateGeometryVertices();
        updateGeometryNormals();
        updateGeometryTextures();
        updateGeometryIndices();
    }
    
    public void updateGeometry(Vector3f center, Vector2f topRight, Vector2f topLeft, Vector2f bottomRight, Vector2f bottomLeft) {
        this.center = center;
        this.texTopRight = topRight;
        this.texTopLeft = topLeft;
        this.texBotRight = bottomRight;
        this.texBotLeft = bottomLeft;
        this.correctCorners = false;
        vectorStore = new Vector3f[8];
        for (int i = 0; i < vectorStore.length; i++) {
            vectorStore[i] = new Vector3f();
        }
        updateGeometry();
   }

    /** Sets the correct indices array for the box. */
    private void updateGeometryIndices() {
        setIndexBuffer(BufferUtils.createIntBuffer(getIndexBuffer(), 36));
        setTriangleQuantity(12);
        int[] ints = new int[6];
        for (int i = 0; i < 6; i++) {
            ints[0] = i * 4 + 0;
            ints[1] = i * 4 + 1;
            ints[2] = i * 4 + 3;
            ints[3] = i * 4 + 1;
            ints[4] = i * 4 + 2;
            ints[5] = i * 4 + 3;
            getIndexBuffer().put(ints);
        }
    }

    /** Sets the correct normal array for the box. */
    private void updateGeometryNormals() {
        setNormalBuffer(BufferUtils.createVector3Buffer(getNormalBuffer(), 24));
        getNormalBuffer().put(new float[] {
                 yAxis.x,  yAxis.y,  yAxis.z,  yAxis.x,  yAxis.y,  yAxis.z,  yAxis.x,  yAxis.y,  yAxis.z,  yAxis.x,  yAxis.y,  yAxis.z, // top
                 xAxis.x,  xAxis.y,  xAxis.z,  xAxis.x,  xAxis.y,  xAxis.z,  xAxis.x,  xAxis.y,  xAxis.z,  xAxis.x,  xAxis.y,  xAxis.z, // right
                -xAxis.x, -xAxis.y, -xAxis.z, -xAxis.x, -xAxis.y, -xAxis.z, -xAxis.x, -xAxis.y, -xAxis.z, -xAxis.x, -xAxis.y, -xAxis.z, // left
                -yAxis.x, -yAxis.y, -yAxis.z, -yAxis.x, -yAxis.y, -yAxis.z, -yAxis.x, -yAxis.y, -yAxis.z, -yAxis.x, -yAxis.y, -yAxis.z, // bottom
                -zAxis.x, -zAxis.y, -zAxis.z, -zAxis.x, -zAxis.y, -zAxis.z, -zAxis.x, -zAxis.y, -zAxis.z, -zAxis.x, -zAxis.y, -zAxis.z, // back
                 zAxis.x,  zAxis.y,  zAxis.z,  zAxis.x,  zAxis.y,  zAxis.z,  zAxis.x,  zAxis.y,  zAxis.z,  zAxis.x,  zAxis.y,  zAxis.z, //front
        });
    }

    /** Sets the correct texture array for the box. */
    private void updateGeometryTextures() {
        if (getTextureCoords().get(0) == null) {
            getTextureCoords().set(0, new TexCoords(BufferUtils.createFloatBuffer(new float[] {
                    texTopRight.x, texTopRight.y, texTopLeft.x, texTopLeft.y, texBotLeft.x, texBotLeft.y, texBotRight.x, texBotRight.y,
                    texTopRight.x, texTopRight.y, texTopLeft.x, texTopLeft.y, texBotLeft.x, texBotLeft.y, texBotRight.x, texBotRight.y,
                    texTopRight.x, texTopRight.y, texTopLeft.x, texTopLeft.y, texBotLeft.x, texBotLeft.y, texBotRight.x, texBotRight.y,
                    texTopRight.x, texTopRight.y, texTopLeft.x, texTopLeft.y, texBotLeft.x, texBotLeft.y, texBotRight.x, texBotRight.y,
                    texTopRight.x, texTopRight.y, texTopLeft.x, texTopLeft.y, texBotLeft.x, texBotLeft.y, texBotRight.x, texBotRight.y,
                    texTopRight.x, texTopRight.y, texTopLeft.x, texTopLeft.y, texBotLeft.x, texBotLeft.y, texBotRight.x, texBotRight.y
            })));
        }
    }

    /** Sets the correct vertex information for the box. */
    private void updateGeometryVertices() {
        computeCorners();
        setVertexBuffer(BufferUtils.createVector3Buffer(getVertexBuffer(), 24));
        setVertexCount(24);
        Vector3f[] v = vectorStore; // use an alias to save typring ;-)
        getVertexBuffer().put(new float[] {
                v[0].x, v[0].y, v[0].z, v[1].x, v[1].y, v[1].z, v[5].x, v[5].y, v[5].z, v[3].x, v[3].y, v[3].z, // top
                v[0].x, v[0].y, v[0].z, v[3].x, v[3].y, v[3].z, v[6].x, v[6].y, v[6].z, v[2].x, v[2].y, v[2].z, // right
                v[5].x, v[5].y, v[5].z, v[1].x, v[1].y, v[1].z, v[4].x, v[4].y, v[4].z, v[7].x, v[7].y, v[7].z, // left
                v[6].x, v[6].y, v[6].z, v[7].x, v[7].y, v[7].z, v[4].x, v[4].y, v[4].z, v[2].x, v[2].y, v[2].z, // bottom
                v[3].x, v[3].y, v[3].z, v[5].x, v[5].y, v[5].z, v[7].x, v[7].y, v[7].z, v[6].x, v[6].y, v[6].z, // back
                v[1].x, v[1].y, v[1].z, v[4].x, v[4].y, v[4].z, v[2].x, v[2].y, v[2].z, v[0].x, v[0].y, v[0].z  // front
        });
    }
    
    public void write(JMEExporter e) throws IOException {
        super.write(e);

        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(center, "center", Vector3f.ZERO);
        capsule.write(xAxis, "xAxis", Vector3f.UNIT_X);
        capsule.write(yAxis, "yAxis", Vector3f.UNIT_Y);
        capsule.write(zAxis, "zAxis", Vector3f.UNIT_Z);
        capsule.write(extent, "extent", Vector3f.ZERO);
        capsule.write(texTopRight, "texTopRight", new Vector2f(1, 1));
        capsule.write(texTopLeft, "texTopLeft", new Vector2f(1, 0));
        capsule.write(texBotRight, "texBotRight", new Vector2f(0, 1));
        capsule.write(texBotLeft, "texBotLeft", new Vector2f(0, 0));
        capsule.write(vectorStore, "vectorStore", new Vector3f[8]);
        capsule.write(correctCorners, "correctCorners", false);
    }
}