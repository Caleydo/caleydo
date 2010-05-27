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

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.Renderer;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.geom.BufferUtils;
import com.jmex.effects.particles.Particle.Status;

/**
 * ParticleMesh is a particle system that uses TriMesh as its underlying
 * geometric data.
 * 
 * @author Joshua Slack
 * @version $Id: ParticleMesh.java 4133 2009-03-19 20:40:11Z blaine.dev $
 */
public class ParticleMesh extends ParticleSystem {

    private static final long serialVersionUID = 2L;
    
    private boolean useMeshTexCoords = true;
    private boolean useTriangleNormalEmit = true;

    public ParticleMesh() {}

    public ParticleMesh(String name, int numParticles) {
        super(name, numParticles);
        setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
        setLightCombineMode(LightCombineMode.Off);
        setTextureCombineMode(TextureCombineMode.Replace);
    }

    public ParticleMesh(String name, int numParticles, ParticleSystem.ParticleType type) {
        super(name, numParticles, type);
        setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
        setLightCombineMode(LightCombineMode.Off);
        setTextureCombineMode(TextureCombineMode.Replace);
    }

    public ParticleMesh(String name, TriMesh geom) {
        super(name, 0, ParticleSystem.ParticleType.GeomMesh);
        numParticles = geom.getTriangleCount();
        psGeom = geom;
        setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
        setLightCombineMode(LightCombineMode.Off);
        setTextureCombineMode(TextureCombineMode.Replace);
        initializeParticles(geom.getTriangleCount());
    }

    @Override
    protected void initializeParticles(int numParticles) {

        if (particleGeom != null) {
            detachChild(particleGeom);
        }
        TriMesh mesh = new TriMesh(name+"_mesh") {
            private static final long serialVersionUID = 1L;
            @Override
            public void updateWorldVectors() {
                ; // Do nothing.
            }
        };
        particleGeom = mesh;
        attachChild(mesh);
        particles = new Particle[numParticles];
        if (numParticles == 0) return;
        Vector2f sharedTextureData[];

        // setup texture coords
        switch (getParticleType()) {
            case GeomMesh:
            case Triangle:
                sharedTextureData = new Vector2f[] {
                        new Vector2f(0.0f, 0.0f), 
                        new Vector2f(0.0f, 2.0f),
                        new Vector2f(2.0f, 0.0f)
                        };
                break;
            case Quad:
                sharedTextureData = new Vector2f[] {
                        new Vector2f(0.0f, 0.0f), 
                        new Vector2f(0.0f, 1.0f),
                        new Vector2f(1.0f, 0.0f),
                        new Vector2f(1.0f, 1.0f)
                        };
                break;
            default:
                throw new IllegalStateException("Particle Mesh may only have particle type of ParticleType.Quad, ParticleType.GeomMesh or ParticleType.Triangle");
        }
        
        int verts = getVertsForParticleType(getParticleType());

        geometryCoordinates = BufferUtils
                .createVector3Buffer(numParticles * verts);

        // setup indices
        int[] indices;
        switch (getParticleType()) {
            case Triangle:
            case GeomMesh:
                indices = new int[numParticles * 3];
                for (int j = 0; j < numParticles; j++) {
                    indices[0 + j * 3] = j * 3 + 2;
                    indices[1 + j * 3] = j * 3 + 1;
                    indices[2 + j * 3] = j * 3 + 0;
                }
                break;
            case Quad:
                indices = new int[numParticles * 6];
                for (int j = 0; j < numParticles; j++) {
                    indices[0 + j * 6] = j * 4 + 2;
                    indices[1 + j * 6] = j * 4 + 1;
                    indices[2 + j * 6] = j * 4 + 0;
                    
                    indices[3 + j * 6] = j * 4 + 2;
                    indices[4 + j * 6] = j * 4 + 3;
                    indices[5 + j * 6] = j * 4 + 1;
                }
                break;
            default:
                throw new IllegalStateException("Particle Mesh may only have particle type of ParticleType.Quad, ParticleType.GeomMesh or ParticleType.Triangle");
        }

        appearanceColors = BufferUtils.createColorBuffer(numParticles * verts);

        mesh.setVertexBuffer(geometryCoordinates);
        mesh.setColorBuffer(appearanceColors);
        mesh.setTextureCoords(new TexCoords(BufferUtils.createVector2Buffer(numParticles * verts)), 0);
        mesh.setIndexBuffer(BufferUtils.createIntBuffer(indices));

        invScale = new Vector3f();

        for (int k = 0; k < numParticles; k++) {
            particles[k] = new Particle(this);
            particles[k].init();
            particles[k].setStartIndex(k*verts);
            for (int a = verts-1; a >= 0; a--) {
                int ind = (k * verts) + a;
                if (particleType == ParticleSystem.ParticleType.GeomMesh && useMeshTexCoords) {
                    int index = ((TriMesh)psGeom).getIndexBuffer().get(ind);
                    BufferUtils.populateFromBuffer(workVect2, psGeom.getTextureCoords(0).coords, index);
                    BufferUtils.setInBuffer(workVect2, mesh.getTextureCoords(0).coords, ind);
                } else
                    BufferUtils.setInBuffer(sharedTextureData[a],
                            mesh.getTextureCoords(0).coords, ind);
                BufferUtils.setInBuffer(particles[k].getCurrentColor(),
                        appearanceColors, (ind));
            }

        }
        updateRenderState();
        particleGeom.setCastsShadows(false);
    }
    
    /**
    * Stop emiting particles
    * Kill all available particles. (status=dead) 
    * @author clovis teixeira
    */
    public void stopEmitting()
    {
    	for (int k = 0; k < numParticles; k++) {
    		if(particles[k].getStatus() == Status.Available)
    			particles[k].setStatus(Status.Dead);
    	}
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


    public void resetParticleVelocity(int i) {
        if (particleType == ParticleSystem.ParticleType.GeomMesh && useTriangleNormalEmit) {
            particles[i].getVelocity().set(particles[i].getTriangleModel().getNormal());
            particles[i].getVelocity().multLocal(emissionDirection);
            particles[i].getVelocity().multLocal(getInitialVelocity());
        } else {
            super.resetParticleVelocity(i);
        }
    }

    public boolean isUseMeshTexCoords() {
        return useMeshTexCoords;
    }

    public void setUseMeshTexCoords(boolean useMeshTexCoords) {
        this.useMeshTexCoords = useMeshTexCoords;
    }

    public boolean isUseTriangleNormalEmit() {
        return useTriangleNormalEmit;
    }

    public void setUseTriangleNormalEmit(boolean useTriangleNormalEmit) {
        this.useTriangleNormalEmit = useTriangleNormalEmit;
    }
//    
//    /**
//     * determines if a collision between this trimesh and a given spatial occurs
//     * if it has true is returned, otherwise false is returned.
//     */
//    public boolean hasCollision(Spatial scene, boolean checkTriangles) {
//        if (this == scene || !isCollidable || !scene.isCollidable()) {
//            return false;
//        }
//        if (getWorldBound().intersects(scene.getWorldBound())) {
//            if (scene instanceof Node) {
//                Node parent = (Node) scene;
//                for (int i = 0; i < parent.getQuantity(); i++) {
//                    if (hasCollision(parent.getChild(i), checkTriangles)) {
//                        return true;
//                    }
//                }
//
//                return false;
//            } 
//                
//            if (!checkTriangles) {
//                return true;
//            } 
//            
//            return hasTriangleCollision((TriMesh) scene);
//        } 
//            
//        return false;        
//    }
//
//    /**
//     * determines if this TriMesh has made contact with the give scene. The
//     * scene is recursively transversed until a trimesh is found, at which time
//     * the two trimesh OBBTrees are then compared to find the triangles that
//     * hit.
//     */
//    public void findCollisions(Spatial scene, CollisionResults results) {
//        if (this == scene || !isCollidable || !scene.isCollidable()) {
//            return;
//        }
//
//        if (getWorldBound().intersects(scene.getWorldBound())) {
//            if (scene instanceof Node) {
//                Node parent = (Node) scene;
//                for (int i = 0; i < parent.getQuantity(); i++) {
//                    findCollisions(parent.getChild(i), results);
//                }
//            } else {
//                results.addCollision(getParticleGeometry(), (Geometry) scene);
//            }
//        }
//    }
//
//    /**
//     * This function checks for intersection between this trimesh and the given
//     * one. On the first intersection, true is returned.
//     * 
//     * @param toCheck
//     *            The intersection testing mesh.
//     * @return True if they intersect.
//     */
//    public boolean hasTriangleCollision(TriMesh toCheck) {
//        TriMesh a,b;
//        for (int x = 0; x < getBatchCount(); x++) {
//            a = getBatch(x);
//            if (a == null || !a.isEnabled()) continue;
//            for (int y = 0; y < toCheck.getBatchCount(); y++) {
//                b = toCheck.getBatch(y);
//                if (b == null || !b.isEnabled()) continue;
//                if (hasTriangleCollision(toCheck, x, y))
//                    return true;
//            }
//        }
//        return false;
//    }
//
//    /**
//     * This function checks for intersection between this trimesh and the given
//     * one. On the first intersection, true is returned.
//     * 
//     * @param toCheck
//     *            The intersection testing mesh.
//     * @return True if they intersect.
//     */
//    public boolean hasTriangleCollision(TriMesh toCheck, int thisBatch, int checkBatch) {
//    	CollisionTree thisCT = CollisionTreeManager.getInstance().getCollisionTree(getBatch(thisBatch));
//    	CollisionTree toCheckCT = CollisionTreeManager.getInstance().getCollisionTree(toCheck.getBatch(checkBatch));
//    	
//        if (thisCT == null
//                || toCheckCT == null
//                || !isCollidable || !toCheck.isCollidable())
//            return false;
//        
//        thisCT.getBounds().transform(
//                worldRotation, worldTranslation, worldScale,
//                thisCT.getWorldBounds());
//        return thisCT.intersect(toCheckCT);        
//    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(useMeshTexCoords, "useMeshTexCoords", true);
        capsule.write(useTriangleNormalEmit, "useTriangleNormalEmit", true);
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        useMeshTexCoords = capsule.readBoolean("useMeshTexCoords", true);
        useTriangleNormalEmit = capsule.readBoolean("useTriangleNormalEmit", true);
    }

    @Override
    public TriMesh getParticleGeometry() {
        return (TriMesh)particleGeom;
    }
}
