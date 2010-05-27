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

package jmetest.input;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.app.BaseGame;
import com.jme.input.InputHandler;
import com.jme.input.RelativeMouse;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.Text;
import com.jme.scene.state.BlendState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;

/**
 * <code>TestRelativeMouse</code>
 * @author Mark Powell
 * @version
 */
public class TestRelativeMouse extends BaseGame {
    private static final Logger logger = Logger
            .getLogger(TestRelativeMouse.class.getName());

    private Text text;
    private Camera cam;
    private Node scene;
    private RelativeMouse mouse;

    String xString, yString;
    private final InputHandler mouseInputHandler = new InputHandler();

    public static void main(String[] args) {
        TestRelativeMouse app = new TestRelativeMouse();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    /**
     * Not used.
     * @see com.jme.app.SimpleGame#update
     */
    protected void update(float interpolation) {
        mouseInputHandler.update( interpolation );

        if(mouse.getLocalTranslation().x > 0) {
            xString = "Right";
        } else if(mouse.getLocalTranslation().x < 0){
            xString = "Left";
        }

        if(mouse.getLocalTranslation().y > 0) {
            yString = "Up";
        } else if(mouse.getLocalTranslation().y < 0){
            yString = "Down";
        }
        text.print("Position: " + xString + " , " +
                yString);
    }

    /**
     * draws the scene graph
     * @see com.jme.app.SimpleGame#render
     */
    protected void render(float interpolation) {
        display.getRenderer().clearBuffers();

        display.getRenderer().draw(scene);
    }

    /**
     * initializes the display and camera.
     * @see com.jme.app.BaseGame#initSystem()
     */
    protected void initSystem() {
        try {
            display = DisplaySystem.getDisplaySystem(settings.getRenderer());
            display.createWindow(
                    settings.getWidth(),
                    settings.getHeight(),
                    settings.getDepth(),
                    settings.getFrequency(),
                    settings.isFullscreen());

            cam = display.getRenderer().createCamera(settings.getWidth(), settings.getHeight());
        } catch (JmeException e) {
            logger.log(Level.SEVERE, "Could not create displaySystem", e);
            System.exit(1);
        }
        ColorRGBA blueColor = new ColorRGBA();
        blueColor.r = 0;
        blueColor.g = 0;
        display.getRenderer().setBackgroundColor(blueColor);
        cam.setFrustum(1.0f, 1000.0f, -0.55f, 0.55f, 0.4125f, -0.4125f);
        Vector3f loc = new Vector3f(4.0f, 0.0f, 0.0f);
        Vector3f left = new Vector3f(0.0f, -1.0f, 0.0f);
        Vector3f up = new Vector3f(0.0f, 0.0f, 1.0f);
        Vector3f dir = new Vector3f(-1.0f, 0f, 0.0f);
        cam.setFrame(loc, left, up, dir);

        display.getRenderer().setCamera(cam);

    }

    /**
     * initializes the scene
     * @see com.jme.app.BaseGame#initGame()
     */
    protected void initGame() {

        mouse = new RelativeMouse("Mouse Input");
        mouse.registerWithInputHandler( mouseInputHandler );

        text = Text.createDefaultTextLabel("Text Input", "Testing Mouse");
        text.setLocalTranslation(new Vector3f(1, 60, 0));
        BlendState as1 = display.getRenderer().createBlendState();
        as1.setBlendEnabled(true);
        as1.setSourceFunction(BlendState.SourceFunction.Zero);
        as1.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceColor);
        as1.setTestEnabled(true);
        as1.setTestFunction(BlendState.TestFunction.GreaterThan);
        mouse.setRenderState(as1);
        scene = new Node("Scene graph node");
        scene.attachChild(text);
        scene.attachChild(mouse);
        cam.update();

        scene.updateGeometricState(0.0f, true);
        scene.updateRenderState();
    }

    /**
     * not used.
     * @see com.jme.app.BaseGame#reinit()
     */
    protected void reinit() {

    }

    /**
     * not used.
     * @see com.jme.app.BaseGame#cleanup()
     */
    protected void cleanup() {

    }

}
