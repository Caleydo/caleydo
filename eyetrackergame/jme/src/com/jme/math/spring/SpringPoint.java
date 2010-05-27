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

package com.jme.math.spring;

import java.io.IOException;

import com.jme.math.Vector3f;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * <code>SpringPoint</code> defines a single point in a SpringSystem.
 * @author Joshua Slack
 * @version $Id: SpringPoint.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public class SpringPoint implements Savable {

	/**
	 * index of this point in the system.  Needs to be set by the programmer.
	 * can be useful for derivatives of SpringpointForce that may apply force
	 * differently to different points based on location in the matrix.
	 */
	public int index = 0;
	/** Mass of this point. */
	public float mass = 1;
	/** Inverse Mass of this point. */
	public float invMass = 1;
	/** Position of this point in space. */
	public Vector3f position;
	/** Previous Position of this point in space. */
	public Vector3f oldPos;
	/** Acceleration vector, zeroed and recalculated on each SpringSystem.calcForces(float). */
	public Vector3f acceleration;

	/**
	 * Public constructor.
	 * @param pos Vertex position of this point.
	 */
	public SpringPoint(Vector3f pos) {
		position = pos;
		oldPos = new Vector3f(pos);
		acceleration = new Vector3f(0, 0, 0);
	}

	/**
	 * Set the mass for this point.  Also calculates and stores the inverse
	 * mass to invMass field for future use.
	 * @param m float
	 */
	public void setMass(float m) {
		mass = m;
		if (m == Float.POSITIVE_INFINITY || m == Float.NEGATIVE_INFINITY)
			invMass = 0;
		else if (m == 0)
			invMass = Float.POSITIVE_INFINITY;
		else
			invMass = 1f / m;
	}

	/**
	 * Verlet update of point location.  Pretty stable.  Updates position
	 * by using implied velocity derived from the distance travled since
	 * last update.  Thus velocity and position do not get out of sync.
	 * @param dt float - change in time since last update.
	 */
	public void update(float dt) {
		float dtSquared = dt * dt;
		if (invMass == 0) return;
		float x = position.x, y = position.y, z = position.z;
		position.set(
				2*position.x - oldPos.x + acceleration.x * dtSquared,
				2*position.y - oldPos.y + acceleration.y * dtSquared,
				2*position.z - oldPos.z + acceleration.z * dtSquared);
		oldPos.set(x, y, z);
	}

    public void write(JMEExporter e) throws IOException {
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(index, "index", 0);
        capsule.write(mass, "mass", 1);
        capsule.write(position, "position", Vector3f.ZERO);
        capsule.write(acceleration, "acceleration", Vector3f.ZERO);
        
    }

    public void read(JMEImporter e) throws IOException {
        InputCapsule capsule = e.getCapsule(this);
        index = capsule.readInt("index", 0);
        mass = capsule.readFloat("mass", 1);
        invMass = 1f / mass;
        position = (Vector3f)capsule.readSavable("position", Vector3f.ZERO.clone());
        acceleration = (Vector3f)capsule.readSavable("acceleration", Vector3f.ZERO.clone());
    }
    
    public Class getClassTag() {
        return this.getClass();
    }
}
