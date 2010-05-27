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

import com.jme.math.Vector3f;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.geometry.PhysicsSphere;
import com.jmex.physics.impl.ode.OdePhysicsNode;
import org.odejava.GeomSphere;
import org.odejava.PlaceableGeom;

/**
 * @author Irrisor
 */
public class OdeSphere extends PhysicsSphere implements OdeGeometry {
    private final GeomSphere geom;

    public PlaceableGeom getOdeGeom() {
        return geom;
    }

    public OdeSphere( PhysicsNode node ) {
        super( node );
        geom = new GeomSphere( getName(), 1 );
        geom.setGeometry( this );
    }

    @Override
    public void updateWorldVectors() {
        super.updateWorldVectors();
        //TODO: only if necessary!
        ( (OdePhysicsNode) getPhysicsNode() ).updateTransforms( geom );
        final Vector3f worldScale = this.worldScale;
        if ( worldScale.x <= 0 || worldScale.y <= 0 || worldScale.z <= 0 ) {
            // this makes ODE crash to prefer to throw an exception
            throw new IllegalArgumentException( "scale must not have 0 as a component!" );
        }
        float scale = Math.max( Math.max( worldScale.x, worldScale.y ), worldScale.z );
        worldScale.set( scale, scale, scale );
        geom.setRadius( scale );
    }
}

/*
 * $Log: OdeSphere.java,v $
 * Revision 1.12  2007/02/22 15:55:15  jspohr
 * Changed behavior in PhysicsCollisionGeometry: geometries can be attached to a different PhysicsNode, the underlying implementation is required to handle this.
 * PhysicsNode.mergeWith(PhysicsNode) is deprecated, use the PhysicsNodeMerger helper class instead.
 *
 */

