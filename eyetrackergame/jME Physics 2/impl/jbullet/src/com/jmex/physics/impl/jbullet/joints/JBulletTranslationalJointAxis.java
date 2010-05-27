package com.jmex.physics.impl.jbullet.joints;

import com.jmex.physics.TranslationalJointAxis;

public class JBulletTranslationalJointAxis extends TranslationalJointAxis {

	JBulletSliderJoint myJoint;
	private float availableAccelleration = 0f;
    private float desiredVelocity = 0f;
    private float positionMinimum = Float.NEGATIVE_INFINITY;
    private float positionMaximum = Float.POSITIVE_INFINITY;

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
    	return myJoint.getLinearPos();
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
        return 0;
    }

    @Override
    public void setAvailableAcceleration( float value ) {
        this.availableAccelleration = value;
        if(myJoint!=null)
        {
        	myJoint.setMaxLinMotorForce(availableAccelleration / myJoint.totalMass);
        	myJoint.setPoweredLinMotor(availableAccelleration>0);
        }
    }

    @Override
    public void setDesiredVelocity( float value ) {
        this.desiredVelocity = value;
        if(myJoint!=null)
        	myJoint.setTargetLinMotorVelocity(desiredVelocity);
    }

    @Override
    public void setPositionMaximum( float value ) {
        this.positionMaximum = value;
        if(myJoint!=null)
        	myJoint.setUpperLinLimit(positionMaximum);
    }

    @Override
    public void setPositionMinimum( float value ) {
        this.positionMinimum = value;
        if(myJoint!=null)
        	myJoint.setLowerLinLimit(positionMinimum);
    }
}
