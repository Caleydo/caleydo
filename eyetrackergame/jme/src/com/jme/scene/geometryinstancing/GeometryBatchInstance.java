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
package com.jme.scene.geometryinstancing;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jme.math.Vector3f;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.scene.geometryinstancing.instance.GeometryInstance;

/**
 * <code>GeometryBatchInstance</code> uses a <code>GeometryBatchInstanceAttributes</code>
 * to define an instance of object in world space. Uses TriMesh as source
 * data for the instance, instead of GeomBatch which does not have an index
 * buffer.
 *
 * @author Patrik Lindegrén
 */
public class GeometryBatchInstance
        extends GeometryInstance<GeometryBatchInstanceAttributes> {
    public TriMesh instanceMesh;

    public GeometryBatchInstance(TriMesh sourceBatch,
                                 GeometryBatchInstanceAttributes attributes) {
        super(attributes);
        this.instanceMesh = sourceBatch;
    }

    /** Vector used to store and calculate world transformations */
    Vector3f worldVector = new Vector3f();

    /**
     * Uses the instanceAttributes to transform the instanceBatch into world
     * coordinates. The transformed instance mesh is added to the mesh.
     *
     * @param mesh
     */
    public void commit(TriMesh mesh) {
        if (mesh == null || instanceMesh == null || getNumVerts() <= 0) {
            return;
        }

        int nVerts = 0;

        // Texture buffers
        for (int i = 0; i < instanceMesh.getNumberOfUnits(); i++) {
            TexCoords texBufSrc = instanceMesh.getTextureCoords(i);
            TexCoords texBufDst = mesh.getTextureCoords(i);
            if (texBufSrc != null && texBufDst != null) {
                texBufSrc.coords.rewind();
                texBufDst.coords.put(texBufSrc.coords);
            }
        }

        // Vertex buffer
        FloatBuffer vertBufSrc = instanceMesh.getVertexBuffer();
        FloatBuffer vertBufDst = mesh.getVertexBuffer();
        if (vertBufSrc != null && vertBufDst != null) {
            vertBufSrc.rewind();
            nVerts = vertBufDst.position() / 3;
            for (int i = 0; i < instanceMesh.getVertexCount(); i++) {
                worldVector.set(vertBufSrc.get(), vertBufSrc.get(),
                                vertBufSrc.get());
                attributes.getWorldMatrix().mult(worldVector, worldVector);
                vertBufDst.put(worldVector.x);
                vertBufDst.put(worldVector.y);
                vertBufDst.put(worldVector.z);
            }
        }

        // Color buffer
        FloatBuffer colorBufSrc = instanceMesh.getColorBuffer();
        FloatBuffer colorBufDst = mesh.getColorBuffer();
        if (colorBufSrc != null && colorBufDst != null) {
            colorBufSrc.rewind();
            for (int i = 0; i < instanceMesh.getVertexCount(); i++) {
                colorBufDst.put(colorBufSrc.get() * attributes.getColor().r);
                colorBufDst.put(colorBufSrc.get() * attributes.getColor().g);
                colorBufDst.put(colorBufSrc.get() * attributes.getColor().b);
                colorBufDst.put(colorBufSrc.get() * attributes.getColor().a);
            }
        } else if (colorBufDst != null) {
            for (int i = 0; i < instanceMesh.getVertexCount(); i++) {
                colorBufDst.put(attributes.getColor().r);
                colorBufDst.put(attributes.getColor().g);
                colorBufDst.put(attributes.getColor().b);
                colorBufDst.put(attributes.getColor().a);
            }
        }

        // Normal buffer
        FloatBuffer normalBufSrc = instanceMesh.getNormalBuffer();
        FloatBuffer normalBufDst = mesh.getNormalBuffer();
        if (normalBufSrc != null && normalBufDst != null) {
            normalBufSrc.rewind();
            for (int i = 0; i < instanceMesh.getVertexCount(); i++) {
                worldVector.set(normalBufSrc.get(), normalBufSrc.get(),
                                normalBufSrc.get());
                attributes.getNormalMatrix().mult(worldVector, worldVector);
                worldVector.normalizeLocal();
                normalBufDst.put(worldVector.x);
                normalBufDst.put(worldVector.y);
                normalBufDst.put(worldVector.z);
            }
        }

        // Index buffer
        IntBuffer indexBufSrc = instanceMesh.getIndexBuffer();
        IntBuffer indexBufDst = mesh.getIndexBuffer();
        if (indexBufSrc != null && indexBufDst != null) {
            indexBufSrc.rewind();
            for (int i = 0; i < instanceMesh.getMaxIndex(); i++) {
                indexBufDst.put(nVerts + indexBufSrc.get());
            }
        }
    }

    public int getNumIndices() {
        if (instanceMesh == null) {
            return 0;
        }
        return instanceMesh.getMaxIndex();
    }

    public int getNumVerts() {
        if (instanceMesh == null) {
            return 0;
        }
        return instanceMesh.getVertexCount();
    }
}