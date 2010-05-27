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

package com.jme.scene.state.lwjgl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.opengl.ARBDepthTexture;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.ARBShadow;
import org.lwjgl.opengl.ARBTextureCompression;
import org.lwjgl.opengl.ARBTextureCubeMap;
import org.lwjgl.opengl.ARBTextureEnvCombine;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.SGISGenerateMipmap;
import org.lwjgl.opengl.Util;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.MipMap;

import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.image.Texture1D;
import com.jme.image.Texture2D;
import com.jme.image.Texture3D;
import com.jme.image.TextureCubeMap;
import com.jme.image.Texture.ApplyMode;
import com.jme.image.Texture.CombinerFunctionAlpha;
import com.jme.image.Texture.CombinerFunctionRGB;
import com.jme.image.Texture.CombinerOperandAlpha;
import com.jme.image.Texture.CombinerOperandRGB;
import com.jme.image.Texture.CombinerSource;
import com.jme.image.Texture.Type;
import com.jme.image.Texture.WrapAxis;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.RenderContext;
import com.jme.scene.Spatial;
import com.jme.scene.Spatial.TextureCombineMode;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.StateRecord;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.lwjgl.records.RendererRecord;
import com.jme.scene.state.lwjgl.records.TextureRecord;
import com.jme.scene.state.lwjgl.records.TextureStateRecord;
import com.jme.scene.state.lwjgl.records.TextureUnitRecord;
import com.jme.system.DisplaySystem;
import com.jme.util.Debug;
import com.jme.util.TextureManager;
import com.jme.util.geom.BufferUtils;
import com.jme.util.stat.StatCollector;
import com.jme.util.stat.StatType;

/**
 * <code>LWJGLTextureState</code> subclasses the TextureState object using the
 * LWJGL API to access OpenGL for texture processing.
 * 
 * @author Mark Powell
 * @author Joshua Slack
 * @version $Id: LWJGLTextureState.java 4644 2009-08-31 06:18:28Z andreas.grabner@gmail.com $
 */
public class LWJGLTextureState extends TextureState {
    private static final Logger logger = Logger
            .getLogger(LWJGLTextureState.class.getName());

    private static final long serialVersionUID = 1L;
    private static boolean inited = false;

    /**
     * Constructor instantiates a new <code>LWJGLTextureState</code> object.
     * The number of textures that can be combined is determined during
     * construction. This equates the number of texture units supported by the
     * graphics card.
     */
    public LWJGLTextureState() {
        super();

        // get our array of texture objects ready.
        texture = new ArrayList<Texture>();

        // See if we haven't already setup a texturestate before.
        if (!inited) {
            // Check for support of multitextures.
            supportsMultiTexture = supportsMultiTextureDetected = GLContext
                    .getCapabilities().GL_ARB_multitexture;

            // Check for support of fixed function dot3 environment settings
            supportsEnvDot3 = supportsEnvDot3Detected = GLContext
                    .getCapabilities().GL_ARB_texture_env_dot3;

            // Check for support of fixed function dot3 environment settings
            supportsEnvCombine = supportsEnvCombineDetected = GLContext
                    .getCapabilities().GL_ARB_texture_env_combine;

            // Check for support of automatic mipmap generation
            automaticMipMaps = automaticMipMapsDetected = GLContext
                    .getCapabilities().GL_SGIS_generate_mipmap;
            
            supportsDepthTexture = GLContext.getCapabilities().GL_ARB_depth_texture;
            supportsShadow = GLContext.getCapabilities().GL_ARB_shadow;
            
            // If we do support multitexturing, find out how many textures we
            // can handle.
            if (supportsMultiTexture) {
                IntBuffer buf = BufferUtils.createIntBuffer(16);
                GL11
                        .glGetInteger(ARBMultitexture.GL_MAX_TEXTURE_UNITS_ARB,
                                buf);
                numFixedTexUnits = buf.get(0);
            } else {
                numFixedTexUnits = 1;
            }

            // Go on to check number of texture units supported for vertex and
            // fragment shaders
            if (GLContext.getCapabilities().GL_ARB_shader_objects
                    && GLContext.getCapabilities().GL_ARB_vertex_shader
                    && GLContext.getCapabilities().GL_ARB_fragment_shader) {
                IntBuffer buf = BufferUtils.createIntBuffer(16);
                GL11.glGetInteger(
                        ARBVertexShader.GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS_ARB,
                        buf);
                numVertexTexUnits = buf.get(0);
                GL11.glGetInteger(
                        ARBFragmentShader.GL_MAX_TEXTURE_IMAGE_UNITS_ARB, buf);
                numFragmentTexUnits = buf.get(0);
                GL11.glGetInteger(ARBFragmentShader.GL_MAX_TEXTURE_COORDS_ARB,
                        buf);
                numFragmentTexCoordUnits = buf.get(0);
            } else {
                // based on nvidia dev doc:
                // http://developer.nvidia.com/object/General_FAQ.html#t6
                // "For GPUs that do not support GL_ARB_fragment_program and
                // GL_NV_fragment_program, those two limits are set equal to
                // GL_MAX_TEXTURE_UNITS."
                numFragmentTexCoordUnits = numFixedTexUnits;
                numFragmentTexUnits = numFixedTexUnits;

                // We'll set this to 0 for now since we do not know:
                numVertexTexUnits = 0;
            }

            // Now determine the maximum number of supported texture units
            numTotalTexUnits = Math.max(numFragmentTexCoordUnits, Math.max(
                    numFixedTexUnits, Math.max(numFragmentTexUnits,
                            numVertexTexUnits)));

            // Check for S3 texture compression capability.
            supportsS3TCCompression = supportsS3TCCompressionDetected = GLContext
                    .getCapabilities().GL_EXT_texture_compression_s3tc;

            // Check for S3 texture compression capability.
            supportsTexture3D = supportsTexture3DDetected = GLContext
                    .getCapabilities().GL_EXT_texture_3d;

            // Check for S3 texture compression capability.
            supportsTextureCubeMap = supportsTextureCubeMapDetected = GLContext
                    .getCapabilities().GL_ARB_texture_cube_map;

            // See if we support anisotropic filtering
            supportsAniso = supportsAnisoDetected = GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic;

            if (supportsAniso) {
                // Due to LWJGL buffer check, you can't use smaller sized
                // buffers (min_size = 16 for glGetFloat()).
                FloatBuffer max_a = BufferUtils.createFloatBuffer(16);
                max_a.rewind();

                // Grab the maximum anisotropic filter.
                GL11
                        .glGetFloat(
                                EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT,
                                max_a);

                // set max.
                maxAnisotropic = max_a.get(0);
            }

            // See if we support textures that are not power of 2 in size.
            supportsNonPowerTwo = supportsNonPowerTwoDetected = GLContext
                    .getCapabilities().GL_ARB_texture_non_power_of_two;

            // See if we support textures that do not have width == height.
            supportsRectangular = supportsRectangularDetected = GLContext
                    .getCapabilities().GL_ARB_texture_rectangle;

            // Setup our default texture by adding it to our array and loading
            // it, then clearing our array.
            setTexture(defaultTexture);
            load(0);
            this.texture.clear();

            // We're done initing! Wee! :)
            inited = true;
        }
    }

    /**
     * override MipMap to access helper methods
     */
    protected static class LWJGLMipMap extends MipMap {
        /**
         * @see MipMap#glGetIntegerv(int)
         */
        protected static int glGetIntegerv(int what) {
            return org.lwjgl.util.glu.Util.glGetIntegerv(what);
        }

        /**
         * @see MipMap#nearestPower(int)
         */
        protected static int nearestPower(int value) {
            return org.lwjgl.util.glu.Util.nearestPower(value);
        }

        /**
         * @see MipMap#bytesPerPixel(int, int)
         */
        protected static int bytesPerPixel(int format, int type) {
            return org.lwjgl.util.glu.Util.bytesPerPixel(format, type);
        }
    }

    @Override
    public final void load(int unit) {
        Texture texture = getTexture(unit);
        if (texture == null) {
            return;
        }
        
        // our texture type:
        Texture.Type type = texture.getType();

        RenderContext<?> context = DisplaySystem.getDisplaySystem().getCurrentContext();
        TextureStateRecord record = null;
        if (context != null)
            record = (TextureStateRecord) context.getStateRecord(StateType.Texture);

        // Check we are in the right unit
        if (record != null)
            checkAndSetUnit(unit, record);

        // Create the texture
        if (texture.getTextureKey() != null) {
            Texture cached = TextureManager.findCachedTexture(texture
                    .getTextureKey());
            if (cached == null) {
                TextureManager.addToCache(texture);
            } else if (cached.getTextureId() != 0) {
                texture.setTextureId(cached.getTextureId());
                GL11.glBindTexture(getGLType(type), cached.getTextureId());
                if (Debug.stats) {
                    StatCollector.addStat(StatType.STAT_TEXTURE_BINDS, 1);
                }
                if (record != null)
                    record.units[unit].boundTexture = texture.getTextureId();
                return;
            }
        }

        IntBuffer id = BufferUtils.createIntBuffer(1);
        id.clear();
        GL11.glGenTextures(id);
        texture.setTextureId(id.get(0));

        GL11.glBindTexture(getGLType(type), texture.getTextureId());
        if (Debug.stats) {
            StatCollector.addStat(StatType.STAT_TEXTURE_BINDS, 1);
        }
        if (record != null)
            record.units[unit].boundTexture = texture.getTextureId();

        TextureManager.registerForCleanup(texture.getTextureKey(), texture
                .getTextureId());

        // pass image data to OpenGL
        Image image = texture.getImage();
        boolean hasBorder = texture.hasBorder();
        if (image == null) {
            logger.warning("Image data for texture is null.");
        }

        // set alignment to support images with width % 4 != 0, as images are
        // not aligned
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

        // Get texture image data. Not all textures have image data.
        // For example, ApplyMode.Combine modes can use primary colors,
        // texture output, and constants to modify fragments via the
        // texture units.
        if (image != null) {
            if (!supportsNonPowerTwo
                    && (!FastMath.isPowerOfTwo(image.getWidth()) || !FastMath
                            .isPowerOfTwo(image.getHeight()))) {
                logger.log(Level.WARNING, "(card unsupported) Attempted to apply texture with size that is not power of 2: "
                                + "{0}x{1}", new Integer[] {image.getWidth(), image.getHeight()});

                final int maxSize = LWJGLMipMap
                        .glGetIntegerv(GL11.GL_MAX_TEXTURE_SIZE);

                int actualWidth = image.getWidth();
                int w = LWJGLMipMap.nearestPower(actualWidth);
                if (w > maxSize) {
                    w = maxSize;
                }

                int actualHeight = image.getHeight();
                int h = LWJGLMipMap.nearestPower(actualHeight);
                if (h > maxSize) {
                    h = maxSize;
                }
                logger.log(Level.WARNING, "Rescaling image to {0} x {1} !!!", new Integer[]{w, h});

                // must rescale image to get "top" mipmap texture image
                int format = TextureStateRecord.getGLPixelFormat(image
                        .getFormat());
                int dType = GL11.GL_UNSIGNED_BYTE;
                int bpp = LWJGLMipMap.bytesPerPixel(format, dType);
                ByteBuffer scaledImage = BufferUtils.createByteBuffer((w + 4)
                        * h * bpp);
                int error = MipMap.gluScaleImage(format, actualWidth,
                        actualHeight, dType, image.getData(0), w, h, dType,
                        scaledImage);
                if (error != 0) {
                    Util.checkGLError();
                }

                image.setWidth(w);
                image.setHeight(h);
                image.setData(scaledImage);
            }

            if (!texture.getMinificationFilter().usesMipMapLevels()
                    && !TextureStateRecord.isCompressedType(image.getFormat())) {

                // Load textures which do not need mipmap auto-generating and
                // which aren't using compressed images.

                switch (texture.getType()) {
                    case TwoDimensional:
                        // ensure the buffer is ready for reading
                        image.getData(0).rewind();
                        // send top level to card
                        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0,
                                TextureStateRecord.getGLDataFormat(image
                                        .getFormat()), image.getWidth(), image
                                        .getHeight(), hasBorder ? 1 : 0,
                                TextureStateRecord.getGLPixelFormat(image
                                        .getFormat()), GL11.GL_UNSIGNED_BYTE,
                                image.getData(0));
                        break;
                    case OneDimensional:
                        // ensure the buffer is ready for reading
                        image.getData(0).rewind();
                        // send top level to card
                        GL11.glTexImage1D(GL11.GL_TEXTURE_1D, 0,
                                TextureStateRecord.getGLDataFormat(image
                                        .getFormat()), image.getWidth(),
                                hasBorder ? 1 : 0, TextureStateRecord
                                        .getGLPixelFormat(image.getFormat()),
                                GL11.GL_UNSIGNED_BYTE, image.getData(0));
                        break;
                    case ThreeDimensional:
                        if (supportsTexture3D) {
                            // concat data into single buffer:
                            int dSize = 0;
                            int count = 0;
                            ByteBuffer data = null;
                            for (int x = 0; x < image.getData().size(); x++) {
                                if (image.getData(x) != null) {
                                    data = image.getData(x);
                                    dSize += data.limit();
                                    count++;
                                }
                            }
                            // reuse buffer if we can.
                            if (count != 1) {
                                data = BufferUtils.createByteBuffer(dSize);
                                for (int x = 0; x < image.getData().size(); x++) {
                                    if (image.getData(x) != null) {
                                        data.put(image.getData(x));
                                    }
                                }
                                // ensure the buffer is ready for reading
                                data.flip();
                            }
                            // send top level to card
                            GL12.glTexImage3D(GL12.GL_TEXTURE_3D, 0,
                                    TextureStateRecord.getGLDataFormat(image
                                            .getFormat()), image.getWidth(),
                                    image.getHeight(), image.getDepth(),
                                    hasBorder ? 1 : 0,
                                    TextureStateRecord.getGLPixelFormat(image
                                            .getFormat()),
                                    GL11.GL_UNSIGNED_BYTE, data);
                        } else {
                            logger
                                    .warning("This card does not support Texture3D.");
                        }
                        break;
                    case CubeMap:
                        // NOTE: Cubemaps MUST be square, so height is ignored
                        // on purpose.
                        if (supportsTextureCubeMap) {
                            for (TextureCubeMap.Face face : TextureCubeMap.Face
                                    .values()) {
                                // ensure the buffer is ready for reading
                                image.getData(face.ordinal()).rewind();
                                // send top level to card
                                GL11.glTexImage2D(getGLCubeMapFace(face), 0,
                                        TextureStateRecord
                                                .getGLDataFormat(image
                                                        .getFormat()), image
                                                .getWidth(), image.getWidth(),
                                        hasBorder ? 1 : 0, TextureStateRecord
                                                .getGLPixelFormat(image
                                                        .getFormat()),
                                        GL11.GL_UNSIGNED_BYTE, image
                                                .getData(face.ordinal()));
                            }
                        } else {
                            logger
                                    .warning("This card does not support Cubemaps.");
                        }
                        break;
                }
            } else if (texture.getMinificationFilter().usesMipMapLevels()
                    && !image.hasMipmaps()
                    && !TextureStateRecord.isCompressedType(image.getFormat())) {

                // For textures which need mipmaps auto-generating and which
                // aren't using compressed images, generate the mipmaps.
                // A new mipmap builder may be needed to build mipmaps for
                // compressed textures.

                if (automaticMipMaps) {
                    // Flag the card to generate mipmaps
                    GL11.glTexParameteri(getGLType(type),
                            SGISGenerateMipmap.GL_GENERATE_MIPMAP_SGIS,
                            GL11.GL_TRUE);
                }

                switch (type) {
                    case TwoDimensional:
                        // ensure the buffer is ready for reading
                        image.getData(0).rewind();
                        if (automaticMipMaps) {
                            // send top level to card
                            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0,
                                    TextureStateRecord.getGLDataFormat(image
                                            .getFormat()), image.getWidth(),
                                    image.getHeight(), hasBorder ? 1 : 0,
                                    TextureStateRecord.getGLPixelFormat(image
                                            .getFormat()),
                                    GL11.GL_UNSIGNED_BYTE, image.getData(0));
                        } else {
                            // send to card
                            GLU.gluBuild2DMipmaps(GL11.GL_TEXTURE_2D,
                                    TextureStateRecord.getGLDataFormat(image
                                            .getFormat()), image.getWidth(),
                                    image.getHeight(),
                                    TextureStateRecord.getGLPixelFormat(image
                                            .getFormat()),
                                    GL11.GL_UNSIGNED_BYTE, image.getData(0));
                        }
                        break;
                    case OneDimensional:
                        // ensure the buffer is ready for reading
                        image.getData(0).rewind();
                        if (automaticMipMaps) {
                            // send top level to card
                            GL11.glTexImage1D(GL11.GL_TEXTURE_1D, 0,
                                    TextureStateRecord.getGLDataFormat(image
                                            .getFormat()), image.getWidth(),
                                    hasBorder ? 1 : 0,
                                    TextureStateRecord.getGLPixelFormat(image
                                            .getFormat()),
                                    GL11.GL_UNSIGNED_BYTE, image.getData(0));
                        } else {
                            // Note: LWJGL's GLU class does not support
                            // gluBuild1DMipmaps.
                            logger
                                    .warning("non-fbo 1d mipmap generation is not currently supported.  Use DDS or a non-mipmap minification filter.");
                            return;
                        }
                        break;
                    case ThreeDimensional:
                        if (supportsTexture3D) {
                            if (automaticMipMaps) {
                                // concat data into single buffer:
                                int dSize = 0;
                                int count = 0;
                                ByteBuffer data = null;
                                for (int x = 0; x < image.getData().size(); x++) {
                                    if (image.getData(x) != null) {
                                        data = image.getData(x);
                                        dSize += data.limit();
                                        count++;
                                    }
                                }
                                // reuse buffer if we can.
                                if (count != 1) {
                                    data = BufferUtils.createByteBuffer(dSize);
                                    for (int x = 0; x < image.getData().size(); x++) {
                                        if (image.getData(x) != null) {
                                            data.put(image.getData(x));
                                        }
                                    }
                                    // ensure the buffer is ready for reading
                                    data.flip();
                                }
                                // send top level to card
                                GL12.glTexImage3D(GL12.GL_TEXTURE_3D, 0,
                                        TextureStateRecord
                                                .getGLDataFormat(image
                                                        .getFormat()), image
                                                .getWidth(), image.getHeight(),
                                        image.getDepth(), hasBorder ? 1 : 0,
                                        TextureStateRecord
                                                .getGLPixelFormat(image
                                                        .getFormat()),
                                        GL11.GL_UNSIGNED_BYTE, data);
                            } else {
                                // Note: LWJGL's GLU class does not support
                                // gluBuild3DMipmaps.
                                logger
                                        .warning("non-fbo 3d mipmap generation is not currently supported.  Use DDS or a non-mipmap minification filter.");
                                return;
                            }
                        } else {
                            logger
                                    .warning("This card does not support Texture3D.");
                            return;
                        }
                        break;
                    case CubeMap:
                        // NOTE: Cubemaps MUST be square, so height is ignored
                        // on purpose.
                        if (supportsTextureCubeMap) {
                            if (automaticMipMaps) {
                                for (TextureCubeMap.Face face : TextureCubeMap.Face
                                        .values()) {
                                    // ensure the buffer is ready for reading
                                    image.getData(face.ordinal()).rewind();
                                    // send top level to card
                                    GL11.glTexImage2D(getGLCubeMapFace(face),
                                            0, TextureStateRecord
                                                    .getGLDataFormat(image
                                                            .getFormat()),
                                            image.getWidth(), image.getWidth(),
                                            hasBorder ? 1 : 0,
                                            TextureStateRecord
                                                    .getGLPixelFormat(image
                                                            .getFormat()),
                                            GL11.GL_UNSIGNED_BYTE, image
                                                    .getData(face.ordinal()));
                                }
                            } else {
                                for (TextureCubeMap.Face face : TextureCubeMap.Face
                                        .values()) {
                                    // ensure the buffer is ready for reading
                                    image.getData(face.ordinal()).rewind();
                                    // send to card
                                    GLU.gluBuild2DMipmaps(
                                            getGLCubeMapFace(face),
                                            TextureStateRecord
                                                    .getGLDataFormat(image
                                                            .getFormat()),
                                            image.getWidth(), image.getWidth(),
                                            TextureStateRecord
                                                    .getGLPixelFormat(image
                                                            .getFormat()),
                                            GL11.GL_UNSIGNED_BYTE, image
                                                    .getData(face.ordinal()));
                                }
                            }
                        } else {
                            logger.warning("This card does not support Cubemaps.");
                            return;
                        }
                        break;
                }

            } else {
                // Here we handle textures that are either compressed or have
                // predefined mipmaps.
                // Get mipmap data sizes and amount of mipmaps to send to
                // opengl. Then loop through all mipmaps and send them.
                int[] mipSizes = image.getMipMapSizes();
                ByteBuffer data = null;
                
                if (type == Type.CubeMap) {
                	if (supportsTextureCubeMap) {
                		for (TextureCubeMap.Face face : TextureCubeMap.Face.values()) {
                			data = image.getData(face.ordinal());
                			int pos = 0;
                			int max = 1;

                			if (mipSizes == null) {
                				mipSizes = new int[] { data.capacity() };
                			} else if (texture.getMinificationFilter().usesMipMapLevels()) {
                				max = mipSizes.length;
                			}

                			for (int m = 0; m < max; m++) {
                				int width = Math.max(1, image.getWidth() >> m);
                				int height = type != Type.OneDimensional ? Math.max(1, image.getHeight() >> m) : 0;

	                            data.position(pos);
	                            data.limit(pos + mipSizes[m]);

	                            if (TextureStateRecord.isCompressedType(image.getFormat())) {
	                            	ARBTextureCompression.glCompressedTexImage2DARB(
	                            			getGLCubeMapFace(face),
	                            			m,
	                            			TextureStateRecord
	                            			.getGLDataFormat(image
	                            					.getFormat()),
	                            					width, height,
	                            					hasBorder ? 1 : 0, data);
	                            } else {
	                            	GL11.glTexImage2D(
	                            			getGLCubeMapFace(face),
	                            			m,
	                            			TextureStateRecord
	                            			.getGLDataFormat(image
	                            					.getFormat()),
	                            					width, height,
	                            					hasBorder ? 1 : 0,
	                            					TextureStateRecord.getGLPixelFormat(image.getFormat()),
                   									GL11.GL_UNSIGNED_BYTE, data);
	                            }
	                            pos += mipSizes[m];
                			}
                		}
                	} else {
                		logger.warning("This card does not support CubeMaps.");
                		return;
                	}
                } else {
                    data = image.getData(0);
                    int pos = 0;
                    int max = 1;
                    
                    if (mipSizes == null) {
                        mipSizes = new int[] { data.capacity() };
                    } else {
                        max = mipSizes.length;
                    }
                    
                    if (type == Type.ThreeDimensional) {
                    	if (supportsTexture3D) {
                    		// concat data into single buffer:
                    		int dSize = 0;
                    		int count = 0;
                    		for (int x = 0; x < image.getData().size(); x++) {
                    			if (image.getData(x) != null) {
                    				data = image.getData(x);
                    				dSize += data.limit();
                    				count++;
                    			}
                    		}
                    		// reuse buffer if we can.
                    		if (count != 1) {
                    			data = BufferUtils.createByteBuffer(dSize);
                    			for (int x = 0; x < image.getData().size(); x++) {
                    				if (image.getData(x) != null) {
                    					data.put(image.getData(x));
                    				}
                    			}
                    			// ensure the buffer is ready for reading
                    			data.flip();
                    		}
                    	} else {
                    		logger.warning("This card does not support Texture3D.");
                    		return;
                    	}
                    }

                	for (int m = 0; m < max; m++) {
                		int width = Math.max(1, image.getWidth() >> m);
                		int height = type != Type.OneDimensional ? Math.max(1,
                				image.getHeight() >> m) : 0;
                        int depth = type == Type.ThreeDimensional ? Math.max(1,
                        		image.getDepth() >> m) : 0;

                                data.position(pos);
                                data.limit(pos + mipSizes[m]);

                                switch (type) {
                                case TwoDimensional:
                                	if (TextureStateRecord.isCompressedType(image
                                			.getFormat())) {
                                		ARBTextureCompression
                                		.glCompressedTexImage2DARB(
                                				GL11.GL_TEXTURE_2D, m,
                                				TextureStateRecord
                                				.getGLDataFormat(image
                                						.getFormat()),
                                						width, height, hasBorder ? 1
                                								: 0, data);
                                	} else {
                                		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, m,
                                				TextureStateRecord
                                				.getGLDataFormat(image
                                						.getFormat()), width,
                                						height, hasBorder ? 1 : 0,
                                								TextureStateRecord
                                								.getGLPixelFormat(image
                                										.getFormat()),
                                										GL11.GL_UNSIGNED_BYTE, data);
                                	}
                                	break;
                                case OneDimensional:
                                	if (TextureStateRecord.isCompressedType(image
                                			.getFormat())) {
                                		ARBTextureCompression
                                		.glCompressedTexImage1DARB(
                                				GL11.GL_TEXTURE_1D, m,
                                				TextureStateRecord
                                				.getGLDataFormat(image
                                						.getFormat()),
                                						width, hasBorder ? 1 : 0, data);
                                	} else {
                                		GL11.glTexImage1D(GL11.GL_TEXTURE_1D, m,
                                				TextureStateRecord
                                				.getGLDataFormat(image
                                						.getFormat()), width,
                                						hasBorder ? 1 : 0, TextureStateRecord
                                								.getGLPixelFormat(image
                                										.getFormat()),
                                										GL11.GL_UNSIGNED_BYTE, data);
                                	}
                                	break;
                                case ThreeDimensional:
                                	// already checked for support above...
                                	if (TextureStateRecord.isCompressedType(image
                                			.getFormat())) {
                                		ARBTextureCompression
                                		.glCompressedTexImage3DARB(
                                				GL12.GL_TEXTURE_3D, m,
                                				TextureStateRecord
                                				.getGLDataFormat(image
                                						.getFormat()),
                                						width, height, depth,
                                						hasBorder ? 1 : 0, data);
                                	} else {
                                		GL12.glTexImage3D(GL12.GL_TEXTURE_3D, m,
                                				TextureStateRecord
                                				.getGLDataFormat(image
                                						.getFormat()), width,
                                						height, depth, hasBorder ? 1 : 0,
                                								TextureStateRecord
                                								.getGLPixelFormat(image
                                										.getFormat()),
                                										GL11.GL_UNSIGNED_BYTE, data);
                                	}
                                	break;
                                }
                                pos += mipSizes[m];
                	}
                }
                if (data != null) {
                	data.clear();
                }
            }
        }
    }

    /**
     * <code>apply</code> manages the textures being described by the state.
     * If the texture has not been loaded yet, it is generated and loaded using
     * OpenGL11. This means the initial pass to set will be longer than
     * subsequent calls. The multitexture extension is used to define the
     * multiple texture states, with the number of units being determined at
     * construction time.
     * 
     * @see com.jme.scene.state.RenderState#apply()
     */
    public void apply() {
        // ask for the current state record
        RenderContext<?> context = DisplaySystem.getDisplaySystem().getCurrentContext();
        TextureStateRecord record = (TextureStateRecord) context.getStateRecord(StateType.Texture);
        context.currentStates[StateType.Texture.ordinal()] = this;

        if (isEnabled()) {

            Texture texture;
            Texture.Type type;
            TextureUnitRecord unitRecord;
            TextureRecord texRecord;

            int glHint = TextureStateRecord.getPerspHint(getCorrectionType());
            if (!record.isValid() || record.hint != glHint) {
                // set up correction mode
                GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, glHint);
                record.hint = glHint;
            }

            // loop through all available texture units...
            for (int i = 0; i < numTotalTexUnits; i++) {
                unitRecord = record.units[i];

                // grab a texture for this unit, if available
                texture = getTexture(i);

                // check for invalid textures - ones that have no opengl id and
                // no image data
                if (texture != null && texture.getTextureId() == 0
                        && texture.getImage() == null)
                    texture = null;

                // null textures above fixed limit do not need to be disabled
                // since they are not really part of the pipeline.
                if (texture == null) {
                    if (i >= numFixedTexUnits)
                        continue;
                    else {
                        // a null texture indicates no texturing at this unit
                        // Disable texturing on this unit if enabled.
                        disableTexturing(unitRecord, record, i);

                        if (i < idCache.length)
                            idCache[i] = 0;

                        // next texture!
                        continue;
                    }
                }

                type = texture.getType();

                // disable other texturing types for this unit, if enabled.
                disableTexturing(unitRecord, record, i, type);

                // Time to bind the texture, so see if we need to load in image
                // data for this texture.
                if (texture.getTextureId() == 0) {
                    // texture not yet loaded.
                    // this will load and bind and set the records...
                    load(i);
                    if (texture.getTextureId() == 0)
                        continue;
                } else {
                    // texture already exists in OpenGL, just bind it if needed
                    if (!unitRecord.isValid()
                            || unitRecord.boundTexture != texture
                                    .getTextureId()) {
                        checkAndSetUnit(i, record);
                        GL11.glBindTexture(getGLType(type), texture
                                .getTextureId());
                        if (Debug.stats) {
                            StatCollector.addStat(StatType.STAT_TEXTURE_BINDS, 1);
                        }
                        unitRecord.boundTexture = texture.getTextureId();
                    }
                }

                // Grab our record for this texture
                texRecord = record.getTextureRecord(texture.getTextureId(),
                        texture.getType());

                // Set the idCache value for this unit of this texture state
                // This is done so during state comparison we don't have to
                // spend a lot of time pulling out classes and finding field
                // data.
                idCache[i] = texture.getTextureId();

                // Some texture things only apply to fixed function pipeline
                if (i < numFixedTexUnits) {

                    // Enable 2D texturing on this unit if not enabled.
                    if (!unitRecord.isValid()
                            || !unitRecord.enabled[type.ordinal()]) {
                        checkAndSetUnit(i, record);
                        GL11.glEnable(getGLType(type));
                        unitRecord.enabled[type.ordinal()] = true;
                    }

                    // Set our blend color, if needed.
                    applyBlendColor(texture, unitRecord, i, record);

                    // Set the texture environment mode if this unit isn't
                    // already set properly
                    applyEnvMode(texture.getApply(), unitRecord, i, record);

                    // If our mode is combine, and we support multitexturing
                    // apply combine settings.
                    if (texture.getApply() == ApplyMode.Combine
                            && supportsMultiTexture && supportsEnvCombine) {
                        applyCombineFactors(texture, unitRecord, i, record);
                    }
                }

                // Other items only apply to textures below the frag unit limit
                if (i < numFragmentTexUnits) {

                    // texture specific params
                    applyFilter(texture, texRecord, i, record);
                    applyWrap(texture, texRecord, i, record);
                    applyShadow(texture, texRecord, i, record);
                    
                    // Set our border color, if needed.
                    applyBorderColor(texture, texRecord, i, record);

                    // all states have now been applied for a tex record, so we
                    // can safely make it valid
                    if (!texRecord.isValid())
                        texRecord.validate();

                }

                // Other items only apply to textures below the frag tex coord
                // unit limit
                if (i < numFragmentTexCoordUnits) {

                    // Now time to play with texture matrices
                    // Determine which transforms to do.
                    applyTextureTransforms(texture, i, record);

                    // Now let's look at automatic texture coordinate
                    // generation.
                    applyTexCoordGeneration(texture, unitRecord, i, record);

                }

            }

        } else {
            // turn off texturing
            TextureUnitRecord unitRecord;

            if (supportsMultiTexture) {
                for (int i = 0; i < numFixedTexUnits; i++) {
                    unitRecord = record.units[i];
                    disableTexturing(unitRecord, record, i);
                }
            } else {
                unitRecord = record.units[0];
                disableTexturing(unitRecord, record, 0);
            }
        }

        if (!record.isValid())
            record.validate();
    }

    private static void disableTexturing(TextureUnitRecord unitRecord,
            TextureStateRecord record, int unit, Type exceptedType) {
        if (exceptedType != Type.TwoDimensional) {
            if (!unitRecord.isValid()
                    || unitRecord.enabled[Type.TwoDimensional.ordinal()]) {
                // Check we are in the right unit
                checkAndSetUnit(unit, record);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                unitRecord.enabled[Type.TwoDimensional.ordinal()] = false;
            }
        }

        if (exceptedType != Type.OneDimensional) {
            if (!unitRecord.isValid()
                    || unitRecord.enabled[Type.OneDimensional.ordinal()]) {
                // Check we are in the right unit
                checkAndSetUnit(unit, record);
                GL11.glDisable(GL11.GL_TEXTURE_1D);
                unitRecord.enabled[Type.OneDimensional.ordinal()] = false;
            }
        }

        if (supportsTexture3D && exceptedType != Type.ThreeDimensional) {
            if (!unitRecord.isValid()
                    || unitRecord.enabled[Type.ThreeDimensional.ordinal()]) {
                // Check we are in the right unit
                checkAndSetUnit(unit, record);
                GL11.glDisable(GL12.GL_TEXTURE_3D);
                unitRecord.enabled[Type.ThreeDimensional.ordinal()] = false;
            }
        }

        if (supportsTextureCubeMap && exceptedType != Type.CubeMap) {
            if (!unitRecord.isValid()
                    || unitRecord.enabled[Type.CubeMap.ordinal()]) {
                // Check we are in the right unit
                checkAndSetUnit(unit, record);
                GL11.glDisable(ARBTextureCubeMap.GL_TEXTURE_CUBE_MAP_ARB);
                unitRecord.enabled[Type.CubeMap.ordinal()] = false;
            }
        }

    }

    private static void disableTexturing(TextureUnitRecord unitRecord,
            TextureStateRecord record, int unit) {
        if (!unitRecord.isValid()
                || unitRecord.enabled[Type.TwoDimensional.ordinal()]) {
            // Check we are in the right unit
            checkAndSetUnit(unit, record);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            unitRecord.enabled[Type.TwoDimensional.ordinal()] = false;
        }

        if (!unitRecord.isValid()
                || unitRecord.enabled[Type.OneDimensional.ordinal()]) {
            // Check we are in the right unit
            checkAndSetUnit(unit, record);
            GL11.glDisable(GL11.GL_TEXTURE_1D);
            unitRecord.enabled[Type.OneDimensional.ordinal()] = false;
        }

        if (supportsTexture3D) {
            if (!unitRecord.isValid()
                    || unitRecord.enabled[Type.ThreeDimensional.ordinal()]) {
                // Check we are in the right unit
                checkAndSetUnit(unit, record);
                GL11.glDisable(GL12.GL_TEXTURE_3D);
                unitRecord.enabled[Type.ThreeDimensional.ordinal()] = false;
            }
        }

        if (supportsTextureCubeMap) {
            if (!unitRecord.isValid()
                    || unitRecord.enabled[Type.CubeMap.ordinal()]) {
                // Check we are in the right unit
                checkAndSetUnit(unit, record);
                GL11.glDisable(ARBTextureCubeMap.GL_TEXTURE_CUBE_MAP_ARB);
                unitRecord.enabled[Type.CubeMap.ordinal()] = false;
            }
        }

    }

    public static void applyCombineFactors(Texture texture,
            TextureUnitRecord unitRecord, int unit, TextureStateRecord record) {
        // check that this is a valid fixed function unit. glTexEnv is only
        // supported for unit < GL_MAX_TEXTURE_UNITS
        if (unit >= numFixedTexUnits) {
            return;
        }

        // first thing's first... if we are doing dot3 and don't
        // support it, disable this texture.
        boolean checked = false;
        if (!supportsEnvDot3
                && (texture.getCombineFuncRGB() == CombinerFunctionRGB.Dot3RGB || texture
                        .getCombineFuncRGB() == CombinerFunctionRGB.Dot3RGBA)) {

            // disable
            disableTexturing(unitRecord, record, unit);

            // No need to continue
            return;
        }

        // Okay, now let's set our scales if we need to:
        // First RGB Combine scale
        if (!unitRecord.isValid()
                || unitRecord.envRGBScale != texture.getCombineScaleRGB()) {
            if (!checked) {
                checkAndSetUnit(unit, record);
                checked = true;
            }
            GL11.glTexEnvf(GL11.GL_TEXTURE_ENV,
                    ARBTextureEnvCombine.GL_RGB_SCALE_ARB, texture
                            .getCombineScaleRGB().floatValue());
            unitRecord.envRGBScale = texture.getCombineScaleRGB();
        }
        // Then Alpha Combine scale
        if (!unitRecord.isValid()
                || unitRecord.envAlphaScale != texture.getCombineScaleAlpha()) {
            if (!checked) {
                checkAndSetUnit(unit, record);
                checked = true;
            }
            GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_ALPHA_SCALE, texture
                    .getCombineScaleAlpha().floatValue());
            unitRecord.envAlphaScale = texture.getCombineScaleAlpha();
        }

        // Time to set the RGB combines
        CombinerFunctionRGB rgbCombineFunc = texture.getCombineFuncRGB();
        if (!unitRecord.isValid()
                || unitRecord.rgbCombineFunc != rgbCombineFunc) {
            if (!checked) {
                checkAndSetUnit(unit, record);
                checked = true;
            }
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,
                    ARBTextureEnvCombine.GL_COMBINE_RGB_ARB, TextureStateRecord
                            .getGLCombineFuncRGB(rgbCombineFunc));
            unitRecord.rgbCombineFunc = rgbCombineFunc;
        }

        CombinerSource combSrcRGB = texture.getCombineSrc0RGB();
        if (!unitRecord.isValid() || unitRecord.combSrcRGB0 != combSrcRGB) {
            if (!checked) {
                checkAndSetUnit(unit, record);
                checked = true;
            }
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,
                    ARBTextureEnvCombine.GL_SOURCE0_RGB_ARB, TextureStateRecord
                            .getGLCombineSrc(combSrcRGB));
            unitRecord.combSrcRGB0 = combSrcRGB;
        }

        CombinerOperandRGB combOpRGB = texture.getCombineOp0RGB();
        if (!unitRecord.isValid() || unitRecord.combOpRGB0 != combOpRGB) {
            if (!checked) {
                checkAndSetUnit(unit, record);
                checked = true;
            }
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,
                    ARBTextureEnvCombine.GL_OPERAND0_RGB_ARB,
                    TextureStateRecord.getGLCombineOpRGB(combOpRGB));
            unitRecord.combOpRGB0 = combOpRGB;
        }

        // We only need to do Arg1 or Arg2 if we aren't in Replace mode
        if (rgbCombineFunc != CombinerFunctionRGB.Replace) {

            combSrcRGB = texture.getCombineSrc1RGB();
            if (!unitRecord.isValid() || unitRecord.combSrcRGB1 != combSrcRGB) {
                if (!checked) {
                    checkAndSetUnit(unit, record);
                    checked = true;
                }
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,
                        ARBTextureEnvCombine.GL_SOURCE1_RGB_ARB,
                        TextureStateRecord.getGLCombineSrc(combSrcRGB));
                unitRecord.combSrcRGB1 = combSrcRGB;
            }

            combOpRGB = texture.getCombineOp1RGB();
            if (!unitRecord.isValid() || unitRecord.combOpRGB1 != combOpRGB) {
                if (!checked) {
                    checkAndSetUnit(unit, record);
                    checked = true;
                }
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,
                        ARBTextureEnvCombine.GL_OPERAND1_RGB_ARB,
                        TextureStateRecord.getGLCombineOpRGB(combOpRGB));
                unitRecord.combOpRGB1 = combOpRGB;
            }

            // We only need to do Arg2 if we are in Interpolate mode
            if (rgbCombineFunc == CombinerFunctionRGB.Interpolate) {

                combSrcRGB = texture.getCombineSrc2RGB();
                if (!unitRecord.isValid()
                        || unitRecord.combSrcRGB2 != combSrcRGB) {
                    if (!checked) {
                        checkAndSetUnit(unit, record);
                        checked = true;
                    }
                    GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,
                            ARBTextureEnvCombine.GL_SOURCE2_RGB_ARB,
                            TextureStateRecord.getGLCombineSrc(combSrcRGB));
                    unitRecord.combSrcRGB2 = combSrcRGB;
                }

                combOpRGB = texture.getCombineOp2RGB();
                if (!unitRecord.isValid() || unitRecord.combOpRGB2 != combOpRGB) {
                    if (!checked) {
                        checkAndSetUnit(unit, record);
                        checked = true;
                    }
                    GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,
                            ARBTextureEnvCombine.GL_OPERAND2_RGB_ARB,
                            TextureStateRecord.getGLCombineOpRGB(combOpRGB));
                    unitRecord.combOpRGB2 = combOpRGB;
                }

            }
        }

        // Now Alpha combines
        CombinerFunctionAlpha alphaCombineFunc = texture.getCombineFuncAlpha();
        if (!unitRecord.isValid()
                || unitRecord.alphaCombineFunc != alphaCombineFunc) {
            if (!checked) {
                checkAndSetUnit(unit, record);
                checked = true;
            }
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,
                    ARBTextureEnvCombine.GL_COMBINE_ALPHA_ARB,
                    TextureStateRecord.getGLCombineFuncAlpha(alphaCombineFunc));
            unitRecord.alphaCombineFunc = alphaCombineFunc;
        }

        CombinerSource combSrcAlpha = texture.getCombineSrc0Alpha();
        if (!unitRecord.isValid() || unitRecord.combSrcAlpha0 != combSrcAlpha) {
            if (!checked) {
                checkAndSetUnit(unit, record);
                checked = true;
            }
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,
                    ARBTextureEnvCombine.GL_SOURCE0_ALPHA_ARB,
                    TextureStateRecord.getGLCombineSrc(combSrcAlpha));
            unitRecord.combSrcAlpha0 = combSrcAlpha;
        }

        CombinerOperandAlpha combOpAlpha = texture.getCombineOp0Alpha();
        if (!unitRecord.isValid() || unitRecord.combOpAlpha0 != combOpAlpha) {
            if (!checked) {
                checkAndSetUnit(unit, record);
                checked = true;
            }
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,
                    ARBTextureEnvCombine.GL_OPERAND0_ALPHA_ARB,
                    TextureStateRecord.getGLCombineOpAlpha(combOpAlpha));
            unitRecord.combOpAlpha0 = combOpAlpha;
        }

        // We only need to do Arg1 or Arg2 if we aren't in Replace mode
        if (alphaCombineFunc != CombinerFunctionAlpha.Replace) {

            combSrcAlpha = texture.getCombineSrc1Alpha();
            if (!unitRecord.isValid()
                    || unitRecord.combSrcAlpha1 != combSrcAlpha) {
                if (!checked) {
                    checkAndSetUnit(unit, record);
                    checked = true;
                }
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,
                        ARBTextureEnvCombine.GL_SOURCE1_ALPHA_ARB,
                        TextureStateRecord.getGLCombineSrc(combSrcAlpha));
                unitRecord.combSrcAlpha1 = combSrcAlpha;
            }

            combOpAlpha = texture.getCombineOp1Alpha();
            if (!unitRecord.isValid() || unitRecord.combOpAlpha1 != combOpAlpha) {
                if (!checked) {
                    checkAndSetUnit(unit, record);
                    checked = true;
                }
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,
                        ARBTextureEnvCombine.GL_OPERAND1_ALPHA_ARB,
                        TextureStateRecord.getGLCombineOpAlpha(combOpAlpha));
                unitRecord.combOpAlpha1 = combOpAlpha;
            }

            // We only need to do Arg2 if we are in Interpolate mode
            if (alphaCombineFunc == CombinerFunctionAlpha.Interpolate) {

                combSrcAlpha = texture.getCombineSrc2Alpha();
                if (!unitRecord.isValid()
                        || unitRecord.combSrcAlpha2 != combSrcAlpha) {
                    if (!checked) {
                        checkAndSetUnit(unit, record);
                        checked = true;
                    }
                    GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,
                            ARBTextureEnvCombine.GL_SOURCE2_ALPHA_ARB,
                            TextureStateRecord.getGLCombineSrc(combSrcAlpha));
                    unitRecord.combSrcAlpha2 = combSrcAlpha;
                }

                combOpAlpha = texture.getCombineOp2Alpha();
                if (!unitRecord.isValid()
                        || unitRecord.combOpAlpha2 != combOpAlpha) {
                    if (!checked) {
                        checkAndSetUnit(unit, record);
                        checked = true;
                    }
                    GL11
                            .glTexEnvi(GL11.GL_TEXTURE_ENV,
                                    ARBTextureEnvCombine.GL_OPERAND2_ALPHA_ARB,
                                    TextureStateRecord
                                            .getGLCombineOpAlpha(combOpAlpha));
                    unitRecord.combOpAlpha2 = combOpAlpha;
                }
            }
        }
    }

    public static void applyEnvMode(ApplyMode mode,
            TextureUnitRecord unitRecord, int unit, TextureStateRecord record) {
        if (!unitRecord.isValid() || unitRecord.envMode != mode) {
            checkAndSetUnit(unit, record);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE,
                    TextureStateRecord.getGLEnvMode(mode));
            unitRecord.envMode = mode;
        }
    }

    public static void applyBlendColor(Texture texture,
            TextureUnitRecord unitRecord, int unit, TextureStateRecord record) {
        ColorRGBA texBlend = texture.getBlendColor();
        if (texBlend == null)
            texBlend = TextureRecord.defaultColor;
        if (!unitRecord.isValid() || unitRecord.blendColor.r != texBlend.r
                || unitRecord.blendColor.g != texBlend.g
                || unitRecord.blendColor.b != texBlend.b
                || unitRecord.blendColor.a != texBlend.a) {
            checkAndSetUnit(unit, record);
            TextureRecord.colorBuffer.clear();
            TextureRecord.colorBuffer.put(texBlend.r).put(texBlend.g).put(
                    texBlend.b).put(texBlend.a);
            TextureRecord.colorBuffer.rewind();
            GL11.glTexEnv(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_COLOR,
                    TextureRecord.colorBuffer);
            unitRecord.blendColor.set(texBlend);
        }
    }

    public static void applyBorderColor(Texture texture,
            TextureRecord texRecord, int unit, TextureStateRecord record) {
        ColorRGBA texBorder = texture.getBorderColor();
        if (texBorder == null)
            texBorder = TextureRecord.defaultColor;
        if (!texRecord.isValid() || texRecord.borderColor.r != texBorder.r
                || texRecord.borderColor.g != texBorder.g
                || texRecord.borderColor.b != texBorder.b
                || texRecord.borderColor.a != texBorder.a) {
            TextureRecord.colorBuffer.clear();
            TextureRecord.colorBuffer.put(texBorder.r).put(texBorder.g).put(
                    texBorder.b).put(texBorder.a);
            TextureRecord.colorBuffer.rewind();
            GL11.glTexParameter(getGLType(texture.getType()),
                    GL11.GL_TEXTURE_BORDER_COLOR, TextureRecord.colorBuffer);
            texRecord.borderColor.set(texBorder);
        }
    }

    public static void applyTextureTransforms(Texture texture, int unit,
            TextureStateRecord record) {
        boolean needsReset = !record.units[unit].identityMatrix;

        // Should we load a base matrix?
        boolean doMatrix = (texture.getMatrix() != null && !texture.getMatrix()
                .isIdentity());

        // Should we apply transforms?
        boolean doTrans = texture.getTranslation() != null
                && (texture.getTranslation().x != 0
                        || texture.getTranslation().y != 0 || texture
                        .getTranslation().z != 0);
        boolean doRot = texture.getRotation() != null
                && !texture.getRotation().isIdentity();
        boolean doScale = texture.getScale() != null
                && (texture.getScale().x != 1 || texture.getScale().y != 1 || texture
                        .getScale().z != 1);

        // Now do them.
        RendererRecord matRecord = (RendererRecord) DisplaySystem
                .getDisplaySystem().getCurrentContext().getRendererRecord();
        if (doMatrix || doTrans || doRot || doScale) {
            checkAndSetUnit(unit, record);
            matRecord.switchMode(GL11.GL_TEXTURE);
            if (doMatrix) {
                record.tmp_matrixBuffer.rewind();
                texture.getMatrix().fillFloatBuffer(record.tmp_matrixBuffer,
                        true);
                record.tmp_matrixBuffer.rewind();
                GL11.glLoadMatrix(record.tmp_matrixBuffer);
            } else {
                GL11.glLoadIdentity();
            }
            if (doTrans) {
                GL11.glTranslatef(texture.getTranslation().x, texture
                        .getTranslation().y, texture.getTranslation().z);
            }
            if (doRot) {
                Vector3f vRot = record.tmp_rotation1;
                float rot = texture.getRotation().toAngleAxis(vRot)
                        * FastMath.RAD_TO_DEG;
                GL11.glRotatef(rot, vRot.x, vRot.y, vRot.z);
            }
            if (doScale)
                GL11.glScalef(texture.getScale().x, texture.getScale().y,
                        texture.getScale().z);

            record.units[unit].identityMatrix = false;
        } else if (needsReset) {
            checkAndSetUnit(unit, record);
            matRecord.switchMode(GL11.GL_TEXTURE);
            GL11.glLoadIdentity();
            record.units[unit].identityMatrix = true;
        }
        // Switch back to the modelview matrix for further operations
        matRecord.switchMode(GL11.GL_MODELVIEW);
    }

    public static void applyTexCoordGeneration(Texture texture,
            TextureUnitRecord unitRecord, int unit, TextureStateRecord record) {

        switch (texture.getEnvironmentalMapMode()) {
            case None:
                // No coordinate generation
                if (!unitRecord.isValid() || unitRecord.textureGenQ) {
                    checkAndSetUnit(unit, record);
                    GL11.glDisable(GL11.GL_TEXTURE_GEN_Q);
                    unitRecord.textureGenQ = false;
                }
                if (!unitRecord.isValid() || unitRecord.textureGenR) {
                    checkAndSetUnit(unit, record);
                    GL11.glDisable(GL11.GL_TEXTURE_GEN_R);
                    unitRecord.textureGenR = false;
                }
                if (!unitRecord.isValid() || unitRecord.textureGenS) {
                    checkAndSetUnit(unit, record);
                    GL11.glDisable(GL11.GL_TEXTURE_GEN_S);
                    unitRecord.textureGenS = false;
                }
                if (!unitRecord.isValid() || unitRecord.textureGenT) {
                    checkAndSetUnit(unit, record);
                    GL11.glDisable(GL11.GL_TEXTURE_GEN_T);
                    unitRecord.textureGenT = false;
                }
                break;
            case SphereMap:
                // generate spherical texture coordinates
                if (!unitRecord.isValid()
                        || unitRecord.textureGenSMode != GL11.GL_SPHERE_MAP) {
                    checkAndSetUnit(unit, record);
                    GL11.glTexGeni(GL11.GL_S, GL11.GL_TEXTURE_GEN_MODE,
                            GL11.GL_SPHERE_MAP);
                    unitRecord.textureGenSMode = GL11.GL_SPHERE_MAP;
                }

                if (!unitRecord.isValid()
                        || unitRecord.textureGenTMode != GL11.GL_SPHERE_MAP) {
                    checkAndSetUnit(unit, record);
                    GL11.glTexGeni(GL11.GL_T, GL11.GL_TEXTURE_GEN_MODE,
                            GL11.GL_SPHERE_MAP);
                    unitRecord.textureGenTMode = GL11.GL_SPHERE_MAP;
                }

                if (!unitRecord.isValid() || unitRecord.textureGenQ) {
                    checkAndSetUnit(unit, record);
                    GL11.glDisable(GL11.GL_TEXTURE_GEN_Q);
                    unitRecord.textureGenQ = false;
                }
                if (!unitRecord.isValid() || unitRecord.textureGenR) {
                    checkAndSetUnit(unit, record);
                    GL11.glDisable(GL11.GL_TEXTURE_GEN_R);
                    unitRecord.textureGenR = false;
                }
                if (!unitRecord.isValid() || !unitRecord.textureGenS) {
                    checkAndSetUnit(unit, record);
                    GL11.glEnable(GL11.GL_TEXTURE_GEN_S);
                    unitRecord.textureGenS = true;
                }
                if (!unitRecord.isValid() || !unitRecord.textureGenT) {
                    checkAndSetUnit(unit, record);
                    GL11.glEnable(GL11.GL_TEXTURE_GEN_T);
                    unitRecord.textureGenT = true;
                }
                break;
            case NormalMap:
                // generate spherical texture coordinates
                if (!unitRecord.isValid()
                        || unitRecord.textureGenSMode != ARBTextureCubeMap.GL_NORMAL_MAP_ARB) {
                    checkAndSetUnit(unit, record);
                    GL11.glTexGeni(GL11.GL_S, GL11.GL_TEXTURE_GEN_MODE,
                            ARBTextureCubeMap.GL_NORMAL_MAP_ARB);
                    unitRecord.textureGenSMode = ARBTextureCubeMap.GL_NORMAL_MAP_ARB;
                }

                if (!unitRecord.isValid()
                        || unitRecord.textureGenTMode != ARBTextureCubeMap.GL_NORMAL_MAP_ARB) {
                    checkAndSetUnit(unit, record);
                    GL11.glTexGeni(GL11.GL_T, GL11.GL_TEXTURE_GEN_MODE,
                            ARBTextureCubeMap.GL_NORMAL_MAP_ARB);
                    unitRecord.textureGenTMode = ARBTextureCubeMap.GL_NORMAL_MAP_ARB;
                }

                if (!unitRecord.isValid()
                        || unitRecord.textureGenRMode != ARBTextureCubeMap.GL_NORMAL_MAP_ARB) {
                    checkAndSetUnit(unit, record);
                    GL11.glTexGeni(GL11.GL_R, GL11.GL_TEXTURE_GEN_MODE,
                            ARBTextureCubeMap.GL_NORMAL_MAP_ARB);
                    unitRecord.textureGenRMode = ARBTextureCubeMap.GL_NORMAL_MAP_ARB;
                }

                if (!unitRecord.isValid() || unitRecord.textureGenQ) {
                    checkAndSetUnit(unit, record);
                    GL11.glDisable(GL11.GL_TEXTURE_GEN_Q);
                    unitRecord.textureGenQ = false;
                }
                if (!unitRecord.isValid() || !unitRecord.textureGenR) {
                    checkAndSetUnit(unit, record);
                    GL11.glEnable(GL11.GL_TEXTURE_GEN_R);
                    unitRecord.textureGenR = true;
                }
                if (!unitRecord.isValid() || !unitRecord.textureGenS) {
                    checkAndSetUnit(unit, record);
                    GL11.glEnable(GL11.GL_TEXTURE_GEN_S);
                    unitRecord.textureGenS = true;
                }
                if (!unitRecord.isValid() || !unitRecord.textureGenT) {
                    checkAndSetUnit(unit, record);
                    GL11.glEnable(GL11.GL_TEXTURE_GEN_T);
                    unitRecord.textureGenT = true;
                }
                break;
            case ReflectionMap:
                // generate spherical texture coordinates
                if (!unitRecord.isValid()
                        || unitRecord.textureGenSMode != ARBTextureCubeMap.GL_REFLECTION_MAP_ARB) {
                    checkAndSetUnit(unit, record);
                    GL11.glTexGeni(GL11.GL_S, GL11.GL_TEXTURE_GEN_MODE,
                            ARBTextureCubeMap.GL_REFLECTION_MAP_ARB);
                    unitRecord.textureGenSMode = ARBTextureCubeMap.GL_REFLECTION_MAP_ARB;
                }

                if (!unitRecord.isValid()
                        || unitRecord.textureGenTMode != ARBTextureCubeMap.GL_REFLECTION_MAP_ARB) {
                    checkAndSetUnit(unit, record);
                    GL11.glTexGeni(GL11.GL_T, GL11.GL_TEXTURE_GEN_MODE,
                            ARBTextureCubeMap.GL_REFLECTION_MAP_ARB);
                    unitRecord.textureGenTMode = ARBTextureCubeMap.GL_REFLECTION_MAP_ARB;
                }
                
                if (!unitRecord.isValid()
                        || unitRecord.textureGenRMode != ARBTextureCubeMap.GL_REFLECTION_MAP_ARB) {
                    checkAndSetUnit(unit, record);
                    GL11.glTexGeni(GL11.GL_R, GL11.GL_TEXTURE_GEN_MODE,
                            ARBTextureCubeMap.GL_REFLECTION_MAP_ARB);
                    unitRecord.textureGenRMode = ARBTextureCubeMap.GL_REFLECTION_MAP_ARB;
                }

                if (!unitRecord.isValid() || unitRecord.textureGenQ) {
                    checkAndSetUnit(unit, record);
                    GL11.glDisable(GL11.GL_TEXTURE_GEN_Q);
                    unitRecord.textureGenQ = false;
                }
                if (!unitRecord.isValid() || !unitRecord.textureGenR) {
                    checkAndSetUnit(unit, record);
                    GL11.glEnable(GL11.GL_TEXTURE_GEN_R);
                    unitRecord.textureGenR = true;
                }
                if (!unitRecord.isValid() || !unitRecord.textureGenS) {
                    checkAndSetUnit(unit, record);
                    GL11.glEnable(GL11.GL_TEXTURE_GEN_S);
                    unitRecord.textureGenS = true;
                }
                if (!unitRecord.isValid() || !unitRecord.textureGenT) {
                    checkAndSetUnit(unit, record);
                    GL11.glEnable(GL11.GL_TEXTURE_GEN_T);
                    unitRecord.textureGenT = true;
                }
                break;
            case EyeLinear:
                // do here because we don't check planes 
                checkAndSetUnit(unit, record);
                
                // generate eye linear texture coordinates
                if (!unitRecord.isValid()
                        || unitRecord.textureGenQMode != GL11.GL_EYE_LINEAR) {
                    GL11.glTexGeni(GL11.GL_Q, GL11.GL_TEXTURE_GEN_MODE,
                            GL11.GL_EYE_LINEAR);
                    unitRecord.textureGenQMode = GL11.GL_EYE_LINEAR;
                }

                if (!unitRecord.isValid()
                        || unitRecord.textureGenRMode != GL11.GL_EYE_LINEAR) {
                    GL11.glTexGeni(GL11.GL_R, GL11.GL_TEXTURE_GEN_MODE,
                            GL11.GL_EYE_LINEAR);
                    unitRecord.textureGenRMode = GL11.GL_EYE_LINEAR;
                }

                if (!unitRecord.isValid()
                        || unitRecord.textureGenSMode != GL11.GL_EYE_LINEAR) {
                    GL11.glTexGeni(GL11.GL_S, GL11.GL_TEXTURE_GEN_MODE,
                            GL11.GL_EYE_LINEAR);
                    unitRecord.textureGenSMode = GL11.GL_EYE_LINEAR;
                }

                if (!unitRecord.isValid()
                        || unitRecord.textureGenTMode != GL11.GL_EYE_LINEAR) {
                    GL11.glTexGeni(GL11.GL_T, GL11.GL_TEXTURE_GEN_MODE,
                            GL11.GL_EYE_LINEAR);
                    unitRecord.textureGenTMode = GL11.GL_EYE_LINEAR;
                }

                record.eyePlaneS.rewind();
                GL11.glTexGen(GL11.GL_S, GL11.GL_EYE_PLANE, record.eyePlaneS);
                record.eyePlaneT.rewind();
                GL11.glTexGen(GL11.GL_T, GL11.GL_EYE_PLANE, record.eyePlaneT);
                record.eyePlaneR.rewind();
                GL11.glTexGen(GL11.GL_R, GL11.GL_EYE_PLANE, record.eyePlaneR);
                record.eyePlaneQ.rewind();
                GL11.glTexGen(GL11.GL_Q, GL11.GL_EYE_PLANE, record.eyePlaneQ);

                if (!unitRecord.isValid() || !unitRecord.textureGenQ) {
                    GL11.glEnable(GL11.GL_TEXTURE_GEN_Q);
                    unitRecord.textureGenQ = true;
                }
                if (!unitRecord.isValid() || !unitRecord.textureGenR) {
                    GL11.glEnable(GL11.GL_TEXTURE_GEN_R);
                    unitRecord.textureGenR = true;
                }
                if (!unitRecord.isValid() || !unitRecord.textureGenS) {
                    GL11.glEnable(GL11.GL_TEXTURE_GEN_S);
                    unitRecord.textureGenS = true;
                }
                if (!unitRecord.isValid() || !unitRecord.textureGenT) {
                    GL11.glEnable(GL11.GL_TEXTURE_GEN_T);
                    unitRecord.textureGenT = true;
                }
                break;
            case ObjectLinear:
                // do here because we don't check planes 
                checkAndSetUnit(unit, record);
                
                // generate object linear texture coordinates
                if (!unitRecord.isValid()
                        || unitRecord.textureGenQMode != GL11.GL_OBJECT_LINEAR) {
                    GL11.glTexGeni(GL11.GL_Q, GL11.GL_TEXTURE_GEN_MODE,
                            GL11.GL_OBJECT_LINEAR);
                    unitRecord.textureGenQMode = GL11.GL_OBJECT_LINEAR;
                }

                if (!unitRecord.isValid()
                        || unitRecord.textureGenRMode != GL11.GL_OBJECT_LINEAR) {
                    GL11.glTexGeni(GL11.GL_R, GL11.GL_TEXTURE_GEN_MODE,
                            GL11.GL_OBJECT_LINEAR);
                    unitRecord.textureGenRMode = GL11.GL_OBJECT_LINEAR;
                }

                if (!unitRecord.isValid()
                        || unitRecord.textureGenSMode != GL11.GL_OBJECT_LINEAR) {
                    GL11.glTexGeni(GL11.GL_S, GL11.GL_TEXTURE_GEN_MODE,
                            GL11.GL_OBJECT_LINEAR);
                    unitRecord.textureGenSMode = GL11.GL_OBJECT_LINEAR;
                }

                if (!unitRecord.isValid()
                        || unitRecord.textureGenTMode != GL11.GL_OBJECT_LINEAR) {
                    GL11.glTexGeni(GL11.GL_T, GL11.GL_TEXTURE_GEN_MODE,
                            GL11.GL_OBJECT_LINEAR);
                    unitRecord.textureGenTMode = GL11.GL_OBJECT_LINEAR;
                }

                record.eyePlaneS.rewind();
                GL11
                        .glTexGen(GL11.GL_S, GL11.GL_OBJECT_PLANE,
                                record.eyePlaneS);
                record.eyePlaneT.rewind();
                GL11
                        .glTexGen(GL11.GL_T, GL11.GL_OBJECT_PLANE,
                                record.eyePlaneT);
                record.eyePlaneR.rewind();
                GL11
                        .glTexGen(GL11.GL_R, GL11.GL_OBJECT_PLANE,
                                record.eyePlaneR);
                record.eyePlaneQ.rewind();
                GL11
                        .glTexGen(GL11.GL_Q, GL11.GL_OBJECT_PLANE,
                                record.eyePlaneQ);

                if (!unitRecord.isValid() || !unitRecord.textureGenQ) {
                    GL11.glEnable(GL11.GL_TEXTURE_GEN_Q);
                    unitRecord.textureGenQ = true;
                }
                if (!unitRecord.isValid() || !unitRecord.textureGenR) {
                    GL11.glEnable(GL11.GL_TEXTURE_GEN_R);
                    unitRecord.textureGenR = true;
                }
                if (!unitRecord.isValid() || !unitRecord.textureGenS) {
                    GL11.glEnable(GL11.GL_TEXTURE_GEN_S);
                    unitRecord.textureGenS = true;
                }
                if (!unitRecord.isValid() || !unitRecord.textureGenT) {
                    GL11.glEnable(GL11.GL_TEXTURE_GEN_T);
                    unitRecord.textureGenT = true;
                }
                break;
        }
    }

    // If we support multtexturing, specify the unit we are affecting.
    public static void checkAndSetUnit(int unit, TextureStateRecord record) {
        // No need to worry about valid record, since invalidate sets record's
        // currentUnit to -1.
        if (record.currentUnit != unit) {
            if (unit >= numTotalTexUnits || !supportsMultiTexture || unit < 0) {
                // ignore this request as it is not valid for the user's hardware.
                return;
            }
            ARBMultitexture.glActiveTextureARB(ARBMultitexture.GL_TEXTURE0_ARB
                    + unit);
            record.currentUnit = unit;
        }
    }

    /**
     * Check if the filter settings of this particular texture have been changed
     * and apply as needed.
     * 
     * @param texture
     *            our texture object
     * @param texRecord
     *            our record of the last state of the texture in gl
     * @param record
     */
    public static void applyShadow(Texture texture, TextureRecord texRecord,
            int unit, TextureStateRecord record) {
        Type type = texture.getType();

        if (supportsDepthTexture) {
	        int depthMode = TextureStateRecord.getGLDepthTextureMode(texture.getDepthMode());
	        // set up magnification filter
	        if (!texRecord.isValid() || texRecord.depthTextureMode != depthMode) {
	            checkAndSetUnit(unit, record);
	            GL11.glTexParameteri(getGLType(type), ARBDepthTexture.GL_DEPTH_TEXTURE_MODE_ARB,
	            		depthMode);
	            texRecord.depthTextureMode = depthMode;
	        }
        }
        
        if (supportsShadow) {
	        int depthCompareMode = TextureStateRecord.getGLDepthTextureCompareMode(texture.getDepthCompareMode());
	        // set up magnification filter
	        if (!texRecord.isValid() || texRecord.depthTextureFunc != depthCompareMode) {
	            checkAndSetUnit(unit, record);
	            GL11.glTexParameteri(getGLType(type), ARBShadow.GL_TEXTURE_COMPARE_MODE_ARB,
	            		depthCompareMode);
	            texRecord.depthTextureFunc = depthCompareMode;
	        }
	        
	        int depthCompareFunc = TextureStateRecord.getGLDepthTextureCompareFunc(texture.getDepthCompareFunc());
	        // set up magnification filter
	        if (!texRecord.isValid() || texRecord.depthTextureFunc != depthCompareFunc) {
	            checkAndSetUnit(unit, record);
	            GL11.glTexParameteri(getGLType(type), ARBShadow.GL_TEXTURE_COMPARE_FUNC_ARB,
	            		depthCompareFunc);
	            texRecord.depthTextureFunc = depthCompareFunc;
	        }
        }
    }
    /**
     * Check if the filter settings of this particular texture have been changed
     * and apply as needed.
     * 
     * @param texture
     *            our texture object
     * @param texRecord
     *            our record of the last state of the texture in gl
     * @param record
     */
    public static void applyFilter(Texture texture, TextureRecord texRecord,
            int unit, TextureStateRecord record) {
        Type type = texture.getType();

        int magFilter = TextureStateRecord.getGLMagFilter(texture
                .getMagnificationFilter());
        // set up magnification filter
        if (!texRecord.isValid() || texRecord.magFilter != magFilter) {
            checkAndSetUnit(unit, record);
            GL11.glTexParameteri(getGLType(type), GL11.GL_TEXTURE_MAG_FILTER,
                    magFilter);
            texRecord.magFilter = magFilter;
        }

        int minFilter = TextureStateRecord.getGLMinFilter(texture
                .getMinificationFilter());
        // set up mipmap filter
        if (!texRecord.isValid() || texRecord.minFilter != minFilter) {
            checkAndSetUnit(unit, record);
            GL11.glTexParameteri(getGLType(type), GL11.GL_TEXTURE_MIN_FILTER,
                    minFilter);
            texRecord.minFilter = minFilter;
        }

        // set up aniso filter
        if (supportsAniso) {
            float aniso = texture.getAnisotropicFilterPercent()
                    * (maxAnisotropic - 1.0f);
            aniso += 1.0f;
            if (!texRecord.isValid() || texRecord.anisoLevel != aniso) {
                checkAndSetUnit(unit, record);
                GL11
                        .glTexParameterf(
                                getGLType(type),
                                EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT,
                                aniso);
                texRecord.anisoLevel = aniso;
            }
        }
    }

    /**
     * Check if the wrap mode of this particular texture has been changed and
     * apply as needed.
     * 
     * @param texture
     *            our texture object
     * @param texRecord
     *            our record of the last state of the unit in gl
     * @param record
     */
    public static void applyWrap(Texture3D texture, TextureRecord texRecord,
            int unit, TextureStateRecord record) {
        if (!supportsTexture3D)
            return;

        int wrapS = record.getGLWrap(texture.getWrap(WrapAxis.S));
        int wrapT = record.getGLWrap(texture.getWrap(WrapAxis.T));
        int wrapR = record.getGLWrap(texture.getWrap(WrapAxis.R));

        if (!texRecord.isValid() || texRecord.wrapS != wrapS) {
            checkAndSetUnit(unit, record);
            GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_WRAP_S,
                    wrapS);
            texRecord.wrapS = wrapS;
        }
        if (!texRecord.isValid() || texRecord.wrapT != wrapT) {
            checkAndSetUnit(unit, record);
            GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_WRAP_T,
                    wrapT);
            texRecord.wrapT = wrapT;
        }
        if (!texRecord.isValid() || texRecord.wrapR != wrapR) {
            checkAndSetUnit(unit, record);
            GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL12.GL_TEXTURE_WRAP_R,
                    wrapR);
            texRecord.wrapR = wrapR;
        }

    }

    /**
     * Check if the wrap mode of this particular texture has been changed and
     * apply as needed.
     * 
     * @param texture
     *            our texture object
     * @param texRecord
     *            our record of the last state of the unit in gl
     * @param record
     */
    public static void applyWrap(Texture1D texture, TextureRecord texRecord,
            int unit, TextureStateRecord record) {
        int wrapS = record.getGLWrap(texture.getWrap(WrapAxis.S));

        if (!texRecord.isValid() || texRecord.wrapS != wrapS) {
            checkAndSetUnit(unit, record);
            GL11.glTexParameteri(GL11.GL_TEXTURE_1D, GL11.GL_TEXTURE_WRAP_S,
                    wrapS);
            texRecord.wrapS = wrapS;
        }
    }

    /**
     * Check if the wrap mode of this particular texture has been changed and
     * apply as needed.
     * 
     * @param texture
     *            our texture object
     * @param texRecord
     *            our record of the last state of the unit in gl
     * @param record
     */
    public static void applyWrap(Texture texture, TextureRecord texRecord,
            int unit, TextureStateRecord record) {
        if (texture instanceof Texture2D) {
            applyWrap((Texture2D) texture, texRecord, unit, record);
        } else if (texture instanceof Texture1D) {
            applyWrap((Texture1D) texture, texRecord, unit, record);
        } else if (texture instanceof Texture3D) {
            applyWrap((Texture3D) texture, texRecord, unit, record);
        } else if (texture instanceof TextureCubeMap) {
            applyWrap((TextureCubeMap) texture, texRecord, unit, record);
        }
    }

    /**
     * Check if the wrap mode of this particular texture has been changed and
     * apply as needed.
     * 
     * @param texture
     *            our texture object
     * @param texRecord
     *            our record of the last state of the unit in gl
     * @param record
     */
    public static void applyWrap(Texture2D texture, TextureRecord texRecord,
            int unit, TextureStateRecord record) {
        int wrapS = record.getGLWrap(texture.getWrap(WrapAxis.S));
        int wrapT = record.getGLWrap(texture.getWrap(WrapAxis.T));

        if (!texRecord.isValid() || texRecord.wrapS != wrapS) {
            checkAndSetUnit(unit, record);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S,
                    wrapS);
            texRecord.wrapS = wrapS;
        }
        if (!texRecord.isValid() || texRecord.wrapT != wrapT) {
            checkAndSetUnit(unit, record);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T,
                    wrapT);
            texRecord.wrapT = wrapT;
        }

    }

    /**
     * Check if the wrap mode of this particular texture has been changed and
     * apply as needed.
     * 
     * @param cubeMap
     *            our texture object
     * @param texRecord
     *            our record of the last state of the unit in gl
     * @param record
     */
    public static void applyWrap(TextureCubeMap cubeMap,
            TextureRecord texRecord, int unit, TextureStateRecord record) {
        if (!supportsTexture3D)
            return;

        int wrapS = record.getGLWrap(cubeMap.getWrap(WrapAxis.S));
        int wrapT = record.getGLWrap(cubeMap.getWrap(WrapAxis.T));
        int wrapR = record.getGLWrap(cubeMap.getWrap(WrapAxis.R));

        if (!texRecord.isValid() || texRecord.wrapS != wrapS) {
            checkAndSetUnit(unit, record);
            GL11.glTexParameteri(ARBTextureCubeMap.GL_TEXTURE_CUBE_MAP_ARB,
                    GL11.GL_TEXTURE_WRAP_S, wrapS);
            texRecord.wrapS = wrapS;
        }
        if (!texRecord.isValid() || texRecord.wrapT != wrapT) {
            checkAndSetUnit(unit, record);
            GL11.glTexParameteri(ARBTextureCubeMap.GL_TEXTURE_CUBE_MAP_ARB,
                    GL11.GL_TEXTURE_WRAP_T, wrapT);
            texRecord.wrapT = wrapT;
        }
        if (!texRecord.isValid() || texRecord.wrapR != wrapR) {
            checkAndSetUnit(unit, record);
            GL11.glTexParameteri(ARBTextureCubeMap.GL_TEXTURE_CUBE_MAP_ARB,
                    GL12.GL_TEXTURE_WRAP_R, wrapR);
            texRecord.wrapR = wrapR;
        }
    }

    public RenderState extract(Stack<? extends RenderState> stack, Spatial spat) {
        TextureCombineMode mode = spat.getTextureCombineMode();
        if (mode == TextureCombineMode.Replace
                || (mode != TextureCombineMode.Off && stack.size() == 1)) {
            // todo: use dummy state if off?
            return (LWJGLTextureState) stack.peek();
        }

        // accumulate the textures in the stack into a single LightState object
        LWJGLTextureState newTState = new LWJGLTextureState();
        boolean foundEnabled = false;
        Object states[] = stack.toArray();
        switch (mode) {
            case CombineClosest:
            case CombineClosestEnabled:
                for (int iIndex = states.length - 1; iIndex >= 0; iIndex--) {
                    TextureState pkTState = (TextureState) states[iIndex];
                    if (!pkTState.isEnabled()) {
                        if (mode == TextureCombineMode.CombineClosestEnabled)
                            break;

                        continue;
                    }

                    foundEnabled = true;
                    for (int i = 0, max = pkTState.getNumberOfSetTextures(); i < max; i++) {
                        Texture pkText = pkTState.getTexture(i);
                        if (newTState.getTexture(i) == null) {
                            newTState.setTexture(pkText, i);
                        }
                    }
                }
                break;
            case CombineFirst:
                for (int iIndex = 0, max = states.length; iIndex < max; iIndex++) {
                    TextureState pkTState = (TextureState) states[iIndex];
                    if (!pkTState.isEnabled())
                        continue;

                    foundEnabled = true;
                    for (int i = 0; i < numTotalTexUnits; i++) {
                        Texture pkText = pkTState.getTexture(i);
                        if (newTState.getTexture(i) == null) {
                            newTState.setTexture(pkText, i);
                        }
                    }
                }
                break;
            case Off:
                break;
        }
        newTState.setEnabled(foundEnabled);
        return newTState;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.scene.state.TextureState#delete(int)
     */
    public void delete(int unit) {
        if (unit < 0 || unit >= texture.size() || texture.get(unit) == null)
            return;

        // ask for the current state record
        RenderContext<?> context = DisplaySystem.getDisplaySystem().getCurrentContext();
        TextureStateRecord record = (TextureStateRecord) context.getStateRecord(StateType.Texture);

        Texture tex = texture.get(unit);
        int texId = tex.getTextureId();

        IntBuffer id = BufferUtils.createIntBuffer(1);
        id.clear();
        id.put(texId);
        id.rewind();
        tex.setTextureId(0);

        GL11.glDeleteTextures(id);

        // if the texture was currently bound glDeleteTextures reverts the
        // binding to 0
        // however we still have to clear it from currentTexture.
        record.removeTextureRecord(texId);
        idCache[unit] = 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.scene.state.TextureState#deleteAll()
     */
    public void deleteAll() {
        deleteAll(false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.scene.state.TextureState#deleteAll()
     */
    public void deleteAll(boolean removeFromCache) {

        // ask for the current state record
        RenderContext<?> context = DisplaySystem.getDisplaySystem().getCurrentContext();
        TextureStateRecord record = (TextureStateRecord) context.getStateRecord(StateType.Texture);

        IntBuffer id = BufferUtils.createIntBuffer(texture.size());

        for (int i = 0; i < texture.size(); i++) {
            Texture tex = texture.get(i);
            if (tex == null)
                continue;

            if (removeFromCache)
                TextureManager.releaseTexture(tex);
            int texId = tex.getTextureId();
            id.put(texId);
            tex.setTextureId(0);

            // if the texture was currently bound glDeleteTextures reverts the
            // binding to 0
            // however we still have to clear it from currentTexture.
            record.removeTextureRecord(texId);
            idCache[i] = 0;
        }

        // Now delete them all from GL in one fell swoop.
        id.rewind();
        GL11.glDeleteTextures(id);
    }

    public void deleteTextureId(int textureId) {

        // ask for the current state record
        RenderContext<?> context = DisplaySystem.getDisplaySystem().getCurrentContext();
        TextureStateRecord record = (TextureStateRecord) context.getStateRecord(StateType.Texture);

        IntBuffer id = BufferUtils.createIntBuffer(1);
        id.clear();
        id.put(textureId);
        id.rewind();
        GL11.glDeleteTextures(id);
        record.removeTextureRecord(textureId);
    }

    @Override
    public StateRecord createStateRecord() {
        return new TextureStateRecord(numTotalTexUnits);
    }

    /**
     * Useful for external lwjgl based classes that need to safely set the
     * current texture.
     */
    public static void doTextureBind(int textureId, int unit, Type type) {
        // ask for the current state record
        RenderContext<?> context = DisplaySystem.getDisplaySystem().getCurrentContext();
        TextureStateRecord record = (TextureStateRecord) context.getStateRecord(StateType.Texture);
        context.currentStates[StateType.Texture.ordinal()] = null;
        checkAndSetUnit(unit, record);

        GL11.glBindTexture(getGLType(type), textureId);
        if (Debug.stats) {
            StatCollector.addStat(StatType.STAT_TEXTURE_BINDS, 1);
        }
        if (record != null)
            record.units[unit].boundTexture = textureId;
    }

    private static int getGLType(Type type) {
        switch (type) {
            case TwoDimensional:
                return GL11.GL_TEXTURE_2D;
            case OneDimensional:
                return GL11.GL_TEXTURE_1D;
            case ThreeDimensional:
                return GL12.GL_TEXTURE_3D;
            case CubeMap:
                return ARBTextureCubeMap.GL_TEXTURE_CUBE_MAP_ARB;
        }
        throw new IllegalArgumentException("invalid texture type: " + type);
    }

    private static int getGLCubeMapFace(TextureCubeMap.Face face) {
        switch (face) {
            case PositiveX:
                return ARBTextureCubeMap.GL_TEXTURE_CUBE_MAP_POSITIVE_X_ARB;
            case NegativeX:
                return ARBTextureCubeMap.GL_TEXTURE_CUBE_MAP_NEGATIVE_X_ARB;
            case PositiveY:
                return ARBTextureCubeMap.GL_TEXTURE_CUBE_MAP_POSITIVE_Y_ARB;
            case NegativeY:
                return ARBTextureCubeMap.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y_ARB;
            case PositiveZ:
                return ARBTextureCubeMap.GL_TEXTURE_CUBE_MAP_POSITIVE_Z_ARB;
            case NegativeZ:
                return ARBTextureCubeMap.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z_ARB;
        }
        throw new IllegalArgumentException("invalid cubemap face: " + face);
    }
}

