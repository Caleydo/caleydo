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
// $Id: Dodecahedron.java 4639 2009-08-29 00:34:05Z skye.book $
package com.jme.scene.shape;

import java.io.IOException;
import java.nio.FloatBuffer;
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
 * A regular polyhedron with 12 faces.
 * 
 * @author Joshua Slack
 * @version $Revision: 4639 $, $Date: 2009-08-29 02:34:05 +0200 (Sa, 29 Aug 2009) $
 */
public class Dodecahedron extends RegularPolyhedron {

    private static final long serialVersionUID = 1L;

    private static final int NUM_POINTS = 20;
    private static final int NUM_TRIS = 36;

    /** <strong>NOT API:</strong> for internal use, do not call from user code. */
    public Dodecahedron() {}

    /**
     * origin. The length of the sides will be as specified in sideLength.
     * 
     * @param name
     *            The name of the octahedron.
     * @param sideLength
     *            The length of each side of the octahedron.
     */
    public Dodecahedron(String name, float sideLength) {
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
        updateGeometryVertices();
        updateGeometryNormals();
        updateGeometryTextures();
        updateGeometryIndices();
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        sideLength = capsule.readInt("sideLength", 0);
    }

    private void updateGeometryIndices() {
        IntBuffer indices = getIndexBuffer();
        indices.rewind();
        indices.put(0).put(8).put(9);
        indices.put(0).put(9).put(4);
        indices.put(0).put(4).put(16);
        indices.put(0).put(12).put(13);
        indices.put(0).put(13).put(1);
        indices.put(0).put(1).put(8);
        indices.put(0).put(16).put(17);
        indices.put(0).put(17).put(2);
        indices.put(0).put(2).put(12);
        indices.put(8).put(1).put(18);
        indices.put(8).put(18).put(5);
        indices.put(8).put(5).put(9);
        indices.put(12).put(2).put(10);
        indices.put(12).put(10).put(3);
        indices.put(12).put(3).put(13);
        indices.put(16).put(4).put(14);
        indices.put(16).put(14).put(6);
        indices.put(16).put(6).put(17);
        indices.put(9).put(5).put(15);
        indices.put(9).put(15).put(14);
        indices.put(9).put(14).put(4);
        indices.put(6).put(11).put(10);
        indices.put(6).put(10).put(2);
        indices.put(6).put(2).put(17);
        indices.put(3).put(19).put(18);
        indices.put(3).put(18).put(1);
        indices.put(3).put(1).put(13);
        indices.put(7).put(15).put(5);
        indices.put(7).put(5).put(18);
        indices.put(7).put(18).put(19);
        indices.put(7).put(11).put(6);
        indices.put(7).put(6).put(14);
        indices.put(7).put(14).put(15);
        indices.put(7).put(19).put(3);
        indices.put(7).put(3).put(10);
        indices.put(7).put(10).put(11);

        if (!true) { // outside view
            for (int i = 0; i < getTriangleCount(); i++) {
                int iSave = getIndexBuffer().get(3 * i + 1);
                getIndexBuffer()
                        .put(3 * i + 1, getIndexBuffer().get(3 * i + 2));
                getIndexBuffer().put(3 * i + 2, iSave);
            }
        }

    }

    private void updateGeometryNormals() {
        Vector3f norm = new Vector3f();
        for (int i = 0; i < NUM_POINTS; i++) {
            BufferUtils.populateFromBuffer(norm, getVertexBuffer(), i);
            norm.normalizeLocal();
            BufferUtils.setInBuffer(norm, getNormalBuffer(), i);
        }
    }

    private void updateGeometryTextures() {
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

    private void updateGeometryVertices() {
        float fA = 1.0f / FastMath.sqrt(3.0f);
        float fB = FastMath.sqrt((3.0f - FastMath.sqrt(5.0f)) / 6.0f);
        float fC = FastMath.sqrt((3.0f + FastMath.sqrt(5.0f)) / 6.0f);
        fA *= sideLength;
        fB *= sideLength;
        fC *= sideLength;

        FloatBuffer vbuf = getVertexBuffer();
        vbuf.rewind();
        vbuf.put(fA).put(fA).put(fA);
        vbuf.put(fA).put(fA).put(-fA);
        vbuf.put(fA).put(-fA).put(fA);
        vbuf.put(fA).put(-fA).put(-fA);
        vbuf.put(-fA).put(fA).put(fA);
        vbuf.put(-fA).put(fA).put(-fA);
        vbuf.put(-fA).put(-fA).put(fA);
        vbuf.put(-fA).put(-fA).put(-fA);
        vbuf.put(fB).put(fC).put(0.0f);
        vbuf.put(-fB).put(fC).put(0.0f);
        vbuf.put(fB).put(-fC).put(0.0f);
        vbuf.put(-fB).put(-fC).put(0.0f);
        vbuf.put(fC).put(0.0f).put(fB);
        vbuf.put(fC).put(0.0f).put(-fB);
        vbuf.put(-fC).put(0.0f).put(fB);
        vbuf.put(-fC).put(0.0f).put(-fB);
        vbuf.put(0.0f).put(fB).put(fC);
        vbuf.put(0.0f).put(-fB).put(fC);
        vbuf.put(0.0f).put(fB).put(-fC);
        vbuf.put(0.0f).put(-fB).put(-fC);
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(sideLength, "sideLength", 0);

    }
    
}
