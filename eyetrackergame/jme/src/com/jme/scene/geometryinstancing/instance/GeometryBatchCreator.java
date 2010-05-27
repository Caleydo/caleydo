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
package com.jme.scene.geometryinstancing.instance;

import java.util.ArrayList;

import com.jme.scene.TriMesh;

/**
 * <code>GeometryBatchCreator</code> is a container class for
 * <code>GeometryInstances</code>.
 *
 * @author Patrik Lindegrén
 */
public class GeometryBatchCreator {
    protected ArrayList<GeometryInstance> instances;
    private int nVerts;
    private int nIndices;

    public GeometryBatchCreator() {
        instances = new ArrayList<GeometryInstance>(1);
        nVerts = 0;
        nIndices = 0;
    }

    public void clearInstances() {
        instances.clear();
        nVerts = 0;
        nIndices = 0;
    }

    public void addInstance(GeometryInstance geometryInstance) {
        if (geometryInstance == null) {
            return;
        }
        instances.add(geometryInstance);
        nIndices += geometryInstance.getNumIndices();
        nVerts += geometryInstance.getNumVerts();
    }

    public void removeInstance(GeometryInstance geometryInstance) {
        if (instances.remove(geometryInstance)) {
            nIndices -= geometryInstance.getNumIndices();
            nVerts -= geometryInstance.getNumVerts();
        }
    }

    public int getNumVertices() {
        return nVerts;
    }

    public int getNumIndices() {
        return nIndices;
    }

    public ArrayList<GeometryInstance> getInstances() {
        return instances;
    }

    public void commit(TriMesh batch) {
        for (GeometryInstance instance : instances) {
            instance.commit(batch);
        }
    }
}