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

/**
 * A MovementValue represents a value that reaches a target value over time with a certain speed. It is
 * intended to adjust properties of displayed objects over time to make animations possible.
 * 
 * @author Christian Partl
 */
public class MovementValue {

	/**
	 * Criterion that a movement value has reached its target if the value is greater or equal the target
	 * value.
	 */
	public static final int CRITERION_GREATER_OR_EQUAL = 0;
	/**
	 * Criterion that a movement value has reached its target if the value is smaller or equal the target
	 * value.
	 */
	public static final int CRITERION_SMALLER_OR_EQUAL = 1;

	private float fMovementValue;
	private float fTargetValue;
	private float fSpeed;
	private int iCreterionType;

	/**
	 * Constructor.
	 * 
	 * @param fStartValue
	 *            The start value of the movement value.
	 * @param fTargetValue
	 *            The target value the movement value should reach.
	 * @param fSpeed
	 *            The speed of the movement value in units/second.
	 */
	public MovementValue(float fStartValue, float fTargetValue, float fSpeed) {
		this.fMovementValue = fStartValue;
		this.fTargetValue = fTargetValue;
		this.fSpeed = fSpeed;
		this.iCreterionType =
			(fStartValue <= fTargetValue) ? CRITERION_GREATER_OR_EQUAL : CRITERION_SMALLER_OR_EQUAL;
	}

	/**
	 * Constructor.
	 * 
	 * @param fStartValue
	 *            The start value of the movement value.
	 * @param fTargetValue
	 *            The target value the movement value should reach.
	 * @param fSpeed
	 *            The speed of the movement value in units/second.
	 * @param iCreterionType
	 *            Type of criterion which specifies when the target is counted as reached.
	 */
	public MovementValue(float fStartValue, float fTargetValue, float fSpeed, int iCreterionType) {
		this.fMovementValue = fStartValue;
		this.fTargetValue = fTargetValue;
		this.fSpeed = fSpeed;
		this.iCreterionType = iCreterionType;
	}

	/**
	 * Moves a movement value with its speed.
	 * 
	 * @param dTimePassed
	 *            Time difference since the last call of move, i.e. the last frame.
	 */
	public void move(double dTimePassed) {
		if (!isTargetValueReached())
			fMovementValue += (fSpeed * dTimePassed);
		if (isTargetValueReached())
			fMovementValue = fTargetValue;

	}

	/**
	 * @return True if the movement value has reached the target value, false otherwise.
	 */
	public boolean isTargetValueReached() {
		if (iCreterionType == CRITERION_GREATER_OR_EQUAL) {
			return (fMovementValue >= (fTargetValue - 0.0001));
		}
		return (fMovementValue <= (fTargetValue + 0.0001));
	}

	/**
	 * @return The target value of the movement value.
	 */
	public float getTargetValue() {
		return fTargetValue;
	}

	/**
	 * Sets the target value to the specified value.
	 * 
	 * @param fTargetValue
	 *            Value the target value should be set to.
	 */
	public void setTargetValue(float fTargetValue) {
		this.fTargetValue = fTargetValue;
	}

	/**
	 * @return The current movement value.
	 */
	public float getMovementValue() {
		return fMovementValue;
	}

	/**
	 * Sets the movement value to the specified value.
	 * 
	 * @param fMovementValue
	 *            Value the movement value should be set to.
	 */
	public void setMovementValue(float fMovementValue) {
		this.fMovementValue = fMovementValue;
	}

	/**
	 * @return The current speed of the movement value.
	 */
	public float getSpeed() {
		return fSpeed;
	}

	/**
	 * Sets the movement value's speed to the specified value.
	 * 
	 * @param fSpeed
	 *            Value the movement value's speed should be set to.
	 */
	public void setSpeed(float fSpeed) {
		this.fSpeed = fSpeed;
	}

	/**
	 * @return The criterion type which specifies when the target is counted as reached.
	 */
	public int getCreterionType() {
		return iCreterionType;
	}

	/**
	 * Sets the criterion type which specifies when the target is counted as reached.
	 * 
	 * @param iCreterionType
	 *            Criterion type.
	 */
	public void setCreterionType(int iCreterionType) {
		this.iCreterionType = iCreterionType;
	}

}
