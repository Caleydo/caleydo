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

package com.jme.scene.shape;

import java.nio.FloatBuffer;

import com.jme.math.Vector3f;

/**
 * The used Texture is 1 Unit wide and 8 Units high. (to avoid rescaling)<br>
 * The first 6 squares are mapped to the sides of the Box.<br>
 * Everything else is exactly as in {@link Box}.<br>
 */
public class MultiFaceBox extends Box {

    private static final long serialVersionUID = 1L;

    public MultiFaceBox() {
        super();
        remap();
    }

    public MultiFaceBox(String name) {
        super(name);
        remap();
    }

    public MultiFaceBox(String name, Vector3f min, Vector3f max) {
        super(name, min, max);
        remap();
    }

    public MultiFaceBox(String name, Vector3f center, float xExtent,
            float yExtent, float zExtent) {
        super(name, center, xExtent, yExtent, zExtent);
        remap();
    }

    private void remap() {
        FloatBuffer fb = getTextureCoords(0).coords;
        fb.rewind();
        for (int i = 0; i < 6; i++) {
            float top = i / 8f;
            float bottom = (i + 1) / 8f;
            float[] tex = new float[] { 1, bottom, 0, bottom, 0, top, 1, top };
            fb.put(tex);
        }
    }
}