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

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.light.LightNode;
import com.jme.light.PointLight;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.BezierMesh;
import com.jme.scene.BezierPatch;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

/**
 * <code>TestLightState</code>
 * @author Mark Powell
 * @version $Id: TestBezierMesh.java 4130 2009-03-19 20:04:51Z blaine.dev $
 */
public class TestBezierMesh extends SimpleGame {
  private BezierMesh bez;
  private PointLight pl;
  private LightNode lightNode;
  private Vector3f currentPos;
  private Vector3f newPos;

  /**
   * Entry point for the test,
   * @param args
   */
  public static void main(String[] args) {
    TestBezierMesh app = new TestBezierMesh();
    app.setConfigShowMode(ConfigShowMode.AlwaysShow);
    app.start();

  }

  protected void simpleUpdate() {
    // update light pos
    if ( (int) currentPos.x == (int) newPos.x
        && (int) currentPos.y == (int) newPos.y
        && (int) currentPos.z == (int) newPos.z) {
      newPos.x = (float) Math.random() * 10 - 5;
      newPos.y = (float) Math.random() * 10 - 5;
      newPos.z = (float) Math.random() * 10 - 5;
    }

    currentPos.x -= (currentPos.x - newPos.x)
        / (timer.getFrameRate() / 2);
    currentPos.y -= (currentPos.y - newPos.y)
        / (timer.getFrameRate() / 2);
    currentPos.z -= (currentPos.z - newPos.z)
        / (timer.getFrameRate() / 2);

    lightNode.setLocalTranslation(currentPos);
  }

  protected void simpleInitGame() {
    display.setTitle("Bezier Mesh Test");
    cam.setLocation(new Vector3f(0, 0, 5));
    cam.update();
    currentPos = new Vector3f();
    newPos = new Vector3f();

    BezierPatch bp = new BezierPatch();
    bp.setAnchor(0, 0, new Vector3f( -0.75f, -0.75f, -0.5f));
    bp.setAnchor(0, 1, new Vector3f( -0.25f, -0.75f, 0.0f));
    bp.setAnchor(0, 2, new Vector3f(0.25f, -0.75f, 0.5f));
    bp.setAnchor(0, 3, new Vector3f(0.75f, -0.75f, 0.5f));
    bp.setAnchor(1, 0, new Vector3f( -0.75f, -0.25f, -0.75f));
    bp.setAnchor(1, 1, new Vector3f( -0.25f, -0.25f, 0.5f));
    bp.setAnchor(1, 2, new Vector3f(0.25f, -0.25f, 0.5f));
    bp.setAnchor(1, 3, new Vector3f(0.75f, -0.25f, 0.75f));
    bp.setAnchor(2, 0, new Vector3f( -0.75f, 0.25f, -0.5f));
    bp.setAnchor(2, 1, new Vector3f( -0.25f, 0.25f, -0.5f));
    bp.setAnchor(2, 2, new Vector3f(0.25f, 0.25f, -0.5f));
    bp.setAnchor(2, 3, new Vector3f(0.75f, 0.25f, 0.0f));
    bp.setAnchor(3, 0, new Vector3f( -0.75f, 0.75f, -0.5f));
    bp.setAnchor(3, 1, new Vector3f( -0.25f, 0.75f, -1.0f));
    bp.setAnchor(3, 2, new Vector3f(0.25f, 0.75f, -1.0f));
    bp.setAnchor(3, 3, new Vector3f(0.75f, 0.75f, -0.5f));
    bp.setDetailLevel(64);

    bez = new BezierMesh("Bezier Mesh");
    bez.setPatch(bp);
    Quaternion rot = new Quaternion();
    rot.fromAngleAxis(FastMath.PI, new Vector3f(0,0,1));
    bez.setLocalRotation(rot);
    bez.setModelBound(new BoundingSphere());
    bez.updateModelBound();
    rootNode.attachChild(bez);

    MaterialState ms = display.getRenderer().createMaterialState();
    ms.setEmissive(new ColorRGBA(0.1f, 0.1f, 0.1f, 1.0f));
//    ms.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
//    ms.setDiffuse(new ColorRGBA(1.0f, 0.85f, 0.75f, 1.0f));
//    ms.setSpecular(new ColorRGBA(0.8f, 0.8f, 0.8f, 1.0f));
    ms.setShininess(128.0f);
    ms.setEnabled(true);
    bez.setRenderState(ms);

    pl = new PointLight();
    pl.setAmbient(new ColorRGBA(0, 0, 0, 1));
    pl.setDiffuse(new ColorRGBA(1, 1, 1, 1));
    pl.setSpecular(new ColorRGBA(.2f, .2f, .2f, 1));
    pl.setEnabled(true);

    lightState.detachAll();
    lightState.attach(pl);
    
    lightState.setTwoSidedLighting(true);
    lightNode = new LightNode("Light Node");
    lightNode.setLight(pl);
    bez.setRenderState(lightState);

    Vector3f min = new Vector3f( -0.15f, -0.15f, -0.15f);
    Vector3f max = new Vector3f(0.15f, 0.15f, 0.15f);
    Box lightBox = new Box("box", min, max);
    lightBox.setLightCombineMode(Spatial.LightCombineMode.Off);
    lightBox.setModelBound(new BoundingSphere());
    lightBox.updateModelBound();

    lightNode.attachChild(lightBox);
    lightNode.setCullHint(Spatial.CullHint.Never);

    rootNode.attachChild(lightNode);

    TextureState ts = display.getRenderer().createTextureState();
    ts.setEnabled(true);
    ts.setTexture(
        TextureManager.loadTexture(
        TestBezierMesh.class.getClassLoader().getResource(
        "jmetest/data/images/Monkey.jpg"),
        Texture.MinificationFilter.BilinearNearestMipMap,
        Texture.MagnificationFilter.Bilinear));

    bez.setRenderState(ts);
  }
}
