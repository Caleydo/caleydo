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

package com.jme.util.shader.uniformtypes;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.shader.ShaderVariable;

/** ShaderVariablePointerByte */
public class ShaderVariablePointerByte extends ShaderVariable {
    /**
     * Specifies the number of values for each element of the generic vertex
     * attribute array. Must be 1, 2, 3, or 4.
     */
    public int size;
    /**
     * Specifies the byte offset between consecutive attribute values. If stride
     * is 0 (the initial value), the attribute values are understood to be
     * tightly packed in the array.
     */
    public int stride;
    /**
     * Specifies whether fixed-point data values should be normalized (true) or
     * converted directly as fixed-point values (false) when they are accessed.
     */
    public boolean normalized;
    /** Specifies if the data is in signed or unsigned format */
    public boolean unsigned;
    /** The data for the attribute value */
    public ByteBuffer data;

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);

        capsule.write(size, "size", 0);
        capsule.write(stride, "stride", 0);
        capsule.write(normalized, "normalized", false);
        capsule.write(unsigned, "unsigned", false);
        capsule.write(data, "data", null);
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);

        size = capsule.readInt("size", 0);
        stride = capsule.readInt("stride", 0);
        normalized = capsule.readBoolean("normalized", false);
        unsigned = capsule.readBoolean("unsigned", false);
        data = capsule.readByteBuffer("data", null);
    }
}
