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

import java.io.IOException;
import java.nio.FloatBuffer;

import com.jme.intersection.IntersectionRecord;
import com.jme.math.FastMath;
import com.jme.math.Matrix3f;
import com.jme.math.Plane;
import com.jme.math.Plane.Side;
import com.jme.math.Quaternion;
import com.jme.math.Ray;
import com.jme.math.Triangle;
import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.geom.BufferUtils;

/**
 * <code>BoundingBox</code> defines an axis-aligned cube that defines a
 * container for a group of vertices of a particular piece of geometry. This box
 * defines a center and extents from that center along the x, y and z axis. <br>
 * <br>
 * A typical usage is to allow the class define the center and radius by calling
 * either <code>containAABB</code> or <code>averagePoints</code>. A call to
 * <code>computeFramePoint</code> in turn calls <code>containAABB</code>.
 * 
 * @author Joshua Slack
 * @version $Id: BoundingBox.java 4746 2009-11-03 03:46:49Z blaine.dev $
 */
public class BoundingBox extends BoundingVolume {

    private static final long serialVersionUID = 2L;

    public float xExtent, yExtent, zExtent;

    private static final transient Matrix3f _compMat = new Matrix3f();
    private static final float[] fWdU = new float[3];
    private static final float[] fAWdU = new float[3];
    private static final float[] fDdU = new float[3];
    private static final float[] fADdU = new float[3];
    private static final float[] fAWxDdU = new float[3];


	private static final Vector3f[] verts = new Vector3f[3];
    /**
     * Default constructor instantiates a new <code>BoundingBox</code>
     * object.
     */
    public BoundingBox() {
    }

    /**
     * Contstructor instantiates a new <code>BoundingBox</code> object with
     * given specs.
     */
    public BoundingBox(Vector3f c, float x, float y, float z) {
        this.center.set(c);
        this.xExtent = x;
        this.yExtent = y;
        this.zExtent = z;
    }

    public Type getType() {
        return Type.AABB;
    }

    /**
     * <code>computeFromPoints</code> creates a new Bounding Box from a given
     * set of points. It uses the <code>containAABB</code> method as default.
     * 
     * @param points
     *            the points to contain.
     */
    public void computeFromPoints(FloatBuffer points) {
        containAABB(points);
    }

    /**
     * <code>computeFromTris</code> creates a new Bounding Box from a given
     * set of triangles. It is used in OBBTree calculations.
     * 
     * @param tris
     * @param start
     * @param end
     */
    public void computeFromTris(Triangle[] tris, int start, int end) {
        if (end - start <= 0) {
            return;
        }

        Vector3f min = _compVect1.set(new Vector3f(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY));
        Vector3f max = _compVect2.set(new Vector3f(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY));
        
        Vector3f point;
        for (int i = start; i < end; i++) {
            point = tris[i].get(0);
            checkMinMax(min, max, point);
            point = tris[i].get(1);
            checkMinMax(min, max, point);
            point = tris[i].get(2);
            checkMinMax(min, max, point);
        }
        
        center.set(min.addLocal(max));
        center.multLocal(0.5f);

        xExtent = max.x - center.x;
        yExtent = max.y - center.y;
        zExtent = max.z - center.z;
    }
    
    public void computeFromTris(int[] indices, TriMesh mesh, int start, int end) {
    	if (end - start <= 0) {
            return;
        }
    	
    	Vector3f min = _compVect1.set(new Vector3f(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY));
        Vector3f max = _compVect2.set(new Vector3f(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY));
        Vector3f point;
        
        for (int i = start; i < end; i++) {
        	mesh.getTriangle(indices[i], verts);
        	point = verts[0];
            checkMinMax(min, max, point);
            point = verts[1];
            checkMinMax(min, max, point);
            point = verts[2];
            checkMinMax(min, max, point);
        }
        
        center.set(min.addLocal(max));
        center.multLocal(0.5f);

        xExtent = max.x - center.x;
        yExtent = max.y - center.y;
        zExtent = max.z - center.z;
    }

    private void checkMinMax(Vector3f min, Vector3f max, Vector3f point) {
        if (point.x < min.x)
            min.x = point.x;
        else if (point.x > max.x)
            max.x = point.x;
        if (point.y < min.y)
            min.y = point.y;
        else if (point.y > max.y)
            max.y = point.y;
        if (point.z < min.z)
            min.z = point.z;
        else if (point.z > max.z)
            max.z = point.z;
    }

    /**
     * <code>containAABB</code> creates a minimum-volume axis-aligned bounding
     * box of the points, then selects the smallest enclosing sphere of the box
     * with the sphere centered at the boxes center.
     * 
     * @param points
     *            the list of points.
     */
    public void containAABB(FloatBuffer points) {
        if (points == null)
            return;

        points.rewind();
        if (points.remaining() <= 2) // we need at least a 3 float vector
            return;

        BufferUtils.populateFromBuffer(_compVect1, points, 0);
        float minX = _compVect1.x, minY = _compVect1.y, minZ = _compVect1.z;
        float maxX = _compVect1.x, maxY = _compVect1.y, maxZ = _compVect1.z;

        for (int i = 1, len = points.remaining() / 3; i < len; i++) {
            BufferUtils.populateFromBuffer(_compVect1, points, i);

            if (_compVect1.x < minX)
                minX = _compVect1.x;
            else if (_compVect1.x > maxX)
                maxX = _compVect1.x;

            if (_compVect1.y < minY)
                minY = _compVect1.y;
            else if (_compVect1.y > maxY)
                maxY = _compVect1.y;

            if (_compVect1.z < minZ)
                minZ = _compVect1.z;
            else if (_compVect1.z > maxZ)
                maxZ = _compVect1.z;
        }

        center.set(minX + maxX, minY + maxY, minZ + maxZ);
        center.multLocal(0.5f);

        xExtent = maxX - center.x;
        yExtent = maxY - center.y;
        zExtent = maxZ - center.z;
    }

    /**
     * <code>transform</code> modifies the center of the box to reflect the
     * change made via a rotation, translation and scale.
     * 
     * @param rotate
     *            the rotation change.
     * @param translate
     *            the translation change.
     * @param scale
     *            the size change.
     * @param store
     *            box to store result in
     */
    public BoundingVolume transform(Quaternion rotate, Vector3f translate,
            Vector3f scale, BoundingVolume store) {

        BoundingBox box;
        if (store == null || store.getType() != Type.AABB) {
            box = new BoundingBox();
        } else {
            box = (BoundingBox) store;
        }

        center.mult(scale, box.center);
        rotate.mult(box.center, box.center);
        box.center.addLocal(translate);

        Matrix3f transMatrix = _compMat;
        transMatrix.set(rotate);
        // Make the rotation matrix all positive to get the maximum x/y/z extent
        transMatrix.m00 = FastMath.abs(transMatrix.m00);
        transMatrix.m01 = FastMath.abs(transMatrix.m01);
        transMatrix.m02 = FastMath.abs(transMatrix.m02);
        transMatrix.m10 = FastMath.abs(transMatrix.m10);
        transMatrix.m11 = FastMath.abs(transMatrix.m11);
        transMatrix.m12 = FastMath.abs(transMatrix.m12);
        transMatrix.m20 = FastMath.abs(transMatrix.m20);
        transMatrix.m21 = FastMath.abs(transMatrix.m21);
        transMatrix.m22 = FastMath.abs(transMatrix.m22);

        _compVect1.set(xExtent * scale.x, yExtent * scale.y, zExtent * scale.z);
        transMatrix.mult(_compVect1, _compVect2);
        // Assign the biggest rotations after scales.
        box.xExtent = FastMath.abs(_compVect2.x);
        box.yExtent = FastMath.abs(_compVect2.y);
        box.zExtent = FastMath.abs(_compVect2.z);

        return box;
    }

    /**
     * <code>whichSide</code> takes a plane (typically provided by a view
     * frustum) to determine which side this bound is on.
     * 
     * @param plane
     *            the plane to check against.
     */
    public Side whichSide(Plane plane) {
        float radius = FastMath.abs(xExtent * plane.normal.x)
                + FastMath.abs(yExtent * plane.normal.y)
                + FastMath.abs(zExtent * plane.normal.z);

        float distance = plane.pseudoDistance(center);

        //changed to < and > to prevent floating point precision problems
        if (distance < -radius) { return Side.NEGATIVE; }
        if (distance >  radius) { return Side.POSITIVE; }
        return Side.NONE;
    }

    /**
     * <code>merge</code> combines this sphere with a second bounding sphere.
     * This new sphere contains both bounding spheres and is returned.
     * 
     * @param volume
     *            the sphere to combine with this sphere.
     * @return the new sphere
     */
    public BoundingVolume merge(BoundingVolume volume) {
        if (volume == null) {
            return this;
        }

        switch (volume.getType()) {
            case AABB: {
                BoundingBox vBox = (BoundingBox) volume;
                return merge(vBox.center, vBox.xExtent, vBox.yExtent,
                        vBox.zExtent, new BoundingBox(new Vector3f(0, 0, 0), 0,
                                0, 0));
            }

            case Sphere: {
                BoundingSphere vSphere = (BoundingSphere) volume;
                return merge(vSphere.center, vSphere.radius, vSphere.radius,
                        vSphere.radius, new BoundingBox(new Vector3f(0, 0, 0),
                                0, 0, 0));
            }
            
            //Treating Capsule like sphere, inefficient
            case Capsule: {
                BoundingCapsule capsule = (BoundingCapsule) volume;
                float totalRadius = capsule.getRadius() + capsule.getLineSegment().getExtent();
                return merge(capsule.center, totalRadius, totalRadius,
                        totalRadius, new BoundingBox(new Vector3f(0, 0, 0),
                                0, 0, 0));
            }

            case OBB: {
                OrientedBoundingBox box = (OrientedBoundingBox) volume;
                BoundingBox rVal = (BoundingBox) this.clone(null);
                return rVal.mergeOBB(box);
            }

            default:
                return null;
        }
    }

    /**
     * <code>mergeLocal</code> combines this sphere with a second bounding
     * sphere locally. Altering this sphere to contain both the original and the
     * additional sphere volumes;
     * 
     * @param volume
     *            the sphere to combine with this sphere.
     * @return this
     */
    public BoundingVolume mergeLocal(BoundingVolume volume) {
        if (volume == null) {
            return this;
        }

        switch (volume.getType()) {
            case AABB: {
                BoundingBox vBox = (BoundingBox) volume;
                return merge(vBox.center, vBox.xExtent, vBox.yExtent,
                        vBox.zExtent, this);
            }

            case Sphere: {
                BoundingSphere vSphere = (BoundingSphere) volume;
                return merge(vSphere.center, vSphere.radius, vSphere.radius,
                        vSphere.radius, this);
            }
            
            //Treating capsule like sphere, inefficient
            case Capsule: {
                BoundingCapsule capsule = (BoundingCapsule) volume;
                float totalRadius = capsule.getRadius() + capsule.getLineSegment().getExtent();
                return merge(capsule.center, totalRadius, totalRadius,
                		totalRadius, this);
            }

            case OBB: {
                return mergeOBB((OrientedBoundingBox) volume);
            }

            default:
                return null;
        }
    }

    /**
     * Merges this AABB with the given OBB.
     * 
     * @param volume
     *            the OBB to merge this AABB with.
     * @return This AABB extended to fit the given OBB.
     */
    private BoundingBox mergeOBB(OrientedBoundingBox volume) {
        if (!volume.correctCorners)
            volume.computeCorners();

        Vector3f min = _compVect1.set(center.x - xExtent, center.y - yExtent,
                center.z - zExtent);
        Vector3f max = _compVect2.set(center.x + xExtent, center.y + yExtent,
                center.z + zExtent);

        for (int i = 1; i < volume.vectorStore.length; i++) {
            Vector3f temp = volume.vectorStore[i];
            if (temp.x < min.x)
                min.x = temp.x;
            else if (temp.x > max.x)
                max.x = temp.x;

            if (temp.y < min.y)
                min.y = temp.y;
            else if (temp.y > max.y)
                max.y = temp.y;

            if (temp.z < min.z)
                min.z = temp.z;
            else if (temp.z > max.z)
                max.z = temp.z;
        }

        center.set(min.addLocal(max));
        center.multLocal(0.5f);

        xExtent = max.x - center.x;
        yExtent = max.y - center.y;
        zExtent = max.z - center.z;
        return this;
    }

    /**
     * <code>merge</code> combines this bounding box with another box which is
     * defined by the center, x, y, z extents.
     * 
     * @param boxCenter
     *            the center of the box to merge with
     * @param boxX
     *            the x extent of the box to merge with.
     * @param boxY
     *            the y extent of the box to merge with.
     * @param boxZ
     *            the z extent of the box to merge with.
     * @param rVal
     *            the resulting merged box.
     * @return the resulting merged box.
     */
    private BoundingBox merge(Vector3f boxCenter, float boxX, float boxY,
            float boxZ, BoundingBox rVal) {

        _compVect1.x = center.x - xExtent;
        if (_compVect1.x > boxCenter.x - boxX)
            _compVect1.x = boxCenter.x - boxX;
        _compVect1.y = center.y - yExtent;
        if (_compVect1.y > boxCenter.y - boxY)
            _compVect1.y = boxCenter.y - boxY;
        _compVect1.z = center.z - zExtent;
        if (_compVect1.z > boxCenter.z - boxZ)
            _compVect1.z = boxCenter.z - boxZ;

        _compVect2.x = center.x + xExtent;
        if (_compVect2.x < boxCenter.x + boxX)
            _compVect2.x = boxCenter.x + boxX;
        _compVect2.y = center.y + yExtent;
        if (_compVect2.y < boxCenter.y + boxY)
            _compVect2.y = boxCenter.y + boxY;
        _compVect2.z = center.z + zExtent;
        if (_compVect2.z < boxCenter.z + boxZ)
            _compVect2.z = boxCenter.z + boxZ;

        center.set(_compVect2).addLocal(_compVect1).multLocal(0.5f);

        xExtent = _compVect2.x - center.x;
        yExtent = _compVect2.y - center.y;
        zExtent = _compVect2.z - center.z;

        return rVal;
    }

    /**
     * <code>clone</code> creates a new BoundingBox object containing the same
     * data as this one.
     * 
     * @param store
     *            where to store the cloned information. if null or wrong class,
     *            a new store is created.
     * @return the new BoundingBox
     */
    public BoundingVolume clone(BoundingVolume store) {
        if (store != null && store.getType() == Type.AABB) {
            BoundingBox rVal = (BoundingBox) store;
            rVal.center.set(center);
            rVal.xExtent = xExtent;
            rVal.yExtent = yExtent;
            rVal.zExtent = zExtent;
            rVal.checkPlane = checkPlane;
            return rVal;
        }
        
        BoundingBox rVal = new BoundingBox(center.clone(),
                xExtent, yExtent, zExtent);
        return rVal;        
    }

    /**
     * <code>toString</code> returns the string representation of this object.
     * <p>
     * If you want to display a class name, then use
     * Vector3f.class.getName() or getClass().getName().
     * </p>
     * 
     * @return the string representation of this.
     */
    public String toString() {
        return "center=" + center + ", extents=("
                + xExtent + ", " + yExtent + ", " + zExtent + ')';
    }

    /**
     * intersects determines if this Bounding Box intersects with another given
     * bounding volume. If so, true is returned, otherwise, false is returned.
     * 
     * @see com.jme.bounding.BoundingVolume#intersects(com.jme.bounding.BoundingVolume)
     */
    public boolean intersects(BoundingVolume bv) {
        if (bv == null)
            return false;
       
        return bv.intersectsBoundingBox(this);
    }

    /**
     * determines if this bounding box intersects a given bounding sphere.
     * 
     * @see com.jme.bounding.BoundingVolume#intersectsSphere(com.jme.bounding.BoundingSphere)
     */
    public boolean intersectsSphere(BoundingSphere bs) {
        if (!Vector3f.isValidVector(center) || !Vector3f.isValidVector(bs.center)) return false;

        if (FastMath.abs(center.x - bs.getCenter().x) < bs.getRadius()
                + xExtent
                && FastMath.abs(center.y - bs.getCenter().y) < bs.getRadius()
                        + yExtent
                && FastMath.abs(center.z - bs.getCenter().z) < bs.getRadius()
                        + zExtent)
            return true;

        return false;
    }

    /**
     * determines if this bounding box intersects a given bounding box. If the
     * two boxes intersect in any way, true is returned. Otherwise, false is
     * returned.
     * 
     * @see com.jme.bounding.BoundingVolume#intersectsBoundingBox(com.jme.bounding.BoundingBox)
     */
    public boolean intersectsBoundingBox(BoundingBox bb) {
        if (!Vector3f.isValidVector(center) || !Vector3f.isValidVector(bb.center)) return false;

        if (center.x + xExtent < bb.center.x - bb.xExtent
                || center.x - xExtent > bb.center.x + bb.xExtent)
            return false;
        else if (center.y + yExtent < bb.center.y - bb.yExtent
                || center.y - yExtent > bb.center.y + bb.yExtent)
            return false;
        else if (center.z + zExtent < bb.center.z - bb.zExtent
                || center.z - zExtent > bb.center.z + bb.zExtent)
            return false;
        else
            return true;
    }

    /**
     * determines if this bounding box intersects with a given oriented bounding
     * box.
     * 
     * @see com.jme.bounding.BoundingVolume#intersectsOrientedBoundingBox(com.jme.bounding.OrientedBoundingBox)
     */
    public boolean intersectsOrientedBoundingBox(OrientedBoundingBox obb) {
        return obb.intersectsBoundingBox(this);
    }
    
    /**
     * determines if this bounding box intersects with a given bounding capsule.
     * 
     * @see com.jme.bounding.BoundingVolume#intersectsCapsule(BoundingCapsule)
     */
    public boolean intersectsCapsule(BoundingCapsule bc) {
    	return bc.intersectsBoundingBox(this);
    }

    /**
     * determines if this bounding box intersects with a given ray object. If an
     * intersection has occurred, true is returned, otherwise false is returned.
     * 
     * @see com.jme.bounding.BoundingVolume#intersects(com.jme.math.Ray)
     */
    public boolean intersects(Ray ray) {
        if (!Vector3f.isValidVector(center)) return false;

        float rhs;

        Vector3f diff = ray.origin.subtract(getCenter(_compVect2), _compVect1);

        fWdU[0] = ray.getDirection().dot(Vector3f.UNIT_X);
        fAWdU[0] = FastMath.abs(fWdU[0]);
        fDdU[0] = diff.dot(Vector3f.UNIT_X);
        fADdU[0] = FastMath.abs(fDdU[0]);
        if (fADdU[0] > xExtent && fDdU[0] * fWdU[0] >= 0.0) {
            return false;
        }

        fWdU[1] = ray.getDirection().dot(Vector3f.UNIT_Y);
        fAWdU[1] = FastMath.abs(fWdU[1]);
        fDdU[1] = diff.dot(Vector3f.UNIT_Y);
        fADdU[1] = FastMath.abs(fDdU[1]);
        if (fADdU[1] > yExtent && fDdU[1] * fWdU[1] >= 0.0) {
            return false;
        }

        fWdU[2] = ray.getDirection().dot(Vector3f.UNIT_Z);
        fAWdU[2] = FastMath.abs(fWdU[2]);
        fDdU[2] = diff.dot(Vector3f.UNIT_Z);
        fADdU[2] = FastMath.abs(fDdU[2]);
        if (fADdU[2] > zExtent && fDdU[2] * fWdU[2] >= 0.0) {
            return false;
        }

        Vector3f wCrossD = ray.getDirection().cross(diff, _compVect2);

        fAWxDdU[0] = FastMath.abs(wCrossD.dot(Vector3f.UNIT_X));
        rhs = yExtent * fAWdU[2] + zExtent * fAWdU[1];
        if (fAWxDdU[0] > rhs) {
            return false;
        }

        fAWxDdU[1] = FastMath.abs(wCrossD.dot(Vector3f.UNIT_Y));
        rhs = xExtent * fAWdU[2] + zExtent * fAWdU[0];
        if (fAWxDdU[1] > rhs) {
            return false;
        }

        fAWxDdU[2] = FastMath.abs(wCrossD.dot(Vector3f.UNIT_Z));
        rhs = xExtent * fAWdU[1] + yExtent * fAWdU[0];
        if (fAWxDdU[2] > rhs) {
            return false;

        }

        return true;
    }

    /**
     * @see com.jme.bounding.BoundingVolume#intersectsWhere(com.jme.math.Ray)
     */
    public IntersectionRecord intersectsWhere(Ray ray) {
        Vector3f diff = _compVect1.set(ray.origin).subtractLocal(center);
        Vector3f direction = _compVect2.set(ray.direction);

        float[] t = { 0f, Float.POSITIVE_INFINITY };
        
        float saveT0 = t[0], saveT1 = t[1];
        boolean notEntirelyClipped = clip(+direction.x, -diff.x - xExtent, t)
                && clip(-direction.x, +diff.x - xExtent, t)
                && clip(+direction.y, -diff.y - yExtent, t)
                && clip(-direction.y, +diff.y - yExtent, t)
                && clip(+direction.z, -diff.z - zExtent, t)
                && clip(-direction.z, +diff.z - zExtent, t);
        
        if (notEntirelyClipped && (t[0] != saveT0 || t[1] != saveT1)) {
            if (t[1] > t[0]) {
                float[] distances = t;
                Vector3f[] points = new Vector3f[] { 
                        new Vector3f(ray.direction).multLocal(distances[0]).addLocal(ray.origin),
                        new Vector3f(ray.direction).multLocal(distances[1]).addLocal(ray.origin)
                        };
                IntersectionRecord record = new IntersectionRecord(distances, points);
                return record;
            } 
                
            float[] distances = new float[] { t[0] };
            Vector3f[] points = new Vector3f[] { 
                    new Vector3f(ray.direction).multLocal(distances[0]).addLocal(ray.origin),
                    };
            IntersectionRecord record = new IntersectionRecord(distances, points);
            return record;            
        } 
        
        return new IntersectionRecord();        

    }

    @Override
    public boolean contains(Vector3f point) {
        return FastMath.abs(center.x - point.x) < xExtent
                && FastMath.abs(center.y - point.y) < yExtent
                && FastMath.abs(center.z - point.z) < zExtent;
    }

    public float distanceToEdge(Vector3f point) {
        // compute coordinates of point in box coordinate system
        Vector3f closest = point.subtract(center);

        // project test point onto box
        float sqrDistance = 0.0f;
        float delta;

        if (closest.x < -xExtent) {
            delta = closest.x + xExtent;
            sqrDistance += delta * delta;
            closest.x = -xExtent;
        } else if (closest.x > xExtent) {
            delta = closest.x - xExtent;
            sqrDistance += delta * delta;
            closest.x = xExtent;
        }

        if (closest.y < -yExtent) {
            delta = closest.y + yExtent;
            sqrDistance += delta * delta;
            closest.y = -yExtent;
        } else if (closest.y > yExtent) {
            delta = closest.y - yExtent;
            sqrDistance += delta * delta;
            closest.y = yExtent;
        }

        if (closest.z < -zExtent) {
            delta = closest.z + zExtent;
            sqrDistance += delta * delta;
            closest.z = -zExtent;
        } else if (closest.z > zExtent) {
            delta = closest.z - zExtent;
            sqrDistance += delta * delta;
            closest.z = zExtent;
        }

        return FastMath.sqrt(sqrDistance);
    }

    /**
     * <code>clip</code> determines if a line segment intersects the current
     * test plane.
     * 
     * @param denom
     *            the denominator of the line segment.
     * @param numer
     *            the numerator of the line segment.
     * @param t
     *            test values of the plane.
     * @return true if the line segment intersects the plane, false otherwise.
     */
    private boolean clip(float denom, float numer, float[] t) {
        // Return value is 'true' if line segment intersects the current test
        // plane. Otherwise 'false' is returned in which case the line segment
        // is entirely clipped.
        if (denom > 0.0f) {
            if (numer > denom * t[1])
                return false;
            if (numer > denom * t[0])
                t[0] = numer / denom;
            return true;
        } else if (denom < 0.0f) {
            if (numer > denom * t[0])
                return false;
            if (numer > denom * t[1])
                t[1] = numer / denom;
            return true;
        } else {
            return numer <= 0.0;
        }
    }

    /**
     * Query extent.
     * 
     * @param store
     *            where extent gets stored - null to return a new vector
     * @return store / new vector
     */
    public Vector3f getExtent(Vector3f store) {
        if (store == null) {
            store = new Vector3f();
        }
        store.set(xExtent, yExtent, zExtent);
        return store;
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(xExtent, "xExtent", 0);
        capsule.write(yExtent, "yExtent", 0);
        capsule.write(zExtent, "zExtent", 0);
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        xExtent = capsule.readFloat("xExtent", 0);
        yExtent = capsule.readFloat("yExtent", 0);
        zExtent = capsule.readFloat("zExtent", 0);
    }
    
    @Override
    public float getVolume() {
        return (8*xExtent*yExtent*zExtent);
    }
}

