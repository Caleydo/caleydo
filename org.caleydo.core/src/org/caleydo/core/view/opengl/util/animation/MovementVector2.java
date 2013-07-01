/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.util.animation;

import gleem.linalg.Vec2f;

public class MovementVector2 {

	protected MovementValue x;
	protected MovementValue y;

	public MovementVector2(Vec2f startPosition, Vec2f targetPosition, float duration) {

		x =
			new MovementValue(startPosition.x(), targetPosition.x(), (targetPosition.x() - startPosition.x())
				/ duration);
		y =
			new MovementValue(startPosition.y(), targetPosition.y(), (targetPosition.y() - startPosition.y())
				/ duration);
	}

	public MovementVector2(float startValueX, float targetValueX, float startValueY, float targetValueY,
		float duration) {

		x = new MovementValue(startValueX, targetValueX, (targetValueX - startValueX) / duration);
		y = new MovementValue(startValueY, targetValueY, (targetValueY - startValueY) / duration);

	}

	public MovementVector2(float startValueX, float targetValueX, float speedX, float startValueY,
		float targetValueY, float speedY) {
		x = new MovementValue(startValueX, targetValueX, speedX);
		y = new MovementValue(startValueY, targetValueY, speedY);
	}

	public void setX(float startValue, float targetValue, float speed) {
		x.setSpeed(speed);
		x.setTargetValue(targetValue);
		x.setMovementValue(startValue);
	}

	public void setY(float startValue, float targetValue, float speed) {
		y.setSpeed(speed);
		y.setTargetValue(targetValue);
		y.setMovementValue(startValue);
	}

	public float x() {
		return x.getMovementValue();
	}

	public float y() {
		return y.getMovementValue();
	}

	public boolean isTargetReached() {
		return (x.isTargetValueReached() && y.isTargetValueReached());
	}

	public void move(double timePassed) {
		x.move(timePassed);
		y.move(timePassed);
	}

	public Vec2f getVec2f() {
		return new Vec2f(x.getMovementValue(), y.getMovementValue());
	}

	public Vec2f getTargetVec2f() {
		return new Vec2f(x.getTargetValue(), y.getTargetValue());
	}

	public Vec2f getRemainingVec2f() {
		return new Vec2f(x.getTargetValue() - x.getMovementValue(), y.getTargetValue() - y.getMovementValue());
	}
}
