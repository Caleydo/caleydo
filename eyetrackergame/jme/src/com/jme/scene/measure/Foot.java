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
package com.jme.scene.measure;

import com.jme.math.Vector3f;

/**
 * @author <a href="mailto:skye.book@gmail.com">Skye Book
 *
 */
public class Foot extends LengthUnit {

	/**
	 * Constructor creates a specified number of Feet,
	 * allowing you to convert easily between popular measures of
	 * distance.
	 *
	 *@param length The number of feet.
	 */
	public Foot(float length) {
		super(length);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.jme.scene.measure.LengthUnit#convert(com.jme.scene.measure.LengthUnit.Units)
	 */
	@Override
	public LengthUnit convert(DistanceUnits targetUnit) {
		if(targetUnit.equals(DistanceUnits.MILLIMETER))
		{
			return new Millimeter(numberOfUnits*304.8f);
		}
		else if(targetUnit.equals(DistanceUnits.CENTIMETER))
		{
			return new Centimeter(numberOfUnits*30.48f);
		}
		else if(targetUnit.equals(DistanceUnits.DECIMETER))
		{
			return new Decimeter(numberOfUnits*3.048f);
		}
		else if(targetUnit.equals(DistanceUnits.METER))
		{
			return new Meter(numberOfUnits*0.3048f);
		}
		else if(targetUnit.equals(DistanceUnits.KILOMETER))
		{
			return new Kilometer(numberOfUnits*0.0003048f);
		}
		else if(targetUnit.equals(DistanceUnits.INCH))
		{
			return new Inch(numberOfUnits*12);
		}
		else if(targetUnit.equals(DistanceUnits.FOOT))
		{
			return this;
		}
		else if(targetUnit.equals(DistanceUnits.YARD))
		{
			return new Yard(numberOfUnits/3);
		}
		else if(targetUnit.equals(DistanceUnits.MILE))
		{
			return new Mile(numberOfUnits/5280);
		}
		else if(targetUnit.equals(DistanceUnits.NAUTICAL_MILE))
		{
			return new NauticalMile(numberOfUnits/6076.1155f);
		}
		else
			return null;
	}

	/* (non-Javadoc)
	 * @see com.jme.scene.measure.LengthUnit#convertToFloat()
	 */
	@Override
	public float convertToFloat() {
		Meter meterToCompare = (Meter) convert(DistanceUnits.METER);
		return meterToCompare.getNumberOfUnits()*metersPerFloat;
	}
	
	public static Vector3f convertVectorUnits(Vector3f vectorToConvert, DistanceUnits targetUnit)
	{
		return LengthUnit.convertVectorUnits(vectorToConvert, DistanceUnits.FOOT, targetUnit);
	}
}