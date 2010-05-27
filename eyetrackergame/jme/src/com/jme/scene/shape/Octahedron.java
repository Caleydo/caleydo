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
// $Id: Octahedron.java 4639 2009-08-29 00:34:05Z skye.book $
package com.jme.scene.shape;

import java.io.IOException;
import java.nio.IntBuffer;

import com.jme.math.FastMath;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.TexCoords;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.geom.BufferUtils;

/**
 * A regular polyhedron with 8 faces.
 * <p>
 * It looks somewhat like two pyramids placed bottom to bottom (or a 8-sided
 * die for all you D&D fans!).
 * 
 * @author Mark Powell
 * @version $Revision: 4639 $, $Date: 2009-08-29 02:34:05 +0200 (Sa, 29 Aug 2009) $
 */
public class Octahedron extends RegularPolyhedron {

    private static final long serialVersionUID = 1L;

    private static final int NUM_POINTS = 6;

    private static final int NUM_TRIS = 8;

    public Octahedron() {
    }

    /**
     * Creates an octahedron with center at the origin. The length of the
     * sides are given.
     * 
     * @param name
     *            The name of the octahedron.
     * @param sideLength
     *            The length of each side of the octahedron.
     */
    public Octahedron(String name, float sideLength) {
        super(name);
        updateGeometry(sideLength);
    }

    protected void doUpdateGeometry() {
        setVertexCount(NUM_POINTS);
        setVertexBuffer(BufferUtils.createVector3Buffer(NUM_POINTS));
        setNormalBuffer(BufferUtils.createVector3Buffer(NUM_POINTS));
        setTextureCoords(new TexCoords(BufferUtils.createVector2Buffer(NUM_POINTS)), 0);
        setTriangleQuantity(NUM_TRIS);
        setIndexBuffer(BufferUtils.createIntBuffer(3 * getTriangleCount()));
        setVertexData();
        setNormalData();
        setTextureData();
        setIndexData();
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        sideLength = capsule.readInt("sideLength", 0);
    }

    private void setIndexData() {
        IntBuffer indices = getIndexBuffer();
        indices.rewind();
        indices.put(4).put(0).put(2);
        indices.put(4).put(2).put(1);
        indices.put(4).put(1).put(3);
        indices.put(4).put(3).put(0);
        indices.put(5).put(2).put(0);
        indices.put(5).put(1).put(2);
        indices.put(5).put(3).put(1);
        indices.put(5).put(0).put(3);

        if (!true) {
            for (int i = 0; i < getTriangleCount(); i++) {
                int iSave = getIndexBuffer().get(3 * i + 1);
                getIndexBuffer()
                        .put(3 * i + 1, getIndexBuffer().get(3 * i + 2));
                getIndexBuffer().put(3 * i + 2, iSave);
            }
        }
    }

    private void setNormalData() {
        Vector3f norm = new Vector3f();
        for (int i = 0; i < NUM_POINTS; i++) {
            BufferUtils.populateFromBuffer(norm, getVertexBuffer(), i);
            norm.normalizeLocal();
            BufferUtils.setInBuffer(norm, getNormalBuffer(), i);
        }
    }

    private void setTextureData() {
        Vector2f tex = new Vector2f();
        Vector3f vert = new Vector3f();
        for (int i = 0; i < NUM_POINTS; i++) {
            BufferUtils.populateFromBuffer(vert, getVertexBuffer(), i);
            if (FastMath.abs(vert.z) < sideLength) {
                tex.x = 0.5f * (1.0f + FastMath.atan2(vert.y, vert.x)
                        * FastMath.INV_PI);
            } else {
                tex.x = 0.5f;
            }
            tex.y = FastMath.acos(vert.z) * FastMath.INV_PI;
            getTextureCoords().get(0).coords.put(tex.x).put(tex.y);
        }
    }

    private void setVertexData() {
        getVertexBuffer().put(sideLength).put(0.0f).put(0.0f);
        getVertexBuffer().put(-sideLength).put(0.0f).put(0.0f);
        getVertexBuffer().put(0.0f).put(sideLength).put(0.0f);
        getVertexBuffer().put(0.0f).put(-sideLength).put(0.0f);
        getVertexBuffer().put(0.0f).put(0.0f).put(sideLength);
        getVertexBuffer().put(0.0f).put(0.0f).put(-sideLength);
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(sideLength, "sideLength", 0);
    }

}