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

package com.jmex.effects.water;

import com.jme.math.FastMath;

/**
 * <code>WaterHeightGenerator</code>
 * Sample implementation of a water height generator
 *
 * @author Rikard Herlitz (MrCoder)
 */
public class WaterHeightGenerator implements HeightGenerator {
	private float scalexsmall = 0.04f;
	private float scaleysmall = 0.02f;
	private float scalexbig = 0.015f;
	private float scaleybig = 0.01f;
	private float heightsmall = 3.0f;
	private float heightbig = 10.0f;
	private float speedsmall = 1.0f;
	private float speedbig = 0.5f;
	private int octaves = 2;

	public float getHeight( float x, float z, float time ) {
		float zval = z * scaleybig * 4f + time * speedbig * 4f;
		float height = FastMath.sin( zval );
		height *= heightbig;

		if( octaves > 0 ) {
			float height2 = (float) ImprovedNoise.noise( x * scaleybig, z * scalexbig, time * speedbig ) * heightbig;
			height = height * 0.4f + height2 * 0.6f;
		}
		if( octaves > 1 )
			height += ImprovedNoise.noise( x * scaleysmall, z * scalexsmall, time * speedsmall ) * heightsmall;
		if( octaves > 2 )
			height += ImprovedNoise.noise( x * scaleysmall * 2.0f, z * scalexsmall * 2.0f, time * speedsmall * 1.5f ) * heightsmall * 0.5f;
		if( octaves > 3 )
			height += ImprovedNoise.noise( x * scaleysmall * 4.0f, z * scalexsmall * 4.0f, time * speedsmall * 2.0f ) * heightsmall * 0.25f;

		return height; // + waterHeight
	}

	public float getScalexsmall() {
		return scalexsmall;
	}

	public void setScalexsmall( float scalexsmall ) {
		this.scalexsmall = scalexsmall;
	}

	public float getScaleysmall() {
		return scaleysmall;
	}

	public void setScaleysmall( float scaleysmall ) {
		this.scaleysmall = scaleysmall;
	}

	public float getScalexbig() {
		return scalexbig;
	}

	public void setScalexbig( float scalexbig ) {
		this.scalexbig = scalexbig;
	}

	public float getScaleybig() {
		return scaleybig;
	}

	public void setScaleybig( float scaleybig ) {
		this.scaleybig = scaleybig;
	}

	public float getHeightsmall() {
		return heightsmall;
	}

	public void setHeightsmall( float heightsmall ) {
		this.heightsmall = heightsmall;
	}

	public float getHeightbig() {
		return heightbig;
	}

	public void setHeightbig( float heightbig ) {
		this.heightbig = heightbig;
	}

	public float getSpeedsmall() {
		return speedsmall;
	}

	public void setSpeedsmall( float speedsmall ) {
		this.speedsmall = speedsmall;
	}

	public float getSpeedbig() {
		return speedbig;
	}

	public void setSpeedbig( float speedbig ) {
		this.speedbig = speedbig;
	}

	public int getOctaves() {
		return octaves;
	}

	public void setOctaves( int octaves ) {
		this.octaves = octaves;
	}
}
