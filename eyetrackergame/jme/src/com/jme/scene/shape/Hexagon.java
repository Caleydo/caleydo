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
// $Id: Hexagon.java 4131 2009-03-19 20:15:28Z blaine.dev $
package com.jme.scene.shape;

import java.io.IOException;

import com.jme.math.Vector3f;
import com.jme.scene.TexCoords;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.geom.BufferUtils;

/**
 * <code>Hexagon</code> provides an extension of <code>TriMesh</code>. A
 * <code>Hexagon</code> provides a regular hexagon with each triangle having
 * side length that is given in the constructor.
 * 
 * @author Joel Schuster
 * @version $Revision: 4131 $, $Date: 2009-03-19 21:15:28 +0100 (Do, 19 Mär 2009) $
 */
public class Hexagon extends RegularPolyhedron {

    private static final long serialVersionUID = 1L;

    private static final int NUM_POINTS = 7;

    private static final int NUM_TRIS = 6;

    /**
     * Hexagon Constructor instantiates a new Hexagon. This element is center on
     * 0,0,0 with all normals pointing up. The user must move and rotate for
     * positioning.
     * 
     * @param name
     *            the name of the scene element. This is required for
     *            identification and comparision purposes.
     * @param sideLength
     *            The length of all the sides of the tiangles
     */
    public Hexagon(String name, float sideLength) {
        super(name);
        updateGeometry(sideLength);
    }

    protected void doUpdateGeometry() {
        setVertexCount(NUM_POINTS);
        setVertexBuffer(BufferUtils.createVector3Buffer(getVertexCount()));
        setNormalBuffer(BufferUtils.createVector3Buffer(getVertexCount()));
        getTextureCoords().set(0,
                new TexCoords(BufferUtils.createVector2Buffer(getVertexCount())));
        setTriangleQuantity(NUM_TRIS);
        setIndexBuffer(BufferUtils.createIntBuffer(3 * getTriangleCount()));
        setVertexData();
        setIndexData();
        setTextureData();
        setNormalData();
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        sideLength = capsule.readInt("sideLength", 0);
    }

    /**
     * Sets up the indexes of the mesh. These go in a clockwise fashion and thus
     * only the 'up' side of the hex is lit properly. If you wish to have to
     * either set two sided lighting or create two hexes back-to-back
     */

    private void setIndexData() {
        getIndexBuffer().rewind();
        // tri 1
        getIndexBuffer().put(0);
        getIndexBuffer().put(6);
        getIndexBuffer().put(1);
        // tri 2
        getIndexBuffer().put(1);
        getIndexBuffer().put(6);
        getIndexBuffer().put(2);
        // tri 3
        getIndexBuffer().put(2);
        getIndexBuffer().put(6);
        getIndexBuffer().put(3);
        // tri 4
        getIndexBuffer().put(3);
        getIndexBuffer().put(6);
        getIndexBuffer().put(4);
        // tri 5
        getIndexBuffer().put(4);
        getIndexBuffer().put(6);
        getIndexBuffer().put(5);
        // tri 6
        getIndexBuffer().put(5);
        getIndexBuffer().put(6);
        getIndexBuffer().put(0);
    }

    /**
     * Sets all the default vertex normals to 'up', +1 in the Z direction.
     */
    private void setNormalData() {
        Vector3f zAxis = new Vector3f(0, 0, 1);
        for (int i = 0; i < NUM_POINTS; i++)
            BufferUtils.setInBuffer(zAxis, getNormalBuffer(), i);
    }

    private void setTextureData() {
        getTextureCoords().get(0).coords.put(0.25f).put(0);
        getTextureCoords().get(0).coords.put(0.75f).put(0);
        getTextureCoords().get(0).coords.put(1.0f).put(0.5f);
        getTextureCoords().get(0).coords.put(0.75f).put(1.0f);
        getTextureCoords().get(0).coords.put(0.25f).put(1.0f);
        getTextureCoords().get(0).coords.put(0.0f).put(0.5f);
        getTextureCoords().get(0).coords.put(0.5f).put(0.5f);
    }

    /**
     * Vertexes are set up like this: 0__1 / \ / \ 5/__\6/__\2 \ / \ / \ /___\ /
     * 4 3 All lines on this diagram are sideLength long. Therefore, the width
     * of the hexagon is sideLength * 2, and the height is 2 * the height of one
     * equalateral triangle with all side = sideLength which is .866
     */
    private void setVertexData() {
        getVertexBuffer().put(-(sideLength / 2)).put(sideLength * 0.866f).put(
                0.0f);
        getVertexBuffer().put(sideLength / 2).put(sideLength * 0.866f)
                .put(0.0f);
        getVertexBuffer().put(sideLength).put(0.0f).put(0.0f);
        getVertexBuffer().put(sideLength / 2).put(-sideLength * 0.866f).put(
                0.0f);
        getVertexBuffer().put(-(sideLength / 2)).put(-sideLength * 0.866f).put(
                0.0f);
        getVertexBuffer().put(-sideLength).put(0.0f).put(0.0f);
        getVertexBuffer().put(0.0f).put(0.0f).put(0.0f);
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(sideLength, "sideLength", 0);
    }

}
