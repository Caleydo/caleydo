package com.jmex.physics.impl.jbullet.geometry.proxies;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.CylinderShapeZ;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.geometry.PhysicsCylinder;

public class CylinderProxy extends CylinderShapeZ implements ShapeProxy {

	private PhysicsCylinder jmeShape;
	
	public CylinderProxy(Vector3f halfExtents, PhysicsCylinder box) {
		super(halfExtents);
		jmeShape = box;
	}

	public PhysicsCollisionGeometry getJmeShape() {
		return jmeShape;
	}
	
}
