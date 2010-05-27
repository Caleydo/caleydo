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

package jmetest.renderer.loader;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jmetest.renderer.TestEnvMap;
import jmetest.renderer.TestMipMaps;

import com.jme.app.SimpleGame;
import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.input.FirstPersonHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.light.DirectionalLight;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.state.GLSLShaderObjectsState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;
import com.jmex.model.collada.ColladaImporter;

/**
 * TestNormalmap
 */
public class TestNormalmap extends SimpleGame {
    private static final Logger logger = Logger.getLogger(TestNormalmap.class
            .getName());
    
    private Vector3f lightDir = new Vector3f();
    private GLSLShaderObjectsState so;
    private String currentShaderStr = "jmetest/data/images/normalmap";

    public static void main(String[] args) {
        TestNormalmap app = new TestNormalmap();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    protected void simpleUpdate() {
        if (KeyBindingManager.getKeyBindingManager().isValidCommand(
                "reloadShader", false)) {
            reloadShader();
        }

        float spinValX = FastMath.sin(timer.getTimeInSeconds() * 2.0f);
        float spinValY = FastMath.cos(timer.getTimeInSeconds() * 2.0f);
        lightDir.set(spinValX, spinValY, -1.0f).normalizeLocal();
    }

    public void reloadShader() {
        GLSLShaderObjectsState testShader = DisplaySystem.getDisplaySystem()
                .getRenderer().createGLSLShaderObjectsState();
        try {
            testShader.load(TestColladaLoading.class.getClassLoader()
                    .getResource(currentShaderStr + ".vert"),
                    TestColladaLoading.class.getClassLoader().getResource(
                            currentShaderStr + ".frag"));
            testShader.apply();
            DisplaySystem.getDisplaySystem().getRenderer().checkCardError();
        } catch (JmeException e) {
            logger.log(Level.WARNING, "Failed to reload shader", e);
            return;
        }

        so.load(TestColladaLoading.class.getClassLoader().getResource(
                currentShaderStr + ".vert"), TestColladaLoading.class
                .getClassLoader().getResource(currentShaderStr + ".frag"));

        so.setUniform("baseMap", 0);
        so.setUniform("normalMap", 1);
        so.setUniform("specularMap", 2);

        logger.info("Shader reloaded...");
    }

    protected void simpleInitGame() {
        KeyBindingManager.getKeyBindingManager().set("reloadShader",
                KeyInput.KEY_F);

        // Our model is Z up so orient the camera properly.
        cam.setAxes(new Vector3f(-1, 0, 0), new Vector3f(0, 0, 1),
                new Vector3f(0, 1, 0));
        cam.setLocation(new Vector3f(0, -100, 0));

        // Create a directional light
        DirectionalLight dr = new DirectionalLight();
        dr.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
        dr.setAmbient(new ColorRGBA(0.2f, 0.2f, 0.2f, 1.0f));
        dr.setSpecular(new ColorRGBA(0.7f, 0.7f, 0.7f, 1.0f));
        dr.setDirection(lightDir);
        dr.setEnabled(true);

        lightState.detachAll();
        lightState.attach(dr);

        Box box = new Box("box", new Vector3f(), 1, 1, 1);
        rootNode.attachChild(box);

        so = display.getRenderer().createGLSLShaderObjectsState();

        // Check is GLSL is supported on current hardware.
        if (!GLSLShaderObjectsState.isSupported()) {
            logger.severe("Your graphics card does not support GLSL programs, and thus cannot run this test.");
            quit();
        }

        reloadShader();

        TextureState ts = display.getRenderer().createTextureState();

        // Base texture
        Texture baseMap = TextureManager.loadTexture(TestEnvMap.class
                .getClassLoader().getResource(
                "jmetest/data/images/Fieldstone.jpg"),
                Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear);
        baseMap.setWrap(Texture.WrapMode.Repeat);
        ts.setTexture(baseMap, 0);

        // Normal map
        Texture normalMap = TextureManager.loadTexture(TestEnvMap.class
                .getClassLoader().getResource(
                "jmetest/data/images/FieldstoneNormal.jpg"),
                Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear,
                Image.Format.GuessNoCompression, 0.0f, true);
        normalMap.setWrap(Texture.WrapMode.Repeat);
        ts.setTexture(normalMap, 1);

        // Specular map
        Texture specMap = TextureManager.loadTexture(TestEnvMap.class
                .getClassLoader().getResource(
                "jmetest/data/images/FieldstoneSpec.jpg"),
                Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear);
        specMap.setWrap(Texture.WrapMode.Repeat);
        ts.setTexture(specMap, 2);

        try {
            ResourceLocatorTool.addResourceLocator(
                    ResourceLocatorTool.TYPE_TEXTURE,
                    new SimpleResourceLocator(TestMipMaps.class
                            .getClassLoader().getResource(
                                    "jmetest/data/model/collada/")));
        } catch (URISyntaxException e1) {
            logger.warning("Unable to add texture directory to RLT: "
                    + e1.toString());
        }

        // this stream points to the model itself.
        InputStream modelStream = TestColladaLoading.class.getClassLoader()
                .getResourceAsStream(
                "jmetest/data/model/collada/Test_Ball_Hard.dae");

        if (modelStream == null) {
            logger.info("Unable to find file, did you include jme-test.jar in classpath?");
            System.exit(0);
        }
        // tell the importer to load the model
        ColladaImporter.load(modelStream, "model");

        Node model = ColladaImporter.getModel();

        // Remove MaterialState(s) for this test because the model/importer
        // creates lousy settings
        removeMaterialStates(model);

        // Test MaterialState (should be set through the import anyway)
        MaterialState ms = display.getRenderer().createMaterialState();
        ms.setColorMaterial(MaterialState.ColorMaterial.AmbientAndDiffuse);
        ms.setAmbient(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
        ms.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
        ms.setSpecular(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
        ms.setShininess(25.0f);

        // Set all states on model
        model.setRenderState(ts);
        model.setRenderState(so);
        model.setRenderState(ms);

        ColladaImporter.cleanUp();

        rootNode.attachChild(model);

        rootNode.updateGeometricState(0, true);

         input = new FirstPersonHandler( cam, 80, 1 );
    }

    public static void removeMaterialStates(Node node) {
        node.clearRenderState(RenderState.StateType.Material);

        if (node.getQuantity() == 0) {
            return;
        }
        List<Spatial> children = node.getChildren();
        for (int i = 0, cSize = children.size(); i < cSize; i++) {
            Spatial child = children.get(i);
            if (child != null) {
                child.clearRenderState(RenderState.StateType.Material);
                if (child instanceof Node) {
                    removeMaterialStates((Node) child);
                } else if (child instanceof SharedMesh) {
                    SharedMesh sharedMesh = (SharedMesh) child;
                    TriMesh t = sharedMesh.getTarget();
                    t.clearRenderState(RenderState.StateType.Material);
                }
            }
        }
    }
}