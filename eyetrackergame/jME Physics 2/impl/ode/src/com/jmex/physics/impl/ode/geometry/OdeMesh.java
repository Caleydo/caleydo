/*
 * Copyright (c) 2005-2006 jME Physics 2
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of 'jME Physics 2' nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
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
package com.jmex.physics.impl.ode.geometry;

import com.jme.math.Matrix3f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.TriMesh;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsDebugger;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.geometry.PhysicsMesh;
import com.jmex.physics.geometry.TriMeshMassProperties;
import com.jmex.physics.impl.ode.OdePhysicsNode;
import org.odejava.GeomTriMesh;
import org.odejava.PlaceableGeom;

/**
 * @author Irrisor
 */
public class OdeMesh extends PhysicsMesh implements OdeGeometry {
    private final GeomTriMesh geom;

    public PlaceableGeom getOdeGeom() {
        return geom;
    }

    public OdeMesh( PhysicsNode node ) {
        super( node );
        geom = new GeomTriMesh( getName(), null );
        geom.setGeometry( this );
        geom.setTCEnabled( false );
        geom.setEnabled( false );
    }

    @Override
    public void copyFrom( TriMesh triMesh ) {
        float volume;
        Vector3f centerOfMass;
        Matrix3f inertia;
        if ( getParent() instanceof DynamicPhysicsNode ) {
            triMesh.updateWorldVectors();
            TriMeshMassProperties properties = new TriMeshMassProperties( triMesh );
            volume = properties.getVolume();
            centerOfMass = properties.getCenterOfMass();
            inertia = properties.getInertia();
        } else {
            volume = 0;
            centerOfMass = new Vector3f();
            inertia = null;
        }
        copyFrom( triMesh, volume, centerOfMass, inertia );
    }

    public void copyFrom( TriMesh triMesh, float volume, Vector3f centerOfMass, Matrix3f inertia ) {
        geom.updateData( triMesh );
        geom.setEnabled( true );
        getLocalTranslation().set( triMesh.getLocalTranslation() );
        getLocalScale().set( triMesh.getLocalScale() );
        getLocalRotation().set( triMesh.getLocalRotation() );
        this.volume = volume;
        this.centerOfMass = centerOfMass;
        this.inertia = inertia;
    }

    @Override
    public void updateWorldVectors() {
        super.updateWorldVectors();
        //TODO: only if necessary!
        if ( geom != null ) {
            ( (OdePhysicsNode) getPhysicsNode() ).updateTransforms( geom );
            //TODO: scale
//            final Vector3f worldScale = this.worldScale;
//            worldScale.y = worldScale.x;
//            geom.setRadius( worldScale.x );
//            geom.setLength( worldScale.z );
        }
    }

    @Override
    protected void drawDebugShape( PhysicsNode physicsNode, Renderer r ) {
        PhysicsDebugger.drawDebugShape( null, getWorldTranslation(), this, r, 1 );
    }

    private float volume;
    private Vector3f centerOfMass; //TODO: use this in inertia computation of the body
    private Matrix3f inertia; //TODO: use this in inertia computation of the body

    @Override
    public float getVolume() {
        return volume;
    }

    private final Vector3f lastTranslation = new Vector3f();
    private final Quaternion lastRotation = new Quaternion();

    public void updateOdeLastTransformation() {
        if ( getParent() instanceof DynamicPhysicsNode ) {
            geom.setLastTransformation( lastTranslation, lastRotation );
            lastTranslation.set( getWorldTranslation() );
            lastRotation.set( getWorldRotation() );
        }
    }
}

/*
 * $log$
 */

