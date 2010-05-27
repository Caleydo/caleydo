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

package jmetest.renderer.state;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.app.BaseGame;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.input.FirstPersonHandler;
import com.jme.input.InputHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.state.CullState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;
import com.jme.util.Timer;
/**
 * <code>TestTextureState</code>
 * @author Mark Powell
 * @version
 */
public class TestRenderStateList extends BaseGame {
    private static final Logger logger = Logger
            .getLogger(TestRenderStateList.class.getName());
    
    private TriMesh t, t2, t3;
    private Camera cam;
    private Node scene;
    private InputHandler input;
    private Timer timer;

    /**
     * Entry point for the test,
     * @param args
     */
    public static void main(String[] args) {
        TestRenderStateList app = new TestRenderStateList();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    /**
     * Not used in this test.
     * @see com.jme.app.SimpleGame#update
     */
    protected void update(float interpolation) {
        timer.update();
        
        if (KeyBindingManager.getKeyBindingManager().isValidCommand("exit", false)) {
            finish();
        }

        input.update(timer.getTimePerFrame());
    }

    /**
     * clears the buffers and then draws the TriMesh.
     * @see com.jme.app.SimpleGame#render
     */
    protected void render(float interpolation) {
        display.getRenderer().clearBuffers();

        display.getRenderer().draw(scene);

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
        ColorRGBA blueColor = new ColorRGBA();
        blueColor.r = 0;
        blueColor.g = 0;
        display.getRenderer().setBackgroundColor(blueColor);
        cam.setFrustum(1.0f, 1000.0f, -0.55f, 0.55f, 0.4125f, -0.4125f);
        Vector3f loc = new Vector3f(0.0f, 0.0f, 4.0f);
        Vector3f left = new Vector3f(-1.0f, 0.0f, 0.0f);
        Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
        Vector3f dir = new Vector3f(0.0f, 0f, -1.0f);
        cam.setFrame(loc, left, up, dir);
        display.getRenderer().setCamera(cam);

        input = new FirstPersonHandler(cam, 15, 1 );
        timer = Timer.getTimer();
        KeyBindingManager.getKeyBindingManager().set(
                "exit",
                KeyInput.KEY_ESCAPE);

    }

    /**
     * builds the trimesh.
     * @see com.jme.app.BaseGame#initGame()
     */
    protected void initGame() {
        Vector3f min = new Vector3f(-5,-5,-5);
        Vector3f max = new Vector3f(5,5,5);

        t = new Box("Box 1", min, max);
        t.setModelBound(new BoundingSphere());
        t.setLocalTranslation(new Vector3f(-15,0,-20));
        t.updateModelBound();

        t2 = new Box("Box 2", min, max);
        t2.setModelBound(new BoundingSphere());
        t2.setLocalTranslation(new Vector3f(0,0,-20));

        t2.updateModelBound();

        t3 = new Box("Box 3", min, max);
        t3.setModelBound(new BoundingSphere());
        t3.setLocalTranslation(new Vector3f(15,0,-20));

        t3.updateModelBound();

        cam.update();

        scene = new Node("Scene Node");
        scene.attachChild(t);
        scene.attachChild(t2);
        scene.attachChild(t3);
        scene.setLocalTranslation(new Vector3f(0, 0, 0));

        ZBufferState buf = display.getRenderer().createZBufferState();
        buf.setEnabled(true);
        buf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);

        scene.setRenderState(buf);

        CullState cs = display.getRenderer().createCullState();
        cs.setEnabled(true);
        cs.setCullFace(CullState.Face.Back);
        scene.setRenderState(cs);

        CullState cs2 = display.getRenderer().createCullState();
        cs2.setEnabled(true);
        cs2.setCullFace(CullState.Face.None);
        t3.setRenderState(cs2);

        TextureState ts = display.getRenderer().createTextureState();
        ts.setEnabled(true);
        ts.setTexture(
            TextureManager.loadTexture(
                TestRenderStateList.class.getClassLoader().getResource("jmetest/data/images/Monkey.jpg"),
                Texture.MinificationFilter.BilinearNearestMipMap,
                Texture.MagnificationFilter.Bilinear));
        TextureState ts2 = display.getRenderer().createTextureState();
            ts2.setEnabled(true);
            ts2.setTexture(
                TextureManager.loadTexture(
                    TestRenderStateList.class.getClassLoader().getResource("jmetest/data/texture/dirt.jpg"),
                    Texture.MinificationFilter.BilinearNearestMipMap,
                    Texture.MagnificationFilter.Bilinear));
        t2.setRenderState(ts2);
        scene.setRenderState(ts);

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
     * Not used.
     * @see com.jme.app.BaseGame#cleanup()
     */
    protected void cleanup() {

    }

}
