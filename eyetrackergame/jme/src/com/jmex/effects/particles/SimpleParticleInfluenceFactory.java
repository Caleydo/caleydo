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

import com.jme.math.FastMath;
import com.jme.math.Line;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

/**
 * <code>SimpleParticleForceFactory</code>
 * @author Joshua Slack
 * @version $Id: SimpleParticleInfluenceFactory.java 4133 2009-03-19 20:40:11Z blaine.dev $
 */
public final class SimpleParticleInfluenceFactory {

    public static class BasicWind extends ParticleInfluence {
        private float strength;
        private Vector3f windDirection;
        private boolean random, rotateWithScene;
        private Vector3f vector = new Vector3f(); 
        
        public BasicWind() {
        }
        
        public BasicWind(float windStr, Vector3f windDir, boolean addRandom,
            boolean rotateWithScene) {
            strength = windStr;
            windDirection = windDir;
            random = addRandom;
            this.rotateWithScene = rotateWithScene;
        }
        
        public float getStrength() {
            return strength;
        }
        
        public void setStrength(float windStr) {
            strength = windStr;
        }
        
        public Vector3f getWindDirection() {
            return windDirection;
        }
        
        public void setWindDirection(Vector3f windDir) {
            windDirection = windDir;
        }

        public boolean isRandom() {
            return random;
        }
        
        public void setRandom(boolean addRandom) {
            random = addRandom;
        }
        
        public boolean isRotateWithScene() {
            return rotateWithScene;
        }
        
        public void setRotateWithScene(boolean rotateWithScene) {
            this.rotateWithScene = rotateWithScene;
        }
        
        public void prepare(ParticleSystem system) {
            vector.set(windDirection);
            if (rotateWithScene) {
                system.getEmitterTransform().multNormal(vector);
            }
        }
        
        public void apply(float dt, Particle p, int index) {
            float tStr = (random ? FastMath.nextRandomFloat() * strength : strength);
            p.getVelocity().scaleAdd(tStr * dt, vector, p.getVelocity());
        }
        
        public void write(JMEExporter e) throws IOException {
            super.write(e);
            OutputCapsule capsule = e.getCapsule(this);
            capsule.write(strength, "strength", 1f);
            capsule.write(windDirection, "windDirection", Vector3f.UNIT_X);
            capsule.write(random, "random", false);
            capsule.write(rotateWithScene, "rotateWithScene", true);
        }

        public void read(JMEImporter e) throws IOException {
            super.read(e);
            InputCapsule capsule = e.getCapsule(this);
            strength = capsule.readFloat("strength", 1f);
            windDirection = (Vector3f)capsule.readSavable("windDirection",
                Vector3f.UNIT_X.clone());
            random = capsule.readBoolean("random", false);
            rotateWithScene = capsule.readBoolean("rotateWithScene", true);
        }

        public Class getClassTag() {
            return this.getClass();
        }
    }
    
    public static class BasicGravity extends ParticleInfluence {
        private Vector3f gravity;
        private boolean rotateWithScene;
        private Vector3f vector = new Vector3f();
        
        public BasicGravity() {
        }
        
        public BasicGravity(Vector3f gravForce, boolean rotateWithScene) {
            gravity = new Vector3f(gravForce);
            this.rotateWithScene = rotateWithScene;
        }
        
        public Vector3f getGravityForce() {
            return gravity;
        }
        
        public void setGravityForce(Vector3f gravForce) {
            gravity = gravForce;
        }
        
        public boolean isRotateWithScene() {
            return rotateWithScene;
        }
        
        public void setRotateWithScene(boolean rotateWithScene) {
            this.rotateWithScene = rotateWithScene;
        }
        
        public void prepare(ParticleSystem system) {
            vector.set(gravity);
            if (rotateWithScene) {
                system.getEmitterTransform().multNormal(vector);
            }
        }
        
        public void apply(float dt, Particle p, int index) {
            p.getVelocity().scaleAdd(dt, vector, p.getVelocity());
        }
    
        public void write(JMEExporter e) throws IOException {
            super.write(e);
            OutputCapsule capsule = e.getCapsule(this);
            capsule.write(gravity, "gravity", Vector3f.ZERO);
            capsule.write(rotateWithScene, "rotateWithScene", true);
        }

        public void read(JMEImporter e) throws IOException {
            super.read(e);
            InputCapsule capsule = e.getCapsule(this);
            gravity = (Vector3f)capsule.readSavable("gravity",
                Vector3f.ZERO.clone());
            rotateWithScene = capsule.readBoolean("rotateWithScene", true);
        }
        
        public Class getClassTag() {
            return this.getClass();
        }
    }
    
    public static class BasicDrag extends ParticleInfluence {
        private Vector3f velocity = new Vector3f();
        private float dragCoefficient;
        
        public BasicDrag() {
        }
        
        public BasicDrag(float dragCoef) {
            dragCoefficient = dragCoef;
        }
        
        public float getDragCoefficient() {
            return dragCoefficient;
        }
        
        public void setDragCoefficient(float dragCoef) {
            dragCoefficient = dragCoef;
        }
        
        public void apply(float dt, Particle p, int index) {
            // viscous drag
            velocity.set(p.getVelocity());
            p.getVelocity().addLocal(velocity.multLocal(-dragCoefficient * dt * p.getInvMass()));
        }
    
        public void write(JMEExporter e) throws IOException {
            super.write(e);
            OutputCapsule capsule = e.getCapsule(this);
            capsule.write(dragCoefficient, "dragCoefficient", 1f);
        }

        public void read(JMEImporter e) throws IOException {
            super.read(e);
            InputCapsule capsule = e.getCapsule(this);
            dragCoefficient = capsule.readFloat("dragCoefficient", 1f);
        }
        
        public Class getClassTag() {
            return this.getClass();
        }
    }
    
    public static class BasicVortex extends ParticleInfluence {
    
        public static final int VT_CYLINDER = 0;
        public static final int VT_TORUS = 1;
        
        private int type = VT_CYLINDER;
        private float strength, divergence, height, radius;
        private Line axis;
        private boolean random, transformWithScene;
        private Vector3f v1 = new Vector3f(), v2 = new Vector3f(),
            v3 = new Vector3f();
        private Quaternion rot = new Quaternion();
        private Line line = new Line();
        
        public BasicVortex() {
        }
        
        public BasicVortex(float strength, float divergence, Line axis,
            boolean random, boolean transformWithScene) {
            this.strength = strength;
            this.divergence = divergence;
            this.axis = axis;
            this.height = 0f;
            this.radius = 1f;
            this.random = random;
            this.transformWithScene = transformWithScene;
        }
        
        public int getType() {
            return type;
        }
        
        public void setType(int type) {
            this.type = type;
        }
        
        public float getStrength() {
            return strength;
        }
        
        public void setStrength(float strength) {
            this.strength = strength;
        }
        
        public float getDivergence() {
            return divergence;
        }
        
        public void setDivergence(float divergence) {
            this.divergence = divergence;            
        }
        
        public Line getAxis() {
            return axis;
        }
        
        public void setAxis(Line axis) {
            this.axis = axis;
        }
        
        public float getHeight() {
            return height;
        }
        
        public void setHeight(float height) {
            this.height = height;
        }
        
        public float getRadius() {
            return radius;
        }
        
        public void setRadius(float radius) {
            this.radius = radius;
        }
        
        public boolean isRandom() {
            return random;
        }
        
        public void setRandom(boolean random) {
            this.random = random;
        }
        
        public boolean isTransformWithScene() {
            return transformWithScene;
        }
        
        public void setTransformWithScene(boolean transformWithScene) {
            this.transformWithScene = transformWithScene;
        }
        
        public void prepare(ParticleSystem system) {
            line.getOrigin().set(axis.getOrigin());
            line.getDirection().set(axis.getDirection());
            if (transformWithScene) {
                system.getEmitterTransform().multPoint(line.getOrigin());
                system.getEmitterTransform().multNormal(line.getDirection());
            }
            if (type == VT_CYLINDER) {
                rot.fromAngleAxis(-divergence, line.getDirection());
            }
        }
        
        public void apply(float dt, Particle p, int index) {
            float dtStr = dt * strength *
                (random ? FastMath.nextRandomFloat() : 1f);
            p.getPosition().subtract(line.getOrigin(), v1);
            line.getDirection().cross(v1, v2);
            if (v2.length() == 0) { // particle is on the axis
                return;
            }
            v2.normalizeLocal();
            if (type == VT_CYLINDER) {
                rot.multLocal(v2);
                p.getVelocity().scaleAdd(dtStr, v2, p.getVelocity());
                return;
            }
            v2.cross(line.getDirection(), v1);
            v1.multLocal(radius);
            v1.scaleAdd(height, line.getDirection(), v1);
            v1.addLocal(line.getOrigin());
            v1.subtractLocal(p.getPosition());
            if (v1.length() == 0) { // particle is on the ring
                return;
            }
            v1.normalizeLocal();
            v1.cross(v2, v3);
            rot.fromAngleAxis(-divergence, v2);
            rot.multLocal(v3);
            p.getVelocity().scaleAdd(dtStr, v3, p.getVelocity());
        }
    
        public void write(JMEExporter e) throws IOException {
            super.write(e);
            OutputCapsule capsule = e.getCapsule(this);
            capsule.write(type, "type", VT_CYLINDER);
            capsule.write(strength, "strength", 1f);
            capsule.write(divergence, "divergence", 0f);
            capsule.write(axis, "axis", new Line(new Vector3f(),
                new Vector3f(Vector3f.UNIT_Y)));
            capsule.write(height, "height", 0f);
            capsule.write(radius, "radius", 1f);
            capsule.write(random, "random", false);
            capsule.write(transformWithScene, "transformWithScene", true);
        }

        public void read(JMEImporter e) throws IOException {
            super.read(e);
            InputCapsule capsule = e.getCapsule(this);
            type = capsule.readInt("type", VT_CYLINDER);
            strength = capsule.readFloat("strength", 1f);
            divergence = capsule.readFloat("divergence", 0f);
            axis = (Line)capsule.readSavable("axis", new Line(new Vector3f(),
                Vector3f.UNIT_Y.clone()));
            height = capsule.readFloat("height", 0f);
            radius = capsule.readFloat("radius", 1f);
            random = capsule.readBoolean("random", false);
            transformWithScene = capsule.readBoolean("transformWithScene", true);
        }
        
        public Class getClassTag() {
            return this.getClass();
        }
    }
    
    /**
     * Not used.
     */
    private SimpleParticleInfluenceFactory() {
    }

    /**
     * Creates a basic wind that always blows in a single direction.
     * 
     * @param windStr
     *            Max strength of wind.
     * @param windDir
     *            Direction wind should blow.
     * @param addRandom
     *            randomly alter the strength of the wind by 0-100%
     * @param rotateWithScene
     *            rotate the wind direction with the particle system
     * @return ParticleInfluence
     */
    public static ParticleInfluence createBasicWind(float windStr,
        Vector3f windDir, boolean addRandom, boolean rotateWithScene) {
        return new BasicWind(windStr, windDir, addRandom, rotateWithScene);
    }

    /**
     * Create a basic gravitational force.
     *
     * @param rotateWithScene
     *            rotate the gravity vector with the particle system
     * @return ParticleInfluence
     */
    public static ParticleInfluence createBasicGravity(Vector3f gravForce,
        boolean rotateWithScene) {
        return new BasicGravity(gravForce, rotateWithScene);
    }

    /**
     * Create a basic drag force that will use the given drag coefficient. Drag
     * is determined by figuring the current velocity and reversing it, then
     * multiplying by the drag coefficient and dividing by the particle mass.
     * 
     * @param dragCoef
     *            Should be positive. Larger values mean more drag but possibly
     *            more instability.
     * @return ParticleInfluence
     */
    public static ParticleInfluence createBasicDrag(float dragCoef) {
        return new BasicDrag(dragCoef);
    }
    
    /**
     * Creates a basic vortex.
     * 
     * @param strength
     *            Max strength of vortex.
     * @param divergence
     *            The divergence in radians from the tangent vector
     * @param axis
     *            The center of the vortex.
     * @param random
     *            randomly alter the strength of the vortex by 0-100%
     * @param transformWithScene
     *            transform the axis with the particle system
     * @return ParticleInfluence
     */
    public static ParticleInfluence createBasicVortex(float strength,
        float divergence, Line axis, boolean random,
        boolean transformWithScene) {
        return new BasicVortex(strength, divergence, axis, random,
            transformWithScene);
    }
}
