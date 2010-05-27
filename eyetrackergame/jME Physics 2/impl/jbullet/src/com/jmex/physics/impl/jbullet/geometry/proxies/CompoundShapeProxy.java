package com.jmex.physics.impl.jbullet.geometry.proxies;

import com.bulletphysics.collision.shapes.CompoundShape;
import com.jmex.physics.PhysicsCollisionGeometry;

public class CompoundShapeProxy extends CompoundShape implements ShapeProxy {

	private PhysicsCollisionGeometry jmeShape;
	
	public CompoundShapeProxy(PhysicsCollisionGeometry jmeShape) {
		this.jmeShape = jmeShape;
	}

	public PhysicsCollisionGeometry getJmeShape() {
		return jmeShape;
	}
}
