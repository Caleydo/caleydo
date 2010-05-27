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

package jmetest.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.app.BaseGame;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.input.FirstPersonHandler;
import com.jme.input.InputHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.light.DirectionalLight;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Pyramid;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;
import com.jme.util.Timer;

/**
 * <code>TestLightState</code>
 * @author Mark Powell
 * @version $Id: TestTimer.java 4130 2009-03-19 20:04:51Z blaine.dev $
 */
public class TestTimer extends BaseGame {
    private static final Logger logger = Logger.getLogger(TestTimer.class
            .getName());
    
    private TriMesh t;
    private Camera cam;
    private Text text;
    private Node root;
    private Node scene;
    private InputHandler input;
    private Timer timer;
    private Quaternion rotQuat;
    private float angle = 0;
    private Vector3f axis;

    /**
     * Entry point for the test,
     * @param args
     */
    public static void main(String[] args) {
        TestTimer app = new TestTimer();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();

    }

    public void addSpatial(Spatial spatial) {
        scene.attachChild(spatial);
        scene.updateGeometricState(0.0f, true);
    }

    /**
     * Not used in this test.
     * @see com.jme.app.BaseGame#update(float)
     */
    protected void update(float interpolation) {
        if(timer.getTimePerFrame() < 1) {
            angle = angle + (timer.getTimePerFrame() * 1);
            if(angle > 360) {
                angle = 0;
            }
        }

        rotQuat.fromAngleAxis(angle, axis);
        timer.update();
        input.update(timer.getTimePerFrame());
        text.print("Frame Rate: " + timer.getFrameRate());

        t.setLocalRotation(rotQuat);
        scene.updateGeometricState(0.0f, true);

        
        if (KeyBindingManager.getKeyBindingManager().isValidCommand("exit", false)) {
            finish();
        }

    }

    /**
     * clears the buffers and then draws the TriMesh.
     * @see com.jme.app.BaseGame#render(float)
     */
    protected void render(float interpolation) {
        display.getRenderer().clearBuffers();

        display.getRenderer().draw(root);

    }

    /**
     * creates the displays and sets up the viewport.
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
            cam =
                display.getRenderer().createCamera(
                    settings.getWidth(),
                    settings.getHeight());

        } catch (JmeException e) {
            logger.log(Level.SEVERE, "Could not create displaySystem", e);
            System.exit(1);
        }
        ColorRGBA blackColor = new ColorRGBA(0, 0, 0, 1);
        display.getRenderer().setBackgroundColor(blackColor);
        cam.setFrustum(1.0f, 1000.0f, -0.55f, 0.55f, 0.4125f, -0.4125f);
        Vector3f loc = new Vector3f(0.0f, 0.0f, 75.0f);
        Vector3f left = new Vector3f(-1.0f, 0.0f, 0.0f);
        Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
        Vector3f dir = new Vector3f(0.0f, 0f, -1.0f);
        cam.setFrame(loc, left, up, dir);
        display.getRenderer().setCamera(cam);

        input = new FirstPersonHandler(cam, 15, 1);
        timer = Timer.getTimer();
        logger.info("Timer resolution:" + timer.getResolution());

        rotQuat = new Quaternion();
        axis = new Vector3f(1,1,1);
        KeyBindingManager.getKeyBindingManager().set(
                "exit",
                KeyInput.KEY_ESCAPE);

    }

    /**
     * builds the trimesh.
     * @see com.jme.app.BaseGame#initGame()
     */
    protected void initGame() {
        text = Text.createDefaultTextLabel("Text Label", "Timer");
        text.setLocalTranslation(new Vector3f(1,60,0));

        scene = new Node("Scene Node");
        root = new Node("Root Scene Node");
        root.attachChild(text);

        t = new Pyramid("Pyramid", 10,10);
        t.setModelBound(new BoundingSphere());
        t.updateModelBound();

        t.setLocalTranslation(new Vector3f(0,0,0));

        scene.attachChild(t);
        root.attachChild(scene);

        ZBufferState buf = display.getRenderer().createZBufferState();
        buf.setEnabled(true);
        buf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);

        DirectionalLight am = new DirectionalLight();
        am.setDiffuse(new ColorRGBA(0.0f, 1.0f, 0.0f, 1.0f));
        am.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        am.setDirection(new Vector3f(0, 0, -1));

        LightState state = display.getRenderer().createLightState();
        state.attach(am);
        am.setEnabled(true);
        scene.setRenderState(state);
        scene.setRenderState(buf);
        cam.update();

        TextureState ts = display.getRenderer().createTextureState();
                ts.setEnabled(true);
                ts.setTexture(
                    TextureManager.loadTexture(
                        TestTimer.class.getClassLoader().getResource("jmetest/data/images/Monkey.jpg"),
                        Texture.MinificationFilter.BilinearNearestMipMap,
                        Texture.MagnificationFilter.Bilinear));

        scene.setRenderState(ts);

        root.attachChild(text);


        root.updateGeometricState(0.0f, true);
        root.updateRenderState();

    }
    /**
     * not used.
     * @see com.jme.app.BaseGame#reinit()
     */
    protected void reinit() {

    }

    /**
     * Not used.
     * @see com.jme.app.BaseGame#cleanup()
     */
    protected void cleanup() {

    }

}
