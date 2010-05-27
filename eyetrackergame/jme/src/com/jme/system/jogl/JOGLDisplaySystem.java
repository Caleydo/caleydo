/*
 * Copyright (c) 2008 SRA International, Inc.
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

package com.jme.system.jogl;

import java.awt.Canvas;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.opengl.DebugGL;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLException;
import javax.media.opengl.Threading;
import javax.media.opengl.glu.GLU;

import com.jme.image.Image;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.renderer.RenderContext;
import com.jme.renderer.Renderer;
import com.jme.renderer.TextureRenderer;
import com.jme.renderer.TextureRenderer.Target;
import com.jme.renderer.jogl.JOGLContextCapabilities;
import com.jme.renderer.jogl.JOGLRenderer;
import com.jme.renderer.jogl.JOGLTextureRenderer;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.system.canvas.CanvasConstructor;
import com.jme.system.canvas.JMECanvas;
import com.jme.util.WeakIdentityCache;
import com.jmex.awt.input.AWTKeyInput;
import com.jmex.awt.input.AWTMouseInput;
import com.jmex.awt.jogl.JOGLAWTCanvas;

/**
 * @author Steve Vaughan
 */
public class JOGLDisplaySystem extends DisplaySystem {

    private static final Logger logger = Logger
            .getLogger(JOGLDisplaySystem.class.getName());

    private JOGLRenderer renderer;

    private RenderContext<GLContext> currentContext;

    private WeakIdentityCache<GLContext, RenderContext<GLContext>> contextStore = new WeakIdentityCache<GLContext, RenderContext<GLContext>>();

    private Frame frame;

    private GLAutoDrawable autoDrawable;

    private boolean isClosing = false;
    
    private final DisplayMode[] availableDisplayModes;
    
    
    JOGLDisplaySystem() {
        super();
        final GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        availableDisplayModes = gd.getDisplayModes();
    }

    @Override
    public synchronized void createWindow(int width, int height, int bpp,
            int frq, boolean fs) {
        // For know, ensure that only one OpenGL surface is active at a time.
        if (autoDrawable != null) {
            throw new IllegalStateException(
                    "There is already an active OpenGL canvas.");
        }

        // Validate window dimensions.
        if (width <= 0 || height <= 0) {
            throw new JmeException("Invalid resolution values: " + width + " "
                    + height);
        }

        // Validate bit depth.
        if ((bpp != 32) && (bpp != 16) && (bpp != 24)) {
            throw new JmeException("Invalid pixel depth: " + bpp);
        }

        // Remember the window surfaces attributes.
        this.width = width;
        this.height = height;
        this.bpp = bpp;
        this.frq = frq;
        this.fs = fs;

        // Create the OpenGL canvas, and place it within a frame.
        frame = new Frame();

        // Create the singleton's status.
        GLCanvas glCanvas = createGLCanvas();
        glCanvas.setSize(width, height);
        glCanvas.setIgnoreRepaint(true);
        glCanvas.setAutoSwapBufferMode(false);

        // GLContext glContext = glCanvas.getContext();
        // glContext.makeCurrent();
        frame.add(glCanvas);
        final boolean isDisplayModeModified;
        final GraphicsDevice gd = GraphicsEnvironment
                .getLocalGraphicsEnvironment().getDefaultScreenDevice();
        // Get the current display mode
        final DisplayMode previousDisplayMode=gd.getDisplayMode();
        // Handle full screen mode if requested.
        if (fs) {
            frame.setUndecorated(true);
            // Check if the full-screen mode is supported by the OS
            boolean isFullScreenSupported = gd.isFullScreenSupported();
            if (isFullScreenSupported) {
            	gd.setFullScreenWindow(frame);
                // Check if display mode changes are supported by the OS
                if (gd.isDisplayChangeSupported()) {
                    // Get all available display modes
                    DisplayMode[] displayModes = gd.getDisplayModes();
                    DisplayMode multiBitsDepthSupportedDisplayMode = null;
                    DisplayMode refreshRateUnknownDisplayMode = null;
                    DisplayMode multiBitsDepthSupportedAndRefreshRateUnknownDisplayMode = null;
                    DisplayMode matchingDisplayMode = null;
                    DisplayMode currentDisplayMode;
                    // Look for the display mode that matches with our parameters
                    // Look for some display modes that are close to these parameters
                    // and that could be used as substitutes
                    // On some machines, the refresh rate is unknown and/or multi bit
                    // depths are supported. If you try to force a particular refresh 
                    // rate or a bit depth, you might find no available display mode
                    // that matches exactly with your parameters
                    for (int i = 0; i < displayModes.length && matchingDisplayMode == null; i++) {
                        currentDisplayMode = displayModes[i];
                        if (currentDisplayMode.getWidth()  == width &&
                            currentDisplayMode.getHeight() == height) {
                            if (currentDisplayMode.getBitDepth() == bpp) {
                                if (currentDisplayMode.getRefreshRate() == frq) {
                                    matchingDisplayMode = currentDisplayMode;
                                } else if (currentDisplayMode.getRefreshRate() == DisplayMode.REFRESH_RATE_UNKNOWN) {
                                    refreshRateUnknownDisplayMode = currentDisplayMode;
                                }
                            } else if (currentDisplayMode.getBitDepth() == DisplayMode.BIT_DEPTH_MULTI) {
                                if (currentDisplayMode.getRefreshRate() == frq) {
                                    multiBitsDepthSupportedDisplayMode = currentDisplayMode;
                                } else if (currentDisplayMode.getRefreshRate() == DisplayMode.REFRESH_RATE_UNKNOWN) {
                                    multiBitsDepthSupportedAndRefreshRateUnknownDisplayMode = currentDisplayMode;
                                }
                            }
                        }
                    }
                    DisplayMode nextDisplayMode = null;
                    if (matchingDisplayMode != null) {
                        nextDisplayMode = matchingDisplayMode;                    
                    } else if (multiBitsDepthSupportedDisplayMode != null) {
                        nextDisplayMode = multiBitsDepthSupportedDisplayMode;
                    } else if (refreshRateUnknownDisplayMode != null) {
                        nextDisplayMode = refreshRateUnknownDisplayMode;
                    } else if (multiBitsDepthSupportedAndRefreshRateUnknownDisplayMode != null) {
                        nextDisplayMode = multiBitsDepthSupportedAndRefreshRateUnknownDisplayMode;
                    } else {
                        isFullScreenSupported = false;
                    }
                    // If we have found a display mode that approximatively matches
                    // with the input parameters, use it
                    if (nextDisplayMode != null) {
                        gd.setDisplayMode(nextDisplayMode);
                        isDisplayModeModified = true;
                    } else { 
                        isDisplayModeModified = false;
                    }
                 } else {
                     isDisplayModeModified = false;
                     // Resize the canvas if the display mode cannot be changed
                     // and the screen size is not equal to the canvas size
                     Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
                     if (screenSize.width != width || screenSize.height != height) {
                         this.width = screenSize.width;
                         this.height = screenSize.height;
                         glCanvas.setSize(screenSize);
                     }
                 }
            } else {
                isDisplayModeModified = false;
            }
                
            // Software windowed full-screen mode
            if (!isFullScreenSupported) {
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                // Resize the canvas
                glCanvas.setSize(screenSize);
                this.width = screenSize.width;
                this.height = screenSize.height;
                // Resize the frame so that it occupies the whole screen
                frame.setSize(screenSize);
                // Set its location at the top left corner
                frame.setLocation(0, 0);
            }
        }
        // Otherwise, center the window on the screen.
        else {
            isDisplayModeModified = false;
            frame.pack();

            int x, y;
            x = (Toolkit.getDefaultToolkit().getScreenSize().width - width) / 2;
            y = (Toolkit.getDefaultToolkit().getScreenSize().height - height) / 2;
            frame.setLocation(x, y);
        }
       
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public final void windowClosing(WindowEvent e) {
                isClosing = true;
                // If required, restore the previous display mode
                if (isDisplayModeModified) {
                    gd.setDisplayMode(previousDisplayMode);
                }
                // If required, get back to the windowed mode
                if (gd.getFullScreenWindow() == frame) {
                    gd.setFullScreenWindow(null);
                }
            }
        });

        // Make the window visible to realize the OpenGL surface.
        frame.setVisible(true);

        // Make the GLContext the current.
        GLContext glContext = glCanvas.getContext();
        while (glContext.makeCurrent() == GLContext.CONTEXT_NOT_CURRENT) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException interruption) {
                logger.warning("Interruped while waiting for makeCurrent()");
            }
        }

        // Now it is time to request the focus because the canvas
        // is displayable, focusable, visible and its ancestor is
        // visible too
        glCanvas.requestFocusInWindow();

        // Store singleton OpenGL canvas.
        autoDrawable = glCanvas;
        created = true;

        // Initialize the display system.
        initForWindow(width, height);
    }

    @Override
    public JMECanvas createCanvas(int width, int height) {
        return this.createCanvas(width, height, "AWT",
                new HashMap<String, Object>());
    }

    @Override
    public synchronized JMECanvas createCanvas(int width, int height,
            String type, HashMap<String, Object> props) {
        // For know, ensure that only one OpenGL surface is active at a time.
        if (autoDrawable != null) {
            throw new IllegalStateException(
                    "There is already an active OpenGL canvas.");
        }

        // Validate window dimensions.
        if (width <= 0 || height <= 0) {
            throw new JmeException("Invalid resolution values: " + width + " "
                    + height);
        }

        // Retrieve registered constructor, or throw an exception.
        CanvasConstructor constructor = makeCanvasConstructor(type);

        // Remember the window surfaces attributes.
        this.width = width;
        this.height = height;
        // FIXME this.bpp = bpp;
        // FIXME this.frq = frq;
        this.fs = false;

        // Create the new canvas.
        JMECanvas glCanvas = constructor.makeCanvas(props);

        // Configure the canvas.
        ((Canvas) glCanvas).setSize(width, height);

        // Store singleton OpenGL canvas.
        autoDrawable = (GLAutoDrawable) glCanvas;
        created = true;

        return glCanvas;
    }

    @Override
    public void createHeadlessWindow(int w, int h, int bpp) {
        // TODOX Auto-generated method stub
    }

    @Override
    public TextureRenderer createTextureRenderer(final int width,
            final int height, final Target target) {
        if (!isCreated()) {
            return null;
        }

        return new JOGLTextureRenderer(width, height, this, renderer);
    }

    @Override
    public TextureRenderer createTextureRenderer(int width, int height, int samples, Target target) {
        if (!isCreated()) {
            return null;
        }

        return new JOGLTextureRenderer(width, height, this, renderer);
    }

    public static JOGLAWTCanvas createGLCanvas() {
        // Initialize the OpenGL requested capabilities.
        final GLCapabilities caps = new GLCapabilities();
        caps.setHardwareAccelerated(true);
        caps.setDoubleBuffered(true);
        DisplaySystem ds = DisplaySystem.getDisplaySystem();
        caps.setAlphaBits(ds.getMinAlphaBits());
        caps.setDepthBits(ds.getMinDepthBits());
        caps.setStencilBits(ds.getMinStencilBits());
        caps.setNumSamples(ds.getMinSamples());

        // Create the OpenGL canvas,
        final JOGLAWTCanvas glCanvas = new JOGLAWTCanvas(caps);

        // Put the window into orthographic projection mode with 1:1
        // pixel
        // ratio.
        // We haven't used GLU here to do this to avoid an unnecessary
        // dependency.
        // TODO Same initialization should probably be used for all
        // OpenGL
        // surfaces (VBO, Pbuffer, etc.).
        // final GL gl = glCanvas.getGL();
        // gl.setSwapInterval(0);
        // gl.glMatrixMode(GL.GL_PROJECTION);
        // gl.glLoadIdentity();
        // gl.glOrtho(0.0, glCanvas.getWidth(), 0.0, glCanvas.getHeight(),
        // -1.0,
        // 1.0);
        // gl.glMatrixMode(GL.GL_MODELVIEW);
        // gl.glLoadIdentity();
        // XXX: Same as default JOGLCamer.doViewPortChange
        // gl.glViewport(0, 0, glCanvas.getWidth(),
        // glCanvas.getHeight());
        //
        // // Clear window to avoid the desktop "showing through"
        // gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        // // Enable automatic checking for OpenGL errors.
        // // TODO Make this configurable, possibly from a system property.
        // glCanvas.setGL(new DebugGL(glCanvas.getGL()));

        // FIXME All of this is boilerplate which should be a part of the
        // MouseInput, KeyInput, etc. classes if possible.
        MouseInput.setProvider(MouseInput.INPUT_AWT);
        ((AWTMouseInput) MouseInput.get()).setDragOnly(true);
        final MouseListener mouseListener = (MouseListener) MouseInput.get();
        glCanvas.addMouseListener(mouseListener);
        glCanvas.addMouseMotionListener((MouseMotionListener) MouseInput.get());
        glCanvas.addMouseWheelListener((MouseWheelListener) MouseInput.get());

        // Setting a custom cursor when hidden does not change its visibility
        ((AWTMouseInput) MouseInput.get()).setHardwareCursor(new Cursor(Cursor.HAND_CURSOR));

        KeyInput.setProvider(KeyInput.INPUT_AWT);
        final KeyListener keyListener = (KeyListener) KeyInput.get();
        glCanvas.addKeyListener(keyListener);

        // TODO Look into JInput.
        // JoystickInput.setProvider(JoystickInput.INPUT_DUMMY);

        glCanvas.setFocusable(true);
        glCanvas.requestFocus();
        glCanvas.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent event) {
                ((AWTMouseInput) MouseInput.get()).setEnabled(true);
                ((AWTKeyInput) KeyInput.get()).setEnabled(true);
            }

            @Override
            public void focusLost(FocusEvent event) {
                ((AWTMouseInput) MouseInput.get()).setEnabled(false);
                ((AWTKeyInput) KeyInput.get()).setEnabled(false);
            }

        });

        return glCanvas;
    }

    @Override
    public JOGLRenderer getRenderer() {
        return renderer;
    }

    @Override
    public void setTitle(String title) {
        if (frame != null)
            frame.setTitle(title);
    }

    @Override
    public String getAdapter() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RenderContext<GLContext> getCurrentContext() {
        return currentContext;
    }

    @Override
    public String getDisplayAPIVersion() {
        try {
            GL gl = GLU.getCurrentGL();
            return gl.glGetString(GL.GL_VERSION);
        } catch (Exception e) {
            return "Unable to retrieve API version.";
        }
    }

    @Override
    public String getDisplayRenderer() {
        try {
            GL gl = GLU.getCurrentGL();
            return gl.glGetString(GL.GL_RENDERER);
        } catch (Exception e) {
            return "Unable to retrieve adapter details.";
        }
    }

    @Override
    public String getDisplayVendor() {
        try {
            GL gl = GLU.getCurrentGL();
            return gl.glGetString(GL.GL_VENDOR);
        } catch (Exception e) {
            return "Unable to retrieve vendor.";
        }
    }

    @Override
    public String getDriverVersion() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Initializes the window for the DisplaySystem.
     * <p>
     * NOTE: The window will already have been made visible, meaning that the
     * {@link java.awt.Component} will have been realized and the
     * {@link GLContext} will already have been made current.
     * 
     * @param width
     *            the width of the window's canvas.
     * @param height
     *            the height of the window's canvas.
     * @see #initForCanvas(int, int)
     */
    private void initForWindow(final int width, final int height) {
        // Enable automatic checking for OpenGL errors.
        // TODO Make this configurable, possibly from a system property.
        // autoDrawable.setGL(new DebugGL(autoDrawable.getGL()));

        // TODO Move this into the canvas, instead of grabbing the current
        // current GLAutoDrawable, since this precludes multiple canvases.
        // TODO Can this be centralized in createGLCanvas?
        JOGLContextCapabilities caps = new JOGLContextCapabilities(autoDrawable);
        renderer = new JOGLRenderer(this, caps, width, height);
        // TODO switching to GLAutoDrawable as the key would allow this to be
        // centralized. The other option is to move the context into the surface
        // where it belongs.
        currentContext = new RenderContext<GLContext>(autoDrawable.getContext());
        currentContext.setupRecords(renderer);
        updateStates(renderer);

        // XXX Currently assuming that VSync is disabled.
        GL gl = autoDrawable.getGL();
        gl.setSwapInterval(0);
    }

    /**
     * This will be called from a GLEventListener, meaning that the
     * {@link java.awt.Component} will have been realized and the
     * {@link GLContext} will already have been made current.
     */
    public void initForCanvas(final int width, final int height) {
        // FIXME This seems turned around.
        ((Canvas) autoDrawable).setSize(width, height);

        // Enable automatic checking for OpenGL errors.
        // TODO Make this configurable, possibly from a system property.
        // TODO Can this be centralized in createGLCanvas?
        autoDrawable.setGL(new DebugGL(autoDrawable.getGL()));

        // TODO Move this into the canvas, instead of grabbing the current
        // current GLAutoDrawable, since this precludes multiple canvases.
        JOGLContextCapabilities caps = new JOGLContextCapabilities(autoDrawable);
        renderer = new JOGLRenderer(this, caps, width, height);
        // XXX What does this mean? Copied from LWJGLDisplaySystem.
        renderer.setHeadless(true);
        // TODO switching to GLAutoDrawable as the key would allow this to be
        // centralized. The other option is to move the context into the surface
        // where it belongs.
        currentContext = new RenderContext<GLContext>(autoDrawable.getContext());
        currentContext.setupRecords(renderer);
        updateStates(renderer);

        // XXX Currently assuming that VSync is disabled.
        GL gl = autoDrawable.getGL();
        gl.setSwapInterval(0);
    }

    @Override
    public boolean isActive() {
        // TODO Auto-generated method stub return false;
        // FIXME handle both frame and canvas implementations.
        return frame.hasFocus();
    }

    @Override
    public boolean isClosing() {
        return isClosing;
    }

    @Override
    public boolean isValidDisplayMode(int width, int height, int bpp, int freq) {
        boolean isValid = false;
        for(DisplayMode dm : availableDisplayModes) {
            if( dm.getWidth() == width && dm.getHeight() == height && 
                    dm.getBitDepth() == bpp && dm.getRefreshRate() == freq ){
                 isValid = true; 
                 break;
            }
        }
        return( isValid );
    }

    @Override
    public void moveWindowTo(int locX, int locY) {
        frame.setLocation(locX,locY);
    }

    @Override
    public void recreateWindow(int w, int h, int bpp, int frq, boolean fs) {
        // TODO Auto-generated method stub

    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setIcon(Image[] iconImages) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setRenderer(final Renderer renderer) {
        if (renderer instanceof JOGLRenderer) {
            this.renderer = (JOGLRenderer) renderer;
        } else {
            logger.warning("Invalid Renderer type");
        }
    }

    @Override
    public void setVSyncEnabled(boolean enabled) {
        // TODO Should this be immediate?
        // FIXME What if the autoDrawable has not been allocated.
        autoDrawable.getGL().setSwapInterval(enabled ? 1 : 0);
    }

    @Override
    protected void updateDisplayBGC() {
        // TODO Auto-generated method stub

    }

    @Override
    public void close() {
        // Dispose of any JOGL resources.
        if (autoDrawable != null) {
            try {
                // Handle the case where jME is controlling the game loop.
                if (GLContext.getCurrent() != null) {
                    // Release the OpenGL resources.
                    autoDrawable.getContext().release();
                } else {
                    // Assume that the single threaded model is in effect, and
                    // request that the context be closed on that thread.
                    Threading.invokeOnOpenGLThread(new Runnable() {

                        public void run() {
                            // Make the context current if necessary
                            if (GLContext.getCurrent() == null) {
                                autoDrawable.getContext().makeCurrent();
                            }

                            // Release the OpenGL resources.
                            autoDrawable.getContext().release();
                        }

                    });
                }
            } catch (GLException releaseFailure) {
                logger.log(Level.WARNING, "Failed to release OpenGL Context"
                        + autoDrawable, releaseFailure);
            }
        }

        // Dispose of any window resources.
        if (frame != null) {
            frame.dispose();
        }
    }

    /**
     * Switches to another RenderContext identified by the contextKey or to a
     * new RenderContext if none is provided.
     * 
     * @param contextKey
     *            key identifier
     * @return RenderContext identified by the contextKey or new RenderContext
     *         if none provided
     * @todo Move JOGL renderer and display system into the same package to
     *       allow for better permission control.
     */
    public synchronized RenderContext<GLContext> switchContext(
            final GLContext contextKey) {
        // Since we are switching contexts, make the provided context the
        // current context. Start by releasing any existing context.
        if (currentContext != null) {
            GLContext holder = currentContext.getContextHolder();
            holder.release();
        }

        // Make the new context the current context, waiting if necessary as the
        // context is initializing.
        while (contextKey.makeCurrent() == GLContext.CONTEXT_NOT_CURRENT) {
            try {
                logger.info("Waiting for the GLContext to initialize...");
                Thread.sleep(500);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

        // Get the stored context state records for this GLContext.
        currentContext = contextStore.get(contextKey);
        if (currentContext == null) {
            // Since the context has no known existing state records. Setup the
            // records and add them to the store.
            currentContext = new RenderContext<GLContext>(contextKey);
            currentContext.setupRecords(renderer);
            contextStore.put(contextKey, currentContext);
        }

        return currentContext;
    }

    public RenderContext<GLContext> removeContext(GLContext contextKey) {
        if (contextKey != null) {
            RenderContext<GLContext> context = contextStore.get(contextKey);
            if (context != currentContext) {
                return contextStore.remove(contextKey);
            } else {
                logger.warning("Can not remove current context.");
            }
        }
        return null;
    }
}
