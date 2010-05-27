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
package jmetest.scene;

import javax.swing.ImageIcon;

import jmetest.terrain.TestTerrain;

import com.jme.app.SimplePassGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.pass.RenderPass;
import com.jme.scene.PassNode;
import com.jme.scene.PassNodeState;
import com.jme.scene.shape.Box;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.FogState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.WireframeState;
import com.jme.util.TextureManager;
import com.jmex.terrain.TerrainPage;
import com.jmex.terrain.util.FaultFractalHeightMap;
import com.jmex.terrain.util.ProceduralTextureGenerator;

/** TestPassNode Creator: rikard.herlitz, 2007-maj-10 */
public class TestPassNode extends SimplePassGame {
    public static void main(String[] args) {
        TestPassNode app = new TestPassNode();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    protected void simpleInitGame() {
        display.setTitle("Test Pass Node");

        setupEnvironment();

        //create geometry
        Box floor = new Box("Floor", new Vector3f(), 10, 10, 10);
        floor.getLocalTranslation().set(0, 0, 0);
        floor.setModelBound(new BoundingBox());
        floor.updateModelBound();
        floor.copyTextureCoordinates(0, 1, 1.0f);
        floor.copyTextureCoordinates(0, 2, 1.0f);

        Box box1 = new Box("box1", new Vector3f(), 10, 10, 10);
        box1.getLocalTranslation().set(0, 0, 25);
        box1.setModelBound(new BoundingBox());
        box1.updateModelBound();

        Box box2 = new Box("box2", new Vector3f(), 10, 10, 10);
        box2.getLocalTranslation().set(25, 0, 25);
        box2.setModelBound(new BoundingBox());
        box2.updateModelBound();
        box2.copyTextureCoordinates(0, 1, 1.0f);
        box2.copyTextureCoordinates(0, 2, 1.0f);

        FaultFractalHeightMap heightMap =
                new FaultFractalHeightMap(65, 32, 0, 255, 0.75f);
        Vector3f terrainScale = new Vector3f(8, 0.4f, 8);
        heightMap.setHeightScale(0.001f);
        TerrainPage page = new TerrainPage("Terrain", 33, heightMap.getSize(),
                terrainScale, heightMap.getHeightMap());
        page.getLocalTranslation().set(0, -100, 0);

        page.setDetailTexture(1, 16);
        page.setDetailTexture(2, 2);

        ProceduralTextureGenerator pt =
                new ProceduralTextureGenerator(heightMap);
        pt.addTexture(new ImageIcon(
                TestTerrain.class.getClassLoader().getResource(
                        "jmetest/data/texture/grassb.png")), -128, 0, 128);
        pt.addTexture(new ImageIcon(
                TestTerrain.class.getClassLoader().getResource(
                        "jmetest/data/texture/dirt.jpg")), 0, 128, 255);
        pt.addTexture(new ImageIcon(
                TestTerrain.class.getClassLoader().getResource(
                        "jmetest/data/texture/highest.jpg")), 128, 255, 384);

        pt.createTexture(512);

        //create some interesting texturestates for splatting
        TextureState ts2 = display.getRenderer().createTextureState();
        ts2.setEnabled(true);
        Texture t1 = TextureManager
                .loadTexture(pt.getImageIcon().getImage(),
                        Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear, true);
        ts2.setTexture(t1, 0);

        Texture t2 =
                TextureManager.loadTexture(TestTerrain.class.getClassLoader().
                        getResource("jmetest/data/texture/Detail.jpg"),
                        Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear);
        ts2.setTexture(t2, 1);
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

        TextureState ts1 = createSplatTextureState(
                "jmetest/data/texture/clouds.png", null);

        addAlphaSplat(ts2, "jmetest/data/images/checkdown.png");

        TextureState ts3 = createSplatTextureState(
                "jmetest/data/texture/wall.jpg",
                "jmetest/data/cursor/cursor1.png");


        BlendState as = display.getRenderer().createBlendState();
        as.setBlendEnabled(true);
        as.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
        as.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
        as.setTestEnabled(true);
        as.setTestFunction(BlendState.TestFunction.GreaterThan);
        as.setEnabled(true);

        ////////////////////// PASS STUFF START
        //try out a passnode to use for splatting
        PassNode splattingPassNode = new PassNode("SplatPassNode");
        splattingPassNode.attachChild(floor);
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

        //try out a passnode that just overlays a wireframe
        WireframeState ws = display.getRenderer().createWireframeState();

        PassNode wireAddPassNode = new PassNode("WirePassNode");
        wireAddPassNode.attachChild(box2);

        PassNodeState baseNodeState = new PassNodeState();
        baseNodeState.setPassState(ts1);
        wireAddPassNode.addPass(baseNodeState);

        PassNodeState wireNodeState = new PassNodeState();
        wireNodeState.setPassState(ws);
        wireAddPassNode.addPass(wireNodeState);
        ////////////////////// PASS STUFF END       

        //attach stuff to rootnode
        rootNode.attachChild(splattingPassNode);
        rootNode.attachChild(wireAddPassNode);
        rootNode.attachChild(box1);

        rootNode.lockBounds();
        rootNode.lockTransforms();
        rootNode.lockShadows();

        RenderPass rootPass = new RenderPass();
        rootPass.add(rootNode);
        pManager.add(rootPass);

        RenderPass statPass = new RenderPass();
        statPass.add(statNode);
        pManager.add(statPass);
    }

    private void setupEnvironment() {
        cam.setFrustumPerspective(45.0f,
                (float) display.getWidth() / (float) display.getHeight(), 1f,
                1000f);
        cam.setLocation(new Vector3f(50, 50, 50));
        cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);

        CullState cs = display.getRenderer().createCullState();
        cs.setCullFace(CullState.Face.Back);
        rootNode.setRenderState(cs);

        FogState fs = display.getRenderer().createFogState();
        fs.setDensity(1.0f);
        fs.setEnabled(true);
        fs.setColor(new ColorRGBA(0.0f, 0.0f, 0.0f, 1.0f));
        fs.setEnd(1000);
        fs.setStart(100);
        fs.setDensityFunction(FogState.DensityFunction.Linear);
        fs.setQuality(FogState.Quality.PerVertex);
        rootNode.setRenderState(fs);
    }

    private void addAlphaSplat(TextureState ts, String alpha) {
        Texture t1 = TextureManager.loadTexture(
                TestPassNode.class.getClassLoader().getResource(alpha),
                Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear);
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

        Texture t0 = TextureManager.loadTexture(
                TestPassNode.class.getClassLoader().getResource(texture),
                Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear);
        t0.setWrap(Texture.WrapMode.Repeat);
        t0.setApply(Texture.ApplyMode.Modulate);
        ts.setTexture(t0, 0);

        if (alpha != null) {
            addAlphaSplat(ts, alpha);
        }

        return ts;
    }
}
