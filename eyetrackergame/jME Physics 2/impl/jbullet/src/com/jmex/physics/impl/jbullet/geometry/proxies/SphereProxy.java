package com.jmex.physics.impl.jbullet.geometry.proxies;

import com.bulletphysics.collision.shapes.SphereShape;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.geometry.PhysicsSphere;

public class SphereProxy extends SphereShape implements ShapeProxy {

	private PhysicsSphere jmeShape;
	
	public SphereProxy(float radius, PhysicsSphere sphere) {
		super(radius);
		jmeShape = sphere;
	}

	public PhysicsCollisionGeometry getJmeShape() {
		return jmeShape;
	}
	
}
