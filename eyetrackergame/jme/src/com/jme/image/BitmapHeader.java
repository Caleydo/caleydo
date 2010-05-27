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

package com.jme.image;

import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.io.IOException;

/**
 * <code>BitmapHeader</code> defines header information about a bitmap (BMP) image
 * file format.
 * @author Mark Powell
 * @version $Id: BitmapHeader.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public class BitmapHeader {
    /**
     * the number of bits that makes up the image.
     */
    public int bitcount;

    private int size;
    private int bisize;
    private int width;
    private int height;
    private int planes;

    private int compression;
    private int sizeimage;
    private int xpm;
    private int ypm;
    private int clrused;
    private int clrimp;

    /**
     *
     * <code>readMap32</code> reads a 32 bit bitmap file.
     * @param data the byte data that contains the file information.
     * @return the Image that contains the bitmap.
     */
    public java.awt.Image readMap32(byte[] data) {
        java.awt.Image image;
        int xwidth = sizeimage / height;
        int ndata[] = new int[height * width];
        byte brgb[] = new byte[width * 4 * height];

        for(int i = 0; i < width * 4 * height; i++) {
            if(i+54 >= data.length) {
                break;
            }
            brgb[i] = data[i + 54];
        }
        int nindex = 0;

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                ndata[width * (height - j - 1) + i] =
                    constructInt3(brgb, nindex);
                nindex += 4;
            }
        }

        image =
            Toolkit.getDefaultToolkit().createImage(
                new MemoryImageSource(width, height, ndata, 0, width));
        return (image);
    }

    /**
     *
     * <code>readMap24</code> reads a 24 bit bitmap file.
     * @param data the byte data that contains the file information.
     * @return the Image that contains the bitmap.
     */
    public java.awt.Image readMap24(byte[] data)
        throws IOException {
        java.awt.Image image;
        int npad = (sizeimage / height) - width * 3;
        int ndata[] = new int[height * width];
        byte brgb[] = new byte[(width + npad) * 3 * height];
        for(int i = 0; i < brgb.length; i++) {
            if(i + 54>= data.length) {
                break;
            }
            brgb[i] = data[i + 54];
        }

        int nindex = 0;

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                ndata[width * (height - j - 1) + i] =
                    constructInt3(brgb, nindex);
                nindex += 3;
            }
            nindex += npad;
        }

        image =
            Toolkit.getDefaultToolkit().createImage(
                new MemoryImageSource(width, height, ndata, 0, width));
        return image;
    }

    /**
     *
     * <code>readMap8</code> reads a 8 bit bitmap file.
     * @param data the byte data that contains the file information.
     * @return the Image that contains the bitmap.
     */
    public java.awt.Image readMap8(byte[] data) {
    	 java.awt.Image image;
        int nNumColors = 0;

        if (clrused > 0) {
            nNumColors = clrused;
        } else {
            nNumColors = (1 & 0xff) << bitcount;
        }

        if (sizeimage == 0) {
            sizeimage = ((((width * bitcount) + 31) & ~31) >> 3);
            sizeimage *= height;
        }

        int npalette[] = new int[nNumColors];
        byte bpalette[] = new byte[nNumColors * 4];

        for(int i = 0; i < nNumColors * 4; i++) {
            bpalette[i] = data[i + 54];
        }

        int nindex8 = 0;

        for (int n = 0; n < nNumColors; n++) {
            npalette[n] = constructInt3(bpalette, nindex8);
            nindex8 += 4;
        }

        int npad8 = (sizeimage / height) - width;
        int ndata8[] = new int[width * height];
        byte bdata[] = new byte[(width + npad8) * height];
        for(int i = 0; i < bdata.length; i++) {
            bdata[i] = data[i + bpalette.length + 54];
        }
        nindex8 = 0;

        for (int j8 = 0; j8 < height; j8++) {
            for (int i8 = 0; i8 < width; i8++) {
                ndata8[width * (height - j8 - 1) + i8] =
                    npalette[(bdata[nindex8] & 0xff)];
                nindex8++;
            }

            nindex8 += npad8;
        }

        image =
            Toolkit.getDefaultToolkit().createImage(
                new MemoryImageSource(
                    width,
                    height,
                    ndata8,
                    0,
                    width));

        return image;
    }

    /**
     * Builds an int from a byte array - convert little to big endian.
     * @param in Byte array to convert
     * @param offset offset in byte array
     * @return Big endian int from in[offset]-in[offset+3]
     */
    private int constructInt(byte[] in, int offset) {
        int ret = (in[offset + 3] & 0xff);
        ret = (ret << 8) | (in[offset + 2] & 0xff);
        ret = (ret << 8) | (in[offset + 1] & 0xff);
        ret = (ret << 8) | (in[offset + 0] & 0xff);
        return (ret);
    }

    /**
     * Builds an int from a byte array - convert little to big endian
     * set high order bytes to 0xfff..
     * @param in Byte array to convert.
     * @param offset Offset in byte array.
     * @return Big endian int from in[offset]-in[offset+2] with higher order of 0xff
     */
    private int constructInt3(byte[] in, int offset) {
        int ret = 0xff;
        ret = (ret << 8) | (in[offset + 2] & 0xff);
        ret = (ret << 8) | (in[offset + 1] & 0xff);
        ret = (ret << 8) | (in[offset + 0] & 0xff);
        return (ret);
    }

    /**
     * Builds a long from a byte array - convert little to big endian.
     * @param in Byte array to convert.
     * @param offset Offset in byte array.
     * @return Big endian long from in[offset]-in[offset+7]
     */
    private long constructLong(byte[] in, int offset) {
        long ret = ((long) in[offset + 7] & 0xff);
        ret |= (ret << 8) | ((long) in[offset + 6] & 0xff);
        ret |= (ret << 8) | ((long) in[offset + 5] & 0xff);
        ret |= (ret << 8) | ((long) in[offset + 4] & 0xff);
        ret |= (ret << 8) | ((long) in[offset + 3] & 0xff);
        ret |= (ret << 8) | ((long) in[offset + 2] & 0xff);
        ret |= (ret << 8) | ((long) in[offset + 1] & 0xff);
        ret |= (ret << 8) | ((long) in[offset + 0] & 0xff);
        return (ret);
    }

    /**
     * Builds an double from a byte array - convert little to big endian.
     * @param in Byte array to convert.
     * @param offset Offset in byte array.
     * @return Big endian double from in[offset]-in[offset+7]
     */
    private double constructDouble(byte[] in, int offset) {
        long ret = constructLong(in, offset);
        return (Double.longBitsToDouble(ret));
    }

    /**
     * Builds an short from a byte array - convert little to big endian.
     * @param in Byte array to convert.
     * @param offset Offset in byte array.
     * @return Big endian short from in[offset]-in[offset+1]
     */
    private short constructShort(byte[] in, int offset) {
        short ret = (short) (in[offset + 1] & 0xff);
        ret = (short) ((ret << 8) | (short) (in[offset + 0] & 0xff));
        return (ret);
    }

    /**
     * Reads in a bitmap header from the byte[] and chops it up into BitemapHeader variables
     * @param data The byte[] to read.
     */
    public final void read(byte[] data){
        final int bflen = 14;
        byte bf[] = new byte[bflen];
        for(int i = 0; i < bf.length; i++) {
            bf[i] = data[i];
        }
        final int bilen = 40;
        byte bi[] = new byte[bilen];
        for(int i = 0; i < bi.length; i++) {
            bi[i] = data[i + 14];
        }

        size = constructInt(bf, 2);
        bisize = constructInt(bi, 2);
        width = constructInt(bi, 4);
        height = constructInt(bi, 8);
        planes = constructShort(bi, 12);
        bitcount = constructShort(bi, 14);
        compression = constructInt(bi, 16);
        sizeimage = (constructInt(bi, 20) == 0 ? size - constructInt(bf,10) : constructInt(bi, 20));
        xpm = constructInt(bi, 24);
        ypm = constructInt(bi, 28);
        clrused = constructInt(bi, 32);
        clrimp = constructInt(bi, 36);

    }
}
