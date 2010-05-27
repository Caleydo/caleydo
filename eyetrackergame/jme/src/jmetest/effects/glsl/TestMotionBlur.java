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

package jmetest.effects.glsl;

import com.jme.app.SimplePassGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.light.PointLight;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.renderer.pass.RenderPass;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Quad;
import com.jme.scene.shape.Sphere;
import com.jme.scene.shape.Torus;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jmex.effects.glsl.MotionBlurRenderPass;

/**
 * Motion blur effect pass test.
 * 
 * @author Rikard Herlitz (MrCoder)
 */
public class TestMotionBlur extends SimplePassGame {
    private MotionBlurRenderPass motionBlurRenderPass;
    private int screenshotIndex = 0;
    private Node debugQuadsNode;

    private Spatial torus;
    private Spatial sphere;

    public static void main(String[] args) {
        TestMotionBlur app = new TestMotionBlur();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    protected void cleanup() {
        super.cleanup();
        if (motionBlurRenderPass != null)
            motionBlurRenderPass.cleanup();
    }

    protected void simpleInitGame() {
        display.setTitle("MotionBlur Test");

        // Setup camera
        cam.setFrustumPerspective(45.0f, (float) display.getWidth()
                / (float) display.getHeight(), 1, 5000);
        cam.setLocation(new Vector3f(200, 150, 200));
        cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);
        cam.update();

        setupKeyBindings();

        // Setup lights
        PointLight light = new PointLight();
        light.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
        light.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        light.setLocation(new Vector3f(0, 30, 0));
        light.setEnabled(true);
        lightState.attach(light);

        // Add dummy objects to rootNode
        rootNode.attachChild(createObjects());

        // Setup renderpasses
        RenderPass rootPass = new RenderPass();
        rootPass.add(rootNode);
        pManager.add(rootPass);

        motionBlurRenderPass = new MotionBlurRenderPass(cam);

        if (!motionBlurRenderPass.isSupported()) {
            Text t = Text.createDefaultTextLabel("Text", "GLSL Not supported on this computer.");
            t.setRenderQueueMode(Renderer.QUEUE_ORTHO);
            t.setLightCombineMode(Spatial.LightCombineMode.Off);
            t.setLocalTranslation(new Vector3f(0, 20, 0));
            statNode.attachChild(t);
        } else {
            motionBlurRenderPass.add(rootNode);
            motionBlurRenderPass.addMotionBlurSpatial(sphere);
            motionBlurRenderPass.addMotionBlurSpatial(torus);
            motionBlurRenderPass.setUseCurrentScene(true);
            pManager.add(motionBlurRenderPass);
        }

        createDebugQuads();
        statNode.attachChild(debugQuadsNode);

        RenderPass statPass = new RenderPass();
        statPass.add(statNode);
        pManager.add(statPass);
    }

    protected void simpleUpdate() {
        if (!motionBlurRenderPass.isFreeze()) {
            float time = (FastMath.sin(timer.getTimeInSeconds() * 0.5f) + 1.0f) * 10.0f;
            torus.getLocalRotation().fromAngles(time * 2f, time * 2.0f,
                    time * 1.0f);
            torus.getLocalTranslation().x = FastMath.sin(time * 1.0f) * 100.0f;
            torus.getLocalTranslation().z = FastMath.cos(time * 1.0f) * 100.0f;

            float scale = (FastMath.sin(timer.getTimeInSeconds() * 10.0f) + 2.0f) * 0.5f;
            sphere.getLocalScale().set(scale, scale, scale);
            sphere.getLocalTranslation().z = FastMath.sin(time * 1.0f) * 200.0f;
        }

        if (KeyBindingManager.getKeyBindingManager().isValidCommand("1", false)) {
            motionBlurRenderPass.setEnabled(!motionBlurRenderPass.isEnabled());
        }

        if (KeyBindingManager.getKeyBindingManager().isValidCommand("2", false)) {
            motionBlurRenderPass.setBlurStrength(motionBlurRenderPass
                    .getBlurStrength() * 0.5f);
        }
        if (KeyBindingManager.getKeyBindingManager().isValidCommand("3", false)) {
            motionBlurRenderPass.setBlurStrength(motionBlurRenderPass
                    .getBlurStrength() * 2.0f);
        }

        if (KeyBindingManager.getKeyBindingManager().isValidCommand("f", false)) {
            motionBlurRenderPass.setFreeze(!motionBlurRenderPass.isFreeze());
        }

        if (KeyBindingManager.getKeyBindingManager().isValidCommand("g", false)) {
            motionBlurRenderPass.reloadShader();
        }

        if (KeyBindingManager.getKeyBindingManager().isValidCommand("0", false)) {
            motionBlurRenderPass.resetParameters();
            motionBlurRenderPass.setUseCurrentScene(true);
        }

        if (KeyBindingManager.getKeyBindingManager().isValidCommand("shot",
                false)) {
            display.getRenderer().takeScreenShot("shot" + screenshotIndex++);
        }
    }

    private void setupKeyBindings() {
        KeyBindingManager.getKeyBindingManager().set("1", KeyInput.KEY_1);
        KeyBindingManager.getKeyBindingManager().set("2", KeyInput.KEY_2);
        KeyBindingManager.getKeyBindingManager().set("3", KeyInput.KEY_3);
        KeyBindingManager.getKeyBindingManager().set("0", KeyInput.KEY_0);
        KeyBindingManager.getKeyBindingManager().set("g", KeyInput.KEY_G);
        KeyBindingManager.getKeyBindingManager().set("f", KeyInput.KEY_F);
        KeyBindingManager.getKeyBindingManager().set("shot", KeyInput.KEY_F4);

        Text t = Text.createDefaultTextLabel("Text", "1: enable/disable motionblur pass");
        t.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        t.setLightCombineMode(Spatial.LightCombineMode.Off);
        t.setLocalTranslation(new Vector3f(0, 20, 1));
        statNode.attachChild(t);

        t = Text.createDefaultTextLabel("Text", "2/3: decrease/increase blur strength");
        t.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        t.setLightCombineMode(Spatial.LightCombineMode.Off);
        t.setLocalTranslation(new Vector3f(0, 40, 1));
        statNode.attachChild(t);

        t = Text.createDefaultTextLabel("Text", "f: freeze/unfreeze movement");
        t.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        t.setLightCombineMode(Spatial.LightCombineMode.Off);
        t.setLocalTranslation(new Vector3f(0, 60, 1));
        statNode.attachChild(t);
    }

    private Node createObjects() {
        Node objects = new Node("objects");

        torus = new Torus("Torus", 50, 50, 10, 25);
        torus.setLocalTranslation(new Vector3f(50, -5, 20));
        TextureState ts = display.getRenderer().createTextureState();
        Texture t0 = TextureManager.loadTexture(
                TestMotionBlur.class.getClassLoader().getResource(
                        "jmetest/data/images/Monkey.jpg"),
                Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear);
        Texture t1 = TextureManager.loadTexture(
                TestMotionBlur.class.getClassLoader().getResource(
                        "jmetest/data/texture/north.jpg"),
                Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear);
        t1.setEnvironmentalMapMode(Texture.EnvironmentalMapMode.SphereMap);
        ts.setTexture(t0, 0);
        ts.setTexture(t1, 1);
        ts.setEnabled(true);
        torus.setRenderState(ts);
        objects.attachChild(torus);

        ts = display.getRenderer().createTextureState();
        t0 = TextureManager.loadTexture(TestMotionBlur.class.getClassLoader()
                .getResource("jmetest/data/texture/wall.jpg"),
                Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear);
        t0.setWrap(Texture.WrapMode.Repeat);
        ts.setTexture(t0);

        ts = display.getRenderer().createTextureState();
        t0 = TextureManager.loadTexture(TestMotionBlur.class.getClassLoader()
                .getResource("jmetest/data/texture/cloud_land.jpg"),
                Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear);
        t0.setWrap(Texture.WrapMode.Repeat);
        ts.setTexture(t0);

        sphere = new Sphere("sphere", 16, 16, 10);
        sphere.setLocalTranslation(new Vector3f(0, -10, 15));
        sphere.setRenderState(ts);
        objects.attachChild(sphere);

        Box box = new Box("floor", new Vector3f(-1000, -10, -1000),
                new Vector3f(1000, 10, 1000));
        box.setLocalTranslation(new Vector3f(0, -100, 0));
        box.setRenderState(ts);
        box.setModelBound(new BoundingBox());
        box.updateModelBound();
        objects.attachChild(box);

        ts = display.getRenderer().createTextureState();
        t0 = TextureManager.loadTexture(TestMotionBlur.class.getClassLoader()
                .getResource("jmetest/data/texture/wall.jpg"),
                Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear);
        t0.setWrap(Texture.WrapMode.Repeat);
        ts.setTexture(t0);

        box = new Box("box1", new Vector3f(-10, -10, -10), new Vector3f(10, 10,
                10));
        box.setLocalTranslation(new Vector3f(0, -20, 0));
        box.setRenderState(ts);
        objects.attachChild(box);

        box = new Box("box2", new Vector3f(-5, -5, -5), new Vector3f(5, 5, 5));
        box.setLocalTranslation(new Vector3f(15, 30, 0));
        box.setRenderState(ts);
        objects.attachChild(box);

        box = new Box("box3", new Vector3f(-5, -5, -5), new Vector3f(5, 5, 5));
        box.setLocalTranslation(new Vector3f(0, -10, 45));
        box.setRenderState(ts);
        objects.attachChild(box);

        return objects;
    }

    private void createDebugQuads() {
        debugQuadsNode = new Node("quadNode");
        debugQuadsNode.setCullHint(Spatial.CullHint.Never);

        float quadWidth = display.getWidth() / 8;
        float quadHeight = display.getWidth() / 8;
        Quad debugQuad = new Quad("reflectionQuad", quadWidth, quadHeight);
        debugQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        debugQuad.setCullHint(Spatial.CullHint.Never);
        debugQuad.setLightCombineMode(Spatial.LightCombineMode.Off);
        TextureState ts = display.getRenderer().createTextureState();
        ts.setTexture(motionBlurRenderPass.getMainTexture());
        debugQuad.setRenderState(ts);
        debugQuad.updateRenderState();
        debugQuad.getLocalTranslation().set(quadWidth * 0.6f,
                quadHeight * 1.0f, 1.0f);
        debugQuadsNode.attachChild(debugQuad);
    }

}
