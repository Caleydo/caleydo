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

import java.nio.FloatBuffer;

import com.jme.math.FastMath;
import com.jme.util.geom.BufferUtils;

/**
 * <code>Circle</code> consists of Line Segments. you can use Line's methods to
 * set circle property such as width, stipple.
 * 
 * @author YongHoon Lim (mulova)
 */
public class Circle extends Line {
    private static final long serialVersionUID = 1L;


    /**
     * create line with
     * 
     * @param name
     *            the name of the scene element. This is required for
     *            identification and comparison purposes.
     * @param samples
     *            sampling rate to generate circle
     * @param radius
     *            circle size
     */
    public Circle(String name, int samples, float radius) {
        super(name);
        FloatBuffer vertexBuf = createVertex(samples, radius);
        FloatBuffer normalBuf = createNormal(samples);
        FloatBuffer colorBuf = createColor(samples);

        reconstruct(vertexBuf, normalBuf, colorBuf, null);
        setMode(Mode.Loop);
        setLightCombineMode(LightCombineMode.Off);
    }


    /**
     * @param samples
     *            sampling rate to generate circle
     * @return the color buffer of this circle
     */
    private FloatBuffer createColor(int samples) {
        FloatBuffer buf = BufferUtils.createColorBuffer(samples);
        for (int i = 0; i < samples; i++) {
            buf.put(1).put(1).put(1).put(1);
        }
        return buf;
    }


    /**
     * @param samples
     *            sampling rate to generate circle
     * @return the normal buffer of this circle
     */
    private FloatBuffer createNormal(int samples) {
        FloatBuffer buf = BufferUtils.createVector3Buffer(samples);
        for (int i = 0; i < samples; i++) {
            buf.put(0).put(0).put(1);
        }
        return buf;
    }


    /**
     * @param samples
     *            sampling rate to generate circle
     * @param radius
     *            circle size
     * @return the vertex buffer of this circle
     */
    private FloatBuffer createVertex(int sample, float radius) {
        FloatBuffer buf = BufferUtils.createVector3Buffer(sample);

        buf.rewind();
        for (int i = 0; i < sample; i++) {
            float theta = FastMath.TWO_PI / sample * i;
            float x = FastMath.cos(theta) * radius;
            float y = FastMath.sin(theta) * radius;
            buf.put(x).put(y).put(0);
        }
        return buf;
    }
}
