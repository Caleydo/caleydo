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

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * <code>Image</code> defines a data format for a graphical image. The image
 * is defined by a format, a height and width, and the image data. The width and
 * height must be greater than 0. The data is contained in a byte buffer, and
 * should be packed before creation of the image object.
 * 
 * @author Mark Powell
 * @author Joshua Slack
 * @version $Id: Image.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public class Image implements Serializable, Savable {

    private static final long serialVersionUID = 2L;

    public enum Format {
        /**
         * When used in texture loading, this indicates to let jME guess the
         * format. jME will use S3TC compression, if available.
         */
        Guess,
        /**
         * When used in texture loading, this indicates to let jME guess the
         * format, but not to use S3TC compression, even if available.
         */
        GuessNoCompression,
        /**
         * 4 bit alpha only format - usually forced to 8bit by the card
         */
        Alpha4,
        /**
         * 8 bit alpha only format
         */
        Alpha8,
        /**
         * 12 bit alpha only format - often forced to 8bit or 16bit by the card
         */
        Alpha12,
        /**
         * 16 bit alpha only format - older cards will often use 8bit instead.
         */
        Alpha16,
        /**
         * 4 bit luminance only format - usually forced to 8bit by the card
         */
        Luminance4,
        /**
         * 8 bit luminance only format
         */
        Luminance8,
        /**
         * 12 bit luminance only format - often forced to 8bit or 16bit by the
         * card
         */
        Luminance12,
        /**
         * 16 bit luminance only format - older cards will often use 8bit
         * instead.
         */
        Luminance16,
        /**
         * 4 bit luminance, 4 bit alpha format
         */
        Luminance4Alpha4,
        /**
         * 6 bit luminance, 2 bit alpha format
         */
        Luminance6Alpha2,
        /**
         * 8 bit luminance, 8 bit alpha format
         */
        Luminance8Alpha8,
        /**
         * 12 bit luminance, 4 bit alpha format
         */
        Luminance12Alpha4,
        /**
         * 12 bit luminance, 12 bit alpha format
         */
        Luminance12Alpha12,
        /**
         * 16 bit luminance, 16 bit alpha format
         */
        Luminance16Alpha16,
        /**
         * 4 bit intensity only format - usually forced to 8bit by the card
         */
        Intensity4,
        /**
         * 8 bit intensity only format
         */
        Intensity8,
        /**
         * 12 bit intensity only format - often forced to 8bit or 16bit by the
         * card
         */
        Intensity12,
        /**
         * 16 bit intensity only format - older cards will often use 8bit
         * instead.
         */
        Intensity16,
        /**
         * 3 bit red, 3 bit green, 3 bit blue - often forced to 16 bit by the
         * card
         */
        R3G3B2,
        /**
         * 4 bits per red, green and blue
         */
        RGB4,
        /**
         * 5 bits per red, green and blue
         */
        RGB5,
        /**
         * 8 bits per red, green and blue
         */
        RGB8,
        /**
         * 10 bits per red, green and blue - usually falls back to 8 bits on the
         * card
         */
        RGB10,
        /**
         * 12 bits per red, green and blue - usually falls back to 8 bits on the
         * card
         */
        RGB12,
        /**
         * 16 bits per red, green and blue - usually falls back to 8 bits on the
         * card
         */
        RGB16,
        /**
         * 2 bits per red, green, blue and alpha - often forced to RGBA4 by the
         * card
         */
        RGBA2,
        /**
         * 4 bits per red, green, blue and alpha
         */
        RGBA4,
        /**
         * 5 bits per red, green and blue. 1 bit of alpha
         */
        RGB5A1,
        /**
         * 8 bits per red, green, blue and alpha
         */
        RGBA8,
        /**
         * 10 bits per red, green and blue. 2 bits of alpha - often forced to
         * RGBA8 by the card
         */
        RGB10A2,
        /**
         * 12 bits per red, green, blue and alpha - often forced to RGBA8 by the
         * card
         */
        RGBA12,
        /**
         * 16 bits per red, green, blue and alpha - often forced to RGBA8 by the
         * card
         */
        RGBA16,
        /**
         * 8 bits per red, green and blue. Compressed and stored by the card in
         * DXT1 format.
         */
        RGB_TO_DXT1,
        /**
         * 8 bits per red, green, blue and alpha. Compressed and stored by the
         * card in DXT1 format.
         */
        RGBA_TO_DXT1,
        /**
         * 8 bits per red, green, blue and alpha. Compressed and stored by the
         * card in DXT3 format.
         */
        RGBA_TO_DXT3,
        /**
         * 8 bits per red, green, blue and alpha. Compressed and stored by the
         * card in DXT5 format.
         */
        RGBA_TO_DXT5,
        /**
         * Image data already in DXT1 format.
         */
        NativeDXT1,
        /**
         * Image data already in DXT1 (with Alpha) format.
         */
        NativeDXT1A,
        /**
         * Image data already in DXT3 format.
         */
        NativeDXT3,
        /**
         * Image data already in DXT5 format.
         */
        NativeDXT5,
        /**
         * 16 bit depth component format
         */
        Depth16,
        /**
         * 24 bit depth component format
         */
        Depth24,
        /**
         * 32 bit depth component format - often stored in Depth24 format by the
         * card.
         */
        Depth32,
        /**
         * 16 bit float per red, green and blue
         */
        RGB16F,
        /**
         * 32 bit float per red, green and blue
         */
        RGB32F,
        /**
         * 16 bit float per red, green, blue and alpha
         */
        RGBA16F,
        /**
         * 32 bit float per red, green, blue and alpha
         */
        RGBA32F,
        /**
         * 16 bit float, alpha only format
         */
        Alpha16F,
        /**
         * 16 bit float, alpha only format
         */
        Alpha32F,
        /**
         * 16 bit float, luminance only format
         */
        Luminance16F,
        /**
         * 32 bit float, luminance only format
         */
        Luminance32F,
        /**
         * 16 bit float per luminance and alpha
         */
        LuminanceAlpha16F,
        /**
         * 32 bit float per luminance and alpha
         */
        LuminanceAlpha32F,
        /**
         * 16 bit float, intensity only format
         */
        Intensity16F,
        /**
         * 32 bit float, intensity only format
         */
        Intensity32F;
    }

    // image attributes
    protected Format format;
    protected int width, height, depth;
    protected int[] mipMapSizes;
    protected transient ArrayList<ByteBuffer> data;

    /**
     * Constructor instantiates a new <code>Image</code> object. All values
     * are undefined.
     */
    public Image() {
        data = new ArrayList<ByteBuffer>(1);
    }

    /**
     * Constructor instantiates a new <code>Image</code> object. The
     * attributes of the image are defined during construction.
     * 
     * @param format
     *            the data format of the image.
     * @param width
     *            the width of the image.
     * @param height
     *            the height of the image.
     * @param data
     *            the image data.
     * @param mipMapSizes
     *            the array of mipmap sizes, or null for no mipmaps.
     */
    public Image(Format format, int width, int height, int depth, ArrayList<ByteBuffer> data,
            int[] mipMapSizes) {

        if (mipMapSizes != null && mipMapSizes.length <= 1) {
            mipMapSizes = null;
        }

        setFormat(format);
        this.width = width;
        this.height = height;
        this.data = data;
        this.depth = depth;
        this.mipMapSizes = mipMapSizes;
    }

    /**
     * Constructor instantiates a new <code>Image</code> object. The
     * attributes of the image are defined during construction.
     * 
     * @param format
     *            the data format of the image.
     * @param width
     *            the width of the image.
     * @param height
     *            the height of the image.
     * @param data
     *            the image data.
     * @param mipMapSizes
     *            the array of mipmap sizes, or null for no mipmaps.
     */
    public Image(Format format, int width, int height, ByteBuffer data,
            int[] mipMapSizes) {

        if (mipMapSizes != null && mipMapSizes.length <= 1) {
            mipMapSizes = null;
        }

        setFormat(format);
        this.width = width;
        this.height = height;
        this.data = new ArrayList<ByteBuffer>(1);
        this.data.add(data);
        this.mipMapSizes = mipMapSizes;
    }

    /**
     * Constructor instantiates a new <code>Image</code> object. The
     * attributes of the image are defined during construction.
     * 
     * @param type
     *            the data format of the image.
     * @param width
     *            the width of the image.
     * @param height
     *            the height of the image.
     * @param data
     *            the image data.
     */
    public Image(Format format, int width, int height, int depth, ArrayList<ByteBuffer> data) {
        this(format, width, height, depth, data, null);
    }

    /**
     * Constructor instantiates a new <code>Image</code> object. The
     * attributes of the image are defined during construction.
     * 
     * @param type
     *            the data format of the image.
     * @param width
     *            the width of the image.
     * @param height
     *            the height of the image.
     * @param data
     *            the image data.
     */
    public Image(Format format, int width, int height, ByteBuffer data) {
        this(format, width, height, data, null);
    }

    /**
     * <code>setData</code> sets the data that makes up the image. This data
     * is packed into an array of <code>ByteBuffer</code> objects.
     * 
     * @param data
     *            the data that contains the image information.
     */
    public void setData(ArrayList<ByteBuffer> data) {
        this.data = data;
    }

    /**
     * <code>setData</code> sets the data that makes up the image. This data
     * is packed into a single <code>ByteBuffer</code>.
     * 
     * @param data
     *            the data that contains the image information.
     */
    public void setData(ByteBuffer data) {
        this.data = new ArrayList<ByteBuffer>(1);
        this.data.add(data);
    }

    public void addData(ByteBuffer data) {
        if (this.data == null)
            this.data = new ArrayList<ByteBuffer>(1);
        this.data.add(data);
    }

    public void setData(int index, ByteBuffer data) {
        if (index >= 0) {
            while (this.data.size() <= index) {
                this.data.add(null);
            }
            this.data.set(index, data);
        } else {
            throw new IllegalArgumentException("index must be greater than or equal to 0.");
        }
    }

    /**
     * Sets the mipmap sizes stored in this image's data buffer. Mipmaps are
     * stored sequentially, and the first mipmap is the main image data. To
     * specify no mipmaps, pass null and this will automatically be expanded
     * into a single mipmap of the full
     * 
     * @param mipMapSizes
     *            the mipmap sizes array, or null for a single image map.
     */
    public void setMipMapSizes(int[] mipMapSizes) {
        if (mipMapSizes != null && mipMapSizes.length <= 1)
            mipMapSizes = null;

        this.mipMapSizes = mipMapSizes;
    }

    /**
     * <code>setHeight</code> sets the height value of the image. It is
     * typically a good idea to try to keep this as a multiple of 2.
     * 
     * @param height
     *            the height of the image.
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * <code>setDepth</code> sets the depth value of the image. It is
     * typically a good idea to try to keep this as a multiple of 2. This is
     * used for 3d images.
     * 
     * @param depth
     *            the depth of the image.
     */
    public void setDepth(int depth) {
        this.depth = depth;
    }

    /**
     * <code>setWidth</code> sets the width value of the image. It is
     * typically a good idea to try to keep this as a multiple of 2.
     * 
     * @param width
     *            the width of the image.
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * <code>setFormat</code> sets the image format for this image.
     * 
     * @param format
     *            the image format.
     * @throws NullPointerException
     *             if format is null
     * @see Format
     */
    public void setFormat(Format format) {
        if (format == null) {
            throw new NullPointerException("format may not be null.");
        }

        this.format = format;
    }

    /**
     * <code>getFormat</code> returns the image format for this image.
     * 
     * @return the image format.
     * @see Format
     */
    public Format getFormat() {
        return format;
    }

    /**
     * <code>getWidth</code> returns the width of this image.
     * 
     * @return the width of this image.
     */
    public int getWidth() {
        return width;
    }

    /**
     * <code>getHeight</code> returns the height of this image.
     * 
     * @return the height of this image.
     */
    public int getHeight() {
        return height;
    }

    /**
     * <code>getDepth</code> returns the depth of this image (for 3d images).
     * 
     * @return the depth of this image.
     */
    public int getDepth() {
        return depth;
    }

    /**
     * <code>getData</code> returns the data for this image. If the data is
     * undefined, null will be returned.
     * 
     * @return the data for this image.
     */
    public ArrayList<ByteBuffer> getData() {
        return data;
    }

    /**
     * <code>getData</code> returns the data for this image. If the data is
     * undefined, null will be returned.
     * 
     * @return the data for this image.
     */
    public ByteBuffer getData(int index) {
        if (data.size() > index)
            return data.get(index);
        else 
            return null;
    }

    /**
     * Returns whether the image data contains mipmaps.
     * 
     * @return true if the image data contains mipmaps, false if not.
     */
    public boolean hasMipmaps() {
        return mipMapSizes != null;
    }

    /**
     * Returns the mipmap sizes for this image.
     * 
     * @return the mipmap sizes for this image.
     */
    public int[] getMipMapSizes() {
        return mipMapSizes;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Image)) {
            return false;
        }
        Image that = (Image) other;
        if (this.getFormat() != that.getFormat())
            return false;
        if (this.getWidth() != that.getWidth())
            return false;
        if (this.getHeight() != that.getHeight())
            return false;
        if (this.getData() != null && !this.getData().equals(that.getData()))
            return false;
        if (this.getData() == null && that.getData() != null)
            return false;
        if (this.getMipMapSizes() != null
                && !Arrays.equals(this.getMipMapSizes(), that.getMipMapSizes()))
            return false;
        if (this.getMipMapSizes() == null && that.getMipMapSizes() != null)
            return false;

        return true;
    }

    public void write(JMEExporter e) throws IOException {
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(format, "format", Format.RGBA8);
        capsule.write(width, "width", 0);
        capsule.write(height, "height", 0);
        capsule.write(depth, "depth", 0);
        capsule.write(mipMapSizes, "mipMapSizes", null);
        capsule.writeByteBufferArrayList(data, "data", null);
    }

    public void read(JMEImporter e) throws IOException {
        InputCapsule capsule = e.getCapsule(this);
        format = capsule.readEnum("format", Format.class, Format.RGBA8);
        width = capsule.readInt("width", 0);
        height = capsule.readInt("height", 0);
        depth = capsule.readInt("depth", 0);
        mipMapSizes = capsule.readIntArray("mipMapSizes", null);
        data = capsule.readByteBufferArrayList("data", null);
    }

    public Class getClassTag() {
        return this.getClass();
    }

    /**
     * @param format
     *            image data format
     * @return an estimate of the size of a texture on the video card of an
     *         image in the given format. Mostly this is just a guess and could
     *         probably be cleaned up a lot.
     */
    public static int getEstimatedByteSize(Format format) {
        switch (format) {
            case RGBA2:
            case Alpha4:
            case Luminance4:
            case Intensity4:

            case Alpha8:
            case Luminance8:
            case Intensity8:
            case Luminance4Alpha4:
            case Luminance6Alpha2:

            case RGBA_TO_DXT1: // DXT1 = 1/8 * blocksize of 8 (.125 * 8 == 1)
            case NativeDXT1A:
            case RGB_TO_DXT1:
            case NativeDXT1:
                return 1;

            case R3G3B2: // (usually forced to 2 byte by the card)
            case RGB4:
            case RGB5:
            case RGBA4:
            case RGB5A1:

            case Alpha12:
            case Luminance12:
            case Intensity12:

            case Alpha16:
            case Alpha16F:
            case Luminance16:
            case Luminance16F:
            case Intensity16:
            case Intensity16F:
            case Luminance8Alpha8:
            case Luminance12Alpha4:

            case RGBA_TO_DXT3: // DXT3,5 = 1/8 * blocksize of 16 (.125 * 16 == 2)
            case NativeDXT3:
            case RGBA_TO_DXT5:
            case NativeDXT5:
            case Depth16:
                return 2;
                
            case RGB8:
            case Luminance12Alpha12:
            case Depth24:
                return 3;
                
            case RGBA8:
            case RGB10A2:
            case RGB10:
            case Alpha32F:
            case Luminance32F:
            case Intensity32F:
            case Luminance16Alpha16:
            case LuminanceAlpha16F:
            case Depth32:
                return 4;
                
            case RGB12:
                return 5;
                
            case RGBA12:
            case RGB16:
            case RGB16F:
                return 6;
                
            case RGBA16:
            case RGBA16F:
            case LuminanceAlpha32F:
                return 8;
                
            case RGB32F:
                return 12;
                
            case RGBA32F:
                return 16;
        }
        throw new IllegalArgumentException("unknown image format type: "+format);
    }
}
