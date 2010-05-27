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
import java.io.Serializable;
import java.nio.FloatBuffer;

import com.jme.intersection.IntersectionRecord;
import com.jme.math.Plane;
import com.jme.math.Quaternion;
import com.jme.math.Ray;
import com.jme.math.Triangle;
import com.jme.math.Vector3f;
import com.jme.math.Plane.Side;
import com.jme.scene.TriMesh;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.Savable;

/**
 * <code>BoundingVolume</code> defines an interface for dealing with
 * containment of a collection of points.
 * 
 * @author Mark Powell
 * @version $Id: BoundingVolume.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public abstract class BoundingVolume implements Serializable, Savable {
    private static final long serialVersionUID = 2L;
    
    public enum Type {
        Sphere, AABB, OBB, Capsule;
    }
	
	protected int checkPlane = 0;

    protected Vector3f center = new Vector3f();
	
	protected static final transient Vector3f _compVect1 = new Vector3f();
	protected static final transient Vector3f _compVect2 = new Vector3f();
	protected static final transient Vector3f _compVect3 = new Vector3f();

	public BoundingVolume() {
    }

	public BoundingVolume(Vector3f center) {
	    this.center.set(center);
    }

    /**
	 * Grabs the checkplane we should check first.
	 * 
	 */
	public int getCheckPlane() {
	    return checkPlane;
	}

	/**
	 * Sets the index of the plane that should be first checked during rendering.
	 * 
	 * @param value
	 */
	public final void setCheckPlane(int value) {
	    checkPlane = value;
	}
	
	/**
	 * getType returns the type of bounding volume this is.
	 */
	public abstract Type getType();

	/**
	 * 
	 * <code>transform</code> alters the location of the bounding volume by a
	 * rotation, translation and a scalar.
	 * 
	 * @param rotate
	 *            the rotation to affect the bound.
	 * @param translate
	 *            the translation to affect the bound.
	 * @param scale
	 *            the scale to resize the bound.
	 * @return the new bounding volume.
	 */
    public final BoundingVolume transform(Quaternion rotate, Vector3f translate, Vector3f scale) {
        return transform(rotate, translate, scale, null);
    }

	/**
	 * 
	 * <code>transform</code> alters the location of the bounding volume by a
	 * rotation, translation and a scalar.
	 * 
	 * @param rotate
	 *            the rotation to affect the bound.
	 * @param translate
	 *            the translation to affect the bound.
	 * @param scale
	 *            the scale to resize the bound.
	 * @param store
	 *            sphere to store result in
	 * @return the new bounding volume.
	 */
	public abstract BoundingVolume transform(Quaternion rotate, Vector3f translate, Vector3f scale, BoundingVolume store);

	/**
	 * 
	 * <code>whichSide</code> returns the side on which the bounding volume
	 * lies on a plane. Possible values are POSITIVE_SIDE, NEGATIVE_SIDE, and
	 * NO_SIDE.
	 * 
	 * @param plane
	 *            the plane to check against this bounding volume.
	 * @return the side on which this bounding volume lies.
	 */
	public abstract Side whichSide(Plane plane);

	/**
	 * 
	 * <code>computeFromPoints</code> generates a bounding volume that
	 * encompasses a collection of points.
	 * 
	 * @param points
	 *            the points to contain.
	 */
	public abstract void computeFromPoints(FloatBuffer points);

	/**
	 * <code>merge</code> combines two bounding volumes into a single bounding
	 * volume that contains both this bounding volume and the parameter volume.
	 * 
	 * @param volume
	 *            the volume to combine.
	 * @return the new merged bounding volume.
	 */
	public abstract BoundingVolume merge(BoundingVolume volume);

	/**
	 * <code>mergeLocal</code> combines two bounding volumes into a single
	 * bounding volume that contains both this bounding volume and the parameter
	 * volume. The result is stored locally.
	 * 
	 * @param volume
	 *            the volume to combine.
	 * @return this
	 */
	public abstract BoundingVolume mergeLocal(BoundingVolume volume);

	/**
	 * <code>clone</code> creates a new BoundingVolume object containing the
	 * same data as this one.
	 * 
	 * @param store
	 *            where to store the cloned information. if null or wrong class,
	 *            a new store is created.
	 * @return the new BoundingVolume
	 */
	public abstract BoundingVolume clone(BoundingVolume store);

	
	public final Vector3f getCenter() {
        return center;
    }
	
	public final Vector3f getCenter(Vector3f store) {
        store.set(center);
        return store;
    }

	public final void setCenter(Vector3f newCenter) {
        center = newCenter;
    }

    /**
     * Find the distance from the center of this Bounding Volume to the given
     * point.
     * 
     * @param point
     *            The point to get the distance to
     * @return distance
     */
    public final float distanceTo(Vector3f point) {
        return center.distance(point);
    }

    /**
     * Find the squared distance from the center of this Bounding Volume to the
     * given point.
     * 
     * @param point
     *            The point to get the distance to
     * @return distance
     */
    public final float distanceSquaredTo(Vector3f point) {
        return center.distanceSquared(point);
    }

    /**
     * Find the distance from the nearest edge of this Bounding Volume to the given
     * point.
     * 
     * @param point
     *            The point to get the distance to
     * @return distance
     */
    public abstract float distanceToEdge(Vector3f point);
    
	/**
	 * determines if this bounding volume and a second given volume are
	 * intersecting. Intersecting being: one volume contains another, one volume
	 * overlaps another or one volume touches another.
	 * 
	 * @param bv
	 *            the second volume to test against.
	 * @return true if this volume intersects the given volume.
	 */
	public abstract boolean intersects(BoundingVolume bv);

	/**
	 * determines if a ray intersects this bounding volume.
	 * 
	 * @param ray
	 *            the ray to test.
	 * @return true if this volume is intersected by a given ray.
	 */
	public abstract boolean intersects(Ray ray);

    /**
     * determines if a ray intersects this bounding volume and if so, where.
     * 
     * @param ray
     *            the ray to test.
     * @return an IntersectionRecord containing information about any
     *         intersections made by the given Ray with this bounding
     */
    public abstract IntersectionRecord intersectsWhere(Ray ray);

	/**
	 * determines if this bounding volume and a given bounding sphere are
	 * intersecting.
	 * 
	 * @param bs
	 *            the bounding sphere to test against.
	 * @return true if this volume intersects the given bounding sphere.
	 */
	public abstract boolean intersectsSphere(BoundingSphere bs);

	/**
	 * determines if this bounding volume and a given bounding box are
	 * intersecting.
	 * 
	 * @param bb
	 *            the bounding box to test against.
	 * @return true if this volume intersects the given bounding box.
	 */
	public abstract boolean intersectsBoundingBox(BoundingBox bb);

	/**
	 * determines if this bounding volume and a given bounding box are
	 * intersecting.
	 * 
	 * @param bb
	 *            the bounding box to test against.
	 * @return true if this volume intersects the given bounding box.
	 */
	public abstract boolean intersectsOrientedBoundingBox(OrientedBoundingBox bb);
	
	/**
	 * determins if this bounding volume and a given bounding capsule are
	 * intersecting.
	 * @param bc the bounding capsule to test against.
	 * @return true if this volume instersects the given bounding capsule.
	 */
	public abstract boolean intersectsCapsule(BoundingCapsule bc);
    
    /**
     * 
     * determines if a given point is contained within this bounding volume.
     * 
     * @param point
     *            the point to check
     * @return true if the point lies within this bounding volume.
     */
    public abstract boolean contains(Vector3f point);
    
    
    public void write(JMEExporter e) throws IOException {
        e.getCapsule(this).write(center, "center", Vector3f.ZERO);
    }
    
    public void read(JMEImporter e) throws IOException {
        center = (Vector3f)e.getCapsule(this).readSavable("center", Vector3f.ZERO.clone());
    }
    
    public Class getClassTag() {
        return this.getClass();
    }

	public abstract void computeFromTris(int[] triIndex, TriMesh mesh, int start, int end);
	public abstract void computeFromTris(Triangle[] tris, int start, int end);
    public abstract float getVolume();
}

