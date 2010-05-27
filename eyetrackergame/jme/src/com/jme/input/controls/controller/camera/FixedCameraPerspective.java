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
package com.jme.input.controls.controller.camera;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.scene.Spatial;
import com.jme.scene.Spatial.CullHint;

/**
 * Follows behind the spatial
 * 
 * @author Matthew D. Hicks
 */
public class FixedCameraPerspective implements CameraPerspective {
	private Quaternion q;
	private Vector3f v;
	private Vector3f v2;
	
	private Vector3f location;
	private boolean hideSpatialOnActivate;
	
	private CullHint previousCullHint;
	
	public FixedCameraPerspective(Vector3f location) {
		this(location, false);
	}
	
	public FixedCameraPerspective(Vector3f location, boolean hideSpatialOnActivate) {
		q = new Quaternion();
		v = new Vector3f();
		v2 = new Vector3f();
		this.location = location;
		this.hideSpatialOnActivate = hideSpatialOnActivate;
	}
	
	public Vector3f getLocation() {
		return location;
	}
	
	public void update(Camera camera, Spatial spatial, float time) {
		// Update rotation
		q.set(spatial.getWorldRotation());				// Get the spatial's current rotation
		camera.setDirection(q.getRotationColumn(2));	// Match direction to the spatial's
		camera.setLeft(q.getRotationColumn(0));			// Match left to the spatial's
		camera.setUp(q.getRotationColumn(1));			// Match up to the spatial's
		
		// Update location
		spatial.updateWorldVectors();				// Update this spatial's world coordinates
		v.set(spatial.getWorldTranslation());		// Set the location to the same as our spatial's
		q.set(spatial.getWorldRotation());			// Set the Quaternion value to the spatials' rotation
		v2.set(location);							// Set the values for our location to this temp holder
		q.multLocal(v2);							// Multiply the intended location offset to the rotation (to match our spatial's rotation)
		v.addLocal(v2);								// Add the rotational applied offset to the location
		camera.setLocation(v);						// Set the camera location
		
		// Now lets make sure the camera is looking at the spatial
		camera.lookAt(spatial.getLocalTranslation(), camera.getUp());
		
		camera.update();
	}

	
	public void setActive(Camera camera, Spatial spatial, boolean active) {
		if ((active) && (hideSpatialOnActivate)) {
			previousCullHint = spatial.getCullHint();
			spatial.setCullHint(Spatial.CullHint.Always);
		} else if ((!active) && (hideSpatialOnActivate)) {
			spatial.setCullHint(previousCullHint);
		}
	}
}