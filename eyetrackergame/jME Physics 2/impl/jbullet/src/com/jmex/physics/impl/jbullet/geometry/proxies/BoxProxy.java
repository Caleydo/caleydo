package com.jmex.physics.impl.jbullet.geometry.proxies;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.geometry.PhysicsBox;

public class BoxProxy extends BoxShape implements ShapeProxy {

	private PhysicsBox jmeShape;
	
	public BoxProxy(Vector3f boxHalfExtents, PhysicsBox box) {
		super(boxHalfExtents);
		jmeShape = box;
	}

	public PhysicsCollisionGeometry getJmeShape() {
		return jmeShape;
	}
	
}
