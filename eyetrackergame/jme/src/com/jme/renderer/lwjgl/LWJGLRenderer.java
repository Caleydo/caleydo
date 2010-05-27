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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.ARBBufferObject;
import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.EXTFogCoord;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.OpenGLException;
import org.lwjgl.util.glu.GLU;

import com.jme.curve.Curve;
import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.RenderContext;
import com.jme.renderer.RenderQueue;
import com.jme.renderer.Renderer;
import com.jme.scene.Geometry;
import com.jme.scene.Line;
import com.jme.scene.Point;
import com.jme.scene.QuadMesh;
import com.jme.scene.Spatial;
import com.jme.scene.TexCoords;
import com.jme.scene.Text;
import com.jme.scene.TriMesh;
import com.jme.scene.VBOInfo;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.ClipState;
import com.jme.scene.state.ColorMaskState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.FogState;
import com.jme.scene.state.FragmentProgramState;
import com.jme.scene.state.GLSLShaderObjectsState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.ShadeState;
import com.jme.scene.state.StateRecord;
import com.jme.scene.state.StencilState;
import com.jme.scene.state.StippleState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.VertexProgramState;
import com.jme.scene.state.WireframeState;
import com.jme.scene.state.ZBufferState;
import com.jme.scene.state.lwjgl.LWJGLBlendState;
import com.jme.scene.state.lwjgl.LWJGLClipState;
import com.jme.scene.state.lwjgl.LWJGLColorMaskState;
import com.jme.scene.state.lwjgl.LWJGLCullState;
import com.jme.scene.state.lwjgl.LWJGLFogState;
import com.jme.scene.state.lwjgl.LWJGLFragmentProgramState;
import com.jme.scene.state.lwjgl.LWJGLLightState;
import com.jme.scene.state.lwjgl.LWJGLMaterialState;
import com.jme.scene.state.lwjgl.LWJGLShadeState;
import com.jme.scene.state.lwjgl.LWJGLShaderObjectsState;
import com.jme.scene.state.lwjgl.LWJGLStencilState;
import com.jme.scene.state.lwjgl.LWJGLStippleState;
import com.jme.scene.state.lwjgl.LWJGLTextureState;
import com.jme.scene.state.lwjgl.LWJGLVertexProgramState;
import com.jme.scene.state.lwjgl.LWJGLWireframeState;
import com.jme.scene.state.lwjgl.LWJGLZBufferState;
import com.jme.scene.state.lwjgl.records.LineRecord;
import com.jme.scene.state.lwjgl.records.RendererRecord;
import com.jme.scene.state.lwjgl.records.TextureStateRecord;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.Debug;
import com.jme.util.WeakIdentityCache;
import com.jme.util.geom.BufferUtils;
import com.jme.util.stat.StatCollector;
import com.jme.util.stat.StatType;

/**
 * <code>LWJGLRenderer</code> provides an implementation of the
 * <code>Renderer</code> interface using the LWJGL API.
 * 
 * @see com.jme.renderer.Renderer
 * @author Mark Powell - initial implementation, and more.
 * @author Joshua Slack - Further work, Optimizations, Headless rendering
 * @author Tijl Houtbeckers - Small optimizations and improved VBO
 * @version $Id: LWJGLRenderer.java 5165 2010-04-27 14:35:36Z andreas.grabner@gmail.com $
 */
public class LWJGLRenderer extends Renderer {
    private static final Logger logger = Logger.getLogger(LWJGLRenderer.class
            .getName());

    private Vector3f vRot = new Vector3f();

    private LWJGLFont font;

    private boolean supportsVBO = false;
    
    private boolean supportsFogCoords = false;

    private boolean indicesVBO = false;

    private boolean inOrthoMode;

    private Vector3f tempVa = new Vector3f();

    private FloatBuffer prevVerts;

    private FloatBuffer prevFogCoords;

    private FloatBuffer prevNorms;

    private FloatBuffer prevColor;

    private FloatBuffer[] prevTex;

    private int prevNormMode = GL11.GL_ZERO;

    protected ContextCapabilities capabilities;

    private int prevTextureNumber = 0;

    private boolean generatingDisplayList = false;

    protected WeakIdentityCache<Buffer, Integer> vboMap = new WeakIdentityCache<Buffer, Integer>();

    /**
     * Constructor instantiates a new <code>LWJGLRenderer</code> object. The
     * size of the rendering window is passed during construction.
     * 
     * @param width
     *            the width of the rendering context.
     * @param height
     *            the height of the rendering context.
     */
    public LWJGLRenderer(int width, int height) {
        if (width <= 0 || height <= 0) {
            logger.warning("Invalid width and/or height values.");
            throw new JmeException("Invalid width and/or height values.");
        }
        this.width = width;
        this.height = height;

        logger.log(Level.INFO, "LWJGLRenderer created. W: {0} H: {1}\tVersion: {2}"
                , new Object[] {width, height, org.lwjgl.Sys.getVersion()} );

        capabilities = GLContext.getCapabilities();

        queue = new RenderQueue(this);
        if (TextureState.getNumberOfTotalUnits() == -1)
            createTextureState(); // force units population
        prevTex = new FloatBuffer[TextureState.getNumberOfTotalUnits()];

        supportsVBO = capabilities.GL_ARB_vertex_buffer_object;
        
        supportsFogCoords = capabilities.GL_EXT_fog_coord;
    }

    /**
     * Reinitialize the renderer with the given width/height. Also calls resize
     * on the attached camera if present.
     * 
     * @param width
     *            int
     * @param height
     *            int
     */
    public void reinit(int width, int height) {
        if (width <= 0 || height <= 0) {
            logger.warning("Invalid width and/or height values.");
            throw new JmeException("Invalid width and/or height values.");
        }
        this.width = width;
        this.height = height;
        if (camera != null) {
            camera.resize(width, height);
            camera.apply();
        }
        capabilities = GLContext.getCapabilities();
    }

    /**
     * <code>setCamera</code> sets the camera this renderer is using. It
     * asserts that the camera is of type <code>LWJGLCamera</code>.
     * 
     * @see com.jme.renderer.Renderer#setCamera(com.jme.renderer.Camera)
     */
    public void setCamera(final Camera camera) {
        // Check that this isn't the same camera to avoid unnecessary work.
        if (camera == this.camera)
            return;

        if (camera instanceof LWJGLCamera) {
            this.camera = (LWJGLCamera) camera;

            // Update dimensions for the newly associated camera and apply the
            // changes.
            ((LWJGLCamera) this.camera).resize(width, height, true);
            this.camera.apply();
        }
    }

    /**
     * <code>createCamera</code> returns a default camera for use with the
     * LWJGL renderer.
     * 
     * @param width
     *            the width of the frame.
     * @param height
     *            the height of the frame.
     * @return a default LWJGL camera.
     */
    public Camera createCamera(int width, int height) {
        return new LWJGLCamera(width, height);
    }

    /**
     * <code>createBlendState</code> returns a new LWJGLBlendState object as a
     * regular BlendState.
     * 
     * @return an BlendState object.
     */
    public BlendState createBlendState() {
        return new LWJGLBlendState();
    }

    /**
     * <code>createCullState</code> returns a new LWJGLCullState object as a
     * regular CullState.
     * 
     * @return a CullState object.
     * @see com.jme.renderer.Renderer#createCullState()
     */
    public CullState createCullState() {
        return new LWJGLCullState();
    }

    /**
     * <code>createFogState</code> returns a new LWJGLFogState object as a
     * regular FogState.
     * 
     * @return an FogState object.
     */
    public FogState createFogState() {
        return new LWJGLFogState();
    }

    /**
     * <code>createLightState</code> returns a new LWJGLLightState object as a
     * regular LightState.
     * 
     * @return an LightState object.
     */
    public LightState createLightState() {
        return new LWJGLLightState();
    }

    /**
     * <code>createMaterialState</code> returns a new LWJGLMaterialState
     * object as a regular MaterialState.
     * 
     * @return an MaterialState object.
     */
    public MaterialState createMaterialState() {
        return new LWJGLMaterialState();
    }

    /**
     * <code>createShadeState</code> returns a new LWJGLShadeState object as a
     * regular ShadeState.
     * 
     * @return an ShadeState object.
     */
    public ShadeState createShadeState() {
        return new LWJGLShadeState();
    }

    /**
     * <code>createTextureState</code> returns a new LWJGLTextureState object
     * as a regular TextureState.
     * 
     * @return an TextureState object.
     */
    public TextureState createTextureState() {
        return new LWJGLTextureState();
    }

    /**
     * <code>createWireframeState</code> returns a new LWJGLWireframeState
     * object as a regular WireframeState.
     * 
     * @return an WireframeState object.
     */
    public WireframeState createWireframeState() {
        return new LWJGLWireframeState();
    }

    /**
     * <code>createZBufferState</code> returns a new LWJGLZBufferState object
     * as a regular ZBufferState.
     * 
     * @return a ZBufferState object.
     */
    public ZBufferState createZBufferState() {
        return new LWJGLZBufferState();
    }

    /**
     * <code>createVertexProgramState</code> returns a new
     * LWJGLVertexProgramState object as a regular VertexProgramState.
     * 
     * @return a LWJGLVertexProgramState object.
     */
    public VertexProgramState createVertexProgramState() {
        return new LWJGLVertexProgramState();
    }

    /**
     * <code>createFragmentProgramState</code> returns a new
     * LWJGLFragmentProgramState object as a regular FragmentProgramState.
     * 
     * @return a LWJGLFragmentProgramState object.
     */
    public FragmentProgramState createFragmentProgramState() {
        return new LWJGLFragmentProgramState();
    }

    /**
     * <code>createShaderObjectsState</code> returns a new
     * LWJGLShaderObjectsState object as a regular ShaderObjectsState.
     * 
     * @return an ShaderObjectsState object.
     */
    public GLSLShaderObjectsState createGLSLShaderObjectsState() {
        return new LWJGLShaderObjectsState();
    }

    /**
     * <code>createStencilState</code> returns a new LWJGLStencilState object
     * as a regular StencilState.
     * 
     * @return a StencilState object.
     */
    public StencilState createStencilState() {
        return new LWJGLStencilState();
    }

    /**
     * <code>createClipState</code> returns a new LWJGLClipState object as a
     * regular ClipState.
     * 
     * @return a ClipState object.
     * @see com.jme.renderer.Renderer#createClipState()
     */
    public ClipState createClipState() {
        return new LWJGLClipState();
    }

    /**
     * <code>createColorMaskState</code> returns a new LWJGLColorMaskState
     * object as a regular ColorMaskState.
     * 
     * @return a ColorMaskState object.
     */
    public ColorMaskState createColorMaskState() {
        return new LWJGLColorMaskState();
    }


    /**
     * <code>createStippleState</code> returns a new LWJGLStippleState
     * object as a regular StippleState.
     * 
     * @return a StippleState object.
     */
    @Override
    public StippleState createStippleState() {
    	return new LWJGLStippleState();
    }
    
    /**
     * <code>setBackgroundColor</code> sets the OpenGL clear color to the
     * color specified.
     * 
     * @see com.jme.renderer.Renderer#setBackgroundColor(com.jme.renderer.ColorRGBA)
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
        GL11.glClearColor(backgroundColor.r, backgroundColor.g,
                backgroundColor.b, backgroundColor.a);
    }

    /**
     * <code>clearZBuffer</code> clears the OpenGL depth buffer.
     * 
     * @see com.jme.renderer.Renderer#clearZBuffer()
     */
    public void clearZBuffer() {
        if (Renderer.defaultStateList[RenderState.StateType.ZBuffer.ordinal()] != null)
            Renderer.defaultStateList[RenderState.StateType.ZBuffer.ordinal()].apply();
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
    }

    /**
     * <code>clearBackBuffer</code> clears the OpenGL color buffer.
     * 
     * @see com.jme.renderer.Renderer#clearColorBuffer()
     */
    public void clearColorBuffer() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
    }

    /**
     * <code>clearStencilBuffer</code>
     * 
     * @see com.jme.renderer.Renderer#clearStencilBuffer()
     */
    public void clearStencilBuffer() {
        // Clear the stencil buffer
        GL11.glClearStencil(0);
        GL11.glStencilMask(~0);
        GL11.glDisable(GL11.GL_DITHER);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(0, 0, getWidth(), getHeight());
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    /**
     * <code>clearBuffers</code> clears both the color and the depth buffer.
     * 
     * @see com.jme.renderer.Renderer#clearBuffers()
     */
    public void clearBuffers() {
        // make sure no funny business is going on in the z before clearing.
        if (Renderer.defaultStateList[RenderState.StateType.ZBuffer.ordinal()] != null) {
            Renderer.defaultStateList[RenderState.StateType.ZBuffer.ordinal()].setNeedsRefresh(true);
            Renderer.defaultStateList[RenderState.StateType.ZBuffer.ordinal()].apply();
        }
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    /**
     * <code>clearBuffers</code> clears both the color and the depth buffer
     * for only the part of the buffer defined by the renderer width/height.
     * 
     * @see com.jme.renderer.Renderer#clearBuffers()
     */
    public void clearStrictBuffers() {
        GL11.glDisable(GL11.GL_DITHER);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(0, 0, width, height);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glEnable(GL11.GL_DITHER);
    }

    /**
     * <code>displayBackBuffer</code> renders any queued items then flips the
     * rendered buffer (back) with the currently displayed buffer.
     * 
     * @see com.jme.renderer.Renderer#displayBackBuffer()
     */
    public void displayBackBuffer() {
        renderQueue();

        Renderer.defaultStateList[RenderState.StateType.ColorMask.ordinal()].apply();

        reset();

        GL11.glFlush();
        if (!isHeadless()) {
            if (Debug.stats) {
                StatCollector.startStat(StatType.STAT_DISPLAYSWAP_TIMER);
            }
            Display.update();
            if (Debug.stats) {
                StatCollector.endStat(StatType.STAT_DISPLAYSWAP_TIMER);
            }
        }

        vboMap.expunge();
        
        if (Debug.stats) {
            StatCollector.addStat(StatType.STAT_FRAMES, 1);
        }
    }

    // XXX: look more at this
    public void reset() {
        prevColor = prevNorms = prevVerts = prevFogCoords = null;
        Arrays.fill(prevTex, null);
    }

    public boolean isInOrthoMode() {
        return inOrthoMode;
    }

    /**
     * <code>setOrtho</code> sets the display system to be in orthographic
     * mode. If the system has already been set to orthographic mode a
     * <code>JmeException</code> is thrown. The origin (0,0) is the bottom
     * left of the screen.
     */
    public void setOrtho() {
        if (inOrthoMode) {
            throw new JmeException("Already in Orthographic mode.");
        }
        // set up ortho mode
        RendererRecord matRecord = (RendererRecord) DisplaySystem
                .getDisplaySystem().getCurrentContext().getRendererRecord();
        matRecord.switchMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        float viewportWidth = width * (camera.getViewPortRight() - camera.getViewPortLeft());
        float viewportHeight = height * (camera.getViewPortTop() - camera.getViewPortBottom());
        GLU.gluOrtho2D(0, viewportWidth, 0, viewportHeight);
        matRecord.switchMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        inOrthoMode = true;
    }

    public void setOrthoCenter() {
        if (inOrthoMode) {
            throw new JmeException("Already in Orthographic mode.");
        }
        // set up ortho mode
        RendererRecord matRecord = (RendererRecord) DisplaySystem
                .getDisplaySystem().getCurrentContext().getRendererRecord();
        matRecord.switchMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GLU.gluOrtho2D(-width / 2f, width / 2f, -height / 2f, height / 2f);
        matRecord.switchMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        inOrthoMode = true;
    }

    /**
     * <code>setOrthoCenter</code> sets the display system to be in
     * orthographic mode. If the system has already been set to orthographic
     * mode a <code>JmeException</code> is thrown. The origin (0,0) is the
     * center of the screen.
     */
    public void unsetOrtho() {
        if (!inOrthoMode) {
            throw new JmeException("Not in Orthographic mode.");
        }
        // remove ortho mode, and go back to original
        // state
        RendererRecord matRecord = (RendererRecord) DisplaySystem
                .getDisplaySystem().getCurrentContext().getRendererRecord();
        matRecord.switchMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        matRecord.switchMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();
        inOrthoMode = false;
    }

    /**
     * <code>takeScreenShot</code> saves the current buffer to a file. The
     * file name is provided, and .png will be appended. True is returned if the
     * capture was successful, false otherwise.
     * 
     * @param filename
     *            the name of the file to save.
     */
    public void takeScreenShot(final String filename) {
        if (null == filename) {
            throw new JmeException("Screenshot filename cannot be null");
        }
        final ByteBuffer buff = BufferUtils.createByteBuffer(width * height * 3);
        grabScreenContents(buff, Image.Format.RGB8, 0, 0, width, height);
        final int w = width;
        final int h = height;
                
        Thread saveThread = new Thread() {
            
            public void run() {
            	BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

                // Grab each pixel information and set it to the BufferedImage info.
                for (int x = 0; x < w; x++) {
                    for (int y = 0; y < h; y++) {
                        
                        int index = 3 * ((h- y - 1) * w + x);
                        int argb = (((int) (buff.get(index+0)) & 0xFF) << 16) //r
                                 | (((int) (buff.get(index+1)) & 0xFF) << 8)  //g
                                 | (((int) (buff.get(index+2)) & 0xFF));      //b

                        img.setRGB(x, y, argb);
                    }
                }

                // write out the screenshot image to a file.
                try {
                    File out = new File(filename + ".png");
                    logger.log(Level.INFO, "Taking screenshot: {0}", out.getAbsolutePath());
                    ImageIO.write(img, "png", out);
                } catch (IOException e) {
                    logger.warning("Could not create file: " + filename + ".png");
                }
            }
        };
        saveThread.start();
    }

    /**
     * <code>grabScreenContents</code> reads a block of pixels from the
     * current framebuffer.
     * 
     * @param buff
     *            a buffer to store contents in.
     * @param format
     *            the format to read
     * @param x -
     *            x starting point of block
     * @param y -
     *            y starting point of block
     * @param w -
     *            width of block
     * @param h -
     *            height of block
     */
    public void grabScreenContents(ByteBuffer buff, Image.Format format, int x,
            int y, int w, int h) {
        int pixFormat = TextureStateRecord.getGLPixelFormat(format);
        GL11.glReadPixels(x, y, w, h, pixFormat, GL11.GL_UNSIGNED_BYTE, buff);
    }

    /**
     * <code>draw</code> renders a curve object.
     * 
     * @param curve
     *            the curve object to render.
     */
    public void draw(Curve curve) {
        // set world matrix
        Quaternion rotation = curve.getWorldRotation();
        Vector3f translation = curve.getWorldTranslation();
        Vector3f scale = curve.getWorldScale();
        float rot = rotation.toAngleAxis(vRot) * FastMath.RAD_TO_DEG;
        RendererRecord matRecord = (RendererRecord) DisplaySystem
                .getDisplaySystem().getCurrentContext().getRendererRecord();
        matRecord.switchMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();

        GL11.glTranslatef(translation.x, translation.y, translation.z);
        GL11.glRotatef(rot, vRot.x, vRot.y, vRot.z);
        GL11.glScalef(scale.x, scale.y, scale.z);

        applyStates(curve.states, null);

        // render the object
        GL11.glBegin(GL11.GL_LINE_STRIP);

        FloatBuffer color = curve.getColorBuffer();
        if (color != null)
            color.rewind();
        float colorInterval = 0;
        float colorModifier = 0;
        if (null != color) {
            matRecord.setCurrentColor(color.get(), color.get(), color.get(),
                    color.get());

            colorInterval = 4f / color.limit();
            colorModifier = colorInterval;
            color.rewind();
        }

        Vector3f point;
        float limit = (1 + (1.0f / curve.getSteps()));
        for (float t = 0; t <= limit; t += 1.0f / curve.getSteps()) {

            if (t >= colorInterval && color != null) {

                colorInterval += colorModifier;
                matRecord.setCurrentColor(color.get(), color.get(),
                        color.get(), color.get());
            }

            point = curve.getPoint(t, tempVa);
            GL11.glVertex3f(point.x, point.y, point.z);
        }

        if (Debug.stats) {
            StatCollector.addStat(StatType.STAT_VERTEX_COUNT, limit);
        }

        GL11.glEnd();
        undoTransforms(curve);
    }

    /**
     * <code>draw</code> renders a <code>Line</code> object including it's
     * normals, colors, textures and vertices.
     * 
     * @see Renderer#draw(Line)
     * @param lines
     *            the lines to render.
     */
    public void draw(Line lines) {
        if (!lines.predraw(this))
            return;

        if (Debug.stats) {
            StatCollector.addStat(StatType.STAT_LINE_COUNT, 1);
            StatCollector.addStat(StatType.STAT_VERTEX_COUNT, lines.getVertexCount());
            StatCollector.addStat(StatType.STAT_GEOM_COUNT, 1);
        }

        if (lines.getDisplayListID() != -1) {
            renderDisplayList(lines);
            return;
        }

        if (!generatingDisplayList)
            applyStates(lines.states, lines);
        if (Debug.stats) {
            StatCollector.startStat(StatType.STAT_RENDER_TIMER);
        }
        boolean transformed = doTransforms(lines);
        int mode = GL11.GL_LINES;
        switch (lines.getMode()) {
            case Segments:
                mode = GL11.GL_LINES;
                break;
            case Connected:
                mode = GL11.GL_LINE_STRIP;
                break;
            case Loop:
                mode = GL11.GL_LINE_LOOP;
                break;
        }

        LineRecord lineRecord = (LineRecord) DisplaySystem.getDisplaySystem()
                .getCurrentContext().getLineRecord();
        lineRecord.applyLineWidth(lines.getLineWidth());
        lineRecord.applyLineStipple(lines.getStippleFactor(), lines
                .getStipplePattern());
        lineRecord.applyLineSmooth(lines.isAntialiased());
        if (!lineRecord.isValid())
            lineRecord.validate();

        if (!predrawGeometry(lines)) {
            // make sure only the necessary indices are sent through on old
            // cards.
            IntBuffer indices = lines.getIndexBuffer();
            indices.rewind();
            indices.limit(lines.getVertexCount());

            GL11.glDrawElements(mode, indices);

            indices.clear();
        } else {
            GL11.glDrawElements(mode, lines.getIndexBuffer().limit(),
                    GL11.GL_UNSIGNED_INT, 0);
        }

        postdrawGeometry(lines);
        if (transformed) undoTransforms(lines);

        if (Debug.stats) {
            StatCollector.endStat(StatType.STAT_RENDER_TIMER);
        }
        lines.postdraw(this);
    }

    /**
     * <code>draw</code> renders a <code>Point</code> object including it's
     * normals, colors, textures and vertices.
     * 
     * @see Renderer#draw(Point)
     * @param points
     *            the points to render.
     */
    public void draw(Point points) {
        if (!points.predraw(this))
            return;

        if (Debug.stats) {
            StatCollector.addStat(StatType.STAT_POINT_COUNT, 1);
            StatCollector.addStat(StatType.STAT_VERTEX_COUNT, points.getVertexCount());
            StatCollector.addStat(StatType.STAT_GEOM_COUNT, 1);
        }

        if (points.getDisplayListID() != -1) {
            renderDisplayList(points);
            return;
        }

        if (!generatingDisplayList)
            applyStates(points.states, points);
        if (Debug.stats) {
            StatCollector.startStat(StatType.STAT_RENDER_TIMER);
        }
        boolean transformed = doTransforms(points);

        GL11.glPointSize(points.getPointSize());
        if (points.isAntialiased()) {
            GL11.glEnable(GL11.GL_POINT_SMOOTH);
            GL11.glHint(GL11.GL_POINT_SMOOTH_HINT, GL11.GL_NICEST);
        }

        if (!predrawGeometry(points)) {
            // make sure only the necessary indices are sent through on old
            // cards.
            IntBuffer indices = points.getIndexBuffer();
            indices.rewind();
            indices.limit(points.getVertexCount());

            GL11.glDrawElements(GL11.GL_POINTS, indices);

            indices.clear();
        } else {
            GL11.glDrawElements(GL11.GL_POINTS,
                    points.getIndexBuffer().limit(), GL11.GL_UNSIGNED_INT, 0);
        }

        if (points.isAntialiased()) {
            GL11.glDisable(GL11.GL_POINT_SMOOTH);
        }

        postdrawGeometry(points);
        if (transformed) undoTransforms(points);

        if (Debug.stats) {
            StatCollector.endStat(StatType.STAT_RENDER_TIMER);
        }
        points.postdraw(this);
    }

    /**
     * <code>draw</code> renders a <code>QuadMesh</code> object including
     * it's normals, colors, textures and vertices.
     * 
     * @see Renderer#draw(QuadMesh)
     * @param quads
     *            the mesh to render.
     */
    public void draw(QuadMesh quads) {
        if (!quads.predraw(this))
            return;

        if (Debug.stats) {
            StatCollector.addStat(StatType.STAT_QUAD_COUNT, quads.getQuadCount());
            StatCollector.addStat(StatType.STAT_VERTEX_COUNT, quads.getVertexCount());
            StatCollector.addStat(StatType.STAT_GEOM_COUNT, 1);
        }

        if (quads.getDisplayListID() != -1) {
            renderDisplayList(quads);
            return;
        }

        if (!generatingDisplayList)
            applyStates(quads.states, quads);
        if (Debug.stats) {
            StatCollector.startStat(StatType.STAT_RENDER_TIMER);
        }
        boolean transformed = doTransforms(quads);
        int glMode = GL11.GL_QUADS;
        switch (quads.getMode()) {
            case Quads:
                glMode = GL11.GL_QUADS;
                break;
            case Strip:
                glMode = GL11.GL_QUAD_STRIP;
                break;
        }

        if (!predrawGeometry(quads)) {
            // make sure only the necessary indices are sent through on old
            // cards.
            IntBuffer indices = quads.getIndexBuffer();
            indices.rewind();
            indices.limit(quads.getMaxIndex());

            GL11.glDrawElements(glMode, indices);

            indices.clear();
        } else {

            GL11.glDrawElements(glMode, quads.getIndexBuffer().limit(),
                    GL11.GL_UNSIGNED_INT, 0);

        }

        postdrawGeometry(quads);
        if (transformed) undoTransforms(quads);

        if (Debug.stats) {
            StatCollector.endStat(StatType.STAT_RENDER_TIMER);
        }
        quads.postdraw(this);
    }

    /**
     * <code>draw</code> renders a <code>TriMesh</code> object including
     * it's normals, colors, textures and vertices.
     * 
     * @see Renderer#draw(TriMesh)
     * @param tris
     *            the mesh to render.
     */
    public void draw(TriMesh tris) {
        if (!tris.predraw(this))
            return;
        if (Debug.stats) {
            StatCollector.addStat(StatType.STAT_TRIANGLE_COUNT, tris.getTriangleCount());
            StatCollector.addStat(StatType.STAT_VERTEX_COUNT, tris.getVertexCount());
            StatCollector.addStat(StatType.STAT_GEOM_COUNT, 1);
        }

        if (tris.getDisplayListID() != -1) {
            renderDisplayList(tris);
            return;
        }

        if (!generatingDisplayList) {
            applyStates(tris.states, tris);
        }
        if (Debug.stats) {
            StatCollector.startStat(StatType.STAT_RENDER_TIMER);
        }
        boolean transformed = doTransforms(tris);

        int glMode = GL11.GL_TRIANGLES;
        switch (tris.getMode()) {
            case Triangles:
                glMode = GL11.GL_TRIANGLES;
                break;
            case Strip:
                glMode = GL11.GL_TRIANGLE_STRIP;
                break;
            case Fan:
                glMode = GL11.GL_TRIANGLE_FAN;
                break;
        }

        if (!predrawGeometry(tris)) {
            // make sure only the necessary indices are sent through on old
            // cards.
            IntBuffer indices = tris.getIndexBuffer();
            if (indices == null) {
                logger.log(Level.SEVERE, "missing indices on geometry object: {0}",
                 tris.toString());
            } else {
                indices.rewind();
                indices.limit(tris.getMaxIndex());

                GL11.glDrawElements(glMode, indices);

                indices.clear();
            }
        } else {
            GL11.glDrawElements(glMode, tris.getIndexBuffer().limit(),
                    GL11.GL_UNSIGNED_INT, 0);
        }

        postdrawGeometry(tris);
        if (transformed) undoTransforms(tris);

        if (Debug.stats) {
            StatCollector.endStat(StatType.STAT_RENDER_TIMER);
        }
        tris.postdraw(this);
    }

    private synchronized void renderDisplayList(Geometry geom) {
        applyStates(geom.states, geom);
        
        if (Debug.stats) {
            StatCollector.startStat(StatType.STAT_RENDER_TIMER);
        }
        if ((geom.getLocks() & Spatial.LOCKED_TRANSFORMS) == 0) {
            boolean transformed = doTransforms(geom);
            GL11.glCallList(geom.getDisplayListID());
            if (transformed) undoTransforms(geom);
        } else {
            GL11.glCallList(geom.getDisplayListID());
        }
        // invalidate line record as we do not know the line state anymore
        ((LineRecord) DisplaySystem.getDisplaySystem().getCurrentContext()
                .getLineRecord()).invalidate();
        // invalidate "current arrays"
        reset();
        if (Debug.stats) {
            StatCollector.endStat(StatType.STAT_RENDER_TIMER);
        }
    }

    /**
     * <code>prepVBO</code> binds the geometry data to a vbo buffer and sends
     * it to the GPU if necessary. The vbo id is stored in the geometry's
     * VBOInfo class. If a new vbo id is created, the VBO is also stored in a
     * cache. Before creating a new VBO this cache will be checked to see if a
     * VBO is already created for that Buffer.
     * 
     * @param g
     *            the geometry to initialize VBO for.
     */
    protected void prepVBO(Geometry g) {
        if (!supportsVBO())
            return;
        RendererRecord rendRecord = (RendererRecord) DisplaySystem
                .getDisplaySystem().getCurrentContext().getRendererRecord();

        VBOInfo vbo = g.getVBOInfo();

        if (vbo.isVBOVertexEnabled() && vbo.getVBOVertexID() <= 0) {
            if (g.getVertexBuffer() != null) {

                Object vboid;
                if ((vboid = vboMap.get(g.getVertexBuffer())) != null) {
                    vbo.setVBOVertexID(((Integer) vboid).intValue());
                } else {
                    g.getVertexBuffer().rewind();
                    int vboID = rendRecord.makeVBOId();
                    vbo.setVBOVertexID(vboID);
                    vboMap.put(g.getVertexBuffer(), vboID);

                    // ensure no VBO is bound
                    rendRecord.invalidateVBO(); // make sure we set it...
                    rendRecord.setBoundVBO(vbo.getVBOVertexID());
                    ARBBufferObject.glBindBufferARB(
                            ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, vbo
                                    .getVBOVertexID());
                    ARBBufferObject.glBufferDataARB(
                            ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, g
                                    .getVertexBuffer(),
                            ARBBufferObject.GL_STATIC_DRAW_ARB);
                }
            }
        }

        if (g instanceof TriMesh) {

            if (vbo.isVBOIndexEnabled() && vbo.getVBOIndexID() <= 0) {
                TriMesh tb = (TriMesh) g;
                if (tb.getIndexBuffer() != null) {
                    Object vboid;
                    if ((vboid = vboMap.get(tb.getIndexBuffer())) != null) {
                        vbo.setVBOIndexID(((Integer) vboid).intValue());
                    } else {
                        tb.getIndexBuffer().rewind();
                        int vboID = rendRecord.makeVBOId();
                        vbo.setVBOIndexID(vboID);
                        vboMap.put(tb.getIndexBuffer(), vboID);

                        rendRecord.invalidateVBO(); // make sure we set it...
                        rendRecord.setBoundElementVBO(vbo.getVBOIndexID());
                        ARBBufferObject
                                .glBufferDataARB(
                                        ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB,
                                        tb.getIndexBuffer(),
                                        ARBBufferObject.GL_STATIC_DRAW_ARB);

                    }
                }
            }
        }

        if (vbo.isVBONormalEnabled() && vbo.getVBONormalID() <= 0) {
            if (g.getNormalBuffer() != null) {

                Object vboid;
                if ((vboid = vboMap.get(g.getNormalBuffer())) != null) {
                    vbo.setVBONormalID(((Integer) vboid).intValue());
                } else {
                    g.getNormalBuffer().rewind();
                    int vboID = rendRecord.makeVBOId();
                    vbo.setVBONormalID(vboID);
                    vboMap.put(g.getNormalBuffer(), vboID);

                    rendRecord.invalidateVBO(); // make sure we set it...
                    rendRecord.setBoundVBO(vbo.getVBONormalID());
                    ARBBufferObject.glBufferDataARB(
                            ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, g
                                    .getNormalBuffer(),
                            ARBBufferObject.GL_STATIC_DRAW_ARB);
                }
            }
        }
        if (vbo.isVBOColorEnabled() && vbo.getVBOColorID() <= 0) {
            if (g.getColorBuffer() != null) {
                Object vboid;
                if ((vboid = vboMap.get(g.getColorBuffer())) != null) {
                    vbo.setVBOColorID(((Integer) vboid).intValue());
                } else {
                    g.getColorBuffer().rewind();
                    int vboID = rendRecord.makeVBOId();
                    vbo.setVBOColorID(vboID);
                    vboMap.put(g.getColorBuffer(), vboID);

                    rendRecord.invalidateVBO(); // make sure we set it...
                    rendRecord.setBoundVBO(vbo.getVBOColorID());
                    ARBBufferObject.glBufferDataARB(
                            ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, g
                                    .getColorBuffer(),
                            ARBBufferObject.GL_STATIC_DRAW_ARB);
                }
            }
        }
        if (supportsFogCoords && vbo.isVBOFogCoordsEnabled() && vbo.getVBOFogCoordsID() <= 0) {
            if (g.getFogBuffer() != null) {
                Object vboid;
                if ((vboid = vboMap.get(g.getFogBuffer())) != null) {
                    vbo.setVBOFogCoordsID(((Integer) vboid).intValue());
                } else {
                    g.getFogBuffer().rewind();
                    int vboID = rendRecord.makeVBOId();
                    vbo.setVBOFogCoordsID(vboID);
                    vboMap.put(g.getFogBuffer(), vboID);

                    rendRecord.invalidateVBO(); // make sure we set it...
                    rendRecord.setBoundVBO(vbo.getVBOFogCoordsID());
                    ARBBufferObject.glBufferDataARB(
                            ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, g
                                    .getFogBuffer(),
                            ARBBufferObject.GL_STATIC_DRAW_ARB);
                }
            }
        }
        if (vbo.isVBOTextureEnabled()) {
            for (int i = 0; i < g.getNumberOfUnits(); i++) {

                if (vbo.getVBOTextureID(i) <= 0
                        && g.getTextureCoords(i) != null) {
                    Object vboid;
                    TexCoords texC = g.getTextureCoords(i);
                    if ((vboid = vboMap.get(texC.coords)) != null) {
                        vbo.setVBOTextureID(i, ((Integer) vboid).intValue());
                    } else {
                        texC.coords.rewind();
                        int vboID = rendRecord.makeVBOId();
                        vbo.setVBOTextureID(i, vboID);
                        vboMap.put(texC.coords, vboID);

                        rendRecord.invalidateVBO(); // make sure we set it...
                        rendRecord.setBoundVBO(vbo.getVBOTextureID(i));
                        ARBBufferObject.glBufferDataARB(
                                ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, texC.coords,
                                ARBBufferObject.GL_STATIC_DRAW_ARB);
                    }
                }
            }
        }
    }

    /**
     * <code>draw</code> renders a scene by calling the nodes
     * <code>onDraw</code> method.
     * 
     * @see com.jme.renderer.Renderer#draw(com.jme.scene.Spatial)
     */
    public void draw(final Spatial s) {
        if (camera != null)
            camera.apply();

        if (s != null) {
            s.onDraw(this);
        }
    }

    /**
     * <code>draw</code> renders a text object using a predefined font.
     * 
     * @see com.jme.renderer.Renderer#draw(com.jme.scene.Text)
     */
    public void draw(Text t) {
        if (font == null) {
            font = new LWJGLFont();
        }
        font.setColor(t.getTextColor());
        applyStates(t.states, null);
        if (Debug.stats) {
            StatCollector.startStat(StatType.STAT_RENDER_TIMER);
        }
        
        font.print(this, t.getWorldTranslation().x, t
                .getWorldTranslation().y, t.getWorldScale(), t.getText(), 0);
        
        if (Debug.stats) {
            StatCollector.endStat(StatType.STAT_RENDER_TIMER);
        }
    }

    /**
     * checkAndAdd is used to process the Spatial for the render queue.
     * It's queue mode is checked, and it is added to the proper queue. If the
     * queue mode is QUEUE_SKIP, false is returned.
     * 
     * @return true if the Spatial was added to a queue, false otherwise.
     */
    public boolean checkAndAdd(Spatial s) {
        int rqMode = s.getRenderQueueMode();
        if (rqMode != Renderer.QUEUE_SKIP) {
            getQueue().addToQueue(s, rqMode);
            return true;
        }
        return false;
    }

    /**
     * Return true if the system running this supports VBO
     * 
     * @return boolean true if VBO supported
     */
    public boolean supportsVBO() {
        return supportsVBO;
    }

    /**
     * re-initializes the GL context for rendering of another piece of geometry.
     */
    protected void postdrawGeometry(Geometry g) {
        // Nothing to do here
    }

    /**
     * <code>flush</code> tells opengl to send through all currently waiting
     * commands in the buffer.
     */
    public void flush() {
        GL11.glFlush();
    }

    /**
     * <code>finish</code> is similar to flush, however it blocks until all
     * waiting OpenGL commands have been finished.
     */
    public void finish() {
        GL11.glFinish();
    }

    /**
     * Prepares the GL Context for rendering this geometry. This involves
     * initializing the VBO and obtaining the buffer data.
     * 
     * @param g
     *            the geometry to process.
     * @return true if VBO is used for indicis, false if not
     */
    protected boolean predrawGeometry(Geometry g) {
        RenderContext<?> context = DisplaySystem.getDisplaySystem()
                .getCurrentContext();
        RendererRecord rendRecord = (RendererRecord) context
                .getRendererRecord();

        VBOInfo vbo = !generatingDisplayList ? g.getVBOInfo() : null;
        if (vbo != null && supportsVBO()) {
            prepVBO(g);
        }

        indicesVBO = false;

        // set up data to be sent to card
        // first to go is vertices
        int oldLimit = -1;
        FloatBuffer vertices = g.getVertexBuffer();
        if (vertices != null) {
            oldLimit = vertices.limit();
            // make sure only the necessary verts are sent through on old cards.
            vertices.limit(g.getVertexCount() * 3);
        }
        if ((supportsVBO && vbo != null && vbo.getVBOVertexID() > 0)) { // use
            // VBO
            GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
            rendRecord.setBoundVBO(vbo.getVBOVertexID());
            GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0);
        } else if (vertices == null) {
            GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        } else if (prevVerts != vertices) {
            // verts have changed
            GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
            // ensure no VBO is bound
            if (supportsVBO)
                rendRecord.setBoundVBO(0);
            vertices.rewind();
            GL11.glVertexPointer(3, 0, vertices);
        }

        if (oldLimit != -1)
            vertices.limit(oldLimit);
        prevVerts = vertices;

        // apply fogging coordinate if support and the buffer is set for this
        // tri mesh
        if (supportsFogCoords) {
            oldLimit = -1;
            FloatBuffer fogCoords = g.getFogBuffer();
            if (fogCoords != null) {
                oldLimit = fogCoords.limit();
                // make sure only the necessary verts are sent through on old cards.
                fogCoords.limit(g.getVertexCount());
            }
            if ((supportsVBO && vbo != null && vbo.getVBOVertexID() > 0)) { // use
                // VBO
                GL11.glEnableClientState(EXTFogCoord.GL_FOG_COORDINATE_ARRAY_EXT);
                rendRecord.setBoundVBO(vbo.getVBOVertexID());
                EXTFogCoord.glFogCoordPointerEXT(GL11.GL_FLOAT, 0, 0);
            } else if (fogCoords == null) {
                GL11.glDisableClientState(EXTFogCoord.GL_FOG_COORDINATE_ARRAY_EXT);
            } else if (prevFogCoords != fogCoords) {
                // fog coords have changed
                GL11.glEnableClientState(EXTFogCoord.GL_FOG_COORDINATE_ARRAY_EXT);
                // ensure no VBO is bound
                if (supportsVBO)
                    rendRecord.setBoundVBO(0);
                fogCoords.rewind();
                EXTFogCoord.glFogCoordPointerEXT(0, g.getFogBuffer());
            }
            if (oldLimit != -1)
                fogCoords.limit(oldLimit);
            prevFogCoords = fogCoords;
        }

        if (g instanceof TriMesh) {
            if ((supportsVBO && vbo != null && vbo.getVBOIndexID() > 0)) { // use VBO
                indicesVBO = true;
                rendRecord.setBoundElementVBO(vbo.getVBOIndexID());
            } else if (supportsVBO) {
                rendRecord.setBoundElementVBO(0);
            }
        }

        Spatial.NormalsMode normMode = g.getNormalsMode();
        if (normMode != Spatial.NormalsMode.Off) {
            applyNormalMode(normMode, g);
            FloatBuffer normals = g.getNormalBuffer();
            oldLimit = -1;
            if (normals != null) {
                // make sure only the necessary normals are sent through on old
                // cards.
                oldLimit = normals.limit();
                normals.limit(g.getVertexCount() * 3);
            }
            if ((supportsVBO && vbo != null && vbo.getVBONormalID() > 0)) { // use
                // VBO
                GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
                rendRecord.setBoundVBO(vbo.getVBONormalID());
                GL11.glNormalPointer(GL11.GL_FLOAT, 0, 0);
            } else if (normals == null) {
                GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
            } else if (prevNorms != normals) {
                // textures have changed
                GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
                // ensure no VBO is bound
                if (supportsVBO)
                    rendRecord.setBoundVBO(0);
                normals.rewind();
                GL11.glNormalPointer(0, normals);
            }
            if (oldLimit != -1)
                normals.limit(oldLimit);
            prevNorms = normals;
        } else {
            if (prevNormMode == GL12.GL_RESCALE_NORMAL) {
                GL11.glDisable(GL12.GL_RESCALE_NORMAL);
                prevNormMode = GL11.GL_ZERO;
            } else if (prevNormMode == GL11.GL_NORMALIZE) {
                GL11.glDisable(GL11.GL_NORMALIZE);
                prevNormMode = GL11.GL_ZERO;
            }
            oldLimit = -1;
            GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
            prevNorms = null;
        }

        FloatBuffer colors = g.getColorBuffer();
        oldLimit = -1;
        if (colors != null) {
            // make sure only the necessary colors are sent through on old
            // cards.
            oldLimit = colors.limit();
            colors.limit(g.getVertexCount() * 4);
        }
        if ((supportsVBO && vbo != null && vbo.getVBOColorID() > 0)) { // use
            // VBO
            GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
            rendRecord.setBoundVBO(vbo.getVBOColorID());
            GL11.glColorPointer(4, GL11.GL_FLOAT, 0, 0);
        } else if (colors == null) {
            GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);

            // Disabling a color array causes the current color to be undefined.
            // So enforce a current color here.
            ColorRGBA defCol = g.getDefaultColor();
            if (defCol != null) {
                rendRecord.setCurrentColor(defCol);
            } else {
                // no default color, so set to white.
                rendRecord.setCurrentColor(1, 1, 1, 1);
            }
        } else if (prevColor != colors) {
            // colors have changed
            GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
            // ensure no VBO is bound
            if (supportsVBO)
                rendRecord.setBoundVBO(0);
            colors.rewind();
            GL11.glColorPointer(4, 0, colors);
        }
        if (oldLimit != -1)
            colors.limit(oldLimit);
        prevColor = colors;

        TextureState ts = (TextureState) context.currentStates[RenderState.StateType.Texture.ordinal()];
        int offset = 0;
        if (ts != null) {
            offset = ts.getTextureCoordinateOffset();

            for (int i = 0; i < ts.getNumberOfSetTextures()
                    && i < TextureState.getNumberOfFragmentTexCoordUnits(); i++) {
                TexCoords texC = g.getTextureCoords(i + offset);
                oldLimit = -1;
                if (texC != null && texC.coords != null) {
                    // make sure only the necessary texture coords are sent
                    // through on old cards.
                    oldLimit = texC.coords.limit();
                    texC.coords.limit(g.getVertexCount() * texC.perVert);
                }
                if (capabilities.GL_ARB_multitexture) {
                    ARBMultitexture
                            .glClientActiveTextureARB(ARBMultitexture.GL_TEXTURE0_ARB
                                    + i);
                }
                if ((supportsVBO && vbo != null && vbo.getVBOTextureID(i) > 0)) { // use
                    // VBO
                    GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                    rendRecord.setBoundVBO(vbo.getVBOTextureID(i));
                    GL11.glTexCoordPointer(texC.perVert, GL11.GL_FLOAT, 0, 0);
                } else if (texC == null || texC.coords == null) {
                    GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                } else if (prevTex[i] != texC.coords) {
                    // textures have changed
                    GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                    // ensure no VBO is bound
                    if (supportsVBO)
                        rendRecord.setBoundVBO(0);
                    // set data
                    texC.coords.rewind();
                    GL11.glTexCoordPointer(texC.perVert, 0, texC.coords);
                } else {
                    GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                }
                prevTex[i] = (texC != null && texC.coords != null) ? texC.coords : null;
                if (oldLimit != -1)
                    texC.coords.limit(oldLimit);
            }

            if (ts.getNumberOfSetTextures() < prevTextureNumber) {
                for (int i = ts.getNumberOfSetTextures(); i < prevTextureNumber; i++) {
                    if (capabilities.GL_ARB_multitexture) {
                        ARBMultitexture
                                .glClientActiveTextureARB(ARBMultitexture.GL_TEXTURE0_ARB
                                        + i);
                    }
                    GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                }
            }

            prevTextureNumber = ts.getNumberOfSetTextures() < TextureState
                    .getNumberOfFixedUnits() ? ts.getNumberOfSetTextures()
                    : TextureState.getNumberOfFixedUnits();
        }
        
        return indicesVBO;
    }

    private void applyNormalMode(Spatial.NormalsMode normMode, Geometry t) {
        switch (normMode) {
            case NormalizeIfScaled:
                Vector3f scale = t.getWorldScale();
                if (!scale.equals(Vector3f.UNIT_XYZ)) {
                    if (scale.x == scale.y && scale.y == scale.z
                            && capabilities.OpenGL12
                            && prevNormMode != GL12.GL_RESCALE_NORMAL) {
                        if (prevNormMode == GL11.GL_NORMALIZE)
                            GL11.glDisable(GL11.GL_NORMALIZE);
                        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
                        prevNormMode = GL12.GL_RESCALE_NORMAL;
                    } else if (prevNormMode != GL11.GL_NORMALIZE) {
                        if (prevNormMode == GL12.GL_RESCALE_NORMAL)
                            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
                        GL11.glEnable(GL11.GL_NORMALIZE);
                        prevNormMode = GL11.GL_NORMALIZE;
                    }
                } else {
                    if (prevNormMode == GL12.GL_RESCALE_NORMAL) {
                        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
                        prevNormMode = GL11.GL_ZERO;
                    } else if (prevNormMode == GL11.GL_NORMALIZE) {
                        GL11.glDisable(GL11.GL_NORMALIZE);
                        prevNormMode = GL11.GL_ZERO;
                    }
                }
                break;
            case AlwaysNormalize:
                if (prevNormMode != GL11.GL_NORMALIZE) {
                    if (prevNormMode == GL12.GL_RESCALE_NORMAL)
                        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
                    GL11.glEnable(GL11.GL_NORMALIZE);
                    prevNormMode = GL11.GL_NORMALIZE;
                }
                break;
            case UseProvided:
            default:
                if (prevNormMode == GL12.GL_RESCALE_NORMAL) {
                    GL11.glDisable(GL12.GL_RESCALE_NORMAL);
                    prevNormMode = GL11.GL_ZERO;
                } else if (prevNormMode == GL11.GL_NORMALIZE) {
                    GL11.glDisable(GL11.GL_NORMALIZE);
                    prevNormMode = GL11.GL_ZERO;
                }
                break;
        }
    }

    protected boolean doTransforms(Spatial t) {
        // set world matrix
        if (!generatingDisplayList
                || (t.getLocks() & Spatial.LOCKED_TRANSFORMS) != 0) {
            boolean doT = false, doR = false, doS = false;
            
            Vector3f translation = t.getWorldTranslation();
            if (!translation.equals(Vector3f.ZERO)) {
                doT = true;
            }
    
            Quaternion rotation = t.getWorldRotation();
            if (!rotation.isIdentity()) {
                doR = true;
            }
            
            Vector3f scale = t.getWorldScale();
            if (!scale.equals(Vector3f.UNIT_XYZ)) {
                doS = true;
            }

            if (doT || doR || doS) {
                RendererRecord matRecord = (RendererRecord) DisplaySystem.getDisplaySystem().getCurrentContext().getRendererRecord();
                matRecord.switchMode(GL11.GL_MODELVIEW);
                GL11.glPushMatrix();
                if (doT)
                    GL11.glTranslatef(translation.x, translation.y, translation.z);
                if (doR) {
                    float rot = rotation.toAngleAxis(vRot) * FastMath.RAD_TO_DEG;
                    GL11.glRotatef(rot, vRot.x, vRot.y, vRot.z);
                }
                if (doS) {
                    GL11.glScalef(scale.x, scale.y, scale.z);
                }
                return true;
            }
        }
        return false;
    }

    protected void undoTransforms(Spatial t) {
        if (!generatingDisplayList
                || (t.getLocks() & Spatial.LOCKED_TRANSFORMS) != 0) {
            RendererRecord matRecord = (RendererRecord) DisplaySystem
                    .getDisplaySystem().getCurrentContext().getRendererRecord();
            matRecord.switchMode(GL11.GL_MODELVIEW);
            GL11.glPopMatrix();
        }
    }

    // inherited documentation
    public int createDisplayList(Geometry g) {
        int listID = GL11.glGenLists(1);

        generatingDisplayList = true;
        RenderContext<?> context = DisplaySystem.getDisplaySystem()
                .getCurrentContext();
        // invalidate states -- this makes sure things like line stipple get
        // called in list.
        context.invalidateStates();
        RenderState oldTS = context.currentStates[RenderState.StateType.Texture.ordinal()];
        context.currentStates[RenderState.StateType.Texture.ordinal()] = g.states[RenderState.StateType.Texture.ordinal()];
        GL11.glNewList(listID, GL11.GL_COMPILE);
        if (g instanceof TriMesh)
            draw((TriMesh) g);
        else if (g instanceof QuadMesh)
            draw((QuadMesh) g);
        else if (g instanceof Line)
            draw((Line) g);
        else if (g instanceof Point)
            draw((Point) g);
        GL11.glEndList();
        context.currentStates[RenderState.StateType.Texture.ordinal()] = oldTS;
        generatingDisplayList = false;

        return listID;
    }

    // inherited documentation
    public void releaseDisplayList(int listId) {
        GL11.glDeleteLists(listId, 1);
    }

    // inherited documentation
    public void setPolygonOffset(float factor, float offset) {
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_LINE);
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_POINT);
        GL11.glPolygonOffset(factor, offset);
    }

    // inherited documentation
    public void clearPolygonOffset() {
        GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glDisable(GL11.GL_POLYGON_OFFSET_LINE);
        GL11.glDisable(GL11.GL_POLYGON_OFFSET_POINT);
    }

    /**
     * @see Renderer#deleteVBO(Buffer)
     */
    public void deleteVBO(Buffer buffer) {
        Integer i = removeFromVBOCache(buffer);
        if (i != null)
            deleteVBO(i.intValue());
    }

    /**
     * @see Renderer#deleteVBO(int)
     */
    public void deleteVBO(int vboid) {
        if (vboid < 1 || !supportsVBO())
            return;
        RendererRecord rendRecord = (RendererRecord) DisplaySystem
                .getDisplaySystem().getCurrentContext().getRendererRecord();
        rendRecord.deleteVBOId(vboid);
    }

    /**
     * @see Renderer#clearVBOCache()
     */
    public void clearVBOCache() {
        vboMap.clear();
    }

    /**
     * @see Renderer#removeFromVBOCache(Buffer)
     */
    public Integer removeFromVBOCache(Buffer buffer) {
        return vboMap.remove(buffer);

    }

    /**
     * <code>setStates</code> applies the given states if and only if they are
     * different from the currently set states.
     */
    public void applyStates(RenderState[] states, Geometry geom) {
        if (Debug.stats) {
            StatCollector.startStat(StatType.STAT_STATES_TIMER);
        }

        RenderContext<?> context = DisplaySystem.getDisplaySystem()
                .getCurrentContext();

        // TODO: To be used for the attribute shader solution
        if (geom != null) {
            GLSLShaderObjectsState shaderState = (GLSLShaderObjectsState) (context.enforcedStateList[RenderState.StateType.GLSLShaderObjects.ordinal()] != null ? context.enforcedStateList[RenderState.StateType.GLSLShaderObjects.ordinal()]
                    : states[RenderState.StateType.GLSLShaderObjects.ordinal()]);
            if (shaderState != null
                    && shaderState != defaultStateList[RenderState.StateType.GLSLShaderObjects.ordinal()]) {
                shaderState.setGeometry(geom);
                shaderState.setNeedsRefresh(true);
            }
        }

        RenderState tempState = null;
        for (int i = 0; i < states.length; i++) {
            tempState = context.enforcedStateList[i] != null ? context.enforcedStateList[i]
                    : states[i];

            if (tempState != null) {
                if (!tempState.getStateType().canQuickCompare() || tempState.needsRefresh()
                        || tempState != context.currentStates[i]) {
                    tempState.apply();
                    tempState.setNeedsRefresh(false);
                }
            }
        }
        
        if (Debug.stats) {
            StatCollector.endStat(StatType.STAT_STATES_TIMER);
        }
    }

    @Override
    public StateRecord createLineRecord() {
        return new LineRecord();
    }

    @Override
    public StateRecord createRendererRecord() {
        return new RendererRecord();
    }

    @Override
    public void updateTextureSubImage(Texture dstTexture, int dstX, int dstY,
            Image srcImage, int srcX, int srcY, int width, int height)
            throws JmeException, UnsupportedOperationException {
        // Check that the texture type is supported.
        if (dstTexture.getType() != Texture.Type.TwoDimensional)
            throw new UnsupportedOperationException(
                    "Unsupported Texture Type: " + dstTexture.getType());

        // Determine the original texture configuration, so that this method can
        // restore the texture configuration to its original state.
        final IntBuffer intBuf = BufferUtils.createIntBuffer(16);
        GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D, intBuf);
        final int origTexBinding = intBuf.get(0);
        GL11.glGetInteger(GL11.GL_UNPACK_ALIGNMENT, intBuf);
        final int origAlignment = intBuf.get(0);
        final int origRowLength = 0;
        final int origSkipPixels = 0;
        final int origSkipRows = 0;

        int alignment = 1;
        int rowLength;
        if (srcImage.getWidth() == width) {
            // When the row length is zero, then the width parameter is used.
            // We use zero in these cases in the hope that we can avoid two
            // unnecessary calls to glPixelStorei.
            rowLength = 0;
        } else {
            // The number of pixels in a row is different than the number of
            // pixels in the region to be uploaded to the texture.
            rowLength = srcImage.getWidth();
        }
        // Consider moving these conversion methods.
        int pixelFormat = TextureStateRecord.getGLPixelFormat(srcImage
                .getFormat());
        ByteBuffer data = srcImage.getData(0);
        data.rewind();

        // Update the texture configuration (when necessary).
        if (origTexBinding != dstTexture.getTextureId())
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, dstTexture.getTextureId());
        if (origAlignment != alignment)
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, alignment);
        if (origRowLength != rowLength)
            GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, rowLength);
        if (origSkipPixels != srcX)
            GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, srcX);
        if (origSkipRows != srcY)
            GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, srcY);

        // Upload the image region into the texture.
        GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, dstX, dstY, width, height,
                pixelFormat, GL11.GL_UNSIGNED_BYTE, data);

        // Restore the texture configuration (when necessary).
        // Restore the texture binding.
        if (origTexBinding != dstTexture.getTextureId())
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, origTexBinding);
        // Restore alignment.
        if (origAlignment != alignment)
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, origAlignment);
        // Restore row length.
        if (origRowLength != rowLength)
            GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, origRowLength);
        // Restore skip pixels.
        if (origSkipPixels != srcX)
            GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, origSkipPixels);
        // Restore skip rows.
        if (origSkipRows != srcY)
            GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, origSkipRows);
    }

    @Override
    public void checkCardError() throws JmeException {
        try {
            org.lwjgl.opengl.Util.checkGLError();
        } catch (OpenGLException exception) {
            throw new JmeException(
                    "Error in opengl: " + exception.getMessage(), exception);
        }
    }

    @Override
    public void cleanup() {
        // clear vbos
        RendererRecord rendRecord = (RendererRecord) DisplaySystem
                .getDisplaySystem().getCurrentContext().getRendererRecord();
        rendRecord.cleanupVBOs();
        if (font != null) {
            font.deleteFont();
            font = null;
        }
    }
}
