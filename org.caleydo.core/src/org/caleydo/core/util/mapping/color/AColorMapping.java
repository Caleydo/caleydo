package org.caleydo.core.util.mapping.color;

import gleem.linalg.Vec3f;

import java.awt.Color;

/**
 * Abstract class for color mapping.
 * 
 * @author Marc Streit
 * @author Michael Kalkusch
 */
public abstract class AColorMapping {

	protected float fMin = 0;
	protected float fMax = 1;
	
	protected Vec3f color_outOfRange = new Vec3f(0,0,0);
	
	protected AColorMapping(final float fMin, final float fMax) {

		setValueMinMax(fMin, fMax);
	}
	
	/**
	 * @return the iMax
	 */
	public final float getValueMin() {
	
		return fMin;
	}
	
	/**
	 * @return the iMax
	 */
	public final float getValueMax() {
	
		return fMax;
	}

	
	/**
	 * @param max the largest value to be mapped
	 * @param min the smallest value to be mapped
	 */
	public void setValueMinMax(final float fMin, final float fMax) {
	
		this.fMin = fMin;
		this.fMax = fMax;
	}

	
	/**
	 * @return the color_outOfRange
	 */
	public final Vec3f getColor_outOfRange() {
	
		return color_outOfRange;
	}

	
	/**
	 * @param color_outOfRange the color_outOfRange to set
	 */
	public final void setColor_outOfRange(Vec3f color_outOfRange) {
	
		this.color_outOfRange = color_outOfRange;
	}	
}
