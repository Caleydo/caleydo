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
// $Id: SimpleHeadlessApp.java 4131 2009-03-19 20:15:28Z blaine.dev $
package com.jme.app;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.input.InputHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.input.joystick.JoystickInput;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.state.LightState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.Timer;

/**
 * Similar to {@link BaseSimpleGame} but without the FPS and stats rendering.
 *
 * @author Joshua Slack
 * @version $Revision: 4131 $, $Date: 2009-03-19 21:15:28 +0100 (Do, 19 Mär 2009) $
 */
public abstract class SimpleHeadlessApp extends BaseHeadlessApp {
    private static final Logger logger = Logger
            .getLogger(SimpleHeadlessApp.class.getName());

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
     * Simply an easy way to get at timer.getTimePerFrame().  Also saves time so you don't call it more than once per frame.
     */
    protected float tpf;
    /**
     * A lightstate to turn on and off for the rootNode
     */
    protected LightState lightState;

    long startTime = 0;
    long fps = 0;

    /**
     * This is called every frame in BaseGame.start()
     *
     * @param interpolation unused in this implementation
     * @see AbstractGame#update(float interpolation)
     */
    protected final void update( float interpolation ) {
        /** Recalculate the framerate. */
        timer.update();
        /** Update tpf to time per frame according to the Timer. */
        tpf = timer.getTimePerFrame();

        /** Call simpleUpdate in any derived classes of SimpleHeadlessApp. */
        simpleUpdate();

        /** Update controllers/render states/transforms/bounds for rootNode. */
        rootNode.updateGeometricState( tpf, true );

    }

    /**
     * This is called every frame in BaseGame.start(), after update()
     *
     * @param interpolation unused in this implementation
     * @see AbstractGame#render(float interpolation)
     */
    protected final void render( float interpolation ) {
        /** Clears the previously rendered information. */
        display.getRenderer().clearBuffers();
        /** Draw the rootNode and all its children. */
        display.getRenderer().draw( rootNode );
        /** Call simpleRender() in any derived classes. */
        simpleRender();

        if ( startTime > System.currentTimeMillis() ) {
            fps++;
        }
        else {
            long timeUsed = 5000 + ( startTime - System.currentTimeMillis() );
            startTime = System.currentTimeMillis() + 5000;
            logger.info( fps + " frames in " + ( timeUsed / 1000f ) + " seconds = "
                    + ( fps / ( timeUsed / 1000f ) ) );
            fps = 0;
        }
    }

    /**
     * Creates display, sets up camera, and binds keys.  Called in BaseGame.start() directly after
     * the dialog box.
     *
     * @see AbstractGame#initSystem()
     */
    protected final void initSystem() {
        try {
            /** Get a DisplaySystem acording to the renderer selected in the startup box. */
            display = DisplaySystem.getDisplaySystem( "dummy");
            /** Create a window with the startup box's information. */
            display.createHeadlessWindow(
                    settings.getWidth(),
                    settings.getHeight(),
                    settings.getDepth() );
            /** Create a camera specific to the DisplaySystem that works with
             * the display's width and height*/
            cam =
                    display.getRenderer().createCamera(
                            display.getWidth(),
                            display.getHeight() );

        }
        catch ( JmeException e ) {
            /** If the displaysystem can't be initialized correctly, exit instantly. */
            logger.log(Level.SEVERE, "Could not create displaySystem", e);
            System.exit( 1 );
        }

        /** Set a black background.*/
        display.getRenderer().setBackgroundColor( ColorRGBA.black.clone() );

        if(cam != null) {
            /** Set up how our camera sees. */
            cam.setFrustumPerspective( 45.0f,
                    (float) display.getWidth() /
                            (float) display.getHeight(), 1, 1000 );
            Vector3f loc = new Vector3f( 0.0f, 0.0f, 25.0f );
            Vector3f left = new Vector3f( -1.0f, 0.0f, 0.0f );
            Vector3f up = new Vector3f( 0.0f, 1.0f, 0.0f );
            Vector3f dir = new Vector3f( 0.0f, 0f, -1.0f );
            /** Move our camera to a correct place and orientation. */
            cam.setFrame( loc, left, up, dir );
            /** Signal that we've changed our camera's location/frustum. */
            cam.update();
            /** Assign the camera to this renderer.*/
            display.getRenderer().setCamera( cam );
        }

        /** Get a high resolution timer for FPS updates. */
        timer = Timer.getTimer();

        /** Sets the title of our display. */
        display.setTitle( "SimpleHeadlessApp" );
        /** Signal to the renderer that it should keep track of rendering information. */
        KeyBindingManager.getKeyBindingManager().set(
                "exit",
                KeyInput.KEY_ESCAPE );
    }

    /**
     * Creates rootNode, lighting, statistic text, and other basic render states.
     * Called in BaseGame.start() after initSystem().
     *
     * @see AbstractGame#initGame()
     */
    protected final void initGame() {
        /** Create rootNode */
        rootNode = new Node( "rootNode" );

        /** Create a ZBuffer to display pixels closest to the camera above farther ones.  */
        ZBufferState buf = display.getRenderer().createZBufferState();
        buf.setEnabled( true );
        buf.setFunction( ZBufferState.TestFunction.LessThanOrEqualTo );

        rootNode.setRenderState( buf );

        // ---- LIGHTS
        /** Set up a basic, default light. */
        PointLight light = new PointLight();
        light.setDiffuse( new ColorRGBA( 1.0f, 1.0f, 1.0f, 1.0f ) );
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

        /** Update geometric and rendering information for both the rootNode and fpsNode. */
        rootNode.updateGeometricState( 0.0f, true );
        rootNode.updateRenderState();

        startTime = System.currentTimeMillis() + 5000;
    }

    /**
     * Called near end of initGame(). Must be defined by derived classes.
     */
    protected abstract void simpleInitGame();

    /**
     * Can be defined in derived classes for custom updating.
     * Called every frame in update.
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
     * @see AbstractGame#cleanup()
     */
    protected void cleanup() {
        logger.info("Cleaning up resources.");

        KeyInput.destroyIfInitalized();
        MouseInput.destroyIfInitalized();
        JoystickInput.destroyIfInitalized();
  }
}
