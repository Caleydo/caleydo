/*
 * Copyright (c) 2005-2007 jME Physics 2
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
package com.jmex.physics.geometry;

import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Line;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsDebugger;
import com.jmex.physics.PhysicsNode;

/**
 * Ray has a direction of (1,1,1) and starts at (0,0,0) - change direction via local scale and start via local
 * translation.
 *
 * @author Irrisor
 */
public abstract class PhysicsRay extends PhysicsCollisionGeometry {
    protected PhysicsRay( PhysicsNode node ) {
        super( node );
    }


    private static final Line debugShape = new Line( "PhysicsRay",
            new Vector3f[]{ new Vector3f(), new Vector3f( 1, 1, 1 )}, null, null, null );

    static {
        PhysicsDebugger.setupDebugGeom( debugShape );
    }

    @Override
    protected void drawDebugShape( PhysicsNode physicsNode, Renderer r ) {
        Vector3f size = debugShape.getLocalScale();
        size.set( getWorldScale() );
        debugShape.setLocalTranslation( getWorldTranslation() );
        debugShape.setLocalRotation( getWorldRotation() );
        debugShape.updateWorldVectors();
        PhysicsDebugger.drawDebugShape( debugShape, getWorldTranslation(), this, r,
                Math.max( Math.max( size.x, size.y ), size.z ) );
    }

    @Override
    public float getVolume() {
        return 0;
    }

    @Override
    public Class getClassTag() {
        return PhysicsRay.class;
    }
}

/*
 * $Log: PhysicsRay.java,v $
 * Revision 1.3  2007/09/22 14:28:37  irrisor
 * spongy contacts, improved material handling, center of mass correctly handled now, breakable joints, use odejava 0.3.1
 *
 * Revision 1.2  2006/12/23 22:06:57  irrisor
 * Ray added, Picking interface (natives pending), JOODE implementation added, license header added
 *
 * Revision 1.1  2006/12/15 15:04:32  irrisor
 * Contribution by tmagnaud: facility for computing volume, center of mass and inertia of trimeshes (currently only volume used)
 *
 */

