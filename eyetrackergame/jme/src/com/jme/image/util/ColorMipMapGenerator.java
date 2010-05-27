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

package com.jme.image.util;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.jme.image.Image;
import com.jme.math.FastMath;
import com.jme.renderer.ColorRGBA;
import com.jme.system.JmeException;
import com.jme.util.geom.BufferUtils;

/**
 * 
 * <code>ColorMipMapGenerator</code>
 *
 * @author Joshua Slack
 * @version $Revision: 4131 $
 *
 */
public class ColorMipMapGenerator {

    /**
     * Generates a jme Image object containing a mipmapped Image. Each mipmap is
     * a solid color. The first X mipmap colors are defined in topColors, any
     * remaining mipmaps are a shade of default color.
     * 
     * @param size
     *            dimensions of the texture (square)
     * @param topColors
     *            initial colors to use for the mipmaps
     * @param defaultColor
     *            color to use for remaining mipmaps, scaled darker for each
     *            successive mipmap
     * @return generated Image object
     */
    public static Image generateColorMipMap(int size, ColorRGBA[] topColors, ColorRGBA defaultColor) {
        
        if (!FastMath.isPowerOfTwo(size))
            throw new JmeException("size must be power of two!");
        
        int mips = (int)(FastMath.log(size) / FastMath.log(2)) + 1;
        Image rVal = new Image(Image.Format.RGBA8, size, size, 0, (ArrayList<ByteBuffer>)null);
        
        int bufLength = size * size * 4;
        int[] mipLengths = new int[mips];
        mipLengths[0] = bufLength;
        for (int x = 1; x < mips; x++) {
            mipLengths[x] = mipLengths[x-1] >> 1;
            bufLength += (mipLengths[x]);
        }
        rVal.setMipMapSizes(mipLengths);
        
        ByteBuffer bb = BufferUtils.createByteBuffer(bufLength);
        
        int[] base = new int[] {(int)(defaultColor.r * 255), (int)(defaultColor.g * 255), (int)(defaultColor.b * 255)};
        
        for (int x = 0; x < mips; x++) {
            int length = mipLengths[x] >> 2;
            float div = (float)(mips-x+topColors.length) / mips;
            for (int i = 0; i < length; i++) {
                if (x >= topColors.length) {
                    bb.put((byte)(base[0] * div));
                    bb.put((byte)(base[1] * div));
                    bb.put((byte)(base[2] * div));
                } else {
                    bb.put((byte)(topColors[x].r * 255));
                    bb.put((byte)(topColors[x].g * 255));
                    bb.put((byte)(topColors[x].b * 255));
                }
                bb.put((byte)255);
            }
        }
        bb.rewind();
        
        rVal.setData(bb);
        
        return rVal;
    }
    
}
