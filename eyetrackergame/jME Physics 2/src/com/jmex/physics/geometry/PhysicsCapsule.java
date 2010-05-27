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

import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.shape.Cylinder;
import com.jme.scene.shape.Dome;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsDebugger;
import com.jmex.physics.PhysicsNode;

/**
 * Capsule has a radius of 1, a height of 1, and a center of (0,0,0) - change center via local translation,
 * radius via local x scale and height via local z scale.
 *
 * @author Irrisor
 */
public abstract class PhysicsCapsule extends PhysicsCollisionGeometry {

    protected PhysicsCapsule( PhysicsNode node ) {
        super( node );
    }

    private static final Node debugShape = new Node( "PhysicsCapsule" );

    private static final Cylinder cylinder = new Cylinder( "PhysicsCapsule", 8, 10, 1, 1, true );
    private static final Dome dome1 = new Dome( "PhysicsCapsule", 5, 10, 1 );
    private static final Dome dome2 = new Dome( "PhysicsCapsule", 5, 10, 1 );

    static {
        debugShape.attachChild( cylinder );
        debugShape.attachChild( dome1 );
        debugShape.attachChild( dome2 );
        PhysicsDebugger.setupDebugGeom( cylinder );
        PhysicsDebugger.setupDebugGeom( dome1 );
        PhysicsDebugger.setupDebugGeom( dome2 );
        dome2.getLocalRotation().fromAngleNormalAxis( FastMath.PI / 2, new Vector3f( 1, 0, 0 ) );
        dome1.getLocalRotation().fromAngleNormalAxis( -FastMath.PI / 2, new Vector3f( 1, 0, 0 ) );
    }

    @Override
    protected void drawDebugShape( PhysicsNode physicsNode, Renderer r ) {
        final Vector3f worldScale = getWorldScale();
        final float radius = worldScale.x;
        final float height = worldScale.z;
        Vector3f size = cylinder.getLocalScale();
        size.set( radius, radius, height ); // keep the capsule base a circle
        dome1.getLocalScale().set( radius, radius, radius );
        dome2.getLocalScale().set( radius, radius, radius );
        dome1.getLocalTranslation().set( 0, 0, -height / 2 );
        dome2.getLocalTranslation().set( 0, 0, height / 2 );
        debugShape.setLocalTranslation( getWorldTranslation() );
        debugShape.getLocalRotation().set( getWorldRotation() );
        debugShape.updateGeometricState( 0, true );
        PhysicsDebugger.drawDebugShape( PhysicsCapsule.debugShape, getWorldTranslation(), this, r,
                Math.max( Math.max( size.x, size.y ), size.z ) );
    }

    @Override
    public float getVolume() {
        Vector3f size = getWorldScale();
        final float radius = size.x;
        final float height = size.z;
        return FastMath.PI * FastMath.sqr( radius ) * ( height + 4f / 3 * radius );
    }

	@Override
	public Class getClassTag() {
		return PhysicsCapsule.class;
	}
}

/*
 * $log$
 */

