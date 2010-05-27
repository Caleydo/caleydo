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

package jmetest.scene;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingSphere;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.shape.Sphere;

/**
 * @version $Id: TestRotateAboutPoint.java 4130 2009-03-19 20:04:51Z blaine.dev $
 */
public class TestRotateAboutPoint extends SimpleGame {
  private Quaternion rotQuat1 = new Quaternion();
  private Quaternion rotQuat2 = new Quaternion();
  
  private float angle = 0;
  private Vector3f axis = new Vector3f(0, 1, 0);
  private Sphere s;
  private Sphere moon1, moon2;
  private Node pivotNode1, pivotNode2;

  /**
   * Entry point for the test,
   * @param args
   */
  public static void main(String[] args) {
    TestRotateAboutPoint app = new TestRotateAboutPoint();
    app.setConfigShowMode(ConfigShowMode.AlwaysShow);
    app.start();
  }

  protected void simpleUpdate() {
    if (tpf < 1) {
      angle = angle + (tpf * 1);
      if (angle > 360) {
        angle = 0;
      }
    }
    rotQuat1.fromAngleAxis(angle, axis);
    pivotNode1.setLocalRotation(rotQuat1);
    
    rotQuat2.fromAngleAxis(angle * 2, axis);
    pivotNode2.setLocalRotation(rotQuat2);
    
  }

  /**
   * builds the trimesh.
   * @see com.jme.app.SimpleGame#initGame()
   */
  protected void simpleInitGame() {
    display.setTitle("jME - Rotation About a Point");

    //Planet
    s = new Sphere("Planet", 25, 25, 25);
    s.setModelBound(new BoundingSphere());
    s.updateModelBound();
    rootNode.attachChild(s);
    
    //Moons
    moon1 = new Sphere("Moon 1",25, 25, 10);
    moon1.setModelBound(new BoundingSphere());
    moon1.updateModelBound();
    moon1.setLocalTranslation(40, 0, 0);
    pivotNode1 = new Node("PivotNode 1");
    pivotNode1.attachChild(moon1);
    
    moon2 = new Sphere("Moon 2",25, 25, 7.5f);
    moon2.setModelBound(new BoundingSphere());
    moon2.updateModelBound();
    moon2.setLocalTranslation(60, 0, 0);
    pivotNode2 = new Node("PivotNode 2");
    pivotNode2.attachChild(moon2);
    
    rootNode.attachChild(pivotNode1);
    rootNode.attachChild(pivotNode2);
  }
}
