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

import com.jme.scene.Geometry;

/**
 * BoundingCollisionResults creates a CollisionResults object that only cares
 * about bounding volume accuracy. CollisionData objects are added to the
 * collision list as they happen, these data objects only refer to the two
 * meshes, not their triangle lists. While BoundingCollisionResults defines a
 * processCollisions method, it is empty and should be further defined by the
 * user if so desired.
 * 
 * @author Mark Powell
 * @version $Id: BoundingCollisionResults.java,v 1.2 2004/10/05 23:38:16
 *          mojomonkey Exp $
 */
public class BoundingCollisionResults extends CollisionResults {

    /**
     * adds a CollisionData object to this results list, the objects only refer
     * to the collision meshes, not the triangles.
     * 
     * @see com.jme.intersection.CollisionResults#addCollision(com.jme.scene.Geometry,
     *      com.jme.scene.Geometry)
     */
    public void addCollision(Geometry s, Geometry t) {
        CollisionData data = new CollisionData(s, t);
        addCollisionData(data);
    }

    /**
     * empty implementation, it is highly recommended that you override this
     * method to handle any collisions as needed.
     * 
     * @see com.jme.intersection.CollisionResults#processCollisions()
     */
    public void processCollisions() {

    }

}