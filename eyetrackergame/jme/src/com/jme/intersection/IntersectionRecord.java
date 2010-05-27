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

import com.jme.math.Vector3f;
import com.jme.system.JmeException;

/**
 * IntersectionRecord stores needed information for a interesection query between
 * two objects. This includes all points that were intersected, and the
 * distances between these points. Therefore, a 1 to 1 ratio between the distance
 * array and the point array is enforced.
 *
 */
public class IntersectionRecord {

    private float[] distances;
    private Vector3f[] points;

    /**
     * Instantiates a new IntersectionRecord with no distances or points assigned.
     *
     */
    public IntersectionRecord() {
    }

    /**
     * Instantiates a new IntersectionRecord defining the distances and points. 
     * If the size of the distance and point arrays do not match, an exception
     * is thrown.
     * @param distances the distances of this intersection.
     * @param points the points of this intersection.
     */
    public IntersectionRecord(float[] distances, Vector3f[] points) {
        if (distances.length != points.length)
            throw new JmeException(
                    "The distances and points variables must have an equal number of elements.");
        this.distances = distances;
        this.points = points;
    }

    /**
     * Returns the number of intersections that occured.
     * @return the number of intersections that occured.
     */
    public int getQuantity() {
        if (points == null)
            return 0;
        return points.length;
    }

    /**
     * Returns an intersection point at a provided index.
     * @param index the index of the point to obtain.
     * @return the point at the index of the array.
     */
    public Vector3f getIntersectionPoint(int index) {
        return points[index];
    }

    /**
     * Returns an intersection distance at a provided index.
     * @param index the index of the distance to obtain.
     * @return the distance at the index of the array.
     */
    public float getIntersectionDistance(int index) {
        return distances[index];
    }

    /**
     * Returns the smallest distance in the distance array.
     * @return the smallest distance in the distance array.
     */
    public float getClosestDistance() {
        float min = Float.MAX_VALUE;
        if (distances != null) {
            for (float val : distances) {
                if (val < min) {
                    min = val;
                }
            }
        }
        return min;
    }

    /**
     * Returns the point that has the smallest associated distance value.
     * @return the point that has the smallest associated distance value.
     */
    public int getClosestPoint() {
        float min = Float.MAX_VALUE;
        int point = 0;
        if (distances != null) {
            for (int i = distances.length; --i >= 0;) {
                float val = distances[i];
                if (val < min) {
                    min = val;
                    point = i;
                }
            }
        }
        return point;
    }

    /**
     * Returns the point that has the largest associated distance value.
     * @return the point that has the largest associated distance value.
     */
    public int getFarthestPoint() {
        float max = Float.MIN_VALUE;
        int point = 0;
        if (distances != null) {
            for (int i = distances.length; --i >= 0;) {
                float val = distances[i];
                if (val > max) {
                    max = val;
                    point = i;
                }
            }
        }
        return point;
    }

}
