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

import java.io.IOException;
import java.nio.FloatBuffer;

import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Triangle;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.jme.util.geom.BufferUtils;
import com.jmex.effects.particles.ParticleSystem.ParticleType;

/**
 * <code>Particle</code> defines a single Particle of a Particle system.
 * Generally, you would not interact with this class directly.
 * 
 * @author Joshua Slack
 * @version $Id: Particle.java 4133 2009-03-19 20:40:11Z blaine.dev $
 */
public class Particle implements Savable {

    public enum Status {
        /** Particle is dead -- not in play. */
        Dead,
        /** Particle is currently active. */
        Alive,
        /** Particle is available for spawning. */
        Available;
    }

    static final int VAL_CURRENT_SIZE = 0;
    static final int VAL_CURRENT_SPIN = 1;
    static final int VAL_CURRENT_MASS = 2;

    private int startIndex;
    private Vector3f position;
    private ColorRGBA currColor = ColorRGBA.black.clone();
    private Status status = Status.Available;
    private float lifeSpan;
    private float[] values = new float[3];
    private int currentAge;
    private int currentTexIndex = -1;
    private ParticleSystem parent;
    private Vector3f velocity;
    private Vector3f bbX = new Vector3f(), bbY = new Vector3f();

    // colors
    private ParticleType type = ParticleSystem.ParticleType.Quad;

    private Triangle triModel;

    // static variable for use in calculations to eliminate object creation
    private static Vector3f tempVec3 = new Vector3f();
    private static Quaternion tempQuat = new Quaternion();

    /**
     * Empty constructor - mostly for use with Savable interface
     */
    public Particle() {
    }

    /**
     * Normal use constructor. Sets up the parent and particle type for this
     * particle.
     * 
     * @param parent
     *            the particle collection this particle belongs to
     */
    public Particle(ParticleSystem parent) {
        this.parent = parent;
        this.type = parent.getParticleType();
    }

    /**
     * Cause this particle to reset it's lifespan, velocity, color, age and size
     * per the parent's settings. status is set to Status.Available and location
     * is set to 0,0,0. Actual geometry data is not affected by this call, only
     * particle params.
     */
    public void init() {
        init(parent.getRandomVelocity(null), new Vector3f(), parent
                .getRandomLifeSpan());
    }

    /**
     * Cause this particle to reset it's color, age and size per the parent's
     * settings. status is set to Status.Available. Location, velocity and
     * lifespan are set as given. Actual geometry data is not affected by this
     * call, only particle params.
     * 
     * @param velocity
     *            new initial particle velocity
     * @param position
     *            new initial particle position
     * @param lifeSpan
     *            new particle lifespan in ms
     */
    public void init(Vector3f velocity, Vector3f position, float lifeSpan) {
        this.lifeSpan = lifeSpan;
        this.velocity = (Vector3f) velocity.clone();
        this.position = (Vector3f) position.clone();

        currColor.set(parent.getStartColor());
        currentAge = 0;
        status = Status.Available;
        values[VAL_CURRENT_SIZE] = parent.getStartSize();
    }

    /**
     * Reset particle conditions. Besides the passed lifespan, we also reset
     * color, size, and spin angle to their starting values (as given by
     * parent.) Status is set to Status.Available.
     * 
     * @param lifeSpan
     *            the recreated particle's new lifespan
     */
    public void recreateParticle(float lifeSpan) {
        this.lifeSpan = lifeSpan;

        int verts = ParticleSystem.getVertsForParticleType(type);
        currColor.set(parent.getStartColor());
        for (int x = 0; x < verts; x++)
            BufferUtils.setInBuffer(currColor, parent.getParticleGeometry()
                    .getColorBuffer(), startIndex + x);
        values[VAL_CURRENT_SIZE] = parent.getStartSize();
        currentAge = 0;
        values[VAL_CURRENT_MASS] = 1;
        status = Status.Available;
    }

    /**
     * Update the vertices for this particle, taking size, spin and viewer into
     * consideration. In the case of particle type ParticleType.GeomMesh, the
     * original triangle normal is maintained rather than rotating it to face
     * the camera or parent vectors.
     * 
     * @param cam
     *            Camera to use in determining viewer aspect. If null, or if
     *            parent is not set to camera facing, parent's left and up
     *            vectors are used.
     */
    public void updateVerts(Camera cam) {
        float orient = parent.getParticleOrientation()
                + values[VAL_CURRENT_SPIN];
        float currSize = values[VAL_CURRENT_SIZE];

        if (type == ParticleSystem.ParticleType.GeomMesh
                || type == ParticleSystem.ParticleType.Point) {
            ; // nothing to do
        } else if (cam != null && parent.isCameraFacing()) {
            if (parent.isVelocityAligned()) {
                bbX.set(velocity).normalizeLocal().multLocal(currSize);
                cam.getDirection().cross(bbX, bbY).normalizeLocal().multLocal(
                        currSize);
            } else if (orient == 0) {
                bbX.set(cam.getLeft()).multLocal(currSize);
                bbY.set(cam.getUp()).multLocal(currSize);
            } else {
                float cA = FastMath.cos(orient) * currSize;
                float sA = FastMath.sin(orient) * currSize;
                bbX.set(cam.getLeft()).multLocal(cA).addLocal(
                        cam.getUp().x * sA, cam.getUp().y * sA,
                        cam.getUp().z * sA);
                bbY.set(cam.getLeft()).multLocal(-sA).addLocal(
                        cam.getUp().x * cA, cam.getUp().y * cA,
                        cam.getUp().z * cA);
            }
        } else {
            bbX.set(parent.getLeftVector()).multLocal(currSize);
            bbY.set(parent.getUpVector()).multLocal(currSize);
        }

        switch (type) {
            case Quad: {
                position.add(bbX, tempVec3).subtractLocal(bbY);
                BufferUtils.setInBuffer(tempVec3, parent.getParticleGeometry()
                        .getVertexBuffer(), startIndex);

                position.add(bbX, tempVec3).addLocal(bbY);
                BufferUtils.setInBuffer(tempVec3, parent.getParticleGeometry()
                        .getVertexBuffer(), startIndex + 1);

                position.subtract(bbX, tempVec3).subtractLocal(bbY);
                BufferUtils.setInBuffer(tempVec3, parent.getParticleGeometry()
                        .getVertexBuffer(), startIndex + 2);

                position.subtract(bbX, tempVec3).addLocal(bbY);
                BufferUtils.setInBuffer(tempVec3, parent.getParticleGeometry()
                        .getVertexBuffer(), startIndex + 3);
                break;
            }
            case GeomMesh: {
                Vector3f norm = triModel.getNormal();
                if (orient != 0)
                    tempQuat.fromAngleNormalAxis(orient, norm);

                for (int x = 0; x < 3; x++) {
                    if (orient != 0)
                        tempQuat.mult(triModel.get(x), tempVec3);
                    else
                        tempVec3.set(triModel.get(x));
                    tempVec3.multLocal(currSize).addLocal(position);
                    BufferUtils.setInBuffer(tempVec3, parent
                            .getParticleGeometry().getVertexBuffer(),
                            startIndex + x);
                }
                break;
            }
            case Triangle: {
                position.add(bbX, tempVec3).subtractLocal(bbY);
                BufferUtils.setInBuffer(tempVec3, parent.getParticleGeometry()
                        .getVertexBuffer(), startIndex);

                position.add(bbX, tempVec3).addLocal(3 * bbY.x, 3 * bbY.y,
                        3 * bbY.z);
                BufferUtils.setInBuffer(tempVec3, parent.getParticleGeometry()
                        .getVertexBuffer(), startIndex + 1);

                position.subtract(bbX.multLocal(3), tempVec3).subtractLocal(bbY);
                BufferUtils.setInBuffer(tempVec3, parent.getParticleGeometry()
                        .getVertexBuffer(), startIndex + 2);
                break;
            }
            case Line: {
                position.subtract(bbX, tempVec3);
                BufferUtils.setInBuffer(tempVec3, parent.getParticleGeometry()
                        .getVertexBuffer(), startIndex);

                position.add(bbX, tempVec3);
                BufferUtils.setInBuffer(tempVec3, parent.getParticleGeometry()
                        .getVertexBuffer(), startIndex + 1);
                break;
            }
            case Point: {
                BufferUtils.setInBuffer(position, parent.getParticleGeometry()
                        .getVertexBuffer(), startIndex);
                break;
            }
        }
    }

    /**
     * <p>
     * update position (using current position and velocity), color
     * (interpolating between start and end color), size (interpolating between
     * start and end size), spin (using parent's spin speed) and current age of
     * particle. If this particle's age is greater than its lifespan, it is set
     * to status DEAD.
     * </p>
     * <p>
     * Note that this only changes the parameters of the Particle, not the
     * geometry the particle is associated with.
     * </p>
     * 
     * @param secondsPassed
     *            number of seconds passed since last update.
     * @return true if this particle is not ALIVE (in other words, if it is
     *         ready to be reused.)
     */
    public boolean updateAndCheck(float secondsPassed) {
        if (status != Status.Alive) {
            return true;
        }
        currentAge += secondsPassed * 1000; // add ms time to age
        if (currentAge > lifeSpan) {
        	killParticle();
            return true;
        }

        position.scaleAdd(secondsPassed * 1000f, velocity, position);

        // get interpolated values from appearance ramp:
        parent.getRamp().getValuesAtAge(currentAge, lifeSpan, currColor,
                values, parent);

        // interpolate colors
        int verts = ParticleSystem.getVertsForParticleType(type);
        for (int x = 0; x < verts; x++)
            BufferUtils.setInBuffer(currColor, parent.getParticleGeometry()
                    .getColorBuffer(), startIndex + x);

        // check for tex animation
        int newTexIndex = parent.getTexAnimation().getTexIndexAtAge(currentAge, lifeSpan, parent);
        // Update tex coords if applicable
        if (currentTexIndex != newTexIndex) {
            // Only supported in Quad type for now.
            if (ParticleType.Quad.equals(parent.getParticleType())) {
                // determine side
                float side = FastMath.sqrt(parent.getTexQuantity());
                int index = newTexIndex;
                if (index >= parent.getTexQuantity()) {
                    index %= parent.getTexQuantity();
                }
                // figure row / col
                float row = side - (int)(index / side) - 1;
                float col = index % side;
                // set texcoords
                float sU = col/side, eU = (col+1)/side;
                float sV = row/side, eV = (row+1)/side;
                FloatBuffer texs = parent.getParticleGeometry().getTextureCoords(0).coords;
                texs.position(startIndex*2);
                texs.put(sU).put(sV);
                texs.put(sU).put(eV);
                texs.put(eU).put(sV);
                texs.put(eU).put(eV);
                texs.clear();
            }
        }
        
        return false;
    }

    public void killParticle() {
        setStatus(Status.Dead);
        
        currColor.a = 0;

        BufferUtils.populateFromBuffer(tempVec3, parent
                .getParticleGeometry().getVertexBuffer(), startIndex);
        int verts = ParticleSystem.getVertsForParticleType(type);
        for (int x = 0; x < verts; x++) {
            BufferUtils.setInBuffer(tempVec3, parent.getParticleGeometry()
                    .getVertexBuffer(), startIndex + x);
            BufferUtils.setInBuffer(currColor, parent.getParticleGeometry()
                    .getColorBuffer(), startIndex + x);
        }

	}

	/**
     * Resets current age to 0
     */
    public void resetAge() {
        currentAge = 0;
    }

    /**
     * @return the current age of the particle in ms
     */
    public int getCurrentAge() {
        return currentAge;
    }

    /**
     * @return the current position of the particle in space
     */
    public Vector3f getPosition() {
        return position;
    }

    /**
     * Set the position of the particle in space.
     * 
     * @param position
     *            the new position in world coordinates
     */
    public void setPosition(Vector3f position) {
        this.position.set(position);
    }

    /**
     * @return the current status of this particle.
     * @see Status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Set the status of this particle.
     * 
     * @param status
     *            new status of this particle
     * @see Status
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * @return the current velocity of this particle
     */
    public Vector3f getVelocity() {
        return velocity;
    }

    /**
     * Set the current velocity of this particle
     * 
     * @param velocity
     *            the new velocity
     */
    public void setVelocity(Vector3f velocity) {
        this.velocity.set(velocity);
    }

    /**
     * @return the current color applied to this particle
     */
    public ColorRGBA getCurrentColor() {
        return currColor;
    }

    /**
     * @return the start index of this particle in relation to where it exists
     *         in its parent's geometry data.
     */
    public int getStartIndex() {
        return startIndex;
    }

    /**
     * Set the starting index where this particle is represented in its parent's
     * geometry data
     * 
     * @param index
     */
    public void setStartIndex(int index) {
        this.startIndex = index;
    }

    /**
     * @return the mass of this particle. Only used by ParticleInfluences such
     *         as drag.
     */
    public float getMass() {
        return values[VAL_CURRENT_MASS];
    }

    /**
     * @return the inverse mass of this particle. Often useful for skipping
     *         constant division by mass calculations. If the mass is 0, the
     *         inverse mass is considered to be positive infinity. Conversely,
     *         if the mass is positive infinity, the inverse is 0. The inverse
     *         of negative infinity is considered to be -0.
     */
    public float getInvMass() {
        float mass = values[VAL_CURRENT_MASS];
        if (mass == 0)
            return Float.POSITIVE_INFINITY;
        else if (mass == Float.POSITIVE_INFINITY)
            return 0;
        else if (mass == Float.NEGATIVE_INFINITY)
            return -0;
        else
            return 1f / mass;
    }

    /**
     * Sets a triangle model to use for particle calculations when using
     * particle type ParticleType.GeomMesh. The particle will maintain the
     * triangle's ratio and plane of orientation. It will spin (if applicable)
     * around the triangle's normal axis. The triangle should already have its
     * center and normal fields calculated before calling this method.
     * 
     * @param t
     *            the triangle to model this particle after.
     */
    public void setTriangleModel(Triangle t) {
        this.triModel = t;
    }

    /**
     * @return the triangle model used by this particle
     * @see #setTriangleModel(Triangle)
     */
    public Triangle getTriangleModel() {
        return this.triModel;
    }

    // /////
    // Savable interface methods
    // /////

    public void write(JMEExporter e) throws IOException {
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(startIndex, "startIndex", 0);
        capsule.write(position, "position", Vector3f.ZERO);
        capsule.write(status, "status", Status.Available);
        capsule.write(lifeSpan, "lifeSpan", 0);
        capsule.write(currentAge, "currentAge", 0);
        capsule.write(parent, "parent", null);
        capsule.write(velocity, "velocity", Vector3f.UNIT_XYZ);
        capsule.write(type, "type", ParticleSystem.ParticleType.Quad);
    }

    public void read(JMEImporter e) throws IOException {
        InputCapsule capsule = e.getCapsule(this);
        startIndex = capsule.readInt("startIndex", 0);
        position = (Vector3f) capsule.readSavable("position", Vector3f.ZERO
                .clone());
        status = capsule.readEnum("status", Status.class, Status.Available);
        lifeSpan = capsule.readFloat("lifeSpan", 0);
        currentAge = capsule.readInt("currentAge", 0);
        parent = (ParticleSystem) capsule.readSavable("parent", null);
        velocity = (Vector3f) capsule.readSavable("velocity", Vector3f.UNIT_XYZ
                .clone());
        type = capsule.readEnum("type", ParticleSystem.ParticleType.class,
                ParticleSystem.ParticleType.Quad);
    }

    public Class<? extends Particle> getClassTag() {
        return this.getClass();
    }
}
