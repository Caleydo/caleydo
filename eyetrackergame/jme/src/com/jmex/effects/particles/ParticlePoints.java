package com.jmex.effects.particles;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.Renderer;
import com.jme.scene.Point;
import com.jme.scene.TexCoords;
import com.jme.util.geom.BufferUtils;

/**
 * ParticlePoints is a particle system that uses Point as its underlying
 * geometric data.
 * 
 * @author Joshua Slack
 * @version $Id: ParticlePoints.java 4133 2009-03-19 20:40:11Z blaine.dev $
 */
public class ParticlePoints extends ParticleSystem {

    private static final long serialVersionUID = 2L;

    public ParticlePoints() {}
    
    public ParticlePoints(String name, int numParticles) {
        super(name, numParticles);
    }

    @Override
    protected void initializeParticles(int numParticles) {
        Vector2f sharedTextureData[];

        // setup texture coords
        sharedTextureData = new Vector2f[] {new Vector2f(0.0f, 0.0f)};
        
        int verts = getVertsForParticleType(getParticleType());

        geometryCoordinates = BufferUtils
                .createVector3Buffer(numParticles * verts);

        appearanceColors = BufferUtils.createColorBuffer(numParticles * verts);
        particles = new Particle[numParticles];

        if (particleGeom != null) {
            detachChild(particleGeom);
        }
        particleGeom = new Point(name+"_points", geometryCoordinates, null, appearanceColors, null) {
            private static final long serialVersionUID = 1L;
            @Override
            public void updateWorldVectors() {
                ; // Do nothing.
            }
        };
        particleGeom.setTextureCoords(new TexCoords(BufferUtils.createVector2Buffer(numParticles)), 0);
        attachChild(particleGeom);
        setRenderQueueMode(Renderer.QUEUE_OPAQUE);
        setLightCombineMode(LightCombineMode.Off);
        setTextureCombineMode(TextureCombineMode.Replace);

        invScale = new Vector3f();

        for (int k = 0; k < numParticles; k++) {
            particles[k] = new Particle(this);
            particles[k].init();
            particles[k].setStartIndex(k*verts);
            for (int a = verts-1; a >= 0; a--) {
                int ind = (k * verts) + a;
                BufferUtils.setInBuffer(sharedTextureData[a],
                        getParticleGeometry().getTextureCoords(0).coords, ind);
                BufferUtils.setInBuffer(particles[k].getCurrentColor(),
                        appearanceColors, (ind));
            }

        }
        updateRenderState();
        particleGeom.setCastsShadows(false);
    }

    @Override
    public ParticleType getParticleType() {
        return ParticleSystem.ParticleType.Point;
    }

    public void draw(Renderer r) {
        Camera camera = r.getCamera();
        for (int i = 0; i < particles.length; i++) {
            Particle particle = particles[i];
            if (particle.getStatus() == Particle.Status.Alive) {
                particle.updateVerts(camera);
            }
        }

        if (!particlesInWorldCoords) {
        	getParticleGeometry().getWorldTranslation().set(getWorldTranslation());
        	getParticleGeometry().getWorldRotation().set(getWorldRotation());
        } else {
        	getParticleGeometry().getWorldTranslation().zero();
        	getParticleGeometry().getWorldRotation().loadIdentity();
        }
        getParticleGeometry().getWorldScale().set(getWorldScale());
        getParticleGeometry().draw(r);
    }
    
    /**
     * @return true if points are to be drawn antialiased
     */
    public boolean isAntialiased() {
        return getParticleGeometry().isAntialiased();
    }
    
    /**
     * Sets whether the points should be antialiased. May decrease performance. If
     * you want to enabled antialiasing, you should also use an alphastate with
     * a source of SourceFunction.SourceAlpha and a destination of DB_ONE_MINUS_SRC_ALPHA or
     * DB_ONE.
     * 
     * @param antialiased
     *            true if the line should be antialiased.
     */
    public void setAntialiased(boolean antialiased) {
        getParticleGeometry().setAntialiased(antialiased);
    }

    /**
     * @return the pixel size of each point.
     */
    public float getPointSize() {
        return getParticleGeometry().getPointSize();
    }

    /**
     * Sets the pixel width of the points when drawn. Non anti-aliased point
     * sizes are rounded to the nearest whole number by opengl.
     * 
     * @param size
     *            The size to set.
     */
    public void setPointSize(float size) {
        getParticleGeometry().setPointSize(size);
    }

    @Override
    public Point getParticleGeometry() {
        return (Point)particleGeom;
    }

}
