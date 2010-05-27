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
import com.jmex.physics.geometry.PhysicsRay;
import org.odejava.Geom;
import org.odejava.GeomRay;


/**
 * @author Irrisor
 */
public class OdeRay extends PhysicsRay implements OdeGeometry {

    private GeomRay geom;

    public Geom getOdeGeom() {
        return geom;
    }

    public OdeRay( PhysicsNode node ) {
        super( node );
        geom = new GeomRay( getName(), new Vector3f( 1, 1, 1 ).length() );
        geom.setGeometry( this );
    }

    protected void activate() {
    }

    private final Vector3f direction = new Vector3f();

    @Override
    public void updateWorldVectors() {
        super.updateWorldVectors();
        direction.set( 1, 1, 1 );
        getWorldRotation().mult( direction.multLocal( getWorldScale() ), direction );
        float length = direction.length();
        geom.setLength( length );
        direction.divideLocal( length );
        Vector3f pos = getWorldTranslation();
        geom.setStartPosAndDirection( pos.x, pos.y, pos.z, direction.x, direction.y, direction.z );
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        geom.delete();
    }
}

/*
 * $Log: OdeRay.java,v $
 * Revision 1.4  2007/02/22 15:55:16  jspohr
 * Changed behavior in PhysicsCollisionGeometry: geometries can be attached to a different PhysicsNode, the underlying implementation is required to handle this.
 * PhysicsNode.mergeWith(PhysicsNode) is deprecated, use the PhysicsNodeMerger helper class instead.
 *
 * Revision 1.3  2006/12/23 22:06:53  irrisor
 * Ray added, Picking interface (natives pending), JOODE implementation added, license header added
 *
 * Revision 1.2  2006/12/15 16:22:29  irrisor
 * rays don't generate contact points, max contact increased in windows-lib (linux and macos rebuild pending to allow for correct TriMeshTest)
 *
 * Revision 1.1  2006/12/15 15:06:50  irrisor
 * Ray added
 *
 */

