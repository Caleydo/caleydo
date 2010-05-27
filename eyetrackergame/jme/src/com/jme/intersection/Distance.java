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

package com.jme.intersection;

import com.jme.math.FastMath;
import com.jme.math.Ray;
import com.jme.math.Vector3f;

/**
 * <code>Distance</code> provides functional methods for determining the
 * distances between one object and another. These methods are static to allow
 * for easy calling.
 *
 * @author Mark Powell
 * @version $Id: Distance.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public class Distance {

    private Distance() {
    }

    /**
     * <code>distance</code> calculates the distance between two points.
     *
     * @param point1 the first point to test.
     * @param point2 the second point to test.
     * @return the distance between the two points.
     */
    public static float distance(Vector3f point1, Vector3f point2) {
        return FastMath.sqrt(distanceSquared(point1, point2));

    }

    /**
     * <code>distanceSquared</code> returns the distance between two points,
     * with the distance squared. This allows for faster comparisons if relation
     * is important but actual distance is not.
     *
     * @param p1 the first point to test.
     * @param p2 the second point to test.
     * @return the distance squared between two points.
     */
    public static float distanceSquared(Vector3f p1, Vector3f p2) {
        return ((p1.x - p2.x) * (p1.x - p2.x))
                + ((p1.y - p2.y) * (p1.y - p2.y))
                + ((p1.z - p2.z) * (p1.z - p2.z));
    }

    /**
     * <code>distance</code> calculates the distance between a point and a ray.
     *
     * @param point the point to test.
     * @param ray   the ray to test.
     * @return the distance between the point and the ray.
     */
    public static float distance(Vector3f point, Ray ray) {
        Vector3f diff = point.subtract(ray.getOrigin());
        float t = diff.dot(ray.getDirection());

        if (t <= 0.0) {
            t = 0.0f;
        } else {
            t /= ray.getDirection().lengthSquared();
            diff.subtractLocal(ray.getDirection().mult(t));
        }

        return diff.length();
    }
}
