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

import javax.swing.ImageIcon;

import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.input.NodeHandler;
import com.jme.light.DirectionalLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.CameraNode;
import com.jme.scene.state.CullState;
import com.jme.scene.state.FogState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jmex.terrain.TerrainPage;
import com.jmex.terrain.util.FaultFractalHeightMap;
import com.jmex.terrain.util.ProceduralTextureGenerator;

/**
 * <code>TestTerrainPage</code>
 *
 * @author Mark Powell
 * @version $Id: TestTerrainPage.java 4130 2009-03-19 20:04:51Z blaine.dev $
 */
public class TestTerrainPage extends SimpleGame {

  private CameraNode camNode;
private TerrainPage page;

  /**
   * Entry point for the test,
   *
   * @param args
   */
  public static void main(String[] args) {
    TestTerrainPage app = new TestTerrainPage();
    app.setConfigShowMode(ConfigShowMode.AlwaysShow);
    app.start();
  }

  /**
   * builds the trimesh.
   *
   * @see com.jme.app.SimpleGame#initGame()
   */
  protected void simpleInitGame() {
      rootNode.setRenderQueueMode(Renderer.QUEUE_OPAQUE);

    DirectionalLight dl = new DirectionalLight();
    dl.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
    dl.setDirection(new Vector3f(1, -0.5f, 1));
    dl.setEnabled(true);
    lightState.attach(dl);

    cam.setFrustum(1.0f, 1000.0f, -0.55f, 0.55f, 0.4125f, -0.4125f);
    cam.update();

    camNode = new CameraNode("Camera Node", cam);
    camNode.setLocalTranslation(new Vector3f(0, 250, -20));
    camNode.updateWorldData(0);
    input = new NodeHandler(camNode, 150, 1);
    rootNode.attachChild(camNode);
    display.setTitle("Terrain Test");
    display.getRenderer().setBackgroundColor(new ColorRGBA(0.5f,0.5f,0.5f,1));

    DirectionalLight dr = new DirectionalLight();
    dr.setEnabled(true);
    dr.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
    dr.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
    dr.setDirection(new Vector3f(0.5f, -0.5f, 0).normalizeLocal());

    CullState cs = display.getRenderer().createCullState();
    cs.setCullFace(CullState.Face.Back);
    cs.setEnabled(true);
    rootNode.setRenderState(cs);

    lightState.attach(dr);

    FaultFractalHeightMap heightMap = new FaultFractalHeightMap(257, 32, 0, 255,
        0.75f);
    Vector3f terrainScale = new Vector3f(10,1,10);
    heightMap.setHeightScale( 0.001f);
    page = new TerrainPage("Terrain", 33, heightMap.getSize(), terrainScale,
                                     heightMap.getHeightMap());

    page.setDetailTexture(1, 16);
    rootNode.attachChild(page);

    ProceduralTextureGenerator pt = new ProceduralTextureGenerator(heightMap);
    pt.addTexture(new ImageIcon(TestTerrain.class.getClassLoader().getResource(
        "jmetest/data/texture/grassb.png")), -128, 0, 128);
    pt.addTexture(new ImageIcon(TestTerrain.class.getClassLoader().getResource(
        "jmetest/data/texture/dirt.jpg")), 0, 128, 255);
    pt.addTexture(new ImageIcon(TestTerrain.class.getClassLoader().getResource(
        "jmetest/data/texture/highest.jpg")), 128, 255, 384);

    pt.createTexture(512);

    TextureState ts = display.getRenderer().createTextureState();
    ts.setEnabled(true);
    Texture t1 = TextureManager.loadTexture(
        pt.getImageIcon().getImage(),
        Texture.MinificationFilter.Trilinear,
        Texture.MagnificationFilter.Bilinear,
        true);
    ts.setTexture(t1, 0);

    Texture t2 = TextureManager.loadTexture(TestTerrain.class.getClassLoader().
                                            getResource(
        "jmetest/data/texture/Detail.jpg"),
                                            Texture.MinificationFilter.Trilinear,
                                            Texture.MagnificationFilter.Bilinear);
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

    FogState fs = display.getRenderer().createFogState();
    fs.setDensity(0.5f);
    fs.setEnabled(true);
    fs.setColor(new ColorRGBA(0.5f, 0.5f, 0.5f, 0.5f));
    fs.setEnd(1000);
    fs.setStart(500);
    fs.setDensityFunction(FogState.DensityFunction.Linear);
    fs.setQuality(FogState.Quality.PerVertex);
    rootNode.setRenderState(fs);


  }
}
