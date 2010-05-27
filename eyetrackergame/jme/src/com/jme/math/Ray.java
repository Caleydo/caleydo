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

package com.jme.math;

import java.io.IOException;
import java.io.Serializable;

import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * <code>Ray</code> defines a line segment which has an origin and a direction.
 * That is, a point and an infinite ray is cast from this point. The ray is
 * defined by the following equation: R(t) = origin + t*direction for t >= 0.
 * 
 * @author Mark Powell
 * @author Joshua Slack
 */
public class Ray  implements Serializable, Savable, Cloneable {

    //todo: merge with Line?
    private static final long serialVersionUID = 1L;

    /** The ray's begining point. */
    public Vector3f origin;
    /** The direction of the ray. */
    public Vector3f direction;
    
    protected static final Vector3f tempVa=new Vector3f();
    protected static final Vector3f tempVb=new Vector3f();
    protected static final Vector3f tempVc=new Vector3f();
    protected static final Vector3f tempVd=new Vector3f();

    /**
     * Constructor instantiates a new <code>Ray</code> object. As default, the
     * origin is (0,0,0) and the direction is (0,0,0).
     *
     */
    public Ray() {
        origin = new Vector3f();
        direction = new Vector3f();
    }

    /**
     * Constructor instantiates a new <code>Ray</code> object. The origin and
     * direction are given.
     * @param origin the origin of the ray.
     * @param direction the direction the ray travels in.
     */
    public Ray(Vector3f origin, Vector3f direction) {
        this.origin = origin;
        this.direction = direction;
    }

    /**
     * <code>intersect</code> determines if the Ray intersects a triangle.
     * @param t the Triangle to test against.
     * @return true if the ray collides.
     */
    public boolean intersect(Triangle t) {
        return intersect(t.get(0), t.get(1), t.get(2));
    }

    /**
     * <code>intersect</code> determines if the Ray intersects a triangle
     * defined by the specified points.
     *
     * @param v0
     *            first point of the triangle.
     * @param v1
     *            second point of the triangle.
     * @param v2
     *            third point of the triangle.
     * @return true if the ray collides.
     */
    public boolean intersect(Vector3f v0,Vector3f v1,Vector3f v2){
        return intersectWhere(v0, v1, v2, null);
    }

    /**
     * <code>intersectWhere</code> determines if the Ray intersects a triangle. It then
     * stores the point of intersection in the given loc vector
     * @param t the Triangle to test against.
     * @param loc
     *            storage vector to save the collision point in (if the ray
     *            collides)
     * @return true if the ray collides.
     */
    public boolean intersectWhere(Triangle t, Vector3f loc) {
        return intersectWhere(t.get(0), t.get(1), t.get(2), loc);
    }

    /**
     * <code>intersectWhere</code> determines if the Ray intersects a triangle
     * defined by the specified points and if so it stores the point of
     * intersection in the given loc vector.
     *
     * @param v0
     *            first point of the triangle.
     * @param v1
     *            second point of the triangle.
     * @param v2
     *            third point of the triangle.
     * @param loc
     *            storage vector to save the collision point in (if the ray
     *            collides)  if null, only boolean is calculated.
     * @return true if the ray collides.
     */
    public boolean intersectWhere(Vector3f v0, Vector3f v1, Vector3f v2,
                                  Vector3f loc) {
        return intersects(v0, v1, v2, loc, false, false );
    }

    /**
     * <code>intersectWherePlanar</code> determines if the Ray intersects a
     * triangle and if so it stores the point of
     * intersection in the given loc vector as t, u, v where t is the distance
     * from the origin to the point of intersection and u,v is the intersection
     * point in terms of the triangle plane.
     *
     * @param t the Triangle to test against.
     * @param loc
     *            storage vector to save the collision point in (if the ray
     *            collides) as t, u, v
     * @return true if the ray collides.
     */
    public boolean intersectWherePlanar(Triangle t, Vector3f loc) {
        return intersectWherePlanar(t.get(0), t.get(1), t.get(2), loc);
    }

    /**
     * <code>intersectWherePlanar</code> determines if the Ray intersects a
     * triangle defined by the specified points and if so it stores the point of
     * intersection in the given loc vector as t, u, v where t is the distance
     * from the origin to the point of intersection and u,v is the intersection
     * point in terms of the triangle plane.
     *
     * @param v0
     *            first point of the triangle.
     * @param v1
     *            second point of the triangle.
     * @param v2
     *            third point of the triangle.
     * @param loc
     *            storage vector to save the collision point in (if the ray
     *            collides) as t, u, v
     * @return true if the ray collides.
     */
    public boolean intersectWherePlanar(Vector3f v0, Vector3f v1, Vector3f v2,
                                        Vector3f loc) {
        return intersects(v0, v1, v2, loc, true, false );
    }

    /**
     * <code>intersects</code> does the actual intersection work.
     *
     * @param v0
     *            first point of the triangle.
     * @param v1
     *            second point of the triangle.
     * @param v2
     *            third point of the triangle.
     * @param store
     *            storage vector - if null, no intersection is calc'd
     * @param doPlanar
     *            true if we are calcing planar results.
     * @param quad
     * @return true if ray intersects triangle
     */
    private boolean intersects( Vector3f v0, Vector3f v1, Vector3f v2,
                                Vector3f store, boolean doPlanar, boolean quad ) {
        Vector3f diff = origin.subtract(v0, tempVa);
        Vector3f edge1 = v1.subtract(v0, tempVb);
        Vector3f edge2 = v2.subtract(v0, tempVc);
        Vector3f norm = edge1.cross(edge2, tempVd);

        float dirDotNorm = direction.dot(norm);
        float sign;
        if (dirDotNorm > FastMath.FLT_EPSILON) {
            sign = 1;
        } else if (dirDotNorm < -FastMath.FLT_EPSILON) {
            sign = -1f;
            dirDotNorm = -dirDotNorm;
        } else {
            // ray and triangle/quad are parallel
            return false;
        }

        float dirDotDiffxEdge2 = sign * direction.dot(diff.cross(edge2, edge2));
        if (dirDotDiffxEdge2 >= 0.0f) {
            float dirDotEdge1xDiff = sign
                    * direction.dot(edge1.crossLocal(diff));
            if (dirDotEdge1xDiff >= 0.0f) {
                if ( !quad ? dirDotDiffxEdge2 + dirDotEdge1xDiff <= dirDotNorm : dirDotEdge1xDiff <= dirDotNorm ) {
                    float diffDotNorm = -sign * diff.dot(norm);
                    if (diffDotNorm >= 0.0f) {
                        // ray intersects triangle
                        // if storage vector is null, just return true,
                        if (store == null)
                            return true;
                        // else fill in.
                        float inv = 1f / dirDotNorm;
                        float t = diffDotNorm * inv;
                        if (!doPlanar) {
                            store.set(origin).addLocal(direction.x * t,
                                    direction.y * t, direction.z * t);
                        } else {
                            // these weights can be used to determine
                            // interpolated values, such as texture coord.
                            // eg. texcoord s,t at intersection point:
                            // s = w0*s0 + w1*s1 + w2*s2;
                            // t = w0*t0 + w1*t1 + w2*t2;
                            float w1 = dirDotDiffxEdge2 * inv;
                            float w2 = dirDotEdge1xDiff * inv;
                            //float w0 = 1.0f - w1 - w2;
                            store.set(t, w1, w2);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * <code>intersectWherePlanar</code> determines if the Ray intersects a
     * quad defined by the specified points and if so it stores the point of
     * intersection in the given loc vector as t, u, v where t is the distance
     * from the origin to the point of intersection and u,v is the intersection
     * point in terms of the quad plane.
     * One edge of the quad is [v0,v1], another one [v0,v2]. The behaviour thus is like
     * {@link #intersectWherePlanar(Vector3f, Vector3f, Vector3f, Vector3f)} except for
     * the extended area, which is equivalent to the union of the triangles [v0,v1,v2]
     * and [-v0+v1+v2,v1,v2].
     *
     * @param v0
     *            top left point of the quad.
     * @param v1
     *            top right point of the quad.
     * @param v2
     *            bottom left point of the quad.
     * @param loc
     *            storage vector to save the collision point in (if the ray
     *            collides) as t, u, v
     * @return true if the ray collides with the quad.
     */
    public boolean intersectWherePlanarQuad(Vector3f v0, Vector3f v1, Vector3f v2,
                                            Vector3f loc) {
        return intersects( v0, v1, v2, loc, true, true );
    }
    
    /**
     * 
     * @param p
     * @param loc
     * @return true if the ray collides with the given Plane
     */
    public boolean intersectsWherePlane(Plane p, Vector3f loc) {
        float denominator = p.getNormal().dot(direction);

        if (denominator > -FastMath.FLT_EPSILON && denominator < FastMath.FLT_EPSILON)
            return false; // coplanar

        float numerator = -(p.getNormal().dot(origin) - p.getConstant());
        float ratio = numerator / denominator;

        if (ratio < FastMath.FLT_EPSILON)
            return false; // intersects behind origin

        loc.set(direction).multLocal(ratio).addLocal(origin);

        return true;
    }
    
    public float distanceSquared(Vector3f point) {
		point.subtract(origin, tempVa);
		float rayParam = direction.dot(tempVa);
		if (rayParam > 0) {
			origin.add(direction.mult(rayParam, tempVb), tempVb);
		} else {
			tempVb.set(origin);
			rayParam = 0.0f;
		}

		tempVb.subtract(point, tempVa);
		return tempVa.lengthSquared();
	}

    /**
	 * 
	 * <code>getOrigin</code> retrieves the origin point of the ray.
	 * 
	 * @return the origin of the ray.
	 */
    public Vector3f getOrigin() {
        return origin;
    }

    /**
     *
     * <code>setOrigin</code> sets the origin of the ray.
     * @param origin the origin of the ray.
     */
    public void setOrigin(Vector3f origin) {
        this.origin = origin;
    }

    /**
     *
     * <code>getDirection</code> retrieves the direction vector of the ray.
     * @return the direction of the ray.
     */
    public Vector3f getDirection() {
        return direction;
    }

    /**
     *
     * <code>setDirection</code> sets the direction vector of the ray.
     * @param direction the direction of the ray.
     */
    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    /**
     * Copies information from a source ray into this ray.
     * 
     * @param source
     *            the ray to copy information from
     */
    public void set(Ray source) {
        origin.set(source.getOrigin());
        direction.set(source.getDirection());
    }

    public void write(JMEExporter e) throws IOException {
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(origin, "origin", Vector3f.ZERO);
        capsule.write(direction, "direction", Vector3f.ZERO);
    }

    public void read(JMEImporter e) throws IOException {
        InputCapsule capsule = e.getCapsule(this);
        origin = (Vector3f)capsule.readSavable("origin", Vector3f.ZERO.clone());
        direction = (Vector3f)capsule.readSavable("direction", Vector3f.ZERO.clone());
    }
    
    public Class<? extends Ray> getClassTag() {
        return this.getClass();
    }
    
    @Override
    public Ray clone() {
        try {
            Ray r = (Ray) super.clone();
            r.direction = direction.clone();
            r.origin = direction.clone();
            return r;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}

