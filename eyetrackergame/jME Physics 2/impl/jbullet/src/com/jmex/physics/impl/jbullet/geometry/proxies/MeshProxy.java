package com.jmex.physics.impl.jbullet.geometry.proxies;

import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;
import com.jmex.physics.PhysicsCollisionGeometry;

public class MeshProxy extends BvhTriangleMeshShape implements ShapeProxy {

	private PhysicsCollisionGeometry jmeShape;
	
	public MeshProxy(TriangleIndexVertexArray vertexData, boolean useQuantizedAABBCompression, PhysicsCollisionGeometry shape) {
		super(vertexData,useQuantizedAABBCompression);
		jmeShape = shape;
	}

	public PhysicsCollisionGeometry getJmeShape() {
		return jmeShape;
	}
	
}
