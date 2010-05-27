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

package com.jme.renderer.lwjgl;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.opengl.ARBDrawBuffers;
import org.lwjgl.opengl.ARBTextureFloat;
import org.lwjgl.opengl.EXTFramebufferBlit;
import org.lwjgl.opengl.EXTFramebufferMultisample;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GLContext;

import com.jme.image.Texture;
import com.jme.image.Texture2D;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.RenderContext;
import com.jme.renderer.TextureRenderer;
import com.jme.scene.Spatial;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.lwjgl.LWJGLTextureState;
import com.jme.scene.state.lwjgl.records.TextureRecord;
import com.jme.scene.state.lwjgl.records.TextureStateRecord;
import com.jme.system.lwjgl.LWJGLDisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.geom.BufferUtils;

/**
 * This class is used by LWJGL to render textures. Users should <b>not </b>
 * create this class directly. Instead, allow DisplaySystem to create it for
 * you.
 * 
 * @author Joshua Slack, Mark Powell
 * @version $Id: LWJGLTextureRenderer.java,v 1.50 2007/11/05 01:51:54 renanse
 *          Exp $
 * @see com.jme.system.DisplaySystem#createTextureRenderer
 */
public class LWJGLTextureRenderer implements TextureRenderer {
    private static final Logger logger = Logger
            .getLogger(LWJGLTextureRenderer.class.getName());

    private LWJGLCamera camera;

    private ColorRGBA backgroundColor = new ColorRGBA(1, 1, 1, 1);

    private int active, fboID, width, height, fboIDMS;
    private int depthRBID, colorRBID;
    private static boolean inited = false;
    private static boolean isSupported = true;
    private static boolean supportsMultiDraw = false;
    private static boolean supportsMultiSample = true;
    private static int maxSamples = 0;
    private int samplesUsed = 0;
    private static int maxDrawBuffers = 1;
    private static IntBuffer attachBuffer = null;
    private boolean usingDepthRB = false;

    private final LWJGLDisplaySystem display;
    
    private final LWJGLRenderer parentRenderer;

    public LWJGLTextureRenderer(int width, int height, LWJGLDisplaySystem display, LWJGLRenderer parentRenderer) {
        this(width, height, 0, display, parentRenderer);
    }

    public LWJGLTextureRenderer(int width, int height, int samples, LWJGLDisplaySystem display,
            LWJGLRenderer parentRenderer) {
        this.samplesUsed = samples;
        this.display = display;
        this.parentRenderer = parentRenderer;

        if (!inited) {
            supportsMultiSample = GLContext.getCapabilities().GL_EXT_framebuffer_multisample;
            if (supportsMultiSample) {
                IntBuffer buf = BufferUtils.createIntBuffer(16);
                GL11.glGetInteger(EXTFramebufferMultisample.GL_MAX_SAMPLES_EXT, buf);
                maxSamples = buf.get(0);
                logger.log(Level.FINER, "FBO Max Samples: {0}", maxSamples);
            }
            isSupported = GLContext.getCapabilities().GL_EXT_framebuffer_object;
            supportsMultiDraw = GLContext.getCapabilities().GL_ARB_draw_buffers;
            if (supportsMultiDraw && isSupported) {
                IntBuffer buf = BufferUtils.createIntBuffer(16);
                GL11.glGetInteger(EXTFramebufferObject.GL_MAX_COLOR_ATTACHMENTS_EXT, buf);
                maxDrawBuffers = buf.get(0);
                if (maxDrawBuffers > 1) {
                    attachBuffer = BufferUtils.createIntBuffer(maxDrawBuffers);
                    for (int i = 0; i < maxDrawBuffers; i++) {
                        attachBuffer.put(EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT + i);
                    }

                } else {
                    maxDrawBuffers = 1;
                }
            }
            if (!isSupported) {
                logger.warning("FBO not supported.");
                return;
            } else {
                logger.info("FBO support detected.");
            }
        }

        if (!GLContext.getCapabilities().GL_ARB_texture_non_power_of_two) {
            // Check if we have non-power of two sizes. If so,
            // find the smallest power of two size that is greater than
            // the provided size.
            if (!FastMath.isPowerOfTwo(width)) {
                int newWidth = 2;
                do {
                    newWidth <<= 1;

                } while (newWidth < width);
                width = newWidth;
            }

            if (!FastMath.isPowerOfTwo(height)) {
                int newHeight = 2;
                do {
                    newHeight <<= 1;

                } while (newHeight < height);
                height = newHeight;
            }
        }

        this.width = width;
        this.height = height;
        this.samplesUsed = Math.min(maxSamples, samplesUsed);

        IntBuffer buffer = BufferUtils.createIntBuffer(1);
        EXTFramebufferObject.glGenFramebuffersEXT(buffer); // generate id
        fboID = buffer.get(0);
        logger.log(Level.FINE, "Creating FBO {0} sized: {1} x {2}", new Integer[] { fboID, width, height });

        if (samplesUsed > 0) {
            buffer = BufferUtils.createIntBuffer(1);
            EXTFramebufferObject.glGenFramebuffersEXT(buffer); // generate id
            fboIDMS = buffer.get(0);
            logger.log(Level.FINE, "Creating multisampled FBO {0} sized: {1} x {2}", new Integer[] { fboIDMS, width,
                    height });
        }

        if (fboID <= 0) {
            logger.log(Level.SEVERE, "Invalid FBO id returned! {0}", fboID);
            isSupported = false;
            return;
        }

        if (samplesUsed > 0 && fboIDMS <= 0) {
            logger.log(Level.SEVERE, "Invalid FBO id returned! {0}", fboIDMS);
            isSupported = false;
            return;
        }

        if (samplesUsed > 0) {
            EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, fboIDMS);

            buffer.clear();
            EXTFramebufferObject.glGenRenderbuffersEXT(buffer);
            colorRBID = buffer.get(0);
            buffer.clear();
            EXTFramebufferObject.glGenRenderbuffersEXT(buffer); // generate id
            depthRBID = buffer.get(0);

            EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, colorRBID);
            EXTFramebufferMultisample.glRenderbufferStorageMultisampleEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT,
                    samplesUsed, GL11.GL_RGBA8, width, height);

            EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, depthRBID);
            EXTFramebufferMultisample.glRenderbufferStorageMultisampleEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT,
                    samplesUsed, GL11.GL_DEPTH_COMPONENT, width, height);

            EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
                    EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, colorRBID);
            EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
                    EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, depthRBID);
            checkFBOComplete();
            
        } else {

            EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, fboID);

            buffer.clear();
            EXTFramebufferObject.glGenRenderbuffersEXT(buffer);
            colorRBID = buffer.get(0);
            buffer.clear();
            EXTFramebufferObject.glGenRenderbuffersEXT(buffer); // generate id
            depthRBID = buffer.get(0);

            EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, colorRBID);
            EXTFramebufferObject.glRenderbufferStorageEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, GL11.GL_RGBA8,
                    width, height);

            EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, depthRBID);
            EXTFramebufferObject.glRenderbufferStorageEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT,
                    GL11.GL_DEPTH_COMPONENT, width, height);

            EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
                    EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, colorRBID);
            EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
                    EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, depthRBID);

            checkFBOComplete();
        }

        // reactivate the main display buffer after setup of the FBOs
        EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
        
        initCamera();
    }

    /**
     * <code>isSupported</code> obtains the capability of the graphics card.
     * If the graphics card does not have pbuffer support, false is returned,
     * otherwise, true is returned. TextureRenderer will not process any scene
     * elements if pbuffer is not supported.
     * 
     * @return if this graphics card supports pbuffers or not.
     */
    public boolean isSupported() {
        return isSupported;
    }

    /**
     * <code>getCamera</code> retrieves the camera this renderer is using.
     * 
     * @return the camera this renderer is using.
     */
    public Camera getCamera() {
        return camera;
    }

    /**
     * <code>setCamera</code> sets the camera this renderer should use.
     * 
     * @param camera
     *            the camera this renderer should use.
     */
    public void setCamera(Camera camera) {

        this.camera = (LWJGLCamera) camera;
    }

    /**
     * <code>setBackgroundColor</code> sets the OpenGL clear color to the
     * color specified.
     * 
     * @see com.jme.renderer.TextureRenderer#setBackgroundColor(com.jme.renderer.ColorRGBA)
     * @param c
     *            the color to set the background color to.
     */
    public void setBackgroundColor(ColorRGBA c) {
        // if color is null set background to white.
        if (c == null) {
            backgroundColor.a = 1.0f;
            backgroundColor.b = 1.0f;
            backgroundColor.g = 1.0f;
            backgroundColor.r = 1.0f;
        } else {
            backgroundColor = c;
        }
    }

    /**
     * <code>getBackgroundColor</code> retrieves the clear color of the
     * current OpenGL context.
     * 
     * @see com.jme.renderer.Renderer#getBackgroundColor()
     * @return the current clear color.
     */
    public ColorRGBA getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * <code>setupTexture</code> initializes a new Texture object for use with
     * TextureRenderer. Generates a valid OpenGL texture id for this texture and
     * initializes the data type for the texture.
     */
    public void setupTexture(Texture2D tex) {
        if (!isSupported) {
            return;
        }

        int format = GL11.GL_RGBA;
        int components = GL11.GL_RGBA8;
        int dataType = GL11.GL_UNSIGNED_BYTE;
        switch (tex.getRTTSource()) {
        case RGBA:
        case RGBA8:
            break;
        case RGB:
        case RGB8:
            format = GL11.GL_RGB;
            components = GL11.GL_RGB8;
            break;
        case Alpha:
        case Alpha8:
            format = GL11.GL_ALPHA;
            components = GL11.GL_ALPHA8;
            break;
        case Depth:
            format = GL11.GL_DEPTH_COMPONENT;
            components = GL11.GL_DEPTH_COMPONENT;
            break;
        case Depth16:
            if (!GLContext.getCapabilities().OpenGL14) {
                logger.warning("Depth16 textures are not supported.");
                isSupported = false;
                return;
            }
            format = GL11.GL_DEPTH_COMPONENT;
            components = GL14.GL_DEPTH_COMPONENT16;
            break;
        case Depth24:
            if (!GLContext.getCapabilities().OpenGL14) {
                logger.warning("Depth24 textures are not supported.");
                isSupported = false;
                return;
            }
            format = GL11.GL_DEPTH_COMPONENT;
            components = GL14.GL_DEPTH_COMPONENT24;
            break;
        case Depth32:
            if (!GLContext.getCapabilities().OpenGL14) {
                logger.warning("Depth32 textures are not supported.");
                isSupported = false;
                return;
            }
            format = GL11.GL_DEPTH_COMPONENT;
            components = GL14.GL_DEPTH_COMPONENT32;
            break;
        case Intensity:
        case Intensity8:
            format = GL11.GL_INTENSITY;
            components = GL11.GL_INTENSITY8;
            break;
        case Luminance:
        case Luminance8:
            format = GL11.GL_LUMINANCE;
            components = GL11.GL_LUMINANCE8;
            break;
        case LuminanceAlpha:
        case Luminance8Alpha8:
            format = GL11.GL_LUMINANCE_ALPHA;
            components = GL11.GL_LUMINANCE8_ALPHA8;
            break;
        case Alpha4:
            format = GL11.GL_ALPHA;
            components = GL11.GL_ALPHA4;
            break;
        case Alpha12:
            format = GL11.GL_ALPHA;
            components = GL11.GL_ALPHA12;
            break;
        case Alpha16:
            format = GL11.GL_ALPHA;
            components = GL11.GL_ALPHA16;
            break;
        case Luminance4:
            format = GL11.GL_LUMINANCE;
            components = GL11.GL_LUMINANCE4;
            break;
        case Luminance12:
            format = GL11.GL_LUMINANCE;
            components = GL11.GL_LUMINANCE12;
            break;
        case Luminance16:
            format = GL11.GL_LUMINANCE;
            components = GL11.GL_LUMINANCE16;
            break;
        case Luminance4Alpha4:
            format = GL11.GL_LUMINANCE_ALPHA;
            components = GL11.GL_LUMINANCE4_ALPHA4;
            break;
        case Luminance6Alpha2:
            format = GL11.GL_LUMINANCE_ALPHA;
            components = GL11.GL_LUMINANCE6_ALPHA2;
            break;
        case Luminance12Alpha4:
            format = GL11.GL_LUMINANCE_ALPHA;
            components = GL11.GL_LUMINANCE12_ALPHA4;
            break;
        case Luminance12Alpha12:
            format = GL11.GL_LUMINANCE_ALPHA;
            components = GL11.GL_LUMINANCE12_ALPHA12;
            break;
        case Luminance16Alpha16:
            format = GL11.GL_LUMINANCE_ALPHA;
            components = GL11.GL_LUMINANCE16_ALPHA16;
            break;
        case Intensity4:
            format = GL11.GL_INTENSITY;
            components = GL11.GL_INTENSITY4;
            break;
        case Intensity12:
            format = GL11.GL_INTENSITY;
            components = GL11.GL_INTENSITY12;
            break;
        case Intensity16:
            format = GL11.GL_INTENSITY;
            components = GL11.GL_INTENSITY4;
            break;
        case R3_G3_B2:
            format = GL11.GL_RGB;
            components = GL11.GL_R3_G3_B2;
            break;
        case RGB4:
            format = GL11.GL_RGB;
            components = GL11.GL_RGB4;
            break;
        case RGB5:
            format = GL11.GL_RGB;
            components = GL11.GL_RGB5;
            break;
        case RGB10:
            format = GL11.GL_RGB;
            components = GL11.GL_RGB10;
            break;
        case RGB12:
            format = GL11.GL_RGB;
            components = GL11.GL_RGB12;
            break;
        case RGB16:
            format = GL11.GL_RGB;
            components = GL11.GL_RGB16;
            break;
        case RGBA2:
            format = GL11.GL_RGBA;
            components = GL11.GL_RGBA2;
            break;
        case RGBA4:
            format = GL11.GL_RGBA;
            components = GL11.GL_RGBA4;
            break;
        case RGB5_A1:
            format = GL11.GL_RGBA;
            components = GL11.GL_RGB5_A1;
            break;
        case RGB10_A2:
            format = GL11.GL_RGBA;
            components = GL11.GL_RGB10_A2;
            break;
        case RGBA12:
            format = GL11.GL_RGBA;
            components = GL11.GL_RGBA12;
            break;
        case RGBA16:
            format = GL11.GL_RGBA;
            components = GL11.GL_RGBA16;
            break;
        case RGBA32F:
            format = GL11.GL_RGBA;
            components = ARBTextureFloat.GL_RGBA32F_ARB;
            dataType = GL11.GL_FLOAT;
            break;
        case RGB32F:
            format = GL11.GL_RGB;
            components = ARBTextureFloat.GL_RGB32F_ARB;
            dataType = GL11.GL_FLOAT;
            break;
        case Alpha32F:
            format = GL11.GL_ALPHA;
            components = ARBTextureFloat.GL_ALPHA32F_ARB;
            dataType = GL11.GL_FLOAT;
            break;
        case Intensity32F:
            format = GL11.GL_INTENSITY;
            components = ARBTextureFloat.GL_INTENSITY32F_ARB;
            dataType = GL11.GL_FLOAT;
            break;
        case Luminance32F:
            format = GL11.GL_LUMINANCE;
            components = ARBTextureFloat.GL_LUMINANCE32F_ARB;
            dataType = GL11.GL_FLOAT;
            break;
        case LuminanceAlpha32F:
            format = GL11.GL_LUMINANCE_ALPHA;
            components = ARBTextureFloat.GL_LUMINANCE_ALPHA32F_ARB;
            dataType = GL11.GL_FLOAT;
            break;
        case RGBA16F:
            format = GL11.GL_RGBA;
            components = ARBTextureFloat.GL_RGBA16F_ARB;
            dataType = GL11.GL_FLOAT;
            break;
        case RGB16F:
            format = GL11.GL_RGB;
            components = ARBTextureFloat.GL_RGB16F_ARB;
            dataType = GL11.GL_FLOAT;
            break;
        case Alpha16F:
            format = GL11.GL_ALPHA;
            components = ARBTextureFloat.GL_ALPHA16F_ARB;
            dataType = GL11.GL_FLOAT;
            break;
        case Intensity16F:
            format = GL11.GL_INTENSITY;
            components = ARBTextureFloat.GL_INTENSITY16F_ARB;
            dataType = GL11.GL_FLOAT;
            break;
        case Luminance16F:
            format = GL11.GL_LUMINANCE;
            components = ARBTextureFloat.GL_LUMINANCE16F_ARB;
            dataType = GL11.GL_FLOAT;
            break;
        case LuminanceAlpha16F:
            format = GL11.GL_LUMINANCE_ALPHA;
            components = ARBTextureFloat.GL_LUMINANCE_ALPHA16F_ARB;
            dataType = GL11.GL_FLOAT;
            break;
        }
        if (dataType == GL11.GL_FLOAT
                && !GLContext.getCapabilities().GL_ARB_texture_float) {
            logger.warning("Float textures are not supported.");
            return;
        }
        IntBuffer ibuf = BufferUtils.createIntBuffer(1);

        if (tex.getTextureId() != 0) {
            ibuf.put(tex.getTextureId());
            GL11.glDeleteTextures(ibuf);
            ibuf.clear();
        }

        if (dataType == GL11.GL_FLOAT
                && !GLContext.getCapabilities().GL_ARB_texture_float) {
            logger.warning("Float textures are not supported.");
            isSupported = false;
            return;
        }

        // Create the texture
        GL11.glGenTextures(ibuf);
        tex.setTextureId(ibuf.get(0));
        TextureManager.registerForCleanup(tex.getTextureKey(), tex
                .getTextureId());

        LWJGLTextureState.doTextureBind(tex.getTextureId(), 0,
                Texture.Type.TwoDimensional);
        // Initialize our texture with some default data.
        if (dataType == GL11.GL_FLOAT)
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, components, width, height,
                    0, format, dataType, (FloatBuffer) null);
        else
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, components, width, height,
                    0, format, dataType, (ByteBuffer) null);

        // Initialize mipmapping for this texture, if requested
        if (tex.getMinificationFilter().usesMipMapLevels()) {
            EXTFramebufferObject.glGenerateMipmapEXT(GL11.GL_TEXTURE_2D);
        }

        // Setup filtering and wrap
        RenderContext<?> context = display.getCurrentContext();
        TextureStateRecord record = (TextureStateRecord) context.getStateRecord(RenderState.StateType.Texture);
        TextureRecord texRecord = record.getTextureRecord(tex.getTextureId(), tex.getType());

        LWJGLTextureState.applyFilter(tex, texRecord, 0, record);
        LWJGLTextureState.applyWrap(tex, texRecord, 0, record);

        logger.log(Level.INFO, "setup fbo tex with id {0}: {1},{2}", 
        	new Integer[] {tex.getTextureId(), width, height});
    }

    /**
     * <code>render</code> renders a scene. As it recieves a base class of
     * <code>Spatial</code> the renderer hands off management of the scene to
     * spatial for it to determine when a <code>Geometry</code> leaf is
     * reached. The result of the rendering is then copied into the given
     * texture(s). What is copied is based on the Texture object's rttSource
     * field.
     * 
     * @param toDraw
     *            the scene to render.
     * @param tex
     *            the Texture(s) to render it to.
     */
    public void render(Spatial toDraw, Texture tex) {
        render(toDraw, tex, true);
    }

    /**
     * <code>render</code> renders a scene. As it recieves a base class of
     * <code>Spatial</code> the renderer hands off management of the scene to
     * spatial for it to determine when a <code>Geometry</code> leaf is
     * reached. The result of the rendering is then copied into the given
     * texture(s). What is copied is based on the Texture object's rttSource
     * field.
     * 
     * @param toDraw
     *            the scene to render.
     * @param tex
     *            the Texture(s) to render it to.
     */
    public void render(Spatial toDraw, Texture tex, boolean doClear) {
        if (!isSupported) {
            return;
        }

        try {
            activate();

            setupForSingleTexDraw(tex, doClear);

            doDraw(toDraw);
            if (samplesUsed > 0) {
                copyFbo(fboIDMS, fboID);
            } // if
            takedownForSingleTexDraw(tex);

        } catch (Exception e) {
            logger.logp(Level.SEVERE, this.getClass().toString(), "render(Spatial, Texture, boolean)", "Exception", e);
        } finally {
            deactivate();
        }
    }

    private void copyFbo(int from, int to) {

        if (GLContext.getCapabilities().GL_EXT_framebuffer_blit) {
            EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferBlit.GL_READ_FRAMEBUFFER_EXT, from);
            EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferBlit.GL_DRAW_FRAMEBUFFER_EXT, to);

            EXTFramebufferBlit.glBlitFramebufferEXT(0, 0, width, height, 0, 0, width, height, GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);

            EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, from);
            checkFBOComplete();
        } else {
            throw new UnsupportedOperationException("EXT_framebuffer_blit required.");
            // TODO: support non-blit copies?
        }

    }

    public void render(ArrayList<? extends Spatial> toDraw, ArrayList<Texture> texs) {
        render(toDraw, texs, true);
    }

    public void render(ArrayList<? extends Spatial> toDraw, ArrayList<Texture> texs, boolean doClear) {
        if (!isSupported) {
            return;
        }

        // if we only support 1 draw buffer at a time anyway, we'll have to
        // render to each texture individually...
        if (maxDrawBuffers == 1 || texs.size() == 1) {
            try {
                activate();
                for (int i = 0; i < texs.size(); i++) {
                    Texture tex = texs.get(i);

                    setupForSingleTexDraw(tex, doClear);

                    doDraw(toDraw);
                    if (samplesUsed > 0) {
                        copyFbo(fboIDMS, fboID);
                    } // if
                    takedownForSingleTexDraw(tex);
                }
            } catch (Exception e) {
                logger.logp(Level.SEVERE, this.getClass().toString(), "render(Spatial, Texture, boolean)", "Exception",
                        e);
            } finally {
                deactivate();
            }
            return;
        }
        try {
            activate();

            // Otherwise, we can streamline this by rendering to multiple
            // textures at once.
            // first determine how many groups we need
            LinkedList<Texture> depths = new LinkedList<Texture>();
            LinkedList<Texture> colors = new LinkedList<Texture>();
            for (int i = 0; i < texs.size(); i++) {
                Texture tex = texs.get(i);
                if (tex.getRTTSource() == Texture.RenderToTextureType.Depth ||
                        tex.getRTTSource() == Texture.RenderToTextureType.Depth16 ||
                        tex.getRTTSource() == Texture.RenderToTextureType.Depth24 ||
                        tex.getRTTSource() == Texture.RenderToTextureType.Depth32) {
                    depths.add(tex);
                } else {
                    colors.add(tex);
                }
            }
            // we can only render to 1 depth texture at a time, so # groups is at minimum == numDepth
            int groups = Math.max(depths.size(), (int)(0.999f + (colors.size() / (float)maxDrawBuffers)));
            for (int i = 0; i < groups; i++) {
                // First handle colors
                int colorsAdded = 0;
                while (colorsAdded < maxDrawBuffers && !colors.isEmpty()) {
                    Texture tex = colors.removeFirst();
                    EXTFramebufferObject.glFramebufferTexture2DEXT(
                            EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
                            EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT + colorsAdded,
                            GL11.GL_TEXTURE_2D, tex.getTextureId(), 0);
                    colorsAdded++;
                }

                // Now take care of depth.
                if (!depths.isEmpty()) {
                    Texture tex = depths.removeFirst();
                    // Set up our depth texture
                    EXTFramebufferObject.glFramebufferTexture2DEXT(
                            EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
                            EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT,
                            GL11.GL_TEXTURE_2D, tex.getTextureId(), 0);
                    usingDepthRB = false;
                } else if (!usingDepthRB) {
                    // setup our default depth render buffer if not already set
                    EXTFramebufferObject.glFramebufferRenderbufferEXT(
                            EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
                            EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT,
                            EXTFramebufferObject.GL_RENDERBUFFER_EXT, depthRBID);
                    usingDepthRB = true;
                }

                setDrawBuffers(colorsAdded);
                setReadBuffer(colorsAdded != 0 ? EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT : GL11.GL_NONE);

                // Check FBO complete
                checkFBOComplete();

                switchCameraIn(doClear);

                doDraw(toDraw);
                if (samplesUsed > 0) {
                    copyFbo(fboIDMS, fboID);
                } // if
                switchCameraOut();
            }

            // automatically generate mipmaps for our textures.
            for (int x = 0, max = texs.size(); x < max; x++) {
                if (texs.get(x).getMinificationFilter().usesMipMapLevels()) {
                    LWJGLTextureState.doTextureBind(texs.get(x).getTextureId(), 0, Texture.Type.TwoDimensional);
                    EXTFramebufferObject.glGenerateMipmapEXT(GL11.GL_TEXTURE_2D);
                }
            }

        } catch (Exception e) {
            logger.logp(Level.SEVERE, this.getClass().toString(),
                    "render(Spatial, Texture)", "Exception", e);
        } finally {
            deactivate();
        }

        try {
        } catch (Exception e) {
            logger.logp(Level.SEVERE, this.getClass().toString(), "render(Spatial, Texture, boolean)", "Exception", e);
        }
    }

    private void setupForSingleTexDraw(Texture tex, boolean doClear) {

        LWJGLTextureState.doTextureBind(tex.getTextureId(), 0, Texture.Type.TwoDimensional);

        if (tex.getRTTSource() == Texture.RenderToTextureType.Depth ||
                tex.getRTTSource() == Texture.RenderToTextureType.Depth16 ||
                tex.getRTTSource() == Texture.RenderToTextureType.Depth24 ||
                tex.getRTTSource() == Texture.RenderToTextureType.Depth32) {
            // Setup depth texture into FBO

            if (samplesUsed > 0) {
                EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, fboID);
            }

            EXTFramebufferObject.glFramebufferTexture2DEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
                    EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, GL11.GL_TEXTURE_2D, tex.getTextureId(), 0);

            if (samplesUsed > 0) {
                EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, fboIDMS);
            }

            setDrawBuffer(GL11.GL_NONE);
            setReadBuffer(GL11.GL_NONE);
        } else {

            if (samplesUsed > 0) {
                EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, fboID);
            }

            // Set textures into FBO
            EXTFramebufferObject.glFramebufferTexture2DEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
                    EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT, GL11.GL_TEXTURE_2D, tex.getTextureId(), 0);

            setDrawBuffer(EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT);
            setReadBuffer(EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT);

            if (samplesUsed > 0) {
                EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, fboIDMS);
            }
        }

        // Check FBO complete
        checkFBOComplete();

        switchCameraIn(doClear);
    }

    private void setReadBuffer(int attachVal) {
        GL11.glReadBuffer(attachVal);
    }

    private void setDrawBuffer(int attachVal) {
        GL11.glDrawBuffer(attachVal);
    }

    private void setDrawBuffers(int maxEntry) {
        if (maxEntry <= 1) {
            setDrawBuffer(maxEntry != 0 ? EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT : GL11.GL_NONE);
        } else {
            // We should only get to this point if we support ARBDrawBuffers.
            attachBuffer.clear();
            attachBuffer.limit(maxEntry);
            ARBDrawBuffers.glDrawBuffersARB(attachBuffer);
        }
    }

    private void takedownForSingleTexDraw(Texture tex) {
        switchCameraOut();

        // automatically generate mipmaps for our texture.
        if (tex.getMinificationFilter().usesMipMapLevels()) {
            LWJGLTextureState.doTextureBind(tex.getTextureId(), 0, Texture.Type.TwoDimensional);
            EXTFramebufferObject.glGenerateMipmapEXT(GL11.GL_TEXTURE_2D);
        }
    }

    private void checkFBOComplete() {
        int framebuffer = EXTFramebufferObject.glCheckFramebufferStatusEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT);
        int fboIdToShow = samplesUsed > 0 ? fboIDMS : fboID;
        switch (framebuffer) {
            case EXTFramebufferObject.GL_FRAMEBUFFER_COMPLETE_EXT:
                break;
            case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT:
                throw new RuntimeException(
                        "FrameBuffer: "
                                + fboID
                                + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT exception");
            case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT:
                throw new RuntimeException(
                        "FrameBuffer: "
                                + fboID
                                + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT exception");
            case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT:
                throw new RuntimeException(
                        "FrameBuffer: "
                                + fboID
                                + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT exception");
            case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT:
                throw new RuntimeException(
                        "FrameBuffer: "
                                + fboID
                                + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT exception");
            case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT:
                throw new RuntimeException(
                        "FrameBuffer: "
                                + fboID
                                + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT exception");
            case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT:
                throw new RuntimeException(
                        "FrameBuffer: "
                                + fboID
                                + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT exception");
            case EXTFramebufferObject.GL_FRAMEBUFFER_UNSUPPORTED_EXT:
                throw new RuntimeException(
                        "FrameBuffer: "
                                + fboID
                                + ", has caused a GL_FRAMEBUFFER_UNSUPPORTED_EXT exception");
            case EXTFramebufferMultisample.GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE_EXT:
                throw new RuntimeException(
                        "FrameBuffer: "
                                + fboIdToShow
                                + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE_EXT exception");
            default:
                throw new RuntimeException(
                        "Unexpected reply from glCheckFramebufferStatusEXT: "
                                + framebuffer);
        }
    }

    /**
     * <code>copyToTexture</code> copies the FBO contents to the given
     * Texture. What is copied is up to the Texture object's rttSource field.
     * 
     * @param tex
     *            The Texture to copy into.
     * @param width
     *            the width of the texture image
     * @param height
     *            the height of the texture image
     */
    public void copyToTexture(Texture tex, int width, int height) {
        copyToTexture(tex, 0, 0, width, height);
    }
    
    
    /**
     * <code>copyToTexture</code> copies the FBO contents to the given
     * Texture. What is copied is up to the Texture object's rttSource field.
     * 
     * @param tex
     *            The Texture to copy into.
     * @param x
     *            the x offset on the texture image
     * @param y
     *            the y offset on the texture image
     * @param width
     *            the width of the texture image
     * @param height
     *            the height of the texture image
     */
    public void copyToTexture(Texture tex, int x, int y, int width, int height) {
        LWJGLTextureState.doTextureBind(tex.getTextureId(), 0, Texture.Type.TwoDimensional);

        int source = GL11.GL_RGBA;
        switch (tex.getRTTSource()) {
        case RGBA:
        case RGBA8:
            break;
        case RGB:
        case RGB8:
            source = GL11.GL_RGB;
            break;
        case Alpha:
        case Alpha8:
            source = GL11.GL_ALPHA;
            break;
        case Depth:
        case Depth16:
        case Depth24:
        case Depth32:
            source = GL11.GL_DEPTH_COMPONENT;
            break;
        case Intensity:
        case Intensity8:
            source = GL11.GL_INTENSITY;
            break;
        case Luminance:
        case Luminance8:
            source = GL11.GL_LUMINANCE;
            break;
        case LuminanceAlpha:
        case Luminance8Alpha8:
            source = GL11.GL_LUMINANCE_ALPHA;
            break;
        case Alpha4:
            source = GL11.GL_ALPHA;
            break;
        case Alpha12:
            source = GL11.GL_ALPHA;
            break;
        case Alpha16:
            source = GL11.GL_ALPHA;
            break;
        case Luminance4:
            source = GL11.GL_LUMINANCE;
            break;
        case Luminance12:
            source = GL11.GL_LUMINANCE;
            break;
        case Luminance16:
            source = GL11.GL_LUMINANCE;
            break;
        case Luminance4Alpha4:
            source = GL11.GL_LUMINANCE_ALPHA;
            break;
        case Luminance6Alpha2:
            source = GL11.GL_LUMINANCE_ALPHA;
            break;
        case Luminance12Alpha4:
            source = GL11.GL_LUMINANCE_ALPHA;
            break;
        case Luminance12Alpha12:
            source = GL11.GL_LUMINANCE_ALPHA;
            break;
        case Luminance16Alpha16:
            source = GL11.GL_LUMINANCE_ALPHA;
            break;
        case Intensity4:
            source = GL11.GL_INTENSITY;
            break;
        case Intensity12:
            source = GL11.GL_INTENSITY;
            break;
        case Intensity16:
            source = GL11.GL_INTENSITY;
            break;
        case R3_G3_B2:
            source = GL11.GL_RGB;
            break;
        case RGB4:
            source = GL11.GL_RGB;
            break;
        case RGB5:
            source = GL11.GL_RGB;
            break;
        case RGB10:
            source = GL11.GL_RGB;
            break;
        case RGB12:
            source = GL11.GL_RGB;
            break;
        case RGB16:
            source = GL11.GL_RGB;
            break;
        case RGBA2:
            source = GL11.GL_RGBA;
            break;
        case RGBA4:
            source = GL11.GL_RGBA;
            break;
        case RGB5_A1:
            source = GL11.GL_RGBA;
            break;
        case RGB10_A2:
            source = GL11.GL_RGBA;
            break;
        case RGBA12:
            source = GL11.GL_RGBA;
            break;
        case RGBA16:
            source = GL11.GL_RGBA;
            break;
        case RGBA32F:
            source = GL11.GL_RGBA;
            break;
        case RGB32F:
            source = GL11.GL_RGB;
            break;
        case Alpha32F:
            source = GL11.GL_ALPHA;
            break;
        case Intensity32F:
            source = GL11.GL_INTENSITY;
            break;
        case Luminance32F:
            source = GL11.GL_LUMINANCE;
            break;
        case LuminanceAlpha32F:
            source = GL11.GL_LUMINANCE_ALPHA;
            break;
        case RGBA16F:
            source = GL11.GL_RGBA;
            break;
        case RGB16F:
            source = GL11.GL_RGB;
            break;
        case Alpha16F:
            source = GL11.GL_ALPHA;
            break;
        case Intensity16F:
            source = GL11.GL_INTENSITY;
            break;
        case Luminance16F:
            source = GL11.GL_LUMINANCE;
            break;
        case LuminanceAlpha16F:
            source = GL11.GL_LUMINANCE_ALPHA;
            break;
        }
        GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, source, x, y, width,
                height, 0);
    }

    private Camera oldCamera;
    private int oldWidth, oldHeight;
    private void switchCameraIn(boolean doClear) {
        // grab non-rtt settings
        oldCamera = parentRenderer.getCamera();
        oldWidth = parentRenderer.getWidth();
        oldHeight = parentRenderer.getHeight();
        parentRenderer.setCamera(getCamera());


        float viewportWidthFactor = camera.getViewPortRight() - camera.getViewPortLeft();
        float viewportHeightFactor = camera.getViewPortTop() - camera.getViewPortBottom();
        
        // swap to rtt settings
        parentRenderer.getQueue().swapBuckets();
        parentRenderer.reinit((int) (width / viewportWidthFactor), (int) (height / viewportHeightFactor));

        // clear the scene
        if (doClear) {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            parentRenderer.clearBuffers();
        }

        getCamera().update();
        getCamera().apply();
    }

    private void switchCameraOut() {
        parentRenderer.setCamera(oldCamera);
        parentRenderer.reinit(oldWidth, oldHeight);

        // back to the non rtt settings
        parentRenderer.getQueue().swapBuckets();
        oldCamera.update();
        oldCamera.apply();
    }

    private void doDraw(Spatial spat) {
        // Override parent's last frustum test to avoid accidental incorrect
        // cull
        if (spat.getParent() != null)
            spat.getParent().setLastFrustumIntersection(Camera.FrustumIntersect.Intersects);

        // do rtt scene render
        spat.onDraw(parentRenderer);
        parentRenderer.renderQueue();
    }
    
    private void doDraw(ArrayList<? extends Spatial> toDraw) {
        for (int x = 0, max = toDraw.size(); x < max; x++) {
            Spatial spat = toDraw.get(x);
            doDraw(spat);
        }
    }

    private void activate() {
        if (!isSupported) {
            return;
        }
        if (active == 0) {
            int fboIdToUse = samplesUsed > 0 ? fboIDMS : fboID;
            GL11.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
            EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, fboIdToUse);
        }
        active++;
    }

    private void deactivate() {
        if (!isSupported) {
            return;
        }
        if (active == 1) {
            GL11.glClearColor(parentRenderer.getBackgroundColor().r, parentRenderer.getBackgroundColor().g,
                    parentRenderer.getBackgroundColor().b, parentRenderer.getBackgroundColor().a);
            EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
        }
        active--;
    }

    private void initCamera() {
        if (!isSupported) {
            return;
        }
        logger.info("Init RTT camera");
        camera = new LWJGLCamera(width, height, false);
        camera.setFrustum(1.0f, 1000.0f, -0.50f, 0.50f, 0.50f, -0.50f);
        Vector3f loc = new Vector3f(0.0f, 0.0f, 0.0f);
        Vector3f left = new Vector3f(-1.0f, 0.0f, 0.0f);
        Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
        Vector3f dir = new Vector3f(0.0f, 0f, -1.0f);
        camera.setFrame(loc, left, up, dir);
        camera.setDataOnly(false);
    }

    public void cleanup() {
        if (!isSupported) {
            return;
        }

        if (fboID > 0) {
            IntBuffer id = BufferUtils.createIntBuffer(1);
            id.put(fboID);
            id.rewind();
            EXTFramebufferObject.glDeleteFramebuffersEXT(id);
        }

        if (fboIDMS > 0) {
            IntBuffer id = BufferUtils.createIntBuffer(1);
            id.put(fboIDMS);
            id.rewind();
            EXTFramebufferObject.glDeleteFramebuffersEXT(id);
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setMultipleTargets(boolean multi) {
        // ignore. Does not matter to FBO.
    }
}
