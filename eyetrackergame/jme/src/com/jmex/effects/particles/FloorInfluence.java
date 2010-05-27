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

package com.jmex.effects.particles;

import com.jme.math.Plane;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

/**
 * @author andgra
 */
public class FloorInfluence extends ParticleInfluence {

    /**
     * Bouncyness is the factor of multiplication when bouncing off the floor. A
     * bouncyness factor of 1 means the ball leaves the floor with the same
     * velocity as it hit the floor.
     */
    private float bouncyness = 1;

    /**
     * The normal vector of the floor. Same semantics as in math.Plane.
     */
    private Vector3f normal;

    /**
     * The position vector for the (imaginary) center of the floor.
     */
    private Vector3f pos;

    private Plane floor;

    /**
     * @param pos
     *            The position vector for the (imaginary) center of the floor.
     * @param normal
     *            The normal vector of the floor. Same semantics as in
     *            math.Plane.
     * @param bouncynessBouncyness
     *            is the factor of multiplication when bouncing off the floor. A
     *            bouncyness factor of 1 means the ball leaves the floor with
     *            the same velocity as it hit the floor, much like a rubber
     *            ball.
     */
    public FloorInfluence(Vector3f pos, Vector3f normal, float bouncyness) {
        this.bouncyness = bouncyness;
        this.normal = normal;
        this.pos = pos;
        this.floor = new Plane(normal, normal.dot(pos));
    }

    @Override
    public void apply(float dt, Particle particle, int index) {

        if (particle.getStatus() == Particle.Status.Alive
                && floor.pseudoDistance(particle.getPosition()) <= 0) {

            float t = (floor.getNormal().dot(particle.getPosition()) - floor
                    .getConstant())
                    / floor.getNormal().dot(particle.getVelocity());
            Vector3f s = particle.getPosition().subtract(
                    particle.getVelocity().mult(t));

            this.normal.normalizeLocal();
            Vector3f v1 = this.normal.cross(s.subtract(pos));
            Vector3f v2 = this.normal.cross(v1);
            v1.normalizeLocal();
            v2.normalizeLocal();

            Vector3f newVel = new Vector3f(particle.getVelocity());
            newVel.y *= -bouncyness;
            Quaternion q = new Quaternion();
            q.fromAxes(v1, this.normal, v2);
            newVel = q.mult(newVel);

            particle.setVelocity(newVel);
        } // if
    }

    public float getBouncyness() {
        return bouncyness;
    }

    public void setBouncyness(float bouncyness) {
        this.bouncyness = bouncyness;
    }

    public Plane getFloor() {
        return floor;
    }

    public void setFloor(Plane floor) {

        this.floor = floor;
    }

    public Vector3f getNormal() {
        return normal;
    }

    public void setNormal(Vector3f normal) {
        this.normal = normal;
        this.floor = new Plane(normal, normal.dot(pos));
    }

    public Vector3f getPos() {
        return pos;
    }

    public void setPos(Vector3f pos) {
        this.pos = pos;
        this.floor = new Plane(normal, normal.dot(pos));
    }

}
