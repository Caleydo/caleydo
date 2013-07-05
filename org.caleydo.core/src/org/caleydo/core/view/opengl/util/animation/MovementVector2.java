/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
