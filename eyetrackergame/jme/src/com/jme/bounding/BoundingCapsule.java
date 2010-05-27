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
package com.jme.bounding;

import java.nio.FloatBuffer;

import com.jme.intersection.IntersectionRecord;
import com.jme.math.FastMath;
import com.jme.math.Line;
import com.jme.math.LineSegment;
import com.jme.math.Plane;
import com.jme.math.Quaternion;
import com.jme.math.Ray;
import com.jme.math.Triangle;
import com.jme.math.Vector3f;
import com.jme.math.Plane.Side;
import com.jme.scene.TriMesh;
import com.jme.util.geom.BufferUtils;

public class BoundingCapsule extends BoundingVolume {
    private static final long serialVersionUID = 1L;

    private transient LineSegment ls;

    private float radius;

    private static Vector3f compVec1 = new Vector3f();
    private static Vector3f compVec2 = new Vector3f();
    private static Vector3f compVec3 = new Vector3f();
    private static Vector3f compVec4 = new Vector3f();
    private static Vector3f diff = new Vector3f();
    private static Line compLine1 = new Line();

    public BoundingCapsule() {
        ls = new LineSegment();
    }

    public BoundingCapsule(Vector3f center, LineSegment ls, float radius) {
        this.center = center;
        this.ls = ls;
        this.radius = radius;
    }

    @Override
    public BoundingVolume clone(BoundingVolume store) {
        if (store != null && store.getType() == Type.Capsule) {
            BoundingCapsule capsule = (BoundingCapsule) store;
            capsule.getCenter().set(center);
            capsule.setRadius(radius);
            capsule.getLineSegment().set(ls);
            capsule.checkPlane = checkPlane;
            return capsule;
        }

        BoundingCapsule capsule = new BoundingCapsule(
                (Vector3f) center.clone(), (LineSegment) ls.clone(), radius);
        return capsule;
    }

    @Override
    public void computeFromPoints(FloatBuffer points) {
        leastSquaresFit(points);
    }

    /**
     * Least-Squares Fit will fit the points by a line using the least-squares
     * algorithm. Let the line be A + tW, with W unit length and A the average
     * of the data points. This line contains the capsule line segment. Compute
     * the radius to be the maximum distance from the data points to the line.
     * 
     * @param points
     */
    private void leastSquaresFit(FloatBuffer points) {
        if (points == null) {
            return;
        }

        points.rewind();
        if (points.remaining() <= 2) { // we need at least a 3 float vector
            return;
        }

        if (ls == null) {
            ls = new LineSegment();
        }

        int length = points.remaining() / 3;

        compLine1.orthogonalLineFit(points);

        float maxRadiusSquared = 0f;
        for (int i = 0; i < length; i++) {
            BufferUtils.populateFromBuffer(compVec1, points, i);
            float radiusSquared = compLine1.distanceSquared(compVec1);
            if (radiusSquared > maxRadiusSquared) {
                maxRadiusSquared = radiusSquared;
            }
        }

        Vector3f u = new Vector3f();
        Vector3f v = new Vector3f();
        Vector3f w = new Vector3f(compLine1.getDirection()).normalizeLocal();

        Vector3f.generateComplementBasis(u, v, w);
        float min = Float.POSITIVE_INFINITY;
        float max = Float.NEGATIVE_INFINITY;

        for (int i = 0; i < length; i++) {
            BufferUtils.populateFromBuffer(compVec1, points, i);
            compVec1.subtractLocal(compLine1.getOrigin());
            float uDot = u.dot(compVec1);
            float vDot = v.dot(compVec1);
            float wDot = w.dot(compVec1);
            float discrimenator = maxRadiusSquared
                    - (uDot * uDot + vDot * vDot);
            float radical = FastMath.sqrt(FastMath.abs(discrimenator));

            float testValue = wDot + radical;
            if (testValue < min) {
                min = testValue;
            }

            testValue = wDot - radical;
            if (testValue > max) {
                max = testValue;
            }
        }

        radius = FastMath.sqrt(maxRadiusSquared);
        ls.getOrigin().set(
                (compLine1.getOrigin().addLocal((compLine1.getDirection().mult(
                        ((min + max) * 0.5f), compVec1)))));
        center = ls.getOrigin();
        ls.getDirection().set(compLine1.getDirection());

        if (max > min) {
            // container is a capsule
            ls.setExtent((0.5f) * (max - min));
        } else {
            // container is a sphere
            ls.setExtent(0);
        }
    }

    @Override
    public void computeFromTris(int[] triIndex, TriMesh mesh, int start,
            int end) {
        // TODO Auto-generated method stub

    }

    @Override
    public void computeFromTris(Triangle[] tris, int start, int end) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean contains(Vector3f point) {
        return ls.distanceSquared(point) <= radius * radius;
    }

    public boolean contains(BoundingSphere sphere) {
        return contains(sphere.getRadius(), sphere.getCenter());
    }

    public boolean contains(float sphereRadius, Vector3f sphereCenter) {
        float diff = radius - sphereRadius;
        if (diff >= 0.0) {
            return ls.distanceSquared(sphereCenter) <= diff * diff;
        } else {
            return false;
        }
    }

    public boolean contains(BoundingCapsule capsule) {
        return contains(capsule.getRadius(), capsule.getLineSegment()
                .getNegativeEnd(compVec1), capsule.getLineSegment()
                .getPositiveEnd(compVec2));
    }

    public boolean contains(float radius, Vector3f start, Vector3f end) {
        return contains(radius, start) && contains(radius, end);
    }

    @Override
    public float distanceToEdge(Vector3f point) {
        // return lineSegment.distance(point) - radius;
        return 0;
    }

    @Override
    public Type getType() {
        return Type.Capsule;
    }

    @Override
    public boolean intersects(BoundingVolume bv) {
        if (bv == null) {
            return false;
        }

        return bv.intersectsCapsule(this);
    }

    @Override
    public boolean intersects(Ray ray) {
        if (!Vector3f.isValidVector(center)) return false;

        float squareDistance = ls.distanceSquared(ray);
        return squareDistance <= radius * radius;
    }

    @Override
    public boolean intersectsBoundingBox(BoundingBox bb) {
        if (!Vector3f.isValidVector(center) || !Vector3f.isValidVector(bb.center)) return false;

        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean intersectsCapsule(BoundingCapsule bc) {
        if (!Vector3f.isValidVector(center) || !Vector3f.isValidVector(bc.center)) return false;

        float distance = ls.distance(bc.getLineSegment());
        float radiusSum = radius + bc.getRadius();
        return distance <= radiusSum;
    }

    @Override
    public boolean intersectsOrientedBoundingBox(OrientedBoundingBox obb) {
        if (!Vector3f.isValidVector(center) || !Vector3f.isValidVector(obb.center)) return false;

        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean intersectsSphere(BoundingSphere bs) {
        if (!Vector3f.isValidVector(center) || !Vector3f.isValidVector(bs.center)) return false;

        // float distanceSqr = lineSegment.distanceSquared(bs.getCenter());
        // return distanceSqr <= radius * bs.getRadius();
        return false;
    }

    @Override
    public IntersectionRecord intersectsWhere(Ray ray) {
        // TODO Auto-generated method stub
        return new IntersectionRecord();
    }

    @Override
    public BoundingVolume merge(BoundingVolume volume) {

        switch (volume.getType()) {
            case Capsule:
                return mergeCapsule((BoundingCapsule) volume,
                        new BoundingCapsule());
            case Sphere:
                return mergeSphere((BoundingSphere) volume,
                        new BoundingCapsule());
            case AABB: {
                BoundingBox box = (BoundingBox) volume;
                Vector3f radVect = new Vector3f(box.xExtent, box.yExtent,
                        box.zExtent);
                Vector3f temp_center = box.center;
                BoundingSphere rVal = new BoundingSphere();
                rVal.setCenter(temp_center);
                rVal.setRadius(radVect.length());
                return mergeSphere(rVal, new BoundingCapsule());
            }
            case OBB: {
                OrientedBoundingBox box = (OrientedBoundingBox) volume;
                Vector3f temp_center = box.center;
                BoundingSphere rVal = new BoundingSphere();
                rVal.setCenter(temp_center);
                rVal.setRadius(0);
                rVal.mergeLocal(box);
                return mergeSphere(rVal, new BoundingCapsule());
            }
            default:
                return null;
        }
    }

    public BoundingCapsule mergeSphere(BoundingSphere sphere,
            BoundingCapsule merged) {
        if (contains(sphere)) {
            return this;
        }

        // if (capsule.contains(this)) {
        // return capsule;
        // }

        // axis center is average of input axis centers

        compLine1.getOrigin().set(
                ls.getOrigin().add(sphere.getCenter(), compVec1).mult(0.5f,
                        compVec1));

        compLine1.getDirection().set(ls.getDirection());
        compLine1.getDirection().normalizeLocal();

        // Cylinder with axis 'compLine1' must contain the spheres centered at
        // the end points of the input capsules.
        ls.getPositiveEnd(compVec1);
        float maxRadius = compLine1.distance(compVec1) + radius;

        ls.getNegativeEnd(compVec2);
        float temp = compLine1.distance(compVec2) + radius;
        if (temp > maxRadius) {
            maxRadius = temp;
        }

        temp = compLine1.distance(sphere.getCenter()) + sphere.getRadius();
        if (temp > maxRadius) {
            maxRadius = temp;
        }

        // process sphere <PosEnd0,r0>
        float radiusDiff = maxRadius - sphere.getRadius();
        float radiusDiffSqr = radiusDiff * radiusDiff;
        diff = compLine1.getOrigin().subtract(compVec1, diff);
        float lengthMinusDiff = diff.lengthSquared() - radiusDiffSqr;
        float dotDirection = diff.dot(compLine1.getDirection());
        float discr = dotDirection * dotDirection - lengthMinusDiff;
        float root = FastMath.sqrt(FastMath.abs(discr));
        float tPositive = -dotDirection - root;
        float tNegative = -dotDirection + root;

        // process sphere <NegEnd0,r0>
        diff = compLine1.getOrigin().subtract(compVec2, diff);
        lengthMinusDiff = diff.lengthSquared() - radiusDiffSqr;
        dotDirection = diff.dot(compLine1.getDirection());
        discr = dotDirection * dotDirection - lengthMinusDiff;
        root = FastMath.sqrt(FastMath.abs(discr));
        temp = -dotDirection - root;
        if (temp > tPositive) {
            tPositive = temp;
        }
        temp = -dotDirection + root;
        if (temp < tNegative) {
            tNegative = temp;
        }

        // process sphere <PosEnd1,r1>
        radiusDiff = maxRadius - sphere.getRadius();
        radiusDiffSqr = radiusDiff * radiusDiff;
        diff = compLine1.getOrigin().subtract(sphere.getCenter(), diff);
        lengthMinusDiff = diff.lengthSquared() - radiusDiffSqr;
        dotDirection = diff.dot(compLine1.getDirection());
        discr = dotDirection * dotDirection - lengthMinusDiff;
        root = FastMath.sqrt(FastMath.abs(discr));
        temp = -dotDirection - root;
        if (temp > tPositive) {
            tPositive = temp;
        }
        temp = -dotDirection + root;
        if (temp < tNegative) {
            tNegative = temp;
        }

        merged.setRadius(maxRadius);
        merged.getLineSegment().getOrigin().set(
                compLine1.getOrigin().add(
                        compLine1.getDirection().mult(
                                ((float) 0.5) * (tPositive + tNegative))));
        merged.getLineSegment().getDirection().set(compLine1.getDirection());

        if (tPositive > tNegative) {
            // container is a capsule
            merged.getLineSegment().setExtent((0.5f) * (tPositive - tNegative));
        } else {
            // container is a sphere
            merged.getLineSegment().setExtent(0.0f);
        }

        return merged;
    }

    public BoundingCapsule mergeCapsule(BoundingCapsule capsule,
            BoundingCapsule merged) {

        if (contains(capsule)) {
            return this;
        }

        if (capsule.contains(this)) {
            return capsule;
        }

        // axis center is average of input axis centers

        compLine1.getOrigin().set(
                ls.getOrigin().add(capsule.getLineSegment().getOrigin(),
                        compVec1).mult(0.5f, compVec1));

        // axis unit direction is average of input axis unit directions
        if (ls.getDirection().dot(capsule.getLineSegment().getDirection()) >= 0.0f) {
            compLine1.getDirection().set(
                    ls.getDirection().add(
                            capsule.getLineSegment().getDirection(), compVec1));
        } else {
            compLine1.getDirection().set(
                    ls.getDirection().subtract(
                            capsule.getLineSegment().getDirection(), compVec1));
        }

        compLine1.getDirection().normalizeLocal();

        // Cylinder with axis 'compLine1' must contain the spheres centered at
        // the end points of the input capsules.
        ls.getPositiveEnd(compVec1);
        float maxRadius = compLine1.distance(compVec1) + radius;

        ls.getNegativeEnd(compVec2);
        float temp = compLine1.distance(compVec2) + radius;
        if (temp > maxRadius) {
            maxRadius = temp;
        }

        capsule.getLineSegment().getPositiveEnd(compVec3);
        temp = compLine1.distance(compVec3) + capsule.getRadius();
        if (temp > maxRadius) {
            maxRadius = temp;
        }

        capsule.getLineSegment().getNegativeEnd(compVec4);
        temp = compLine1.distance(compVec4) + capsule.getRadius();
        if (temp > maxRadius) {
            maxRadius = temp;
        }

        // process sphere <PosEnd0,r0>
        float radiusDiff = maxRadius - capsule.getRadius();
        float radiusDiffSqr = radiusDiff * radiusDiff;
        diff = compLine1.getOrigin().subtract(compVec1, diff);
        float lengthMinusDiff = diff.lengthSquared() - radiusDiffSqr;
        float dotDirection = diff.dot(compLine1.getDirection());
        float discr = dotDirection * dotDirection - lengthMinusDiff;
        float root = FastMath.sqrt(FastMath.abs(discr));
        float tPositive = -dotDirection - root;
        float tNegative = -dotDirection + root;

        // process sphere <NegEnd0,r0>
        diff = compLine1.getOrigin().subtract(compVec2, diff);
        lengthMinusDiff = diff.lengthSquared() - radiusDiffSqr;
        dotDirection = diff.dot(compLine1.getDirection());
        discr = dotDirection * dotDirection - lengthMinusDiff;
        root = FastMath.sqrt(FastMath.abs(discr));
        temp = -dotDirection - root;
        if (temp > tPositive) {
            tPositive = temp;
        }
        temp = -dotDirection + root;
        if (temp < tNegative) {
            tNegative = temp;
        }

        // process sphere <PosEnd1,r1>
        radiusDiff = maxRadius - capsule.getRadius();
        radiusDiffSqr = radiusDiff * radiusDiff;
        diff = compLine1.getOrigin().subtract(compVec3, diff);
        lengthMinusDiff = diff.lengthSquared() - radiusDiffSqr;
        dotDirection = diff.dot(compLine1.getDirection());
        discr = dotDirection * dotDirection - lengthMinusDiff;
        root = FastMath.sqrt(FastMath.abs(discr));
        temp = -dotDirection - root;
        if (temp > tPositive) {
            tPositive = temp;
        }
        temp = -dotDirection + root;
        if (temp < tNegative) {
            tNegative = temp;
        }

        // process sphere <NegEnd1,r1>
        diff = compLine1.getOrigin().subtract(compVec4, diff);
        lengthMinusDiff = diff.lengthSquared() - radiusDiffSqr;
        dotDirection = diff.dot(compLine1.getDirection());
        discr = dotDirection * dotDirection - lengthMinusDiff;
        root = FastMath.sqrt(FastMath.abs(discr));
        temp = -dotDirection - root;
        if (temp > tPositive) {
            tPositive = temp;
        }
        temp = -dotDirection + root;
        if (temp < tNegative) {
            tNegative = temp;
        }

        merged.setRadius(maxRadius);
        merged.getLineSegment().getOrigin().set(
                compLine1.getOrigin().add(
                        compLine1.getDirection().mult(
                                ((float) 0.5) * (tPositive + tNegative))));
        merged.getLineSegment().getDirection().set(compLine1.getDirection());

        if (tPositive > tNegative) {
            // container is a capsule
            merged.getLineSegment().setExtent((0.5f) * (tPositive - tNegative));
        } else {
            // container is a sphere
            merged.getLineSegment().setExtent(0.0f);
        }

        return merged;
    }

    @Override
    public BoundingVolume mergeLocal(BoundingVolume volume) {
        if (volume == null) {
            return this;
        }

        switch (volume.getType()) {

            case Capsule: {
                return mergeCapsule((BoundingCapsule) volume, this);
            }

            case Sphere: {
                return mergeSphere((BoundingSphere) volume, this);
            }

            case AABB: {
                BoundingBox box = (BoundingBox) volume;
                Vector3f radVect = new Vector3f(box.xExtent, box.yExtent,
                        box.zExtent);
                Vector3f temp_center = box.center;
                BoundingSphere rVal = new BoundingSphere();
                rVal.setCenter(temp_center);
                rVal.setRadius(radVect.length());
                return mergeSphere(rVal, this);
            }

            default:
                return this;
        }

    }

    @Override
    public BoundingVolume transform(Quaternion rotate, Vector3f translate,
            Vector3f scale, BoundingVolume store) {
        BoundingCapsule capsule;
        if (store == null || store.getType() != Type.Capsule) {
            capsule = new BoundingCapsule();
            capsule.setLineSegment(new LineSegment());
        } else {
            capsule = (BoundingCapsule) store;
        }

        center.mult(scale, capsule.getCenter());
        rotate.mult(capsule.getCenter(), capsule.getCenter());
        capsule.getCenter().addLocal(translate);

        ls.getOrigin().mult(scale, capsule.getLineSegment().getOrigin());
        rotate.mult(capsule.getLineSegment().getOrigin(), capsule
                .getLineSegment().getOrigin());
        capsule.getLineSegment().getOrigin().addLocal(translate);

        capsule.getLineSegment().getDirection().set(ls.getDirection());
        rotate.mult(capsule.getLineSegment().getDirection(), capsule
                .getLineSegment().getDirection());

        ls.getDirection().mult(scale, compVec1).multLocal(ls.getExtent());
        capsule.getLineSegment().setExtent(compVec1.length());

        capsule.setRadius(FastMath.abs(getMaxAxis(scale) * radius));

        return capsule;
    }

    private float getMaxAxis(Vector3f scale) {
        float x = FastMath.abs(scale.x);
        float y = FastMath.abs(scale.y);
        float z = FastMath.abs(scale.z);

        if (x >= y) {
            if (x >= z)
                return x;
            return z;
        }

        if (y >= z)
            return y;

        return z;
    }

    @Override
    public Side whichSide(Plane plane) {
        float distance = plane.pseudoDistance(ls.getNegativeEnd(compVec1));
        if (distance <= -radius) {
            distance = plane.pseudoDistance(ls.getPositiveEnd(compVec1));
            if (distance <= -radius) { return Side.NEGATIVE; }
            if (distance >=  radius) { return Side.POSITIVE; }
            return Side.NONE;
        } else if (distance >= radius) {
            return Side.POSITIVE;
        } else {
            return Side.NONE;
        }
    }

    public LineSegment getLineSegment() {
        return ls;
    }

    public void setLineSegment(LineSegment lineSegment) {
        this.ls = lineSegment;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    @Override
    public float getVolume() {
        return (4 * FastMath.ONE_THIRD * FastMath.PI * radius * radius * radius)
                + (FastMath.PI * radius * radius *
                // FIXME: replace with 2 * line segment extents once that is
                // changed over.
                (getLineSegment().getOrigin().distance(getLineSegment()
                        .getDirection())));
    }
}
