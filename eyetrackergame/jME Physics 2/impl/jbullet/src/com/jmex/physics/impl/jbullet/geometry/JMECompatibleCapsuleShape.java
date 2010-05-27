package com.jmex.physics.impl.jbullet.geometry;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.BroadphaseNativeType;
import com.bulletphysics.collision.shapes.CapsuleShapeZ;

/**
 * 
 * This is a hack to try to get the JBullet capsule shape to match up with
 * the JMEPhysics PhysicsCapsule.  For starters, JBullet capsules are generated
 * around the Y axis, rather than Z.  Secondly, for some reason the scaling
 * doesn't match up.  A JBullet CapsuleShape seems to be exactly 1/2 the width,
 * height, and depth of a corresponding JMEPhysics PhysicsCapsule.
 * 
 * I've chosen to compensate for the scaling problem in the JBulletCapsule
 * class by simply creating a capsule 2x the size.  This solution may need
 * to be revisited.
 * 
 * @author Falken224
 *
 */

public class JMECompatibleCapsuleShape extends CapsuleShapeZ {
	public JMECompatibleCapsuleShape(float radius, float height) {
		super(radius,height);
		implicitShapeDimensions.set(radius, radius, 0.5f * height);
	}

	@Override
	public void batchedUnitVectorGetSupportingVertexWithoutMargin(Vector3f[] vectors, Vector3f[] supportVerticesOut, int numVectors) {
		// TODO: implement
		throw new UnsupportedOperationException("Not supported yet.");
	}


	@Override
	public BroadphaseNativeType getShapeType() {
		return BroadphaseNativeType.CAPSULE_SHAPE_PROXYTYPE;
	}

	@Override
	public String getName() {
		return "CapsuleShape";
	}
	
	public float getRadius() {
		return implicitShapeDimensions.x;
	}

	public float getHalfHeight() {
		return implicitShapeDimensions.z;
	}

}
