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

import com.jme.math.Ray;
import com.jme.scene.Geometry;

/**
 * BoundingPickResults creates a PickResults object that only cares
 * about bounding volume accuracy. PickData objects are added to the
 * pick list as they happen, these data objects only refer to the two
 * meshes, not their triangle lists. While BoundingPickResults defines a
 * processPick method, it is empty and should be further defined by the
 * user if so desired.
 * 
 * @author Mark Powell
 * @version $Id: BoundingCollisionResults.java,v 1.2 2004/10/05 23:38:16
 *          mojomonkey Exp $
 */
public class BoundingPickResults extends PickResults{

    /**
     * adds a PickData object to this results list, the objects only refer
     * to the picked meshes, not the triangles.
     * 
     * @see com.jme.intersection.PickResults#addPick(Ray, Geometry)
     */
	public void addPick(Ray ray, Geometry g) {
		addPick(ray,g,1);
	}
	
	@Override
	public void addPick(Ray ray, Geometry g, int requiredOnBits) {
		PickData data = new PickData(ray, g, willCheckDistance());
		addPickData(data);
	}
	
	
	/**
     * empty implementation, it is highly recommended that you override this
     * method to handle any picks as needed.
     * 
     * @see com.jme.intersection.PickResults#processPick()
     */
	public void processPick() {
		
	}


}
