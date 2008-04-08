/**
 * 
 */
package org.caleydo.core.math.statistics.histogram;



/**
 * Data generated for a histogram.
 * 
 * @author Michael Kalkusch
 *
 */
public final class HistogramData
{
	
	/**
	 * Statistic mean.
	 * mean = 1/ (n-1) * Sum[i=1..n]( x(i) ) 
	 */
	public float fMean;
	
	/**
	 * Statistic variance.
	 * variance =  1 / (n-1) * Sum[i=1..n]( (x_excpect - x(i))^2 ) 
	 */
	public float fVariance;
	
	/**
	 * Maximum value and upper bound of the histogram.
	 * Ignore data values that are above the upper bound.
	 */
	public Number iMaxValue;
	
	/**
	 * Minimum value and lower bound of the histogram.
	 * Ignore data values that are below the lower bound.
	 */
	public Number iMinValue;
	
	/**
	 * Count how many values are below the lower bound. 
	 */
	public int iBelowLowerBound;
	
	/*
	 * Count how many values are above the upper bound. 
	 */
	public int iAboveUpperBound;
	
	/**
	 * Count how many items are in the intervall wioth the most items.
	 * Used for scaling.
	 */
	public int iMaxValuesInIntervall;
	
	public int[] iCounterPerItervall = null;
	
	public Number[] t_intervall = null;
	               
	/**
	 * 
	 */
	protected HistogramData() {
		
	}

	public HistogramData( final HistogramStatisticInteger copyData)  {
		
		this.iMinValue = copyData.getMinValue();
		this.iMaxValue = copyData.getMaxValue();
		this.iMaxValuesInIntervall = copyData.getMaxValuesInAllIntervalls();
		
		this.fMean = copyData.getMeanValueF();
		this.fVariance = copyData.getVarianceValueF();
		
		this.iCounterPerItervall = copyData.getHistogramData();
		this.t_intervall = copyData.getHistogramIntervalls();
	}
	
	/**
	 * Get the number of slots the histogram contains.
	 * 
	 * @return number of slots the histogram contains
	 */
	public final int getHistogramSlotCounter() {
		if ( iCounterPerItervall != null ) {
			return this.iCounterPerItervall.length;
		}
		return 0;
	}
	
	public String toString() {
		String result = "H: ";
		
		result += " <" + iMinValue.toString();
		result += ";" + iMaxValue.toString(); 
		result += "> ~=" + Float.toString( fMean );
		result += " var=" + Float.toString( fVariance ); 
		
		result += " (values=" + Integer.toString( iCounterPerItervall.length );
		result += " ^=" + Integer.toString( iMaxValuesInIntervall );
		result += ") limit=" + Integer.toString( t_intervall.length );
		
		return result;
	}
}
