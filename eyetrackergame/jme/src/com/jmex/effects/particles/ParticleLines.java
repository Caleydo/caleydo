package com.jmex.effects.particles;

import com.jme.intersection.CollisionResults;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.Renderer;
import com.jme.scene.Line;
import com.jme.scene.Spatial;
import com.jme.scene.TexCoords;
import com.jme.scene.Line.Mode;
import com.jme.util.geom.BufferUtils;

/**
 * ParticleLines is a particle system that uses Line as its underlying
 * geometric data.
 * 
 * @author Joshua Slack
 * @version $Id: ParticleLines.java 4640 2009-08-29 02:28:57Z blaine.dev $
 */
public class ParticleLines extends ParticleSystem {

    private static final long serialVersionUID = 2L;

    public ParticleLines() {
    }

    public ParticleLines(String name, int numParticles) {
        super(name, numParticles);
    }

    @Override
    protected void initializeParticles(int numParticles) {

        // setup texture coords
        Vector2f[] sharedTextureData = new Vector2f[] {
                new Vector2f(0.0f, 0.0f), new Vector2f(1.0f, 1.0f) };

        int verts = getVertsForParticleType(getParticleType());

        geometryCoordinates = BufferUtils.createVector3Buffer(numParticles
                * verts);

        // setup indices for PT_LINES
        int[] indices = new int[numParticles * 2];
        for (int j = 0; j < numParticles; j++) {
            indices[0 + j * 2] = j * 2 + 0;
            indices[1 + j * 2] = j * 2 + 1;
        }

        appearanceColors = BufferUtils.createColorBuffer(numParticles * verts);
        particles = new Particle[numParticles];

        if (particleGeom != null) {
            detachChild(particleGeom);
        }
        Line line = new Line(name+"_lines") {
            private static final long serialVersionUID = 1L;
            @Override
            public void updateWorldVectors() {
                ; // Do nothing.
            }
        };
        particleGeom = line;
        attachChild(line);
        line.setVertexBuffer(geometryCoordinates);
        line.setColorBuffer(appearanceColors);
        line.setTextureCoords(new TexCoords(BufferUtils.createVector2Buffer(numParticles*2)), 0);
        line.setIndexBuffer(BufferUtils.createIntBuffer(indices));
        setRenderQueueMode(Renderer.QUEUE_OPAQUE);
        setLightCombineMode(Spatial.LightCombineMode.Off);
        setTextureCombineMode(TextureCombineMode.Replace);

        invScale = new Vector3f();

        for (int k = 0; k < numParticles; k++) {
            particles[k] = new Particle(this);
            particles[k].init();
            particles[k].setStartIndex(k * verts);
            for (int a = verts - 1; a >= 0; a--) {
                int ind = (k * verts) + a;
                BufferUtils.setInBuffer(sharedTextureData[a],
                        line.getTextureCoords(0).coords, ind);
                BufferUtils.setInBuffer(particles[k].getCurrentColor(),
                        appearanceColors, (ind));
            }

        }
        updateRenderState();
        particleGeom.setCastsShadows(false);
    }

    @Override
    public ParticleType getParticleType() {
        return ParticleSystem.ParticleType.Line;
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

    public Line getParticleGeometry() {
        return (Line) particleGeom;
    }

    /**
     * @return true if lines are to be antialiased
     */
    public boolean isAntialiased() {
        return getParticleGeometry().isAntialiased();
    }

    /**
     * Sets whether the line should be antialiased. May decrease performance. If
     * you want to enabled antialiasing, you should also use an alphastate with
     * a source of SourceFunction.SourceAlpha and a destination of
     * DestinationFunction.OneMinusSourceColor or DestinationFunction.One.
     * 
     * @param antialiased
     *            true if the line should be antialiased.
     */
    public void setAntialiased(boolean antialiased) {
        getParticleGeometry().setAntialiased(antialiased);
    }

    /**
     * @return line mode
     * @see Line.Mode
     */
    public Mode getMode() {
        return getParticleGeometry().getMode();
    }

    /**
     * @param mode
     *            Line mode.
     * @see Line.Mode
     */
    public void setMode(Mode mode) {
        getParticleGeometry().setMode(mode);
    }

    /**
     * @return the width of this line.
     */
    public float getLineWidth() {
        return getParticleGeometry().getLineWidth();
    }

    /**
     * Sets the width of each line when drawn. Non anti-aliased line widths are
     * rounded to the nearest whole number by opengl.
     * 
     * @param lineWidth
     *            The lineWidth to set.
     */
    public void setLineWidth(float lineWidth) {
        getParticleGeometry().setLineWidth(lineWidth);
    }

    /**
     * @return the set stipplePattern. 0xFFFF means no stipple.
     */
    public short getStipplePattern() {
        return getParticleGeometry().getStipplePattern();
    }

    /**
     * The stipple or pattern to use when drawing the particle lines. 0xFFFF is
     * a solid line.
     * 
     * @param stipplePattern
     *            a 16bit short whose bits describe the pattern to use when
     *            drawing this line
     */
    public void setStipplePattern(short stipplePattern) {
        getParticleGeometry().setStipplePattern(stipplePattern);
    }

    /**
     * @return the set stippleFactor.
     */
    public int getStippleFactor() {
        return getParticleGeometry().getStippleFactor();
    }

    /**
     * @param stippleFactor
     *            magnification factor to apply to the stipple pattern.
     */
    public void setStippleFactor(int stippleFactor) {
        getParticleGeometry().setStippleFactor(stippleFactor);
    }

    @Override
    public void findCollisions(
            Spatial scene, CollisionResults results, int requiredOnBits) {
        ; // ignore
    }

    @Override
    public boolean hasCollision(
            Spatial scene, boolean checkTriangles, int requiredOnBits) {
        return false;
    }

}
