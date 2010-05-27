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
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.DistanceSwitchModel;
import com.jme.scene.lod.DiscreteLodNode;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

/**
 * <code>TestDiscreteLOD</code>
 * @author Mark Powell
 * @version $Id: TestDiscreteLOD.java 4130 2009-03-19 20:04:51Z blaine.dev $
 */
public class TestDiscreteLOD extends SimpleGame {

  private Quaternion rotQuat = new Quaternion();
  private float angle = 0;
  private Vector3f axis = new Vector3f(1, 1, 0);
  private DiscreteLodNode dlod;

  /**
   * Entry point for the test,
   * @param args
   */
  public static void main(String[] args) {
    TestDiscreteLOD app = new TestDiscreteLOD();
    app.setConfigShowMode(ConfigShowMode.AlwaysShow);
    app.start();
  }

  protected void simpleUpdate() {
    if (timer.getTimePerFrame() < 1) {
      angle = angle + (timer.getTimePerFrame() * 10);
      if (angle > 360)
        angle = 0;
    }

    rotQuat.fromAngleAxis(angle * FastMath.DEG_TO_RAD, axis);

    dlod.setLocalRotation(rotQuat);
  }

  /**
   * builds the trimesh.
   * @see com.jme.app.SimpleGame#initGame()
   */
  protected void simpleInitGame() {
    display.setTitle("Discrete Level of Detail Test");
    cam.setLocation(new Vector3f(0, 0, 50));
    cam.update();

    Sphere s1 = new Sphere("Sphere", 100, 100, 25);
    s1.setModelBound(new BoundingBox());
    s1.updateModelBound();

    Sphere s2 = new Sphere("Sphere", 50, 50, 25);
    s2.setModelBound(new BoundingBox());
    s2.updateModelBound();

    Sphere s3 = new Sphere("Sphere", 30, 20, 25);
    s3.setModelBound(new BoundingBox());
    s3.updateModelBound();

    Sphere s4 = new Sphere("Sphere", 10, 10, 25);
    s4.setModelBound(new BoundingBox());
    s4.updateModelBound();

    DistanceSwitchModel m = new DistanceSwitchModel(4);
    m.setModelDistance(0, 0, 100);
    m.setModelDistance(1, 100, 200);
    m.setModelDistance(2, 200, 300);
    m.setModelDistance(3, 300, 1000);

    dlod = new DiscreteLodNode("DLOD", m);
    dlod.attachChild(s1);
    dlod.attachChild(s2);
    dlod.attachChild(s3);
    dlod.attachChild(s4);
    dlod.setActiveChild(0);
    rootNode.attachChild(dlod);

    TextureState ts = display.getRenderer().createTextureState();
    ts.setEnabled(true);
    ts.setTexture(
        TextureManager.loadTexture(
        TestBoxColor.class.getClassLoader().getResource(
        "jmetest/data/images/Monkey.jpg"),
        Texture.MinificationFilter.Trilinear,
        Texture.MagnificationFilter.Bilinear));

    rootNode.setRenderState(ts);

  }
}
