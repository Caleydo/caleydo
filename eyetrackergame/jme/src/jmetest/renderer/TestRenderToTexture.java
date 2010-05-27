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

import java.util.logging.Logger;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.image.Texture2D;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.renderer.TextureRenderer;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.util.TextureManager;

/**
 * <code>TestRenderToTexture</code>
 * @author Joshua Slack
 * @version $Id: TestRenderToTexture.java 4729 2009-10-22 07:07:29Z andreas.grabner@gmail.com $
 */
public class TestRenderToTexture extends SimpleGame {
    private static final Logger logger = Logger
            .getLogger(TestRenderToTexture.class.getName());
    
  private Box realBox, monkeyBox;
  private Node fakeScene;
  private Quaternion rotQuat = new Quaternion();
  private Quaternion rotMBQuat = new Quaternion();
  private Vector3f axis = new Vector3f(1, 1, 0.5f);
  private float angle = 0;
  private float angle2 = 0;

  private TextureRenderer tRenderer;
  private Texture2D fakeTex;

  /**
   * Entry point for the test,
   * @param args
   */
  public static void main(String[] args) {
    TestRenderToTexture app = new TestRenderToTexture();
    app.setConfigShowMode(ConfigShowMode.AlwaysShow);
    app.start();
  }

  protected void cleanup() {
    tRenderer.cleanup();
    super.cleanup();
  }

  protected void simpleUpdate() {
    if (tpf < 1) {
      angle = angle + (tpf * -.25f);
      angle2 = angle2 + (tpf * 1);
      if (angle < 0) {
        angle = 360 - .25f;
      }
      if (angle2 >= 360) {
        angle2 = 0;
      }
    }
    rotQuat.fromAngleAxis(angle, axis);
    rotMBQuat.fromAngleAxis(angle2, axis);

    realBox.setLocalRotation(rotQuat);
    monkeyBox.setLocalRotation(rotMBQuat);
    fakeScene.updateGeometricState(0.0f, true);
  }

  protected void simpleRender() {
      tRenderer.render(fakeScene, fakeTex);
  }

  /**
   * builds the trimesh.
   * @see com.jme.app.SimpleGame#initGame()
   */
  protected void simpleInitGame() {
    display.setTitle("Render to Texture");
    cam.setLocation(new Vector3f(0, 0, 25));
    cam.update();

    // Setup dimensions for a box
    Vector3f max = new Vector3f(5, 5, 5);
    Vector3f min = new Vector3f( -5, -5, -5);

    // Make the real world box -- you'll see this spinning around..  woo...
    realBox = new Box("Box", min, max);
    realBox.setModelBound(new BoundingSphere());
    realBox.updateModelBound();
    realBox.setLocalTranslation(new Vector3f(0, 0, 0));
    //FIX ME: if the box is put into a queue the texture rendering has to be done before the scene rendering!
    //realBox.setRenderQueueMode(Renderer.QUEUE_OPAQUE);

    rootNode.attachChild(realBox);

    // Make a monkey box -- some geometry that will be rendered onto a flat texture.
    // First, we'd like the box to be bigger, so...
    min.multLocal(3);
    max.multLocal(3);
    monkeyBox = new Box("Fake Monkey Box", min, max);
    monkeyBox.setModelBound(new BoundingSphere());
    monkeyBox.updateModelBound();
    monkeyBox.setLocalTranslation(new Vector3f(0, 0, 0));

    // add the monkey box to a node.  This node is a root node, not part of the "real world" tree.
    fakeScene = new Node("Fake node");
    fakeScene.setRenderQueueMode(Renderer.QUEUE_SKIP);
    fakeScene.attachChild(monkeyBox);

    // Setup our params for the depth buffer
    ZBufferState buf = display.getRenderer().createZBufferState();
    buf.setEnabled(true);
    buf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
    fakeScene.setRenderState(buf);

    // Lets add a monkey texture to the geometry we are going to rendertotexture...
    TextureState ts = display.getRenderer().createTextureState();
    ts.setEnabled(true);
    Texture tex = TextureManager.loadTexture(
        TestRenderToTexture.class.getClassLoader().getResource(
        "jmetest/data/images/Monkey.jpg"),
        Texture.MinificationFilter.Trilinear,
        Texture.MagnificationFilter.Bilinear);
    ts.setTexture(tex);
    fakeScene.setRenderState(ts);

    // Ok, now lets create the Texture object that our monkey cube will be rendered to.
//    tRenderer = display.createTextureRenderer(512, 512, 0, TextureRenderer.Target.Texture2D);
    tRenderer = display.createTextureRenderer(512, 512, 4, TextureRenderer.Target.Texture2D);
    tRenderer.setBackgroundColor(new ColorRGBA(.667f, .667f, .851f, 1f));
    fakeTex = new Texture2D();
    fakeTex.setWrap(Texture.WrapMode.Clamp);
    if ( tRenderer.isSupported() ) {
        tRenderer.setupTexture(fakeTex);
        tRenderer.getCamera().setLocation(new Vector3f(0, 0, 75f));
    } else {
        logger.severe("Render to texture not supported!");
    }

    // Now add that texture to the "real" cube.
    ts = display.getRenderer().createTextureState();
    ts.setEnabled(true);
    ts.setTexture(fakeTex, 0);

    // Heck, while we're at it, why not add another texture to blend with.
    Texture tex2 = TextureManager.loadTexture(
        TestRenderToTexture.class.getClassLoader().getResource(
        "jmetest/data/texture/dirt.jpg"),
        Texture.MinificationFilter.Trilinear,
        Texture.MagnificationFilter.Bilinear);
    ts.setTexture(tex2, 1);
    rootNode.setRenderState(ts);

    // Since we have 2 textures, the geometry needs to know how to split up the coords for the second state.
    realBox.copyTextureCoordinates(0, 1, 1.0f);
    
    fakeScene.updateGeometricState(0.0f, true);
    fakeScene.updateRenderState();
  }
}
