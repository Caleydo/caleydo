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

package com.jmex.effects.cloth;

import java.io.IOException;
import java.util.ArrayList;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.CollisionTreeManager;
import com.jme.intersection.CollisionData;
import com.jme.intersection.TriangleCollisionResults;
import com.jme.math.Vector3f;
import com.jme.math.spring.SpringPoint;
import com.jme.scene.TriMesh;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.geom.BufferUtils;

/**
 * <code>CollidingClothPatch</code> is a ClothPatch with the ability to
 * interact with other objects. Override handleCollision to change collision
 * behavior.
 * 
 * @author Joshua Slack
 * @version $Id: CollidingClothPatch.java 4133 2009-03-19 20:40:11Z blaine.dev $
 */
public class CollidingClothPatch extends ClothPatch {
    private static final long serialVersionUID = 1L;

    /** Used for storing the results of collisions. */
    protected TriangleCollisionResults results;
    /** Array of TriMesh objects to check against for collision. */
    protected ArrayList<TriMesh> colliders;

    // Temp vars used to eliminate object creation
    protected SpringPoint[] srcTemps = new SpringPoint[3];
    protected Vector3f calcTemp = new Vector3f();

    /**
     * Public constructor.
     * 
     * @param name
     *            String
     * @param nodesX
     *            number of nodes wide this cloth will be.
     * @param nodesY
     *            number of nodes high this cloth will be.
     * @param springLength
     *            distance between each node
     * @param nodeMass
     *            mass of an individual node in this Cloth.
     */
    public CollidingClothPatch(String name, int nodesX, int nodesY,
            float springLength, float nodeMass) {
        super(name, nodesX, nodesY, springLength, nodeMass);
        setModelBound(new BoundingBox());
        updateModelBound();
        results = new TriangleCollisionResults();
        colliders = new ArrayList<TriMesh>();
    }

    /**
     * Calls super and then updates model bound and collision info.
     * 
     * @param sinceLast
     *            float
     */
    protected void calcForces(float sinceLast) {
        super.calcForces(sinceLast);
        updateModelBound();
        CollisionTreeManager.getInstance().updateCollisionTree(this);
        checkForCollisions();
    }

    /**
     * Check each collider for collision with this Cloth.
     */
    protected void checkForCollisions() {
        CollisionData data;
        for (int x = colliders.size(); --x >= 0;) {
            results.clear();
            findCollisions(colliders.get(x), results);
            for (int y = results.getNumber(); --y >= 0;) {
                data = results.getCollisionData(y);
                for (int i = 0; i < data.getSourceTris().size(); i++) {
                    int srcTriIndex = data.getSourceTris().get(i).intValue();
                    int tgtTriIndex = data.getTargetTris().get(i).intValue();
                    handleCollision((TriMesh) data.getTargetMesh(),
                            srcTriIndex, tgtTriIndex);
                }
            }
        }
    }

    /**
     * Given the starting triangle index of the two triangles intersecting,
     * decide what to do with those triangles.
     * 
     * @param target
     *            TriMesh
     * @param srcTriIndex
     *            int
     * @param tgtTriIndex
     *            int
     */
    protected void handleCollision(TriMesh target, int srcTriIndex,
            int tgtTriIndex) {
        for (int x = 0; x < 3; x++) {
            srcTemps[x] = system.getNode(getIndexBuffer().get(
                    srcTriIndex * 3 + x));
            if (srcTemps[x].invMass != 0)
                BufferUtils.populateFromBuffer(srcTemps[x].position, target
                        .getVertexBuffer(), target.getIndexBuffer().get(
                        tgtTriIndex * 3 + x));
            srcTemps[x].acceleration.multLocal(.8f); // simple frictional
                                                        // force here.
        }
    }

    /**
     * Adds a TriMesh to check for collision with.
     * 
     * @param item
     *            TriMesh
     */
    public void addCollider(TriMesh item) {
        colliders.add(item);
    }

    /**
     * Remove a given TriMesh from collision consideration.
     * 
     * @param item
     *            TriMesh
     * @return true if found and removed
     */
    public boolean removeCollider(TriMesh item) {
        return colliders.remove(item);
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.writeSavableArrayList(colliders, "colliders",
                new ArrayList<TriMesh>());
    }

    @SuppressWarnings("unchecked")
    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        colliders = capsule.readSavableArrayList("colliders",
                new ArrayList<TriMesh>());
    }

}
