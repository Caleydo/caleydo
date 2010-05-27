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
import javax.media.opengl.GL;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLException;
import javax.media.opengl.glu.GLU;

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
import com.jme.scene.state.jogl.JOGLBlendState;
import com.jme.scene.state.jogl.JOGLClipState;
import com.jme.scene.state.jogl.JOGLColorMaskState;
import com.jme.scene.state.jogl.JOGLCullState;
import com.jme.scene.state.jogl.JOGLFogState;
import com.jme.scene.state.jogl.JOGLFragmentProgramState;
import com.jme.scene.state.jogl.JOGLLightState;
import com.jme.scene.state.jogl.JOGLMaterialState;
import com.jme.scene.state.jogl.JOGLShadeState;
import com.jme.scene.state.jogl.JOGLShaderObjectsState;
import com.jme.scene.state.jogl.JOGLStencilState;
import com.jme.scene.state.jogl.JOGLStippleState;
import com.jme.scene.state.jogl.JOGLTextureState;
import com.jme.scene.state.jogl.JOGLVertexProgramState;
import com.jme.scene.state.jogl.JOGLWireframeState;
import com.jme.scene.state.jogl.JOGLZBufferState;
import com.jme.scene.state.jogl.records.LineRecord;
import com.jme.scene.state.jogl.records.RendererRecord;
import com.jme.scene.state.jogl.records.TextureStateRecord;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.system.jogl.JOGLDisplaySystem;
import com.jme.util.Debug;
import com.jme.util.WeakIdentityCache;
import com.jme.util.geom.BufferUtils;
import com.jme.util.stat.StatCollector;
import com.jme.util.stat.StatType;

/**
 * <code>JOGLRenderer</code> provides an implementation of the
 * <code>Renderer</code> interface using the JOGL API.
 *
 * @see com.jme.renderer.Renderer
 * @author Mark Powell - initial implementation, and more.
 * @author Joshua Slack - Further work, Optimizations, Headless rendering
 * @author Tijl Houtbeckers - Small optimizations and improved VBO
 * @author Steve Vaughan - JOGL port
 * @version $Id: JOGLRenderer.java 4772 2009-12-09 23:09:11Z blaine.dev $
 */
public class JOGLRenderer extends Renderer {
    private static final Logger logger = Logger.getLogger(JOGLRenderer.class
            .getName());

    private final JOGLDisplaySystem display;

    private Vector3f vRot = new Vector3f();

    private JOGLFont font;

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

    private int prevNormMode = GL.GL_ZERO;

    private final JOGLContextCapabilities capabilities;

    private int prevTextureNumber = 0;

    private boolean generatingDisplayList = false;

    protected WeakIdentityCache<Buffer, Integer> vboMap = new WeakIdentityCache<Buffer, Integer>();

    /**
     * Constructor instantiates a new <code>JOGLRenderer</code> object. The
     * size of the rendering window is passed during construction.
     *
     * @param width
     *            the width of the rendering context.
     * @param height
     *            the height of the rendering context.
     *            
     * TODO Replace all of these fields with one surface reference?
     * TODO If the capabilities are really context specific the field should be dropped.
     */
    public JOGLRenderer(JOGLDisplaySystem display, JOGLContextCapabilities caps, int width, int height) {
        if (width <= 0 || height <= 0) {
            logger.warning("Invalid width and/or height values.");
            throw new JmeException("Invalid width and/or height values.");
        }
        this.display = display;
        this.capabilities = caps;
        this.width = width;
        this.height = height;

        logger.info("JOGLRenderer created. W:  " + width + "H: " + height + "\tVersion: "
                + Package.getPackage( "javax.media.opengl" ).getImplementationVersion() );

        queue = new RenderQueue(this);
        if (TextureState.getNumberOfTotalUnits() == -1)
            createTextureState(); // force units population
        prevTex = new FloatBuffer[TextureState.getNumberOfTotalUnits()];

        supportsVBO = capabilities.GL_ARB_vertex_buffer_object;

        supportsFogCoords = capabilities.GL_EXT_fog_coord;
    }

    public JOGLContextCapabilities getContextCapabilities() {
        return capabilities ;
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
            logger.warning("Invalid width and/or height values:" + width + "x" + height);
            throw new JmeException("Invalid width and/or height values:" + width + "x" + height);
        }
        this.width = width;
        this.height = height;
        if (camera != null) {
            camera.resize(width, height);
            camera.apply();
        }
    }

    /**
     * <code>setCamera</code> sets the camera this renderer is using. It
     * asserts that the camera is of type <code>JOGLCamera</code>.
     *
     * @see com.jme.renderer.Renderer#setCamera(com.jme.renderer.Camera)
     */
    public void setCamera(final Camera camera) {
        // Check that this isn't the same camera to avoid unnecessary work.
        if (camera == this.camera)
            return;

        if (camera instanceof JOGLCamera) {
            this.camera = (JOGLCamera) camera;

            // Update dimensions for the newly associated camera and apply the
            // changes.
            ((JOGLCamera) this.camera).resize(width, height, true);
            this.camera.apply();
        }
    }

    /**
     * <code>createCamera</code> returns a default camera for use with the
     * JOGL renderer.
     *
     * @param width
     *            the width of the frame.
     * @param height
     *            the height of the frame.
     * @return a default JOGL camera.
     */
    public Camera createCamera(int width, int height) {
        return new JOGLCamera(width, height);
    }

    /**
     * <code>createBlendState</code> returns a new JOGLBlendState object as a
     * regular BlendState.
     *
     * @return an BlendState object.
     */
    public BlendState createBlendState() {
        return new JOGLBlendState(capabilities);
    }

    /**
     * <code>createCullState</code> returns a new JOGLCullState object as a
     * regular CullState.
     *
     * @return a CullState object.
     * @see com.jme.renderer.Renderer#createCullState()
     */
    public CullState createCullState() {
        return new JOGLCullState();
    }

    /**
     * <code>createFogState</code> returns a new JOGLFogState object as a
     * regular FogState.
     *
     * @return an FogState object.
     */
    public FogState createFogState() {
        return new JOGLFogState(capabilities);
    }

    /**
     * <code>createLightState</code> returns a new JOGLLightState object as a
     * regular LightState.
     *
     * @return an LightState object.
     */
    public LightState createLightState() {
        return new JOGLLightState(capabilities);
    }

    /**
     * <code>createMaterialState</code> returns a new JOGLMaterialState
     * object as a regular MaterialState.
     *
     * @return an MaterialState object.
     */
    public MaterialState createMaterialState() {
        return new JOGLMaterialState();
    }

    /**
     * <code>createShadeState</code> returns a new JOGLShadeState object as a
     * regular ShadeState.
     *
     * @return an ShadeState object.
     */
    public ShadeState createShadeState() {
        return new JOGLShadeState();
    }

    /**
     * <code>createTextureState</code> returns a new JOGLTextureState object
     * as a regular TextureState.
     *
     * @return an TextureState object.
     */
    public TextureState createTextureState() {
        return new JOGLTextureState(capabilities);
    }

    /**
     * <code>createWireframeState</code> returns a new JOGLWireframeState
     * object as a regular WireframeState.
     *
     * @return an WireframeState object.
     */
    public WireframeState createWireframeState() {
        return new JOGLWireframeState();
    }

    /**
     * <code>createZBufferState</code> returns a new JOGLZBufferState object
     * as a regular ZBufferState.
     *
     * @return a ZBufferState object.
     */
    public ZBufferState createZBufferState() {
        return new JOGLZBufferState();
    }

    /**
     * <code>createVertexProgramState</code> returns a new
     * JOGLVertexProgramState object as a regular VertexProgramState.
     *
     * @return a JOGLVertexProgramState object.
     */
    public VertexProgramState createVertexProgramState() {
        return new JOGLVertexProgramState(capabilities);
    }

    /**
     * <code>createFragmentProgramState</code> returns a new
     * JOGLFragmentProgramState object as a regular FragmentProgramState.
     *
     * @return a JOGLFragmentProgramState object.
     */
    public FragmentProgramState createFragmentProgramState() {
        return new JOGLFragmentProgramState(capabilities);
    }

    /**
     * <code>createShaderObjectsState</code> returns a new
     * JOGLShaderObjectsState object as a regular ShaderObjectsState.
     *
     * @return an ShaderObjectsState object.
     */
    public GLSLShaderObjectsState createGLSLShaderObjectsState() {
        return new JOGLShaderObjectsState(capabilities);
    }

    /**
     * <code>createStencilState</code> returns a new JOGLStencilState object
     * as a regular StencilState.
     *
     * @return a StencilState object.
     */
    public StencilState createStencilState() {
        return new JOGLStencilState(capabilities);
    }

    /**
     * <code>createClipState</code> returns a new JOGLClipState object as a
     * regular ClipState.
     *
     * @return a ClipState object.
     * @see com.jme.renderer.Renderer#createClipState()
     */
    public ClipState createClipState() {
        return new JOGLClipState();
    }

    /**
     * <code>createColorMaskState</code> returns a new JOGLColorMaskState
     * object as a regular ColorMaskState.
     *
     * @return a ColorMaskState object.
     */
    public ColorMaskState createColorMaskState() {
        return new JOGLColorMaskState();
    }

    /**
     * <code>createStippleState</code> returns a new JOGLStippleState
     * object as a regular StippleState.
     *
     * @return a StippleState object.
     */
    public StippleState createStippleState() {
        return new JOGLStippleState();
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
        final GL gl = GLU.getCurrentGL();

        // if color is null set background to white.
        if (c == null) {
            backgroundColor.a = 1.0f;
            backgroundColor.b = 1.0f;
            backgroundColor.g = 1.0f;
            backgroundColor.r = 1.0f;
        } else {
            backgroundColor = c;
        }
        gl.glClearColor(backgroundColor.r, backgroundColor.g,
                backgroundColor.b, backgroundColor.a);
    }

    /**
     * <code>clearZBuffer</code> clears the OpenGL depth buffer.
     *
     * @see com.jme.renderer.Renderer#clearZBuffer()
     */
    public void clearZBuffer() {
        final GL gl = GLU.getCurrentGL();

        if (Renderer.defaultStateList[RenderState.StateType.ZBuffer.ordinal()] != null)
            Renderer.defaultStateList[RenderState.StateType.ZBuffer.ordinal()].apply();
        gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
    }

    /**
     * <code>clearBackBuffer</code> clears the OpenGL color buffer.
     *
     * @see com.jme.renderer.Renderer#clearColorBuffer()
     */
    public void clearColorBuffer() {
        final GL gl = GLU.getCurrentGL();

        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
    }

    /**
     * <code>clearStencilBuffer</code>
     *
     * @see com.jme.renderer.Renderer#clearStencilBuffer()
     */
    public void clearStencilBuffer() {
        final GL gl = GLU.getCurrentGL();

        // Clear the stencil buffer
        gl.glClearStencil(0);
        gl.glStencilMask(~0);
        gl.glDisable(GL.GL_DITHER);
        gl.glEnable(GL.GL_SCISSOR_TEST);
        gl.glScissor(0, 0, getWidth(), getHeight());
        gl.glClear(GL.GL_STENCIL_BUFFER_BIT);
        gl.glDisable(GL.GL_SCISSOR_TEST);
    }

    /**
     * <code>clearBuffers</code> clears both the color and the depth buffer.
     *
     * @see com.jme.renderer.Renderer#clearBuffers()
     */
    public void clearBuffers() {
        final GL gl = GLU.getCurrentGL();

        // make sure no funny business is going on in the z before clearing.
        if (Renderer.defaultStateList[RenderState.StateType.ZBuffer.ordinal()] != null) {
            Renderer.defaultStateList[RenderState.StateType.ZBuffer.ordinal()].setNeedsRefresh(true);
            Renderer.defaultStateList[RenderState.StateType.ZBuffer.ordinal()].apply();
        }
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    }

    /**
     * <code>clearBuffers</code> clears both the color and the depth buffer
     * for only the part of the buffer defined by the renderer width/height.
     *
     * @see com.jme.renderer.Renderer#clearBuffers()
     */
    public void clearStrictBuffers() {
        final GL gl = GLU.getCurrentGL();

        gl.glDisable(GL.GL_DITHER);
        gl.glEnable(GL.GL_SCISSOR_TEST);
        gl.glScissor(0, 0, width, height);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glDisable(GL.GL_SCISSOR_TEST);
        gl.glEnable(GL.GL_DITHER);
    }

    /**
     * <code>displayBackBuffer</code> renders any queued items then flips the
     * rendered buffer (back) with the currently displayed buffer.
     *
     * @see com.jme.renderer.Renderer#displayBackBuffer()
     */
    public void displayBackBuffer() {
        final GL gl = GLU.getCurrentGL();

        renderQueue();

        Renderer.defaultStateList[RenderState.StateType.ColorMask.ordinal()].apply();

        reset();

        gl.glFlush();
        if (!isHeadless()) {
            if (Debug.stats) {
                StatCollector.startStat(StatType.STAT_DISPLAYSWAP_TIMER);
            }
            // LWJGL Display.update() checks for errors and swaps the buffer.
            checkCardError();
            GLContext.getCurrent().getGLDrawable().swapBuffers();
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
        final GL gl = GLU.getCurrentGL();
        final GLU glu = new GLU();

        if (inOrthoMode) {
            throw new JmeException("Already in Orthographic mode.");
        }
        // set up ortho mode
        RendererRecord matRecord = (RendererRecord) DisplaySystem
                .getDisplaySystem().getCurrentContext().getRendererRecord();
        matRecord.switchMode(GL.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        float viewportWidth = width * (camera.getViewPortRight() - camera.getViewPortLeft());
        float viewportHeight = height * (camera.getViewPortTop() - camera.getViewPortBottom());
        glu.gluOrtho2D(0, viewportWidth, 0, viewportHeight);
        matRecord.switchMode(GL.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        inOrthoMode = true;
    }

    public void setOrthoCenter() {
        final GL gl = GLU.getCurrentGL();
        final GLU glu = new GLU();

        if (inOrthoMode) {
            throw new JmeException("Already in Orthographic mode.");
        }
        // set up ortho mode
        RendererRecord matRecord = (RendererRecord) DisplaySystem
                .getDisplaySystem().getCurrentContext().getRendererRecord();
        matRecord.switchMode(GL.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        glu.gluOrtho2D(-width / 2f, width / 2f, -height / 2f, height / 2f);
        matRecord.switchMode(GL.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        inOrthoMode = true;
    }

    /**
     * <code>setOrthoCenter</code> sets the display system to be in
     * orthographic mode. If the system has already been set to orthographic
     * mode a <code>JmeException</code> is thrown. The origin (0,0) is the
     * center of the screen.
     */
    public void unsetOrtho() {
        final GL gl = GLU.getCurrentGL();

        if (!inOrthoMode) {
            throw new JmeException("Not in Orthographic mode.");
        }
        // remove ortho mode, and go back to original
        // state
        RendererRecord matRecord = (RendererRecord) DisplaySystem
                .getDisplaySystem().getCurrentContext().getRendererRecord();
        matRecord.switchMode(GL.GL_PROJECTION);
        gl.glPopMatrix();
        matRecord.switchMode(GL.GL_MODELVIEW);
        gl.glPopMatrix();
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
        // Create a pointer to the image info and create a buffered image to
        // hold it.
        final ByteBuffer buff = BufferUtils.createByteBuffer(width * height * 3);
        grabScreenContents(buff, Image.Format.RGB8, 0, 0, width, height);
        final int w = width;
        final int h = height;
                
        Thread saveThread = new Thread() {
            
            public void run() {
                BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

                // Grab each pixel information and set it to the BufferedImage info.
                for (int x = 0; x < w; x++) {
                    for (int y = 0; y < h; y++) {
                        
                        int index = 3 * ((h- y - 1) * w + x);
                        //if (index < 0) { System.out.println(); }
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
        final GL gl = GLU.getCurrentGL();

        int pixFormat = TextureStateRecord.getGLPixelFormat(format);
        gl.glReadPixels(x, y, w, h, pixFormat, GL.GL_UNSIGNED_BYTE, buff);
    }

    /**
     * <code>draw</code> renders a curve object.
     *
     * @param curve
     *            the curve object to render.
     */
    public void draw(Curve curve) {
        final GL gl = GLU.getCurrentGL();

        // set world matrix
        Quaternion rotation = curve.getWorldRotation();
        Vector3f translation = curve.getWorldTranslation();
        Vector3f scale = curve.getWorldScale();
        float rot = rotation.toAngleAxis(vRot) * FastMath.RAD_TO_DEG;
        RendererRecord matRecord = (RendererRecord) DisplaySystem
                .getDisplaySystem().getCurrentContext().getRendererRecord();
        matRecord.switchMode(GL.GL_MODELVIEW);
        gl.glPushMatrix();

        gl.glTranslatef(translation.x, translation.y, translation.z);
        gl.glRotatef(rot, vRot.x, vRot.y, vRot.z);
        gl.glScalef(scale.x, scale.y, scale.z);

        applyStates(curve.states, null);

        // render the object
        gl.glBegin(GL.GL_LINE_STRIP);

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
            gl.glVertex3f(point.x, point.y, point.z);
        }

        if (Debug.stats) {
            StatCollector.addStat(StatType.STAT_VERTEX_COUNT, limit);
        }

        gl.glEnd();
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
        final GL gl = GLU.getCurrentGL();

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
        int mode = GL.GL_LINES;
        switch (lines.getMode()) {
            case Segments:
                mode = GL.GL_LINES;
                break;
            case Connected:
                mode = GL.GL_LINE_STRIP;
                break;
            case Loop:
                mode = GL.GL_LINE_LOOP;
                break;
        }

        LineRecord lineRecord = (LineRecord) display
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

            gl.glDrawElements(mode, indices.limit(), GL.GL_UNSIGNED_INT, indices); // TODO Check <count> and assumed <type> of GL_UNSIGNED_INT

            indices.clear();
        } else {
            gl.glDrawElements(mode, lines.getIndexBuffer().limit(),
                    GL.GL_UNSIGNED_INT, 0);
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
        final GL gl = GLU.getCurrentGL();

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

        gl.glPointSize(points.getPointSize());
        if (points.isAntialiased()) {
            gl.glEnable(GL.GL_POINT_SMOOTH);
            gl.glHint(GL.GL_POINT_SMOOTH_HINT, GL.GL_NICEST);
        }

        if (!predrawGeometry(points)) {
            // make sure only the necessary indices are sent through on old
            // cards.
            IntBuffer indices = points.getIndexBuffer();
            indices.rewind();
            indices.limit(points.getVertexCount());

            gl.glDrawElements(GL.GL_POINTS, indices.limit(), GL.GL_UNSIGNED_INT, indices); // TODO Check <count> and assumed <type> of GL_UNSIGNED_INT

            indices.clear();
        } else {
            gl.glDrawElements(GL.GL_POINTS,
                    points.getIndexBuffer().limit(), GL.GL_UNSIGNED_INT, 0);
        }

        if (points.isAntialiased()) {
            gl.glDisable(GL.GL_POINT_SMOOTH);
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
        final GL gl = GLU.getCurrentGL();

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
        int glMode = GL.GL_QUADS;
        switch (quads.getMode()) {
            case Quads:
                glMode = GL.GL_QUADS;
                break;
            case Strip:
                glMode = GL.GL_QUAD_STRIP;
                break;
        }

        if (!predrawGeometry(quads)) {
            // make sure only the necessary indices are sent through on old
            // cards.
            IntBuffer indices = quads.getIndexBuffer();
            indices.rewind();
            indices.limit(quads.getMaxIndex());

            gl.glDrawElements(glMode, indices.limit(), GL.GL_UNSIGNED_INT, indices); // TODO Check <count> and assumed <type> of GL_UNSIGNED_INT

            indices.clear();
        } else {

            gl.glDrawElements(glMode, quads.getIndexBuffer().limit(),
                    GL.GL_UNSIGNED_INT, 0);

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
        final GL gl = GLU.getCurrentGL();

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

        int glMode = GL.GL_TRIANGLES;
        switch (tris.getMode()) {
            case Triangles:
                glMode = GL.GL_TRIANGLES;
                break;
            case Strip:
                glMode = GL.GL_TRIANGLE_STRIP;
                break;
            case Fan:
                glMode = GL.GL_TRIANGLE_FAN;
                break;
        }

        if (!predrawGeometry(tris)) {
            // make sure only the necessary indices are sent through on old
            // cards.
            IntBuffer indices = tris.getIndexBuffer();
            if (indices == null) {
                logger.severe("missing indices on geometry object: "
                        + tris.toString());
            } else {
                indices.rewind();
                indices.limit(tris.getMaxIndex());

                gl.glDrawElements(glMode, indices.limit(), GL.GL_UNSIGNED_INT, indices); // TODO Check <count> and assumed <type> of GL_UNSIGNED_INT

                indices.clear();
            }
        } else {
            gl.glDrawElements(glMode, tris.getIndexBuffer().limit(),
                    GL.GL_UNSIGNED_INT, 0);
        }

        postdrawGeometry(tris);
        if (transformed) undoTransforms(tris);

        if (Debug.stats) {
            StatCollector.endStat(StatType.STAT_RENDER_TIMER);
        }
        tris.postdraw(this);
    }

    private synchronized void renderDisplayList(Geometry geom) {
        final GL gl = GLU.getCurrentGL();

        applyStates(geom.states, geom);

        if (Debug.stats) {
            StatCollector.startStat(StatType.STAT_RENDER_TIMER);
        }
        if ((geom.getLocks() & Spatial.LOCKED_TRANSFORMS) == 0) {
            boolean transformed = doTransforms(geom);
            gl.glCallList(geom.getDisplayListID());
            if (transformed) undoTransforms(geom);
        } else {
            gl.glCallList(geom.getDisplayListID());
        }
        // invalidate line record as we do not know the line state anymore
        ((LineRecord) display.getCurrentContext()
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
        final GL gl = GLU.getCurrentGL();

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
                    gl.glBindBufferARB(
                            GL.GL_ARRAY_BUFFER_ARB, vbo
                                    .getVBOVertexID());
                    gl.glBufferDataARB(
                            GL.GL_ARRAY_BUFFER_ARB, g
                                    .getVertexBuffer().limit() * 4, g
                                    .getVertexBuffer(),
                            GL.GL_STATIC_DRAW_ARB); // TODO Check <sizeInBytes>
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
                        gl
                                .glBufferDataARB(
                                        GL.GL_ELEMENT_ARRAY_BUFFER_ARB,
                                        tb.getIndexBuffer().limit() * 4,
                                        tb.getIndexBuffer(),
                                        GL.GL_STATIC_DRAW_ARB); // TODO Check <sizeInBytes>

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
                    gl.glBufferDataARB(
                            GL.GL_ARRAY_BUFFER_ARB, g
                                    .getNormalBuffer().limit() * 4, g
                                    .getNormalBuffer(),
                            GL.GL_STATIC_DRAW_ARB); // TODO Check <sizeInBytes>
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
                    gl.glBufferDataARB(
                            GL.GL_ARRAY_BUFFER_ARB, g
                                    .getColorBuffer().limit() * 4, g
                                    .getColorBuffer(),
                            GL.GL_STATIC_DRAW_ARB); // TODO Check <sizeInBytes>
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
                    gl.glBufferDataARB(
                            GL.GL_ARRAY_BUFFER_ARB, g
                                    .getFogBuffer().limit() * 4, g
                                    .getFogBuffer(),
                            GL.GL_STATIC_DRAW_ARB); // TODO Check <sizeInBytes>
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
                        gl.glBufferDataARB(
                                GL.GL_ARRAY_BUFFER_ARB, texC.coords.limit() * 4, texC.coords,
                                GL.GL_STATIC_DRAW_ARB); // TODO Check <sizeInBytes>
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
            font = new JOGLFont();
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
        final GL gl = GLU.getCurrentGL();

        gl.glFlush();
    }

    /**
     * <code>finish</code> is similar to flush, however it blocks until all
     * waiting OpenGL commands have been finished.
     */
    public void finish() {
        final GL gl = GLU.getCurrentGL();

        gl.glFinish();
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
        final GL gl = GLU.getCurrentGL();

        RenderContext<GLContext> context = display
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
            gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
            rendRecord.setBoundVBO(vbo.getVBOVertexID());
            gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);
        } else if (vertices == null) {
            gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
        } else if (prevVerts != vertices) {
            // verts have changed
            gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
            // ensure no VBO is bound
            if (supportsVBO)
                rendRecord.setBoundVBO(0);
            vertices.rewind();
            gl.glVertexPointer(3, GL.GL_FLOAT, 0, vertices); // TODO Check assumed <type> GL_FLOAT
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
                gl.glEnableClientState(GL.GL_FOG_COORDINATE_ARRAY_EXT);
                rendRecord.setBoundVBO(vbo.getVBOVertexID());
                gl.glFogCoordPointerEXT(GL.GL_FLOAT, 0, 0);
            } else if (fogCoords == null) {
                gl.glDisableClientState(GL.GL_FOG_COORDINATE_ARRAY_EXT);
            } else if (prevFogCoords != fogCoords) {
                // fog coords have changed
                gl.glEnableClientState(GL.GL_FOG_COORDINATE_ARRAY_EXT);
                // ensure no VBO is bound
                if (supportsVBO)
                    rendRecord.setBoundVBO(0);
                fogCoords.rewind();
                gl.glFogCoordPointerEXT(0, 0, g.getFogBuffer());
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
                gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
                rendRecord.setBoundVBO(vbo.getVBONormalID());
                gl.glNormalPointer(GL.GL_FLOAT, 0, 0);
            } else if (normals == null) {
                gl.glDisableClientState(GL.GL_NORMAL_ARRAY);
            } else if (prevNorms != normals) {
                // textures have changed
                gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
                // ensure no VBO is bound
                if (supportsVBO)
                    rendRecord.setBoundVBO(0);
                normals.rewind();
                gl.glNormalPointer(GL.GL_FLOAT, 0,  normals); // TODO Check assumed <type> GL_FLOAT
            }
            if (oldLimit != -1)
                normals.limit(oldLimit);
            prevNorms = normals;
        } else {
            if (prevNormMode == GL.GL_RESCALE_NORMAL) {
                gl.glDisable(GL.GL_RESCALE_NORMAL);
                prevNormMode = GL.GL_ZERO;
            } else if (prevNormMode == GL.GL_NORMALIZE) {
                gl.glDisable(GL.GL_NORMALIZE);
                prevNormMode = GL.GL_ZERO;
            }
            oldLimit = -1;
            gl.glDisableClientState(GL.GL_NORMAL_ARRAY);
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
            gl.glEnableClientState(GL.GL_COLOR_ARRAY);
            rendRecord.setBoundVBO(vbo.getVBOColorID());
            gl.glColorPointer(4, GL.GL_FLOAT, 0, 0);
        } else if (colors == null) {
            gl.glDisableClientState(GL.GL_COLOR_ARRAY);

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
            gl.glEnableClientState(GL.GL_COLOR_ARRAY);
            // ensure no VBO is bound
            if (supportsVBO)
                rendRecord.setBoundVBO(0);
            colors.rewind();
            gl.glColorPointer(4, GL.GL_FLOAT, 0, colors); // TODO Check assumed <type> GL_FLOAT
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
                if (texC != null) {
                    // make sure only the necessary texture coords are sent
                    // through on old cards.
                    oldLimit = texC.coords.limit();
                    texC.coords.limit(g.getVertexCount() * texC.perVert);
                }
                if (capabilities.GL_ARB_multitexture) {
                    gl
                            .glClientActiveTexture(GL.GL_TEXTURE0
                                    + i);
                }
                if ((supportsVBO && vbo != null && vbo.getVBOTextureID(i) > 0)) { // use
                    // VBO
                    gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);
                    rendRecord.setBoundVBO(vbo.getVBOTextureID(i));
                    gl.glTexCoordPointer(texC.perVert, GL.GL_FLOAT, 0, 0);
                } else if (texC == null) {
                    gl.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);
                } else if (prevTex[i] != texC.coords) {
                    // textures have changed
                    gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);
                    // ensure no VBO is bound
                    if (supportsVBO)
                        rendRecord.setBoundVBO(0);
                    // set data
                    texC.coords.rewind();
                    gl.glTexCoordPointer(texC.perVert, GL.GL_FLOAT, 0, texC.coords); // TODO Check assumed <type> GL_FLOAT
                } else {
                    gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);
                }
                prevTex[i] = texC != null ? texC.coords : null;
                if (oldLimit != -1)
                    texC.coords.limit(oldLimit);
            }

            if (ts.getNumberOfSetTextures() < prevTextureNumber) {
                for (int i = ts.getNumberOfSetTextures(); i < prevTextureNumber; i++) {
                    if (capabilities.GL_ARB_multitexture) {
                        gl
                                .glClientActiveTexture(GL.GL_TEXTURE0
                                        + i);
                    }
                    gl.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);
                }
            }

            prevTextureNumber = ts.getNumberOfSetTextures() < TextureState
                    .getNumberOfFixedUnits() ? ts.getNumberOfSetTextures()
                    : TextureState.getNumberOfFixedUnits();
        }

        return indicesVBO;
    }

    private void applyNormalMode(Spatial.NormalsMode normMode, Geometry t) {
        final GL gl = GLU.getCurrentGL();

        switch (normMode) {
            case NormalizeIfScaled:
                Vector3f scale = t.getWorldScale();
                if (!scale.equals(Vector3f.UNIT_XYZ)) {
                    if (scale.x == scale.y && scale.y == scale.z
                            && capabilities.GL_VERSION_1_2
                            && prevNormMode != GL.GL_RESCALE_NORMAL) {
                        if (prevNormMode == GL.GL_NORMALIZE)
                            gl.glDisable(GL.GL_NORMALIZE);
                        gl.glEnable(GL.GL_RESCALE_NORMAL);
                        prevNormMode = GL.GL_RESCALE_NORMAL;
                    } else if (prevNormMode != GL.GL_NORMALIZE) {
                        if (prevNormMode == GL.GL_RESCALE_NORMAL)
                            gl.glDisable(GL.GL_RESCALE_NORMAL);
                        gl.glEnable(GL.GL_NORMALIZE);
                        prevNormMode = GL.GL_NORMALIZE;
                    }
                } else {
                    if (prevNormMode == GL.GL_RESCALE_NORMAL) {
                        gl.glDisable(GL.GL_RESCALE_NORMAL);
                        prevNormMode = GL.GL_ZERO;
                    } else if (prevNormMode == GL.GL_NORMALIZE) {
                        gl.glDisable(GL.GL_NORMALIZE);
                        prevNormMode = GL.GL_ZERO;
                    }
                }
                break;
            case AlwaysNormalize:
                if (prevNormMode != GL.GL_NORMALIZE) {
                    if (prevNormMode == GL.GL_RESCALE_NORMAL)
                        gl.glDisable(GL.GL_RESCALE_NORMAL);
                    gl.glEnable(GL.GL_NORMALIZE);
                    prevNormMode = GL.GL_NORMALIZE;
                }
                break;
            case UseProvided:
            default:
                if (prevNormMode == GL.GL_RESCALE_NORMAL) {
                    gl.glDisable(GL.GL_RESCALE_NORMAL);
                    prevNormMode = GL.GL_ZERO;
                } else if (prevNormMode == GL.GL_NORMALIZE) {
                    gl.glDisable(GL.GL_NORMALIZE);
                    prevNormMode = GL.GL_ZERO;
                }
                break;
        }
    }

    protected boolean doTransforms(Spatial t) {
        final GL gl = GLU.getCurrentGL();

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
                RendererRecord matRecord = (RendererRecord) display.getCurrentContext().getRendererRecord();
                matRecord.switchMode(GL.GL_MODELVIEW);
                gl.glPushMatrix();
                if (doT)
                    gl.glTranslatef(translation.x, translation.y, translation.z);
                if (doR) {
                    float rot = rotation.toAngleAxis(vRot) * FastMath.RAD_TO_DEG;
                    gl.glRotatef(rot, vRot.x, vRot.y, vRot.z);
                }
                if (doS) {
                    gl.glScalef(scale.x, scale.y, scale.z);
                }
                return true;
            }
        }
        return false;
    }

    protected void undoTransforms(Spatial t) {
        final GL gl = GLU.getCurrentGL();

        if (!generatingDisplayList
                || (t.getLocks() & Spatial.LOCKED_TRANSFORMS) != 0) {
            RendererRecord matRecord = (RendererRecord) DisplaySystem
                    .getDisplaySystem().getCurrentContext().getRendererRecord();
            matRecord.switchMode(GL.GL_MODELVIEW);
            gl.glPopMatrix();
        }
    }

    // inherited documentation
    public int createDisplayList(Geometry g) {
        final GL gl = GLU.getCurrentGL();

        int listID = gl.glGenLists(1);

        generatingDisplayList = true;
        RenderContext<GLContext> context = display.getCurrentContext();
        // invalidate states -- this makes sure things like line stipple get
        // called in list.
        context.invalidateStates();
        RenderState oldTS = context.currentStates[RenderState.StateType.Texture.ordinal()];
        context.currentStates[RenderState.StateType.Texture.ordinal()] = g.states[RenderState.StateType.Texture.ordinal()];
        gl.glNewList(listID, GL.GL_COMPILE);
        if (g instanceof TriMesh)
            draw((TriMesh) g);
        else if (g instanceof QuadMesh)
            draw((QuadMesh) g);
        else if (g instanceof Line)
            draw((Line) g);
        else if (g instanceof Point)
            draw((Point) g);
        gl.glEndList();
        context.currentStates[RenderState.StateType.Texture.ordinal()] = oldTS;
        generatingDisplayList = false;

        return listID;
    }

    // inherited documentation
    public void releaseDisplayList(int listId) {
        final GL gl = GLU.getCurrentGL();

        gl.glDeleteLists(listId, 1);
    }

    // inherited documentation
    public void setPolygonOffset(float factor, float offset) {
        final GL gl = GLU.getCurrentGL();

        gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
        gl.glEnable(GL.GL_POLYGON_OFFSET_LINE);
        gl.glEnable(GL.GL_POLYGON_OFFSET_POINT);
        gl.glPolygonOffset(factor, offset);
    }

    // inherited documentation
    public void clearPolygonOffset() {
        final GL gl = GLU.getCurrentGL();

        gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);
        gl.glDisable(GL.GL_POLYGON_OFFSET_LINE);
        gl.glDisable(GL.GL_POLYGON_OFFSET_POINT);
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

        RenderContext<GLContext> context = display.getCurrentContext();

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
            tempState = context.enforcedStateList[i] != null ? context.enforcedStateList[i] : states[i];

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
    public void updateTextureSubImage(final Texture dstTexture, final int dstX,
            final int dstY, final Image srcImage, final int srcX,
            final int srcY, final int width, final int height)
            throws JmeException {
        GL gl = GLU.getCurrentGL();

        // Check that the texture type is supported.
        if (dstTexture.getType() != Texture.Type.TwoDimensional)
            throw new UnsupportedOperationException(
                    "Unsupported Texture Type: " + dstTexture.getType());

        // Determine the original texture configuration, so that this method can
        // restore the texture configuration to its original state.
        final int origTexBinding[] = new int[1];
        gl.glGetIntegerv(GL.GL_TEXTURE_BINDING_2D, origTexBinding, 0);
        final int origAlignment[] = new int[1];
        gl.glGetIntegerv(GL.GL_UNPACK_ALIGNMENT, origAlignment, 0);
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
        if (origTexBinding[0] != dstTexture.getTextureId())
            gl.glBindTexture(GL.GL_TEXTURE_2D, dstTexture.getTextureId());
        if (origAlignment[0] != alignment)
            gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, alignment);
        if (origRowLength != rowLength)
            gl.glPixelStorei(GL.GL_UNPACK_ROW_LENGTH, rowLength);
        if (origSkipPixels != srcX)
            gl.glPixelStorei(GL.GL_UNPACK_SKIP_PIXELS, srcX);
        if (origSkipRows != srcY)
            gl.glPixelStorei(GL.GL_UNPACK_SKIP_ROWS, srcY);

        // Upload the image region into the texture.
        gl.glTexSubImage2D(GL.GL_TEXTURE_2D, 0, dstX, dstY, width, height,
                pixelFormat, GL.GL_UNSIGNED_BYTE, data);

        // Restore the texture configuration (when necessary).
        // Restore the texture binding.
        if (origTexBinding[0] != dstTexture.getTextureId())
            gl.glBindTexture(GL.GL_TEXTURE_2D, origTexBinding[0]);
        // Restore alignment.
        if (origAlignment[0] != alignment)
            gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, origAlignment[0]);
        // Restore row length.
        if (origRowLength != rowLength)
            gl.glPixelStorei(GL.GL_UNPACK_ROW_LENGTH, origRowLength);
        // Restore skip pixels.
        if (origSkipPixels != srcX)
            gl.glPixelStorei(GL.GL_UNPACK_SKIP_PIXELS, origSkipPixels);
        // Restore skip rows.
        if (origSkipRows != srcY)
            gl.glPixelStorei(GL.GL_UNPACK_SKIP_ROWS, origSkipRows);
    }

    @Override
    public void checkCardError() throws JmeException {
        final GL gl = GLU.getCurrentGL();
        final GLU glu = new GLU();

        try {
            final int errorCode = gl.glGetError();
            if (errorCode != GL.GL_NO_ERROR) {
               throw new GLException(glu.gluErrorString(errorCode));
            }
        } catch (GLException exception) {
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
