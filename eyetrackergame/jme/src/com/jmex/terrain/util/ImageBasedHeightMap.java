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

package com.jmex.terrain.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.awt.image.WritableRaster;

import com.jme.util.TextureManager;

/**
 * <code>ImageBasedHeightMap</code> is a height map created from the grayscale
 * conversion of an image. The image used currently must have an equal height
 * and width, although future work could scale an incoming image to a specific
 * height and width.
 * 
 * @author Mike Kienenberger
 * @version $Revision: 4743 $, $Date: 2009-11-01 16:57:44 +0100 (So, 01 Nov 2009) $
 */
public class ImageBasedHeightMap extends AbstractHeightMap {

    static private class ImageConverter {

        // Posted by DrLaszloJamf to Java Technology Forums
        //
        // Copyright 1994-2004 Sun Microsystems, Inc. All Rights Reserved.
        // Redistribution and use in source and binary forms, with or without
        // modification, are permitted provided that the following conditions
        // are met:
        //
        // Redistribution of source code must retain the above copyright notice,
        // this list of conditions and the following disclaimer.
        //
        // Redistribution in binary form must reproduce the above copyright
        // notice, this list of conditions and the following disclaimer in the
        // documentation and/or other materials provided with the distribution.
        //
        // Neither the name of Sun Microsystems, Inc. or the names of
        // contributors may be used to endorse or promote products derived from
        // this software without specific prior written permission.
        //
        // This software is provided "AS IS," without a warranty of any kind.
        // ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
        // INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
        // PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN
        // MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL NOT BE LIABLE FOR
        // ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
        // DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN
        // OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
        // FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE
        // DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY,
        // ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF
        // SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
        //
        //
        // You acknowledge that this software is not designed, licensed or
        // intended for use in the design, construction, operation or
        // maintenance of any nuclear facility.

        //    preserves image's colormodel. Assumes image is loaded
        public static BufferedImage toBufferedImage(Image image) {
            if (image instanceof BufferedImage) return (BufferedImage) image;
            ColorModel cm = getColorModel(image);
            int width = image.getWidth(null);
            int height = image.getHeight(null);
            return copy(createBufferedImage(cm, width, height), image);
        }

        public static BufferedImage toBufferedImage(Image image, int type) {
            if (image instanceof BufferedImage
                    && ((BufferedImage) image).getType() == type)
                    return (BufferedImage) image;
            int width = image.getWidth(null);
            int height = image.getHeight(null);
            return copy(new BufferedImage(width, height, type), image);
        }

        //    Returns target. Assumes source is loaded
        public static BufferedImage copy(BufferedImage target, Image source) {
            Graphics2D g = target.createGraphics();
            g.drawImage(source, 0, 0, null);
            g.dispose();
            return target;
        }

        public static ColorModel getColorModel(Image image) {
            try {
                PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
                pg.grabPixels();
                return pg.getColorModel();
            } catch (InterruptedException e) {
                throw new RuntimeException("Unexpected interruption", e);
            }
        }

        public static BufferedImage createBufferedImage(ColorModel cm, int w,
                int h) {
            WritableRaster raster = cm.createCompatibleWritableRaster(w, h);
            boolean isRasterPremultiplied = cm.isAlphaPremultiplied();
            return new BufferedImage(cm, raster, isRasterPremultiplied, null);
        }
    }

    /**
     * Creates a HeightMap from an Image. The image will be converted to
     * grayscale, and the grayscale values will be used to generate the height
     * map. White is highest point while black is lowest point.
     * 
     * Currently, the Image used must be square (width == height), but future
     * work could rescale the image.
     * 
     * @param colorImage
     *            Image to map to the height map.
     */
    public ImageBasedHeightMap(Image colorImage) {
        super();

        // FUTURE: Rescale image if not square?
        BufferedImage colorBufferedImage = ImageConverter.toBufferedImage(
                colorImage, BufferedImage.TYPE_3BYTE_BGR);

        boolean hasAlpha = TextureManager.hasAlpha(colorBufferedImage);

        int imageWidth = colorBufferedImage.getWidth();
        int imageHeight = colorBufferedImage.getHeight();

        if (imageWidth != imageHeight)
                throw new RuntimeException("imageWidth: " + imageWidth
                        + " != imageHeight: " + imageHeight);

        size = imageWidth;

        byte data[] = (byte[]) colorBufferedImage.getRaster().getDataElements(
                0, 0, imageWidth, imageHeight, null);

        int bytesPerPixel = 3;
        int blueBase = 0;
        if (hasAlpha) {
            bytesPerPixel = 4;
            blueBase = 1;
        }

        heightData = new float[(imageWidth * imageHeight)];

        int index = 0;
        for (int h = 0; h < imageHeight; ++h)
            for (int w = 0; w < imageWidth; ++w) {
                int baseIndex = (h * imageWidth * bytesPerPixel)
                        + (w * bytesPerPixel) + blueBase;
                int blue = data[baseIndex] >= 0 ? data[baseIndex]
                        : (256 + (data[baseIndex]));
                int green = data[baseIndex + 1] >= 0 ? data[baseIndex + 1]
                        : (256 + (data[baseIndex + 1]));
                int red = data[baseIndex + 2] >= 0 ? data[baseIndex + 2]
                        : (256 + (data[baseIndex + 2]));

                int grayscale = (int) (0.299 * red + 0.587 * green + 0.114 * blue);

                heightData[index++] = grayscale;
            }
    }

    /*
     * This does nothing because heightData is loaded when this class is created.
     * 
     * @see com.jmex.terrain.util.AbstractHeightMap#load()
     */
    public boolean load() {
        return true;
    }
}
