/*
 * Copyright (c) 2003-2009 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jmex.effects.cloth;

import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.math.spring.SpringPoint;
import com.jme.math.spring.SpringPointForce;

/**
 * <code>ClothUtils</code>
 * @author Joshua Slack
 * @version $Id: ClothUtils.java 4133 2009-03-19 20:40:11Z blaine.dev $
 */
public final class ClothUtils {

	/**
	 * Not used.
	 */
	private ClothUtils() {
	}

	/**
	 * Creates a basic wind that always blows in a single direction.
	 *
	 * @param windStr Max strength of wind.
	 * @param windDir Direction wind should blow.
	 * @param addRandom randomly alter the strength of the wind by 0-100%
	 * @return SpringPointForce
	 */
	public static SpringPointForce createBasicWind(final float windStr, final Vector3f windDir, final boolean addRandom) {
		return new SpringPointForce() {
			private final float strength = windStr;
			private final Vector3f windDirection = windDir;
			private final boolean random = addRandom;

			public void apply(float dt, SpringPoint node) {
				float tStr = (random ? FastMath.nextRandomFloat() * strength : strength);
				node.acceleration.addLocal(windDirection.x * tStr,
																			 windDirection.y * tStr,
																			 windDirection.z * tStr);
			}
		};
	}

	/**
	 * Create a basic gravitational force.
	 *
	 * @return SpringPointForce
	 */
	public static SpringPointForce createBasicGravity() {
		return new SpringPointForce() {
			private Vector3f gravity = new Vector3f(0, -32.14f, 0); // ft/s2

			public void apply(float dt, SpringPoint node) {
				node.acceleration.addLocal(gravity.x, gravity.y,
												gravity.z);
			}
		};
	}

	/**
	 * Create a basic drag force that will use the given drag coefficient.
	 * Drag is determined by figuring the current velocity and reversing it, then
	 * multiplying by the drag coefficient and dividing by the particle mass.
	 *
	 * @param dragCoef Should be positive.  Larger values mean more drag but possibly more instability.
	 * @return SpringPointForce
	 */
	public static SpringPointForce createBasicDrag(final float dragCoef) {
		return new SpringPointForce() {
			private Vector3f velocity = new Vector3f();
			private float dragCoefficient = dragCoef;

			public void apply(float dt, SpringPoint node) {
				// viscous drag
				velocity.set(node.position);
				velocity.subtractLocal(node.oldPos).divideLocal(dt);
				node.acceleration.addLocal(velocity.multLocal(-dragCoefficient).multLocal(node.invMass));
			}
		};
	}
}
