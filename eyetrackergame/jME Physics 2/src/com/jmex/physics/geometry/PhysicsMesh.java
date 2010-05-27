/*
 * Copyright (c) 2005-2007 jME Physics 2
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of 'jME Physics 2' nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
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
package com.jmex.physics.geometry;

import java.io.IOException;

import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;
import com.jme.util.export.JMEExporter;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsNode;

/**
 * A PhysicsMesh can represent any triangle soup that is used a collision geometry.
 * @see #copyFrom(com.jme.scene.TriMesh)
 * @author Irrisor
 */
public abstract class PhysicsMesh extends PhysicsCollisionGeometry {
    protected PhysicsMesh( PhysicsNode node ) {
        super( node );
    }

    /**
     * Copy data from a scene TriMesh to the collision info. The implementation may hold a reference to the specified
     * TriMesh data but usually will not update the collision info automatically. If this PhysicsMesh belongs to
     * a {@link com.jmex.physics.DynamicPhysicsNode} volume, center of mass and inertia are automatically computed.
     * To avoid this computation use {@link #copyFrom(TriMesh, float, Vector3f, Matrix3f)}
     * @param triMesh where to copy triangle data from
     * @see #copyFrom(TriMesh, float, Vector3f, Matrix3f)
     */
    public abstract void copyFrom( TriMesh triMesh );

    /**
     * Copy data from a scene TriMesh to the collision info. The implementation may hold a reference to the specified
     * TriMesh data but usually will not update the collision info automatically.
     * @param triMesh where to copy triangle data from
     * @param volume volume of the trimesh (see {@link #getVolume()})
     * @param centerOfMass center of mass for this trimesh
     * @param inertia inertia matrix for the dynamic behaviour of the geometry
     */
    public abstract void copyFrom( TriMesh triMesh, float volume, Vector3f centerOfMass, Matrix3f inertia );

	@Override
	public Class getClassTag() {
		return PhysicsMesh.class;
	}

	@Override
	public void write(JMEExporter ex) throws IOException {
		// TODO: write collision mesh independent of the physics implementation
		throw new IOException("Not implemented: currently, saving PhysicsMesh collision geometry doesn't work");
	}
}

/*
 * $log$
 */

