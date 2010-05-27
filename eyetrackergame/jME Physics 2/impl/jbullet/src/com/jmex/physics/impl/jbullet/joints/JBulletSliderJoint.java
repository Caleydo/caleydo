package com.jmex.physics.impl.jbullet.joints;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.SliderConstraint;
import com.bulletphysics.linearmath.Transform;
import com.jmex.physics.impl.jbullet.JBulletRigidBody;

public class JBulletSliderJoint extends SliderConstraint {

	float totalMass;
	
	public JBulletSliderJoint(RigidBody rbA, RigidBody rbB, Transform frameInA,
			Transform frameInB, boolean useLinearReferenceFrameA) {
		super(rbA, rbB, frameInA, frameInB, useLinearReferenceFrameA);
		totalMass=1/rbA.getInvMass();
		if(rbB instanceof JBulletRigidBody)
			totalMass+=1/rbB.getInvMass();
	}
	
}
