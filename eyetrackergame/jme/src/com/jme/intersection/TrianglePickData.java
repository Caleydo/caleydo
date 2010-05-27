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

import java.util.ArrayList;
import java.util.logging.Logger;

import com.jme.math.FastMath;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;

/**
 * Pick data for triangle accuracy picking including sort by distance to
 * intersection point.
 */
public class TrianglePickData extends PickData {
    private static final Logger logger = Logger.getLogger(TrianglePickData.class.getName());

    private final Vector3f[] worldTriangle = new Vector3f[]{new Vector3f(), new Vector3f(), new Vector3f()};
    private final Vector3f[] vertices = new Vector3f[]{new Vector3f(), new Vector3f(), new Vector3f()};

	private final Vector3f intersectionPoint = new Vector3f();

    public TrianglePickData(Ray ray, TriMesh targetMesh,
			ArrayList<Integer> targetTris, boolean checkDistance) {
		super(ray, targetMesh, targetTris, false);
        if (checkDistance) {
            distance = calculateDistance();
        }
	}

	protected float calculateDistance() {
		ArrayList<Integer> tris = getTargetTris();
		if (tris.isEmpty()) {
			return Float.POSITIVE_INFINITY;
		}

        TriMesh mesh = (TriMesh) getTargetMesh();

        //don't update world vectors here - it was has to be done before the intersection
        //mesh.getParentGeom().updateWorldVectors();

        float distanceSq = Float.POSITIVE_INFINITY;
		float[] distances = new float[tris.size()];
		for (int i = 0; i < tris.size(); i++) {
			int triIndex = tris.get( i );
			mesh.getTriangle(triIndex, vertices);
			float triDistanceSq = getDistanceSquaredToTriangle( vertices, mesh );
			distances[i] = triDistanceSq;
			if (triDistanceSq > 0 && triDistanceSq < distanceSq) {
                distanceSq = triDistanceSq;
			}
		}
		
		//XXX optimize! ugly bubble sort for now
		boolean sorted = false;
		while(!sorted) {
			sorted = true;
			for(int sort = 0; sort < distances.length - 1; sort++) {
				if(distances[sort] > distances[sort+1]) {
					//swap
					sorted = false;
					float temp = distances[sort+1];
					distances[sort+1] = distances[sort];
					distances[sort] = temp;
					
					//swap tris too
					int temp2 = tris.get(sort+1);
					tris.set(sort+1, tris.get(sort));
					tris.set(sort, temp2);
				}
			}
		}
		
		if (Float.isInfinite( distanceSq )) {
            return distanceSq;
        } else
			return FastMath.sqrt(distanceSq);
	}

	private float getDistanceSquaredToTriangle( Vector3f[] triangle, Spatial spatial ) {
		// Transform triangle to world space
		for (int i = 0; i < 3; i++) {
            spatial.localToWorld(triangle[i], worldTriangle[i]);
		}
		// Intersection test
		Ray ray = getRay();
		if (ray.intersectWhere(worldTriangle[0], worldTriangle[1],
				worldTriangle[2], intersectionPoint)) {
			return ray.getOrigin().distanceSquared(intersectionPoint);
		}

		// Should not happen
		//TODO: removed because it does happen = spamming... need to find out why instead
//        logger.warning("Couldn't detect nearest triangle intersection!");
		return Float.POSITIVE_INFINITY;
	}

	public Vector3f getIntersectionPoint() {
		return intersectionPoint;
	}
	
	
}
