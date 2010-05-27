package com.jmex.physics.impl.jbullet.joints;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.dynamics.constraintsolver.TypedConstraint;
import com.bulletphysics.linearmath.Transform;
import com.jmex.physics.RotationalJointAxis;

public class JBulletRotationalJointAxis extends RotationalJointAxis {

	TypedConstraint myJoint;
	int myAxis=0;
	
    private float availableAccelleration = 0f;
    private float desiredVelocity = 0f;
    private float positionMinimum = -BulletGlobals.FLT_EPSILON;
    private float positionMaximum = BulletGlobals.FLT_EPSILON;

    @Override
    public float getAvailableAcceleration() {
        return availableAccelleration;
    }

    @Override
    public float getDesiredVelocity() {
        return desiredVelocity;
    }

    @Override
    public float getPosition() {
    	if(myJoint==null)
    		return 0;
    	if(myJoint instanceof JBulletHingeJoint)
    		return ((JBulletHingeJoint)myJoint).getHingeAngle();
    	if(myJoint instanceof JBulletBallJoint)
    		return ((JBulletBallJoint)myJoint).getAngle(myAxis);
    	throw new IllegalStateException ("Joint attached to this axis is not a ball or hinge joint!");
    }

    @Override
    public float getPositionMaximum() {
    	return positionMaximum;
    }

    @Override
    public float getPositionMinimum() {
        return positionMinimum;
    }

    @Override
    public float getVelocity() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setAvailableAcceleration( float value ) {
        if(this.availableAccelleration==value)
        	return;
        this.availableAccelleration = value;
        applyAvailableAccelleration();
    }

    void applyAvailableAccelleration()
    {
        if(myJoint!=null)
        {
        	if(myJoint instanceof JBulletHingeJoint)
        	{
        		JBulletHingeJoint hj=(JBulletHingeJoint)myJoint;
        		if(availableAccelleration<=0)
        		{
        			hj.enableAngularMotor(true, desiredVelocity, 0);
        		}
        		Transform t=new Transform();
        		float angMomentum = ((hj.rbAMass*hj.rbADistanceFromPivot)+(hj.rbBMass*hj.rbBDistanceFromPivot));
        		javax.vecmath.Vector3f ang=new javax.vecmath.Vector3f(0,0,1);
        		hj.getAFrame(t).basis.transform(ang);
        		ang.scale(availableAccelleration);
        		hj.inertialTensorA.transform(ang);
        		angMomentum+=ang.length();
        		ang=new javax.vecmath.Vector3f(0,0,1);
        		hj.getBFrame(t).basis.transform(ang);
        		ang.scale(availableAccelleration);
        		hj.inertialTensorB.transform(ang);
        		angMomentum+=ang.length();
    			hj.enableAngularMotor(true, desiredVelocity, availableAccelleration/angMomentum);
    			hj.getRigidBodyA().activate(true);
    			hj.getRigidBodyB().activate(true);
        	}
        	if(myJoint instanceof JBulletBallJoint)
        	{
        		JBulletBallJoint bj=(JBulletBallJoint)myJoint;
        		if(availableAccelleration<=0)
        		{
        			bj.getRotationalLimitMotor(myAxis).maxMotorForce=0;
        			return;
        		}
        		Transform t=new Transform();
        		float angMomentum = ((bj.rbAMass*bj.rbADistanceFromPivot)+(bj.rbBMass*bj.rbBDistanceFromPivot));
           		javax.vecmath.Vector3f ang=new javax.vecmath.Vector3f((myAxis==0)?1:0,(myAxis==1)?1:0,(myAxis==2)?1:0);
        		bj.getFrameOffsetA(t).basis.transform(ang);
        		ang.scale(availableAccelleration);
        		bj.inertialTensorA.transform(ang);
        		angMomentum+=ang.length();
        		ang=new javax.vecmath.Vector3f((myAxis==0)?1:0,(myAxis==1)?1:0,(myAxis==2)?1:0);
        		bj.getFrameOffsetB(t).basis.transform(ang);
        		ang.scale(availableAccelleration);
        		bj.inertialTensorB.transform(ang);
        		angMomentum+=ang.length();
        		bj.getRotationalLimitMotor(myAxis).maxMotorForce=availableAccelleration/angMomentum;
        		bj.getRigidBodyA().activate();
        		bj.getRigidBodyB().activate();
        	}
        }
    }
    
    @Override
    public void setDesiredVelocity( float value ) {
        if(this.desiredVelocity==value)
        	return;
        this.desiredVelocity = value;
        applyDesiredVelocity();
    }

    void applyDesiredVelocity()
    {
        if(myJoint!=null)
        {
        	if(myJoint instanceof JBulletHingeJoint)
        	{
        		JBulletHingeJoint hj=(JBulletHingeJoint)myJoint;
         		Transform t=new Transform();
        		float angMomentum = ((hj.rbAMass*hj.rbADistanceFromPivot)+(hj.rbBMass*hj.rbBDistanceFromPivot));
        		javax.vecmath.Vector3f ang=new javax.vecmath.Vector3f(0,0,1);
        		hj.getAFrame(t).basis.transform(ang);
        		ang.scale(availableAccelleration);
        		hj.inertialTensorA.transform(ang);
        		angMomentum+=ang.length();
        		ang=new javax.vecmath.Vector3f(0,0,1);
        		hj.getBFrame(t).basis.transform(ang);
        		ang.scale(availableAccelleration);
        		hj.inertialTensorB.transform(ang);
        		angMomentum+=ang.length();
    			hj.enableAngularMotor(true, desiredVelocity, availableAccelleration/angMomentum);
    			hj.getRigidBodyA().activate();
    			hj.getRigidBodyB().activate();
        	}
        	if(myJoint instanceof JBulletBallJoint)
        	{
        		((JBulletBallJoint)myJoint).getRotationalLimitMotor(myAxis).targetVelocity=desiredVelocity;
        		((JBulletBallJoint)myJoint).getRigidBodyA().activate();
        		((JBulletBallJoint)myJoint).getRigidBodyB().activate();
        	}
        }
    }
    
    @Override
    public void setPositionMaximum( float value ) {
    	this.positionMaximum = value;
    	if(myJoint!=null)
    	{
    		if(myJoint instanceof JBulletHingeJoint)
    		{
    			((JBulletHingeJoint)myJoint).setLimit(positionMinimum, positionMaximum);
    			return;
    		}
    		if(myJoint instanceof JBulletBallJoint)
    		{
    			((JBulletBallJoint)myJoint).setLimit(myAxis, positionMinimum, positionMaximum);
    			return;
    		}
    	}
    }

    @Override
    public void setPositionMinimum( float value ) {
        this.positionMinimum = value;
    	if(myJoint!=null)
    	{
    		if(myJoint instanceof JBulletHingeJoint)
    		{
    			((JBulletHingeJoint)myJoint).setLimit(positionMinimum, positionMaximum);
    		}
    		if(myJoint instanceof JBulletBallJoint)
    		{
    			((JBulletBallJoint)myJoint).setLimit(myAxis, positionMinimum, positionMaximum);
    			return;
    		}
    	}
    }

}
