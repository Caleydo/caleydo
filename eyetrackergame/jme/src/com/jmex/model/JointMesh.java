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

package com.jmex.model;

import java.io.IOException;

import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * Started Date: Jun 11, 2004 JointMesh is the same as a TriMesh, but extends to
 * include an index array of joints and to store the original Vertex and Normal
 * information
 * 
 * 
 * @author Jack Lindamood
 */
public class JointMesh extends TriMesh {

    private static final long serialVersionUID = 1L;

    public int[] jointIndex;

    public Vector3f[] originalVertex;

    public Vector3f[] originalNormal;

    public JointMesh() {}
    
    public JointMesh(String name) {
        super(name);
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(jointIndex, "jointIndex", null);
        capsule.write(originalVertex, "originalVertex", null);
        capsule.write(originalNormal, "originalNormal", null);
    }
    
    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        jointIndex = capsule.readIntArray("jointIndex", null);
        Savable[] savs = capsule.readSavableArray("originalVertex", null);
        if (savs == null)
            originalVertex = null;
        else {
            originalVertex = new Vector3f[savs.length];
            for (int x = 0; x < savs.length; x++) {
                originalVertex[x] = (Vector3f)savs[x];
            }
        }
        savs = capsule.readSavableArray("originalNormal", null);
        if (savs == null)
            originalNormal = null;
        else {
            originalNormal = new Vector3f[savs.length];
            for (int x = 0; x < savs.length; x++) {
                originalNormal[x] = (Vector3f)savs[x];
            }
        }
    }
}