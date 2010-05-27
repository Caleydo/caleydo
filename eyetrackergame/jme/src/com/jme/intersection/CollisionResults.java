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

import com.jme.scene.Geometry;

/**
 * <code>CollisionResults</code> stores the results of a collision test by
 * storing an ArrayList of CollisionData.
 * 
 * @author Mark Powell
 * @version $Id: CollisionResults.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public abstract class CollisionResults {

	private ArrayList<CollisionData> nodeList;

	/**
	 * Constructor instantiates a new <code>PickResults</code> object.
	 */
	public CollisionResults() {
		nodeList = new ArrayList<CollisionData>();
	}

	/**
	 * <code>addCollisionData</code> places a new <code>CollisionData</code>
	 * object into the results list.
	 * 
	 * @param col
	 *            The collision data to be placed in the results list.
	 */
	public void addCollisionData(CollisionData col) {
		nodeList.add(col);
	}

	/**
	 * <code>getNumber</code> retrieves the number of collisions that have
	 * been placed in the results.
	 * 
	 * @return the number of collisions in the list.
	 */
	public int getNumber() {
		return nodeList.size();
	}

	/**
	 * <code>getCollisionData</code> retrieves a CollisionData from a specific
	 * index.
	 * 
	 * @param i
	 *            the index requested.
	 * @return the CollisionData at the specified index.
	 */
	public CollisionData getCollisionData(int i) {
		return nodeList.get(i);
	}

	/**
	 * <code>clear</code> clears the list of all CollisionData.
	 */
	public void clear() {
		nodeList.clear();
	}
	
	/**
	 * 
	 * <code>addCollision</code> is an abstract method whose intent is the 
	 * subclass determines what to do when two Geometry object's bounding
	 * volumes are determined to intersect.
	 * 
	 * @param s the first Geometry that intersects.
	 * @param t the second Geometry that intersects.
	 */
	public abstract void addCollision(Geometry s, Geometry t);
	
	/**
	 * 
	 * <code>processCollisions</code> is an abstract method whose intent is
	 * the subclass defines how to process the collision data that has been
	 * collected since the last clear.
	 *
	 *
	 */
	public abstract void processCollisions();

}