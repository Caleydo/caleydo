package com.jmex.physics.impl.jbullet.geometry.proxies;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.BroadphaseNativeType;
import com.bulletphysics.collision.shapes.CapsuleShapeZ;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.geometry.PhysicsCapsule;

public class CapsuleProxy extends CapsuleShapeZ implements ShapeProxy {

	private PhysicsCapsule jmeShape;
	
	public CapsuleProxy(float radius, float height, PhysicsCapsule capsule) {
		super(radius,height);
		implicitShapeDimensions.set(radius, radius, 0.5f * height);
		jmeShape = capsule;
	}

	public PhysicsCollisionGeometry getJmeShape() {
		return jmeShape;
	}
	
	@Override
	public void batchedUnitVectorGetSupportingVertexWithoutMargin(Vector3f[] vectors, Vector3f[] supportVerticesOut, int numVectors) {
		// TODO: implement
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public float getRadius() {
		return implicitShapeDimensions.x;
	}

	public float getHalfHeight() {
		return implicitShapeDimensions.z;
	}

}
