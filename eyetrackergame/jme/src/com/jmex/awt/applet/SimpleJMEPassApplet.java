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

package com.jmex.awt.applet;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.input.FirstPersonHandler;
import com.jme.input.InputHandler;
import com.jme.input.InputSystem;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.renderer.pass.BasicPassManager;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.LightState;
import com.jme.scene.state.WireframeState;
import com.jme.system.DisplaySystem;
import com.jme.system.canvas.JMECanvas;
import com.jme.system.canvas.SimplePassCanvasImpl;
import com.jme.util.Debug;
import com.jme.util.GameTaskQueue;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.TextureManager;
import com.jme.util.stat.StatCollector;
import com.jme.util.stat.StatType;
import com.jme.util.stat.graph.GraphFactory;
import com.jme.util.stat.graph.LineGrapher;
import com.jme.util.stat.graph.TabledLabelGrapher;
import com.jmex.awt.input.AWTKeyInput;
import com.jmex.awt.input.AWTMouseInput;
import com.jmex.awt.lwjgl.LWJGLAWTCanvasConstructor;

public class SimpleJMEPassApplet extends Applet {
    private static final Logger logger = Logger
            .getLogger(SimpleJMEPassApplet.class.getName());
    
    private static final long serialVersionUID = 1L;

    private Canvas glCanvas;
    private SimpleAppletCanvasImplementor impl;

    private static final String INIT_LOCK = "INIT_LOCK";

    protected static final int STATUS_INITING = 0;
    protected static final int STATUS_RUNNING = 1;
    protected static final int STATUS_DESTROYING = 2;
    protected static final int STATUS_DEAD = 3;
    
    private static final String USE_APPLET_CANVAS_SIZE = "useAppletCanvasSize";
    private static final int DEFAULT_JME_CANVAS_WIDTH = 640;
    private static final int DEFAULT_JME_CANVAS_HEIGHT = 480;

    protected int status = STATUS_INITING;

    /**
     * Alpha bits to use for the renderer. Must be set in the constructor.
     */
    protected int alphaBits = 0;

    /**
     * Depth bits to use for the renderer. Must be set in the constructor.
     */
    protected int depthBits = 8;

    /**
     * Stencil bits to use for the renderer. Must be set in the constructor.
     */
    protected int stencilBits = 0;

    /**
     * Number of samples to use for the multisample buffer. Must be set in the constructor.
     */
    protected int samples = 0;

    @Override
    public void init() {

        synchronized (INIT_LOCK) {

            TextureManager.clearCache();
            Text.resetFontTexture();

            DisplaySystem display = DisplaySystem.getDisplaySystem();
            display.registerCanvasConstructor("AWT", LWJGLAWTCanvasConstructor.class);
            display.setMinDepthBits( depthBits );
            display.setMinStencilBits( stencilBits );
            display.setMinAlphaBits( alphaBits );
            display.setMinSamples( samples );

            int canvasWidth;
            int canvasHeight;
            /**
             * Check if we're using the applet's specified dimensions or the
             * default.
             */
            if (Boolean.parseBoolean(this.getParameter(USE_APPLET_CANVAS_SIZE))) {
                canvasWidth = getWidth();
                canvasHeight = getHeight();
            } else {
                canvasWidth = DEFAULT_JME_CANVAS_WIDTH;
                canvasHeight = DEFAULT_JME_CANVAS_HEIGHT;
            }
            glCanvas = (Canvas)DisplaySystem.getDisplaySystem().createCanvas(canvasWidth, canvasHeight);
            // Important! Here is where we add the guts to the canvas:
            impl = new SimpleAppletCanvasImplementor(getWidth(), getHeight());

            ((JMECanvas) glCanvas).setImplementor(impl);
            setLayout(new BorderLayout());
            add(glCanvas, BorderLayout.CENTER);

            glCanvas.addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent ce) {
                    if (impl != null) {
                        impl.resizeCanvas(glCanvas.getWidth(), glCanvas
                                .getHeight());
                        if (impl.getCamera() != null) {
                            Callable<?> exe = new Callable<Object>() {
                                public Object call() {
                                    impl.getCamera().setFrustumPerspective(
                                            45.0f,
                                            (float) glCanvas.getWidth()
                                                    / (float) glCanvas
                                                            .getHeight(), 1,
                                            1000);
                                    return null;
                                }
                            };
                            GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER).enqueue(exe);
                        }
                    }
                }
            });

            glCanvas.setFocusable(true);
            glCanvas.addFocusListener(new FocusListener() {

                public void focusGained(FocusEvent arg0) {
                    ((AWTKeyInput) KeyInput.get()).setEnabled(true);
                    ((AWTMouseInput) MouseInput.get()).setEnabled(true);
                }

                public void focusLost(FocusEvent arg0) {
                    ((AWTKeyInput) KeyInput.get()).setEnabled(false);
                    ((AWTMouseInput) MouseInput.get()).setEnabled(false);
                }

            });

            // We are going to use jme's Input systems, so enable updating.
            ((JMECanvas) glCanvas).setUpdateInput(true);

            if (!KeyInput.isInited())
                KeyInput.setProvider(InputSystem.INPUT_SYSTEM_AWT);
            ((AWTKeyInput) KeyInput.get()).setEnabled(false);
            KeyListener kl = (KeyListener) KeyInput.get();

            glCanvas.addKeyListener(kl);

            if (!MouseInput.isInited())
                MouseInput.setProvider(InputSystem.INPUT_SYSTEM_AWT);
            ((AWTMouseInput) MouseInput.get()).setEnabled(false);
            ((AWTMouseInput) MouseInput.get()).setDragOnly(true);
            glCanvas.addMouseListener((MouseListener) MouseInput.get());
            glCanvas.addMouseWheelListener((MouseWheelListener) MouseInput
                    .get());
            glCanvas.addMouseMotionListener((MouseMotionListener) MouseInput
                    .get());
            glCanvas.addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseMoved(java.awt.event.MouseEvent e) {
                    if (!glCanvas.hasFocus())
                        glCanvas.requestFocus();
                };
            });
        }
    }
    
    public void simpleAppletSetup() {
    }

    public void simpleAppletUpdate() {
    }

    public void simpleAppletRender() {
    }

    public Camera getCamera() {
        return impl.getCamera();
    }

    public Renderer getRenderer() {
        return impl.getRenderer();
    }

    public Node getRootNode() {
        return impl.getRootNode();
    }

    public Node getStatNode() {
        return impl.getStatNode();
    }

    public float getTimePerFrame() {
        return impl.getTimePerFrame();
    }

    public LightState getLightState() {
        return impl.getLightState();
    }

    public WireframeState getWireframeState() {
        return impl.getWireframeState();
    }
    
    public InputHandler getInputHandler() {
        return impl.getInputHandler();
    }
    
    public void setInputHandler(InputHandler input) {
        impl.setInputHandler(input);
    }
    
    public BasicPassManager getManager() {
        return impl.getManager();
    }

    class SimpleAppletCanvasImplementor extends SimplePassCanvasImpl {

        /**
         * True if the renderer should display the depth buffer.
         */
        protected boolean showDepth = false;

        /**
         * True if the renderer should display bounds.
         */
        protected boolean showBounds = false;

        /**
         * True if the rnederer should display normals.
         */
        protected boolean showNormals = false;

        protected boolean pause;

        /**
         * A wirestate to turn on and off for the rootNode
         */
        protected WireframeState wireState;

        private InputHandler input;

        /**
         * A lightstate to turn on and off for the rootNode
         */
        protected LightState lightState;

        /**
         * The root node of our stat graphs.
         */
        protected Node statNode;

        private TabledLabelGrapher tgrapher;
        private Quad labGraph;
        
        protected SimpleAppletCanvasImplementor(int width, int height) {
            super(width, height);
        }
 
        public Node getStatNode() {
            return statNode;
        }

        public LightState getLightState() {
            return lightState;
        }

        public WireframeState getWireframeState() {
            return wireState;
        }
        
        public InputHandler getInputHandler() {
            return input;
        }
        
        public void setInputHandler(InputHandler input) {
            this.input = input;
        }

        public void simpleUpdate() {
            
            input.update(tpf);

            if (Debug.stats) {
                StatCollector.update();
                labGraph.setLocalTranslation(.5f*labGraph.getWidth(), (renderer.getHeight()-.5f*labGraph.getHeight()), 0);
            }

            simpleAppletUpdate();

            /** If toggle_pause is a valid command (via key p), change pause. */
            if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
                    "toggle_pause", false ) ) {
                pause = !pause;
            }

            /** If toggle_wire is a valid command (via key T), change wirestates. */
            if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
                    "toggle_wire", false ) ) {
                wireState.setEnabled( !wireState.isEnabled() );
                rootNode.updateRenderState();
            }
            /** If toggle_lights is a valid command (via key L), change lightstate. */
            if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
                    "toggle_lights", false ) ) {
                lightState.setEnabled( !lightState.isEnabled() );
                rootNode.updateRenderState();
            }
            /** If toggle_bounds is a valid command (via key B), change bounds. */
            if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
                    "toggle_bounds", false ) ) {
                showBounds = !showBounds;
            }
            /** If toggle_depth is a valid command (via key F3), change depth. */
            if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
                    "toggle_depth", false ) ) {
                showDepth = !showDepth;
            }

            if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
                    "toggle_normals", false ) ) {
                showNormals = !showNormals;
            }
            /** If camera_out is a valid command (via key C), show camera location. */
            if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
                    "camera_out", false ) ) {
                logger.info( "Camera at: "
                        + renderer.getCamera().getLocation() );
            }

            if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
                    "screen_shot", false ) ) {
                renderer.takeScreenShot( "SimpleAppletScreenShot" );
            }

            if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
                    "mem_report", false ) ) {
                long totMem = Runtime.getRuntime().totalMemory();
                long freeMem = Runtime.getRuntime().freeMemory();
                long maxMem = Runtime.getRuntime().maxMemory();
                
                logger.info("|*|*|  Memory Stats  |*|*|");
                logger.info("Total memory: "+(totMem>>10)+" kb");
                logger.info("Free memory: "+(freeMem>>10)+" kb");
                logger.info("Max memory: "+(maxMem>>10)+" kb");
            }
        }

        public void simpleSetup() {
            synchronized (INIT_LOCK) {
                input = new FirstPersonHandler(getCamera(), 50, 1);

                /**
                 * Create a wirestate to toggle on and off. Starts disabled with
                 * default width of 1 pixel.
                 */
                wireState = renderer.createWireframeState();
                wireState.setEnabled(false);
                rootNode.setRenderState(wireState);

                // ---- LIGHTS
                /** Set up a basic, default light. */
                PointLight light = new PointLight();
                light.setDiffuse(new ColorRGBA(0.75f, 0.75f, 0.75f, 0.75f));
                light.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
                light.setLocation(new Vector3f(100, 100, 100));
                light.setEnabled(true);

                /**
                 * Attach the light to a lightState and the lightState to
                 * rootNode.
                 */
                lightState = renderer.createLightState();
                lightState.setEnabled(true);
                lightState.attach(light);
                rootNode.setRenderState(lightState);

                // Finally, a stand alone node (not attached to root on purpose)
                statNode = new Node("FPS node");
                statNode.setCullHint(Spatial.CullHint.Never);

                if (Debug.stats) {
                    setupStatGraphs();
                    setupStats();
                }

                statNode.updateGeometricState(0, true);
                statNode.updateRenderState();

                try {
                    simpleAppletSetup();
                } catch (Exception e) {
                    // Had issues setting up. We'll catch it and go on so it
                    // doesn't
                    // try setting up over and over.
                    logger.logp(Level.SEVERE, this.getClass().toString(),
                            "simpleSetup()", "Exception", e);
                }

                /** Assign key P to action "toggle_pause". */
                KeyBindingManager.getKeyBindingManager().set("toggle_pause",
                        KeyInput.KEY_P);
                /** Assign key T to action "toggle_wire". */
                KeyBindingManager.getKeyBindingManager().set("toggle_wire",
                        KeyInput.KEY_T);
                /** Assign key L to action "toggle_lights". */
                KeyBindingManager.getKeyBindingManager().set("toggle_lights",
                        KeyInput.KEY_L);
                /** Assign key B to action "toggle_bounds". */
                KeyBindingManager.getKeyBindingManager().set("toggle_bounds",
                        KeyInput.KEY_B);
                /** Assign key N to action "toggle_normals". */
                KeyBindingManager.getKeyBindingManager().set("toggle_normals",
                        KeyInput.KEY_N);
                /** Assign key C to action "camera_out". */
                KeyBindingManager.getKeyBindingManager().set("camera_out",
                        KeyInput.KEY_C);
                KeyBindingManager.getKeyBindingManager().set("screen_shot",
                        KeyInput.KEY_F1);
                KeyBindingManager.getKeyBindingManager().set("exit",
                        KeyInput.KEY_ESCAPE);
                KeyBindingManager.getKeyBindingManager().set("mem_report",
                        KeyInput.KEY_R);

                status = STATUS_RUNNING;
            }
        }

        public void simpleRender() {
            simpleAppletRender();
            statNode.draw(renderer);
        }

        /**
         * Set up which stats to graph
         *
         */
        protected void setupStats() {
        	tgrapher.addConfig(StatType.STAT_FRAMES, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.green);
        	tgrapher.addConfig(StatType.STAT_TRIANGLE_COUNT, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.cyan);
        	tgrapher.addConfig(StatType.STAT_QUAD_COUNT, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.lightGray);
        	tgrapher.addConfig(StatType.STAT_LINE_COUNT, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.red);
        	tgrapher.addConfig(StatType.STAT_GEOM_COUNT, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.gray);
        	tgrapher.addConfig(StatType.STAT_TEXTURE_BINDS, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.orange);

            tgrapher.addConfig(StatType.STAT_FRAMES, TabledLabelGrapher.ConfigKeys.Decimals.name(), 0);
            tgrapher.addConfig(StatType.STAT_FRAMES, TabledLabelGrapher.ConfigKeys.Name.name(), "Frames/s:");
            tgrapher.addConfig(StatType.STAT_TRIANGLE_COUNT, TabledLabelGrapher.ConfigKeys.Decimals.name(), 0);
            tgrapher.addConfig(StatType.STAT_TRIANGLE_COUNT, TabledLabelGrapher.ConfigKeys.Name.name(), "Avg.Tris:");
            tgrapher.addConfig(StatType.STAT_TRIANGLE_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
            tgrapher.addConfig(StatType.STAT_QUAD_COUNT, TabledLabelGrapher.ConfigKeys.Decimals.name(), 0);
            tgrapher.addConfig(StatType.STAT_QUAD_COUNT, TabledLabelGrapher.ConfigKeys.Name.name(), "Avg.Quads:");
            tgrapher.addConfig(StatType.STAT_QUAD_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
            tgrapher.addConfig(StatType.STAT_LINE_COUNT, TabledLabelGrapher.ConfigKeys.Decimals.name(), 0);
            tgrapher.addConfig(StatType.STAT_LINE_COUNT, TabledLabelGrapher.ConfigKeys.Name.name(), "Avg.Lines:");
            tgrapher.addConfig(StatType.STAT_LINE_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
            tgrapher.addConfig(StatType.STAT_GEOM_COUNT, TabledLabelGrapher.ConfigKeys.Decimals.name(), 0);
            tgrapher.addConfig(StatType.STAT_GEOM_COUNT, TabledLabelGrapher.ConfigKeys.Name.name(), "Avg.Objs:");
            tgrapher.addConfig(StatType.STAT_GEOM_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
            tgrapher.addConfig(StatType.STAT_TEXTURE_BINDS, TabledLabelGrapher.ConfigKeys.Decimals.name(), 0);
            tgrapher.addConfig(StatType.STAT_TEXTURE_BINDS, TabledLabelGrapher.ConfigKeys.Name.name(), "Avg.Tex binds:");
            tgrapher.addConfig(StatType.STAT_TEXTURE_BINDS, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
        }
        
        /**
         * Set up the graphers we will use and the quads we'll show the stats on.
         *
         */
        protected void setupStatGraphs() {
            StatCollector.setSampleRate(1000L);
            StatCollector.setMaxSamples(30);
            labGraph = new Quad("labelGraph", Math.max(renderer.getWidth()/3, 250), Math.max(renderer.getHeight()/3, 250)) {
                private static final long serialVersionUID = 1L;
                @Override
                public void draw(Renderer r) {
                    StatCollector.pause();
                    super.draw(r);
                    StatCollector.resume();
                }
            };
            tgrapher = GraphFactory.makeTabledLabelGraph((int)labGraph.getWidth(), (int)labGraph.getHeight(), labGraph);
            tgrapher.setColumns(1);
            labGraph.setLocalTranslation(0, (renderer.getHeight()*5/6), 0);
            statNode.attachChild(labGraph);
            
        }
    }
}
