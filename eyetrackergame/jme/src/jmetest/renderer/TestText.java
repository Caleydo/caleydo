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

package jmetest.renderer;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.app.BaseGame;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.Text;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;

/**
 * <code>TestText</code> draws text using the scenegraph.
 * @author Mark Powell
 * @version $Id: TestText.java 4807 2010-02-05 13:42:46Z noel.lynch@gmail.com $
 */
public class TestText extends BaseGame {
    private static final Logger logger = Logger.getLogger(TestText.class
            .getName());

    private Text text;
    private Camera cam;
    protected Node scene;

    public static void main(String[] args) {
        TestText app = new TestText();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    /**
     * Not used.
     * @see com.jme.app.BaseGame#update(float)
     */
    protected void update(float interpolation) {}

    /**
     * draws the scene graph
     * @see com.jme.app.BaseGame#render(float)
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
            display.createWindow(settings.getWidth(), settings.getHeight(),
                    settings.getDepth(), settings.getFrequency(), settings.isFullscreen());
            display.setTitle("Test Text");
            cam = display.getRenderer().createCamera(settings.getWidth(), settings.getHeight());
        } catch (JmeException e) {
            logger.log(Level.SEVERE, "Could not create displaySystem", e);
            System.exit(1);
        }

        ColorRGBA blueColor = new ColorRGBA(0f, 0f, 255f, 1f);
        display.getRenderer().setBackgroundColor(blueColor);

        cam.setFrustum(1.0f,1000.0f,-0.55f,0.55f,0.4125f,-0.4125f);
        Vector3f loc = new Vector3f(4.0f,0.0f,0.0f);
        Vector3f left = new Vector3f(0.0f,-1.0f,0.0f);
        Vector3f up = new Vector3f(0.0f,0.0f,1.0f);
        Vector3f dir = new Vector3f(-1.0f,0f,0.0f);
        cam.setFrame(loc,left,up,dir);
        display.getRenderer().setCamera(cam);
    }

    /**
     * initializes the scene
     * @see com.jme.app.BaseGame#initGame()
     */
    protected void initGame() {
        text = Text.createDefaultTextLabel("text", "Testing Text! Look, symbols: <>?!^&*_");
        text.setLocalTranslation(new Vector3f(1,60,0));

        scene = new Node("3D Scene Node");
        scene.attachChild(text);
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
