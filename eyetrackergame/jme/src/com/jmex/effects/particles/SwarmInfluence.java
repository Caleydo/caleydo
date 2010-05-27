package com.jmex.effects.particles;

import java.io.IOException;

import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

/**
 * Simple swarming influence for use with particles.
 * 
 * @author Joshua Slac
 */
public class SwarmInfluence extends ParticleInfluence {

    private float swarmRangeSQ;
    private Vector3f swarmOffset;
    private Vector3f swarmPoint = new Vector3f();
    
    public static final float DEFAULT_SWARM_RANGE_SQ = 0.01f;
    public static final float DEFAULT_DEVIANCE = FastMath.DEG_TO_RAD * 15;
    public static final float DEFAULT_TURN_SPEED = FastMath.DEG_TO_RAD * 180;
    public static final float DEFAULT_SPEED_BUMP = .1f;
    public static final float DEFAULT_MAX_SPEED = .2f;
    
    private float deviance = DEFAULT_DEVIANCE;
    private float turnSpeed = DEFAULT_TURN_SPEED;
    private float speedBump = DEFAULT_SPEED_BUMP;
    private float maxSpeed = DEFAULT_MAX_SPEED;
    
    private transient float maxSpeedSQ = DEFAULT_MAX_SPEED * DEFAULT_MAX_SPEED;

    private static final Vector3f workVect = new Vector3f();
    private static final Vector3f workVect2 = new Vector3f();
    private static final Quaternion workQuat = new Quaternion();
    
    public SwarmInfluence() {
        this.swarmRangeSQ = DEFAULT_SWARM_RANGE_SQ;
        this.swarmOffset = new Vector3f();
    }
    
    public SwarmInfluence(Vector3f offset, float swarmRange) {
        super();
        this.swarmRangeSQ = swarmRange * swarmRange;
        this.swarmOffset = offset;
    }
    
    @Override
    public void prepare(ParticleSystem system) {
        super.prepare(system);
        swarmPoint.set(system.getOriginCenter()).addLocal(swarmOffset);
    }
    
    @Override
    public void apply(float dt, Particle particle, int index) {
        Vector3f pVelocity = particle.getVelocity();
        // determine if the particle is in the inner or outer zone
        float pDist = particle.getPosition().distanceSquared(swarmPoint);
        workVect.set(swarmPoint).subtractLocal(particle.getPosition()).normalizeLocal();
        workVect2.set(pVelocity).normalizeLocal();
        if (pDist > swarmRangeSQ) {
            // IN THE OUTER ZONE...
            // Determine if the angle between particle velocity and a vector to
            // the swarmPoint is less than the accepted deviance
            float angle = workVect.angleBetween(workVect2);
            if (angle < deviance) {
                // if it is, increase the speed speedBump over time
                if (pVelocity.lengthSquared() < maxSpeedSQ) {
                    float change = speedBump*dt;
                    workVect2.multLocal(change); // where workVector2 = pVelocity.normalizeLocal()
                    pVelocity.addLocal(workVect2);
                }
            } else {
                Vector3f axis = workVect2.crossLocal(workVect);
                // if it is not, shift the velocity to bring it back in line
                if ((Float.floatToIntBits(pVelocity.lengthSquared()) & 0x1f) != 0) {
                    workQuat.fromAngleAxis(turnSpeed * dt, axis);
                } else {
                    workQuat.fromAngleAxis(-turnSpeed * dt, axis);
                }
                workQuat.multLocal(pVelocity);
            }
        } else {
            Vector3f axis = workVect2.crossLocal(workVect);
            // IN THE INNER ZONE...
            // Alter the heading based on how fast we are going
            if ((index & 0x1f) != 0) {
                workQuat.fromAngleAxis(turnSpeed * dt, axis);
            } else {
                workQuat.fromAngleAxis(-turnSpeed * dt, axis);
            }
            workQuat.multLocal(pVelocity);
        }        
    }

    public float getSwarmRange() {
        return FastMath.sqrt(swarmRangeSQ);
    }

    public void setSwarmRange(float swarmRange) {
        this.swarmRangeSQ = swarmRange * swarmRange;
    }

    public Vector3f getSwarmOffset() {
        return swarmOffset;
    }

    public void setSwarmOffset(Vector3f offset) {
        this.swarmPoint = offset;
    }

    public float getDeviance() {
        return deviance;
    }

    public void setDeviance(float deviance) {
        this.deviance = deviance;
    }

    public float getSpeedBump() {
        return speedBump;
    }

    public void setSpeedBump(float speedVariance) {
        this.speedBump = speedVariance;
    }

    public float getTurnSpeed() {
        return turnSpeed;
    }

    public void setTurnSpeed(float turnSpeed) {
        this.turnSpeed = turnSpeed;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
        maxSpeedSQ = maxSpeed * maxSpeed;
    }
    
    @Override
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule cap = e.getCapsule(this);
        cap.write(swarmRangeSQ, "swarmRangeSQ", DEFAULT_SWARM_RANGE_SQ);
        cap.write(deviance, "deviance", DEFAULT_DEVIANCE);
        cap.write(turnSpeed, "turnSpeed", DEFAULT_TURN_SPEED);
        cap.write(speedBump, "speedBump", DEFAULT_SPEED_BUMP);
        cap.write(maxSpeed, "maxSpeed", DEFAULT_MAX_SPEED);
        cap.write(swarmOffset, "swarmOffset", new Vector3f());
    }
    
    @Override
    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule cap = e.getCapsule(this);
        swarmRangeSQ = cap.readFloat("swarmRangeSQ", DEFAULT_SWARM_RANGE_SQ);
        deviance = cap.readFloat("deviance", DEFAULT_DEVIANCE);
        turnSpeed = cap.readFloat("turnSpeed", DEFAULT_TURN_SPEED);
        speedBump = cap.readFloat("speedBump", DEFAULT_SPEED_BUMP);
        maxSpeed = cap.readFloat("maxSpeed", DEFAULT_MAX_SPEED);
        swarmOffset = (Vector3f)cap.readSavable("swarmOffset", new Vector3f());
    }

}
