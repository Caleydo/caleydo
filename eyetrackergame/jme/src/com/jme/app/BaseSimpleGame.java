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
// $Id: BaseSimpleGame.java 4397 2009-06-14 12:05:31Z christoph.luder@gmail.com $
package com.jme.app;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.input.FirstPersonHandler;
import com.jme.input.InputHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.input.joystick.JoystickInput;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.Spatial.CullHint;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.LightState;
import com.jme.scene.state.WireframeState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.Debug;
import com.jme.util.GameTaskQueue;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.TextureManager;
import com.jme.util.Timer;
import com.jme.util.geom.Debugger;
import com.jme.util.stat.StatCollector;
import com.jme.util.stat.StatType;
import com.jme.util.stat.graph.DefColorFadeController;
import com.jme.util.stat.graph.GraphFactory;
import com.jme.util.stat.graph.LineGrapher;
import com.jme.util.stat.graph.TabledLabelGrapher;
import com.jmex.audio.AudioSystem;

/**
 * A game implementation that handles numerous common tasks.
 * <p>
 * This class implements all of the abstract methods and provides some default
 * input handlers and code to report on memory usage and to display simple
 * performance metrics on the screen.
 * <p>
 * All that is required to use this class is to build your scene graph (in
 * {@link #simpleInitGame()}) and add it to the {@link #rootNode}.
 * <p>
 * A light state is provided with a single point light, (you can easily add
 * more by accessing the {@link #lightState} field) and there is an FPS and
 * statistics display. The depth buffer compare function is automatically set
 * to {@code CF_LEQUAL}. Wireframe mode and the lights may be toggled via T
 * and L respectively.
 * <p>
 * Note that this class does <strong>not</strong> automatically render the
 * root node, you need to do this yourself by overriding {@link #simpleRender()}.
 * If you need to update your game state then a corresponding
 * {@link #simpleUpdate()} hook is also provided.
 * 
 * @author Joshua Slack, (javadoc by cep21)
 * @version $Revision: 4397 $, $Date: 2009-06-14 14:05:31 +0200 (So, 14 Jun 2009) $
 */
public abstract class BaseSimpleGame extends BaseGame {
    private static final Logger logger = Logger.getLogger(BaseSimpleGame.class
            .getName());

    /**
     * The camera that we see through.
     */
    protected Camera cam;

    /**
     * The root of our normal scene graph.
     */
    protected Node rootNode;

    /**
     * Handles our mouse/keyboard input.
     */
    protected InputHandler input;

    /**
     * High resolution timer for jME.
     */
    protected Timer timer;

    /**
     * The root node for our stats and text.
     */
    protected Node statNode;

    /**
     * The root node for our stats graphs.
     */
    protected Node graphNode;

    /**
     * Alpha bits to use for the renderer. Any changes must be made prior to call of start().
     */
    protected int alphaBits = 0;

    /**
     * Depth bits to use for the renderer. Any changes must be made prior to call of start().
     */
    protected int depthBits = 8;

    /**
     * Stencil bits to use for the renderer. Any changes must be made prior to call of start().
     */
    protected int stencilBits = 0;

    /**
     * Number of samples to use for the multisample buffer. Any changes must be made prior to call of start().
     */
    protected int samples = 0;

    /**
     * Simply an easy way to get at timer.getTimePerFrame(). Also saves math cycles since
     * you don't call getTimePerFrame more than once per frame.
     */
    protected float tpf;

    /**
     * True if the renderer should display the depth buffer.
     */
    protected boolean showDepth = false;

    /**
     * True if the renderer should display bounds.
     */
    protected boolean showBounds = false;

    /**
     * True if the renderer should display normals.
     */
    protected boolean showNormals = false;

    /**
     * True if the we should show the stats graphs.
     */
    protected boolean showGraphs = false;

    /**
     * A wirestate to turn on and off for the rootNode
     */
    protected WireframeState wireState;

    /**
     * A lightstate to turn on and off for the rootNode
     */
    protected LightState lightState;

    /**
     * boolean for toggling the simpleUpdate and geometric update parts of the
     * game loop on and off.
     */
    protected boolean pause;

    private TabledLabelGrapher tgrapher;

//  private TimedAreaGrapher lgrapher;
    private LineGrapher lgrapher;

    private Quad lineGraph, labGraph;

    public BaseSimpleGame() {
        System.setProperty("jme.stats", "set");
    }
    
    /**
     * Updates the timer, sets tpf, updates the input and updates the fps
     * string. Also checks keys for toggling pause, bounds, normals, lights,
     * wire etc.
     *
     * @param interpolation unused in this implementation
     * @see AbstractGame#update(float interpolation)
     */
    protected void update( float interpolation ) {
        /** Recalculate the framerate. */
        timer.update();
        /** Update tpf to time per frame according to the Timer. */
        tpf = timer.getTimePerFrame();

        /** Check for key/mouse updates. */
        updateInput();

        /** update stats, if enabled. */
        if (Debug.stats) {
            StatCollector.update();
        }
        
        // Execute updateQueue item
        GameTaskQueueManager.getManager().getQueue(GameTaskQueue.UPDATE).execute();

        /** If toggle_pause is a valid command (via key p), change pause. */
        if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
                "toggle_pause", false ) ) {
            pause = !pause;
        }
        
        /** If step is a valid command (via key ADD), update scenegraph one unit. */
        if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
                "step", true ) ) {
        	simpleUpdate();
        	rootNode.updateGeometricState(tpf, true);
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

        if (Debug.stats) {
            /** handle toggle_stats command (key F4) */
            if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
                    "toggle_stats", false ) ) {
                    showGraphs = !showGraphs;
                    Debug.updateGraphs = showGraphs;
                    labGraph.clearControllers();
                    lineGraph.clearControllers();
                    labGraph.addController(new DefColorFadeController(labGraph, showGraphs ? .6f : 0f, showGraphs ? .5f : -.5f));
                    lineGraph.addController(new DefColorFadeController(lineGraph, showGraphs ? .6f : 0f, showGraphs ? .5f : -.5f));
            }
        }

        if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
                "toggle_normals", false ) ) {
            showNormals = !showNormals;
        }
        /** If camera_out is a valid command (via key C), show camera location. */
        if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
                "camera_out", false ) ) {
            logger.info( "Camera at: "
                    + display.getRenderer().getCamera().getLocation() );
        }

        if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
                "screen_shot", false ) ) {
            display.getRenderer().takeScreenShot( "SimpleGameScreenShot" );
        }

        if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
                "parallel_projection", false ) ) {
            if ( cam.isParallelProjection() ) {
                cameraPerspective();
            }
            else {
                cameraParallel();
            }
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

        if ( KeyBindingManager.getKeyBindingManager().isValidCommand( "exit",
                false ) ) {
            finish();
        }
    }

    /**
     * Check for key/mouse updates. Allow overriding this method to skip update in subclasses.
     */
    protected void updateInput() {
        input.update( tpf );
    }

    /**
     * Clears stats, the buffers and renders bounds and normals if on.
     *
     * @param interpolation unused in this implementation
     * @see AbstractGame#render(float interpolation)
     */
    protected void render( float interpolation ) {
        Renderer r = display.getRenderer();
        /** Clears the previously rendered information. */
        r.clearBuffers();
        
        // Execute renderQueue item
        GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER).execute();
    }

    protected void doDebug(Renderer r) {
        /**
         * If showing bounds, draw rootNode's bounds, and the bounds of all its
         * children.
         */
        if ( showBounds ) {
            Debugger.drawBounds( rootNode, r, true );
        }

        if ( showNormals ) {
            Debugger.drawNormals( rootNode, r );
            Debugger.drawTangents( rootNode, r );
        }
    }

    /**
     * Creates display, sets up camera, and binds keys. Called in
     * BaseGame.start() directly after the dialog box.
     *
     * @see AbstractGame#initSystem()
     */
    protected void initSystem() throws JmeException {
        logger.info(getVersion());
        try {
            /**
             * Get a DisplaySystem acording to the renderer selected in the
             * startup box.
             */
            display = DisplaySystem.getDisplaySystem(settings.getRenderer() );
            
            display.setMinDepthBits( depthBits );
            display.setMinStencilBits( stencilBits );
            display.setMinAlphaBits( alphaBits );
            display.setMinSamples( samples );

            /** Create a window with the startup box's information. */
            display.createWindow(settings.getWidth(), settings.getHeight(),
                    settings.getDepth(), settings.getFrequency(),
                    settings.isFullscreen() );
            logger.info("Running on: " + display.getAdapter()
                    + "\nDriver version: " + display.getDriverVersion() + "\n"
                    + display.getDisplayVendor() + " - "
                    + display.getDisplayRenderer() + " - "
                    + display.getDisplayAPIVersion());
            
            
            /**
             * Create a camera specific to the DisplaySystem that works with the
             * display's width and height
             */
            cam = display.getRenderer().createCamera( display.getWidth(),
                    display.getHeight() );

        } catch ( JmeException e ) {
            /**
             * If the displaysystem can't be initialized correctly, exit
             * instantly.
             */
            logger.log(Level.SEVERE, "Could not create displaySystem", e);
            System.exit( 1 );
        }

        /** Set a black background. */
        display.getRenderer().setBackgroundColor( ColorRGBA.black.clone() );

        /** Set up how our camera sees. */
        cameraPerspective();
        Vector3f loc = new Vector3f( 0.0f, 0.0f, 25.0f );
        Vector3f left = new Vector3f( -1.0f, 0.0f, 0.0f );
        Vector3f up = new Vector3f( 0.0f, 1.0f, 0.0f );
        Vector3f dir = new Vector3f( 0.0f, 0f, -1.0f );
        /** Move our camera to a correct place and orientation. */
        cam.setFrame( loc, left, up, dir );
        /** Signal that we've changed our camera's location/frustum. */
        cam.update();
        /** Assign the camera to this renderer. */
        display.getRenderer().setCamera( cam );

        /** Create a basic input controller. */
        FirstPersonHandler firstPersonHandler = new FirstPersonHandler( cam, 50,
                1 );
        input = firstPersonHandler;

        /** Get a high resolution timer for FPS updates. */
        timer = Timer.getTimer();

        /** Sets the title of our display. */
        String className = getClass().getName();
        if ( className.lastIndexOf( '.' ) > 0 ) className = className.substring( className.lastIndexOf( '.' )+1 );
        display.setTitle( className );

        /** Assign key P to action "toggle_pause". */
        KeyBindingManager.getKeyBindingManager().set( "toggle_pause",
                KeyInput.KEY_P );
        /** Assign key ADD to action "step". */
        KeyBindingManager.getKeyBindingManager().set( "step",
                KeyInput.KEY_ADD );
        /** Assign key T to action "toggle_wire". */
        KeyBindingManager.getKeyBindingManager().set( "toggle_wire",
                KeyInput.KEY_T );
        /** Assign key L to action "toggle_lights". */
        KeyBindingManager.getKeyBindingManager().set( "toggle_lights",
                KeyInput.KEY_L );
        /** Assign key B to action "toggle_bounds". */
        KeyBindingManager.getKeyBindingManager().set( "toggle_bounds",
                KeyInput.KEY_B );
        /** Assign key N to action "toggle_normals". */
        KeyBindingManager.getKeyBindingManager().set( "toggle_normals",
                KeyInput.KEY_N );
        /** Assign key C to action "camera_out". */
        KeyBindingManager.getKeyBindingManager().set( "camera_out",
                KeyInput.KEY_C );
        /** Assign key R to action "mem_report". */
        KeyBindingManager.getKeyBindingManager().set("mem_report",
                KeyInput.KEY_R);

        KeyBindingManager.getKeyBindingManager().set( "exit",
                KeyInput.KEY_ESCAPE );
        
        KeyBindingManager.getKeyBindingManager().set( "screen_shot",
                KeyInput.KEY_F1 );
        KeyBindingManager.getKeyBindingManager().set( "parallel_projection",
                KeyInput.KEY_F2 );
        KeyBindingManager.getKeyBindingManager().set( "toggle_depth",
                KeyInput.KEY_F3 );
        KeyBindingManager.getKeyBindingManager().set( "toggle_stats",
                KeyInput.KEY_F4 );
    }

    protected void cameraPerspective() {
        cam.setFrustumPerspective( 45.0f, (float) display.getWidth()
                / (float) display.getHeight(), 1, 1000 );
        cam.setParallelProjection( false );
        cam.update();
    }

    protected void cameraParallel() {
        cam.setParallelProjection( true );
        float aspect = (float) display.getWidth() / display.getHeight();
        cam.setFrustum( -100, 1000, -50 * aspect, 50 * aspect, -50, 50 );
        cam.update();
    }

    /**
     * Creates rootNode, lighting, statistic text, and other basic render
     * states. Called in BaseGame.start() after initSystem().
     *
     * @see AbstractGame#initGame()
     */
    protected void initGame() {
        /** Create rootNode */
        rootNode = new Node( "rootNode" );

        /**
         * Create a wirestate to toggle on and off. Starts disabled with default
         * width of 1 pixel.
         */
        wireState = display.getRenderer().createWireframeState();
        wireState.setEnabled( false );
        rootNode.setRenderState( wireState );

        /**
         * Create a ZBuffer to display pixels closest to the camera above
         * farther ones.
         */
        ZBufferState buf = display.getRenderer().createZBufferState();
        buf.setEnabled( true );
        buf.setFunction( ZBufferState.TestFunction.LessThanOrEqualTo );
        rootNode.setRenderState( buf );

        // -- STATS, text node
        // Finally, a stand alone node (not attached to root on purpose)
        statNode = new Node( "Stats node" );
        statNode.setCullHint( Spatial.CullHint.Never );
        statNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);

        if (Debug.stats) {
            graphNode = new Node( "Graph node" );
            graphNode.setCullHint( Spatial.CullHint.Never );
            statNode.attachChild(graphNode);

            setupStatGraphs();
            setupStats();
        }

        // ---- LIGHTS
        /** Set up a basic, default light. */
        PointLight light = new PointLight();
        light.setDiffuse( new ColorRGBA( 0.75f, 0.75f, 0.75f, 0.75f ) );
        light.setAmbient( new ColorRGBA( 0.5f, 0.5f, 0.5f, 1.0f ) );
        light.setLocation( new Vector3f( 100, 100, 100 ) );
        light.setEnabled( true );

        /** Attach the light to a lightState and the lightState to rootNode. */
        lightState = display.getRenderer().createLightState();
        lightState.setEnabled( true );
        lightState.attach( light );
        rootNode.setRenderState( lightState );

        /** Let derived classes initialize. */
        simpleInitGame();

        timer.reset();

        /**
         * Update geometric and rendering information for both the rootNode and
         * fpsNode.
         */
        rootNode.updateGeometricState( 0.0f, true );
        rootNode.updateRenderState();
        statNode.updateGeometricState( 0.0f, true );
        statNode.updateRenderState();

        timer.reset();
    }

    /**
     * Called near end of initGame(). Must be defined by derived classes.
     */
    protected abstract void simpleInitGame();

    /**
     * Can be defined in derived classes for custom updating. Called every frame
     * in update.
     */
    protected void simpleUpdate() {
        //do nothing
    }

    /**
     * Can be defined in derived classes for custom rendering.
     * Called every frame in render.
     */
    protected void simpleRender() {
        //do nothing
    }

    /**
     * unused
     *
     * @see AbstractGame#reinit()
     */
    protected void reinit() {
        //do nothing
    }

    /**
     * Cleans up the keyboard.
     *
     * @see AbstractGame#cleanup()
     */
    protected void cleanup() {
        logger.info( "Cleaning up resources." );

        TextureManager.doTextureCleanup();
        if (display != null && display.getRenderer() != null)
            display.getRenderer().cleanup();
        KeyInput.destroyIfInitalized();
        MouseInput.destroyIfInitalized();
        JoystickInput.destroyIfInitalized();
        if (AudioSystem.isCreated()) {
            AudioSystem.getSystem().cleanup();
        }
    }

    /**
     * Calls the quit of BaseGame to clean up the display and then closes the JVM.
     */
    protected void quit() {
        super.quit();
        System.exit( 0 );
    }
    
    /**
     * Set up which stats to graph
     *
     */
    protected void setupStats() {
        lgrapher.addConfig(StatType.STAT_FRAMES, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.green);
        lgrapher.addConfig(StatType.STAT_FRAMES, LineGrapher.ConfigKeys.Stipple.name(), 0XFF0F);
        lgrapher.addConfig(StatType.STAT_TRIANGLE_COUNT, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.cyan);
        lgrapher.addConfig(StatType.STAT_TRIANGLE_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
        lgrapher.addConfig(StatType.STAT_QUAD_COUNT, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.lightGray);
        lgrapher.addConfig(StatType.STAT_QUAD_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
        lgrapher.addConfig(StatType.STAT_LINE_COUNT, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.red);
        lgrapher.addConfig(StatType.STAT_LINE_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
        lgrapher.addConfig(StatType.STAT_GEOM_COUNT, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.gray);
        lgrapher.addConfig(StatType.STAT_GEOM_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
        lgrapher.addConfig(StatType.STAT_TEXTURE_BINDS, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.orange);
        lgrapher.addConfig(StatType.STAT_TEXTURE_BINDS, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);

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
        
        // If you want to try out 
//        lgrapher.addConfig(StatType.STAT_RENDER_TIMER, TimedAreaGrapher.ConfigKeys.Color.name(), ColorRGBA.blue);
//        lgrapher.addConfig(StatType.STAT_UNSPECIFIED_TIMER, TimedAreaGrapher.ConfigKeys.Color.name(), ColorRGBA.white);
//        lgrapher.addConfig(StatType.STAT_STATES_TIMER, TimedAreaGrapher.ConfigKeys.Color.name(), ColorRGBA.yellow);
//        lgrapher.addConfig(StatType.STAT_DISPLAYSWAP_TIMER, TimedAreaGrapher.ConfigKeys.Color.name(), ColorRGBA.red);
//
//        tgrapher.addConfig(StatType.STAT_RENDER_TIMER, TabledLabelGrapher.ConfigKeys.Decimals.name(), 2);
//        tgrapher.addConfig(StatType.STAT_RENDER_TIMER, TabledLabelGrapher.ConfigKeys.Name.name(), "Render:");
//      tgrapher.addConfig(StatType.STAT_RENDER_TIMER, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
//        tgrapher.addConfig(StatType.STAT_UNSPECIFIED_TIMER, TabledLabelGrapher.ConfigKeys.Decimals.name(), 2);
//        tgrapher.addConfig(StatType.STAT_UNSPECIFIED_TIMER, TabledLabelGrapher.ConfigKeys.Name.name(), "Other:");
//      tgrapher.addConfig(StatType.STAT_UNSPECIFIED_TIMER, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
//        tgrapher.addConfig(StatType.STAT_STATES_TIMER, TabledLabelGrapher.ConfigKeys.Decimals.name(), 2);
//        tgrapher.addConfig(StatType.STAT_STATES_TIMER, TabledLabelGrapher.ConfigKeys.Name.name(), "States:");
//      tgrapher.addConfig(StatType.STAT_STATES_TIMER, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
//        tgrapher.addConfig(StatType.STAT_DISPLAYSWAP_TIMER, TabledLabelGrapher.ConfigKeys.Decimals.name(), 2);
//        tgrapher.addConfig(StatType.STAT_DISPLAYSWAP_TIMER, TabledLabelGrapher.ConfigKeys.Name.name(), "DisplaySwap:");
//      tgrapher.addConfig(StatType.STAT_DISPLAYSWAP_TIMER, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
//
//        StatCollector.addTimedStat(StatType.STAT_RENDER_TIMER);
//        StatCollector.addTimedStat(StatType.STAT_STATES_TIMER);
//        StatCollector.addTimedStat(StatType.STAT_UNSPECIFIED_TIMER);
//        StatCollector.addTimedStat(StatType.STAT_DISPLAYSWAP_TIMER);
    }
    
    /**
     * Set up the graphers we will use and the quads we'll show the stats on.
     *
     */
    protected void setupStatGraphs() {
        StatCollector.setSampleRate(1000L);
        StatCollector.setMaxSamples(40);

        lineGraph = new Quad("lineGraph", display.getWidth(), display.getHeight()*.75f) {
            private static final long serialVersionUID = 1L;
            @Override
            public void draw(Renderer r) {
                StatCollector.pause();
                super.draw(r);
                StatCollector.resume();
            }
        };
        lgrapher = GraphFactory.makeLineGraph((int)(lineGraph.getWidth()+.5f), (int)(lineGraph.getHeight()+.5f), lineGraph);
//      lgrapher = GraphFactory.makeTimedGraph((int)(lineGraph.getWidth()+.5f), (int)(lineGraph.getHeight()+.5f), lineGraph);
        lineGraph.setLocalTranslation((display.getWidth()*.5f), (display.getHeight()*.625f),0);
        lineGraph.setCullHint(CullHint.Always);
        lineGraph.getDefaultColor().a = 0;
        graphNode.attachChild(lineGraph);
        
        Text f4Hint = new Text("f4", "F4 - toggle stats") {
            private static final long serialVersionUID = 1L;
            @Override
            public void draw(Renderer r) {
                StatCollector.pause();
                super.draw(r);
                StatCollector.resume();
            }
        };
        f4Hint.setCullHint( Spatial.CullHint.Never );
        f4Hint.setRenderState( Text.getDefaultFontTextureState() );
        f4Hint.setRenderState( Text.getFontBlend() );
        f4Hint.setLocalScale(.8f);
        f4Hint.setTextColor(ColorRGBA.gray);
        f4Hint.setLocalTranslation(display.getRenderer().getWidth() - f4Hint.getWidth() - 15, display.getRenderer().getHeight() - f4Hint.getHeight() - 10, 0);
        graphNode.attachChild(f4Hint);

        labGraph = new Quad("labelGraph", display.getWidth(), display.getHeight()*.25f) {
            private static final long serialVersionUID = 1L;
            @Override
            public void draw(Renderer r) {
                StatCollector.pause();
                super.draw(r);
                StatCollector.resume();
            }
        };
        tgrapher = GraphFactory.makeTabledLabelGraph((int)(labGraph.getWidth()+.5f), (int)(labGraph.getHeight()+.5f), labGraph);
        tgrapher.setColumns(2);
        tgrapher.setMinimalBackground(false);
        tgrapher.linkTo(lgrapher);
        labGraph.setLocalTranslation((display.getWidth()*.5f), (display.getHeight()*.125f),0);
        labGraph.setCullHint(CullHint.Always);
        labGraph.getDefaultColor().a = 0;
        graphNode.attachChild(labGraph);
        
    }
}
