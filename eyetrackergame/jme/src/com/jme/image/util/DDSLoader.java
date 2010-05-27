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

import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import com.jme.image.Image;
import com.jme.image.Image.Format;
import com.jme.util.LittleEndien;
import com.jme.util.geom.BufferUtils;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * <code>DDSLoader</code> is an image loader that reads in a DirectX DDS file.
 * Supports DXT1, DXT3, DXT5, RGB, RGBA, Grayscale, Alpha pixel formats.
 * 2D images, mipmapped 2D images, and cubemaps.
 * 
 * @author Gareth Jenkins-Jones
 * @author Kirill Vainer
 * @version $Id: DDSLoader.java,v 2.0 2008/8/15
 */
public final class DDSLoader {
    
    private static final Logger logger = Logger.getLogger(DDSLoader.class.getName());
    
    private DDSLoader() {
    }

    public static Image loadImage(InputStream fis) throws IOException {
        return loadImage(fis, false);
    }
    
    public static Image loadImage(InputStream fis, boolean flip) throws IOException {
        DDSReader reader = new DDSReader(fis);
        reader.loadHeader();
        ArrayList<ByteBuffer> data = reader.readData(flip);

        return new Image(reader.pixelFormat_, reader.width_, reader.height_, 0, data, reader.sizes_);
    }

    /**
     * DDS reader
     * 
     * @author Gareth
     */
    public static class DDSReader {
        private static final int DDSD_MANDATORY = 0x1007;
        private static final int DDSD_MIPMAPCOUNT = 0x20000;
        private static final int DDSD_LINEARSIZE = 0x80000;
        private static final int DDSD_DEPTH = 0x800000;

        private static final int DDPF_ALPHAPIXELS = 0x1;
        private static final int DDPF_FOURCC = 0x4;
        private static final int DDPF_RGB = 0x40;
        // used by compressonator to mark grayscale images, red channel mask is used for data and bitcount is 8
        private static final int DDPF_GRAYSCALE = 0x20000;
        // used by compressonator to mark alpha images, alpha channel mask is used for data and bitcount is 8 
        private static final int DDPF_ALPHA = 0x2;

        
        private static final int DDSCAPS_COMPLEX = 0x8;
        private static final int DDSCAPS_TEXTURE = 0x1000;
        private static final int DDSCAPS_MIPMAP = 0x400000;

        private static final int DDSCAPS2_CUBEMAP = 0x200;
        private static final int DDSCAPS2_VOLUME = 0x200000;

        private static final int PF_DXT1 = 0x31545844;
        private static final int PF_DXT3 = 0x33545844;
        private static final int PF_DXT5 = 0x35545844;

        private static final double LOG2 = Math.log(2);

        private int width_;
        private int height_;
        private int depth_; // currently unused
        private int flags_;
        private int pitchOrSize_;
        private int mipMapCount_;
        private int caps1_;
        private int caps2_;

        private boolean compressed_;
        private boolean grayscaleOrAlpha_;
        private Image.Format pixelFormat_;
        private int bpp_;
        private int[] sizes_;

        private int redMask_, greenMask_, blueMask_, alphaMask_;

        private DataInput in_;

        public DDSReader(InputStream in) {
            in_ = new LittleEndien(in);
        }

        /**
         * Reads the header (first 128 bytes) of a DDS File
         */
        public void loadHeader() throws IOException {
            if (in_.readInt() != 0x20534444 || in_.readInt() != 124) {
                throw new IOException("Not a DDS file");
            }

            flags_ = in_.readInt();

            if (!is(flags_, DDSD_MANDATORY)) {
                throw new IOException("Mandatory flags missing");
            }
            if (is(flags_, DDSD_DEPTH)) {
                throw new IOException("Depth not supported");
            }

            height_ = in_.readInt();
            width_ = in_.readInt();
            pitchOrSize_ = in_.readInt();
            depth_ = in_.readInt();
            mipMapCount_ = in_.readInt();
            in_.skipBytes(44);
            readPixelFormat();
            caps1_ = in_.readInt();
            caps2_ = in_.readInt();
            in_.skipBytes(12);

            if (!is(caps1_, DDSCAPS_TEXTURE)) {
                throw new IOException("File is not a texture");
            }
            
            if (is(caps2_, DDSCAPS2_VOLUME)) {
                throw new IOException("Volume textures not supported");
            }else{
                depth_ = 0;
            }

            int expectedMipmaps = 1 + (int) Math.ceil(Math.log(Math.max(height_, width_)) / LOG2);

            if (is(caps1_, DDSCAPS_MIPMAP)) {
                if (!is(flags_, DDSD_MIPMAPCOUNT)) {
                    mipMapCount_ = expectedMipmaps;
                } else if (mipMapCount_ != expectedMipmaps) {
                    // changed to warning- images often do not have the required amount,
                    // or specify that they have mipmaps but include only the top level..
                    logger.log(Level.WARNING, "Got {0} mipmaps, expected {1}", new Integer[]{ mipMapCount_, expectedMipmaps});
                }
            } else {
                mipMapCount_ = 1;
            }

            loadSizes();
        }

        /**
         * Reads the PixelFormat structure in a DDS file
         */
        private void readPixelFormat() throws IOException {
            int pfSize = in_.readInt();
            if (pfSize != 32) {
                throw new IOException("Pixel format size is " + pfSize
                        + ", not 32");
            }

            int flags = in_.readInt();

            if (is(flags, DDPF_FOURCC)) {
                compressed_ = true;
                int fourcc = in_.readInt();
                in_.skipBytes(20);

                switch (fourcc) {
                case PF_DXT1:
                    bpp_ = 4;
                    if (is(flags, DDPF_ALPHAPIXELS)) {
                        pixelFormat_ = Image.Format.NativeDXT1A;
                    } else {
                        pixelFormat_ = Image.Format.NativeDXT1;
                    }
                    break;
                case PF_DXT3:
                    bpp_ = 8;
                    pixelFormat_ = Image.Format.NativeDXT3;
                    break;
                case PF_DXT5:
                    bpp_ = 8;
                    pixelFormat_ = Image.Format.NativeDXT5;
                    break;
                default:
                    throw new IOException("Unknown fourcc: " + string(fourcc));
                }

                int size = ((width_ + 3) / 4) * ((height_ + 3) / 4) * bpp_ * 2;
                
                if (is(flags_, DDSD_LINEARSIZE)) {
                    if (pitchOrSize_ == 0) {
                        logger.warning("Must use linear size with fourcc");
                        pitchOrSize_ = size;
                    } else if (pitchOrSize_ != size) {
                        logger.log(Level.WARNING, "Expected size = {0}, real = {1}", new Integer[] {size, pitchOrSize_});
                }
            } else {
                    pitchOrSize_ = size;
                }
            } else {
                compressed_ = false;

                // skip fourCC
                in_.readInt();

                bpp_ = in_.readInt();
                redMask_ = in_.readInt();
                greenMask_ = in_.readInt();
                blueMask_ = in_.readInt();
                alphaMask_ = in_.readInt();

                if (is(flags, DDPF_RGB)){
                    if (is(flags, DDPF_ALPHAPIXELS)){
                        pixelFormat_ = Format.RGBA8;
                    }else{
                        pixelFormat_ = Format.RGB8;
            }
                }else if (is(flags, DDPF_GRAYSCALE)){
                    switch (bpp_){
                        case 4:  pixelFormat_ = Format.Luminance4; break;
                        case 8:  pixelFormat_ = Format.Luminance8; break;
                        case 12: pixelFormat_ = Format.Luminance12; break;
                        case 16: pixelFormat_ = Format.Luminance16; break;
                        default: throw new IOException("Unsupported Grayscale BPP: "+bpp_);
        }
                    grayscaleOrAlpha_ = true;
                }else if (is(flags, DDPF_ALPHA)){
                    switch (bpp_){
                        case 4:  pixelFormat_ = Format.Alpha4; break;
                        case 8:  pixelFormat_ = Format.Alpha8; break;
                        case 12: pixelFormat_ = Format.Alpha12; break;
                        case 16: pixelFormat_ = Format.Alpha16; break;
                        default: throw new IOException("Unsupported Alpha BPP: "+bpp_);
                    }
                    grayscaleOrAlpha_ = true;
                }else{
                    throw new IOException("Unknown PixelFormat in DDS file");
                }

                int size = (bpp_ / 8 * width_);
                
                if (is(flags_, DDSD_LINEARSIZE)) {
                    if (pitchOrSize_ == 0) {
                        logger.warning("Linear size said to contain valid value but does not");
                        pitchOrSize_ = size;
                    } else if (pitchOrSize_ != size) {
                        logger.log(Level.WARNING, "Expected size = {0}, real = {1}", new Integer[] {size, pitchOrSize_});
                    }
                } else {
                    pitchOrSize_ = size;
                }
            }
        }

        /**
         * Computes the sizes of each mipmap level in bytes, and stores it in sizes_[].
         */
        private void loadSizes() {
            int width = width_;
            int height = height_;

            sizes_ = new int[mipMapCount_];

            for (int i = 0; i < mipMapCount_; i++) {
                int size;

                if (compressed_) {
                    size = ((width + 3) / 4) * ((height + 3) / 4) * bpp_ * 2;
                } else {
                    size = width * height * bpp_ / 8;
                }

                sizes_[i] = ((size + 3) / 4) * 4;

                width = Math.max(width / 2, 1);
                height = Math.max(height / 2, 1);
            }
        }

        /**
         * Flips the given image data on the Y axis.
         * @param data Data array containing image data (without mipmaps)
         * @param scanlineSize Size of a single scanline = width * bytesPerPixel
         * @param height Height of the image in pixels
         * @return The new data flipped by the Y axis
         */
        public byte[] flipData(byte[] data, int scanlineSize, int height){
            byte[] newData = new byte[data.length];

            for (int y = 0; y < height; y++){
                System.arraycopy(data, y * scanlineSize, 
                                 newData, (height-y-1) * scanlineSize, 
                                 scanlineSize);
            }

            return newData;
        }
        
        /**
         * Reads a grayscale image with mipmaps from the InputStream
         * @param flip Flip the loaded image by Y axis
         * @param totalSize Total size of the image in bytes including the mipmaps
         * @return A ByteBuffer containing the grayscale image data with mips.
         * @throws java.io.IOException If an error occured while reading from InputStream
         */
        public ByteBuffer readGrayscale2D(boolean flip, int totalSize) throws IOException{
            ByteBuffer buffer = BufferUtils.createByteBuffer(totalSize);
            
            if (bpp_ == 8)
                logger.finest("Source image format: R8");
            
            assert bpp_ / 8 == Image.getEstimatedByteSize(pixelFormat_);
            
            int width = width_;
            int height = height_;
            
            for (int mip = 0; mip < mipMapCount_; mip++){
                byte[] data = new byte[sizes_[mip]];
                in_.readFully(data);
                if (flip) data = flipData(data, width * bpp_ / 8, height);
                buffer.put(data);
                
                width = Math.max(width / 2, 1);
                height = Math.max(height / 2, 1);
            }

            return buffer;
        }
        
        /**
         * Reads an uncompressed RGB or RGBA image.
         * 
         * @param flip Flip the image on the Y axis
         * @param totalSize Size of the image in bytes including mipmaps
         * @return ByteBuffer containing image data with mipmaps in the format specified by pixelFormat_
         * @throws java.io.IOException If an error occured while reading from InputStream
         */
        public ByteBuffer readRGB2D(boolean flip, int totalSize) throws IOException{
            int redCount = count(redMask_),
                blueCount = count(blueMask_),
                greenCount = count(greenMask_),
                alphaCount = count(alphaMask_);
            
            if (redMask_     == 0x00FF0000
             && greenMask_  == 0x0000FF00
             && blueMask_   == 0x000000FF){
                if (alphaMask_ == 0xFF000000 && bpp_ == 32){
                    logger.finest("Data source format: BGRA8");
                }else if (bpp_ == 24){
                    logger.finest("Data source format: BGR8");
                }
            }
            
            int sourcebytesPP = bpp_ / 8;
            int targetBytesPP = Image.getEstimatedByteSize(pixelFormat_);
  
            ByteBuffer dataBuffer = BufferUtils.createByteBuffer(totalSize);
            
            int width = width_;
            int height = height_;
            
            int offset = 0;
            for (int mip = 0; mip < mipMapCount_; mip++){
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        byte[] b = new byte[sourcebytesPP];
                        in_.readFully(b);

                        int i = byte2int(b);
                        
                        byte red = (byte) (((i & redMask_) >> redCount));
                        byte green = (byte) (((i & greenMask_) >> greenCount));
                        byte blue = (byte) (((i & blueMask_) >> blueCount));
                        byte alpha = (byte) (((i & alphaMask_) >> alphaCount));
                        
                        if (flip)
                            dataBuffer.position(offset + ((height-y-1) * width + x) * targetBytesPP);
                        //else
                        //    dataBuffer.position(offset + (y * width + x) * targetBytesPP);
                            
                        if (alphaMask_ == 0){
                            dataBuffer.put(red).put(green).put(blue);
                        }else{
                            dataBuffer.put(red).put(green).put(blue).put(alpha);
                        }
                    }
                }
                
                offset += width * height * targetBytesPP;
                
                width = Math.max(width / 2, 1);
                height = Math.max(height / 2, 1);
            }
            
            return dataBuffer;
        }
        
        /**
         * Reads a DXT compressed image from the InputStream
         * 
         * @param totalSize Total size of the image in bytes, including mipmaps
         * @return ByteBuffer containing compressed DXT image in the format specified by pixelFormat_
         * @throws java.io.IOException If an error occured while reading from InputStream
         */
        public ByteBuffer readDXT2D(int totalSize) throws IOException{
            byte[] data = new byte[totalSize];
            in_.readFully(data);

            logger.finest("Source image format: DXT");
            
            ByteBuffer buffer = BufferUtils.createByteBuffer(totalSize);
            buffer.put(data);
            buffer.rewind();

            return buffer;
        }

        /**
         * Reads the image data from the InputStream in the required format.
         * If the file contains a cubemap image, it is loaded as 6 ByteBuffers
         * (potentially containing mipmaps if they were specified), otherwise
         * a single ByteBuffer is returned for a 2D image.
         * 
         * @param flip Flip the image data or not. 
         *        For cubemaps, each of the cubemap faces is flipped individually. 
         *        If the image is DXT compressed, no flipping is done.
         * @return An ArrayList containing a single ByteBuffer for a 2D image, or 6 ByteBuffers for a cubemap.
         *         The cubemap ByteBuffer order is PositiveX, NegativeX, PositiveY, NegativeY, PositiveZ, NegativeZ.
         * 
         * @throws java.io.IOException If an error occured while reading from the stream.
         */
        public ArrayList<ByteBuffer> readData(boolean flip) throws IOException {
            int totalSize = 0;

            for (int i = 0; i < sizes_.length; i++) {
                totalSize += sizes_[i];
            }

            ArrayList<ByteBuffer> allMaps = new ArrayList<ByteBuffer>();
            if (is(caps2_, DDSCAPS2_CUBEMAP)) {
                for (int i = 0; i < 6; i++){
                    if (compressed_){
                        allMaps.add( readDXT2D(totalSize));
                    }else if (grayscaleOrAlpha_){
                        allMaps.add( readGrayscale2D(flip, totalSize));
                    }else{
                        allMaps.add( readRGB2D(flip, totalSize));
                    }
                }
            }else{
                if (compressed_){
                    allMaps.add( readDXT2D(totalSize));
                }else if (grayscaleOrAlpha_){
                    allMaps.add( readGrayscale2D(flip, totalSize));
                }else{
                    allMaps.add( readRGB2D(flip, totalSize));
                }
            }
            
            return allMaps;
        }

        /**
         * Checks if flags contains the specified mask
         */
        private static final boolean is(int flags, int mask) {
            return (flags & mask) == mask;
        }

        /**
         * Counts the amount of bits needed to shift till bitmask n is at zero
         * @param n Bitmask to test
         */
        private static int count(int n) {
            if (n == 0)
                return 0;
            
            int i = 0;
            while ((n & 0x1) == 0) {
                n = n >> 1;
                i++;
                if (i > 32)
                    throw new RuntimeException(Integer.toHexString(n));
            }

            return i;
        }
        
        /**
         * Converts a 1 to 4 sized byte array to an integer
         */
        private static int byte2int(byte[] b){
            if (b.length == 1)
                return b[0] & 0xFF;
            else if (b.length == 2)
                return (b[0] & 0xFF)
                     | ((b[1] & 0xFF) << 8);
            else if (b.length == 3)
                return (b[0] & 0xFF)
                     | ((b[1] & 0xFF) << 8)
                     | ((b[2] & 0xFF) << 16);
            else if (b.length == 4)
                return (b[0] & 0xFF)
                     | ((b[1] & 0xFF) << 8)
                     | ((b[2] & 0xFF) << 16)
                     | ((b[3] & 0xFF) << 24);
            else
                return 0;
        }

        /**
         * Converts a int representing a FourCC into a String
         */
        private static final String string(int value) {
            StringBuffer buf = new StringBuffer();

            buf.append((char) (value & 0xFF));
            buf.append((char) ((value & 0xFF00) >> 8));
            buf.append((char) ((value & 0xFF0000) >> 16));
            buf.append((char) ((value & 0xFF00000) >> 24));

            return buf.toString();
        }
    }

}
