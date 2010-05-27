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

package jmetest.ogrexml;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.Spatial.LightCombineMode;
import com.jme.scene.Spatial.TextureCombineMode;
import com.jme.scene.shape.Box;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.util.TextureManager;
import com.jme.util.resource.ClasspathResourceLocator;
import com.jme.util.resource.RelativeResourceLocator;
import com.jme.util.resource.ResourceLocator;
import com.jme.util.resource.ResourceLocatorTool;
import com.jmex.effects.particles.ParticleFactory;
import com.jmex.effects.particles.ParticleMesh;
import com.jmex.model.ModelFormatException;
import com.jmex.model.ogrexml.MaterialLoader;
import com.jmex.model.ogrexml.OgreLoader;
import com.jmex.model.ogrexml.anim.Bone;
import com.jmex.model.ogrexml.anim.MeshAnimationController;

public class TestMeshLoading extends SimpleGame {

    private static final Logger logger = Logger.getLogger(
            TestMeshLoading.class.getName());

    private Node model;

    public static void main(String[] args){
        TestMeshLoading app = new TestMeshLoading();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    protected void loadMeshModel(){
        OgreLoader loader = new OgreLoader();
        MaterialLoader matLoader = new MaterialLoader();
        String matUrlString = "/jmetest/data/model/ogrexml/Example.material";
        String ninjaMeshUrlString =
                "/jmetest/data/model/ogrexml/ninja.mesh.xml";

        try {
            URL matURL = ResourceLocatorTool.locateResource(
                    ResourceLocatorTool.TYPE_TEXTURE, matUrlString);
            URL meshURL = ResourceLocatorTool.locateResource(
                    ResourceLocatorTool.TYPE_MODEL, ninjaMeshUrlString);

            if (meshURL == null)
                throw new IllegalStateException(
                        "Required runtime resource missing: "
                        + ninjaMeshUrlString);
            if (matURL == null)
                throw new IllegalStateException(
                        "Required runtime resource missing: " + matUrlString);
            try {
                ResourceLocatorTool.addResourceLocator(
                        ResourceLocatorTool.TYPE_TEXTURE,
                        new RelativeResourceLocator(matURL));
                  // This causes relative references in the .material file to
                  // resolve to the same dir as the material file.
                  // Don't have to set up a relative locator for TYPE_MODEL
                  // here, because OgreLoader.loadModel() takes care of that.
            } catch (URISyntaxException use) {
                // Since we're generating the URI from a URL we know to be
                // good, we won't get here.  This is just to satisfy the
                // compiler.
                throw new RuntimeException(use);
            }
            matLoader.load(matURL.openStream());
            if (matLoader.getMaterials().size() > 0)
                loader.setMaterials(matLoader.getMaterials());

            model = (Node) loader.loadModel(meshURL);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (ModelFormatException mfe) {
            logger.log(Level.SEVERE, null, mfe);
        }
    }

    protected Spatial loadParticle(){
        BlendState as1 = display.getRenderer().createBlendState();
        as1.setBlendEnabled(true);
        as1.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
        as1.setDestinationFunction(BlendState.DestinationFunction.One);
        as1.setTestEnabled(true);
        as1.setTestFunction(BlendState.TestFunction.GreaterThan);
        as1.setEnabled(true);

        TextureState ts = display.getRenderer().createTextureState();
        ts.setTexture(
            TextureManager.loadTexture("flaresmall.jpg",
            Texture.MinificationFilter.Trilinear,
            Texture.MagnificationFilter.Bilinear));
        ts.setEnabled(true);

        ParticleMesh manager = ParticleFactory.buildParticles("particles", 200);
        manager.setEmissionDirection(new Vector3f(0.0f, 1.0f, 0.0f));
        manager.setMaximumAngle(0.20943952f);
        manager.getParticleController().setSpeed(1.0f);
        manager.setMinimumLifeTime(150.0f);
        manager.setMaximumLifeTime(225.0f);
        manager.setStartSize(8.0f);
        manager.setEndSize(4.0f);
        manager.setStartColor(new ColorRGBA(1.0f, 0.312f, 0.121f, 1.0f));
        manager.setEndColor(new ColorRGBA(1.0f, 0.312f, 0.121f, 0.0f));
        manager.getParticleController().setControlFlow(false);
        manager.setInitialVelocity(0.12f);
        //manager.setGeometry((Geometry)(i.getChild(0)));

        manager.warmUp(60);
        manager.setRenderState(ts);
        manager.setRenderState(as1);
        manager.setLightCombineMode(LightCombineMode.Off);
        manager.setTextureCombineMode(TextureCombineMode.Replace);
        manager.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);

        ZBufferState zstate = display.getRenderer().createZBufferState();
        zstate.setEnabled(true);
        zstate.setWritable(false);
        manager.setRenderState(zstate);

        return manager;
    }

    @Override
    protected void simpleInitGame() {
        ResourceLocator locator = new ClasspathResourceLocator();
        ResourceLocatorTool.addResourceLocator(
                ResourceLocatorTool.TYPE_MODEL, locator);
          // This is to find our *.mesh.xml file.
        ResourceLocatorTool.addResourceLocator(
                ResourceLocatorTool.TYPE_TEXTURE, locator);
          // This is to find our *.material file.

        loadMeshModel();

        Quaternion q =  new Quaternion();
        q.fromAngleAxis(FastMath.PI, Vector3f.UNIT_Y);
        model.setLocalRotation(q); // make it face forward

        rootNode.attachChild(model);

        if (model.getControllerCount() < 1)
            throw new IllegalStateException(
                    "Ninja's animations are missing");

        MeshAnimationController animControl =
                (MeshAnimationController) model.getController(0);
        animControl.setAnimation("Walk");

        Bone b = animControl.getBone("Joint22");
        Node attachNode = b.getAttachmentsNode();
        model.attachChild(attachNode);
        Spatial particle = loadParticle();
        attachNode.attachChild(particle);

        b = animControl.getBone("Joint27");
        attachNode = b.getAttachmentsNode();
        model.attachChild(attachNode);
        particle = loadParticle();
        attachNode.attachChild(particle);

        b = animControl.getBone("Joint17");
        attachNode = b.getAttachmentsNode();
        model.attachChild(attachNode);
        particle = new Box("stick", new Vector3f(0, 0, -25), 2, 2, 30);
        attachNode.attachChild(particle);

        cam.setLocation(new Vector3f(139.05014f, 206.22263f, 225.55989f));
        cam.lookAt(model.getWorldBound().getCenter(), Vector3f.UNIT_Y);

        rootNode.updateGeometricState(0, true);
        rootNode.updateRenderState();
    }


}
