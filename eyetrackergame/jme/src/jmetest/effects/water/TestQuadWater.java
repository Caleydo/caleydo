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

package jmetest.effects.water;

import java.nio.FloatBuffer;

import com.jme.app.SimplePassGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.math.Plane;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.renderer.pass.RenderPass;
import com.jme.scene.Node;
import com.jme.scene.Skybox;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.Spatial.TextureCombineMode;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Quad;
import com.jme.scene.shape.Torus;
import com.jme.scene.state.CullState;
import com.jme.scene.state.FogState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.util.TextureManager;
import com.jmex.effects.water.WaterRenderPass;

/**
 * <code>TestQuadWater</code> Test for the water effect pass.
 * 
 * @author Rikard Herlitz (MrCoder)
 */
public class TestQuadWater extends SimplePassGame {
    private WaterRenderPass waterEffectRenderPass;
    private Skybox skybox;
    private Quad waterQuad;
    private float farPlane = 10000.0f;
    private float textureScale = 0.02f;

    // debug stuff
    private Node debugQuadsNode;

    public static void main(String[] args) {
        TestQuadWater app = new TestQuadWater();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    protected void cleanup() {
        super.cleanup();
        waterEffectRenderPass.cleanup();
    }

    protected void simpleUpdate() {
        if (KeyBindingManager.getKeyBindingManager().isValidCommand("g", false)) {
            waterEffectRenderPass.reloadShader();
        }
        if (KeyBindingManager.getKeyBindingManager().isValidCommand("e", false)) {
            switchShowDebug();
        }

        if (KeyBindingManager.getKeyBindingManager().isValidCommand("1", false)) {
            waterEffectRenderPass.resetParameters();
            textureScale = 0.02f;
        }
        if (KeyBindingManager.getKeyBindingManager().isValidCommand("2", false)) {
            waterEffectRenderPass.setWaterColorStart(ColorRGBA.black.clone());
            waterEffectRenderPass.setWaterColorEnd(ColorRGBA.black.clone());
            textureScale = 0.01f;
            waterEffectRenderPass.setSpeedReflection(0.05f);
            waterEffectRenderPass.setSpeedRefraction(0.01f);
        }

        float tpf = timer.getTimePerFrame();
        if (KeyBindingManager.getKeyBindingManager().isValidCommand("lower",
                true)) {
            waterEffectRenderPass.setWaterHeight(waterEffectRenderPass
                    .getWaterHeight()
                    - tpf * 10.0f);
        }
        if (KeyBindingManager.getKeyBindingManager().isValidCommand("higher",
                true)) {
            waterEffectRenderPass.setWaterHeight(waterEffectRenderPass
                    .getWaterHeight()
                    + tpf * 10.0f);
        }

        skybox.getLocalTranslation().set(cam.getLocation());
        skybox.updateGeometricState(0.0f, true);

        Vector3f transVec = new Vector3f(cam.getLocation().x,
                waterEffectRenderPass.getWaterHeight(), cam.getLocation().z);

        setTextureCoords(0, transVec.x, -transVec.z, textureScale);

        // vertex coords
        setVertexCoords(transVec.x, transVec.y, transVec.z);
    }

    protected void simpleInitGame() {
        display.setTitle("Water Test");
        cam.setFrustumPerspective(45.0f, (float) display.getWidth()
                / (float) display.getHeight(), 1f, farPlane);
        cam.setLocation(new Vector3f(100, 50, 100));
        // y-up
        cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);
        // z-up
        // cam.lookAt( new Vector3f( 0, 0, 0 ), Vector3f.UNIT_Z );
        // ((FirstPersonHandler)input).getMouseLookHandler().setLockAxis(
        // Vector3f.UNIT_Z );
        cam.update();

        setupKeyBindings();

        setupFog();

        Node reflectedNode = new Node("reflectNode");

        buildSkyBox();
        reflectedNode.attachChild(skybox);
        reflectedNode.attachChild(createObjects());

        rootNode.attachChild(reflectedNode);

        waterEffectRenderPass = new WaterRenderPass(cam, 4, false, true);
        // setting to default value just to show
        waterEffectRenderPass.setWaterPlane(new Plane(new Vector3f(0.0f, 1.0f,
                0.0f), 0.0f));

        waterQuad = new Quad("waterQuad", 1, 1);
        FloatBuffer normBuf = waterQuad.getNormalBuffer();
        normBuf.clear();
        normBuf.put(0).put(1).put(0);
        normBuf.put(0).put(1).put(0);
        normBuf.put(0).put(1).put(0);
        normBuf.put(0).put(1).put(0);

        waterEffectRenderPass.setWaterEffectOnSpatial(waterQuad);
        rootNode.attachChild(waterQuad);

        createDebugQuads();
        rootNode.attachChild(debugQuadsNode);

        waterEffectRenderPass.setReflectedScene(reflectedNode);
        waterEffectRenderPass.setSkybox(skybox);
        pManager.add(waterEffectRenderPass);

        RenderPass rootPass = new RenderPass();
        rootPass.add(rootNode);
        pManager.add(rootPass);

        RenderPass statPass = new RenderPass();
        statPass.add(statNode);
        pManager.add(statPass);

        rootNode.setCullHint(Spatial.CullHint.Never);
        rootNode.setRenderQueueMode(Renderer.QUEUE_OPAQUE);
    }

    private void setVertexCoords(float x, float y, float z) {
        FloatBuffer vertBuf = waterQuad.getVertexBuffer();
        vertBuf.clear();

        vertBuf.put(x - farPlane).put(y).put(z - farPlane);
        vertBuf.put(x - farPlane).put(y).put(z + farPlane);
        vertBuf.put(x + farPlane).put(y).put(z + farPlane);
        vertBuf.put(x + farPlane).put(y).put(z - farPlane);
    }

    private void setTextureCoords(int buffer, float x, float y,
            float textureScale) {
        x *= textureScale * 0.5f;
        y *= textureScale * 0.5f;
        textureScale = farPlane * textureScale;
        FloatBuffer texBuf;
        texBuf = waterQuad.getTextureCoords(buffer).coords;
        texBuf.clear();
        texBuf.put(x).put(textureScale + y);
        texBuf.put(x).put(y);
        texBuf.put(textureScale + x).put(y);
        texBuf.put(textureScale + x).put(textureScale + y);
    }

    private void setupFog() {
        FogState fogState = display.getRenderer().createFogState();
        fogState.setDensity(1.0f);
        fogState.setEnabled(true);
        fogState.setColor(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
        fogState.setEnd(farPlane);
        fogState.setStart(farPlane / 10.0f);
        fogState.setDensityFunction(FogState.DensityFunction.Linear);
        fogState.setQuality(FogState.Quality.PerVertex);
        rootNode.setRenderState(fogState);
    }

    private void buildSkyBox() {
        skybox = new Skybox("skybox", 10, 10, 10);

        String dir = "jmetest/data/skybox1/";
        Texture north = TextureManager.loadTexture(TestQuadWater.class
                .getClassLoader().getResource(dir + "1.jpg"),
                Texture.MinificationFilter.BilinearNearestMipMap,
                Texture.MagnificationFilter.Bilinear);
        Texture south = TextureManager.loadTexture(TestQuadWater.class
                .getClassLoader().getResource(dir + "3.jpg"),
                Texture.MinificationFilter.BilinearNearestMipMap,
                Texture.MagnificationFilter.Bilinear);
        Texture east = TextureManager.loadTexture(TestQuadWater.class
                .getClassLoader().getResource(dir + "2.jpg"),
                Texture.MinificationFilter.BilinearNearestMipMap,
                Texture.MagnificationFilter.Bilinear);
        Texture west = TextureManager.loadTexture(TestQuadWater.class
                .getClassLoader().getResource(dir + "4.jpg"),
                Texture.MinificationFilter.BilinearNearestMipMap,
                Texture.MagnificationFilter.Bilinear);
        Texture up = TextureManager.loadTexture(TestQuadWater.class
                .getClassLoader().getResource(dir + "6.jpg"),
                Texture.MinificationFilter.BilinearNearestMipMap,
                Texture.MagnificationFilter.Bilinear);
        Texture down = TextureManager.loadTexture(TestQuadWater.class
                .getClassLoader().getResource(dir + "5.jpg"),
                Texture.MinificationFilter.BilinearNearestMipMap,
                Texture.MagnificationFilter.Bilinear);

        skybox.setTexture(Skybox.Face.North, north);
        skybox.setTexture(Skybox.Face.West, west);
        skybox.setTexture(Skybox.Face.South, south);
        skybox.setTexture(Skybox.Face.East, east);
        skybox.setTexture(Skybox.Face.Up, up);
        skybox.setTexture(Skybox.Face.Down, down);
        skybox.preloadTextures();

        CullState cullState = display.getRenderer().createCullState();
        cullState.setCullFace(CullState.Face.None);
        cullState.setEnabled(true);
        skybox.setRenderState(cullState);

        ZBufferState zState = display.getRenderer().createZBufferState();
        zState.setEnabled(false);
        skybox.setRenderState(zState);

        FogState fs = display.getRenderer().createFogState();
        fs.setEnabled(false);
        skybox.setRenderState(fs);

        skybox.setLightCombineMode(Spatial.LightCombineMode.Off);
        skybox.setCullHint(Spatial.CullHint.Never);
        skybox.setTextureCombineMode(TextureCombineMode.Replace);
        skybox.updateRenderState();

        skybox.lockBounds();
        skybox.lockMeshes();
    }

    private Node createObjects() {
        Node objects = new Node("objects");

        Torus torus = new Torus("Torus", 50, 50, 10, 20);
        torus.setLocalTranslation(new Vector3f(50, -5, 20));
        TextureState ts = display.getRenderer().createTextureState();
        Texture t0 = TextureManager.loadTexture(
                TestQuadWater.class.getClassLoader().getResource(
                        "jmetest/data/images/Monkey.jpg"),
                Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear);
        Texture t1 = TextureManager.loadTexture(
                TestQuadWater.class.getClassLoader().getResource(
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
        t0 = TextureManager.loadTexture(TestQuadWater.class.getClassLoader()
                .getResource("jmetest/data/texture/wall.jpg"),
                Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear);
        t0.setWrap(Texture.WrapMode.Repeat);
        ts.setTexture(t0);

        Box box = new Box("box1", new Vector3f(-10, -10, -10), new Vector3f(10,
                10, 10));
        box.setLocalTranslation(new Vector3f(0, -7, 0));
        box.setRenderState(ts);
        objects.attachChild(box);

        box = new Box("box2", new Vector3f(-5, -5, -5), new Vector3f(5, 5, 5));
        box.setLocalTranslation(new Vector3f(15, 10, 0));
        box.setRenderState(ts);
        objects.attachChild(box);

        box = new Box("box3", new Vector3f(-5, -5, -5), new Vector3f(5, 5, 5));
        box.setLocalTranslation(new Vector3f(0, -10, 15));
        box.setRenderState(ts);
        objects.attachChild(box);

        box = new Box("box4", new Vector3f(-5, -5, -5), new Vector3f(5, 5, 5));
        box.setLocalTranslation(new Vector3f(20, 0, 0));
        box.setRenderState(ts);
        objects.attachChild(box);

        ts = display.getRenderer().createTextureState();
        t0 = TextureManager.loadTexture(
                TestSimpleQuadWater.class.getClassLoader().getResource(
                        "jmetest/data/images/Monkey.jpg"),
                Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear);
        t0.setWrap(Texture.WrapMode.Repeat);
        ts.setTexture(t0);

        box = new Box("box5", new Vector3f(-50, -2, -50), new Vector3f(50, 2,
                50));
        box.setLocalTranslation(new Vector3f(0, -15, 0));
        box.setRenderState(ts);
        box.setModelBound(new BoundingBox());
        box.updateModelBound();
        objects.attachChild(box);

        return objects;
    }

    private void setupKeyBindings() {
        KeyBindingManager.getKeyBindingManager().set("g", KeyInput.KEY_G);
        KeyBindingManager.getKeyBindingManager().set("e", KeyInput.KEY_E);

        KeyBindingManager.getKeyBindingManager().set("lower", KeyInput.KEY_H);
        KeyBindingManager.getKeyBindingManager().set("higher", KeyInput.KEY_Y);

        KeyBindingManager.getKeyBindingManager().set("1", KeyInput.KEY_1);
        KeyBindingManager.getKeyBindingManager().set("2", KeyInput.KEY_2);

        Text t = Text.createDefaultTextLabel("Text", "Y/H: raise/lower waterheight");
        t.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        t.setLightCombineMode(Spatial.LightCombineMode.Off);
        t.setLocalTranslation(new Vector3f(0, 20, 1));
        statNode.attachChild(t);

        t = Text.createDefaultTextLabel("Text",
                "E: debug show/hide reflection and refraction textures");
        t.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        t.setLightCombineMode(Spatial.LightCombineMode.Off);
        t.setLocalTranslation(new Vector3f(0, 40, 1));
        statNode.attachChild(t);
    }

    private void switchShowDebug() {
        if (debugQuadsNode.getCullHint() == Spatial.CullHint.Never) {
            debugQuadsNode.setCullHint(Spatial.CullHint.Always);
        } else {
            debugQuadsNode.setCullHint(Spatial.CullHint.Never);
        }
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
        ts.setTexture(waterEffectRenderPass.getTextureReflect());
        debugQuad.setRenderState(ts);
        debugQuad.updateRenderState();
        debugQuad.getLocalTranslation().set(quadWidth * 0.6f,
                quadHeight * 1.0f, 1.0f);
        debugQuadsNode.attachChild(debugQuad);

        if (waterEffectRenderPass.getTextureRefract() != null) {
            debugQuad = new Quad("refractionQuad", quadWidth, quadHeight);
            debugQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);
            debugQuad.setCullHint(Spatial.CullHint.Never);
            debugQuad.setLightCombineMode(Spatial.LightCombineMode.Off);
            ts = display.getRenderer().createTextureState();
            ts.setTexture(waterEffectRenderPass.getTextureRefract());
            debugQuad.setRenderState(ts);
            debugQuad.updateRenderState();
            debugQuad.getLocalTranslation().set(quadWidth * 0.6f,
                    quadHeight * 2.1f, 1.0f);
            debugQuadsNode.attachChild(debugQuad);
        }

        if (waterEffectRenderPass.getTextureDepth() != null) {
            debugQuad = new Quad("refractionQuad", quadWidth, quadHeight);
            debugQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);
            debugQuad.setCullHint(Spatial.CullHint.Never);
            debugQuad.setLightCombineMode(Spatial.LightCombineMode.Off);
            ts = display.getRenderer().createTextureState();
            ts.setTexture(waterEffectRenderPass.getTextureDepth());
            debugQuad.setRenderState(ts);
            debugQuad.updateRenderState();
            debugQuad.getLocalTranslation().set(quadWidth * 0.6f,
                    quadHeight * 3.2f, 1.0f);
            debugQuadsNode.attachChild(debugQuad);
        }
    }
}