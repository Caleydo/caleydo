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

package com.jme.renderer.jogl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

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
import com.jme.scene.state.jogl.JOGLTextureState;
import com.jme.scene.state.jogl.records.TextureRecord;
import com.jme.scene.state.jogl.records.TextureStateRecord;
import com.jme.system.jogl.JOGLDisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.geom.BufferUtils;

/**
 * This class is used by JOGL to render textures. Users should <b>not </b>
 * create this class directly. Instead, allow DisplaySystem to create it for
 * you.
 *
 * @author Joshua Slack, Mark Powell
 * @author Steve Vaughan - JOGL port
 * @version $Id: JOGLTextureRenderer.java,v 1.50 2007/11/05 01:51:54 renanse
 *          Exp $
 * @see com.jme.system.DisplaySystem#createTextureRenderer
 */
public class JOGLTextureRenderer implements TextureRenderer {
    private static final Logger logger = Logger
            .getLogger(JOGLTextureRenderer.class.getName());

    private JOGLCamera camera;

    private ColorRGBA backgroundColor = new ColorRGBA(1, 1, 1, 1);

    private int active, fboID, depthRBID, width, height;

    private static boolean inited = false;
    private static boolean isSupported = true;
    private static boolean supportsMultiDraw = false;
    private static int maxDrawBuffers = 1;
    private static IntBuffer attachBuffer = null;
    private boolean usingDepthRB = false;

    private final JOGLDisplaySystem display;

    private final JOGLRenderer parentRenderer;

    private final GL gl;

    public JOGLTextureRenderer(int width, int height,
            JOGLDisplaySystem display, JOGLRenderer parentRenderer) {

        gl = GLU.getCurrentGL();
        this.display = display;
        this.parentRenderer = parentRenderer;

        if (!inited) {
            isSupported = gl.isExtensionAvailable("GL_EXT_framebuffer_object");
            supportsMultiDraw = gl.isExtensionAvailable("GL_ARB_draw_buffers");
            if (supportsMultiDraw && isSupported) {
                IntBuffer buf = BufferUtils.createIntBuffer(16);
                gl.glGetIntegerv(GL.GL_MAX_COLOR_ATTACHMENTS_EXT, buf); // TODO Check for integer
                maxDrawBuffers = buf.get(0);
                if (maxDrawBuffers > 1) {
                    attachBuffer = BufferUtils.createIntBuffer(maxDrawBuffers);
                    for (int i = 0; i < maxDrawBuffers; i++) {
                        attachBuffer.put(GL.GL_COLOR_ATTACHMENT0_EXT+i);
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

        if (!gl.isExtensionAvailable("GL_ARB_texture_non_power_of_two")) {
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

        logger.log(Level.FINE, "Creating FBO sized: {0} x {1}", new Integer[] {width, height});

        IntBuffer buffer = BufferUtils.createIntBuffer(1);
        gl.glGenFramebuffersEXT(buffer.limit(),buffer); // TODO Check <size> // generate id
        fboID = buffer.get(0);

        if (fboID <= 0) {
            logger.log(Level.SEVERE, "Invalid FBO id returned! {0}", fboID);
            isSupported = false;
            return;
        }

        gl.glGenRenderbuffersEXT(buffer.limit(),buffer); // TODO Check <size> // generate id
        depthRBID = buffer.get(0);
        gl.glBindRenderbufferEXT(
                GL.GL_RENDERBUFFER_EXT, depthRBID);
        gl.glRenderbufferStorageEXT(
                GL.GL_RENDERBUFFER_EXT,
                GL.GL_DEPTH_COMPONENT, width, height);

        this.width = width;
        this.height = height;

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
        this.camera = (JOGLCamera) camera;
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
        final GL gl = GLU.getCurrentGL();

        if (!isSupported) {
            return;
        }

	int components = GL.GL_RGBA8;
        int format = GL.GL_RGBA;
	int dataType = GL.GL_UNSIGNED_BYTE;
	switch (tex.getRTTSource()) {
	case RGBA:
	case RGBA8:
	    break;
	case RGB:
	case RGB8:
	    format = GL.GL_RGB;
	    components = GL.GL_RGB8;
	    break;
	case Alpha:
	case Alpha8:
	    format = GL.GL_ALPHA;
	    components = GL.GL_ALPHA8;
	    break;
	case Depth:
	    format = GL.GL_DEPTH_COMPONENT;
	    components = GL.GL_DEPTH_COMPONENT;
	    break;
        case Depth16:
            if (!gl.isExtensionAvailable("GL_VERSION_1_4")) {
                logger.warning("Depth16 textures are not supported.");
                isSupported = false;
                return;
            }
            format = GL.GL_DEPTH_COMPONENT;
            components = GL.GL_DEPTH_COMPONENT16_ARB;
            break;
        case Depth24:
            if (!gl.isExtensionAvailable("GL_VERSION_1_4")) {
                logger.warning("Depth24 textures are not supported.");
                isSupported = false;
                return;
            }
            format = GL.GL_DEPTH_COMPONENT;
            components = GL.GL_DEPTH_COMPONENT24_ARB;
            break;
        case Depth32:
            if (!gl.isExtensionAvailable("GL_VERSION_1_4")) {
                logger.warning("Depth32 textures are not supported.");
                isSupported = false;
                return;
            }
            format = GL.GL_DEPTH_COMPONENT;
            components = GL.GL_DEPTH_COMPONENT32_ARB;
            break;
	case Intensity:
	case Intensity8:
	    format = GL.GL_INTENSITY;
	    components = GL.GL_INTENSITY8;
	    break;
	case Luminance:
	case Luminance8:
	    format = GL.GL_LUMINANCE;
	    components = GL.GL_LUMINANCE8;
	    break;
	case LuminanceAlpha:
	case Luminance8Alpha8:
	    format = GL.GL_LUMINANCE_ALPHA;
	    components = GL.GL_LUMINANCE8_ALPHA8;
	    break;
	case Alpha4:
	    format = GL.GL_ALPHA;
	    components = GL.GL_ALPHA4;
	    break;
	case Alpha12:
	    format = GL.GL_ALPHA;
	    components = GL.GL_ALPHA12;
	    break;
	case Alpha16:
	    format = GL.GL_ALPHA;
	    components = GL.GL_ALPHA16;
	    break;
	case Luminance4:
	    format = GL.GL_LUMINANCE;
	    components = GL.GL_LUMINANCE4;
	    break;
	case Luminance12:
	    format = GL.GL_LUMINANCE;
	    components = GL.GL_LUMINANCE12;
	    break;
	case Luminance16:
	    format = GL.GL_LUMINANCE;
	    components = GL.GL_LUMINANCE16;
	    break;
	case Luminance4Alpha4:
	    format = GL.GL_LUMINANCE_ALPHA;
	    components = GL.GL_LUMINANCE4_ALPHA4;
	    break;
	case Luminance6Alpha2:
	    format = GL.GL_LUMINANCE_ALPHA;
	    components = GL.GL_LUMINANCE6_ALPHA2;
	    break;
	case Luminance12Alpha4:
	    format = GL.GL_LUMINANCE_ALPHA;
	    components = GL.GL_LUMINANCE12_ALPHA4;
	    break;
	case Luminance12Alpha12:
	    format = GL.GL_LUMINANCE_ALPHA;
	    components = GL.GL_LUMINANCE12_ALPHA12;
	    break;
	case Luminance16Alpha16:
	    format = GL.GL_LUMINANCE_ALPHA;
	    components = GL.GL_LUMINANCE16_ALPHA16;
	    break;
	case Intensity4:
	    format = GL.GL_INTENSITY;
	    components = GL.GL_INTENSITY4;
	    break;
	case Intensity12:
	    format = GL.GL_INTENSITY;
	    components = GL.GL_INTENSITY12;
	    break;
	case Intensity16:
	    format = GL.GL_INTENSITY;
	    components = GL.GL_INTENSITY4;
	    break;
	case R3_G3_B2:
	    format = GL.GL_RGB;
	    components = GL.GL_R3_G3_B2;
	    break;
	case RGB4:
	    format = GL.GL_RGB;
	    components = GL.GL_RGB4;
	    break;
	case RGB5:
	    format = GL.GL_RGB;
	    components = GL.GL_RGB5;
	    break;
	case RGB10:
	    format = GL.GL_RGB;
	    components = GL.GL_RGB10;
	    break;
	case RGB12:
	    format = GL.GL_RGB;
	    components = GL.GL_RGB12;
	    break;
	case RGB16:
	    format = GL.GL_RGB;
	    components = GL.GL_RGB16;
	    break;
	case RGBA2:
	    format = GL.GL_RGBA;
	    components = GL.GL_RGBA2;
	    break;
	case RGBA4:
	    format = GL.GL_RGBA;
	    components = GL.GL_RGBA4;
	    break;
	case RGB5_A1:
	    format = GL.GL_RGBA;
	    components = GL.GL_RGB5_A1;
	    break;
	case RGB10_A2:
	    format = GL.GL_RGBA;
	    components = GL.GL_RGB10_A2;
	    break;
	case RGBA12:
	    format = GL.GL_RGBA;
	    components = GL.GL_RGBA12;
	    break;
	case RGBA16:
	    format = GL.GL_RGBA;
	    components = GL.GL_RGBA16;
	    break;
	case RGBA32F:
	    format = GL.GL_RGBA;
	    components = GL.GL_RGBA32F_ARB;
	    dataType = GL.GL_FLOAT;
	    break;
	case RGB32F:
	    format = GL.GL_RGB;
	    components = GL.GL_RGB32F_ARB;
	    dataType = GL.GL_FLOAT;
	    break;
	case Alpha32F:
	    format = GL.GL_ALPHA;
	    components = GL.GL_ALPHA32F_ARB;
	    dataType = GL.GL_FLOAT;
	    break;
	case Intensity32F:
	    format = GL.GL_INTENSITY;
	    components = GL.GL_INTENSITY32F_ARB;
	    dataType = GL.GL_FLOAT;
	    break;
	case Luminance32F:
	    format = GL.GL_LUMINANCE;
	    components = GL.GL_LUMINANCE32F_ARB;
	    dataType = GL.GL_FLOAT;
	    break;
	case LuminanceAlpha32F:
	    format = GL.GL_LUMINANCE_ALPHA;
	    components = GL.GL_LUMINANCE_ALPHA32F_ARB;
	    dataType = GL.GL_FLOAT;
	    break;
	case RGBA16F:
	    format = GL.GL_RGBA;
	    components = GL.GL_RGBA16F_ARB;
	    dataType = GL.GL_FLOAT;
	    break;
	case RGB16F:
	    format = GL.GL_RGB;
	    components = GL.GL_RGB16F_ARB;
	    dataType = GL.GL_FLOAT;
	    break;
	case Alpha16F:
	    format = GL.GL_ALPHA;
	    components = GL.GL_ALPHA16F_ARB;
	    dataType = GL.GL_FLOAT;
	    break;
	case Intensity16F:
	    format = GL.GL_INTENSITY;
	    components = GL.GL_INTENSITY16F_ARB;
	    dataType = GL.GL_FLOAT;
	    break;
	case Luminance16F:
	    format = GL.GL_LUMINANCE;
	    components = GL.GL_LUMINANCE16F_ARB;
	    dataType = GL.GL_FLOAT;
	    break;
	case LuminanceAlpha16F:
	    format = GL.GL_LUMINANCE_ALPHA;
	    components = GL.GL_LUMINANCE_ALPHA16F_ARB;
	    dataType = GL.GL_FLOAT;
	    break;
	}
        if (dataType == GL.GL_FLOAT
                && !gl.isExtensionAvailable("GL_ARB_texture_float")) {
            logger.warning("Float textures are not supported.");
            isSupported = false;
            return;
        }

        IntBuffer ibuf = BufferUtils.createIntBuffer(1);

        if (tex.getTextureId() != 0) {
            ibuf.put(tex.getTextureId());
            gl.glDeleteTextures(ibuf.limit(),ibuf); // TODO Check <size>
            ibuf.clear();
        }

        // Create the texture
        gl.glGenTextures(ibuf.limit(),ibuf); // TODO Check <size>
        tex.setTextureId(ibuf.get(0));
        TextureManager.registerForCleanup(tex.getTextureKey(), tex
                .getTextureId());

        JOGLTextureState.doTextureBind(tex.getTextureId(), 0,
                Texture.Type.TwoDimensional);

        // Initialize our texture with some default data.
        if (dataType == GL.GL_FLOAT)
            gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, components, width, height, 0,
                    format, dataType, (FloatBuffer) null);
        else
            gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, components, width, height, 0,
                    format, dataType, (ByteBuffer) null);
        // Initialize mipmapping for this texture, if requested
        if (tex.getMinificationFilter().usesMipMapLevels()) {
            gl.glGenerateMipmapEXT(GL.GL_TEXTURE_2D);
        }
        // Setup filtering and wrap
        RenderContext<?> context = display.getCurrentContext();
        TextureStateRecord record = (TextureStateRecord) context.getStateRecord(RenderState.StateType.Texture);
        TextureRecord texRecord = record.getTextureRecord(tex.getTextureId(), tex.getType());

        JOGLTextureState.applyFilter(tex, texRecord, 0, record);
        JOGLTextureState.applyWrap(tex, texRecord, 0, record);

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

            takedownForSingleTexDraw(tex);

            deactivate();
        } catch (Exception e) {
            logger.logp(Level.SEVERE, this.getClass().toString(),
                    "render(Spatial, Texture, boolean)", "Exception", e);
        }
    }

    public void render(ArrayList<? extends Spatial> toDraw,
            ArrayList<Texture> texs) {
        render(toDraw, texs, true);
    }

    public void render(ArrayList<? extends Spatial> toDraw, ArrayList<Texture> texs, boolean doClear) {
        final GL gl = GLU.getCurrentGL();

        if (!isSupported) {
            return;
        }

        // if we only support 1 draw buffer at a time anyway, we'll have to render to each texture individually...
        if (maxDrawBuffers == 1 || texs.size() == 1) {
            try {
                activate();
                for (int i = 0; i < texs.size(); i++) {
                    Texture tex = texs.get(i);

                    setupForSingleTexDraw(tex, doClear);

                    doDraw(toDraw);

                    takedownForSingleTexDraw(tex);
                }
            } catch (Exception e) {
                logger.logp(Level.SEVERE, this.getClass().toString(),
                        "render(Spatial, Texture, boolean)", "Exception", e);
            } finally {
                deactivate();
            }
            return;
        }
        try {
            activate();

            // Otherwise, we can streamline this by rendering to multiple textures at once.
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
                    gl.glFramebufferTexture2DEXT(
                            GL.GL_FRAMEBUFFER_EXT,
                            GL.GL_COLOR_ATTACHMENT0_EXT + colorsAdded,
                            GL.GL_TEXTURE_2D, tex.getTextureId(), 0);
                    colorsAdded++;
                }

                // Now take care of depth.
                if (!depths.isEmpty()) {
                    Texture tex = depths.removeFirst();
                    // Set up our depth texture
                    gl.glFramebufferTexture2DEXT(
                            GL.GL_FRAMEBUFFER_EXT,
                            GL.GL_DEPTH_ATTACHMENT_EXT,
                            GL.GL_TEXTURE_2D, tex.getTextureId(), 0);
                    usingDepthRB = false;
                } else if (!usingDepthRB) {
                    // setup our default depth render buffer if not already set
                    gl.glFramebufferRenderbufferEXT(
                            GL.GL_FRAMEBUFFER_EXT,
                            GL.GL_DEPTH_ATTACHMENT_EXT,
                            GL.GL_RENDERBUFFER_EXT, depthRBID);
                    usingDepthRB = true;
                }

                setDrawBuffers(colorsAdded);
                setReadBuffer(colorsAdded != 0 ? GL.GL_COLOR_ATTACHMENT0_EXT : GL.GL_NONE);

                // Check FBO complete
                checkFBOComplete();

                switchCameraIn(doClear);

                doDraw(toDraw);

                switchCameraOut();
            }

            // automatically generate mipmaps for our textures.
            for (int x = 0, max = texs.size(); x < max; x++) {
                if (texs.get(x).getMinificationFilter().usesMipMapLevels()) {
                    JOGLTextureState.doTextureBind(texs.get(x).getTextureId(), 0, Texture.Type.TwoDimensional);
                    gl.glGenerateMipmapEXT(GL.GL_TEXTURE_2D);
                }
            }

        } catch (Exception e) {
            logger.logp(Level.SEVERE, this.getClass().toString(),
                    "render(Spatial, Texture)", "Exception", e);
        } finally {
            deactivate();
        }
    }

    private void setupForSingleTexDraw(Texture tex, boolean doClear) {
        final GL gl = GLU.getCurrentGL();

        JOGLTextureState.doTextureBind(tex.getTextureId(), 0, Texture.Type.TwoDimensional);

        if (tex.getRTTSource() == Texture.RenderToTextureType.Depth ||
                tex.getRTTSource() == Texture.RenderToTextureType.Depth16 ||
                tex.getRTTSource() == Texture.RenderToTextureType.Depth24 ||
                tex.getRTTSource() == Texture.RenderToTextureType.Depth32) {
            // Setup depth texture into FBO
            gl.glFramebufferTexture2DEXT(
                    GL.GL_FRAMEBUFFER_EXT,
                    GL.GL_DEPTH_ATTACHMENT_EXT,
                    GL.GL_TEXTURE_2D, tex.getTextureId(), 0);

            setDrawBuffer(GL.GL_NONE);
            setReadBuffer(GL.GL_NONE);
        } else {
            // Set textures into FBO
            gl.glFramebufferTexture2DEXT(
                    GL.GL_FRAMEBUFFER_EXT,
                    GL.GL_COLOR_ATTACHMENT0_EXT,
                    GL.GL_TEXTURE_2D, tex.getTextureId(), 0);

            // setup depth RB
            gl.glFramebufferRenderbufferEXT(
                    GL.GL_FRAMEBUFFER_EXT,
                    GL.GL_DEPTH_ATTACHMENT_EXT,
                    GL.GL_RENDERBUFFER_EXT, depthRBID);

            setDrawBuffer(GL.GL_COLOR_ATTACHMENT0_EXT);
            setReadBuffer(GL.GL_COLOR_ATTACHMENT0_EXT);
        }

        // Check FBO complete
        checkFBOComplete();

        switchCameraIn(doClear);
    }

    private void setReadBuffer(int attachVal) {
        final GL gl = GLU.getCurrentGL();

        gl.glReadBuffer(attachVal);
    }

    private void setDrawBuffer(int attachVal) {
        final GL gl = GLU.getCurrentGL();

        gl.glDrawBuffer(attachVal);
    }

    private void setDrawBuffers(int maxEntry) {
        final GL gl = GLU.getCurrentGL();

        if (maxEntry <= 1) {
            setDrawBuffer(maxEntry != 0 ? GL.GL_COLOR_ATTACHMENT0_EXT : GL.GL_NONE);
        } else {
            // We should only get to this point if we support ARBDrawBuffers.
            attachBuffer.clear();
            attachBuffer.limit(maxEntry);
            gl.glDrawBuffersARB(attachBuffer.limit(),attachBuffer); // TODO Check <size>
        }
    }

    private void takedownForSingleTexDraw(Texture tex) {
        final GL gl = GLU.getCurrentGL();

        switchCameraOut();

        // automatically generate mipmaps for our texture.
        if (tex.getMinificationFilter().usesMipMapLevels()) {
            JOGLTextureState.doTextureBind(tex.getTextureId(), 0, Texture.Type.TwoDimensional);
            gl.glGenerateMipmapEXT(GL.GL_TEXTURE_2D);
        }
    }


    private void checkFBOComplete() {
        final GL gl = GLU.getCurrentGL();

        int framebuffer = gl
                .glCheckFramebufferStatusEXT(GL.GL_FRAMEBUFFER_EXT);
        switch (framebuffer) {
            case GL.GL_FRAMEBUFFER_COMPLETE_EXT:
                break;
            case GL.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT:
                throw new RuntimeException(
                        "FrameBuffer: "
                                + fboID
                                + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT exception");
            case GL.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT:
                throw new RuntimeException(
                        "FrameBuffer: "
                                + fboID
                                + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT exception");
            case GL.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT:
                throw new RuntimeException(
                        "FrameBuffer: "
                                + fboID
                                + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT exception");
            case GL.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT:
                throw new RuntimeException(
                        "FrameBuffer: "
                                + fboID
                                + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT exception");
            case GL.GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT:
                throw new RuntimeException(
                        "FrameBuffer: "
                                + fboID
                                + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT exception");
            case GL.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT:
                throw new RuntimeException(
                        "FrameBuffer: "
                                + fboID
                                + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT exception");
            case GL.GL_FRAMEBUFFER_UNSUPPORTED_EXT:
                throw new RuntimeException(
                        "FrameBuffer: "
                                + fboID
                                + ", has caused a GL_FRAMEBUFFER_UNSUPPORTED_EXT exception");
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
        final GL gl = GLU.getCurrentGL();

        JOGLTextureState.doTextureBind(tex.getTextureId(), 0,
            Texture.Type.TwoDimensional);

	int source = GL.GL_RGBA;
	switch (tex.getRTTSource()) {
	case RGBA:
	case RGBA8:
	    break;
	case RGB:
	case RGB8:
	    source = GL.GL_RGB;
	    break;
	case Alpha:
	case Alpha8:
	    source = GL.GL_ALPHA;
	    break;
	case Depth:
        case Depth16:
        case Depth24:
        case Depth32:
	    source = GL.GL_DEPTH_COMPONENT;
	    break;
	case Intensity:
	case Intensity8:
	    source = GL.GL_INTENSITY;
	    break;
	case Luminance:
	case Luminance8:
	    source = GL.GL_LUMINANCE;
	    break;
	case LuminanceAlpha:
	case Luminance8Alpha8:
	    source = GL.GL_LUMINANCE_ALPHA;
	    break;
	case Alpha4:
	    source = GL.GL_ALPHA;
	    break;
	case Alpha12:
	    source = GL.GL_ALPHA;
	    break;
	case Alpha16:
	    source = GL.GL_ALPHA;
	    break;
	case Luminance4:
	    source = GL.GL_LUMINANCE;
	    break;
	case Luminance12:
	    source = GL.GL_LUMINANCE;
	    break;
	case Luminance16:
	    source = GL.GL_LUMINANCE;
	    break;
	case Luminance4Alpha4:
	    source = GL.GL_LUMINANCE_ALPHA;
	    break;
	case Luminance6Alpha2:
	    source = GL.GL_LUMINANCE_ALPHA;
	    break;
	case Luminance12Alpha4:
	    source = GL.GL_LUMINANCE_ALPHA;
	    break;
	case Luminance12Alpha12:
	    source = GL.GL_LUMINANCE_ALPHA;
	    break;
	case Luminance16Alpha16:
	    source = GL.GL_LUMINANCE_ALPHA;
	    break;
	case Intensity4:
	    source = GL.GL_INTENSITY;
	    break;
	case Intensity12:
	    source = GL.GL_INTENSITY;
	    break;
	case Intensity16:
	    source = GL.GL_INTENSITY;
	    break;
	case R3_G3_B2:
	    source = GL.GL_RGB;
	    break;
	case RGB4:
	    source = GL.GL_RGB;
	    break;
	case RGB5:
	    source = GL.GL_RGB;
	    break;
	case RGB10:
	    source = GL.GL_RGB;
	    break;
	case RGB12:
	    source = GL.GL_RGB;
	    break;
	case RGB16:
	    source = GL.GL_RGB;
	    break;
	case RGBA2:
	    source = GL.GL_RGBA;
	    break;
	case RGBA4:
	    source = GL.GL_RGBA;
	    break;
	case RGB5_A1:
	    source = GL.GL_RGBA;
	    break;
	case RGB10_A2:
	    source = GL.GL_RGBA;
	    break;
	case RGBA12:
	    source = GL.GL_RGBA;
	    break;
	case RGBA16:
	    source = GL.GL_RGBA;
	    break;
	case RGBA32F:
	    source = GL.GL_RGBA;
	    break;
	case RGB32F:
	    source = GL.GL_RGB;
	    break;
	case Alpha32F:
	    source = GL.GL_ALPHA;
	    break;
	case Intensity32F:
	    source = GL.GL_INTENSITY;
	    break;
	case Luminance32F:
	    source = GL.GL_LUMINANCE;
	    break;
	case LuminanceAlpha32F:
	    source = GL.GL_LUMINANCE_ALPHA;
	    break;
	case RGBA16F:
	    source = GL.GL_RGBA;
	    break;
	case RGB16F:
	    source = GL.GL_RGB;
	    break;
	case Alpha16F:
	    source = GL.GL_ALPHA;
	    break;
	case Intensity16F:
	    source = GL.GL_INTENSITY;
	    break;
	case Luminance16F:
	    source = GL.GL_LUMINANCE;
	    break;
	case LuminanceAlpha16F:
	    source = GL.GL_LUMINANCE_ALPHA;
	    break;
	}
        gl
                .glCopyTexImage2D(GL.GL_TEXTURE_2D, 0, source, x, y, width,
			height, 0);
    }

    private Camera oldCamera;
    private int oldWidth, oldHeight;
    private void switchCameraIn(boolean doClear) {
        final GL gl = GLU.getCurrentGL();

        // grab non-rtt settings
        oldCamera = parentRenderer.getCamera();
        oldWidth = parentRenderer.getWidth();
        oldHeight = parentRenderer.getHeight();
        parentRenderer.setCamera(getCamera());

        float viewportWidthFactor = camera.getViewPortRight() - camera.getViewPortLeft();
        float viewportHeightFactor = camera.getViewPortTop() - camera.getViewPortBottom();
        
        // swap to rtt settings
        parentRenderer.getQueue().swapBuckets();
        parentRenderer.reinit((int)(width / viewportWidthFactor), (int)(height / viewportHeightFactor));

        // clear the scene
        if (doClear) {
            gl.glDisable(GL.GL_SCISSOR_TEST);
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
        final GL gl = GLU.getCurrentGL();

        if (!isSupported) {
            return;
        }
        if (active == 0) {
            gl.glClearColor(backgroundColor.r, backgroundColor.g,
                    backgroundColor.b, backgroundColor.a);
            gl.glBindFramebufferEXT(
                    GL.GL_FRAMEBUFFER_EXT, fboID);
        }
        active++;
    }

    private void deactivate() {
        final GL gl = GLU.getCurrentGL();

        if (!isSupported) {
            return;
        }
        if (active == 1) {
            gl.glClearColor(parentRenderer.getBackgroundColor().r,
                    parentRenderer.getBackgroundColor().g, parentRenderer
                            .getBackgroundColor().b, parentRenderer
                            .getBackgroundColor().a);
            gl.glBindFramebufferEXT(
                    GL.GL_FRAMEBUFFER_EXT, 0);
        }
        active--;
    }

    private void initCamera() {
        if (!isSupported) {
            return;
        }
        logger.info("Init RTT camera");
        camera = new JOGLCamera(width, height, false);
        camera.setFrustum(1.0f, 1000.0f, -0.50f, 0.50f, 0.50f, -0.50f);
        Vector3f loc = new Vector3f(0.0f, 0.0f, 0.0f);
        Vector3f left = new Vector3f(-1.0f, 0.0f, 0.0f);
        Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
        Vector3f dir = new Vector3f(0.0f, 0f, -1.0f);
        camera.setFrame(loc, left, up, dir);
        camera.setDataOnly(false);
    }

    public void cleanup() {
        final GL gl = GLU.getCurrentGL();

        if (!isSupported) {
            return;
        }

        if (fboID > 0) {
            IntBuffer id = BufferUtils.createIntBuffer(1);
            id.put(fboID);
            id.rewind();
            gl.glDeleteFramebuffersEXT(id.limit(),id); // TODO Check <size>
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
