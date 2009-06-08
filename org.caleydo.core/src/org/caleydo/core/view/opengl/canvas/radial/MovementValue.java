package org.caleydo.core.view.opengl.canvas.radial;

public class MovementValue {

	public static final int CRITERION_GREATER_OR_EQUAL = 0;
	public static final int CRITERION_SMALLER_OR_EQUAL = 1;

	private float fMovementValue;
	private float fTargetValue;
	private float fSpeed;
	private int iCreterionType;

	public MovementValue(float fStartValue, float fTargetValue, float fSpeed, int iCreterionType) {
		this.fMovementValue = fStartValue;
		this.fTargetValue = fTargetValue;
		this.fSpeed = fSpeed;
		this.iCreterionType = iCreterionType;
	}
	
	public void move(double dTimePassed) {
		fMovementValue += (fSpeed * dTimePassed);
		if(isTargetValueReached())
			fMovementValue = fTargetValue;
	}

	public boolean isTargetValueReached() {
		if (iCreterionType == CRITERION_GREATER_OR_EQUAL) {
			return (fMovementValue >= fTargetValue);
		}
		return (fMovementValue <= fTargetValue);
	}

	public float getTargetValue() {
		return fTargetValue;
	}

	public void setTargetValue(float fTargetValue) {
		this.fTargetValue = fTargetValue;
	}

	public float getMovementValue() {
		return fMovementValue;
	}

	public void setMovementValue(float fMovementValue) {
		this.fMovementValue = fMovementValue;
	}

	public float getSpeed() {
		return fSpeed;
	}

	public void setSpeed(float fSpeed) {
		this.fSpeed = fSpeed;
	}

	public int getCreterionType() {
		return iCreterionType;
	}

	public void setCreterionType(int iCreterionType) {
		this.iCreterionType = iCreterionType;
	}

}
