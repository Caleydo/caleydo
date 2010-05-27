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

package jmetest.terrain;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.input.NodeHandler;
import com.jme.light.LightNode;
import com.jme.light.PointLight;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.CameraNode;
import com.jme.scene.shape.Box;
import com.jme.scene.state.CullState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jmex.effects.LensFlare;
import com.jmex.effects.LensFlareFactory;
import com.jmex.terrain.TerrainBlock;
import com.jmex.terrain.util.MidPointHeightMap;

/**
 * <code>TestTerrainLighting</code>
 * 
 * @author Mark Powell
 * @version $Id: TestTerrainLighting.java 4130 2009-03-19 20:04:51Z blaine.dev $
 */
public class TestTerrainLighting extends SimpleGame {
    private CameraNode camNode;
    private Vector3f currentPos;
    private Vector3f newPos;
    private LightNode lightNode;
    private LensFlare flare;

    /**
     * Entry point for the test,
     * 
     * @param args
     */
    public static void main(String[] args) {
        TestTerrainLighting app = new TestTerrainLighting();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    protected void simpleUpdate() {
        if ((int) currentPos.x == (int) newPos.x
                && (int) currentPos.z == (int) newPos.z) {
            newPos.x = FastMath.nextRandomFloat() * 128 * 5;
            newPos.z = FastMath.nextRandomFloat() * 128 * 5;
        }

        currentPos.x -= (currentPos.x - newPos.x) / (timer.getFrameRate() / 2);
        currentPos.y = 255;
        currentPos.z -= (currentPos.z - newPos.z) / (timer.getFrameRate() / 2);

        lightNode.setLocalTranslation(currentPos);
    }

    /**
     * builds the trimesh.
     * 
     * @see com.jme.app.SimpleGame#initGame()
     */
    protected void simpleInitGame() {

        rootNode.setRenderQueueMode(Renderer.QUEUE_OPAQUE);
        currentPos = new Vector3f();
        newPos = new Vector3f();

        cam.setFrustum(1.0f, 1000.0f, -0.55f, 0.55f, 0.4125f, -0.4125f);
        cam.update();

        camNode = new CameraNode("Camera Node", cam);
        camNode.setLocalTranslation(new Vector3f(0, 250, -20));
        camNode.updateWorldData(0);

        input = new NodeHandler(camNode, 50f, .5f);

        display.setTitle("Terrain Test");

        PointLight dr = new PointLight();
        dr.setEnabled(true);
        dr.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
        dr.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        dr.setLocation(new Vector3f(0.5f, -0.5f, 0));

        lightState.detachAll();
        lightState.attach(dr);
        lightState.setTwoSidedLighting(true);

        CullState cs = display.getRenderer().createCullState();
        cs.setCullFace(CullState.Face.Back);
        cs.setEnabled(true);

        lightNode = new LightNode("light");
        lightNode.setLight(dr);

        Vector3f min2 = new Vector3f(-0.5f, -0.5f, -0.5f);
        Vector3f max2 = new Vector3f(0.5f, 0.5f, 0.5f);
        Box lightBox = new Box("box", min2, max2);
        lightBox.setModelBound(new BoundingBox());
        lightBox.updateModelBound();
        lightNode.attachChild(lightBox);

        // Setup the lensflare textures.
        TextureState[] tex = new TextureState[4];
        tex[0] = display.getRenderer().createTextureState();
        tex[0].setTexture(TextureManager.loadTexture(LensFlare.class
                .getClassLoader()
                .getResource("jmetest/data/texture/flare1.png"),
                Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear, Image.Format.RGBA8,
                1.0f, true));
        tex[0].setEnabled(true);

        tex[1] = display.getRenderer().createTextureState();
        tex[1].setTexture(TextureManager.loadTexture(LensFlare.class
                .getClassLoader()
                .getResource("jmetest/data/texture/flare2.png"),
                Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear));
        tex[1].setEnabled(true);

        tex[2] = display.getRenderer().createTextureState();
        tex[2].setTexture(TextureManager.loadTexture(LensFlare.class
                .getClassLoader()
                .getResource("jmetest/data/texture/flare3.png"),
                Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear));
        tex[2].setEnabled(true);

        tex[3] = display.getRenderer().createTextureState();
        tex[3].setTexture(TextureManager.loadTexture(LensFlare.class
                .getClassLoader()
                .getResource("jmetest/data/texture/flare4.png"),
                Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear));
        tex[3].setEnabled(true);

        flare = LensFlareFactory.createBasicLensFlare("flare", tex);
        flare.setLocalScale(.5f);
        flare.setRootNode(rootNode);
        lightNode.attachChild(flare);
        MidPointHeightMap heightMap = new MidPointHeightMap(128, 1.5f);
        Vector3f terrainScale = new Vector3f(5, 1, 5);
        TerrainBlock tb = new TerrainBlock("Terrain", heightMap.getSize(),
                terrainScale, heightMap.getHeightMap(), new Vector3f(0, 0, 0));
        tb.setDetailTexture(1, 4);
        tb.setModelBound(new BoundingBox());
        tb.updateModelBound();
        rootNode.attachChild(tb);
        rootNode.setRenderState(cs);

        TextureState ts = display.getRenderer().createTextureState();
        ts.setEnabled(false);
        Texture t1 = TextureManager.loadTexture(TestTerrain.class
                .getClassLoader()
                .getResource("jmetest/data/texture/grassb.png"),
                Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear);
        ts.setTexture(t1, 0);

        Texture t2 = TextureManager.loadTexture(TestTerrain.class
                .getClassLoader()
                .getResource("jmetest/data/texture/Detail.jpg"),
                Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear);
        ts.setTexture(t2, 1);
        t2.setWrap(Texture.WrapMode.Repeat);

        t1.setApply(Texture.ApplyMode.Combine);
        t1.setCombineFuncRGB(Texture.CombinerFunctionRGB.Modulate);
        t1.setCombineSrc0RGB(Texture.CombinerSource.CurrentTexture);
        t1.setCombineOp0RGB(Texture.CombinerOperandRGB.SourceColor);
        t1.setCombineSrc1RGB(Texture.CombinerSource.PrimaryColor);
        t1.setCombineOp1RGB(Texture.CombinerOperandRGB.SourceColor);

        t2.setApply(Texture.ApplyMode.Combine);
        t2.setCombineFuncRGB(Texture.CombinerFunctionRGB.AddSigned);
        t2.setCombineSrc0RGB(Texture.CombinerSource.CurrentTexture);
        t2.setCombineOp0RGB(Texture.CombinerOperandRGB.SourceColor);
        t2.setCombineSrc1RGB(Texture.CombinerSource.Previous);
        t2.setCombineOp1RGB(Texture.CombinerOperandRGB.SourceColor);
        rootNode.setRenderState(ts);

        rootNode.attachChild(lightNode);
        rootNode.attachChild(camNode);
    }
}
