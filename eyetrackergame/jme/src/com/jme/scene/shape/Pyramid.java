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
// $Id: Pyramid.java 4131 2009-03-19 20:15:28Z blaine.dev $
package com.jme.scene.shape;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.geom.BufferUtils;

/**
 * A four sided pyramid.
 * <p>
 * A pyramid is defined by a width at the base and a height. The pyramid is a
 * 4-sided pyramid with the center at (0,0), it will be axis aligned with the
 * peak being on the positive y axis and the base being in the x-z plane.
 * <p>
 * The texture that defines the look of the pyramid has the top point of the
 * pyramid as the top center of the texture, with the remaining texture
 * wrapping around it.
 * 
 * @author Mark Powell
 * @version $Revision: 4131 $, $Date: 2009-03-19 21:15:28 +0100 (Do, 19 Mär 2009) $
 */
public class Pyramid extends TriMesh {

    private static final long serialVersionUID = 1L;

    private float height;

    private float width;

    public Pyramid() {
    }

    /**
     * Constructor instantiates a new <code>Pyramid</code> object. The base
     * width and the height are provided.
     * 
     * @param name
     *            the name of the scene element. This is required for
     *            identification and comparision purposes.
     * @param width
     *            the base width of the pyramid.
     * @param height
     *            the height of the pyramid from the base to the peak.
     */
    public Pyramid(String name, float width, float height) {
        super(name);
        updateGeometry(width, height);
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        height = capsule.readFloat("height", 0);
        width = capsule.readFloat("width", 0);
    }

    public void updateGeometry(float width, float height) {
        this.width = width;
        this.height = height;

        // Update the vertex buffer
        float pkx = 0, pky = height / 2, pkz = 0;
        float vx0 = -width / 2, vy0 = -height / 2, vz0 = -width / 2;
        float vx1 =  width / 2, vy1 = -height / 2, vz1 = -width / 2;
        float vx2 =  width / 2, vy2 = -height / 2, vz2 =  width / 2;
        float vx3 = -width / 2, vy3 = -height / 2, vz3 =  width / 2;
        FloatBuffer verts = BufferUtils.createVector3Buffer(16);
        verts.put(new float[] {
                vx3, vy3, vz3, vx2, vy2, vz2, vx1, vy1, vz1, vx0, vy0, vz0, // base
                vx0, vy0, vz0, vx1, vy1, vz1, pkx, pky, pkz, // side 1
                vx1, vy1, vz1, vx2, vy2, vz2, pkx, pky, pkz, // side 2
                vx2, vy2, vz2, vx3, vy3, vz3, pkx, pky, pkz, // side 3
                vx3, vy3, vz3, vx0, vy0, vz0, pkx, pky, pkz  // side 4
        }); 
        verts.rewind();
        setVertexBuffer(verts);
        
        // Update the normals buffer
        FloatBuffer norms = BufferUtils.createVector3Buffer(16);
        float pn = 0.70710677f, nn = -0.70710677f;
        norms.put(new float[] {
                0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, // top
                0, pn, nn, 0, pn, nn, 0, pn, nn, // back
                pn, pn, 0, pn, pn, 0, pn, pn, 0, // right
                0, pn, pn, 0, pn, pn, 0, pn, pn, // front
                nn, pn, 0, nn, pn, 0, nn, pn, 0  // left
        });
        norms.rewind();
        setNormalBuffer(norms);

        // Update the texture buffer
        FloatBuffer texCoords = BufferUtils.createVector2Buffer(16);
        texCoords.put(new float[] {
                1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0.75f, 0, 0.5f, 1, 0.75f, 0, 0.5f,
                0, 0.5f, 1, 0.5f, 0, 0.25f, 0, 0.5f, 1, 0.25f, 0, 0, 0, 0.5f, 1                
        });
        texCoords.rewind();
        setTextureCoords(new TexCoords(texCoords), 0);

        // Update the indices buffer
        IntBuffer indices = BufferUtils.createIntBuffer(18);
        indices.put(new int[] {
                3, 2, 1, 3, 1, 0, 6, 5, 4, 9, 8, 7, 12, 11, 10, 15, 14, 13
        });
        indices.rewind();
        setIndexBuffer(indices);
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(height, "height", 0);
        capsule.write(width, "width", 0);
    }

}