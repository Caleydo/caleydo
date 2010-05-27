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

package com.jme.scene;

import java.io.IOException;
import java.nio.FloatBuffer;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.jme.util.geom.BufferUtils;

/**
 * Simple data class storing a buffer of floats and a number that indicates how
 * many floats to group together to make up a texture coordinate "tuple"
 * 
 * @author Joshua Slack
 */
public class TexCoords implements Savable {

    public FloatBuffer coords;
    public int perVert;

    public TexCoords() {
    }

    public TexCoords(FloatBuffer coords) {
        this(coords, 2);
    }

    public TexCoords(FloatBuffer coords, int coordsPerVert) {
        this.coords = coords;
        this.perVert = coordsPerVert;
    }

    public static TexCoords makeNew(Vector2f[] coords) {
        if (coords == null)
            return null;

        return new TexCoords(BufferUtils.createFloatBuffer(coords), 2);
    }

    public static TexCoords makeNew(Vector3f[] coords) {
        if (coords == null)
            return null;

        return new TexCoords(BufferUtils.createFloatBuffer(coords), 3);
    }

    public static TexCoords makeNew(float[] coords) {
        if (coords == null)
            return null;

        return new TexCoords(BufferUtils.createFloatBuffer(coords), 1);
    }

    /**
     * Check an incoming TexCoords object for null and correct size.
     * 
     * @param tc
     * @param vertexCount
     * @param perVert
     * @return tc if it is not null and the right size, otherwise it will be a
     *         new TexCoords object.
     */
    public static TexCoords ensureSize(TexCoords tc, int vertexCount,
            int perVert) {
        if (tc == null) {
            return new TexCoords(BufferUtils.createFloatBuffer(vertexCount
                    * perVert), perVert);
        }

        if (tc.coords.limit() == perVert * vertexCount && tc.perVert == perVert) {
            tc.coords.rewind();
            return tc;
        } else if (tc.coords.limit() == perVert * vertexCount) {
            tc.perVert = perVert;
        } else {
            tc.coords = BufferUtils.createFloatBuffer(vertexCount * perVert);
            tc.perVert = perVert;
        }

        return tc;
    }

    public Class getClassTag() {
        return TexCoords.class;
    }

    public void read(JMEImporter im) throws IOException {
        InputCapsule cap = im.getCapsule(this);
        coords = cap.readFloatBuffer("coords", null);
        perVert = cap.readInt("perVert", 0);
    }

    public void write(JMEExporter ex) throws IOException {
        OutputCapsule cap = ex.getCapsule(this);
        cap.write(coords, "coords", null);
        cap.write(perVert, "perVert", 0);
    }
}
