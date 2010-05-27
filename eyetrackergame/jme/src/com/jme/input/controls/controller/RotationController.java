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
package com.jme.input.controls.controller;

import com.jme.input.controls.GameControl;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.Spatial;

/**
 * @author Matthew D. Hicks
 */
public class RotationController extends Controller {
	
	private static final long serialVersionUID = -911814334954766964L;

	private Spatial spatial;
	private GameControl positive;
	private GameControl negative;
	private float multiplier;
	
	private Quaternion quat;
	private Vector3f dir;
	
	public RotationController(Spatial spatial, GameControl positive, GameControl negative, float multiplier, final Axis axis) {
		this.spatial = spatial;
		this.positive = positive;
		this.negative = negative;
		this.multiplier = multiplier;
		
		quat = new Quaternion();
		if (axis == Axis.X) {
			dir = new Vector3f(1.0f, 0.0f, 0.0f);
		} else if (axis == Axis.Y) {
			dir = new Vector3f(0.0f, 1.0f, 0.0f);
		} else if (axis == Axis.Z) {
			dir = new Vector3f(0.0f, 0.0f, 1.0f);
		} else {
			throw new RuntimeException("Unknown axis: " + axis);
		}
	}

	public void update(float time) {
		float value = positive.getValue() - negative.getValue();
		float delta = (value * time) * multiplier;
		if (value != 0.0f) {
			quat.fromAngleAxis(delta * FastMath.PI, dir);
			spatial.getLocalRotation().multLocal(quat);
		}
	}

	public GameControl getPositive() {
		return positive;
	}

	public void setPositive(GameControl positive) {
		this.positive = positive;
	}

	public GameControl getNegative() {
		return negative;
	}

	public void setNegative(GameControl negative) {
		this.negative = negative;
	}

	public float getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(float multiplier) {
		this.multiplier = multiplier;
	}

	public Spatial getSpatial() {
		return spatial;
	}

	public void setSpatial(Spatial spatial) {
		this.spatial = spatial;
	}
}

