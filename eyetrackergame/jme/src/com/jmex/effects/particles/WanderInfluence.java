package com.jmex.effects.particles;

import java.io.IOException;
import java.util.ArrayList;

import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

public class WanderInfluence extends ParticleInfluence {

    public static final float DEFAULT_RADIUS = .03f;
    public static final float DEFAULT_DISTANCE = .2f;
    public static final float DEFAULT_JITTER = .005f;
    
    private float wanderRadius = DEFAULT_RADIUS;
    private float wanderDistance = DEFAULT_DISTANCE;
    private float wanderJitter = DEFAULT_JITTER;
    
    private ArrayList<Vector3f> wanderTargets = new ArrayList<Vector3f>(1);
    private Vector3f workVect = new Vector3f(); 
    
    @Override
    public void prepare(ParticleSystem system) {
        if (wanderTargets.size() != system.getNumParticles()) {
            wanderTargets = new ArrayList<Vector3f>(system.getNumParticles());
            for (int x = system.getNumParticles(); --x >= 0; )
                wanderTargets.add(new Vector3f(system.getEmissionDirection()).normalizeLocal());
        }
    }
    
    @Override
    public void apply(float dt, Particle particle, int index) {
        if (wanderRadius == 0 && wanderDistance == 0 && wanderJitter == 0) return;
        
        Vector3f wanderTarget = wanderTargets.get(index);
        
        wanderTarget.addLocal(calcNewJitter(), calcNewJitter(), calcNewJitter());
        wanderTarget.normalizeLocal();
        wanderTarget.multLocal(wanderRadius);
        
        workVect.set(particle.getVelocity()).normalizeLocal().multLocal(wanderDistance);
        workVect.addLocal(wanderTarget).normalizeLocal();
        workVect.multLocal(particle.getVelocity().length());
        particle.getVelocity().set(workVect);
    }

    private float calcNewJitter() {
        return ((FastMath.nextRandomFloat()*2.0f)-1.0f) * wanderJitter;
    }

    public float getWanderDistance() {
        return wanderDistance;
    }

    public void setWanderDistance(float wanderDistance) {
        this.wanderDistance = wanderDistance;
    }

    public float getWanderJitter() {
        return wanderJitter;
    }

    public void setWanderJitter(float wanderJitter) {
        this.wanderJitter = wanderJitter;
    }

    public float getWanderRadius() {
        return wanderRadius;
    }

    public void setWanderRadius(float wanderRadius) {
        this.wanderRadius = wanderRadius;
    }
    
    @Override
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule cap = e.getCapsule(this);
        cap.write(wanderRadius, "wanderRadius", DEFAULT_RADIUS);
        cap.write(wanderDistance, "wanderDistance", DEFAULT_DISTANCE);
        cap.write(wanderJitter, "wanderJitter", DEFAULT_JITTER);
    }
    
    @Override
    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule cap = e.getCapsule(this);
        wanderRadius = cap.readFloat("wanderRadius", DEFAULT_RADIUS);
        wanderDistance = cap.readFloat("wanderDistance", DEFAULT_DISTANCE);
        wanderJitter = cap.readFloat("wanderJitter", DEFAULT_JITTER);
    }
}
