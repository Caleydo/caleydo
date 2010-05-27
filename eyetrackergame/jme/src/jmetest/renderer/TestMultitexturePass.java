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

import com.jme.app.SimplePassGame;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.pass.RenderPass;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

/**
 * <code>TestLightState</code>
 * @author Mark Powell
 * @version $Id: TestMultitexturePass.java 4130 2009-03-19 20:04:51Z blaine.dev $
 */
public class TestMultitexturePass extends SimplePassGame {
  private TriMesh t;
  private Quaternion rotQuat;
  private float angle = 0;
  private Vector3f axis;

  /**
   * Entry point for the test,
   * @param args
   */
  public static void main(String[] args) {
    TestMultitexturePass app = new TestMultitexturePass();
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
   * @see com.jme.app.BaseGame#initGame()
   */
  protected void simpleInitGame() {

    rotQuat = new Quaternion();
    axis = new Vector3f(1, 1, 0.5f);

    display.setTitle("Multitexturing - Multiple Passes");
    cam.setLocation(new Vector3f(0, 0, 40));
    cam.update();

    Vector3f max = new Vector3f(5, 5, 5);
    Vector3f min = new Vector3f( -5, -5, -5);

    t = new Box("Box", min, max);
    t.setLocalTranslation(new Vector3f(0, 0, 0));
    t.setModelBound(new BoundingSphere());
    t.updateModelBound();
    
    t.copyTextureCoordinates(0, 1, 1.0f);

    //attach to rootNode to get updates supplied by SimplePassGame
    rootNode.attachChild(t);

    TextureState ts1 = display.getRenderer().createTextureState();
    Texture t1 = TextureManager.loadTexture(
                TestMultitexturePass.class.getClassLoader().getResource(
                        "jmetest/data/texture/dirt.jpg"), Texture.MinificationFilter.BilinearNearestMipMap,
                Texture.MagnificationFilter.Bilinear);
    ts1.setTexture(t1);

    TextureState ts2 = display.getRenderer().createTextureState();
    Texture t2 = TextureManager.loadTexture(
            TestMultitexturePass.class.getClassLoader().getResource(
                    "jmetest/data/images/Monkey.jpg"), Texture.MinificationFilter.BilinearNearestMipMap,
            Texture.MagnificationFilter.Bilinear);
    t2.setWrap(Texture.WrapMode.Repeat);
    ts2.setTexture(t2);
    ts2.setTextureCoordinateOffset(1);
    
    BlendState as = display.getRenderer().createBlendState();
    as.setBlendEnabled(true);
    as.setSourceFunction(BlendState.SourceFunction.DestinationColor);
    as.setDestinationFunction(BlendState.DestinationFunction.SourceColor);

    RenderPass rp1 = new RenderPass();
    rp1.setPassState(ts1);
    rp1.add(t);
    
    RenderPass rp2 = new RenderPass();
    rp2.setPassState(ts2);
    rp2.setPassState(as);
    rp2.setZFactor(0f);
    rp2.setZOffset(-5f);
    rp2.add(t);
    
    RenderPass rp3 = new RenderPass();
    rp3.add(statNode);
    
    pManager.add(rp1);
    pManager.add(rp2);
    pManager.add(rp3);
  }
}
