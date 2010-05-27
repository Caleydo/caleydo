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
 * <p><code>LengthUnit</code> forms the base class for implementing 
 * scaled measure in a jME scenegraph.</p>
 * 
 * <p><code>LengthUnit</code> can be used in the instantiation
 * of a <code>Vector3f</code> as such:<p>
 * <code> 
 * Vector3f myScaledVector = new Vector3f(<br>
 * 										  new Foot(2).convertToFloat(),<br>
 * 										  new Foot(1).convertToFloat(),<br>
 * 										  new Inch(3).convertToFloat());<br>
 * </code>
 * @author <a href="mailto:skye.book@gmail.com">Skye Book
 *
 */
abstract public class LengthUnit {
	public enum DistanceUnits{
		MILLIMETER, DECIMETER, CENTIMETER, METER, KILOMETER,
		INCH, FOOT, YARD, MILE, NAUTICAL_MILE
	}
	
	protected float numberOfUnits;
	
	/**
	 * <code>metersPerFoot</code> is the general multiplier for
	 * the scale of the unit system.  The default is 1 meter for
	 * each whole number.
	 */
	protected float metersPerFloat = 1;

	/**
	 * Constructor creates a <code>LengthUnit</code> of a specified length
	 * and unit, allowing you to convert easily between popular measures of
	 * distance.
	 */
	public LengthUnit(float length) {
		this.numberOfUnits = length;
	}
	
	/**
	 * Converts the current <code>LengthUnit</code> into another form.
	 * (e.g: Meters -> Feet)
	 * @param targetUnit The measurement being converted into
	 * @return The LengthUnit created in the form of the targetUnit
	 */
	public abstract LengthUnit convert(DistanceUnits targetUnit);
	
	/**
	 * 
	 * @param vectorToConvert A <code>Vector3f</code> to be scaled to
	 * another unit of measure.
	 * @param startUnit The unit being converted from.
	 * @param targetUnit The unit being converted to.
	 * @return A Vector3f with values in the converted unit of measure.
	 */
	protected static Vector3f convertVectorUnits(Vector3f vectorToConvert, DistanceUnits startUnit, DistanceUnits targetUnit)
	{
		/* instantiate blank units.  All enumeration values are
		 * present here, so there's no reason why you should
		 * get a converted Inch of length 0 returned..
		 */
		LengthUnit xUnit = new Inch(0);
		LengthUnit yUnit = new Inch(0);
		LengthUnit zUnit = new Inch(0);
		if(startUnit.equals(DistanceUnits.MILLIMETER))
		{
			xUnit = new Millimeter(vectorToConvert.x);
			yUnit = new Millimeter(vectorToConvert.y);
			zUnit = new Millimeter(vectorToConvert.z);
		}
		else if(startUnit.equals(DistanceUnits.CENTIMETER))
		{
			xUnit = new Centimeter(vectorToConvert.x);
			yUnit = new Centimeter(vectorToConvert.y);
			zUnit = new Centimeter(vectorToConvert.z);
		}
		else if(startUnit.equals(DistanceUnits.DECIMETER))
		{
			xUnit = new Decimeter(vectorToConvert.x);
			yUnit = new Decimeter(vectorToConvert.y);
			zUnit = new Decimeter(vectorToConvert.z);
		}
		else if(startUnit.equals(DistanceUnits.METER))
		{
			xUnit = new Meter(vectorToConvert.x);
			yUnit = new Meter(vectorToConvert.y);
			zUnit = new Meter(vectorToConvert.z);
		}
		else if(startUnit.equals(DistanceUnits.KILOMETER))
		{
			xUnit = new Kilometer(vectorToConvert.x);
			yUnit = new Kilometer(vectorToConvert.y);
			zUnit = new Kilometer(vectorToConvert.z);
		}
		else if(startUnit.equals(DistanceUnits.INCH))
		{
			xUnit = new Inch(vectorToConvert.x);
			yUnit = new Inch(vectorToConvert.y);
			zUnit = new Inch(vectorToConvert.z);
		}
		else if(startUnit.equals(DistanceUnits.FOOT))
		{
			xUnit = new Foot(vectorToConvert.x);
			yUnit = new Foot(vectorToConvert.y);
			zUnit = new Foot(vectorToConvert.z);
		}
		else if(startUnit.equals(DistanceUnits.YARD))
		{
			xUnit = new Yard(vectorToConvert.x);
			yUnit = new Yard(vectorToConvert.y);
			zUnit = new Yard(vectorToConvert.z);
		}
		else if(startUnit.equals(DistanceUnits.MILE))
		{
			xUnit = new Mile(vectorToConvert.x);
			yUnit = new Mile(vectorToConvert.y);
			zUnit = new Mile(vectorToConvert.z);
		}
		else if(startUnit.equals(DistanceUnits.NAUTICAL_MILE))
		{
			xUnit = new NauticalMile(vectorToConvert.x);
			yUnit = new NauticalMile(vectorToConvert.y);
			zUnit = new NauticalMile(vectorToConvert.z);
		}
		else
			return vectorToConvert;

		return new Vector3f(xUnit.convert(targetUnit).convertToFloat(),
							yUnit.convert(targetUnit).convertToFloat(),
							zUnit.convert(targetUnit).convertToFloat());
	}
	
	/**
	 * Converts a LengthUnit into a float that is useful in defining
	 * spaces in the scenegraph.
	 * @return A scaled float which can be used to construct
	 * a <code>Vector3f</code>.
	 */
	public abstract float convertToFloat();

	/**
	 * @return the metersPerFloat
	 */
	public float getMetersPerFloat() {
		return metersPerFloat;
	}

	/**
	 * Sets the general scale in terms of what is actually contained
	 * in a Vector3f.  For example, setting this to 5 would mean that
	 * 15 meters in all directions could be reproduced with
	 * <code>Vector3f(3,3,3)</code>
	 * 
	 * @param metersPerFloat The general multiplier for
	 * the scale of the unit system.  The default is 1 meter for
	 * each whole number.
	 */
	public void setMetersPerFloat(float metersPerFloat) {
		this.metersPerFloat = metersPerFloat;
	}

	public float getNumberOfUnits() {
		return numberOfUnits;
	}

	public void setNumberOfUnits(float numberOfUnits) {
		this.numberOfUnits = numberOfUnits;
	}
}