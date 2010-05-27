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

package com.jme.scene.lod;

import java.io.IOException;
import java.io.Serializable;

import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * <code>CollapseRecord</code> originally ported from David Eberly's c++,
 * modifications and enhancements made from there.<br>
 * <br>
 * This class keeps an array of vertex index positions that are to be collapsed.
 * It is used to modify ClodMesh objects so that they contain less information.
 * 
 * @author Joshua Slack
 * @author Jack Lindamood (javadoc only)
 * @version $Id: CollapseRecord.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */

public class CollapseRecord implements Serializable, Savable {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new CollapseRecord.
     * 
     * @param toKeep
     *            The vertex index that is kept.
     * @param toThrow
     *            The vertex index that is thrown.
     * @param vertQuantity
     *            The new vertex quantity of the mesh using this record.
     * @param triQuantity
     *            The new triangle quantity of the mesh using this record.
     */
    public CollapseRecord(int toKeep, int toThrow, int vertQuantity,
            int triQuantity) {
        vertToKeep = toKeep;
        vertToThrow = toThrow;
        numbVerts = vertQuantity;
        numbTriangles = triQuantity;
    }

    /**
     * Creates a new collapse record with all values at their default.
     */
    public CollapseRecord() {
    }

    // edge <VKeep,VThrow> collapses so that VThrow is replaced by VKeep
    /**
     * An int index in the Mesh's vertex array. It is the vertex assigned from
     * the indices array. Used in collapsing the mesh.
     */
    public int vertToKeep = -1;
    /**
     * An int index in the Mesh's vertex array. It is the vertex assigned from
     * the indices array. Used in expanding the mesh.
     */
    public int vertToThrow = -1;

    // number of vertices after edge collapse
    public int numbVerts = 0;

    /** The number of triangles in the Mesh after edge collapse. */
    public int numbTriangles = 0;

    // connectivity array indices in [0..TQ-1] that contain VThrow
    /** Lenght of this indices array. */
    public int numbIndices = 0;
    /**
     * An integer value in the Mesh's indices array referencing a vertex to keep
     * or throw.
     */
    public int[] indices = null;

    public void write(JMEExporter e) throws IOException {
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(vertToKeep, "vertToKeep", -1);
        capsule.write(vertToThrow, "vertToThrow", -1);
        capsule.write(numbVerts, "numbVerts", 0);
        capsule.write(numbTriangles, "numbTriangles", 0);
        capsule.write(numbIndices, "numbIndices", 0);
        capsule.write(indices, "indices", null);
    }

    public void read(JMEImporter e) throws IOException {
        InputCapsule capsule = e.getCapsule(this);
        vertToKeep = capsule.readInt("vertToKeep", -1);
        vertToThrow = capsule.readInt("vertToThrow", -1);
        numbVerts = capsule.readInt("numbVerts", 0);
        numbTriangles = capsule.readInt("numbTriangles", 0);
        numbIndices = capsule.readInt("numbIndices", 0);
        indices = capsule.readIntArray("indices", null);
    }
    
    public Class getClassTag() {
        return this.getClass();
    }
}
