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
package com.jme.bounding;

import java.util.Comparator;

import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;

public class TreeComparator implements Comparator {
    enum Axis {
        X, Y, Z;
    }

    private Axis axis;

    private Vector3f center;

    private TriMesh mesh;

    private Vector3f[] aCompare = new Vector3f[3];

    private Vector3f[] bCompare = new Vector3f[3];

    public void setAxis(Axis axis) {
        this.axis = axis;
    }

    public void setMesh(TriMesh mesh) {
        this.mesh = mesh;
    }

    public void setCenter(Vector3f center) {
        this.center = center;
    }

    public int compare(Object o1, Object o2) {
        int a = (Integer) o1;
        int b = (Integer) o2;

        if (a == b) {
            return 0;
        }

        Vector3f centerA = null;
        Vector3f centerB = null;
        mesh.getTriangle(a, aCompare);
        mesh.getTriangle(b, bCompare);
        centerA = aCompare[0].addLocal(aCompare[1].addLocal(aCompare[2]))
                .subtractLocal(center);
        centerB = bCompare[0].addLocal(bCompare[1].addLocal(bCompare[2]))
                .subtractLocal(center);

        switch (axis) {
            case X:
                if (centerA.x < centerB.x) {
                    return -1;
                }
                if (centerA.x > centerB.x) {
                    return 1;
                }
                return 0;
            case Y:
                if (centerA.y < centerB.y) {
                    return -1;
                }
                if (centerA.y > centerB.y) {
                    return 1;
                }
                return 0;
            case Z:
                if (centerA.z < centerB.z) {
                    return -1;
                }
                if (centerA.z > centerB.z) {
                    return 1;
                }
                return 0;
            default:
                return 0;
        }
    }
}
