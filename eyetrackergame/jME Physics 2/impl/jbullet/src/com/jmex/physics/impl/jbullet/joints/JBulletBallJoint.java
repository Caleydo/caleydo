package com.jmex.physics.impl.jbullet.joints;

import javax.vecmath.Matrix3f;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.Generic6DofConstraint;
import com.bulletphysics.linearmath.Transform;
import com.jmex.physics.impl.jbullet.JBulletRigidBody;

public class JBulletBallJoint extends Generic6DofConstraint {

	float rbAMass=0;
	float rbBMass=0;
	float rbADistanceFromPivot=0;
	float rbBDistanceFromPivot=0;
	Matrix3f inertialTensorA=new Matrix3f();
	Matrix3f inertialTensorB=new Matrix3f();

	public JBulletBallJoint(RigidBody rbA, RigidBody rbB, Transform rbAFrame,
			Transform rbBFrame, boolean useLinearReferenceFrameA) {
		super(rbA, rbB, rbAFrame, rbBFrame, useLinearReferenceFrameA);
		if(rbA instanceof JBulletRigidBody)
		{
			rbAMass=1/rbA.getInvMass();
			rbA.getInvInertiaTensorWorld(inertialTensorA);
			rbADistanceFromPivot=rbAFrame.origin.length();
		}
		if(rbB instanceof JBulletRigidBody)
		{
			rbBMass=1/rbB.getInvMass();
			rbBDistanceFromPivot=rbBFrame.origin.length();
			rbB.getInvInertiaTensorWorld(inertialTensorB);
		}
	}

	
	
}
