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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import jmetest.input.TestThirdPersonController;
import jmetest.terrain.TestTerrain;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.image.Texture2D;
import com.jme.input.ChaseCamera;
import com.jme.input.ThirdPersonHandler;
import com.jme.light.DirectionalLight;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.renderer.TextureRenderer;
import com.jme.scene.CameraNode;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.CullState;
import com.jme.scene.state.FogState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jme.util.export.binary.BinaryImporter;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;
import com.jmex.model.converters.MilkToJme;
import com.jmex.terrain.TerrainPage;
import com.jmex.terrain.util.FaultFractalHeightMap;
import com.jmex.terrain.util.ProceduralTextureGenerator;

/**
 * <code>TestCameraMan</code>
 * 
 * @author Joshua Slack
 */
public class TestSpatialLookAt extends SimpleGame {
    private static final Logger logger = Logger
            .getLogger(TestSpatialLookAt.class.getName());
    
    private Node monitorNode;

    private CameraNode camNode;

    private TextureRenderer tRenderer;

    private Texture2D fakeTex;

    private float lastRend = 1;

    public Node scene;

    public Node m_character;

    public TerrainPage page;

    public ChaseCamera chaser;

    public ThirdPersonHandler input;

    public Geometry target;

    /**
     * Entry point for the test,
     * 
     * @param args
     */
    public static void main(String[] args) {
        TestSpatialLookAt app = new TestSpatialLookAt();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    protected void cleanup() {
      super.cleanup();
      tRenderer.cleanup();
    }

    protected void simpleUpdate() {
        monitorNode.updateGeometricState(0.0f, true);
    }

    private Vector3f normal = new Vector3f(); 
    protected void simpleRender() {
        input.update(tpf);
        chaser.update(tpf);
        float camMinHeight = page.getHeight(cam.getLocation()) + 2f;
        if (!Float.isInfinite(camMinHeight) && !Float.isNaN(camMinHeight)
                && cam.getLocation().y <= camMinHeight) {
            cam.getLocation().y = camMinHeight;
            cam.update();
        }

        float characterMinHeight = page.getHeight(m_character
                .getLocalTranslation())+((BoundingBox)m_character.getWorldBound()).yExtent;
        if (!Float.isInfinite(characterMinHeight) && !Float.isNaN(characterMinHeight)) {
            m_character.getLocalTranslation().y = characterMinHeight;
        }
        
        page.getSurfaceNormal(m_character.getLocalTranslation(), normal);
        if (normal != null)
            m_character.rotateUpTo(normal);

        lastRend += tpf;
        if (lastRend > .03f) {
            camNode.lookAt(m_character.getWorldTranslation(), Vector3f.UNIT_Y);
            tRenderer.render(scene, fakeTex);
            lastRend = 0;
        }
        display.getRenderer().draw(monitorNode);
    }

    /**
     * builds the trimesh.
     * 
     * @see com.jme.app.SimpleGame#initGame()
     */
    protected void simpleInitGame() {
        display.setTitle("Spatial.lookAt Test");

        scene = new Node("rend_scene");
        rootNode.attachChild(scene);
        rootNode.setRenderQueueMode(Renderer.QUEUE_OPAQUE);

        setupCharacter();
        setupTerrain();
        setupChaseCamera();
        setupInput();
        setupSecurityCamera();
        setupSecurityMonitor();
    }

    private void setupSecurityMonitor() {
        monitorNode = new Node("Monitor Node");
        monitorNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        Quad quad = new Quad("Monitor");
        quad.setZOrder(1);
        quad.updateGeometry(150, 150);
        quad.setLocalTranslation(new Vector3f(90f, 110f, 0));
        monitorNode.attachChild(quad);

        Quad quad2 = new Quad("Monitor");
        quad2.setZOrder(2);
        quad2.updateGeometry(160f, 160f);
        quad2.getLocalTranslation().set(quad.getLocalTranslation());
        monitorNode.attachChild(quad2);

        // Ok, now lets create the Texture object that our scene will be
        // rendered to.
        tRenderer.setBackgroundColor(new ColorRGBA(0f, 0f, 0f, 1f));
        fakeTex = new Texture2D();
        fakeTex.setRenderToTextureType(Texture.RenderToTextureType.RGBA);
        tRenderer.setupTexture(fakeTex);
        TextureState screen = display.getRenderer().createTextureState();
        screen.setTexture(fakeTex);
        screen.setEnabled(true);
        quad.setRenderState(screen);
        monitorNode.updateGeometricState(0.0f, true);
        monitorNode.updateRenderState();
        camNode.lookAt(m_character.getWorldTranslation(), Vector3f.UNIT_Y);
    }

    private void setupSecurityCamera() {
        tRenderer = display.createTextureRenderer(256, 256, TextureRenderer.Target.Texture2D);

        camNode = new CameraNode("Camera Node", tRenderer.getCamera());
        camNode.setLocalTranslation(new Vector3f(0, 255, 0));
        camNode.updateGeometricState(0, true);

        Node camBox;
        try {
            ResourceLocatorTool.addResourceLocator(
                    ResourceLocatorTool.TYPE_TEXTURE,
                    new SimpleResourceLocator(TestSpatialLookAt.class
                            .getClassLoader().getResource(
                                    "jmetest/data/model/msascii/")));
        } catch (URISyntaxException e1) {
            logger.log(Level.WARNING, "unable to setup texture directory.", e1);
        }

        MilkToJme converter2 = new MilkToJme();
        URL MSFile2 = TestSpatialLookAt.class.getClassLoader().getResource(
                "jmetest/data/model/msascii/camera.ms3d");
        ByteArrayOutputStream BO2 = new ByteArrayOutputStream();

        try {
            converter2.convert(MSFile2.openStream(), BO2);
        } catch (IOException e) {
            logger.info("IO problem writting the file!!!");
            logger.info(e.getMessage());
            System.exit(0);
        }
        camBox = null;
        try {
            camBox = (Node)BinaryImporter.getInstance().load(new ByteArrayInputStream(BO2
                    .toByteArray()));
        } catch (IOException e) {
            logger.info("darn exceptions:" + e.getMessage());
        }
        camNode.attachChild(camBox);
        rootNode.attachChild(camNode);
    }

    private void setupCharacter() {
        target = new Box("box", new Vector3f(), .5f, .5f, .5f);
        target.setLocalScale(10);
        target.setModelBound(new BoundingBox());
        target.updateModelBound();
        m_character = new Node("char node");
        scene.attachChild(m_character);
        m_character.attachChild(target);
        m_character.getLocalTranslation().y = 255;
        m_character.updateWorldBound(); // We do this to allow the camera setup
                                        // access to the world bound in our
                                        // setup code.

        TextureState ts = display.getRenderer().createTextureState();
        ts.setEnabled(true);
        ts.setTexture(TextureManager.loadTexture(
                TestThirdPersonController.class.getClassLoader().getResource(
                        "jmetest/data/images/Monkey.jpg"), Texture.MinificationFilter.BilinearNearestMipMap,
                Texture.MagnificationFilter.Bilinear));
        m_character.setRenderState(ts);
    }

    private void setupTerrain() {
        display.getRenderer().setBackgroundColor(
                new ColorRGBA(0.5f, 0.5f, 0.5f, 1));

        DirectionalLight dr = new DirectionalLight();
        dr.setEnabled(true);
        dr.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
        dr.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        dr.setDirection(new Vector3f(0.5f, -0.5f, 0));

        CullState cs = display.getRenderer().createCullState();
        cs.setCullFace(CullState.Face.Back);
        cs.setEnabled(true);
        rootNode.setRenderState(cs);

        LightState lightState = display.getRenderer().createLightState();
        lightState.setEnabled(true);
        lightState.attach(dr);
        rootNode.setRenderState(lightState);

        FaultFractalHeightMap heightMap = new FaultFractalHeightMap(257, 32, 0,
                255, 0.75f);
        Vector3f terrainScale = new Vector3f(10, 1, 10);
        heightMap.setHeightScale(0.001f);
        page = new TerrainPage("Terrain", 33, heightMap.getSize(),
                terrainScale, heightMap.getHeightMap());

        page.setDetailTexture(1, 16);
        scene.attachChild(page);

        ProceduralTextureGenerator pt = new ProceduralTextureGenerator(
                heightMap);
        pt.addTexture(new ImageIcon(TestTerrain.class.getClassLoader()
                .getResource("jmetest/data/texture/grassb.png")), -128, 0, 128);
        pt.addTexture(new ImageIcon(TestTerrain.class.getClassLoader()
                .getResource("jmetest/data/texture/dirt.jpg")), 0, 128, 255);
        pt.addTexture(new ImageIcon(TestTerrain.class.getClassLoader()
                .getResource("jmetest/data/texture/highest.jpg")), 128, 255,
                384);

        pt.createTexture(512);

        TextureState ts = display.getRenderer().createTextureState();
        ts.setEnabled(true);
        Texture t1 = TextureManager.loadTexture(pt.getImageIcon().getImage(),
                Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear, true);
        ts.setTexture(t1, 0);

        Texture t2 = TextureManager.loadTexture(TestThirdPersonController.class
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

    private void setupChaseCamera() {
        Vector3f targetOffset = new Vector3f();
        targetOffset.y = ((BoundingBox) m_character.getWorldBound()).yExtent * 1.5f;
        chaser = new ChaseCamera(cam, m_character);
        chaser.setTargetOffset(targetOffset);
    }

    private void setupInput() {
        HashMap<String, Object> handlerProps = new HashMap<String, Object>();
        handlerProps.put(ThirdPersonHandler.PROP_DOGRADUAL, "true");
        handlerProps.put(ThirdPersonHandler.PROP_TURNSPEED, ""
                + (1.0f * FastMath.PI));
        handlerProps.put(ThirdPersonHandler.PROP_LOCKBACKWARDS, "false");
        handlerProps.put(ThirdPersonHandler.PROP_CAMERAALIGNEDMOVE, "true");
        input = new ThirdPersonHandler(m_character, cam, handlerProps);
        input.setActionSpeed(100f);
    }
}