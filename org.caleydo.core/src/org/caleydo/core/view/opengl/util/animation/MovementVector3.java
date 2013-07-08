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
