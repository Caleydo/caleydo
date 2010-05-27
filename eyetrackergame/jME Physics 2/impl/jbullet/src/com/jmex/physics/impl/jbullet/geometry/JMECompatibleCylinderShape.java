package com.jmex.physics.impl.jbullet.geometry;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.CylinderShapeZ;
import com.bulletphysics.linearmath.MatrixUtil;
import com.bulletphysics.linearmath.Transform;

/**
 * 
 * This CylinderShape semi-hack addresses two problems.  First, the JBullet
 * default cylinder is formed around the Y axis, rather than Z.  Secondly,
 * the AABB formula built into the default CylinderShape is wrong.  I've
 * gone ahead and grabbed the original implementation (mysteriously commented
 * out) from BoxShape and pasted it in here.  It works well enough for now.
 * 
 * The alternative is late-detected collisions which obviously will rebound
 * very, very oddly.
 * 
 * @author Falken224
 *
 */
public class JMECompatibleCylinderShape extends CylinderShapeZ {

	public JMECompatibleCylinderShape(Vector3f halfExtents) {
		super(halfExtents);
	}

}
