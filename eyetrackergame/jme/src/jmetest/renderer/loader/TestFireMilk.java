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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.input.FirstPersonHandler;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Controller;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Spatial.LightCombineMode;
import com.jme.scene.Spatial.TextureCombineMode;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.util.TextureManager;
import com.jme.util.export.binary.BinaryImporter;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;
import com.jmex.effects.particles.ParticleFactory;
import com.jmex.effects.particles.ParticleMesh;
import com.jmex.model.animation.JointController;
import com.jmex.model.converters.MilkToJme;

/**
 * <code>TestFireMilk</code>
 * @author Joshua Slack
 */
public class TestFireMilk extends SimpleGame {
    private static final Logger logger = Logger.getLogger(TestFireMilk.class
            .getName());
    
  private Node i;
  public static void main(String[] args) {
    TestFireMilk app = new TestFireMilk();
    app.setConfigShowMode(ConfigShowMode.AlwaysShow);
    app.start();
  }

  /**
   * set up the scene
   * @see com.jme.app.AbstractGame#initGame()
   */
  protected void simpleInitGame() {
    display.setTitle("Joint Animation");
    display.setVSyncEnabled(true);
    cam.setLocation(new Vector3f(0.0f, 0.0f, 200.0f));
    cam.update();
    (( FirstPersonHandler)input).getKeyboardLookHandler().setActionSpeed(100);
    
    lightState.setEnabled(false);

    try {
        ResourceLocatorTool.addResourceLocator(
                ResourceLocatorTool.TYPE_TEXTURE,
                new SimpleResourceLocator(TestFireMilk.class
                        .getClassLoader().getResource(
                                "jmetest/data/model/msascii/")));
        ResourceLocatorTool.addResourceLocator(
                ResourceLocatorTool.TYPE_TEXTURE,
                new SimpleResourceLocator(TestFireMilk.class
                        .getClassLoader().getResource(
                                "jmetest/data/texture/")));
        } catch (URISyntaxException e1) {
            logger.log(Level.WARNING, "unable to setup texture directories.", e1);
        }

    MilkToJme converter=new MilkToJme();
    URL MSFile=TestFireMilk.class.getClassLoader().getResource(
    "jmetest/data/model/msascii/run.ms3d");
    ByteArrayOutputStream BO=new ByteArrayOutputStream();

    try {
        converter.convert(MSFile.openStream(),BO);
    } catch (IOException e) {
        logger.info("IO problem writting the file!!!");
        logger.info(e.getMessage());
        System.exit(0);
    }
    
    i=null;
    try {
        i=(Node)BinaryImporter.getInstance().load(new ByteArrayInputStream(BO.toByteArray()));
    } catch (IOException e) {
        logger.info("darn exceptions:" + e.getMessage());
    }
    ((JointController) i.getController(0)).setSpeed(1.0f);
    ((JointController) i.getController(0)).setRepeatType(Controller.RT_CYCLE);
    i.setRenderQueueMode(Renderer.QUEUE_OPAQUE);
    rootNode.attachChild(i);

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
    manager.setGeometry((Geometry)(i.getChild(0)));

    manager.warmUp(60);
    manager.setRenderState(ts);
    manager.setRenderState(as1);
    manager.setLightCombineMode(LightCombineMode.Off);
    manager.setTextureCombineMode(TextureCombineMode.Replace);
    ZBufferState zstate = display.getRenderer().createZBufferState();
    zstate.setEnabled(false);
    manager.setRenderState(zstate);
    rootNode.attachChild(manager);
  }
}
