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

package jmetest.flagrushtut.lesson6;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import jmetest.renderer.TestSkybox;
import jmetest.terrain.TestTerrain;

import com.jme.app.BaseGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.ChaseCamera;
import com.jme.input.InputHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.thirdperson.ThirdPersonMouseLook;
import com.jme.light.DirectionalLight;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Skybox;
import com.jme.scene.shape.Box;
import com.jme.scene.state.CullState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;
import com.jme.util.Timer;
import com.jmex.terrain.TerrainBlock;
import com.jmex.terrain.util.MidPointHeightMap;
import com.jmex.terrain.util.ProceduralTextureGenerator;

/**
 * <code>Tutorial 6</code> builds a custom Vehicle class using custom InputActions. This allows 
 * us to add a more "realistic" (realism being very subjective) vehicle handling system. There
 * is now an acceleration period, braking speed, turning speed, and friction.
 * 
 * @author Mark Powell
 */
public class Lesson6 extends BaseGame {
    private static final Logger logger = Logger.getLogger(Lesson6.class
            .getName());
    
    // the terrain we will drive over.
    private TerrainBlock tb;
    // fence that will keep us in.
    private ForceFieldFence fence;
    //Sky box (we update it each frame)
    private Skybox skybox;
    //the new player object
    private Vehicle player;
    //private ChaseCamera chaser;
    protected InputHandler input;
    
    //the timer
    protected Timer timer;

    // Our camera object for viewing the scene
    private Camera cam;
    //The chase camera, this will follow our player as he zooms around the level
    private ChaseCamera chaser;

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
        Lesson6 app = new Lesson6();
        // We will load our own "fantastic" Flag Rush logo. Yes, I'm an artist.
        app.setConfigShowMode(ConfigShowMode.AlwaysShow, Lesson6.class
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
        //update the keyboard input (move the player around)
        input.update(interpolation);
        //update the chase camera to handle the player moving around.
        chaser.update(interpolation);

        fence.update(interpolation);
        
        //we want to keep the skybox around our eyes, so move it with
        //the camera
        skybox.setLocalTranslation(cam.getLocation());
        
        // if escape was pressed, we exit
        if (KeyBindingManager.getKeyBindingManager().isValidCommand("exit")) {
            finished = true;
        }
        
        //We don't want the chase camera to go below the world, so always keep 
        //it 2 units above the level.
        if(cam.getLocation().y < (tb.getHeight(cam.getLocation())+2)) {
            cam.getLocation().y = tb.getHeight(cam.getLocation()) + 2;
            cam.update();
        }
        
        //make sure that if the player left the level we don't crash. When we add collisions,
        //the fence will do its job and keep the player inside.
        float characterMinHeight = tb.getHeight(player
                .getLocalTranslation())+((BoundingBox)player.getWorldBound()).yExtent;
        if (!Float.isInfinite(characterMinHeight) && !Float.isNaN(characterMinHeight)) {
            player.getLocalTranslation().y = characterMinHeight;
        }
        
        //Because we are changing the scene (moving the skybox and player) we need to update
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
        cam.setLocation(new Vector3f(200,1000,200));
        
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
        
        //Time for a little optimization. We don't need to render back face triangles, so lets
        //not. This will give us a performance boost for very little effort.
        CullState cs = display.getRenderer().createCullState();
        cs.setCullFace(CullState.Face.Back);
        scene.setRenderState(cs);
        
        //Add terrain to the scene
        buildTerrain();
        //Light the world
        buildLighting();
        //add the force field fence
        buildEnvironment();
        //Add the skybox
        buildSkyBox();
        //Build the player
        buildPlayer();
        //build the chase cam
        buildChaseCamera();
        //build the player input
        buildInput();

        // update the scene graph for rendering
        scene.updateGeometricState(0.0f, true);
        scene.updateRenderState();
    }
    
    /**
     * we are going to build the player object here. For now, we will use a box as a place 
     * holder. This is a good demonstration that you don't always need your graphics in place
     * before you can start working on your application.
     * 
     * We now have a Vehicle object that represents our player. The vehicle object will allow
     * us to have multiple vehicle types with different capabilities.
     *
     */
    private void buildPlayer() {
        //box stand in
        Box b = new Box("box", new Vector3f(), 0.35f,0.25f,0.5f);
        b.setModelBound(new BoundingBox());
        b.updateModelBound();
        
        //set the vehicles attributes (these numbers can be thought
        //of as Unit/Second).
        player = new Vehicle("Player Node", b);
        player.setAcceleration(15);
        player.setBraking(15);
        player.setTurnSpeed(5);
        player.setWeight(25);
        player.setMaxSpeed(15);
        player.setMinSpeed(5);
        
        player.setLocalTranslation(new Vector3f(100,0, 100));
        scene.attachChild(player);
        player.updateWorldBound();
        
        player.setRenderQueueMode(Renderer.QUEUE_OPAQUE);
    }
    
    /**
     * buildEnvironment will create a fence. 
     */
    private void buildEnvironment() {
        //This is the main node of our fence
        fence = new ForceFieldFence("fence");
        
        //we will do a little 'tweaking' by hand to make it fit in the terrain a bit better.
        //first we'll scale the entire "model" by a factor of 5
        fence.setLocalScale(5);
        //now let's move the fence to to the height of the terrain and in a little bit.
        fence.setLocalTranslation(new Vector3f(25, tb.getHeight(25,25)+10, 25));
        
        scene.attachChild(fence);
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
     * set the basic parameters of the chase camera. This includes the offset. We want
     * to be behind the vehicle and a little above it. So we will the offset as 0 for
     * x and z, but be 1.5 times higher than the node.
     * 
     * We then set the roll out parameters (2 units is the closest the camera can get, and
     * 5 is the furthest).
     *
     */
    private void buildChaseCamera() {
        Vector3f targetOffset = new Vector3f();
        targetOffset.y = ((BoundingBox) player.getWorldBound()).yExtent * 1.5f;
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put(ThirdPersonMouseLook.PROP_MAXROLLOUT, "6");
        props.put(ThirdPersonMouseLook.PROP_MINROLLOUT, "3");
        props.put(ThirdPersonMouseLook.PROP_MAXASCENT, ""+45 * FastMath.DEG_TO_RAD);
        props.put(ChaseCamera.PROP_INITIALSPHERECOORDS, new Vector3f(5, 0, 30 * FastMath.DEG_TO_RAD));
        props.put(ChaseCamera.PROP_TARGETOFFSET, targetOffset);
        props.put(ChaseCamera.PROP_DAMPINGK, "4");
        props.put(ChaseCamera.PROP_SPRINGK, "9");
        chaser = new ChaseCamera(cam, player, props);
        chaser.setMaxDistance(8);
        chaser.setMinDistance(2);
    }

    /**
     * create our custom input handler.
     *
     */
    private void buildInput() {
        input = new FlagRushHandler(player, settings.getRenderer());
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
