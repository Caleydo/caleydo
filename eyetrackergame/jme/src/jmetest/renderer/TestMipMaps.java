/*
 * Copyright (c) 2003-2009 jMonkeyEngine All rights reserved. Redistribution and
 * use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met: * Redistributions of source
 * code must retain the above copyright notice, this list of conditions and the
 * following disclaimer. * Redistributions in binary form must reproduce the
 * above copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the distribution. *
 * Neither the name of 'jMonkeyEngine' nor the names of its contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package jmetest.renderer;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.animation.AnimationController;
import com.jme.animation.Bone;
import com.jme.animation.BoneAnimation;
import com.jme.animation.SkinNode;
import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.image.Texture2D;
import com.jme.image.util.ColorMipMapGenerator;
import com.jme.input.FirstPersonHandler;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.renderer.TextureRenderer;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.JmeException;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;
import com.jmex.model.collada.ColladaImporter;

/**
 * <code>TestMipMaps</code> shows off mipmapping in a regular scene and in the
 * context of render to texture. Different colors indicate different levels of
 * mipmaps with blue being highest and red being lowest.
 * 
 * @author Joshua Slack
 */
public class TestMipMaps extends SimpleGame {
    private static final Logger logger = Logger.getLogger(TestMipMaps.class
            .getName());

    private TextureRenderer tRenderer;
    private Node monitorNode;
    private Texture2D fakeTex;
    private float lastRend = 1;
    private float throttle = 1 / 30f;
    private SkinNode sn;

    /**
     * Entry point for the test,
     * 
     * @param args
     */
    public static void main(String[] args) {
        TestMipMaps app = new TestMipMaps();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    @Override
    protected void simpleInitGame() {
        setupModel();
        setupMonitor();
    }

    protected void cleanup() {
        super.cleanup();
        tRenderer.cleanup();
    }

    protected void simpleRender() {
        lastRend += tpf;
        if (lastRend > throttle) {
            tRenderer.render(sn, fakeTex);
            lastRend = 0;
        }
    }

    private void setupMonitor() {

        tRenderer = display.createTextureRenderer(256, 256, TextureRenderer.Target.Texture2D);
        tRenderer.getCamera().setAxes(new Vector3f(-1, 0, 0),
                new Vector3f(0, 0, 1), new Vector3f(0, 1, 0));
        tRenderer.getCamera().setLocation(new Vector3f(0, -100, 20));

        monitorNode = new Node("Monitor Node");
        monitorNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        Quad quad = new Quad("Monitor");
        quad.updateGeometry(100, 100);
        quad.setLocalTranslation(new Vector3f(110, 110, 0));
        quad.setZOrder(1);
        monitorNode.attachChild(quad);

        Quad quad2 = new Quad("Monitor Back");
        quad2.updateGeometry(110, 110);
        quad2.setLocalTranslation(new Vector3f(110, 110, 0));
        quad2.setZOrder(2);
        monitorNode.attachChild(quad2);

        // Setup our params for the depth buffer
        ZBufferState buf = display.getRenderer().createZBufferState();
        buf.setEnabled(false);

        monitorNode.setRenderState(buf);

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

        monitorNode.setLightCombineMode(Spatial.LightCombineMode.Off);
        rootNode.attachChild(monitorNode);
    }

    private void setupModel() {
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
        
        // Our model is Z up so orient the camera properly.
        cam.setAxes(new Vector3f(-1, 0, 0), new Vector3f(0, 0, 1),
                new Vector3f(0, 1, 0));
        cam.setLocation(new Vector3f(0, -100, 20));
        input = new FirstPersonHandler(cam, 80, 1);

        // url to the location of the model's textures
        // this stream points to the model itself.
        InputStream mobboss = TestMipMaps.class.getClassLoader()
                .getResourceAsStream("jmetest/data/model/collada/man.dae");
        // this stream points to the animation file. Note: You don't necessarily
        // have to split animations out into seperate files, this just helps.
        InputStream animation = TestMipMaps.class.getClassLoader()
                .getResourceAsStream("jmetest/data/model/collada/man_walk.dae");
        if (mobboss == null) {
            logger.info("Unable to find file, did you include jme-test.jar in classpath?");
            System.exit(0);
        }
        // tell the importer to load the mob boss
        ColladaImporter.load(mobboss, "model");
        // we can then retrieve the skin from the importer as well as the
        // skeleton
        sn = ColladaImporter.getSkinNode(ColladaImporter.getSkinNodeNames()
                .get(0));
        Bone skel = ColladaImporter.getSkeleton(ColladaImporter
                .getSkeletonNames().get(0));
        // clean up the importer as we are about to use it again.
        ColladaImporter.cleanUp();

        // load the animation file.
        ColladaImporter.load(animation, "anim");
        // this file might contain multiple animations, (in our case it's one)
        ArrayList<String> animations = ColladaImporter.getControllerNames();
        logger.info("Number of animations: " + animations.size());
        for (int i = 0; i < animations.size(); i++) {
            logger.info(animations.get(i));
        }
        // Obtain the animation from the file by name
        BoneAnimation anim1 = ColladaImporter.getAnimationController(animations
                .get(0));

        // set up a new animation controller with our BoneAnimation
        AnimationController ac = new AnimationController();
        ac.addAnimation(anim1);
        ac.setRepeatType(Controller.RT_CYCLE);
        ac.setActive(true);
        ac.setActiveAnimation(anim1);

        // assign the animation controller to our skeleton
        skel.addController(ac);

        // Let's strip out any textures.
        stripTexturesAndMaterials(sn);

        // Now add our mipmap texture
        Texture texture = new Texture2D();
        texture.setMagnificationFilter(Texture.MagnificationFilter.Bilinear);
        texture.setMinificationFilter(Texture.MinificationFilter.BilinearNearestMipMap);
        try {
            texture.setImage(ColorMipMapGenerator.generateColorMipMap(512,
                    new ColorRGBA[] { ColorRGBA.blue.clone(), ColorRGBA.green.clone(),
                            ColorRGBA.white.clone() }, ColorRGBA.red.clone()));
        } catch (JmeException e) {
            logger.logp(Level.SEVERE, this.getClass().toString(), "setupModel()", "Exception", e);
        }
        TextureState ts = display.getRenderer().createTextureState();
        ts.setTexture(texture);
        sn.setRenderState(ts);
        
        // attach the skeleton and the skin to the rootnode. Skeletons could
        // possibly be used to update multiple skins, so they are seperate
        // objects.
        rootNode.attachChild(sn);
        rootNode.attachChild(skel);

        // all done clean up.
        ColladaImporter.cleanUp();

        lightState.detachAll();
        lightState.setEnabled(false);
    }

    private void stripTexturesAndMaterials(Spatial sp) {
        sp.clearRenderState(RenderState.StateType.Texture);
        sp.clearRenderState(RenderState.StateType.Material);
        if (sp instanceof Node) {
            Node n = (Node) sp;
            for (Spatial child : n.getChildren()) {
                stripTexturesAndMaterials(child);
            }
        }
    }

}
