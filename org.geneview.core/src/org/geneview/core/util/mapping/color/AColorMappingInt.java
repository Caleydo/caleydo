package org.geneview.core.util.mapping.color;

import java.awt.Color;

/**
 * Abstract class for color mapping.
 * 
 * @author Marc Streit
 * @author Michael Kalkusch
 */
public abstract class AColorMappingInt {

	protected int iMin = 0;
	protected int iMax = 0;
	
	protected Color color_outOfRange = Color.BLACK;
	
	protected AColorMappingInt(int iMin, int iMax) {
		setValueMinMax(iMin,iMax);
	}
	
	/**
	 * @return the iMax
	 */
	public final int getValueMinInt() {
	
		return iMin;
	}
	
	/**
	 * @return the iMax
	 */
	public final int getValueMaxInt() {
	
		return iMax;
	}

	
	/**
	 * @param max the largest value to be mapped
	 * @param min the smallest value to be mapped
	 */
	public void setValueMinMax(final int min, final int max) {
	
		iMin = min;
		iMax = max;
	}

	
	/**
	 * @return the color_outOfRange
	 */
	public final Color getColor_outOfRange() {
	
		return color_outOfRange;
	}

	
	/**
	 * @param color_outOfRange the color_outOfRange to set
	 */
	public final void setColor_outOfRange(Color color_outOfRange) {
	
		this.color_outOfRange = color_outOfRange;
	}
	
}
