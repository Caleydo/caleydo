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
import java.util.logging.Level;
import java.util.logging.Logger;

import jmetest.renderer.loader.TestMilkJmeWrite;

import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.image.Texture2D;
import com.jme.input.NodeHandler;
import com.jme.light.DirectionalLight;
import com.jme.light.LightNode;
import com.jme.light.SpotLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.TextureRenderer;
import com.jme.scene.CameraNode;
import com.jme.scene.Node;
import com.jme.scene.Spatial.LightCombineMode;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.CullState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.util.export.binary.BinaryImporter;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;
import com.jmex.model.converters.MilkToJme;

/**
 * <code>TestRenderToTexture</code>
 * @author Joshua Slack
 */
public class TestCameraMan extends SimpleGame {
    private static final Logger logger = Logger.getLogger(TestCameraMan.class
            .getName());
    
  private Node model;
  private Node monitorNode;
  private CameraNode camNode;

  private TextureRenderer tRenderer;
  private Texture2D fakeTex;
  private float lastRend = 1;
  private float throttle = 1/30f;

  /**
   * Entry point for the test,
   * @param args
   */
  public static void main(String[] args) {
    TestCameraMan app = new TestCameraMan();
    app.setConfigShowMode(ConfigShowMode.AlwaysShow);
    app.start();
  }

  protected void cleanup() {
    super.cleanup();
    tRenderer.cleanup();
  }

  protected void simpleRender() {
    lastRend += tpf;
    if (lastRend > throttle ) {
      tRenderer.render(model, fakeTex);
      lastRend = 0;
    }
  }

  /**
   * builds the trimesh.
   * @see com.jme.app.SimpleGame#initGame()
   */
  protected void simpleInitGame() {
      try {
        ResourceLocatorTool.addResourceLocator(
                    ResourceLocatorTool.TYPE_TEXTURE,
                    new SimpleResourceLocator(TestCameraMan.class
                            .getClassLoader().getResource(
                                    "jmetest/data/model/msascii/")));
    } catch (URISyntaxException e1) {
        logger.log(Level.WARNING, "unable to setup texture directory.", e1);
    }

    cam.setLocation(new Vector3f(0.0f, 50.0f, 100.0f));
    cam.update();

    tRenderer = display.createTextureRenderer(256, 256, TextureRenderer.Target.Texture2D);
    camNode = new CameraNode("Camera Node", tRenderer.getCamera());
    camNode.setLocalTranslation(new Vector3f(0, 50, -50));
    camNode.updateGeometricState(0, true);

    // Setup the input controller and timer
    input = new NodeHandler(camNode, 10, 1 );

    display.setTitle("Camera Man");

    DirectionalLight am = new DirectionalLight();
    am.setDiffuse(new ColorRGBA(0.0f, 1.0f, 0.0f, 1.0f));
    am.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
    am.setDirection(new Vector3f(1, 0, 0));
    am.setEnabled(true);

    SpotLight sl = new SpotLight();
    sl.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
    sl.setAmbient(new ColorRGBA(0.75f, 0.75f, 0.75f, 1.0f));
    sl.setDirection(new Vector3f(0, 0, 1));
    sl.setLocation(new Vector3f(0, 0, 0));
    sl.setAngle(25);
    sl.setEnabled(true);

    lightState.detachAll();
    lightState.attach(am);
    lightState.attach(sl);

    MilkToJme converter=new MilkToJme();
    URL MSFile=TestCameraMan.class.getClassLoader().getResource(
    "jmetest/data/model/msascii/run.ms3d");
    ByteArrayOutputStream BO=new ByteArrayOutputStream();

    try {
        converter.convert(MSFile.openStream(),BO);
    } catch (IOException e) {
        logger.info("IO problem writting the file!!!");
        logger.info(e.getMessage());
        System.exit(0);
    }
    
    model=null;
    try {
        model=(Node)BinaryImporter.getInstance().load(new ByteArrayInputStream(BO.toByteArray()));
    } catch (IOException e) {
        logger.info("darn exceptions:" + e.getMessage());
    }
    model.getController(0).setActive(false);

    rootNode.attachChild(model);

    CullState cs = display.getRenderer().createCullState();
    cs.setCullFace(CullState.Face.Back);
    cs.setEnabled(true);
    rootNode.setRenderState(cs);
    model.setRenderState(cs);

    LightNode cameraLight = new LightNode("Camera Light");
    cameraLight.setLight(sl);

    Node camBox;
    MilkToJme converter2=new MilkToJme();
    URL MSFile2=TestMilkJmeWrite.class.getClassLoader().getResource(
    "jmetest/data/model/msascii/camera.ms3d");
    ByteArrayOutputStream BO2=new ByteArrayOutputStream();

    try {
        converter2.convert(MSFile2.openStream(),BO2);
    } catch (IOException e) {
        logger.info("IO problem writting the file!!!");
        logger.info(e.getMessage());
        System.exit(0);
    }

    camBox=null;
    try {
        camBox=(Node)BinaryImporter.getInstance().load(new ByteArrayInputStream(BO2.toByteArray()));
    } catch (IOException e) {
        logger.info("darn exceptions:" + e.getMessage());
    }
    camNode.attachChild(camBox);
    camNode.attachChild(cameraLight);
    rootNode.attachChild(camNode);

    monitorNode = new Node("Monitor Node");
    Quad quad = new Quad("Monitor");
    quad.updateGeometry(3, 3);
    quad.setLocalTranslation(new Vector3f(3.75f, 52.5f, 90));
    monitorNode.attachChild(quad);

    Quad quad2 = new Quad("Monitor");
    quad2.updateGeometry(3.4f, 3.4f);
    quad2.setLocalTranslation(new Vector3f(3.95f, 52.6f, 89.5f));
    monitorNode.attachChild(quad2);

    // Setup our params for the depth buffer
    ZBufferState buf = display.getRenderer().createZBufferState();
    buf.setEnabled(true);
    buf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);

    monitorNode.setRenderState(buf);

    // Ok, now lets create the Texture object that our scene will be rendered to.
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
    monitorNode.setLightCombineMode(LightCombineMode.Off);
    rootNode.attachChild(monitorNode);
  }
}