package com.jmex.effects.particles;

import com.jme.scene.TriMesh;

public class ParticleFactory {

    public static ParticleMesh buildParticles(String name, int number) {
        return buildParticles(name, number, ParticleSystem.ParticleType.Quad);
    }

    public static ParticleMesh buildParticles(String name, int number, ParticleSystem.ParticleType particleType) {
        if (particleType != ParticleSystem.ParticleType.Triangle && particleType != ParticleSystem.ParticleType.Quad)
            throw new IllegalArgumentException("particleType should be either ParticleSystem.ParticleType.TRIANGLE or ParticleSystem.ParticleType.QUAD");
        ParticleMesh particleMesh = new ParticleMesh(name, number, particleType);
        ParticleController particleController = new ParticleController(particleMesh);
        particleMesh.addController(particleController);
        return particleMesh;
    }

    public static ParticleMesh buildMeshParticles(String name, TriMesh mesh) {
        ParticleMesh particleMesh = new ParticleMesh(name, mesh);
        ParticleController particleController = new ParticleController(particleMesh);
        particleMesh.addController(particleController);
        return particleMesh;
    }

    public static ParticlePoints buildPointParticles(String name, int number) {
        ParticlePoints p = new ParticlePoints(name, number);
        ParticleController particleController = new ParticleController(p);
        p.addController(particleController);
        return p;
    }

    public static ParticleLines buildLineParticles(String name, int number) {
        ParticleLines l = new ParticleLines(name, number);
        ParticleController particleController = new ParticleController(l);
        l.addController(particleController);
        return l;
    }
}
