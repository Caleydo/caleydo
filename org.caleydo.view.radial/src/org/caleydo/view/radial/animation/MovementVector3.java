/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.radial.animation;

import gleem.linalg.Vec3f;

public class MovementVector3
	extends MovementVector2 {
	private MovementValue z;

	public MovementVector3(Vec3f startPosition, Vec3f targetPosition, float duration) {

		super(startPosition.x(), targetPosition.x(), (targetPosition.x() - startPosition.x()) / duration,
			startPosition.y(), targetPosition.y(), (targetPosition.y() - startPosition.y()) / duration);
		z =
			new MovementValue(startPosition.z(), targetPosition.z(), (targetPosition.z() - startPosition.z())
				/ duration);
	}

	public MovementVector3(float startValueX, float targetValueX, float speedX, float startValueY,
		float targetValueY, float speedY, float startValueZ, float targetValueZ, float speedZ) {

		super(startValueX, targetValueX, speedX, startValueY, targetValueY, speedY);
		z = new MovementValue(startValueZ, targetValueZ, speedZ);
	}

	public void setZ(float startValue, float targetValue, float speed) {
		z.setSpeed(speed);
		z.setTargetValue(targetValue);
		z.setMovementValue(startValue);
	}

	public float z() {
		return z.getMovementValue();
	}

	@Override
	public boolean isTargetReached() {
		return (super.isTargetReached() && z.isTargetValueReached());
	}

	@Override
	public void move(double timePassed) {
		super.move(timePassed);
		z.move(timePassed);
	}

	public Vec3f getVec3f() {
		return new Vec3f(x.getMovementValue(), y.getMovementValue(), z.getMovementValue());
	}

	public Vec3f getTargetVec3f() {
		return new Vec3f(x.getTargetValue(), y.getTargetValue(), z.getTargetValue());
	}

	public Vec3f getRemainingVec3f() {
		return new Vec3f(x.getTargetValue() - x.getMovementValue(),
			y.getTargetValue() - y.getMovementValue(), z.getTargetValue() - z.getMovementValue());
	}
}
