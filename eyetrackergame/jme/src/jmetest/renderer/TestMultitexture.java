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
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

/**
 * <code>TestMultitexture</code>
 * @author Mark Powell
 * @version $Id: TestMultitexture.java 4130 2009-03-19 20:04:51Z blaine.dev $
 */
public class TestMultitexture extends SimpleGame {
    private static final Logger logger = Logger
            .getLogger(TestMultitexture.class.getName());
    
  private TriMesh t;
  private Quaternion rotQuat;
  private float angle = 0;
  private Vector3f axis;

  /**
   * Entry point for the test,
   * @param args
   */
  public static void main(String[] args) {
    TestMultitexture app = new TestMultitexture();
    app.setConfigShowMode(ConfigShowMode.AlwaysShow);
    app.start();
  }

  /**
   * @see com.jme.app.SimpleGame#update
   */
  protected void simpleUpdate() {
    if (timer.getTimePerFrame() < 1) {
      angle = angle + (timer.getTimePerFrame() * 25);
      if (angle > 360) {
        angle = 0;
      }
    }

    rotQuat.fromAngleAxis(angle*FastMath.DEG_TO_RAD, axis);
    t.setLocalRotation(rotQuat);
  }

  /**
   * builds the trimesh.
   * @see com.jme.app.SimpleGame#initGame()
   */
  protected void simpleInitGame() {

    rotQuat = new Quaternion();
    axis = new Vector3f(1, 1, 0.5f);

    display.setTitle("Multitexturing");
    cam.setLocation(new Vector3f(0, 0, 40));
    cam.update();

    Vector3f max = new Vector3f(5, 5, 5);
    Vector3f min = new Vector3f( -5, -5, -5);

    t = new Box("Box", min, max);
    t.setModelBound(new BoundingSphere());
    t.updateModelBound();

    t.setLocalTranslation(new Vector3f(0, 0, 0));

    rootNode.attachChild(t);

    TextureState ts = display.getRenderer().createTextureState();
    ts.setEnabled(true);
    Texture t1 = TextureManager.loadTexture(
        TestBoxColor.class.getClassLoader().getResource(
        "jmetest/data/images/Monkey.jpg"),
        Texture.MinificationFilter.BilinearNearestMipMap,
        Texture.MagnificationFilter.Bilinear);
    ts.setTexture(t1, 0);

    Texture t2 = TextureManager.loadTexture(TestBoxColor.class.getClassLoader().
                                            getResource("jmetest/data/texture/dirt.jpg"),
                                            Texture.MinificationFilter.BilinearNearestMipMap,
                                            Texture.MagnificationFilter.Bilinear);
    ts.setTexture(t2, 1);
    logger.info("This video card has a total of " + TextureState.getNumberOfTotalUnits() +
                       " texture units.");
    logger.info("This video card has a total of " + TextureState.getNumberOfFixedUnits() +
    " fixed function pipeline texture units.");
    logger.info("This video card has a total of " + TextureState.getNumberOfFragmentUnits() +
    " units available for fragment shaders.");
    logger.info("This video card has a total of " + TextureState.getNumberOfVertexUnits() +
    " units available for vertex shaders.");

    t.copyTextureCoordinates(0, 1, 1.0f);
    rootNode.setRenderState(ts);

  }
}
