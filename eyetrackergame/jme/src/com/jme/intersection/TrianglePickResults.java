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

import com.jme.math.Ray;
import com.jme.scene.Geometry;
import com.jme.scene.TriMesh;

/**
 * TrianglePickResults creates a PickResults object that calculates picking to
 * the triangle accuracy. PickData objects are added to the pick list as they
 * happen, these data objects refer to the two meshes, as well as their triangle
 * lists. While TrianglePickResults defines a processPick method, it is empty
 * and should be further defined by the user if so desired.
 * 
 * NOTE: Only TriMesh objects may obtain triangle accuracy, all others will
 * result in Bounding accuracy.
 * 
 * @author Mark Powell
 * @version $Id: TrianglePickResults.java,v 1.2 2004/10/14 01:23:12 mojomonkey
 *          Exp $
 */
public class TrianglePickResults extends PickResults {

    /**
     * Convenience wrapper for
     * addPick(Ray, Geometry, int)
     * collidability (first bit of the collidable bit mask).
     *
     * @see #addPick(Ray, Geometry, int)
     */
	public void addPick(Ray ray, Geometry g) {
		addPick(ray,g,1);
	}

	/**
     * <code>addPick</code> adds a Geometry object to the pick list. If the
     * Geometry object is not a TriMesh, the process stops here. However, if the
     * Geometry is a TriMesh, further processing occurs to obtain the triangle
     * lists that the ray passes through.
     * 
     * @param ray
     *            the ray that is doing the picking.
     * @param g
     *            the Geometry to add to the pick list.
     * @param requiredOnBits TrianglePick will only be considered if 'this'
     *            has these bits of its collision masks set.
     *                       
     * @see com.jme.intersection.PickResults#addPick(Ray, Geometry)
     */	
	public void addPick(Ray ray, Geometry g, int requiredOnBits) {
		//find the triangle that is being hit.
		//add this node and the triangle to the CollisionResults
		// list.
		if (!(g instanceof TriMesh)) {
			PickData data = new PickData(ray, g, willCheckDistance());
			addPickData(data);
		} else {
            ArrayList<Integer> a = new ArrayList<Integer>();
            ((TriMesh) g).findTrianglePick(ray, a, requiredOnBits);
			PickData data = new TrianglePickData(ray, ((TriMesh) g), a, willCheckDistance());
			
			addPickData(data);
		}
	}

	/**
	 * <code>processPick</code> will handle processing of the pick list. This
	 * is very application specific and therefore left as an empty method. 
	 * Applications wanting an automated picking system should extend 
	 * TrianglePickResults and override this method.
	 * 
	 * @see com.jme.intersection.PickResults#processPick()
	 */
	public void processPick() {

	}

}