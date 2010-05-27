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

import java.util.HashMap;

import javax.swing.ImageIcon;

import com.jme.app.SimplePassGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.ChaseCamera;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.ThirdPersonHandler;
import com.jme.light.DirectionalLight;
import com.jme.light.PointLight;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.renderer.pass.RenderPass;
import com.jme.renderer.pass.ShadowedRenderPass;
import com.jme.scene.Node;
import com.jme.scene.VBOInfo;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.PQTorus;
import com.jme.scene.state.CullState;
import com.jme.scene.state.FogState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jmex.terrain.TerrainPage;
import com.jmex.terrain.util.FaultFractalHeightMap;
import com.jmex.terrain.util.ProceduralTextureGenerator;

/**
 * <code>TestShadowPass</code>
 * 
 * @author Joshua Slack
 * @version $Revision: 4130 $
 */
public class TestShadowPass extends SimplePassGame {
    private Node m_character;
    private Node occluders;
    private ChaseCamera chaser;
    private TerrainPage page;
    private FogState fs;
    private Vector3f normal = new Vector3f();
    private static ShadowedRenderPass sPass = new ShadowedRenderPass();
    private static boolean debug = true;

    /**
     * Entry point for the test,
     * 
     * @param args
     */
    public static void main(String[] args) {
        TestShadowPass app = new TestShadowPass();
        if (debug) new ShadowTweaker(sPass).setVisible(true);
        
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }
    
    TestShadowPass() {
        stencilBits = 4; // we need a minimum stencil buffer at least.
        
    }

    /**
     * builds the scene.
     * 
     * @see com.jme.app.BaseGame#initGame()
     */
    protected void simpleInitGame() {
        display.setTitle("jME - Shadow Volume Test : X - enable/disable shadows");
        display.getRenderer().setBackgroundColor(ColorRGBA.gray.clone());

        setupCharacter();
        setupTerrain();
        setupChaseCamera();
        setupInput();
        setupOccluders();
        
        rootNode.setRenderQueueMode(Renderer.QUEUE_OPAQUE);
        
        /** Assign key X to action "toggle_shadows". */
        KeyBindingManager.getKeyBindingManager().set("toggle_shadows",
                KeyInput.KEY_X);
 
        
        sPass.add(rootNode);
        sPass.addOccluder(m_character);
        sPass.addOccluder(occluders);
        sPass.setRenderShadows(true);
        sPass.setLightingMethod(ShadowedRenderPass.LightingMethod.Additive);
        pManager.add(sPass);
        
        RenderPass rPass = new RenderPass();
        rPass.add(statNode);
        pManager.add(rPass);
    }
    
    protected void simpleUpdate() {
        chaser.update(tpf);
        float characterMinHeight = page.getHeight(m_character
                .getLocalTranslation())+((BoundingBox)m_character.getWorldBound()).yExtent;
        if (!Float.isInfinite(characterMinHeight) && !Float.isNaN(characterMinHeight)) {
            m_character.getLocalTranslation().y = characterMinHeight;
        }

        float camMinHeight = characterMinHeight + 150f;
        if (!Float.isInfinite(camMinHeight) && !Float.isNaN(camMinHeight)
                && cam.getLocation().y <= camMinHeight) {
            cam.getLocation().y = camMinHeight;
            cam.update();
        }


        if (KeyBindingManager.getKeyBindingManager().isValidCommand(
                "toggle_shadows", false)) {
            sPass.setRenderShadows(!sPass.getRenderShadows());
        }
    }

    private void setupCharacter() {
        PQTorus b = new PQTorus("torus - target", 2, 3, 2.0f, 1.0f, 64, 12);
        b.setModelBound(new BoundingBox());
        b.updateModelBound();
        b.setVBOInfo(new VBOInfo(true));
        m_character = new Node("char node");
        rootNode.attachChild(m_character);
        m_character.attachChild(b);
        m_character.updateWorldBound(); // We do this to allow the camera setup access to the world bound in our setup code.

        TextureState ts = display.getRenderer().createTextureState();
        ts.setEnabled(true);
        ts.setTexture(
            TextureManager.loadTexture(
            TestShadowPass.class.getClassLoader().getResource(
            "jmetest/data/images/Monkey.jpg"),
            Texture.MinificationFilter.BilinearNearestMipMap,
            Texture.MagnificationFilter.Bilinear));
        m_character.setRenderState(ts);
    }
    
    private void setupTerrain() {

        DirectionalLight dr = new DirectionalLight();
        dr.setEnabled(true);
        dr.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
        dr.setAmbient(new ColorRGBA(.2f, .2f, .2f, .3f));
        dr.setDirection(new Vector3f(0.5f, -0.4f, 0).normalizeLocal());
        dr.setShadowCaster(true);

        PointLight pl = new PointLight();
        pl.setEnabled(true);
        pl.setDiffuse(new ColorRGBA(.7f, .7f, .7f, 1.0f));
        pl.setAmbient(new ColorRGBA(.25f, .25f, .25f, .25f));
        pl.setLocation(new Vector3f(0,500,0));
        pl.setShadowCaster(true);

        DirectionalLight dr2 = new DirectionalLight();
        dr2.setEnabled(true);
        dr2.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
        dr2.setAmbient(new ColorRGBA(.2f, .2f, .2f, .4f));
        dr2.setDirection(new Vector3f(-0.2f, -0.3f, .2f).normalizeLocal());
        dr2.setShadowCaster(true);

        CullState cs = display.getRenderer().createCullState();
        cs.setCullFace(CullState.Face.Back);
        cs.setEnabled(true);
        rootNode.setRenderState(cs);

        lightState.detachAll();
        lightState.attach(dr);
        lightState.attach(dr2);
        lightState.attach(pl);
        lightState.setGlobalAmbient(new ColorRGBA(0.6f, 0.6f, 0.6f, 1.0f));

        FaultFractalHeightMap heightMap = new FaultFractalHeightMap(257, 32, 0,
                255, 0.55f);
        Vector3f terrainScale = new Vector3f(10, 1, 10);
        heightMap.setHeightScale(0.001f);
        page = new TerrainPage("Terrain", 33, heightMap.getSize(),
                terrainScale, heightMap.getHeightMap());

        page.setDetailTexture(1, 16);
        rootNode.attachChild(page);

        ProceduralTextureGenerator pt = new ProceduralTextureGenerator(
                heightMap);
        pt.addTexture(new ImageIcon(TestShadowPass.class.getClassLoader()
                .getResource("jmetest/data/texture/grassb.png")), -128, 0, 128);
        pt.addTexture(new ImageIcon(TestShadowPass.class.getClassLoader()
                .getResource("jmetest/data/texture/dirt.jpg")), 0, 128, 255);
        pt.addTexture(new ImageIcon(TestShadowPass.class.getClassLoader()
                .getResource("jmetest/data/texture/highest.jpg")), 128, 255,
                384);

        pt.createTexture(512);

        TextureState ts = display.getRenderer().createTextureState();
        ts.setEnabled(true);
        Texture t1 = TextureManager.loadTexture(pt.getImageIcon().getImage(),
                Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear, true);
        ts.setTexture(t1, 0);

        Texture t2 = TextureManager.loadTexture(TestShadowPass.class
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

        fs = display.getRenderer().createFogState();
        fs.setDensity(0.5f);
        fs.setEnabled(true);
        fs.setColor(new ColorRGBA(0.5f, 0.5f, 0.5f, 0.5f));
        fs.setEnd(1000);
        fs.setStart(500);
        fs.setDensityFunction(FogState.DensityFunction.Linear);
        fs.setQuality(FogState.Quality.PerVertex);
        rootNode.setRenderState(fs);
    }

    private void setupOccluders() {

        TextureState ts = display.getRenderer().createTextureState();
        ts.setEnabled(true);
        ts.setTexture(
            TextureManager.loadTexture(
            TestShadowPass.class.getClassLoader().getResource(
            "jmetest/data/texture/rust.jpg"),
            Texture.MinificationFilter.Trilinear,
            Texture.MagnificationFilter.Bilinear));

        occluders = new Node("occs");
        occluders.setRenderState(ts);
        rootNode.attachChild(occluders);
        for (int i = 0; i < 50; i++) {
            Box b = new Box("box", new Vector3f(), 8, 50, 8);
            b.setModelBound(new BoundingBox());
            b.updateModelBound();
            float x = (float) Math.random() * 2000 - 1000;
            float z = (float) Math.random() * 2000 - 1000;
            b.setLocalTranslation(new Vector3f(x, page.getHeight(x, z)+50, z));
            page.getSurfaceNormal(b.getLocalTranslation(), normal );
            if (normal != null)
                b.rotateUpTo(normal);
            occluders.attachChild(b);
        }
       occluders.lock();
    }
    
    private void setupChaseCamera() {
        Vector3f targetOffset = new Vector3f();
        targetOffset.y = ((BoundingBox) m_character.getWorldBound()).yExtent * 1.5f;
        chaser = new ChaseCamera(cam, m_character);
        chaser.setTargetOffset(targetOffset);
        chaser.getMouseLook().setMinRollOut(150);
        chaser.setMaxDistance(300);
    }

    private void setupInput() {
        HashMap<String, Object> handlerProps = new HashMap<String, Object>();
        handlerProps.put(ThirdPersonHandler.PROP_DOGRADUAL, "true");
        handlerProps.put(ThirdPersonHandler.PROP_TURNSPEED, ""+(.5f * FastMath.PI));
        handlerProps.put(ThirdPersonHandler.PROP_LOCKBACKWARDS, "true");
        handlerProps.put(ThirdPersonHandler.PROP_CAMERAALIGNEDMOVE, "true");
        handlerProps.put(ThirdPersonHandler.PROP_ROTATEONLY, "true");
        input = new ThirdPersonHandler(m_character, cam, handlerProps);
        input.setActionSpeed(100f);
    }
}
