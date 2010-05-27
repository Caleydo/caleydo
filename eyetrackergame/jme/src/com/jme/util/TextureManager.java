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

package com.jme.util;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.FileCacheImageInputStream;

import com.jme.image.BitmapHeader;
import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.image.Texture2D;
import com.jme.image.TextureCubeMap;
import com.jme.image.util.DDSLoader;
import com.jme.image.util.TGALoader;
import com.jme.renderer.Renderer;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.export.Savable;
import com.jme.util.export.binary.BinaryImporter;
import com.jme.util.geom.BufferUtils;
import com.jme.util.resource.ResourceLocatorTool;

/**
 * <code>TextureManager</code> provides static methods for building a
 * <code>Texture</code> object. Typically, the information supplied is the
 * filename and the texture properties.
 * 
 * @author Mark Powell
 * @author Joshua Slack -- cache code and enhancements
 * @version $Id: TextureManager.java 4752 2009-11-09 19:08:29Z blaine.dev $
 */
final public class TextureManager {
    private static final Logger logger = Logger.getLogger(TextureManager.class
            .getName());

    private static HashMap<TextureKey, Texture> m_tCache = new HashMap<TextureKey, Texture>();
    private static HashMap<String, ImageLoader> loaders = new HashMap<String, ImageLoader>();
    private static ArrayList<Integer> cleanupStore = new ArrayList<Integer>();

    public static boolean COMPRESS_BY_DEFAULT = true;

    public static Texture.MagnificationFilter DEFAULT_MAG_FILTER = Texture.MagnificationFilter.Bilinear;

    public static Texture.MinificationFilter DEFAULT_MIN_FILTER = Texture.MinificationFilter.BilinearNoMipMaps;

    public static float DEFAULT_ANISO_LEVEL = 0.0f;
    
    private static boolean createOnHeap = false;

    private TextureManager() {
    }

    /**
     * <code>loadTexture</code> loads a new texture defined by the parameter
     * string. Filter parameters are used to define the filtering of the
     * texture. If there is an error loading the file, null is returned.
     * 
     * @param file
     *            the filename of the texture image.
     * @param minFilter
     *            the filter for the near values.
     * @param magFilter
     *            the filter for the far values.
     * @return the loaded texture. If there is a problem loading the texture,
     *         null is returned.
     */
    public static com.jme.image.Texture loadTexture(String file,
            Texture.MinificationFilter minFilter,
            Texture.MagnificationFilter magFilter) {
        return loadTexture(file, minFilter, magFilter, DEFAULT_ANISO_LEVEL,
                true);
    }

    /**
     * <code>loadTexture</code> loads a new texture defined by the parameter
     * string. Filter parameters are used to define the filtering of the
     * texture. If there is an error loading the file, null is returned.
     * 
     * @param file
     *            the filename of the texture image.
     * @param minFilter
     *            the filter for the near values.
     * @param magFilter
     *            the filter for the far values.
     * @param anisoLevel
     *            the aniso level for this texture
     * @param flipped
     *            If true, the images Y values are flipped.
     * @return the loaded texture. If there is a problem loading the texture,
     *         null is returned.
     */
    public static com.jme.image.Texture loadTexture(String file,
            Texture.MinificationFilter minFilter,
            Texture.MagnificationFilter magFilter, float anisoLevel,
            boolean flipped) {
        return loadTexture(file, minFilter, magFilter,
                (COMPRESS_BY_DEFAULT ? Image.Format.Guess
                        : Image.Format.GuessNoCompression), anisoLevel, flipped);
    }

    /**
     * <code>loadTexture</code> loads a new texture defined by the parameter
     * string. Filter parameters are used to define the filtering of the
     * texture. If there is an error loading the file, null is returned.
     * 
     * @param file
     *            the filename of the texture image.
     * @param minFilter
     *            the filter for the near values.
     * @param magFilter
     *            the filter for the far values.
     * @param imageType
     *            the type to use for image data
     * @param anisoLevel
     *            the aniso level for this texture
     * @param flipped
     *            If true, the images Y values are flipped.
     * @return the loaded texture. If there is a problem loading the texture,
     *         null is returned.
     */
    public static com.jme.image.Texture loadTexture(String file,
            Texture.MinificationFilter minFilter,
            Texture.MagnificationFilter magFilter, Image.Format imageType,
            float anisoLevel, boolean flipped) {
        URL url = getTextureURL(file);
        return loadTexture(url, minFilter, magFilter, imageType, anisoLevel,
                flipped);
    }

    /**
     * Convert the provided String file name into a Texture URL, first
     * attempting to use the {@link ResourceLocatorTool}, then trying to load
     * it as a direct file path.
     * 
     * @param file
     *            the file name
     * @return a URL
     */
    private static URL getTextureURL(String file) {
        URL url = ResourceLocatorTool.locateResource(
                ResourceLocatorTool.TYPE_TEXTURE, file);
        if (url == null) {
            try {
                url = new URL("file:" + file);
            } catch (MalformedURLException e) {
                logger.logp(Level.SEVERE, TextureManager.class.toString(),
                        "getTextureURL(file)", "Exception", e);
            }
        }
        return url;
    }

    /**
     * <code>loadTexture</code> loads a new texture defined by the parameter
     * url. If there is an error loading the file, null is returned.
     * 
     * @param file
     *            the url of the texture image.
     * @param flipped
     *            If true, the images Y values are flipped.
     * @return the loaded texture. If there is a problem loading the texture,
     *         null is returned.
     */
    public static com.jme.image.Texture loadTexture(URL file) {
        return loadTexture(file, true);
    }

    /**
     * <code>loadTexture</code> loads a new texture defined by the parameter
     * url. If there is an error loading the file, null is returned.
     * 
     * @param file
     *            the url of the texture image.
     * @param flipped
     *            If true, the images Y values are flipped.
     * @return the loaded texture. If there is a problem loading the texture,
     *         null is returned.
     */
    public static com.jme.image.Texture loadTexture(URL file, boolean flipped) {
        return loadTexture(file, DEFAULT_MIN_FILTER, DEFAULT_MAG_FILTER,
                (COMPRESS_BY_DEFAULT ? Image.Format.Guess
                        : Image.Format.GuessNoCompression),
                DEFAULT_ANISO_LEVEL, flipped);
    }

    /**
     * <code>loadTexture</code> loads a new texture defined by the parameter
     * url. Filter parameters are used to define the filtering of the texture.
     * If there is an error loading the file, null is returned.
     * 
     * @param file
     *            the url of the texture image.
     * @param minFilter
     *            the filter for the near values.
     * @param magFilter
     *            the filter for the far values.
     * @return the loaded texture. If there is a problem loading the texture,
     *         null is returned.
     */
    public static com.jme.image.Texture loadTexture(URL file,
            Texture.MinificationFilter minFilter,
            Texture.MagnificationFilter magFilter) {
        return loadTexture(file, minFilter, magFilter,
                (COMPRESS_BY_DEFAULT ? Image.Format.Guess
                        : Image.Format.GuessNoCompression),
                DEFAULT_ANISO_LEVEL, true);
    }

    public static com.jme.image.Texture loadTexture(URL file,
            Texture.MinificationFilter minFilter,
            Texture.MagnificationFilter magFilter, float anisoLevel,
            boolean flipped) {
        return loadTexture(file, minFilter, magFilter,
                (COMPRESS_BY_DEFAULT ? Image.Format.Guess
                        : Image.Format.GuessNoCompression), anisoLevel, flipped);
    }

    /**
     * <code>loadTexture</code> loads a new texture defined by the parameter
     * url. Filter parameters are used to define the filtering of the texture.
     * If there is an error loading the file, null is returned.
     * 
     * @param file
     *            the url of the texture image.
     * @param minFilter
     *            the filter for the near values.
     * @param magFilter
     *            the filter for the far values.
     * @param imageType
     *            the image type to use. if Image.Format.Guess, the type is
     *            determined by jME. If S3TC/DXT is available we use that. if
     *            Image.Format.GuessNoCompression, the type is determined by jME
     *            without using S3TC, even if available. See
     *            com.jme.image.Image.Format for possible types.
     * @param flipped
     *            If true, the images Y values are flipped.
     * @return the loaded texture if possible, otherwise a default texture.
     * @throw IllegalArgumentException for the case where it is impossible for
     *        us to "recover":  we can not load the specified URL and no
     *        default texture has been set up.
     * @see Image.Format
     */
    public static com.jme.image.Texture loadTexture(URL file,
            Texture.MinificationFilter minFilter,
            Texture.MagnificationFilter magFilter, Image.Format imageType,
            float anisoLevel, boolean flipped) {

        if (null == file) {
            Texture defaultTexture = TextureState.getDefaultTexture();
            logger.warning("Could not load image.  Specified URL is null.");
            if (defaultTexture != null) return defaultTexture;
            throw new IllegalArgumentException("Image for 'null' "
                    + "requested, and no default Texture is set up");
        }

        String fileName = file.getFile();
        if (fileName == null) {
            Texture defaultTexture = TextureState.getDefaultTexture();
            logger.warning("Could not load image.  "
                    + "Specified fileName is null.");
            if (defaultTexture != null) return defaultTexture;
            throw new IllegalArgumentException("Image for '" + file
                    + "' requested, and no default Texture is set up");
        }

        TextureKey tkey = new TextureKey(file, flipped, imageType);

        return loadTexture(null, tkey, null, minFilter, magFilter, anisoLevel);
    }

    public static com.jme.image.Texture loadTexture(TextureKey tkey) {
        return loadTexture(null, tkey);
    }

    public static com.jme.image.Texture loadTexture(Texture texture,
            TextureKey tkey) {
        return loadTexture(texture, tkey, null, DEFAULT_MIN_FILTER,
                DEFAULT_MAG_FILTER, DEFAULT_ANISO_LEVEL);
    }

    public static com.jme.image.Texture loadTexture(Texture texture,
            TextureKey tkey, com.jme.image.Image imageData,
            Texture.MinificationFilter minFilter,
            Texture.MagnificationFilter magFilter, float anisoLevel) {
        if (tkey == null) {
            logger.warning("TextureKey is null, cannot load");
            return TextureState.getDefaultTexture();
        }

        Texture cache = findCachedTexture(tkey);
        if (cache != null) {
            // look into cache.
            // Uncomment if you want to see when this occurs.
            // logging.info("******** REUSING TEXTURE ******** "+cache);
            if (texture == null) {
                Texture tClone = cache.createSimpleClone();
                if (tClone.getTextureKey() == null) {
                    tClone.setTextureKey(tkey);
                }
                return tClone;
            }
            cache.createSimpleClone(texture);
            return texture;
        }

        if (imageData == null)
            imageData = loadImage(tkey);

        if (null == imageData) {
            logger.warning("(image null) Could not load: "
                    + (tkey.getLocation() != null ? tkey.getLocation()
                            .getFile() : tkey.getFileType()));
            return TextureState.getDefaultTexture();
        }

        // Default to Texture2D
        if (texture == null) {
        	if (imageData.getData().size() == 6) {
        		texture = new TextureCubeMap();
        	} else {
        		texture = new Texture2D();
        	}
        }

        // Use a tex state only to determine if S3TC is available.
        TextureState state = null;
        if (DisplaySystem.getDisplaySystem() != null
                && DisplaySystem.getDisplaySystem().getRenderer() != null) {
            state = (TextureState) Renderer.defaultStateList[RenderState.StateType.Texture.ordinal()];
        }

        // we've already guessed the format. override if given.
        if (tkey.format != Image.Format.GuessNoCompression
                && tkey.format != Image.Format.Guess) {
            imageData.setFormat(tkey.format);
        } else if (tkey.format == Image.Format.Guess && state != null
                && state.isS3TCSupported()) {
            // Enable S3TC DXT1 compression if available and we're guessing
            // format.
            if (imageData.getFormat() == Image.Format.RGB8) {
                imageData.setFormat(Image.Format.RGB_TO_DXT1);
            } else if (imageData.getFormat() == Image.Format.RGBA8) {
                imageData.setFormat(Image.Format.RGBA_TO_DXT5);
            }
        }

        texture.setTextureKey(tkey);
        texture.setMagnificationFilter(magFilter);
        texture.setImage(imageData);
        texture.setAnisotropicFilterPercent(anisoLevel);
        texture.setMinificationFilter(minFilter);
        if (tkey.location != null) {
            texture.setImageLocation(tkey.location.toString());
        }

        addToCache(texture);
        return texture;
    }

    public static void addToCache(Texture t) {
        if (TextureState.getDefaultTexture() == null
                || (t != TextureState.getDefaultTexture() && t.getImage() != TextureState
                        .getDefaultTextureImage())) {
            m_tCache.put(t.getTextureKey(), t);
        }
    }

    public static com.jme.image.Texture loadTexture(java.awt.Image image,
            Texture.MinificationFilter minFilter,
            Texture.MagnificationFilter magFilter, boolean flipped) {
        return loadTexture(image, minFilter, magFilter, DEFAULT_ANISO_LEVEL,
                (COMPRESS_BY_DEFAULT ? Image.Format.Guess
                        : Image.Format.GuessNoCompression), flipped);
    }

    public static com.jme.image.Texture loadTexture(java.awt.Image image,
            Texture.MinificationFilter minFilter,
            Texture.MagnificationFilter magFilter, float anisoLevel,
            boolean flipped) {
        return loadTexture(image, minFilter, magFilter, anisoLevel,
                (COMPRESS_BY_DEFAULT ? Image.Format.Guess
                        : Image.Format.GuessNoCompression), flipped);
    }

    public static com.jme.image.Texture loadTexture(java.awt.Image image,
            Texture.MinificationFilter minFilter,
            Texture.MagnificationFilter magFilter, float anisoLevel,
            Image.Format imageFormat, boolean flipped) {
        com.jme.image.Image imageData = loadImage(image, flipped);

        TextureKey tkey = new TextureKey(null, flipped, imageFormat);
        if (image != null)
            tkey.setFileType("" + image.hashCode());
        return loadTexture(null, tkey, imageData, minFilter, magFilter,
                anisoLevel);
    }

    public static com.jme.image.Image loadImage(TextureKey key) {
        if (key == null) {
            return null;
        }

        if ("savable".equalsIgnoreCase(key.fileType)) {
            Savable s;
            try {
                s = BinaryImporter.getInstance().load(key.location);
            } catch (IOException e) {
                logger.log(Level.WARNING, "Could not load Savable.", e);
                return null;
            }
            if (s instanceof com.jme.image.Image) {
                return (Image) s;
            }
            logger.warning("Savable not of type Image.");
            return TextureState.getDefaultTextureImage();
        }
        return loadImage(key.location, key.flipped);
    }

    public static com.jme.image.Image loadImage(URL file, boolean flipped) {
        if (file == null) {
            logger
                    .warning("loadImage(URL file, boolean flipped): file is null, defaultTexture used.");
            return TextureState.getDefaultTextureImage();
        }

        String fileName = file.getFile();
        if (fileName == null) {
            logger
                    .warning("loadImage(URL file, boolean flipped): fileName is null, defaultTexture used.");
            return TextureState.getDefaultTextureImage();
        }

        int dot = fileName.lastIndexOf('.');
        String fileExt = dot >= 0 ? fileName.substring(dot) : "";
        InputStream is = null;
        try {
            is = file.openStream();
            return loadImage(fileExt, is, flipped);
        } catch (IOException e) {
            logger
                    .log(
                            Level.WARNING,
                            "loadImage(URL file, boolean flipped): defaultTexture used",
                            e);
            return TextureState.getDefaultTextureImage();
        } finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException ioe) { } // ignore
            }
        }
    }

    public static com.jme.image.Image loadImage(String fileName, boolean flipped) {
        return loadImage(getTextureURL(fileName), flipped);
    }

    public static com.jme.image.Image loadImage(String fileExt,
            InputStream stream, boolean flipped) {

        com.jme.image.Image imageData = null;
        try {
            ImageLoader loader = loaders.get(fileExt.toLowerCase());
            if (loader != null)
                imageData = loader.load(stream);
            else if (".TGA".equalsIgnoreCase(fileExt)) { // TGA, direct to
                // imageData
                imageData = TGALoader.loadImage(stream, flipped);
            } else if (".DDS".equalsIgnoreCase(fileExt)) { // DDS, direct to
                // imageData
                imageData = DDSLoader.loadImage(stream, flipped);
            } else if (".BMP".equalsIgnoreCase(fileExt)) { // BMP, awtImage to
                // imageData
                java.awt.Image image = loadBMPImage(stream);
                imageData = loadImage(image, flipped);
            } else { // Anything else
                java.awt.Image image = ImageIO.read(stream); // readImage(fileExt, stream);
                imageData = loadImage(image, flipped);
            }
            if (imageData == null) {
                logger
                        .warning("loadImage(String fileExt, InputStream stream, boolean flipped): no imageData found.  defaultTexture used.");
                imageData = TextureState.getDefaultTextureImage();
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Could not load Image.", e);
            imageData = TextureState.getDefaultTextureImage();
        }
        return imageData;
    }

    /**
     * Load the image as either TYPE_3BYTE_BGR or TYPE_4BYTE_ABGR
     * 
     * @param fileExt
     * @param imageIn
     * @return
     * @throws java.io.IOException
     * TODO Does this need to be removed?
     */
    private static BufferedImage readImage(String fileExt, InputStream imageIn)
            throws IOException {
        BufferedImage image;
        ImageTypeSpecifier imageType;
        int width;
        int height;

        if (imageIn == null)
            throw new IOException("Null Stream");

        String format = fileExt.substring(1); // Remove .
        ImageReader reader = (ImageReader) ImageIO.getImageReadersByFormatName(
                format).next();

        try {
            // Not ideal as we are creating a cache file, but as we
            // are processing
            // a stream we don't have access to the local file info
            reader.setInput(new FileCacheImageInputStream(imageIn, null));
            imageType = reader.getRawImageType(0);
            if (imageType == null) {
                // Workaround for Mac issue getting image type of JPEG images.
                // Look through the list to find the first type with
                // a non-null ColorModel
                for (Iterator<ImageTypeSpecifier> i = reader.getImageTypes(0); i
                        .hasNext();) {
                    ImageTypeSpecifier temp = i.next();
                    if (temp != null && temp.getColorModel() != null) {
                        imageType = temp;
                        break;
                    }
                }

                // if there is still no image type, throw an
                // exception
                if (imageType == null) {
                    throw new IOException("Cannot get image type for "
                            + fileExt);
                }
            }
            width = reader.getWidth(0);
            height = reader.getHeight(0);
        } catch (IndexOutOfBoundsException ioob) {
            logger.warning("Corrupt image file ");
            // The image file is corrupt
            throw new IOException("Image read failure");
        }

        if (imageType.getColorModel().getTransparency() == ColorModel.OPAQUE) {
            image = new BufferedImage(width, height,
                    BufferedImage.TYPE_3BYTE_BGR);
        } else {
            image = new BufferedImage(width, height,
                    BufferedImage.TYPE_4BYTE_ABGR);
        }
        ImageReadParam param = reader.getDefaultReadParam();
        param.setDestination(image);
        image = reader.read(0, param);

        reader.dispose();

        return image;
    }

    /**
     * <code>loadImage</code> sets the image data. It will set the jme Image's
     * format to RGB8 or RGBA8 or Alpha8 depending on the incoming java Image.
     * (greyscale will be sent back as Alpha8)
     * 
     * @param image
     *            The image data.
     * @param flipImage
     *            if true will flip the image's y values.
     * @return the loaded image.
     */
    public static com.jme.image.Image loadImage(java.awt.Image image,
            boolean flipImage) {
        if (image == null)
            return null;
        boolean hasAlpha = hasAlpha(image), grayscale = isGreyscale(image);
        BufferedImage tex;
        
        if (flipImage
                || !(image instanceof BufferedImage)
                || (((BufferedImage) image).getType() != BufferedImage.TYPE_BYTE_GRAY && (hasAlpha ? ((BufferedImage) image)
                        .getType() != BufferedImage.TYPE_4BYTE_ABGR
                        : ((BufferedImage) image).getType() != BufferedImage.TYPE_3BYTE_BGR))) {
            // Obtain the image data.
            try {
                tex = new BufferedImage(image.getWidth(null), image
                        .getHeight(null), grayscale ? BufferedImage.TYPE_BYTE_GRAY
                        : hasAlpha ? BufferedImage.TYPE_4BYTE_ABGR
                                : BufferedImage.TYPE_3BYTE_BGR);
            } catch (IllegalArgumentException e) {
                logger.warning("Problem creating buffered Image: "
                        + e.getMessage());
                return TextureState.getDefaultTextureImage();
            }
            image.getWidth(null);
            image.getHeight(null);

            if (image instanceof BufferedImage) {
                int imageWidth = image.getWidth(null);
                int[] tmpData = new int[imageWidth];
                int row = 0;
                BufferedImage bufferedImage = ((BufferedImage) image);
                for (int y = image.getHeight(null) - 1; y >= 0; y--) {
                    bufferedImage.getRGB(0, (flipImage ? row++ : y),
                            imageWidth, 1, tmpData, 0, imageWidth);
                    tex.setRGB(0, y, imageWidth, 1, tmpData, 0, imageWidth);
                }
            } else {
                AffineTransform tx = null;
                if (flipImage) {
                    tx = AffineTransform.getScaleInstance(1, -1);
                    tx.translate(0, -image.getHeight(null));
                }
                Graphics2D g = (Graphics2D) tex.getGraphics();
                g.drawImage(image, tx, null);
                g.dispose();
            }

        } else {
            tex = (BufferedImage) image;
        }
        // Get a pointer to the image memory
        byte data[] = (byte[]) tex.getRaster().getDataElements(0, 0,
                tex.getWidth(), tex.getHeight(), null);
        ByteBuffer scratch = createOnHeap ? BufferUtils.createByteBufferOnHeap(data.length) : BufferUtils.createByteBuffer(data.length);
        scratch.clear();
        scratch.put(data);
        scratch.flip();
        com.jme.image.Image textureImage = new com.jme.image.Image();
        textureImage.setFormat(grayscale ? Image.Format.Alpha8 : hasAlpha ? Image.Format.RGBA8
                : Image.Format.RGB8);
        textureImage.setWidth(tex.getWidth());
        textureImage.setHeight(tex.getHeight());
        textureImage.setData(scratch);
        return textureImage;
    }

    /**
     * <code>loadBMPImage</code> because bitmap is not directly supported by
     * Java, we must load it manually. The requires opening a stream to the file
     * and reading in each byte. After the image data is read, it is used to
     * create a new <code>Image</code> object. This object is returned to be
     * used for normal use.
     * 
     * @param fs
     *            The bitmap file stream.
     * @return <code>Image</code> object that contains the bitmap information.
     */
    private static java.awt.Image loadBMPImage(InputStream fs) {
        try {
            DataInputStream dis = new DataInputStream(fs);
            BitmapHeader bh = new BitmapHeader();
            byte[] data = new byte[dis.available()];
            dis.readFully(data);
            dis.close();
            bh.read(data);
            if (bh.bitcount == 24) {
                return (bh.readMap24(data));
            }
            if (bh.bitcount == 32) {
                return (bh.readMap32(data));
            }
            if (bh.bitcount == 8) {
                return (bh.readMap8(data));
            }
        } catch (IOException e) {
            logger.warning("Error while loading bitmap texture.");
        }
        return null;
    }

    /**
     * <code>hasAlpha</code> returns true if the specified image has
     * transparent pixels
     * 
     * @param image
     *            Image to check
     * @return true if the specified image has transparent pixels
     */
    public static boolean hasAlpha(java.awt.Image image) {
        if (null == image) {
            return false;
        }
        if (image instanceof BufferedImage) {
            BufferedImage bufferedImage = (BufferedImage) image;
            return bufferedImage.getColorModel().hasAlpha();
        }
        PixelGrabber pixelGrabber = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pixelGrabber.grabPixels();
            ColorModel colorModel = pixelGrabber.getColorModel();
            if (colorModel != null) {
                return colorModel.hasAlpha();
            }

            return false;
        } catch (InterruptedException e) {
            logger.warning("Unable to determine alpha of image: " + image);
        }
        return false;
    }

    /**
     * <code>isGreyscale</code> returns true if the specified image is greyscale.
     * 
     * @param image
     *            Image to check
     * @return true if the specified image is greyscale.
     */
    public static boolean isGreyscale(java.awt.Image image) {
        if (null == image) {
            return false;
        }
        if (image instanceof BufferedImage) {
            BufferedImage bufferedImage = (BufferedImage) image;
            return bufferedImage.getColorModel().getNumComponents() == 1;
        }
        PixelGrabber pixelGrabber = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pixelGrabber.grabPixels();
            ColorModel colorModel = pixelGrabber.getColorModel();
            if (colorModel != null) {
                return colorModel.getNumComponents() == 1;
            }

            return false;
        } catch (InterruptedException e) {
            logger.warning("Unable to determine if image is greyscale: " + image);
        }
        return false;
    }

    public static boolean releaseTexture(Texture texture) {
        if (texture == null)
            return false;

        Collection<TextureKey> c = m_tCache.keySet();
        Iterator<TextureKey> it = c.iterator();
        TextureKey key;
        Texture next;
        while (it.hasNext()) {
            key = it.next();
            next = m_tCache.get(key);
            if (texture.equals(next)) {
                return releaseTexture(key);
            }
        }
        return false;
    }

    public static boolean releaseTexture(TextureKey tKey) {
        return m_tCache.remove(tKey) != null;
    }

    public static void clearCache() {
        m_tCache.clear();
    }

    /**
     * Register an ImageLoader to handle all files with a specific extention. An
     * ImageLoader can be registered to handle several formats without problems.
     * 
     * @param format
     *            The file extention for the format this ImageLoader will
     *            handle. Make sure to include the dot (eg. ".BMP"). This value
     *            is case insensitive (".Bmp" will register for ".BMP", ".bmp",
     *            etc.)
     * @param handler
     */
    public static void registerHandler(String format, ImageLoader handler) {
        loaders.put(format.toLowerCase(), handler);
    }

    public static void unregisterHandler(String format) {
        loaders.remove(format.toLowerCase());
    }

    public static void registerForCleanup(TextureKey textureKey, int textureId) {
        Texture t = m_tCache.get(textureKey);
        if (t != null) {
            t.setTextureId(textureId);
        }

        cleanupStore.add(textureId);
    }

    public static void doTextureCleanup() {
        if (DisplaySystem.getDisplaySystem() == null
                || DisplaySystem.getDisplaySystem().getRenderer() == null)
            return;
        
        TextureState ts = (TextureState)Renderer.defaultStateList[RenderState.StateType.Texture.ordinal()];
        for (Integer i : cleanupStore) {
            if (i != null) {
                try {
                    ts.deleteTextureId(i.intValue());
                } catch (Exception e) {
                } // ignore.
            }
        }
    }

    public static void deleteTextureFromCard(Texture tex) {
        if (tex == null || DisplaySystem.getDisplaySystem() == null
                || DisplaySystem.getDisplaySystem().getRenderer() == null)
            return;

        TextureState ts = (TextureState)Renderer.defaultStateList[RenderState.StateType.Texture.ordinal()];
        try {
            ts.deleteTextureId(tex.getTextureId());
            tex.setTextureId(0);
        } catch (Exception e) {
        } // ignore.
    }

    public static Texture findCachedTexture(TextureKey textureKey) {
        return m_tCache.get(textureKey);
    }

    public static void preloadCache(Renderer r) {
        TextureState ts = r.createTextureState();
        for (Texture t : m_tCache.values()) {
            if (t.getTextureKey().location != null) {
                ts.setTexture(t);
                ts.load(0);
            }
        }
    }

    public static void setCreateOnHeap(boolean createOnHeap) {
        TextureManager.createOnHeap = createOnHeap;
    }

    public static boolean isCreateOnHeap() {
        return createOnHeap;
    }
}
