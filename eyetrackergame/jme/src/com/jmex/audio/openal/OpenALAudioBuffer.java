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

package com.jmex.audio.openal;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.openal.AL10;

import com.jme.util.geom.BufferUtils;
import com.jmex.audio.AudioBuffer;

/**
 * @see AudioBuffer
 * @author Joshua Slack
 * @version $Id: OpenALAudioBuffer.java 4133 2009-03-19 20:40:11Z blaine.dev $
 */
public class OpenALAudioBuffer extends AudioBuffer {

    private int id;
    private ByteBuffer data;

    protected OpenALAudioBuffer(int id) {
        this.id = id;
    }

    public void setup(ByteBuffer data, int channels, int bitRate, float length, int depth) {
        super.setup(data, channels, bitRate, length, depth);
        this.data = data;
        boolean mono = channels == 1;

        int format = 0;
        if (depth == 8) {
            format = (mono ? AL10.AL_FORMAT_MONO8
                    : AL10.AL_FORMAT_STEREO8);
        } else if (depth == 16) {
            format = (mono ? AL10.AL_FORMAT_MONO16
                    : AL10.AL_FORMAT_STEREO16);                    
        }
        data.rewind();
        AL10.alBufferData(id, format, data, bitRate);
    }

    public void delete() {
        IntBuffer alBuffer = BufferUtils.createIntBuffer(1);
        alBuffer.put(id);
        alBuffer.rewind();
        AL10.alDeleteBuffers(alBuffer);
    }

    public int getBitDepth() {
        return AL10.alGetBufferi(id, AL10.AL_BITS);
    }

    public int getNumChannels() {
        return AL10.alGetBufferi(id, AL10.AL_CHANNELS);
    }

    public ByteBuffer getData() {
        return data;
    }

    public int getFrequency() {
        return AL10.alGetBufferi(id, AL10.AL_FREQUENCY);
    }

    public int getSize() {
        return AL10.alGetBufferi(id, AL10.AL_SIZE);
    }

    public int getId() {
        return id;
    }

    public static OpenALAudioBuffer generateBuffer() {
        IntBuffer alBuffers = BufferUtils.createIntBuffer(1);
        AL10.alGenBuffers(alBuffers);
        return new OpenALAudioBuffer(alBuffers.get(0));
    }
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        delete();
    }
}
