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

import com.jme.app.SimplePassGame;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.renderer.pass.RenderPass;
import com.jme.scene.PassNode;
import com.jme.scene.PassNodeState;
import com.jme.scene.Spatial;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jmex.terrain.TerrainPage;
import com.jmex.terrain.util.RawHeightMap;

/**
 * TestTerrainSplatting shows multipass texturesplatting(6 passes) through usage
 * of the PassNode
 * 
 * @author Heightmap and textures originally from Jadestone(but heavily
 *         downsampled)
 * @author Rikard Herlitz (MrCoder)
 */
public class TestTerrainSplatting extends SimplePassGame {

    private float globalSplatScale = 90.0f;

    public static void main(String[] args) {
        TestTerrainSplatting app = new TestTerrainSplatting();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    protected void simpleInitGame() {
        display.setTitle("Test Terrainsplatting");

        setupEnvironment();

        rootNode.attachChild(createTerrain());

        RenderPass rootPass = new RenderPass();
        rootPass.add(rootNode);
        pManager.add(rootPass);

        RenderPass statPass = new RenderPass();
        statPass.add(statNode);
        pManager.add(statPass);
    }

    private Spatial createTerrain() {
        RawHeightMap heightMap = new RawHeightMap(TestTerrainSplatting.class
                .getClassLoader().getResource(
                        "jmetest/data/texture/terrain/heights.raw"),
                129, RawHeightMap.FORMAT_16BITLE, false);

        Vector3f terrainScale = new Vector3f(5, 0.003f, 6);
        heightMap.setHeightScale(0.001f);
        TerrainPage page = new TerrainPage("Terrain", 33, heightMap.getSize(),
                terrainScale, heightMap.getHeightMap());
        page.getLocalTranslation().set(0, 10, 0);
        page.setDetailTexture(1, 1);

        // create some interesting texturestates for splatting
        TextureState ts1 = createSplatTextureState(
                "jmetest/data/texture/terrain/baserock.jpg", null);

        TextureState ts2 = createSplatTextureState(
                "jmetest/data/texture/terrain/darkrock.jpg",
                "jmetest/data/texture/terrain/darkrockalpha.png");

        TextureState ts3 = createSplatTextureState(
                "jmetest/data/texture/terrain/deadgrass.jpg",
                "jmetest/data/texture/terrain/deadalpha.png");

        TextureState ts4 = createSplatTextureState(
                "jmetest/data/texture/terrain/nicegrass.jpg",
                "jmetest/data/texture/terrain/grassalpha.png");

        TextureState ts5 = createSplatTextureState(
                "jmetest/data/texture/terrain/road.jpg",
                "jmetest/data/texture/terrain/roadalpha.png");

        TextureState ts6 = createLightmapTextureState("jmetest/data/texture/terrain/lightmap.jpg");

        // alpha used for blending the passnodestates together
        BlendState as = display.getRenderer().createBlendState();
        as.setBlendEnabled(true);
        as.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
        as.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
        as.setTestEnabled(true);
        as.setTestFunction(BlendState.TestFunction.GreaterThan);
        as.setEnabled(true);

        // alpha used for blending the lightmap
        BlendState as2 = display.getRenderer().createBlendState();
        as2.setBlendEnabled(true);
        as2.setSourceFunction(BlendState.SourceFunction.DestinationColor);
        as2.setDestinationFunction(BlendState.DestinationFunction.SourceColor);
        as2.setTestEnabled(true);
        as2.setTestFunction(BlendState.TestFunction.GreaterThan);
        as2.setEnabled(true);

        // //////////////////// PASS STUFF START
        // try out a passnode to use for splatting
        PassNode splattingPassNode = new PassNode("SplatPassNode");
        splattingPassNode.attachChild(page);

        PassNodeState passNodeState = new PassNodeState();
        passNodeState.setPassState(ts1);
        splattingPassNode.addPass(passNodeState);

        passNodeState = new PassNodeState();
        passNodeState.setPassState(ts2);
        passNodeState.setPassState(as);
        splattingPassNode.addPass(passNodeState);

        passNodeState = new PassNodeState();
        passNodeState.setPassState(ts3);
        passNodeState.setPassState(as);
        splattingPassNode.addPass(passNodeState);

        passNodeState = new PassNodeState();
        passNodeState.setPassState(ts4);
        passNodeState.setPassState(as);
        splattingPassNode.addPass(passNodeState);

        passNodeState = new PassNodeState();
        passNodeState.setPassState(ts5);
        passNodeState.setPassState(as);
        splattingPassNode.addPass(passNodeState);

        passNodeState = new PassNodeState();
        passNodeState.setPassState(ts6);
        passNodeState.setPassState(as2);
        splattingPassNode.addPass(passNodeState);
        // //////////////////// PASS STUFF END

        // lock some things to increase the performance
        splattingPassNode.lockBounds();
        splattingPassNode.lockTransforms();
        splattingPassNode.lockShadows();

        return splattingPassNode;
    }

    private void setupEnvironment() {
        cam.setFrustumPerspective(50.0f, (float) display.getWidth()
                / (float) display.getHeight(), 1f, 1000f);
        cam.setLocation(new Vector3f(-270, 180, -270));
        cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);
        cam.update();

        CullState cs = display.getRenderer().createCullState();
        cs.setCullFace(CullState.Face.Back);
        rootNode.setRenderState(cs);

        rootNode.setLightCombineMode(Spatial.LightCombineMode.Off);
    }

    private void addAlphaSplat(TextureState ts, String alpha) {
        Texture t1 = TextureManager.loadTexture(TestTerrainSplatting.class
                .getClassLoader().getResource(alpha), Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear);
        t1.setWrap(Texture.WrapMode.Repeat);
        t1.setApply(Texture.ApplyMode.Combine);
        t1.setCombineFuncRGB(Texture.CombinerFunctionRGB.Replace);
        t1.setCombineSrc0RGB(Texture.CombinerSource.Previous);
        t1.setCombineOp0RGB(Texture.CombinerOperandRGB.SourceColor);
        t1.setCombineFuncAlpha(Texture.CombinerFunctionAlpha.Replace);
        ts.setTexture(t1, ts.getNumberOfSetTextures());
    }

    private TextureState createSplatTextureState(String texture, String alpha) {
        TextureState ts = display.getRenderer().createTextureState();

        Texture t0 = TextureManager.loadTexture(TestTerrainSplatting.class
                .getClassLoader().getResource(texture),
                Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear);
        t0.setWrap(Texture.WrapMode.Repeat);
        t0.setApply(Texture.ApplyMode.Modulate);
        t0.setScale(new Vector3f(globalSplatScale, globalSplatScale, 1.0f));
        ts.setTexture(t0, 0);

        if (alpha != null) {
            addAlphaSplat(ts, alpha);
        }

        return ts;
    }

    private TextureState createLightmapTextureState(String texture) {
        TextureState ts = display.getRenderer().createTextureState();

        Texture t0 = TextureManager.loadTexture(TestTerrainSplatting.class
                .getClassLoader().getResource(texture),
                Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear);
        t0.setWrap(Texture.WrapMode.Repeat);
        ts.setTexture(t0, 0);

        return ts;
    }
}
