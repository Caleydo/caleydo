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

package com.jme.curve;

import com.jme.intersection.CollisionResults;
import com.jme.intersection.PickResults;
import com.jme.math.Matrix3f;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jme.util.geom.BufferUtils;


public class PolylineCurve extends Curve {

  private Vector3f prev = new Vector3f();

  private Vector3f next = new Vector3f();

  private Vector3f temp = new Vector3f();

  private static final long serialVersionUID = -1650585082935112089L;

  private float[] percents;

  private final Vector3f[] controlPoints;

  private float totalLength;


  /**
   * Constructor instantiates a new <code>PolylineCurve</code> object. The control points that define the curve are
   * supplied.
   * 
   * @param name the name of the scene element. This is required for identification and comparision purposes.
   * @param controlPoints the points that define the curve.
   */
  public PolylineCurve(String name, Vector3f[] controlPoints) {
    super(name, controlPoints);
    this.controlPoints = controlPoints;

    float[] cummLengths = new float[controlPoints.length];
    percents = new float[controlPoints.length];

    // calculate length of curve up to control points
    for (int i = 1; i < controlPoints.length; i++) {
      cummLengths[i] = cummLengths[i - 1] + controlPoints[i].subtract(controlPoints[i - 1]).length();
    } // for

    // calculate place in percent on curve of all control points
    totalLength = cummLengths[cummLengths.length - 1];
    for (int i = 1; i < controlPoints.length; i++) {
      percents[i] = (1f / totalLength) * cummLengths[i];
    } // for
  } // PolylineCurve


  /**
   * <code>getOrientation</code> calculates the rotation matrix for any given point along to the line to still be facing
   * in the direction of the line.
   * 
   * @param time the current time (between 0 and 1)
   * @param precision how accurate to (i.e. the next time) to check against.
   * @return the rotation matrix.
   * @see com.jme.curve.Curve#getOrientation(float, float)
   */
  public Matrix3f getOrientation(float time, float precision) {
    Matrix3f rotation = new Matrix3f();

    // calculate tangent
    Vector3f point = getPoint(time);
    Vector3f tangent = point.subtract(getPoint(time + precision)).normalizeLocal();
    // calculate normal
    Vector3f tangent2 = getPoint(time - precision).subtract(point);
    Vector3f normal = tangent.cross(tangent2).normalizeLocal();
    // calculate binormal
    Vector3f binormal = tangent.cross(normal).normalizeLocal();
    // set columns
    rotation.setColumn(0, tangent);
    rotation.setColumn(1, normal);
    rotation.setColumn(2, binormal);
    return rotation;
  }


  /**
   * <code>getOrientation</code> calculates the rotation matrix for any given point along to the line to still be facing
   * in the direction of the line. A up vector is supplied, this keep the rotation matrix following the line, but
   * insures the object's up vector is not drastically changed.
   * 
   * @param time the current time (between 0 and 1)
   * @param precision how accurate to (i.e. the next time) to check against.
   * @return the rotation matrix.
   * @see com.jme.curve.Curve#getOrientation(float, float)
   */
  public Matrix3f getOrientation(float time, float precision, Vector3f up) {
    if (up == null) {
      return getOrientation(time, precision);
    }
    Matrix3f rotation = new Matrix3f();
    // calculate tangent
    Vector3f tangent = getPoint(time).subtract(getPoint(time + precision)).normalizeLocal();
    // calculate binormal
    Vector3f binormal = tangent.cross(up).normalizeLocal();
    // calculate normal
    Vector3f normal = binormal.cross(tangent).normalizeLocal();
    // set columns
    rotation.setColumn(0, tangent);
    rotation.setColumn(1, normal);
    rotation.setColumn(2, binormal);
    return rotation;
  }


  /*
   * (non-Javadoc)
   * 
   * @see com.jme.curve.Curve#getPoint(float)
   */
  @Override
  public Vector3f getPoint(float time) {
    return getPoint(time, new Vector3f());
  }


  @Override
  public Vector3f getPoint(float time, Vector3f store) {
    // first point
    if (time <= 0 || controlPoints.length <= 1) {
      BufferUtils.populateFromBuffer(store, getVertexBuffer(), 0);
      return store;
    } // if
    // last point.
    if (time >= 1) {
      BufferUtils.populateFromBuffer(store, getVertexBuffer(), getVertexCount() - 1);
      return store;
    } // if

    int i = 0;
    while (time > percents[i]) {
      i++;
    } // while

    // get the next and previous control point on the curve
    BufferUtils.populateFromBuffer(next, getVertexBuffer(), i);
    BufferUtils.populateFromBuffer(prev, getVertexBuffer(), i - 1);

    temp.set(next);
    temp.subtractLocal(prev);
    store.set(prev);
    store.addLocal(temp.normalizeLocal().multLocal(totalLength * (time - percents[i - 1])));

    return store;
  }


  @Override
  public void findCollisions(
          Spatial scene, CollisionResults results, int requiredOnBits) {
    // TODO Auto-generated method stub
  }


  @Override
  public boolean hasCollision(
          Spatial scene, boolean checkTriangles, int requiredOnBits) {
    // TODO Auto-generated method stub
    return false;
  }


  /*
   * (non-Javadoc)
   * 
   * @see com.jme.scene.Spatial#doPick(com.jme.math.Ray, com.jme.intersection.PickResults)
   */
  public void findPick(
          Ray toTest, PickResults results, int requiredOnBits) {
    // TODO Auto-generated method stub

  }

}
