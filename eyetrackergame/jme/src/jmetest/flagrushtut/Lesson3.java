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

package jmetest.flagrushtut;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import jmetest.terrain.TestTerrain;

import com.jme.app.BaseGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.light.DirectionalLight;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;
import com.jme.util.Timer;
import com.jmex.terrain.TerrainBlock;
import com.jmex.terrain.util.MidPointHeightMap;
import com.jmex.terrain.util.ProceduralTextureGenerator;

/**
 * <code>Tutorial 3</code> Loads a random terrain for uses at the game level.
 * framework for Flag Rush. For Flag Rush Tutorial Series.
 * 
 * @author Mark Powell
 */
public class Lesson3 extends BaseGame {
    private static final Logger logger = Logger.getLogger(Lesson3.class
            .getName());
    
	private TerrainBlock tb;

	protected Timer timer;

	// Our camera object for viewing the scene
	private Camera cam;

	// the root node of the scene graph
	private Node scene;

	// display attributes for the window. We will keep these values
	// to allow the user to change them
	private int width, height, depth, freq;

	private boolean fullscreen;

	/**
	 * Main entry point of the application
	 */
	public static void main(String[] args) {
		Lesson3 app = new Lesson3();
		// We will load our own "fantastic" Flag Rush logo. Yes, I'm an artist.
		app.setConfigShowMode(ConfigShowMode.AlwaysShow, Lesson3.class
				.getClassLoader().getResource(
						"jmetest/data/images/FlagRush.png"));
		app.start();
	}

	/**
	 * During an update we only look for the escape button and update the timer
	 * to get the framerate.
	 * 
	 * @see com.jme.app.BaseGame#update(float)
	 */
	protected void update(float interpolation) {
		// update the time to get the framerate
		timer.update();
		interpolation = timer.getTimePerFrame();
		// if escape was pressed, we exit
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("exit")) {
			finished = true;
		}
	}

	/**
	 * draws the scene graph
	 * 
	 * @see com.jme.app.BaseGame#render(float)
	 */
	protected void render(float interpolation) {
		// Clear the screen
		display.getRenderer().clearBuffers();

		display.getRenderer().draw(scene);

	}

	/**
	 * initializes the display and camera.
	 * 
	 * @see com.jme.app.BaseGame#initSystem()
	 */
	protected void initSystem() {
		// store the settings information
		width = settings.getWidth();
		height = settings.getHeight();
		depth = settings.getDepth();
		freq = settings.getFrequency();
		fullscreen = settings.isFullscreen();

		try {
			display = DisplaySystem.getDisplaySystem(settings.getRenderer());
			display.createWindow(width, height, depth, freq, fullscreen);

			cam = display.getRenderer().createCamera(width, height);
		} catch (JmeException e) {
            logger.log(Level.SEVERE, "Could not create displaySystem", e);
			System.exit(1);
		}

		// set the background to black
		display.getRenderer().setBackgroundColor(ColorRGBA.black.clone());

		// initialize the camera
		cam.setFrustumPerspective(45.0f, (float) width / (float) height, 1,
				1000);
		Vector3f loc = new Vector3f(500.0f, 150.0f, 500.0f);
		Vector3f left = new Vector3f(-1.0f, 0.0f, 0.0f);
		Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
		Vector3f dir = new Vector3f(0.0f, 0.0f, -1.0f);
		// Move our camera to a correct place and orientation.
		cam.setFrame(loc, left, up, dir);
		/** Signal that we've changed our camera's location/frustum. */
		cam.update();

		/** Get a high resolution timer for FPS updates. */
		timer = Timer.getTimer();

		display.getRenderer().setCamera(cam);

		KeyBindingManager.getKeyBindingManager().set("exit",
				KeyInput.KEY_ESCAPE);
	}

	/**
	 * initializes the scene
	 * 
	 * @see com.jme.app.BaseGame#initGame()
	 */
	protected void initGame() {
		scene = new Node("Scene graph node");
		buildTerrain();
		scene.attachChild(tb);
		
	    buildLighting();

		// update the scene graph for rendering
		scene.updateGeometricState(0.0f, true);
		scene.updateRenderState();
	}

	/**
	 * creates a light for the terrain.
	 */
	private void buildLighting() {
		/** Set up a basic, default light. */
	    DirectionalLight light = new DirectionalLight();
	    light.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
	    light.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
	    light.setDirection(new Vector3f(1,-1,0));
	    light.setEnabled(true);

	      /** Attach the light to a lightState and the lightState to rootNode. */
	    LightState lightState = display.getRenderer().createLightState();
	    lightState.setEnabled(true);
	    lightState.attach(light);
	    scene.setRenderState(lightState);
	}

	/**
	 * build the height map and terrain block.
	 */
	private void buildTerrain() {
		
		
		// Generate a random terrain data
		MidPointHeightMap heightMap = new MidPointHeightMap(64, 1f);
		// Scale the data
		Vector3f terrainScale = new Vector3f(20, 0.5f, 20);
		// create a terrainblock
		tb = new TerrainBlock("Terrain", heightMap.getSize(), terrainScale,
				heightMap.getHeightMap(), new Vector3f(0, 0, 0));

		tb.setModelBound(new BoundingBox());
		tb.updateModelBound();

		// generate a terrain texture with 2 textures
		ProceduralTextureGenerator pt = new ProceduralTextureGenerator(
				heightMap);
		pt.addTexture(new ImageIcon(TestTerrain.class.getClassLoader()
				.getResource("jmetest/data/texture/grassb.png")), -128, 0, 128);
		pt.addTexture(new ImageIcon(TestTerrain.class.getClassLoader()
				.getResource("jmetest/data/texture/dirt.jpg")), 0, 128, 255);
		pt.addTexture(new ImageIcon(TestTerrain.class.getClassLoader()
				.getResource("jmetest/data/texture/highest.jpg")), 128, 255,
				384);
		pt.createTexture(32);
		
		// assign the texture to the terrain
		TextureState ts = display.getRenderer().createTextureState();
		ts.setEnabled(true);
		Texture t1 = TextureManager.loadTexture(pt.getImageIcon().getImage(),
				Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear, true);
		ts.setTexture(t1, 0);

		tb.setRenderState(ts);
	}

	/**
	 * will be called if the resolution changes
	 * 
	 * @see com.jme.app.BaseGame#reinit()
	 */
	protected void reinit() {
		display.recreateWindow(width, height, depth, freq, fullscreen);
	}

    /**
     * close the window and also exit the program.
     */
    protected void quit() {
        super.quit();
        System.exit(0);
    }

	/**
	 * clean up the textures.
	 * 
	 * @see com.jme.app.BaseGame#cleanup()
	 */
	protected void cleanup() {

	}
}
