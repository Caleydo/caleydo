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

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.state.CullState;
import com.jme.scene.state.FogState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jmex.terrain.TerrainBlock;
import com.jmex.terrain.util.ImageBasedHeightMap;
import com.jmex.terrain.util.ProceduralSplatTextureGenerator;

/**
 * <code>TestProceduralSplatTexture</code>
 * 
 * @author Chris Gray
 * @version $Id: TestProceduralSplatTexture.java 4130 2009-03-19 20:04:51Z blaine.dev $
 */
public class TestProceduralSplatTexture extends SimpleGame {
    private static final Logger logger = Logger
            .getLogger(TestProceduralSplatTexture.class.getName());

	/**
	 * Entry point for the test
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		TestProceduralSplatTexture app = new TestProceduralSplatTexture();
		app.setConfigShowMode(ConfigShowMode.AlwaysShow);
		app.start();
	}

	/**
	 * builds the trimesh.	 
	 * 
	 * @see com.jme.app.SimpleGame#initGame()
	 */
	protected void simpleInitGame() {
		try {
			display.setTitle("ProceduralSplatTexture Test");
			cam.setLocation(new Vector3f(64 * 5, 125, 64 * 5));
			cam.update();

			FogState fs = display.getRenderer().createFogState();
			fs.setEnabled(false);
			rootNode.setRenderState(fs);

			CullState cs = display.getRenderer().createCullState();
			cs.setCullFace(CullState.Face.Back);
			cs.setEnabled(true);

			lightState.setTwoSidedLighting(true);
			((PointLight) lightState.get(0)).setLocation(new Vector3f(100, 500, 50));
			
			URL grayScale=TestProceduralSplatTexture.class.getClassLoader().getResource("jmetest/data/texture/terrain.png");
			
			ImageBasedHeightMap heightMap = new ImageBasedHeightMap(new ImageIcon(grayScale).getImage());

			Vector3f terrainScale = new Vector3f(5, 0.25f, 5);
			TerrainBlock tb = new TerrainBlock("Terrain", heightMap.getSize(), terrainScale, heightMap.getHeightMap(),
					new Vector3f(0, 0, 0));
			tb.setDetailTexture(1, 16);
			tb.setModelBound(new BoundingBox());
			tb.updateModelBound();
			tb.setLocalTranslation(new Vector3f(0, 0, 0));
			rootNode.attachChild(tb);
			rootNode.setRenderState(cs);

			ProceduralSplatTextureGenerator pst = new ProceduralSplatTextureGenerator(heightMap);
			pst.addTexture(new ImageIcon(TestProceduralSplatTexture.class.getClassLoader().getResource(
					"jmetest/data/texture/grassb.png")), -128, 0, 128);
			pst.addTexture(new ImageIcon(TestProceduralSplatTexture.class.getClassLoader().getResource(
					"jmetest/data/texture/dirt.jpg")), 0, 128, 255);
			pst.addTexture(new ImageIcon(TestProceduralSplatTexture.class.getClassLoader().getResource(
					"jmetest/data/texture/highest.jpg")), 128, 255, 384);

			pst.addSplatTexture(new ImageIcon(TestProceduralSplatTexture.class.getClassLoader().getResource(
					"jmetest/data/texture/terrainTex.png")), new ImageIcon(TestProceduralSplatTexture.class.getClassLoader().getResource(
					"jmetest/data/texture/water.png")));

			pst.createTexture(512);

			TextureState ts = display.getRenderer().createTextureState();
			ts.setEnabled(true);
			Texture t1 = TextureManager.loadTexture(pst.getImageIcon().getImage(), Texture.MinificationFilter.Trilinear,
					Texture.MagnificationFilter.Bilinear, true);
			ts.setTexture(t1, 0);

			Texture t2 = TextureManager.loadTexture(TestProceduralSplatTexture.class.getClassLoader().getResource(
					"jmetest/data/texture/Detail.jpg"), Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear);

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

			rootNode.setRenderQueueMode(Renderer.QUEUE_OPAQUE);
		} catch (Exception e) {
			logger.logp(Level.SEVERE, this.getClass().toString(),
                    "simpleInitGame()", "Exception", e);
		}
	}
}