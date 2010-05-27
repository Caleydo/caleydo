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

package jmetest.curve;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingSphere;
import com.jme.curve.CurveController;
import com.jme.curve.PolylineCurve;
import com.jme.image.Texture;
import com.jme.input.MouseInput;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.util.TextureManager;
import com.jme.util.geom.BufferUtils;

/**
 * <code>TestPolyCurve</code>
 * @author Mark Powell
 * @version $Id: TestBezierCurve.java 4130 2009-03-19 20:04:51Z blaine.dev $
 */
public class TestPolylineCurve extends SimpleGame {

  private Vector3f up = new Vector3f(0, 1, 0);

  public static void main(String[] args) {
    TestPolylineCurve app = new TestPolylineCurve();
    app.setConfigShowMode(ConfigShowMode.AlwaysShow);
    app.start();
  }

  /* (non-Javadoc)
   * @see com.jme.app.SimpleGame#initGame()
   */
  protected void simpleInitGame() {
    MouseInput.get().setCursorVisible(true);
    lightState.setEnabled(false); // by default for this demo
    display.setTitle("Polyline Curve Test");

    //create control Points
    Vector3f[] points = new Vector3f[4];
    points[0] = new Vector3f( -4, 0, 0);
    points[1] = new Vector3f( -2, 3, 2);
    points[2] = new Vector3f(2, -3, -2);
    points[3] = new Vector3f(4, 0, 0);

    PolylineCurve curve = new PolylineCurve("Curve", points);
    curve.setSteps(256);
    ColorRGBA[] colors = new ColorRGBA[4];
    colors[0] = new ColorRGBA(0, 1, 0, 1);
    colors[1] = new ColorRGBA(1, 0, 0, 1);
    colors[2] = new ColorRGBA(1, 1, 0, 1);
    colors[3] = new ColorRGBA(0, 0, 1, 1);
    curve.setColorBuffer(BufferUtils.createFloatBuffer(colors));

    Vector3f min = new Vector3f( -0.1f, -0.1f, -0.1f);
    Vector3f max = new Vector3f(0.1f, 0.1f, 0.1f);

    ZBufferState buf = display.getRenderer().createZBufferState();
    buf.setEnabled(true);
    buf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);

    TriMesh t = new Box("Control 1", min, max);
    t.setModelBound(new BoundingSphere());
    t.updateModelBound();

    t.setLocalTranslation(new Vector3f(points[0]));

    TriMesh t2 = new Box("Control 2", min, max);
    t2.setModelBound(new BoundingSphere());
    t2.updateModelBound();

    t2.setLocalTranslation(new Vector3f(points[1]));

    TriMesh t3 = new Box("Control 3", min, max);
    t3.setModelBound(new BoundingSphere());
    t3.updateModelBound();

    t3.setLocalTranslation(new Vector3f(points[2]));

    TriMesh t4 = new Box("Control 4", min, max);
    t4.setModelBound(new BoundingSphere());
    t4.updateModelBound();

    t4.setLocalTranslation(new Vector3f(points[3]));

    TriMesh box = new Box("Controlled Box", min.mult(5), max.mult(5));
    box.setModelBound(new BoundingSphere());
    box.updateModelBound();

    box.setLocalTranslation(new Vector3f(points[0]));

    CurveController cc = new CurveController(curve, box);
    box.addController(cc);
    cc.setRepeatType(Controller.RT_CYCLE);
    cc.setUpVector(up);
    cc.setSpeed(0.5f);

    TextureState ts = display.getRenderer().createTextureState();
    ts.setEnabled(true);
    ts.setTexture(
        TextureManager.loadTexture(
        TestPolylineCurve.class.getClassLoader().getResource(
        "jmetest/data/images/Monkey.jpg"),
        Texture.MinificationFilter.BilinearNearestMipMap,
        Texture.MagnificationFilter.Bilinear));
    box.setRenderState(ts);

    rootNode.setRenderState(buf);
    rootNode.attachChild(t);
    rootNode.attachChild(t2);
    rootNode.attachChild(t3);
    rootNode.attachChild(t4);
    rootNode.attachChild(box);
    rootNode.attachChild(curve);

  }
}
