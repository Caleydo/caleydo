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

package com.jmex.audio.stream;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.jmex.audio.AudioTrack.Format;
import com.jmex.audio.filter.Filter;

/**
 * Super class for audio streams implementing the ability to interpret a
 * specific audio format.
 * 
 * @author Arman Ozcelik
 * @author Joshua Slack
 * @version $Id: AudioInputStream.java 4133 2009-03-19 20:40:11Z blaine.dev $
 */
public abstract class AudioInputStream extends FilterInputStream {

    protected ArrayList<Filter> filters;
    private URL resource;
    private float length;

    public AudioInputStream(URL resource, float length) throws IOException {
        super(resource.openStream());
        this.resource = resource;
        this.length = length;
        filters = new ArrayList<Filter>();
    }

    /**
     * Reads up to len bytes of data from the input stream into a ByteBuffer.
     * 
     * @param b
     *            the buffer into which the data is read.
     * @param off
     *            the start offset of the data.
     * @param len
     *            the maximum number of bytes read.
     * @return the total number of bytes read into the buffer, or -1 if there is
     *         no more data because the end of the stream has been reached.
     */
    public abstract int read(ByteBuffer buffer, int offset, int length)
            throws IOException;

    /**
     * Adds a DSP filter on this stream
     * 
     * @param f
     *            the filter to apply on the stream
     */
    public void addFilter(Filter f) {
        filters.add(f);
    }

    /**
     * @return number of channels in this stream
     */
    public abstract int getChannelCount();

    /**
     * @return the bitrate of this stream
     */
    public abstract int getBitRate();

    /**
     * @return the bit depth of this stream
     */
    public abstract int getDepth();

    public URL getResource() {
        return resource;
    }

    public abstract AudioInputStream makeNew() throws IOException;

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public static Format sniffFormat(InputStream stream) throws IOException {
        byte[] sniffed = new byte[128];
        int count = stream.read(sniffed);
        if (count >= 4 && sniffed[0] == 0x4F && sniffed[1] == 0x67 && sniffed[2] == 0x67 && sniffed[3] == 0x53)
            return Format.OGG;
        else if (count >= 12 && sniffed[0] == 0x52 && sniffed[1] == 0x49 && sniffed[2] == 0x46 && sniffed[3] == 0x46 &&
                sniffed[8] == 0x57 && sniffed[9] == 0x41 && sniffed[10] == 0x56 && sniffed[11] == 0x45)
            return Format.WAV;
        else
            return null; // unsupported type.
    }

}
