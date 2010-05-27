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

package jmetest.effects;

import jmetest.renderer.TestBoxColor;
import jmetest.renderer.TestEnvMap;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.Spatial.CullHint;
import com.jme.scene.Spatial.LightCombineMode;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.util.TextureManager;
import com.jme.util.Timer;
import com.jmex.effects.TrailMesh;

/**
 * <code>TestTrailMesh</code>
 * 
 * @author Rikard Herlitz (MrCoder)
 */
public class TestTrailMesh extends SimpleGame {
    private Sphere sphere;
    private TrailMesh trailMesh;

    private Vector3f tangent = new Vector3f();

    private boolean updateTrail = true;
    private boolean variableWidth = false;

    public static void main(String[] args) {
        TestTrailMesh app = new TestTrailMesh();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    protected void simpleUpdate() {
        if (KeyBindingManager.getKeyBindingManager().isValidCommand("1", false)) {
            trailMesh.setFacingMode(TrailMesh.FacingMode.Tangent);
        }
        if (KeyBindingManager.getKeyBindingManager().isValidCommand("2", false)) {
            trailMesh.setFacingMode(TrailMesh.FacingMode.Billboard);
        }

        if (KeyBindingManager.getKeyBindingManager().isValidCommand("3", false)) {
            trailMesh.setUpdateMode(TrailMesh.UpdateMode.Step);
        }
        if (KeyBindingManager.getKeyBindingManager().isValidCommand("4", false)) {
            trailMesh.setUpdateMode(TrailMesh.UpdateMode.Interpolate);
        }

        if (KeyBindingManager.getKeyBindingManager().isValidCommand("5", false)) {
            trailMesh.setUpdateSpeed(trailMesh.getUpdateSpeed() * 2.0f);
        }
        if (KeyBindingManager.getKeyBindingManager().isValidCommand("6", false)) {
            trailMesh.setUpdateSpeed(trailMesh.getUpdateSpeed() * 0.5f);
        }

        if (KeyBindingManager.getKeyBindingManager().isValidCommand("freeze",
                false)) {
            updateTrail = !updateTrail;
        }

        if (KeyBindingManager.getKeyBindingManager().isValidCommand("width",
                false)) {
            variableWidth = !variableWidth;
        }

        if (KeyBindingManager.getKeyBindingManager().isValidCommand("reset",
                false)) {
            trailMesh.resetPosition(sphere.getWorldTranslation());
        }

        // Update the trail front position
        if (updateTrail) {
            // Do some crappy moving around
            float speed = timer.getTimeInSeconds() * 4.0f;
            float xPos = FastMath.sin(speed)
                    * (FastMath.sin(speed * 0.7f) * 80.0f + 0.0f);
            float yPos = FastMath.sin(speed * 1.5f) * 20.0f + 20.0f;
            float zPos = FastMath.cos(speed * 1.2f)
                    * (FastMath.sin(speed) * 80.0f + 0.0f);

            sphere.getLocalTranslation().set(xPos, yPos, zPos);
            sphere.updateGeometricState(0.0f, true);

            // Create a spin for tangent mode
            tangent.set(xPos, yPos, zPos);
            tangent.normalizeLocal();

            // Setup width
            float width = 7.0f;
            if (variableWidth) {
                width = FastMath.sin(speed * 3.7f) * 10.0f + 15.0f;
            }

            // If you use the Tangent mode you have to send a tangent vector as
            // well (spin), otherwise you can just drop that variable like this:
            // trailMesh.setTrailFront(sphere.getWorldTranslation(), width,
            // Timer
            // .getTimer().getTimePerFrame());

            trailMesh.setTrailFront(sphere.getWorldTranslation(), tangent,
                    width, Timer.getTimer().getTimePerFrame());
        }

        // Update the mesh
        trailMesh.update(cam.getLocation());
    }

    protected void simpleInitGame() {
        display.setTitle("TestTrailMesh");

        cam.getLocation().set(new Vector3f(140, 140, 140));
        cam.lookAt(new Vector3f(), Vector3f.UNIT_Y);

        // Create the trail
        trailMesh = new TrailMesh("TrailMesh", 100);
        trailMesh.setUpdateSpeed(60.0f);
        trailMesh.setFacingMode(TrailMesh.FacingMode.Billboard);
        trailMesh.setUpdateMode(TrailMesh.UpdateMode.Step);

        // Try out some additive blending etc
        trailMesh.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
        trailMesh.setCullHint(CullHint.Never);

        TextureState ts = display.getRenderer().createTextureState();
        ts.setEnabled(true);
        Texture t1 = TextureManager.loadTexture(
                TestBoxColor.class.getClassLoader().getResource(
                        "jmetest/data/texture/trail.png"),
                Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear);
        ts.setTexture(t1);
        trailMesh.setRenderState(ts);

        BlendState bs = display.getRenderer().createBlendState();
        bs.setBlendEnabled(true);
        bs.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
        bs.setDestinationFunction(BlendState.DestinationFunction.One);
        bs.setTestEnabled(true);
        trailMesh.setRenderState(bs);

        ZBufferState zs = display.getRenderer().createZBufferState();
        zs.setWritable(false);
        trailMesh.setRenderState(zs);

        CullState cs = display.getRenderer().createCullState();
        cs.setCullFace(CullState.Face.None);
        cs.setEnabled(true);
        trailMesh.setRenderState(cs);

        rootNode.attachChild(trailMesh);

        // Create a background floor
        Box floor = new Box("Floor", new Vector3f(), 200, 1, 200);
        floor.setModelBound(new BoundingBox());
        floor.updateModelBound();
        floor.getLocalTranslation().y = -20;
        ts = display.getRenderer().createTextureState();
        Texture t0 = TextureManager.loadTexture(TestEnvMap.class
                .getClassLoader().getResource("jmetest/data/texture/top.jpg"),
                Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear);
        t0.setWrap(Texture.WrapMode.Repeat);
        ts.setTexture(t0);
        floor.setRenderState(ts);
        floor.scaleTextureCoordinates(0, 5);
        rootNode.attachChild(floor);

        // Create a sphere as trail guide
        sphere = new Sphere("Sphere", 16, 16, 4);
        sphere.setModelBound(new BoundingSphere());
        sphere.updateModelBound();
        ts = display.getRenderer().createTextureState();
        ts.setEnabled(true);
        t1 = TextureManager.loadTexture(TestBoxColor.class.getClassLoader()
                .getResource("jmetest/data/texture/post.jpg"),
                Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear);
        ts.setTexture(t1);
        sphere.setRenderState(ts);
        rootNode.attachChild(sphere);

        // No lighting for clarity
        rootNode.setLightCombineMode(LightCombineMode.Off);
        rootNode.setRenderQueueMode(com.jme.renderer.Renderer.QUEUE_OPAQUE);

        // Setup some keys for playing around with settings
        setupKeyBindings();
    }

    private void setupKeyBindings() {
        KeyBindingManager.getKeyBindingManager().set("freeze", KeyInput.KEY_F);
        KeyBindingManager.getKeyBindingManager().set("width", KeyInput.KEY_G);

        KeyBindingManager.getKeyBindingManager().set("1", KeyInput.KEY_1);
        KeyBindingManager.getKeyBindingManager().set("2", KeyInput.KEY_2);
        KeyBindingManager.getKeyBindingManager().set("3", KeyInput.KEY_3);
        KeyBindingManager.getKeyBindingManager().set("4", KeyInput.KEY_4);
        KeyBindingManager.getKeyBindingManager().set("5", KeyInput.KEY_5);
        KeyBindingManager.getKeyBindingManager().set("6", KeyInput.KEY_6);

        KeyBindingManager.getKeyBindingManager().set("reset", KeyInput.KEY_E);

        Text t = Text.createDefaultTextLabel("Text", "1/2: Tangent/Billboard");
        t.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        t.setLightCombineMode(Spatial.LightCombineMode.Off);
        t.setLocalTranslation(new Vector3f(0, 120, 1));
        statNode.attachChild(t);

        t = Text.createDefaultTextLabel("Text", "3/4: Step/Interpolate");
        t.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        t.setLightCombineMode(Spatial.LightCombineMode.Off);
        t.setLocalTranslation(new Vector3f(0, 100, 1));
        statNode.attachChild(t);

        t = Text
                .createDefaultTextLabel("Text", "5/6: Raise/Lower update speed");
        t.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        t.setLightCombineMode(Spatial.LightCombineMode.Off);
        t.setLocalTranslation(new Vector3f(0, 80, 1));
        statNode.attachChild(t);

        t = Text.createDefaultTextLabel("Text", "F: Freeze");
        t.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        t.setLightCombineMode(Spatial.LightCombineMode.Off);
        t.setLocalTranslation(new Vector3f(0, 60, 1));
        statNode.attachChild(t);

        t = Text.createDefaultTextLabel("Text",
                "G: Enable/Disable variable width");
        t.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        t.setLightCombineMode(Spatial.LightCombineMode.Off);
        t.setLocalTranslation(new Vector3f(0, 40, 1));
        statNode.attachChild(t);

        t = Text.createDefaultTextLabel("Text", "E: Reset position");
        t.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        t.setLightCombineMode(Spatial.LightCombineMode.Off);
        t.setLocalTranslation(new Vector3f(0, 20, 1));
        statNode.attachChild(t);
    }
}
