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

package jmetest.TutorialGuide;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.app.BaseGame;
import com.jme.app.SimpleGame;
import com.jme.image.Texture;
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
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.Spatial.TextureCombineMode;
import com.jme.scene.shape.Box;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.WireframeState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;
import com.jme.util.Timer;
import com.jme.util.geom.Debugger;

/**
 * Started Date: Jul 29, 2004<br><br>
 *
 * Is used to demonstrate the inner workings of SimpleGame.
 * 
 * @author Jack Lindamood
 */
public class HelloSimpleGame extends BaseGame {
    private static final Logger logger = Logger.getLogger(HelloSimpleGame.class
            .getName());
    
    public static void main(String[] args) {
        HelloSimpleGame app = new HelloSimpleGame();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    /** The camera that we see through. */
    protected Camera cam;
      /** The root of our normal scene graph. */
    protected Node rootNode;
      /** Handles our mouse/keyboard input. */
    protected InputHandler input;
      /** High resolution timer for jME. */
    protected Timer timer;
      /** The root node of our text. */
    protected Node fpsNode;
      /** Displays all the lovely information at the bottom. */
    protected Text fps;
      /** Simply an easy way to get at timer.getTimePerFrame(). */
    protected float tpf;
      /** True if the renderer should display bounds. */
    protected boolean showBounds = false;

      /** A wirestate to turn on and off for the rootNode */
    protected WireframeState wireState;
      /** A lightstate to turn on and off for the rootNode */
    protected LightState lightState;

      /** Location of the font for jME's text at the bottom */
    public static String fontLocation = "com/jme/app/defaultfont.tga";

    /**
     * This is called every frame in BaseGame.start()
     * @param interpolation unused in this implementation
     * @see com.jme.app.AbstractGame#update(float interpolation)
     */
    protected final void update(float interpolation) {
        /** Recalculate the framerate. */
      timer.update();
        /** Update tpf to time per frame according to the Timer. */
      tpf = timer.getTimePerFrame();
        /** Check for key/mouse updates. */
      input.update(tpf);
        /** Send the fps to our fps bar at the bottom. */
      fps.print("FPS: " + (int) timer.getFrameRate());
        /** Call simpleUpdate in any derived classes of SimpleGame. */
      simpleUpdate();

        /** Update controllers/render states/transforms/bounds for rootNode. */
      rootNode.updateGeometricState(tpf, true);

        /** If toggle_wire is a valid command (via key T), change wirestates. */
      if (KeyBindingManager
          .getKeyBindingManager()
          .isValidCommand("toggle_wire", false)) {
        wireState.setEnabled(!wireState.isEnabled());
        rootNode.updateRenderState();
      }
        /** If toggle_lights is a valid command (via key L), change lightstate. */
      if (KeyBindingManager
          .getKeyBindingManager()
          .isValidCommand("toggle_lights", false)) {
        lightState.setEnabled(!lightState.isEnabled());
        rootNode.updateRenderState();
      }
        /** If toggle_bounds is a valid command (via key B), change bounds. */
      if (KeyBindingManager
          .getKeyBindingManager()
          .isValidCommand("toggle_bounds", false)) {
        showBounds = !showBounds;
      }
        /** If camera_out is a valid command (via key C), show camera location. */
      if (KeyBindingManager
          .getKeyBindingManager()
          .isValidCommand("camera_out", false)) {
          logger.info("Camera at: " +
                           display.getRenderer().getCamera().getLocation());
      }
      
      if (KeyBindingManager.getKeyBindingManager().isValidCommand("exit", false)) {
          finish();
      }

    }

    /**
     * This is called every frame in BaseGame.start(), after update()
     * @param interpolation unused in this implementation
     * @see com.jme.app.AbstractGame#render(float interpolation)
     */
    protected final void render(float interpolation) {
        /** Clears the previously rendered information. */
      display.getRenderer().clearBuffers();
        /** Draw the rootNode and all its children. */
      display.getRenderer().draw(rootNode);
        /** If showing bounds, draw rootNode's bounds, and the bounds of all its children. */
      if (showBounds)
        Debugger.drawBounds(rootNode, display.getRenderer());
        /** Draw the fps node to show the fancy information at the bottom. */
      display.getRenderer().draw(fpsNode);
        /** Call simpleRender() in any derived classes. */
      simpleRender();
    }

    /**
     * Creates display, sets up camera, and binds keys.  Called in BaseGame.start() directly after
     * the dialog box.
     * @see com.jme.app.AbstractGame#initSystem()
     */
    protected final void initSystem() {
      try {
          /** Get a DisplaySystem acording to the renderer selected in the startup box. */
        display = DisplaySystem.getDisplaySystem(settings.getRenderer());
         /** Create a window with the startup box's information. */
        display.createWindow(
            settings.getWidth(),
            settings.getHeight(),
            settings.getDepth(),
            settings.getFrequency(),
            settings.isFullscreen());
           /** Create a camera specific to the DisplaySystem that works with
            * the display's width and height*/
        cam =
            display.getRenderer().createCamera(
            display.getWidth(),
            display.getHeight());

      }
      catch (JmeException e) {
          /** If the displaysystem can't be initialized correctly, exit instantly. */
          logger.log(Level.SEVERE, "Could not create displaySystem", e);
        System.exit(1);
      }

        /** Set a black background.*/
      display.getRenderer().setBackgroundColor(ColorRGBA.black.clone());

      /** Set up how our camera sees. */
      cam.setFrustumPerspective(45.0f,
                                (float) display.getWidth() /
                                (float) display.getHeight(), 1, 1000);
      Vector3f loc = new Vector3f(0.0f, 0.0f, 25.0f);
      Vector3f left = new Vector3f( -1.0f, 0.0f, 0.0f);
      Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
      Vector3f dir = new Vector3f(0.0f, 0f, -1.0f);
        /** Move our camera to a correct place and orientation. */
      cam.setFrame(loc, left, up, dir);
        /** Signal that we've changed our camera's location/frustum. */
      cam.update();
        /** Assign the camera to this renderer.*/
      display.getRenderer().setCamera(cam);

      /** Create a basic input controller. */
        FirstPersonHandler firstPersonHandler = new FirstPersonHandler( cam );
        /** Signal to all key inputs they should work 10x faster. */
        firstPersonHandler.getKeyboardLookHandler().setActionSpeed(10f);
        firstPersonHandler.getMouseLookHandler().setActionSpeed(1f);
        input = firstPersonHandler;

        /** Get a high resolution timer for FPS updates. */
      timer = Timer.getTimer();

        /** Sets the title of our display. */
      display.setTitle("SimpleGame");

        /** Assign key T to action "toggle_wire". */
      KeyBindingManager.getKeyBindingManager().set(
          "toggle_wire",
          KeyInput.KEY_T);
        /** Assign key L to action "toggle_lights". */
      KeyBindingManager.getKeyBindingManager().set(
          "toggle_lights",
          KeyInput.KEY_L);
        /** Assign key B to action "toggle_bounds". */
      KeyBindingManager.getKeyBindingManager().set(
          "toggle_bounds",
          KeyInput.KEY_B);
        /** Assign key C to action "camera_out". */
      KeyBindingManager.getKeyBindingManager().set(
          "camera_out",
          KeyInput.KEY_C);
      KeyBindingManager.getKeyBindingManager().set(
              "exit",
              KeyInput.KEY_ESCAPE);
    }

    /**
     * Creates rootNode, lighting, statistic text, and other basic render states.
     * Called in BaseGame.start() after initSystem().
     * @see com.jme.app.AbstractGame#initGame()
     */
    protected final void initGame() {
        /** Create rootNode */
      rootNode = new Node("rootNode");

      /** Create a wirestate to toggle on and off.  Starts disabled with
       * default width of 1 pixel. */
      wireState = display.getRenderer().createWireframeState();
      wireState.setEnabled(false);
      rootNode.setRenderState(wireState);

      /** Create a ZBuffer to display pixels closest to the camera above farther ones.  */
      ZBufferState buf = display.getRenderer().createZBufferState();
      buf.setEnabled(true);
      buf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);

      rootNode.setRenderState(buf);

      // -- FPS DISPLAY
      // First setup blend state
        /** This allows correct blending of text and what is already rendered below it*/
      BlendState as1 = display.getRenderer().createBlendState();
      as1.setBlendEnabled(true);
      as1.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
      as1.setDestinationFunction(BlendState.DestinationFunction.One);
      as1.setTestEnabled(true);
      as1.setTestFunction(BlendState.TestFunction.GreaterThan);
      as1.setEnabled(true);

      // Now setup font texture
      TextureState font = display.getRenderer().createTextureState();
        /** The texture is loaded from fontLocation */
      font.setTexture(
          TextureManager.loadTexture(
          SimpleGame.class.getClassLoader().getResource(
          fontLocation),
          Texture.MinificationFilter.BilinearNearestMipMap,
          Texture.MagnificationFilter.Bilinear));
      font.setEnabled(true);

      // Then our font Text object.
        /** This is what will actually have the text at the bottom. */
      fps = Text.createDefaultTextLabel("FPS label", "");
      fps.setCullHint(Spatial.CullHint.Never);
      fps.setTextureCombineMode(TextureCombineMode.Replace);

      // Finally, a stand alone node (not attached to root on purpose)
      fpsNode = new Node("FPS node");
      fpsNode.attachChild(fps);
      fpsNode.setRenderState(font);
      fpsNode.setRenderState(as1);
      fpsNode.setCullHint(Spatial.CullHint.Never);

      // ---- LIGHTS
        /** Set up a basic, default light. */
      PointLight light = new PointLight();
      light.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
      light.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
      light.setLocation(new Vector3f(100, 100, 100));
      light.setEnabled(true);

        /** Attach the light to a lightState and the lightState to rootNode. */
      lightState = display.getRenderer().createLightState();
      lightState.setEnabled(true);
      lightState.attach(light);
      rootNode.setRenderState(lightState);

        /** Let derived classes initialize. */
      simpleInitGame();

        /** Update geometric and rendering information for both the rootNode and fpsNode. */
      rootNode.updateGeometricState(0.0f, true);
      rootNode.updateRenderState();
      fpsNode.updateGeometricState(0.0f, true);
      fpsNode.updateRenderState();
    }

    protected void simpleInitGame() {
        rootNode.attachChild(new Box("my box",new Vector3f(0,0,0),new Vector3f(1,1,1)));
    }

    /**
       * Can be defined in derived classes for custom updating.
       * Called every frame in update.
       */
    protected void simpleUpdate() {}

      /**
       * Can be defined in derived classes for custom rendering.
       * Called every frame in render.
       */
    protected void simpleRender() {}

    /**
     * unused
     * @see com.jme.app.AbstractGame#reinit()
     */
    protected void reinit() {
    }

    /**
     * Cleans up the keyboard.
     * @see com.jme.app.AbstractGame#cleanup()
     */
    protected void cleanup() {
      logger.info("Cleaning up resources.");
      KeyInput.destroyIfInitalized();
      MouseInput.destroyIfInitalized();
      JoystickInput.destroyIfInitalized();
    }
}
