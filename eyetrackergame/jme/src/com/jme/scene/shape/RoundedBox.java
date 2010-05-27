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
// $Id: RoundedBox.java 4131 2009-03-19 20:15:28Z blaine.dev $
package com.jme.scene.shape;

import java.io.IOException;
import java.nio.FloatBuffer;

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
 * @author Pirx
 * @version $Revision: 4131 $, $Date: 2009-03-19 21:15:28 +0100 (Do, 19 Mär 2009) $
 */
public class RoundedBox extends TriMesh implements Savable {

    private static final long serialVersionUID = 1L;

    private static final Vector3f EXTENT = new Vector3f(0.52f, 0.52f, 0.52f);
    private static final Vector3f BORDER = new Vector3f(0.05f, 0.05f, 0.05f);
    private static final Vector3f SLOPE  = new Vector3f(0.02f, 0.02f, 0.02f);

    private Vector3f extent;
    private Vector3f border;
    private Vector3f slope;

    /** Creates a new instance of RoundedBox */
    public RoundedBox(String name) {
        super(name);
        updateGeometry(EXTENT.clone(), BORDER.clone(), SLOPE.clone());
    }

    public RoundedBox(String name, Vector3f extent) {
        super(name);
        updateGeometry(extent, BORDER.clone(), SLOPE.clone());
    }

    public RoundedBox(String name, Vector3f extent, Vector3f border, Vector3f slope) {
        super(name);
        updateGeometry(extent, border, slope);
    }

    /** Creates a deep copy of this rounded box. */
    public RoundedBox clone() {
        return new RoundedBox(getName() + "_clone", (Vector3f) extent.clone(),
                (Vector3f) border.clone(), (Vector3f) slope.clone());
    }

    // XXX: can this be made private? I think that it's is only used in the
    // setVertexAndNormalData method in this class.
    /**
     * @deprecated this method will be made private.
     */
    public Vector3f[] computeVertices() {
        float nebx = -extent.x + border.x, nesy = -extent.y - slope.y,
              neby = -extent.y + border.y, nesz = -extent.z - slope.z,
              nebz = -extent.z + border.z, nesx = -extent.x - slope.x, 
              pebz =  extent.z - border.z, pesy =  extent.y + slope.y,
              peby =  extent.y - border.y, pesz =  extent.z + slope.z,
              pebx =  extent.x - border.x, pesx =  extent.x + slope.x;
        return new Vector3f[] {
                // Cube
                new Vector3f(-extent.x, -extent.y,  extent.z), // 0
                new Vector3f( extent.x, -extent.y,  extent.z), // 1
                new Vector3f(-extent.x, -extent.y, -extent.z), // 2
                new Vector3f( extent.x, -extent.y, -extent.z), // 3
                new Vector3f(-extent.x,  extent.y,  extent.z), // 4
                new Vector3f( extent.x,  extent.y,  extent.z), // 5
                new Vector3f(-extent.x,  extent.y, -extent.z), // 6
                new Vector3f( extent.x,  extent.y, -extent.z), // 7

                // Bottom
                new Vector3f(nebx, nesy, pebz), //  8 (0)
                new Vector3f(pebx, nesy, pebz), //  9 (1)
                new Vector3f(nebx, nesy, nebz), // 10 (2)
                new Vector3f(pebx, nesy, nebz), // 11 (3)

                // Front
                new Vector3f(nebx, neby, pesz), // 12 (0)
                new Vector3f(pebx, neby, pesz), // 13 (1)
                new Vector3f(nebx, peby, pesz), // 14 (4)
                new Vector3f(pebx, peby, pesz), // 15 (5)

                // Right
                new Vector3f(pesx, neby, pebz), // 16 (1)
                new Vector3f(pesx, neby, nebz), // 17 (3)
                new Vector3f(pesx, peby, pebz), // 18 (5)
                new Vector3f(pesx, peby, nebz), // 19 (7)

                // Back
                new Vector3f(nebx, neby, nesz), // 20 (2)
                new Vector3f(pebx, neby, nesz), // 21 (3)
                new Vector3f(nebx, peby, nesz), // 22 (6)
                new Vector3f(pebx, peby, nesz), // 23 (7)

                // Left
                new Vector3f(nesx, neby, pebz), // 24 (0)
                new Vector3f(nesx, neby, nebz), // 25 (2)
                new Vector3f(nesx, peby, pebz), // 26 (4)
                new Vector3f(nesx, peby, nebz), // 27 (6)

                // Top
                new Vector3f(nebx, pesy, pebz), // 28 (4)
                new Vector3f(pebx, pesy, pebz), // 29 (5)
                new Vector3f(nebx, pesy, nebz), // 30 (6)
                new Vector3f(pebx, pesy, nebz), // 31 (7)
        };
    }

    public Vector3f getBorder() {
        return border;
    }

    public Vector3f getExtent() {
        return extent;
    }

    public Vector3f getSlope() {
        return slope;
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        extent.set((Vector3f) capsule.readSavable("extent", Vector3f.ZERO.clone()));
        border.set((Vector3f) capsule.readSavable("border", Vector3f.ZERO.clone()));
        slope.set((Vector3f) capsule.readSavable("slope", Vector3f.ZERO.clone()));
    }

    private void setIndexData() {
        if (getIndexBuffer() == null) {
            int[] indices = new int[180];
            int[] data = new int[] {
                    0, 4, 1, 1, 4, 5, 1, 5, 3, 3, 5, 7, 3, 7, 2,
                    2, 7, 6, 2, 6, 0, 0, 6, 4, 4, 6, 5, 5, 6, 7
            };
            for (int i = 0; i < 6; i++) {
                for (int n = 0; n < 30; n++) {
                    indices[30 * i + n] = 8 * i + data[n];
                }
            }
            setIndexBuffer(BufferUtils.createIntBuffer(indices));
        }
    }

    private void setTextureData() {
        if (getTextureCoords().get(0) == null) {
            getTextureCoords().set(0, new TexCoords(BufferUtils.createVector2Buffer(48)));
            FloatBuffer tex = getTextureCoords().get(0).coords;

            float x = extent.x + slope.x, y = extent.y + slope.y, z = extent.z + slope.z;
            float[][] ratio = new float[][] {
                    { 0.5f * border.x / x, 0.5f * border.z / z },
                    { 0.5f * border.x / x, 0.5f * border.y / y },
                    { 0.5f * border.z / z, 0.5f * border.y / y },
                    { 0.5f * border.x / x, 0.5f * border.y / y },
                    { 0.5f * border.z / z, 0.5f * border.y / y },
                    { 0.5f * border.x / x, 0.5f * border.z / z }
            };

            final float[] floats = { 1, 0, 0, 0, 1, 1, 0, 1 };
            for (float[] r : ratio) {
                tex.put(floats);
                tex.put(new float[] {
                        1 - r[0], 0 + r[1], 0 + r[0], 0 + r[1],
                        1 - r[0], 1 - r[1], 0 + r[0], 1 - r[1]
                });
            }
        }
    }

    private void setVertexAndNormalData() {
        setVertexBuffer(BufferUtils.createVector3Buffer(getVertexBuffer(), 48));
        setNormalBuffer(BufferUtils.createVector3Buffer(48));
        setVertexCount(48);
        Vector3f[] vert = computeVertices(); // returns 32
        FloatBuffer vb = getVertexBuffer();
        FloatBuffer nb = getNormalBuffer();

        int[] order = {
                0, 1, 2, 3, 8, 9, 10, 11, // bottom
                1, 0, 5, 4, 13, 12, 15, 14, //front
                3, 1, 7, 5, 17, 16, 19, 18, // right
                2, 3, 6, 7, 20, 21, 22, 23, // back
                0, 2, 4, 6, 24, 25, 26, 27,  // left
                5, 4, 7, 6, 29, 28, 31, 30 // top
        };
        for (int i : order) {
            Vector3f vec = vert[i];
            vb.put(vec.x).put(vec.y).put(vec.z);
            Vector3f v = vec.normalize();
            nb.put(v.x).put(v.y).put(v.z);

        }
    }

    public void updateGeometry(Vector3f extent, Vector3f border, Vector3f slope) {
        this.extent = extent.subtract(slope);
        this.border = border;
        this.slope = slope;
        setVertexAndNormalData();
        setTextureData();
        setIndexData();
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(extent, "extent", Vector3f.ZERO);
        capsule.write(border, "border", Vector3f.ZERO);
        capsule.write(slope, "slope", Vector3f.ZERO);
    }

}