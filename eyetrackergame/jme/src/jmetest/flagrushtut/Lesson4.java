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

import jmetest.renderer.TestSkybox;
import jmetest.terrain.TestTerrain;

import com.jme.app.BaseGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.light.DirectionalLight;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.Skybox;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Cylinder;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;
import com.jme.util.Timer;
import com.jme.util.geom.Debugger;
import com.jmex.terrain.TerrainBlock;
import com.jmex.terrain.util.MidPointHeightMap;
import com.jmex.terrain.util.ProceduralTextureGenerator;

/**
 * <code>Tutorial 4</code> Builds the environment (surrounding structure and sky).
 * framework for Flag Rush. For Flag Rush Tutorial Series.
 * 
 * @author Mark Powell
 */
public class Lesson4 extends BaseGame {
    private static final Logger logger = Logger.getLogger(Lesson4.class
            .getName());
    
	// the terrain we will drive over.
    private TerrainBlock tb;
    // The texture that makes up the "force field", we will keep a reference to it
    // here to allow us to animate it.
    private Texture t;
    //Sky box (we update it each frame)
	private Skybox skybox;
    
    //the timer
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
		Lesson4 app = new Lesson4();
		// We will load our own "fantastic" Flag Rush logo. Yes, I'm an artist.
		app.setConfigShowMode(ConfigShowMode.AlwaysShow, Lesson4.class
				.getClassLoader().getResource(
						"jmetest/data/images/FlagRush.png"));
		app.start();
	}

	/**
	 * During an update we look for the escape button and update the timer
	 * to get the framerate. Things are now starting to happen, so we will 
     * update 
	 * 
	 * @see com.jme.app.BaseGame#update(float)
	 */
	protected void update(float interpolation) {
		// update the time to get the framerate
		timer.update();
		interpolation = timer.getTimePerFrame();

        //We will use the interpolation value to keep the speed
        //of the forcefield consistent between computers.
        //we update the Y have of the texture matrix to give
        //the appearance the forcefield is moving.
        t.getTranslation().y += 0.3f * interpolation;
        //if the translation is over 1, it's wrapped, so go ahead
        //and check for this (to keep the vector's y value from getting
        //too large.)
        if(t.getTranslation().y > 1) {
            t.getTranslation().y = 0;
        }
		
        //we want to keep the skybox around our eyes, so move it with
        //the camera
		skybox.setLocalTranslation(cam.getLocation());
		
        // if escape was pressed, we exit
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("exit")) {
			finished = true;
		}
        
        //Because we are changing the scene (moving the skybox) we need to update
        //the graph.
        scene.updateGeometricState(interpolation, true);
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
        Debugger.drawBounds(scene, display.getRenderer());
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
				5000);
		Vector3f loc = new Vector3f(250.0f, 100.0f, 250.0f);
		Vector3f left = new Vector3f(-0.5f, 0.0f, 0.5f);
		Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
		Vector3f dir = new Vector3f(-0.5f, 0.0f, -0.5f);
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
        display.setTitle("Flag Rush");
        
		scene = new Node("Scene graph node");
		/** Create a ZBuffer to display pixels closest to the camera above farther ones.  */
	    ZBufferState buf = display.getRenderer().createZBufferState();
	    buf.setEnabled(true);
	    buf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
	    scene.setRenderState(buf);
		//Add terrain to the scene
        buildTerrain();
		//Light the world
	    buildLighting();
	    //add the force field fence
	    buildEnvironment();
	    //Add the skybox
	    buildSkyBox();

		// update the scene graph for rendering
		scene.updateGeometricState(0.0f, true);
		scene.updateRenderState();
	}
	
    /**
     * buildEnvironment will create a fence. This is done by hand
     * to show how to create geometry and shared this geometry.
     * Normally, you wouldn't build your models by hand as it is
     * too much of a trial and error process.
     */
	private void buildEnvironment() {
        //This is the main node of our fence
        Node forceFieldFence = new Node("fence");
        
        //This cylinder will act as the four main posts at each corner
        Cylinder postGeometry = new Cylinder("post", 10, 10, 1, 10);
        Quaternion q = new Quaternion();
        //rotate the cylinder to be vertical
        q.fromAngleAxis(FastMath.PI/2, new Vector3f(1,0,0));
        postGeometry.setLocalRotation(q);
        postGeometry.setModelBound(new BoundingBox());
        postGeometry.updateModelBound();
        
        //We will share the post 4 times (one for each post)
        //It is *not* a good idea to add the original geometry 
        //as the sharedmeshes will alter its local values.
        //We then translate the posts into position. 
        //Magic numbers are bad, but help illustrate the point.:)
        SharedMesh post1 = new SharedMesh("post1", postGeometry);
        post1.setLocalTranslation(new Vector3f(0,0.5f,0));
        SharedMesh post2 = new SharedMesh("post2", postGeometry);
        post2.setLocalTranslation(new Vector3f(32,0.5f,0));
        SharedMesh post3 = new SharedMesh("post3", postGeometry);
        post3.setLocalTranslation(new Vector3f(0,0.5f,32));
        SharedMesh post4 = new SharedMesh("post4", postGeometry);
        post4.setLocalTranslation(new Vector3f(32,0.5f,32));
        
        //This cylinder will be the horizontal struts that hold
        //the field in place.
        Cylinder strutGeometry = new Cylinder("strut", 10,10, 0.125f, 32);
        strutGeometry.setModelBound(new BoundingBox());
        strutGeometry.updateModelBound();
        
        //again, we'll share this mesh.
        //Some we need to rotate to connect various posts.
        SharedMesh strut1 = new SharedMesh("strut1", strutGeometry);
        Quaternion rotate90 = new Quaternion();
        rotate90.fromAngleAxis(FastMath.PI/2, new Vector3f(0,1,0));
        strut1.setLocalRotation(rotate90);
        strut1.setLocalTranslation(new Vector3f(16,3f,0));
        SharedMesh strut2 = new SharedMesh("strut2", strutGeometry);
        strut2.setLocalTranslation(new Vector3f(0,3f,16));
        SharedMesh strut3 = new SharedMesh("strut3", strutGeometry);
        strut3.setLocalTranslation(new Vector3f(32,3f,16));
        SharedMesh strut4 = new SharedMesh("strut4", strutGeometry);
        strut4.setLocalRotation(rotate90);
        strut4.setLocalTranslation(new Vector3f(16,3f,32));
        
        //Create the actual forcefield 
        //The first box handles the X-axis, the second handles the z-axis.
        //We don't rotate the box as a demonstration on how boxes can be 
        //created differently.
        Box forceFieldX = new Box("forceFieldX", new Vector3f(-16, -3f, -0.1f), new Vector3f(16f, 3f, 0.1f));
        forceFieldX.setModelBound(new BoundingBox());
        forceFieldX.updateModelBound();
        //We are going to share these boxes as well
        SharedMesh forceFieldX1 = new SharedMesh("forceFieldX1",forceFieldX);
        forceFieldX1.setLocalTranslation(new Vector3f(16,0,0));
        SharedMesh forceFieldX2 = new SharedMesh("forceFieldX2",forceFieldX);
        forceFieldX2.setLocalTranslation(new Vector3f(16,0,32));
        
        //The other box for the Z axis
        Box forceFieldZ = new Box("forceFieldZ", new Vector3f(-0.1f, -3f, -16), new Vector3f(0.1f, 3f, 16));
        forceFieldZ.setModelBound(new BoundingBox());
        forceFieldZ.updateModelBound();
        //and again we will share it
        SharedMesh forceFieldZ1 = new SharedMesh("forceFieldZ1",forceFieldZ);
        forceFieldZ1.setLocalTranslation(new Vector3f(0,0,16));
        SharedMesh forceFieldZ2 = new SharedMesh("forceFieldZ2",forceFieldZ);
        forceFieldZ2.setLocalTranslation(new Vector3f(32,0,16));
        
        //add all the force fields to a single node and make this node part of
        //the transparent queue.
        Node forceFieldNode = new Node("forceFieldNode");
        forceFieldNode.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
        forceFieldNode.attachChild(forceFieldX1);
        forceFieldNode.attachChild(forceFieldX2);
        forceFieldNode.attachChild(forceFieldZ1);
        forceFieldNode.attachChild(forceFieldZ2);
        
        //Add the alpha values for the transparent node
        BlendState as1 = display.getRenderer().createBlendState();
        as1.setBlendEnabled(true);
        as1.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
        as1.setDestinationFunction(BlendState.DestinationFunction.One);
        as1.setTestEnabled(true);
        as1.setTestFunction(BlendState.TestFunction.GreaterThan);
        as1.setEnabled(true);
        
        forceFieldNode.setRenderState(as1);
        
        //load a texture for the force field elements
        TextureState ts = display.getRenderer().createTextureState();
        t = TextureManager.loadTexture(Lesson2.class.getClassLoader()
                  .getResource("jmetest/data/texture/reflector.jpg"),
                  Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear);
        
        t.setWrap(Texture.WrapMode.Repeat);
        t.setTranslation(new Vector3f());
        ts.setTexture(t);
        
        forceFieldNode.setRenderState(ts);
        
       
        //put all the posts into a tower node
        Node towerNode = new Node("tower");
        towerNode.attachChild(post1);
        towerNode.attachChild(post2);
        towerNode.attachChild(post3);
        towerNode.attachChild(post4);
        
        //add the tower to the opaque queue (we don't want to be able to see through them)
        //and we do want to see them through the forcefield.
        towerNode.setRenderQueueMode(Renderer.QUEUE_OPAQUE);
        
        //load a texture for the towers
        TextureState ts2 = display.getRenderer().createTextureState();
        Texture t2 = TextureManager.loadTexture(Lesson2.class.getClassLoader()
                  .getResource("jmetest/data/texture/post.jpg"),
                  Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear);
        
        ts2.setTexture(t2);
        
        towerNode.setRenderState(ts2);
        
        //put all the struts into a single node.
        Node strutNode = new Node("strutNode");
        strutNode.attachChild(strut1);
        strutNode.attachChild(strut2);
        strutNode.attachChild(strut3);
        strutNode.attachChild(strut4);
        //this too is in the opaque queue.
        strutNode.setRenderQueueMode(Renderer.QUEUE_OPAQUE);
        
        //load a texture for the struts
        TextureState ts3 = display.getRenderer().createTextureState();
        Texture t3 = TextureManager.loadTexture(Lesson2.class.getClassLoader()
                  .getResource("jmetest/data/texture/rust.jpg"),
                  Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear);
        
        ts3.setTexture(t3);
        
        strutNode.setRenderState(ts3);
        
        //we will do a little 'tweaking' by hand to make it fit in the terrain a bit better.
        //first we'll scale the entire "model" by a factor of 5
        forceFieldFence.setLocalScale(new Vector3f(5,4,4));
        //now let's move the fence to to the height of the terrain and in a little bit.
        forceFieldFence.setLocalTranslation(new Vector3f(25, tb.getHeight(25,25) + 15, 25));
        
        //Attach all the pieces to the main fence node
        forceFieldFence.attachChild(forceFieldNode);
        forceFieldFence.attachChild(towerNode);
        forceFieldFence.attachChild(strutNode);
        
        scene.attachChild(forceFieldFence);
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
		
		
        MidPointHeightMap heightMap = new MidPointHeightMap(64, 1f);
        // Scale the data
        Vector3f terrainScale = new Vector3f(4, 0.0575f, 4);
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
        Texture t1 = TextureManager.loadTexture(pt.getImageIcon().getImage(),
                Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear, true);
        ts.setTexture(t1, 0);

        tb.setRenderState(ts);
        tb.setRenderQueueMode(Renderer.QUEUE_OPAQUE);
        scene.attachChild(tb);
        
        
	}
	
    /**
     * buildSkyBox creates a new skybox object with all the proper textures. The
     * textures used are the standard skybox textures from all the tests.
     *
     */
	private void buildSkyBox() {
        skybox = new Skybox("skybox", 10, 10, 10);

        Texture north = TextureManager.loadTexture(
            TestSkybox.class.getClassLoader().getResource(
            "jmetest/data/texture/north.jpg"),
            Texture.MinificationFilter.BilinearNearestMipMap,
            Texture.MagnificationFilter.Bilinear);
        Texture south = TextureManager.loadTexture(
            TestSkybox.class.getClassLoader().getResource(
            "jmetest/data/texture/south.jpg"),
            Texture.MinificationFilter.BilinearNearestMipMap,
            Texture.MagnificationFilter.Bilinear);
        Texture east = TextureManager.loadTexture(
            TestSkybox.class.getClassLoader().getResource(
            "jmetest/data/texture/east.jpg"),
            Texture.MinificationFilter.BilinearNearestMipMap,
            Texture.MagnificationFilter.Bilinear);
        Texture west = TextureManager.loadTexture(
            TestSkybox.class.getClassLoader().getResource(
            "jmetest/data/texture/west.jpg"),
            Texture.MinificationFilter.BilinearNearestMipMap,
            Texture.MagnificationFilter.Bilinear);
        Texture up = TextureManager.loadTexture(
            TestSkybox.class.getClassLoader().getResource(
            "jmetest/data/texture/top.jpg"),
            Texture.MinificationFilter.BilinearNearestMipMap,
            Texture.MagnificationFilter.Bilinear);
        Texture down = TextureManager.loadTexture(
            TestSkybox.class.getClassLoader().getResource(
            "jmetest/data/texture/bottom.jpg"),
            Texture.MinificationFilter.BilinearNearestMipMap,
            Texture.MagnificationFilter.Bilinear);

        skybox.setTexture(Skybox.Face.North, north);
        skybox.setTexture(Skybox.Face.West, west);
        skybox.setTexture(Skybox.Face.South, south);
        skybox.setTexture(Skybox.Face.East, east);
        skybox.setTexture(Skybox.Face.Up, up);
        skybox.setTexture(Skybox.Face.Down, down);
        skybox.preloadTextures();
        scene.attachChild(skybox);
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
